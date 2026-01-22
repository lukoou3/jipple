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
     * Creates a code block from string parts and arguments.
     * Note: This is a simplified version. For full functionality, use the code(StringContext, Object...) method.
     */
    public static Block code(Object... args) {
        // This is a simplified version - the full implementation would need StringContext
        // For now, we'll create a simple code block
        if (args.length == 0) {
            return EmptyBlock.INSTANCE;
        }

        // Validate arguments
        for (Object arg : args) {
            if (!(arg instanceof ExprValue || arg instanceof Inline || arg instanceof Block ||
                  arg instanceof Boolean || arg instanceof Byte || arg instanceof Integer ||
                  arg instanceof Long || arg instanceof Float || arg instanceof Double ||
                  arg instanceof String)) {
                throw new IllegalArgumentException("Can not interpolate " + arg.getClass().getName());
            }
        }

        // This is a placeholder - the actual implementation would need StringContext
        // which is a Scala feature. We'll need to create a Java equivalent.
        return EmptyBlock.INSTANCE; // TODO: Implement proper code block creation
    }

    /**
     * Creates a code block from a string context and arguments.
     * This is a Java equivalent of Scala's string interpolation.
     */
    public static Block code(StringContext sc, Object... args) {
        sc.checkLengths(args);
        if (sc.parts().length == 0) {
            return EmptyBlock.INSTANCE;
        }

        // Validate arguments
        for (Object arg : args) {
            if (!(arg instanceof ExprValue || arg instanceof Inline || arg instanceof Block ||
                  arg instanceof Boolean || arg instanceof Byte || arg instanceof Integer ||
                  arg instanceof Long || arg instanceof Float || arg instanceof Double ||
                  arg instanceof String)) {
                throw new IllegalArgumentException("Can not interpolate " + arg.getClass().getName());
            }
        }

        Tuple2<List<String>, List<JavaCode>> result = foldLiteralArgs(sc.parts(), Arrays.asList(args));
        return new CodeBlock(result._1, result._2);
    }

    /**
     * Folds eagerly the literal args into the code parts.
     */
    private static Tuple2<List<String>, List<JavaCode>> foldLiteralArgs(
            String[] parts, List<Object> args) {
        List<String> codeParts = new ArrayList<>();
        List<JavaCode> blockInputs = new ArrayList<>();

        int partIndex = 0;
        int argIndex = 0;
        StringBuilder buf = new StringBuilder(CODE_BLOCK_BUFFER_LENGTH);

        if (partIndex < parts.length) {
            buf.append(StringContext.treatEscapes(parts[partIndex++]));
        }

        while (partIndex < parts.length && argIndex < args.size()) {
            Object input = args.get(argIndex++);
            if (input instanceof ExprValue || input instanceof CodeBlock) {
                codeParts.add(buf.toString());
                buf.setLength(0);
                blockInputs.add((JavaCode) input);
            } else if (input != EmptyBlock.INSTANCE) {
                buf.append(input);
            }
            if (partIndex < parts.length) {
                buf.append(StringContext.treatEscapes(parts[partIndex++]));
            }
        }
        codeParts.add(buf.toString());

        return new Tuple2<>(codeParts, blockInputs);
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
        if (template == null || template.isEmpty()) {
            return EmptyBlock.INSTANCE;
        }
        
        List<String> codeParts = new ArrayList<>();
        List<JavaCode> blockInputs = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder(CODE_BLOCK_BUFFER_LENGTH);
        
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        int lastEnd = 0;
        
        while (matcher.find()) {
            // Add text before the match
            if (matcher.start() > lastEnd) {
                currentPart.append(template, lastEnd, matcher.start());
            }
            
            String escapedDollar = matcher.group(1); // escaped dollar $$
            String placeholderName = matcher.group(2); // placeholder name in ${variable}
            
            if (escapedDollar != null) {
                // Escaped dollar: $$
                // Add single $ to current part
                currentPart.append('$');
            } else if (placeholderName != null) {
                // Found a placeholder ${name}
                // First, save the current part if it's not empty
                // Apply treatEscapes to code parts (template strings), but not to inserted values
                if (currentPart.length() > 0) {
                    codeParts.add(StringContext.treatEscapes(currentPart.toString()));
                    currentPart.setLength(0);
                }
                
                // Get the value for this placeholder
                Object value = variables.get(placeholderName);
                if (value == null) {
                    throw new IllegalArgumentException(
                        "Placeholder '" + placeholderName + "' not found in variables. Available: " + variables.keySet());
                }
                
                // Convert value to JavaCode
                // Note: The value itself should NOT be escaped - only template code parts are escaped
                JavaCode codeValue = toJavaCode(value);
                blockInputs.add(codeValue);
            }
            
            lastEnd = matcher.end();
        }
        
        // Add remaining text after the last match
        if (lastEnd < template.length()) {
            currentPart.append(template, lastEnd, template.length());
        }
        
        // Add the final part (apply treatEscapes to code parts)
        if (currentPart.length() > 0) {
            codeParts.add(StringContext.treatEscapes(currentPart.toString()));
        }
        
        // If no placeholders were found, return a simple code block
        if (codeParts.isEmpty() && blockInputs.isEmpty()) {
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
    private static JavaCode toJavaCode(Object value) {
        if (value instanceof JavaCode) {
            return (JavaCode) value;
        } else if (value instanceof String) {
            return new Inline((String) value);
        } else if (value instanceof Boolean) {
            return new Inline(String.valueOf(value));
        } else if (value instanceof Number) {
            return new Inline(String.valueOf(value));
        } else if (value instanceof Character) {
            return new Inline("'" + value + "'");
        } else {
            // For other types, convert to string
            return new Inline(String.valueOf(value));
        }
    }

    /**
     * Simple tuple class for returning two values.
     */
    private static class Tuple2<T1, T2> {
        final T1 _1;
        final T2 _2;

        Tuple2(T1 _1, T2 _2) {
            this._1 = _1;
            this._2 = _2;
        }
    }
}

