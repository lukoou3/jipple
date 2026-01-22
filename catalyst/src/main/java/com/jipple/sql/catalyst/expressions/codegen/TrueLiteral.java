package com.jipple.sql.catalyst.expressions.codegen;

import java.lang.Boolean;

/**
 * A literal representing the boolean value true.
 */
public class TrueLiteral extends LiteralValue {
    public static final TrueLiteral INSTANCE = new TrueLiteral();

    private TrueLiteral() {
        super("true", Boolean.TYPE);
    }
}

