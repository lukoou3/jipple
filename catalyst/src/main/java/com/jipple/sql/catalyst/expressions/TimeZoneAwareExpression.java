package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;

public interface TimeZoneAwareExpression<T extends Expression & TimeZoneAwareExpression> {

    /** the timezone ID to be used to evaluate value. */
    Option<String> timeZoneId();

    /** Returns a copy of this expression with the specified timeZoneId. */
    T withTimeZone(String timeZoneId);

}
