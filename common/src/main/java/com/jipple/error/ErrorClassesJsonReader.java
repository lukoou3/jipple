package com.jipple.error;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A reader to load error information from one or more JSON files. Note that, if one error appears
 * in more than one JSON files, the latter wins. Please read core/src/main/resources/error/README.md
 * for more details.
 */
public class ErrorClassesJsonReader {
    private final Map<String, ErrorInfo> errorInfoMap;

    public ErrorClassesJsonReader(List<URL> jsonFileURLs) {
        if (jsonFileURLs == null || jsonFileURLs.isEmpty()) {
            throw new IllegalArgumentException("jsonFileURLs cannot be empty");
        }

        // Exposed for testing
        this.errorInfoMap = jsonFileURLs.stream()
                .map(ErrorClassesJsonReader::readAsMap)
                .reduce(new HashMap<>(), (map1, map2) -> {
                    Map<String, ErrorInfo> merged = new HashMap<>(map1);
                    merged.putAll(map2);
                    return merged;
                });
    }

    // Exposed for testing
    Map<String, ErrorInfo> getErrorInfoMap() {
        return errorInfoMap;
    }

    public String getErrorMessage(String errorClass, Map<String, String> messageParameters) {
        String messageTemplate = getMessageTemplate(errorClass);
        StringSubstitutor sub = new StringSubstitutor(messageParameters);
        sub.setEnableUndefinedVariableException(true);
        sub.setDisableSubstitutionInValues(true);
        try {
            return sub.replace(messageTemplate.replaceAll("<([a-zA-Z0-9_-]+)>", "\\$\\{$1\\}"));
        } catch (IllegalArgumentException e) {
            throw JippleException.internalError(
                    "Undefined error message parameter for error class: '" + errorClass + "'. " +
                            "Parameters: " + messageParameters);
        }
    }

    public String getMessageTemplate(String errorClass) {
        String[] errorClasses = errorClass.split("\\.");
        if (errorClasses.length != 1 && errorClasses.length != 2) {
            throw new AssertionError("Invalid error class format: " + errorClass);
        }

        String mainErrorClass = errorClasses[0];
        Optional<String> subErrorClass = errorClasses.length > 1
                ? Optional.of(errorClasses[1])
                : Optional.empty();

        ErrorInfo errorInfo = errorInfoMap.get(mainErrorClass);
        if (errorInfo == null) {
            throw JippleException.internalError("Cannot find main error class '" + errorClass + "'");
        }

        if (errorInfo.getSubClass().isPresent() != subErrorClass.isPresent()) {
            throw new AssertionError("SubClass presence mismatch for error class: " + errorClass);
        }

        if (subErrorClass.isEmpty()) {
            return errorInfo.getMessageTemplate();
        } else {
            Map<String, ErrorSubInfo> subClassMap = errorInfo.getSubClass().get();
            ErrorSubInfo errorSubInfo = subClassMap.get(subErrorClass.get());
            if (errorSubInfo == null) {
                throw JippleException.internalError("Cannot find sub error class '" + errorClass + "'");
            }
            return errorInfo.getMessageTemplate() + " " + errorSubInfo.getMessageTemplate();
        }
    }

    public String getSqlState(String errorClass) {
        if (errorClass == null) {
            return null;
        }
        String[] parts = errorClass.split("\\.");
        if (parts.length == 0) {
            return null;
        }
        String mainErrorClass = parts[0];
        ErrorInfo errorInfo = errorInfoMap.get(mainErrorClass);
        if (errorInfo == null) {
            return null;
        }
        return errorInfo.getSqlState().orElse(null);
    }

    private static ObjectMapper mapper = new ObjectMapper();

    private static Map<String, ErrorInfo> readAsMap(URL url) {
        try {
            TypeReference<java.util.Map<String, ErrorInfoData>> typeRef =
                    new TypeReference<java.util.Map<String, ErrorInfoData>>() {};
            Map<String, ErrorInfoData> dataMap = mapper.readValue(url, typeRef);

            Map<String, ErrorInfo> map = dataMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                ErrorInfoData data = entry.getValue();
                                List<String> message = data.message != null
                                        ? new ArrayList<>(data.message)
                                        : Collections.emptyList();
                                Optional<Map<String, ErrorSubInfo>> subClass = data.subClass != null
                                        ? Optional.of(data.subClass.entrySet().stream()
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                subEntry -> new ErrorSubInfo(
                                                        subEntry.getValue().message != null
                                                                ? new ArrayList<>(subEntry.getValue().message)
                                                                : Collections.emptyList()))))
                                        : Optional.empty();
                                Optional<String> sqlState = Optional.ofNullable(data.sqlState);
                                return new ErrorInfo(message, subClass, sqlState);
                            }));

            // Check for error classes with dots
            Optional<String> errorClassWithDots = map.entrySet().stream()
                    .filter(entry -> entry.getKey().contains("."))
                    .map(Map.Entry::getKey)
                    .findFirst();

            if (errorClassWithDots.isEmpty()) {
                errorClassWithDots = map.values().stream()
                        .filter(errorInfo -> errorInfo.getSubClass().isPresent())
                        .flatMap(errorInfo -> errorInfo.getSubClass().get().keySet().stream())
                        .filter(key -> key.contains("."))
                        .findFirst();
            }

            if (errorClassWithDots.isPresent()) {
                throw JippleException.internalError(
                        "Found the (sub-)error class with dots: " + errorClassWithDots.get());
            }

            return map;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read error classes from URL: " + url, e);
        }
    }

    static class ErrorInfo {
        private final List<String> message;
        private final Optional<Map<String, ErrorSubInfo>> subClass;
        private final Optional<String> sqlState;
        private final String messageTemplate;

        /**
         * Information associated with an error class.
         *
         * @param sqlState SQLSTATE associated with this class.
         * @param subClass SubClass associated with this class.
         * @param message C-style message format compatible with printf.
         *                The error message is constructed by concatenating the lines with newlines.
         */
        ErrorInfo(List<String> message, Optional<Map<String, ErrorSubInfo>> subClass, Optional<String> sqlState) {
            this.message = message;
            this.subClass = subClass;
            this.sqlState = sqlState;
            // For compatibility with multi-line error messages
            this.messageTemplate = String.join("\n", message);
        }

        List<String> getMessage() {
            return message;
        }

        Optional<Map<String, ErrorSubInfo>> getSubClass() {
            return subClass;
        }

        Optional<String> getSqlState() {
            return sqlState;
        }

        String getMessageTemplate() {
            return messageTemplate;
        }
    }

    static class ErrorSubInfo {
        private final List<String> message;
        private final String messageTemplate;

        /**
         * Information associated with an error subclass.
         *
         * @param message C-style message format compatible with printf.
         *                The error message is constructed by concatenating the lines with newlines.
         */
        ErrorSubInfo(List<String> message) {
            this.message = message;
            // For compatibility with multi-line error messages
            this.messageTemplate = String.join("\n", message);
        }

        List<String> getMessage() {
            return message;
        }

        String getMessageTemplate() {
            return messageTemplate;
        }
    }

    /**
     * Data class for JSON deserialization.
     */
    static class ErrorInfoData {
        public List<String> message;
        public Map<String, ErrorSubInfoData> subClass;
        public String sqlState;
    }

    /**
     * Data class for JSON deserialization of sub error info.
     */
    static class ErrorSubInfoData {
        public List<String> message;
    }
}


