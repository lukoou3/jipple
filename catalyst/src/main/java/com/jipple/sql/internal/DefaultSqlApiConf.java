package com.jipple.sql.internal;

import com.jipple.sql.types.AtomicType;
import com.jipple.sql.types.TimestampType;

import java.util.TimeZone;

/**
 * Defaults configurations used when no other {@link SqlApiConf} getter is set.
 */
public final class DefaultSqlApiConf implements SqlApiConf {
    public static final DefaultSqlApiConf INSTANCE = new DefaultSqlApiConf();

    private DefaultSqlApiConf() {
    }

    @Override
    public boolean ansiEnabled() {
        return false;
    }

    @Override
    public boolean caseSensitiveAnalysis() {
        return false;
    }

    @Override
    public int maxToStringFields() {
        return 50;
    }

    @Override
    public boolean setOpsPrecedenceEnforced() {
        return false;
    }

    @Override
    public boolean exponentLiteralAsDecimalEnabled() {
        return false;
    }

    @Override
    public boolean enforceReservedKeywords() {
        return false;
    }

    @Override
    public boolean doubleQuotedIdentifiers() {
        return false;
    }

    @Override
    public AtomicType timestampType() {
        return TimestampType.INSTANCE;
    }

    @Override
    public boolean allowNegativeScaleOfDecimalEnabled() {
        return false;
    }

    @Override
    public boolean charVarcharAsString() {
        return false;
    }

    @Override
    public boolean datetimeJava8ApiEnabled() {
        return false;
    }

    @Override
    public String sessionLocalTimeZone() {
        return TimeZone.getDefault().getID();
    }

    @Override
    public LegacyBehaviorPolicy legacyTimeParserPolicy() {
        return LegacyBehaviorPolicy.EXCEPTION;
    }
}
