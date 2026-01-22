package com.jipple.sql.catalyst.expressions.codegen;

import java.lang.Boolean;

/**
 * A literal representing the boolean value false.
 */
public class FalseLiteral extends LiteralValue {
    public static final FalseLiteral INSTANCE = new FalseLiteral();

    private FalseLiteral() {
        super("false", Boolean.TYPE);
    }
}

