package com.jipple.sql.catalyst.util;

import com.jipple.sql.catalyst.trees.CurrentOrigin;
import com.jipple.sql.catalyst.trees.Origin;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.nio.CharBuffer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JippleParserUtils {
    private static final Pattern U16_CHAR_PATTERN = Pattern.compile("\\\\u([a-fA-F0-9]{4})(?s).*");
    private static final Pattern U32_CHAR_PATTERN = Pattern.compile("\\\\U([a-fA-F0-9]{8})(?s).*");
    private static final Pattern OCTAL_CHAR_PATTERN = Pattern.compile("\\\\([01][0-7]{2})(?s).*");
    private static final Pattern ESCAPED_CHAR_PATTERN = Pattern.compile("\\\\((?s).)(?s).*");

    /** Unescape backslash-escaped string enclosed by quotes. */
    public static String unescapeSQLString(String b) {
        StringBuilder sb = new StringBuilder(b.length());

        if (b.startsWith("r") || b.startsWith("R")) {
            return b.substring(2, b.length() - 1);
        }

        // Skip the first and last quotations enclosing the string literal.
        CharBuffer charBuffer = CharBuffer.wrap(b, 1, b.length() - 1);

        while (charBuffer.remaining() > 0) {
            String remaining = charBuffer.toString();
            Matcher matcher = U16_CHAR_PATTERN.matcher(remaining);
            if (matcher.matches()) {
                // \u0000 style 16-bit unicode character literals.
                sb.append((char) Integer.parseInt(matcher.group(1), 16));
                charBuffer.position(charBuffer.position() + 6);
                continue;
            }

            matcher = U32_CHAR_PATTERN.matcher(remaining);
            if (matcher.matches()) {
                // \U00000000 style 32-bit unicode character literals.
                long codePoint = Long.parseLong(matcher.group(1), 16);
                if (codePoint < 0x10000) {
                    sb.append((char) (codePoint & 0xFFFF));
                } else {
                    long highSurrogate = (codePoint - 0x10000) / 0x400 + 0xD800;
                    long lowSurrogate = (codePoint - 0x10000) % 0x400 + 0xDC00;
                    sb.append((char) highSurrogate);
                    sb.append((char) lowSurrogate);
                }
                charBuffer.position(charBuffer.position() + 10);
                continue;
            }

            matcher = OCTAL_CHAR_PATTERN.matcher(remaining);
            if (matcher.matches()) {
                // \000 style character literals.
                sb.append((char) Integer.parseInt(matcher.group(1), 8));
                charBuffer.position(charBuffer.position() + 4);
                continue;
            }

            matcher = ESCAPED_CHAR_PATTERN.matcher(remaining);
            if (matcher.matches()) {
                // escaped character literals.
                appendEscapedChar(sb, matcher.group(1).charAt(0));
                charBuffer.position(charBuffer.position() + 2);
                continue;
            }

            // non-escaped character literals.
            sb.append(charBuffer.get());
        }
        return sb.toString();
    }

    private static void appendEscapedChar(StringBuilder sb, char n) {
        switch (n) {
            case '0':
                sb.append('\u0000');
                break;
            case '\'':
                sb.append('\'');
                break;
            case '"':
                sb.append('\"');
                break;
            case 'b':
                sb.append('\b');
                break;
            case 'n':
                sb.append('\n');
                break;
            case 'r':
                sb.append('\r');
                break;
            case 't':
                sb.append('\t');
                break;
            case 'Z':
                sb.append('\u001A');
                break;
            case '\\':
                sb.append('\\');
                break;
            // The following 2 lines are exactly what MySQL does TODO: why do we do this?
            case '%':
                sb.append("\\%");
                break;
            case '_':
                sb.append("\\_");
                break;
            default:
                sb.append(n);
                break;
        }
    }

    /** Convert a string token into a string. */
    public static String string(Token token) {
        return unescapeSQLString(token.getText());
    }

    /** Convert a string node into a string. */
    public static String string(TerminalNode node) {
        return unescapeSQLString(node.getText());
    }

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
