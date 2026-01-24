package com.jipple.sql.catalyst.expressions.codegen;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class that indents a block of code based on the curly braces and parentheses.
 * This is used to prettify generated code when in debug mode (or exceptions).
 *
 * Written by Matei Zaharia.
 */
public final class CodeFormatter {
    private static final Pattern COMMENT_HOLDER = Pattern.compile("/\\*(.+?)\\*/");
    private static final Pattern COMMENT_REGEXP = Pattern.compile(
            "([ |\\t]*?/\\*[\\s|\\S]*?\\*/[ |\\t]*?)|([ |\\t]*?//[\\s\\S]*?\\n)"
    );
    private static final Pattern EXTRA_NEWLINES_REGEXP = Pattern.compile("\\n\\s*\\n");

    private CodeFormatter() {
        // Utility class, prevent instantiation
    }

    public static String format(CodeAndComment code) {
        return format(code, -1);
    }

    public static String format(CodeAndComment code, int maxLines) {
        Formatter formatter = new Formatter();
        String[] lines = code.body.split("\n");
        boolean needToTruncate = maxLines >= 0 && lines.length > maxLines;
        String[] filteredLines = needToTruncate ? Arrays.copyOfRange(lines, 0, maxLines) : lines;
        for (String line : filteredLines) {
            String commentReplaced = replaceCommentHolder(line.trim(), code.comment);
            String[] comments = commentReplaced.split("\n");
            for (String commentLine : comments) {
                formatter.addLine(commentLine);
            }
        }
        if (needToTruncate) {
            formatter.addLine("[truncated to " + maxLines + " lines (total lines is " + lines.length + ")]");
        }
        return formatter.result();
    }

    public static String stripExtraNewLines(String input) {
        StringBuilder code = new StringBuilder();
        String lastLine = "dummy";
        for (String l : input.split("\n")) {
            String line = l.trim();
            boolean skip = line.isEmpty() &&
                    (lastLine.isEmpty() || lastLine.endsWith("{") || lastLine.endsWith("*/"));
            if (!skip) {
                code.append(line).append("\n");
            }
            lastLine = line;
        }
        return code.toString();
    }

    public static CodeAndComment stripOverlappingComments(CodeAndComment codeAndComment) {
        StringBuilder code = new StringBuilder();
        Map<String, String> map = codeAndComment.comment;

        String lastLine = "dummy";
        for (String l : codeAndComment.body.split("\n")) {
            String line = l.trim();
            Optional<String> lastComment = getComment(lastLine, map);
            Optional<String> currentComment = getComment(line, map);
            boolean skip = lastComment.isPresent() && currentComment.isPresent()
                    && lastComment.get().substring(3).contains(currentComment.get().substring(3));
            if (!skip) {
                code.append(line).append("\n");
            }
            lastLine = line;
        }
        return new CodeAndComment(code.toString().trim(), map);
    }

    public static String stripExtraNewLinesAndComments(String input) {
        String withoutComments = COMMENT_REGEXP.matcher(input).replaceAll("");
        return EXTRA_NEWLINES_REGEXP.matcher(withoutComments).replaceAll("\n");
    }

    private static Optional<String> getComment(String line, Map<String, String> map) {
        if (line.startsWith("/*") && line.endsWith("*/")) {
            String key = line.substring(2, line.length() - 2);
            return Optional.ofNullable(map.get(key));
        }
        return Optional.empty();
    }

    private static String replaceCommentHolder(String line, Map<String, String> comments) {
        Matcher matcher = COMMENT_HOLDER.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = comments.get(key);
            if (replacement != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static final class Formatter {
        private final StringBuilder code = new StringBuilder();
        private final int indentSize = 2;

        // Tracks the level of indentation in the current line.
        private int indentLevel = 0;
        private String indentString = "";
        private int currentLine = 1;

        // Tracks the level of indentation in multi-line comment blocks.
        private boolean inCommentBlock = false;
        private int indentLevelOutsideCommentBlock = indentLevel;

        private void addLine(String line) {
            // We currently infer the level of indentation of a given line based on a simple heuristic that
            // examines the number of parenthesis and braces in that line. This isn't the most robust
            // implementation but works for all code that we generate.
            int indentChange = countAny(line, "({") - countAny(line, ")}");
            int newIndentLevel = Math.max(0, indentLevel + indentChange);

            // Please note that while we try to format the comment blocks in exactly the same way as the
            // rest of the code, once the block ends, we reset the next line's indentation level to what it
            // was immediately before entering the comment block.
            if (!inCommentBlock) {
                if (line.startsWith("/*")) {
                    // Handle multi-line comments
                    inCommentBlock = true;
                    indentLevelOutsideCommentBlock = indentLevel;
                } else if (line.startsWith("//")) {
                    // Handle single line comments
                    newIndentLevel = indentLevel;
                }
            }
            if (inCommentBlock) {
                if (line.endsWith("*/")) {
                    inCommentBlock = false;
                    newIndentLevel = indentLevelOutsideCommentBlock;
                }
            }

            // Lines starting with '}' should be de-indented even if they contain '{' after;
            // in addition, lines ending with ':' are typically labels
            String thisLineIndent;
            if (line.startsWith("}") || line.startsWith(")") || line.endsWith(":")) {
                int indentCount = Math.max(0, indentLevel - 1);
                thisLineIndent = " ".repeat(indentSize * indentCount);
            } else {
                thisLineIndent = indentString;
            }

            code.append(String.format("/* %03d */", currentLine));
            if (line.trim().length() > 0) {
                code.append(" "); // add a space after the line number comment.
                code.append(thisLineIndent);
                if ((inCommentBlock && line.startsWith("*")) || line.startsWith("*/")) {
                    code.append(" ");
                }
                code.append(line);
            }
            code.append("\n");

            indentLevel = newIndentLevel;
            indentString = " ".repeat(indentSize * newIndentLevel);
            currentLine += 1;
        }

        private String result() {
            return code.toString();
        }

        private int countAny(String line, String chars) {
            int count = 0;
            for (int i = 0; i < line.length(); i++) {
                if (chars.indexOf(line.charAt(i)) >= 0) {
                    count += 1;
                }
            }
            return count;
        }
    }
}
