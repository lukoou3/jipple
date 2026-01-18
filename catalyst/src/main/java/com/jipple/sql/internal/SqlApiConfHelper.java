package com.jipple.sql.internal;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * SqlApiConfHelper is created to avoid a deadlock during a concurrent access to SQLConf and
 * SqlApiConf, which is because SQLConf and SqlApiConf tries to load each other upon
 * initializations. SqlApiConfHelper is private to sql package and is not supposed to be
 * accessed by end users. Variables and methods within SqlApiConfHelper are defined to
 * be used by SQLConf and SqlApiConf only.
 */
public final class SqlApiConfHelper {
    // Shared keys.
    public static final String ANSI_ENABLED_KEY = "jipple.sql.ansi.enabled";
    public static final String LEGACY_TIME_PARSER_POLICY_KEY = "jipple.sql.legacy.timeParserPolicy";
    public static final String CASE_SENSITIVE_KEY = "jipple.sql.caseSensitive";
    public static final String SESSION_LOCAL_TIMEZONE_KEY = "jipple.sql.session.timeZone";
    public static final String LOCAL_RELATION_CACHE_THRESHOLD_KEY =
            "jipple.sql.session.localRelationCacheThreshold";

    public static final AtomicReference<Supplier<SqlApiConf>> CONF_GETTER =
            new AtomicReference<>(() -> DefaultSqlApiConf.INSTANCE);

    private SqlApiConfHelper() {
    }

    public static AtomicReference<Supplier<SqlApiConf>> getConfGetter() {
        return CONF_GETTER;
    }

    /**
     * Sets the active config getter.
     */
    public static void setConfGetter(Supplier<SqlApiConf> getter) {
        CONF_GETTER.set(getter);
    }
}
