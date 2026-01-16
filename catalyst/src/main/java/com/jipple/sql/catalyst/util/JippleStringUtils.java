package com.jipple.sql.catalyst.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for string operations.
 */
public class JippleStringUtils {

    /**
     * Formats two strings side by side for comparison.
     * Splits both strings by newlines and formats them side by side.
     * 
     * @param left the left string
     * @param right the right string
     * @return a list of strings, each representing a line with left and right content side by side
     */
    public static List<String> sideBySide(String left, String right) {
        return sideBySide(splitLines(left), splitLines(right));
    }

    /**
     * Formats two sequences of strings side by side for comparison.
     * Pads the shorter sequence with empty strings, then pairs up corresponding lines.
     * Each output line shows:
     * - A marker: " " if lines are equal, "!" if different
     * - The left line (padded to max left width)
     * - Three spaces separator
     * - The right line
     * 
     * @param left the left sequence of strings
     * @param right the right sequence of strings
     * @return a list of strings, each representing a line with left and right content side by side
     */
    public static List<String> sideBySide(List<String> left, List<String> right) {
        // Find the maximum length of left strings for padding
        int maxLeftSize = left.stream().mapToInt(String::length).max().orElse(0);

        // Pad the shorter sequence with empty strings
        List<String> leftPadded = new ArrayList<>(left);
        int rightSize = right.size();
        int leftSize = left.size();
        int leftPadding = Math.max(rightSize - leftSize, 0);
        for (int i = 0; i < leftPadding; i++) {
            leftPadded.add("");
        }

        List<String> rightPadded = new ArrayList<>(right);
        int rightPadding = Math.max(leftSize - rightSize, 0);
        for (int i = 0; i < rightPadding; i++) {
            rightPadded.add("");
        }

        // Zip and format each pair of lines
        return IntStream.range(0, leftPadded.size())
                .mapToObj(i -> {
                    String l = leftPadded.get(i);
                    String r = rightPadded.get(i);
                    // Use " " if lines are equal, "!" if different
                    String marker = l.equals(r) ? " " : "!";
                    // Calculate padding: maxLeftSize - l.length + 3 spaces separator
                    int padding = maxLeftSize - l.length() + 3;
                    String paddingStr = org.apache.commons.lang3.StringUtils.repeat(" ", padding);
                    return marker + l + paddingStr + r;
                })
                .collect(Collectors.toList());
    }

    /**
     * Splits a string by newlines.
     * 
     * @param str the string to split
     * @return a list of lines
     */
    private static List<String> splitLines(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        String[] lines = str.split("\n", -1); // -1 to preserve trailing empty strings
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(line);
        }
        return result;
    }

}
