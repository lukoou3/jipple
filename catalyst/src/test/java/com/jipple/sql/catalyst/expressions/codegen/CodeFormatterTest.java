package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.util.JippleStringUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test suite for CodeFormatter functionality.
 */
public class CodeFormatterTest {

    private void testCase(
            String name,
            String input,
            Map<String, String> comment,
            int maxLines,
            String expected) {
        CodeAndComment sourceCode = new CodeAndComment(input.trim(), comment);
        String actual = CodeFormatter.format(sourceCode, maxLines).trim();
        String expectedTrimmed = expected.trim();
        if (!actual.equals(expectedTrimmed)) {
            String diff = String.join("\n", JippleStringUtils.sideBySide(actual, expectedTrimmed));
            fail("""
                    == FAIL: Formatted code doesn't match ===
                    %s
                    """.formatted(diff));
        }
    }

    private void testCase(String name, String input, String expected) {
        testCase(name, input, Collections.emptyMap(), -1, expected);
    }

    private void testCase(String name, String input, int maxLines, String expected) {
        testCase(name, input, Collections.emptyMap(), maxLines, expected);
    }

    @Test
    public void testRemovingOverlappingComments() {
        CodeAndComment code = new CodeAndComment(
                """
                        /*project_c4*/
                        /*project_c3*/
                        /*project_c2*/
                        """.stripIndent(),
                Map.of(
                        "project_c4", "// (((input[0, bigint, false] + 1) + 2) + 3))",
                        "project_c3", "// ((input[0, bigint, false] + 1) + 2)",
                        "project_c2", "// (input[0, bigint, false] + 1)"
                ));

        CodeAndComment reducedCode = CodeFormatter.stripOverlappingComments(code);
        assertEquals("/*project_c4*/", reducedCode.body);
    }

    @Test
    public void testRemovingExtraNewLinesAndComments() {
        String code = """
                /*
                 * multi
                 * line
                 * comments
                 */

                public function() {
                /*comment*/
                  /*comment_with_space*/
                code_body
                //comment
                code_body
                  //comment_with_space

                code_body
                }
                """.stripIndent();

        String reducedCode = CodeFormatter.stripExtraNewLinesAndComments(code);
        String expected = """
                public function() {
                code_body
                code_body
                code_body
                }
                """.stripIndent();
        assertEquals(expected.trim(), reducedCode.trim());
    }

    @Test
    public void testBasicExample() {
        testCase("basic example",
                """
                        class A {
                        blahblah;
                        }
                        """.stripIndent(),
                """
                        /* 001 */ class A {
                        /* 002 */   blahblah;
                        /* 003 */ }
                        """.stripIndent());
    }

    @Test
    public void testNestedExample() {
        testCase("nested example",
                """
                        class A {
                         if (c) {
                        duh;
                        }
                        }
                        """.stripIndent(),
                """
                        /* 001 */ class A {
                        /* 002 */   if (c) {
                        /* 003 */     duh;
                        /* 004 */   }
                        /* 005 */ }
                        """.stripIndent());
    }

    @Test
    public void testSingleLine() {
        testCase("single line",
                """
                        class A {
                         if (c) {duh;}
                        }
                        """.stripIndent(),
                """
                        /* 001 */ class A {
                        /* 002 */   if (c) {duh;}
                        /* 003 */ }
                        """.stripIndent());
    }

    @Test
    public void testIfElseOnSameLine() {
        testCase("if else on the same line",
                """
                        class A {
                         if (c) {duh;} else {boo;}
                        }
                        """.stripIndent(),
                """
                        /* 001 */ class A {
                        /* 002 */   if (c) {duh;} else {boo;}
                        /* 003 */ }
                        """.stripIndent());
    }

    @Test
    public void testFunctionCalls() {
        testCase("function calls",
                """
                        foo(
                        a,
                        b,
                        c)
                        """.stripIndent(),
                """
                        /* 001 */ foo(
                        /* 002 */   a,
                        /* 003 */   b,
                        /* 004 */   c)
                        """.stripIndent());
    }

    @Test
    public void testFunctionCallsWithMaxLinesZero() {
        testCase("function calls with maxLines=0",
                """
                        foo(
                        a,
                        b,
                        c)
                        """.stripIndent(),
                0,
                """
                        /* 001 */ [truncated to 0 lines (total lines is 4)]
                        """.stripIndent());
    }

    @Test
    public void testFunctionCallsWithMaxLinesTwo() {
        testCase("function calls with maxLines=2",
                """
                        foo(
                        a,
                        b,
                        c)
                        """.stripIndent(),
                2,
                """
                        /* 001 */ foo(
                        /* 002 */   a,
                        /* 003 */   [truncated to 2 lines (total lines is 4)]
                        """.stripIndent());
    }

    @Test
    public void testSingleLineComments() {
        testCase("single line comments",
                """
                        // This is a comment about class A { { { ( (
                        class A {
                        class body;
                        }
                        """.stripIndent(),
                """
                        /* 001 */ // This is a comment about class A { { { ( (
                        /* 002 */ class A {
                        /* 003 */   class body;
                        /* 004 */ }
                        """.stripIndent());
    }

    @Test
    public void testSingleLineCommentsWithBlockTokens() {
        testCase("single line comments /* */ ",
                """
                        /** This is a comment about class A { { { ( ( */
                        class A {
                        class body;
                        }
                        """.stripIndent(),
                """
                        /* 001 */ /** This is a comment about class A { { { ( ( */
                        /* 002 */ class A {
                        /* 003 */   class body;
                        /* 004 */ }
                        """.stripIndent());
    }

    @Test
    public void testMultiLineComments() {
        testCase("multi-line comments",
                """
                        /* This is a comment about
                        class A {
                        class body; ...*/
                        class A {
                        class body;
                        }
                        """.stripIndent(),
                """
                        /* 001 */ /* This is a comment about
                        /* 002 */ class A {
                        /* 003 */   class body; ...*/
                        /* 004 */ class A {
                        /* 005 */   class body;
                        /* 006 */ }
                        """.stripIndent());
    }

    @Test
    public void testReduceEmptyLines() {
        String reducedInput = CodeFormatter.stripExtraNewLines(
                """
                        class A {


                         /*
                          * multi
                          * line
                          * comment
                          */

                         class body;


                         if (c) {duh;}
                         else {boo;}
                        }
                        """.stripIndent().trim());
        testCase("reduce empty lines",
                reducedInput,
                """
                        /* 001 */ class A {
                        /* 002 */   /*
                        /* 003 */    * multi
                        /* 004 */    * line
                        /* 005 */    * comment
                        /* 006 */    */
                        /* 007 */   class body;
                        /* 008 */
                        /* 009 */   if (c) {duh;}
                        /* 010 */   else {boo;}
                        /* 011 */ }
                        """.stripIndent());
    }

    @Test
    public void testCommentPlaceHolder() {
        testCase("comment place holder",
                """
                        /*c1*/
                        class A
                        /*c2*/
                        class B
                        /*c1*//*c2*/
                        """.stripIndent(),
                Map.of("c1", "/*abc*/", "c2", "/*xyz*/"),
                -1,
                """
                        /* 001 */ /*abc*/
                        /* 002 */ class A
                        /* 003 */ /*xyz*/
                        /* 004 */ class B
                        /* 005 */ /*abc*//*xyz*/
                        """.stripIndent());
    }
}
