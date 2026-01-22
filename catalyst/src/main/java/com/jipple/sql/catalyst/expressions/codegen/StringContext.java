package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A simple StringContext implementation for Java.
 * This is used to simulate Scala's string interpolation feature.
 */
public class StringContext {
    private final String[] parts;

    public StringContext(String... parts) {
        this.parts = parts;
    }

    public String[] parts() {
        return parts;
    }

    public void checkLengths(Object... args) {
        if (parts.length != args.length + 1) {
            throw new IllegalArgumentException(
                "Wrong number of arguments: expected " + (parts.length - 1) + ", got " + args.length);
        }
    }

    /**
     * Processes escape sequences in a string, similar to Scala's StringContext.treatEscapes.
     * 
     * <p><b>Why do we need to treat escapes in code parts?</b></p>
     * <ul>
     *   <li><b>Code parts</b> (template strings) contain escape sequences that need to be processed
     *       to generate valid Java code strings. For example, <code>"foo\\bar"</code> should become
     *       <code>"foo\bar"</code> in the generated code.</li>
     *   <li><b>String inputs</b> (inserted via ${variable}) are already runtime string objects,
     *       so they should NOT be escaped again to avoid double-escaping.</li>
     * </ul>
     * 
     * <p>This method converts escape sequences like:</p>
     * <ul>
     *   <li><code>\\b</code> → backspace (<code>\b</code>)</li>
     *   <li><code>\\t</code> → tab (<code>\t</code>)</li>
     *   <li><code>\\n</code> → newline (<code>\n</code>)</li>
     *   <li><code>\\f</code> → form feed (<code>\f</code>)</li>
     *   <li><code>\\r</code> → carriage return (<code>\r</code>)</li>
     *   <li><code>\\"</code> → double quote (<code>"</code>)</li>
     *   <li><code>\\'</code> → single quote (<code>'</code>)</li>
     *   <li><code>\\\\</code> → backslash (<code>\</code>)</li>
     * </ul>
     * 
     * <p>The processing is done character by character to ensure correct handling of sequences
     * like <code>\\\\n</code> (which should become <code>\\n</code>, not <code>\n</code>).</p>
     * 
     * @param str the string to process
     * @return the string with escape sequences converted
     * @throws IllegalArgumentException if an invalid escape sequence is encountered
     */
    public static String treatEscapes(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        // Fast path: if there's no backslash, return the string as-is
        int firstBackslash = str.indexOf('\\');
        if (firstBackslash == -1) {
            return str;
        }
        
        StringBuilder result = new StringBuilder(str.length());
        int i = 0;
        while (i < str.length()) {
            if (i < str.length() - 1 && str.charAt(i) == '\\') {
                char next = str.charAt(i + 1);
                switch (next) {
                    case 'b':
                        // Escaped backspace: \b -> backspace
                        result.append('\b');
                        i += 2;
                        break;
                    case 't':
                        // Escaped tab: \t -> tab
                        result.append('\t');
                        i += 2;
                        break;
                    case 'n':
                        // Escaped newline: \n -> newline
                        result.append('\n');
                        i += 2;
                        break;
                    case 'f':
                        // Escaped form feed: \f -> form feed
                        result.append('\f');
                        i += 2;
                        break;
                    case 'r':
                        // Escaped carriage return: \r -> carriage return
                        result.append('\r');
                        i += 2;
                        break;
                    case '"':
                        // Escaped double quote: \" -> "
                        result.append('"');
                        i += 2;
                        break;
                    case '\'':
                        // Escaped single quote: \' -> '
                        result.append('\'');
                        i += 2;
                        break;
                    case '\\':
                        // Escaped backslash: \\ -> \
                        result.append('\\');
                        i += 2;
                        break;
                    default:
                        // Invalid escape sequence - throw exception (matching Scala's strict behavior)
                        throw new IllegalArgumentException(
                            String.format("Invalid escape sequence '\\%c' at position %d in string: %s", 
                                next, i, str));
                }
            } else {
                result.append(str.charAt(i));
                i++;
            }
        }
        return result.toString();
    }
}

