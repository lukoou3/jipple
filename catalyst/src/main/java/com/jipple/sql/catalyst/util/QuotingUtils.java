package com.jipple.sql.catalyst.util;

import java.util.List;

public final class QuotingUtils {
    private QuotingUtils() {
    }

    private static String quoteByDefault(String elem) {
        return "\"" + elem + "\"";
    }

    public static String toSQLConf(String conf) {
        return quoteByDefault(conf);
    }

    public static String toSQLSchema(String schema) {
        return quoteByDefault(schema);
    }

    public static String quoteIdentifier(String name) {
        // Escapes back-ticks within the identifier name with double-back-ticks, and then quote the
        // identifier with back-ticks.
        return "`" + name.replace("`", "``") + "`";
    }

    public static String quoteNameParts(List<String> name) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            if (i > 0) {
                builder.append('.');
            }
            builder.append(quoteIdentifier(name.get(i)));
        }
        return builder.toString();
    }

    public static String quoteIfNeeded(String part) {
        if (part.matches("[a-zA-Z0-9_]+") && !part.matches("\\d+")) {
            return part;
        }
        return "`" + part.replace("`", "``") + "`";
    }

    public static String quoted(String[] namespace) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < namespace.length; i++) {
            if (i > 0) {
                builder.append('.');
            }
            builder.append(quoteIfNeeded(namespace[i]));
        }
        return builder.toString();
    }

    public static String escapeSingleQuotedString(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\'') {
                builder.append("\\\'");
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
