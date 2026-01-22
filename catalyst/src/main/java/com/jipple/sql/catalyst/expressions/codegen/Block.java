package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.trees.TreeNode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An abstract class representing a block of java code.
 */
public abstract class Block extends TreeNode<Block> implements JavaCode {
    // The leading prefix that should be stripped from each line.
    // By default we strip blanks or control characters followed by '|' from the line.
    private Character marginChar = '|';

    // Returns java code string for this code block.
    @Override
    public String toString() {
        if (marginChar != null) {
            return stripMargin(code(), marginChar).trim();
        } else {
            return code().trim();
        }
    }

    // We could remove comments, extra whitespaces and newlines when calculating length as it is used
    // only for codegen method splitting, but SPARK-30564 showed that this is a performance critical
    // function so we decided not to do so.
    public int length() {
        return toString().length();
    }

    public boolean isEmpty() {
        return toString().isEmpty();
    }

    public boolean nonEmpty() {
        return !isEmpty();
    }

    public Block stripMargin(char c) {
        this.marginChar = c;
        return this;
    }

    public Block stripMargin() {
        this.marginChar = '|';
        return this;
    }

    /**
     * Apply a map function to each java expression codes present in this java code, and return a new
     * java code based on the mapped java expression codes.
     */
    public Block transformExprValues(Function<ExprValue, ExprValue> f) {
        boolean[] changed = {false};

        Function<ExprValue, ExprValue> transform = e -> {
            ExprValue newE = f.apply(e);
            if (newE == null || newE.equals(e)) {
                return e;
            } else {
                changed[0] = true;
                return newE;
            }
        };

        Function<Object, Object> doTransform = new Function<Object, Object>() {
            @Override
            public Object apply(Object arg) {
                if (arg instanceof ExprValue) {
                    return transform.apply((ExprValue) arg);
                } else if (arg instanceof Optional) {
                    Optional<?> opt = (Optional<?>) arg;
                    return opt.map(this);
                } else if (arg instanceof Collection) {
                    Collection<?> coll = (Collection<?>) arg;
                    return coll.stream().map(this).collect(Collectors.toList());
                } else {
                    return arg;
                }
            }
        };

        Object[] newArgs = mapProductIterator(doTransform);
        if (changed[0]) {
            return (Block) makeCopy(newArgs);
        } else {
            return this;
        }
    }

    // Concatenates this block with other block.
    public Block plus(Block other) {
        if (other == EmptyBlock.INSTANCE) {
            return this;
        } else {
            // Create a simple code block that concatenates this and other
            List<String> codeParts = new java.util.ArrayList<>();
            codeParts.add("");
            codeParts.add("\n");
            codeParts.add("");
            List<JavaCode> blockInputs = new java.util.ArrayList<>();
            blockInputs.add(this);
            blockInputs.add(other);
            return new CodeBlock(codeParts, blockInputs);
        }
    }

    @Override
    public String verboseString(int maxFields) {
        return toString();
    }

    @Override
    public String simpleStringWithNodeId() {
        throw new IllegalStateException(nodeName() + " does not implement simpleStringWithNodeId");
    }

    /**
     * Creates a code block from a template string with placeholders, similar to Python's format().
     * This is a convenience method that delegates to BlockUtils.block().
     * Supports Java text blocks (""") for multi-line templates.
     * 
     * Example:
     * <pre>{@code
     * VariableValue isNull = JavaCode.isNullVariable("expr1_isNull");
     * VariableValue value = JavaCode.variable("expr1", IntegerType.INSTANCE);
     * 
     * // Single line
     * Block code = Block.block(
     *     "boolean ${isNull} = false;",
     *     Map.of("isNull", isNull)
     * );
     * 
     * // Multi-line using text block (Java 17+)
     * Block code2 = Block.block(
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
     * // With static import: import static ...Block.block;
     * Block code3 = block(
     *     "int ${value} = ${literal};",
     *     Map.of("value", value, "literal", JavaCode.literal("100", IntegerType.INSTANCE))
     * );
     * 
     * // Escaping: use $$ to output a single $
     * Block code4 = Block.block(
     *     "String price = \"$${amount}\";",
     *     Map.of("amount", "100")
     * );
     * }</pre>
     * 
     * @param template The template string with ${placeholder} syntax. Can be a text block for multi-line.
     *                 Use $$ to escape a dollar sign.
     * @param variables Map of placeholder names to their values
     * @return A CodeBlock with the template processed
     */
    public static Block block(String template, Map<String, Object> variables) {
        return BlockUtils.block(template, variables);
    }

    /**
     * Alias for {@link #block(String, Map)} for more concise usage.
     * Can be statically imported for even cleaner code.
     * 
     * Example with static import:
     * <pre>{@code
     * import static com.jipple.sql.catalyst.expressions.codegen.Block.code;
     * 
     * Block code = code(
     *     """
     *     boolean ${isNull} = false;
     *     int ${value} = -1;
     *     if (${isNull}) {
     *         return;
     *     }
     *     """,
     *     Map.of("isNull", isNull, "value", value)
     * );
     * }</pre>
     * 
     * @param template The template string with ${placeholder} syntax. Use $$ to escape a dollar sign.
     * @param variables Map of placeholder names to their values
     * @return A CodeBlock with the template processed
     */
    public static Block code(String template, Map<String, Object> variables) {
        return block(template, variables);
    }

    /**
     * Strips the margin character from each line of the string.
     * 
     * <p>For every line in this string:
     * <ul>
     *   <li>Strip a leading prefix consisting of blanks or control characters (char <= ' ')</li>
     *   <li>followed by {@code marginChar} from the line.</li>
     * </ul>
     * 
     * <p>This matches Scala's StringLike.stripMargin behavior.</p>
     * 
     * @param text the text to process
     * @param marginChar the margin character (default is '|')
     * @return the text with margins stripped
     */
    private static String stripMargin(String text, char marginChar) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Split by newlines, preserving empty lines
        String[] lines = text.split("\n", -1);
        StringBuilder result = new StringBuilder();
        
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            
            if (lineIndex > 0) {
                result.append("\n");
            }
            
            // Skip leading blanks or control characters (char <= ' ')
            int index = 0;
            int len = line.length();
            while (index < len && line.charAt(index) <= ' ') {
                index++;
            }
            
            // If we found the margin character after skipping blanks, strip it and everything before it
            if (index < len && line.charAt(index) == marginChar) {
                result.append(line.substring(index + 1));
            } else {
                // No margin character found, keep the line as-is
                result.append(line);
            }
        }
        
        return result.toString();
    }
}


