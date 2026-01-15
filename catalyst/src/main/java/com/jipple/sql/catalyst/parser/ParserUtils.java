package com.jipple.sql.catalyst.parser;

import com.jipple.sql.catalyst.trees.Origin;
import com.jipple.sql.catalyst.util.JippleParserUtils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.function.Supplier;

public class ParserUtils {

    /** Unescape backslash-escaped string enclosed by quotes. */
    public static String unescapeSQLString(String b) {
        return JippleParserUtils.unescapeSQLString(b);
    }

    /** Convert a string token into a string. */
    public static String string(Token token) {
        return JippleParserUtils.string(token);
    }

    /** Convert a string node into a string. */
    public static String string(TerminalNode node) {
        return JippleParserUtils.string(node);
    }

    /** Get the origin (line and position) of the token. */
    public static Origin position(Token token) {
        return JippleParserUtils.position(token);
    }

    /**
     * Register the origin of the context. Any TreeNode created in the closure will be assigned the
     * registered origin. This method restores the previously set origin after completion of the
     * closure.
     */
    public static <T> T withOrigin(ParserRuleContext ctx, String sqlText, Supplier<T> f) {
        return JippleParserUtils.withOrigin(ctx, sqlText, f);
    }

    /** Register the origin of the context without SQL text. */
    public static <T> T withOrigin(ParserRuleContext ctx, Supplier<T> f) {
        return JippleParserUtils.withOrigin(ctx, f);
    }

    /** Get the command which created the token. */
    public static String command(ParserRuleContext ctx) {
        return JippleParserUtils.command(ctx);
    }

    /** Convert a string node into a string without unescaping. */
    public static String stringWithoutUnescape(TerminalNode node) {
        // STRING parser rule forces that the input always has quotes at the starting and ending.
        return node.getText().substring(1, node.getText().length() - 1);
    }

}
