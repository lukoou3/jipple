package com.jipple.sql.catalyst.expressions.datetime;

public class CurrentTimestamp extends CurrentTimestampLike {
    @Override
    public String prettyName() {
        return "current_timestamp";
    }
}
