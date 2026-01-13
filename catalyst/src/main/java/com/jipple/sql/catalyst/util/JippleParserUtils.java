package com.jipple.sql.catalyst.util;

import com.jipple.sql.catalyst.trees.CurrentOrigin;
import com.jipple.sql.catalyst.trees.Origin;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.util.function.Supplier;

public class JippleParserUtils {
    /** Get the command which created the token. */
    public static String command(ParserRuleContext ctx) {
        CharStream stream = ctx.getStart().getInputStream();
        return stream.getText(Interval.of(0, stream.size() - 1));
    }

    /** Get the origin (line and position) of the token. */
    public static Origin position(Token token) {
        return new Origin(token.getLine(),  token.getCharPositionInLine());
    }

    /**
     * Register the origin of the context. Any TreeNode created in the closure will be assigned the
     * registered origin. This method restores the previously set origin after completion of the
     * closure.
     * 
     * @param ctx the parser rule context
     * @param sqlText the SQL text (optional)
     * @param f the function to execute
     * @param <T> the return type
     * @return the result of the function
     */
    public static <T> T withOrigin(ParserRuleContext ctx, String sqlText, Supplier<T> f) {
        Origin current = CurrentOrigin.get();
        String text = sqlText != null ? sqlText : current.sqlText;
        if (text == null || text.isEmpty()) {
            CurrentOrigin.set(position(ctx.getStart()));
        } else {
            CurrentOrigin.set(positionAndText(ctx.getStart(), ctx.getStop(), text,
                current.objectType, current.objectName));
        }
        try {
            return f.get();
        } finally {
            CurrentOrigin.set(current);
        }
    }
    
    /**
     * Register the origin of the context without SQL text.
     * 
     * @param ctx the parser rule context
     * @param f the function to execute
     * @param <T> the return type
     * @return the result of the function
     */
    public static <T> T withOrigin(ParserRuleContext ctx, Supplier<T> f) {
        return withOrigin(ctx, null, f);
    }

    /**
     * Get the origin with position and text information.
     *
     * @param start the start token
     * @param stop the stop token
     * @param sqlText the SQL text
     * @param objectType the object type
     * @param objectName the object name
     * @return the origin with position and text
     */
    private static Origin positionAndText(Token start, Token stop, String sqlText,
                                         String objectType, String objectName) {
        Integer startIndex = start != null ? start.getStartIndex() : null;
        Integer stopIndex = stop != null ? stop.getStopIndex() : null;
        return new Origin(
                start != null ? start.getLine() : null,
                start != null ? start.getCharPositionInLine() : null,
                startIndex,
                stopIndex,
                sqlText,
                objectType,
                objectName
        );
    }

}
