package com.jipple.sql.catalyst.expressions.codegen;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for Block.
 */
public class BlockUtils {
    public static final int CODE_BLOCK_BUFFER_LENGTH = 512;
    
    // Pattern to match $$ (escaped dollar) first, then ${placeholder}
    // Order matters: we need to match $$ before matching ${variable}
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(\\$\\$)|\\$\\{([^}]+)\\}");

    /**
     * Concatenates blocks into a single block.
     */
    public static Block blocksToBlock(List<Block> blocks) {
        return blocks.stream().reduce(EmptyBlock.INSTANCE, Block::plus);
    }

    /**
     * Creates a code block from a template string with placeholders, similar to Python's format().
     * Supports Java text blocks (""") for multi-line templates.
     * 
     * Example:
     * <pre>{@code
     * VariableValue isNull = JavaCode.isNullVariable("expr1_isNull");
     * String stringLiteral = "false";
     * 
     * // Single line
     * Block code = BlockUtils.block(
     *     "boolean ${isNull} = ${stringLiteral};",
     *     Map.of("isNull", isNull, "stringLiteral", stringLiteral)
     * );
     * 
     * // Multi-line using text block (Java 17+)
     * Block code2 = BlockUtils.block(
     *     """
     *     boolean ${isNull} = false;
     *     int ${value} = -1;
     *     if (${isNull}) {
     *         return;
     *     }
     *     """,
     *     Map.of("isNull", isNull, "value", value)
     * );
     * 
     * // Escaping: use $$ to output a single $
     * Block code3 = BlockUtils.block(
     *     "String price = \"$${amount}\";",
     *     Map.of("amount", "100")
     * );
     * // Result: "String price = \"${amount}\";"
     * }</pre>
     * 
     * @param template The template string with ${placeholder} syntax. Can be a text block for multi-line.
     *                 Use $$ to escape a dollar sign.
     * @param variables Map of placeholder names to their values (can be JavaCode, String, or primitives)
     * @return A CodeBlock with the template processed
     * @throws IllegalArgumentException if a placeholder is not found in variables
     */
    public static Block block(String template, Map<String, Object> variables) {
        if (template == null) {
            return EmptyBlock.INSTANCE;
        }

        List<String> codeParts = new ArrayList<>();
        List<JavaCode> blockInputs = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder(CODE_BLOCK_BUFFER_LENGTH);

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        int lastEnd = 0;

        while (matcher.find()) {
            // Add text before the match (treat escapes in template parts only)
            if (matcher.start() > lastEnd) {
                String part = template.substring(lastEnd, matcher.start());
                currentPart.append(StringContext.treatEscapes(part));
            }

            String escapedDollar = matcher.group(1); // escaped dollar $$
            String placeholderName = matcher.group(2); // placeholder name in ${variable}

            if (escapedDollar != null) {
                // Escaped dollar: $$ -> $
                currentPart.append('$');
            } else if (placeholderName != null) {
                if (variables == null || !variables.containsKey(placeholderName)) {
                    throw new IllegalArgumentException(
                        "Placeholder '" + placeholderName + "' not found in variables. Available: " +
                            (variables == null ? "[]" : variables.keySet()));
                }

                Object value = variables.get(placeholderName);
                if (value == null) {
                    throw new IllegalArgumentException(
                        "Placeholder '" + placeholderName + "' is null. Available: " + variables.keySet());
                }

                if (!isValidInterpolationArg(value)) {
                    throw new IllegalArgumentException(
                        "Can not interpolate " + value.getClass().getName());
                }

                if (value == EmptyBlock.INSTANCE) {
                    // Ignore empty blocks, keep current part
                } else if (value instanceof ExprValue || value instanceof Block) {
                    // Flush current part (even if empty) and preserve block input
                    codeParts.add(currentPart.toString());
                    currentPart.setLength(0);
                    blockInputs.add((JavaCode) value);
                } else if (value instanceof Inline) {
                    currentPart.append(((Inline) value).code());
                } else {
                    // Primitive/String literals are folded into code parts
                    currentPart.append(String.valueOf(value));
                }
            }

            lastEnd = matcher.end();
        }

        // Add remaining text after the last match (treat escapes in template parts only)
        if (lastEnd < template.length()) {
            String tail = template.substring(lastEnd, template.length());
            currentPart.append(StringContext.treatEscapes(tail));
        }

        // Always add the final part to keep codeParts = blockInputs + 1
        codeParts.add(currentPart.toString());

        if (blockInputs.isEmpty() && codeParts.size() == 1 && codeParts.get(0).isEmpty()) {
            return EmptyBlock.INSTANCE;
        }

        return new CodeBlock(codeParts, blockInputs);
    }
    
    /**
     * Converts an object to JavaCode.
     * - JavaCode objects are returned as-is
     * - Strings and primitives are wrapped in Inline
     * - Other types are converted to String and wrapped in Inline
     */
    private static boolean isValidInterpolationArg(Object value) {
        return value instanceof ExprValue
            || value instanceof Inline
            || value instanceof Block
            || value instanceof Boolean
            || value instanceof Byte
            || value instanceof Integer
            || value instanceof Long
            || value instanceof Float
            || value instanceof Double
            || value instanceof String;
    }

}

