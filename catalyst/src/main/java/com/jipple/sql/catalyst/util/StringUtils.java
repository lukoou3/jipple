package com.jipple.sql.catalyst.util;

import java.util.regex.Pattern;

public class StringUtils {

    /**
     * Validate and convert SQL 'like' pattern to a Java regular expression.
     *
     * Underscores (_) are converted to '.' and percent signs (%) are converted to '.*', other
     * characters are quoted literally. Escaping is done according to the rules specified in
     * [[com.jipple.sql.catalyst.expressions.regexp.Like]] usage documentation. An invalid pattern will
     * throw an [[AnalysisException]].
     *
     * @param pattern the SQL pattern to convert
     * @param escapeChar the escape string contains one character.
     * @return the equivalent Java regular expression of the pattern
     */
    public static String escapeLikeRegex(String pattern, char escapeChar) {
        StringBuilder out = new StringBuilder();
        int length = pattern.length();

        for (int i = 0; i < length; i++) {
            char c = pattern.charAt(i);
            if (c == escapeChar && i + 1 < length) {
                i++;
                c = pattern.charAt(i);
                if (c == '_' || c == '%') {
                    out.append(Pattern.quote(Character.toString(c)));
                } else if (c == escapeChar) {
                    out.append(Pattern.quote(Character.toString(c)));
                } else {
                    throw new IllegalArgumentException(String.format("the escape character is not allowed to precede '%s'", Character.toString(c)));
                }
            } else if (c == escapeChar) {
                throw new IllegalArgumentException("it is not allowed to end with the escape character");
            } else if (c == '_') {
                out.append(".");
            } else if (c == '%') {
                out.append(".*");
            } else {
                out.append(Pattern.quote(Character.toString(c)));
            }
        }

        return "(?s)" + out.toString(); // (?s) enables dotall mode, causing "." to match new lines
    }

}
