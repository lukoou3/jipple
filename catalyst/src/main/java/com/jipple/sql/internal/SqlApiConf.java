package com.jipple.sql.internal;

import com.jipple.sql.types.AtomicType;

/**
 * Configuration for all objects that are placed in the `sql/api` project. The normal way of
 * accessing this class is through `SqlApiConf.get`. If this code is being used with sql/core
 * then its values are bound to the currently set SQLConf. With Spark Connect, it will default to
 * hardcoded values.
 */
public interface SqlApiConf {
    // Shared keys.
    String ANSI_ENABLED_KEY = SqlApiConfHelper.ANSI_ENABLED_KEY;
    String LEGACY_TIME_PARSER_POLICY_KEY = SqlApiConfHelper.LEGACY_TIME_PARSER_POLICY_KEY;
    String CASE_SENSITIVE_KEY = SqlApiConfHelper.CASE_SENSITIVE_KEY;
    String SESSION_LOCAL_TIMEZONE_KEY = SqlApiConfHelper.SESSION_LOCAL_TIMEZONE_KEY;
    String LOCAL_RELATION_CACHE_THRESHOLD_KEY =
            SqlApiConfHelper.LOCAL_RELATION_CACHE_THRESHOLD_KEY;

    static SqlApiConf get() {
        return SqlApiConfHelper.getConfGetter().get().get();
    }

    boolean ansiEnabled();

    boolean caseSensitiveAnalysis();

    int maxToStringFields();

    boolean setOpsPrecedenceEnforced();

    boolean exponentLiteralAsDecimalEnabled();

    boolean enforceReservedKeywords();

    boolean doubleQuotedIdentifiers();

    AtomicType timestampType();

    boolean allowNegativeScaleOfDecimalEnabled();

    boolean charVarcharAsString();

    boolean datetimeJava8ApiEnabled();

    String sessionLocalTimeZone();

    LegacyBehaviorPolicy legacyTimeParserPolicy();
}
