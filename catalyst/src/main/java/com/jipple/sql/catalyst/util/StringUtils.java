package com.jipple.sql.catalyst.util;

import com.jipple.unsafe.types.UTF8String;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtils {

    /**
     * Set of UTF8String values that represent true.
     * Includes: "t", "true", "y", "yes", "1"
     */
    private static final Set<UTF8String> trueStrings = Stream.of("t", "true", "y", "yes", "1")
            .map(UTF8String::fromString)
            .collect(Collectors.toCollection(HashSet::new));

    /**
     * Set of UTF8String values that represent false.
     * Includes: "f", "false", "n", "no", "0"
     */
    private static final Set<UTF8String> falseStrings = Stream.of("f", "false", "n", "no", "0")
            .map(UTF8String::fromString)
            .collect(Collectors.toCollection(HashSet::new));

    /**
     * Checks if the given UTF8String represents a true value.
     * The string is trimmed and converted to lowercase before checking.
     * 
     * @param s the UTF8String to check
     * @return true if the string represents a true value, false otherwise
     */
    public static boolean isTrueString(UTF8String s) {
        return trueStrings.contains(s.trimAll().toLowerCase());
    }

    /**
     * Checks if the given UTF8String represents a false value.
     * The string is trimmed and converted to lowercase before checking.
     * 
     * @param s the UTF8String to check
     * @return true if the string represents a false value, false otherwise
     */
    public static boolean isFalseString(UTF8String s) {
        return falseStrings.contains(s.trimAll().toLowerCase());
    }


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
