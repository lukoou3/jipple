package com.jipple.sql.catalyst.util;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

public class JippleParserUtils {
    /** Get the command which created the token. */
    public static String command(ParserRuleContext ctx) {
        CharStream stream = ctx.getStart().getInputStream();
        return stream.getText(Interval.of(0, stream.size() - 1));
    }

}
