package com.jipple.sql;

import com.jipple.configuration.Option;
import com.jipple.configuration.Options;
import com.jipple.configuration.util.ConfigUtil;
import com.jipple.sql.catalyst.expressions.CodegenObjectFactoryMode;
import com.jipple.sql.catalyst.expressions.Resolver;
import com.jipple.sql.internal.SqlApiConfHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SQLConf implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(SQLConf.class);
    private static final AtomicReference<Supplier<SQLConf>> CONF_GETTER = new AtomicReference<>(SQLConf::new);

    public static final Option<String> SESSION_LOCAL_TIMEZONE =
            Options.key(SqlApiConfHelper.SESSION_LOCAL_TIMEZONE_KEY)
                    .stringType()
                    .defaultValue("UTC")
                    .withDescription(
                            "The ID of session local timezone in the format of either region-based zone IDs or zone offsets.");

    public static final Option<Boolean> ANSI_ENABLED =
            Options.key(SqlApiConfHelper.ANSI_ENABLED_KEY)
                    .booleanType()
                    .defaultValue(false)
                    .withDescription("");

    public static final Option<Boolean> CASE_SENSITIVE =
            Options.key(SqlApiConfHelper.CASE_SENSITIVE_KEY)
                    .booleanType()
                    .defaultValue(false)
                    .withDescription("");

    public static final Option<String> RIPPLE_SESSION_EXTENSIONS =
            Options.key("jipple.sql.extensions")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("");

    public static final Option<String> RIPPLE_SESSION_INTERNAL_LOOKUPS =
            Options.key("jipple.sql.internal.lookups")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("");

    public static final Option<String> RIPPLE_SESSION_INTERNAL_DICTS =
            Options.key("jipple.sql.internal.dicts")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("");

    public static final Option<Boolean> CAST_DATETIME_TO_STRING =
            Options.key("jipple.sql.typeCoercion.datetimeToString.enabled")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription(
                            "If it is set to true, date/timestamp will cast to string in binary comparisons "
                                    + "with String when " + ANSI_ENABLED.key() + " is false.");

    public static final Option<Boolean> DATETIME_JAVA8API_ENABLED =
            Options.key("jipple.sql.datetime.java8API.enabled")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription("");

    public static final Option<CodegenObjectFactoryMode> CODEGEN_FACTORY_MODE =
            Options.key("jipple.sql.codegen.factoryMode")
                    .objectType(CodegenObjectFactoryMode.class)
                    .defaultValue(CodegenObjectFactoryMode.FALLBACK)
                    .withDescription("");

    public static final Option<Integer> OPTIMIZER_INSET_CONVERSION_THRESHOLD =
            Options.key("jipple.sql.optimizer.inSetConversionThreshold")
                    .intType()
                    .defaultValue(10)
                    .withDescription("The threshold of set size for InSet conversion.");

    public static final Option<Integer> ANALYZER_MAX_ITERATIONS =
            Options.key("jipple.sql.analyzer.maxIterations")
                    .intType()
                    .defaultValue(100)
                    .withDescription("The max number of iterations the analyzer runs.");

    public static final Option<Integer> OPTIMIZER_MAX_ITERATIONS =
            Options.key("jipple.sql.optimizer.maxIterations")
                    .intType()
                    .defaultValue(100)
                    .withDescription("The max number of iterations the optimizer runs.");

    public static final Option<Boolean> LEGACY_NEGATIVE_INDEX_IN_ARRAY_INSERT =
            Options.key("jipple.sql.legacy.negativeIndexInArrayInsert")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription("");

    public static final Option<Integer> CODEGEN_METHOD_SPLIT_THRESHOLD =
            Options.key("jipple.sql.codegen.methodSplitThreshold")
                    .intType()
                    .defaultValue(1024)
                    .withDescription("The threshold of source-code splitting in the codegen.");

    private final Map<String, String> settings = new HashMap<>();

    /**
     * Sets the active config object within the current scope.
     * See {@link #get()} for more information.
     */
    public static void setSQLConfGetter(Supplier<SQLConf> getter) {
        CONF_GETTER.set(getter);
    }

    public static SQLConf get() {
        return CONF_GETTER.get().get();
    }

    public static SQLConf fromMap(Map<String, String> map) {
        SQLConf conf = new SQLConf();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            conf.set(entry.getKey(), entry.getValue());
        }
        return conf;
    }

    public String sessionLocalTimeZone() {
        return getConf(SESSION_LOCAL_TIMEZONE);
    }

    public boolean ansiEnabled() {
        return getConf(ANSI_ENABLED);
    }

    public boolean caseSensitiveAnalysis() {
        return getConf(CASE_SENSITIVE);
    }

    public boolean castDatetimeToString() {
        return getConf(CAST_DATETIME_TO_STRING);
    }

    public boolean datetimeJava8ApiEnabled() {
        return getConf(DATETIME_JAVA8API_ENABLED);
    }

    public boolean allowHashOnMapType() {
        return false;
    }

    public boolean groupByAliases() {
        return false;
    }

    public boolean lateralColumnAliasImplicitEnabled() {
        return true;
    }

    public boolean allowStarWithSingleTableIdentifierInCount() {
        return false;
    }

    public CodegenObjectFactoryMode codegenFactoryMode() {
        return getConf(CODEGEN_FACTORY_MODE);
    }

    public int analyzerMaxIterations() {
        return getConf(ANALYZER_MAX_ITERATIONS);
    }

    public int optimizerMaxIterations() {
        return getConf(OPTIMIZER_MAX_ITERATIONS);
    }

    public int maxToStringFields() {
        return 30;
    }

    public int optimizerInSetConversionThreshold() {
        return getConf(OPTIMIZER_INSET_CONVERSION_THRESHOLD);
    }

    public boolean legacyNegativeIndexInArrayInsert() {
        return getConf(LEGACY_NEGATIVE_INDEX_IN_ARRAY_INSERT);
    }

    public int methodSplitThreshold() {
        return getConf(CODEGEN_METHOD_SPLIT_THRESHOLD);
    }

    /**
     * Returns the {@link Resolver} for the current configuration, which can be used to determine
     * if two identifiers are equal.
     */
    public Resolver resolver() {
        if (caseSensitiveAnalysis()) {
            return Resolver.caseSensitiveResolution();
        }
        return Resolver.caseInsensitiveResolution();
    }

    public void set(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        settings.put(key, value);
    }

    public <T> void setConf(Option<T> key, T value) {
        settings.put(key.key(), value.toString());
    }

    public <T> T getConf(Option<T> option) {
        return getOptional(option).orElse(option.defaultValue());
    }

    public Map<String, String> toMap() {
        return new HashMap<>(settings);
    }

    private <T> Optional<T> getOptional(Option<T> option) {
        if (option == null) {
            throw new NullPointerException("Option not be null.");
        }
        String value = settings.get(option.key());
        if (value == null) {
            for (String fallbackKey : option.getFallbackKeys()) {
                value = settings.get(fallbackKey);
                if (value != null) {
                    LOG.warn(
                            "Please use the new key '{}' instead of the deprecated key '{}'.",
                            option.key(),
                            fallbackKey);
                    return Optional.of(ConfigUtil.convertValue(value, option));
                }
            }
        }
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(ConfigUtil.convertValue(value, option));
    }
}
