package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;

import java.time.ZoneId;

public interface TimeZoneAwareExpression {

    /** the timezone ID to be used to evaluate value. */
    Option<String> timeZoneId();

    /** Returns a copy of this expression with the specified timeZoneId. */
    Expression withTimeZone(String timeZoneId);

    default ZoneId zoneId() {
        return ZoneId.of(timeZoneId().get(), ZoneId.SHORT_IDS);
    }
}
