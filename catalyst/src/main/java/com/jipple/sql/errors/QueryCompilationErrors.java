package com.jipple.sql.errors;

import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;

import java.util.List;
import java.util.Map;

public class QueryCompilationErrors {
    public static AnalysisException unresolvedRoutineError(FunctionIdentifier name, List<String> searchPath) {
        return new AnalysisException("UNRESOLVED_ROUTINE",
                Map.of(
                        "routineName", name.funcName,
                        "searchPath", String.join(", ", searchPath)
                )
        );
    }

    public static AnalysisException funcBuildError(String name, Exception error) {
        String message = "Failed to build function: " + name + ". " + error.getMessage();
        return new AnalysisException(message, null, null, error, null, null, null);
    }

    /**
     * Creates an AnalysisException for wrong number of arguments error without legacy suggestion.
     * 
     * @param name the function name
     * @param validParametersCount the list of valid parameter counts
     * @param actualNumber the actual number of arguments provided
     * @return AnalysisException with error class "WRONG_NUM_ARGS.WITHOUT_SUGGESTION"
     */
    public static AnalysisException wrongNumArgsError(String name, List<Integer> validParametersCount, int actualNumber) {
        String expectedNumberOfParameters = formatExpectedParameters(validParametersCount);
        return new AnalysisException(
            "WRONG_NUM_ARGS.WITHOUT_SUGGESTION",
            Map.of(
                "functionName", name,
                "expectedNum", expectedNumberOfParameters,
                "actualNum", String.valueOf(actualNumber),
                "docroot", ""
            )
        );
    }

    /**
     * Creates an AnalysisException for wrong number of arguments error with legacy suggestion.
     * 
     * @param name the function name
     * @param validParametersCount the list of valid parameter counts
     * @param actualNumber the actual number of arguments provided
     * @param legacyNum the legacy number of parameters
     * @param legacyConfKey the legacy configuration key
     * @param legacyConfValue the legacy configuration value
     * @return AnalysisException with error class "WRONG_NUM_ARGS.WITH_SUGGESTION"
     */
    public static AnalysisException wrongNumArgsError(String name, List<Integer> validParametersCount, 
                                                     int actualNumber, String legacyNum, 
                                                     String legacyConfKey, String legacyConfValue) {
        String expectedNumberOfParameters = formatExpectedParameters(validParametersCount);
        return new AnalysisException(
            "WRONG_NUM_ARGS.WITH_SUGGESTION",
            Map.of(
                "functionName", name,
                "expectedNum", expectedNumberOfParameters,
                "actualNum", String.valueOf(actualNumber),
                "legacyNum", legacyNum != null ? legacyNum : "",
                "legacyConfKey", legacyConfKey != null ? legacyConfKey : "",
                "legacyConfValue", legacyConfValue != null ? legacyConfValue : ""
            )
        );
    }

    /**
     * Formats the expected number of parameters as a string.
     * 
     * @param validParametersCount the list of valid parameter counts
     * @return formatted string: "0" if empty, single number if one element, "[1, 2, 3]" if multiple
     */
    private static String formatExpectedParameters(List<Integer> validParametersCount) {
        if (validParametersCount == null || validParametersCount.isEmpty()) {
            return "0";
        } else if (validParametersCount.size() == 1) {
            return String.valueOf(validParametersCount.get(0));
        } else {
            return "[" + String.join(", ", validParametersCount.stream()
                .map(String::valueOf)
                .toArray(String[]::new)) + "]";
        }
    }

}
