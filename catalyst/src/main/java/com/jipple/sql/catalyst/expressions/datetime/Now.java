package com.jipple.sql.catalyst.expressions.datetime;

public class Now extends CurrentTimestampLike {
    @Override
    public String prettyName() {
        return "now";
    }
}
