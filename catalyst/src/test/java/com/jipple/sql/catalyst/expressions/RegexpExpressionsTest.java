package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.GenerateSafeProjection;
import com.jipple.sql.catalyst.expressions.regexp.*;
import com.jipple.sql.types.ArrayType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jipple.sql.types.DataTypes.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegexpExpressionsTest extends ExpressionEvalHelper {

    /**
     * Check if a given expression evaluates to an expected output, in case the input is
     * a literal and in case the input is in the form of a row.
     * @tparam A type of input
     * @param mkExpr the expression to test for a given input
     * @param input value that will be used to create the expression, as literal and in the form
     *        of a row
     * @param expected the expected output of the expression
     */
    public void checkLiteralRow(Function<Expression, Expression> mkExpr, Object input, Object expected) {
        checkEvaluation(mkExpr.apply(Literal.of(input)), expected); // check literal input

        checkEvaluation(mkExpr.apply(new BoundReference(0, STRING)), expected, createRow(input)); // check row input
    }

    @Test
    public void testLikePattern() {
        // null handling
        checkLiteralRow(expr -> new Like(Literal.create(null, STRING), expr), "a", null);
        checkEvaluation(new Like(Literal.of("a"), Literal.create(null, STRING)), null);
        checkEvaluation(new Like(Literal.create(null, STRING), Literal.create(null, STRING)), null);
        checkEvaluation(new Like(Literal.of("a"), NonFoldableLiteral.create("a", STRING)), true);
        checkEvaluation(new Like(Literal.of("a"), NonFoldableLiteral.create(null, STRING)), null);
        checkEvaluation(new Like(Literal.create(null, STRING), NonFoldableLiteral.create("a", STRING)), null);
        checkEvaluation(new Like(Literal.create(null, STRING), NonFoldableLiteral.create(null, STRING)), null);

        // simple patterns
        checkLiteralRow(expr -> new Like(Literal.of("abdef"), expr), "abdef", true);
        checkLiteralRow(expr -> new Like(Literal.of("a_%b"), expr), "a\\__b", true);
        checkLiteralRow(expr -> new Like(Literal.of("addb"), expr), "a_%b", true);
        checkLiteralRow(expr -> new Like(Literal.of("addb"), expr), "a\\__b", false);
        checkLiteralRow(expr -> new Like(Literal.of("addb"), expr), "a%\\%b", false);
        checkLiteralRow(expr -> new Like(Literal.of("a_%b"), expr), "a%\\%b", true);
        checkLiteralRow(expr -> new Like(Literal.of("addb"), expr), "a%", true);
        checkLiteralRow(expr -> new Like(Literal.of("addb"), expr), "**", false);
        checkLiteralRow(expr -> new Like(Literal.of("abc"), expr), "a%", true);
        checkLiteralRow(expr -> new Like(Literal.of("abc"), expr), "b%", false);
        checkLiteralRow(expr -> new Like(Literal.of("abc"), expr), "bc%", false);
        checkLiteralRow(expr -> new Like(Literal.of("a\nb"), expr), "a_b", true);
        checkLiteralRow(expr -> new Like(Literal.of("ab"), expr), "a%b", true);
        checkLiteralRow(expr -> new Like(Literal.of("a\nb"), expr), "a%b", true);

        // empty input
        checkLiteralRow(expr -> new Like(Literal.of(""), expr), "", true);
        checkLiteralRow(expr -> new Like(Literal.of("a"), expr), "", false);
        checkLiteralRow(expr -> new Like(Literal.of(""), expr), "a", false);

        // double-escaping backslash
        checkLiteralRow(expr -> new Like(Literal.of("\\\\\\\\"), expr), "%\\\\%", true);
        checkLiteralRow(expr -> new Like(Literal.of("%%"), expr), "%%", true);
        checkLiteralRow(expr -> new Like(Literal.of("\\__"), expr), "\\\\\\__", true);
        checkLiteralRow(expr -> new Like(Literal.of("\\\\\\__"), expr), "%\\\\%\\%", false);
        checkLiteralRow(expr -> new Like(Literal.of("_\\\\\\%"), expr), "%\\\\", false);

        // unicode
        checkLiteralRow(expr -> new Like(Literal.of("a\u20ACa"), expr), "_\u20AC_", true);
        checkLiteralRow(expr -> new Like(Literal.of("a€a"), expr), "_€_", true);
        checkLiteralRow(expr -> new Like(Literal.of("a€a"), expr), "_\u20AC_", true);
        checkLiteralRow(expr -> new Like(Literal.of("a\u20ACa"), expr), "_€_", true);

        // case
        checkLiteralRow(expr -> new Like(Literal.of("A"), expr), "a%", false);
        checkLiteralRow(expr -> new Like(Literal.of("a"), expr), "A%", false);
        checkLiteralRow(expr -> new Like(Literal.of("AaA"), expr), "_a_", true);

        // example
        checkLiteralRow(expr -> new Like(Literal.of("%SystemDrive%\\Users\\John"), expr), "\\%SystemDrive\\%\\\\Users%", true);
    }

    @Test
    public void testRlikeRegularExpression() {
        // null handling
        checkLiteralRow(expr -> new RLike(Literal.create(null, STRING), expr), "abdef", null);
        checkEvaluation(new RLike(Literal.of("abdef"), Literal.create(null, STRING)), null);
        checkEvaluation(new RLike(Literal.create(null, STRING), Literal.create(null, STRING)), null);
        checkEvaluation(new RLike(Literal.of("abdef"), NonFoldableLiteral.create("abdef", STRING)), true);
        checkEvaluation(new RLike(Literal.of("abdef"), NonFoldableLiteral.create(null, STRING)), null);
        checkEvaluation(new RLike(Literal.create(null, STRING), NonFoldableLiteral.create("abdef", STRING)), null);
        checkEvaluation(new RLike(Literal.create(null, STRING), NonFoldableLiteral.create(null, STRING)), null);

        // regular expressions
        checkLiteralRow(expr -> new RLike(Literal.of("abdef"), expr), "abdef", true);
        checkLiteralRow(expr -> new RLike(Literal.of("abbbbc"), expr), "a.*c", true);

        checkLiteralRow(expr -> new RLike(Literal.of("fofo"), expr), "^fo", true);
        checkLiteralRow(expr -> new RLike(Literal.of("fo\no"), expr), "^fo\no$", true);
        checkLiteralRow(expr -> new RLike(Literal.of("Bn"), expr), "^Ba*n", true);
        checkLiteralRow(expr -> new RLike(Literal.of("afofo"), expr), "fo", true);
        checkLiteralRow(expr -> new RLike(Literal.of("afofo"), expr), "^fo", false);
        checkLiteralRow(expr -> new RLike(Literal.of("Baan"), expr), "^Ba?n", false);
        checkLiteralRow(expr -> new RLike(Literal.of("axe"), expr), "pi|apa", false);
        checkLiteralRow(expr -> new RLike(Literal.of("pip"), expr), "^(pi)*$", false);

        checkLiteralRow(expr -> new RLike(Literal.of("abc"), expr), "^ab", true);
        checkLiteralRow(expr -> new RLike(Literal.of("abc"), expr), "^bc", false);
        checkLiteralRow(expr -> new RLike(Literal.of("abc"), expr), "^ab", true);
        checkLiteralRow(expr -> new RLike(Literal.of("abc"), expr), "^bc", false);
    }

    @Test
    public void testRegexReplace() {
        InternalRow row1 = createRow("100-200", "(\\d+)", "num");
        InternalRow row2 = createRow("100-200", "(\\d+)", "###");
        InternalRow row3 = createRow("100-200", "(-)", "###");
        InternalRow row4 = createRow(null, "(\\d+)", "###");
        InternalRow row5 = createRow("100-200", null, "###");
        InternalRow row6 = createRow("100-200", "(-)", null);
        InternalRow row7 = createRow("", "^$", "<empty string>");

        BoundReference s = new BoundReference(0, STRING);
        BoundReference p = new BoundReference(1, STRING);
        BoundReference r = new BoundReference(2, STRING);

        Expression expr = new RegExpReplace(s, p, r);
        checkEvaluation(expr, "num-num", row1);
        checkEvaluation(expr, "###-###", row2);
        checkEvaluation(expr, "100###200", row3);
        checkEvaluation(expr, null, row4);
        checkEvaluation(expr, null, row5);
        checkEvaluation(expr, null, row6);
        checkEvaluation(expr, "<empty string>", row7);

        // test position
        Expression exprWithPos = new RegExpReplace(s, p, r, Literal.of(4));
        checkEvaluation(exprWithPos, "100-num", row1);
        checkEvaluation(exprWithPos, "100-###", row2);
        checkEvaluation(exprWithPos, "100###200", row3);
        checkEvaluation(exprWithPos, null, row4);
        checkEvaluation(exprWithPos, null, row5);
        checkEvaluation(exprWithPos, null, row6);
        checkEvaluation(exprWithPos, "", row7);

        Expression exprWithLargePos = new RegExpReplace(s, p, r, Literal.of(7));
        checkEvaluation(exprWithLargePos, "100-20num", row1);
        checkEvaluation(exprWithLargePos, "100-20###", row2);

        Expression exprWithExceedLength = new RegExpReplace(s, p, r, Literal.of(8));
        checkEvaluation(exprWithExceedLength, "100-200", row1);
        checkEvaluation(exprWithExceedLength, "100-200", row2);

        Expression nonNullExpr = new RegExpReplace(Literal.of("100-200"), Literal.of("(\\d+)"), Literal.of("num"));
        checkEvaluation(nonNullExpr, "num-num", row1);

        // Test escaping of arguments
        GenerateSafeProjection.get().generate(List.of(new RegExpReplace(Literal.of("\"quote"), Literal.of("\"quote"), Literal.of("\"quote"))));
    }

    @Test
    public void testRegexReplaceGlobalVariables() {
        // SPARK-22570: RegExpReplace should not create a lot of global variables
        var ctx = new CodegenContext();
        new RegExpReplace(Literal.of("100"), Literal.of("(\\d+)"), Literal.of("num")).genCode(ctx);
        // four global variables (lastRegex, pattern, lastReplacement, and lastReplacementInUTF8)
        // are always required, which are allocated in type-based global array
        assertTrue(ctx.inlinedMutableStates.size() == 0);
        assertTrue(ctx.mutableStateInitCode.size() == 4);
    }

    @Test
    public void testRegexExtract() {
        InternalRow row1 = createRow("100-200", "(\\d+)-(\\d+)", 1);
        InternalRow row2 = createRow("100-200", "(\\d+)-(\\d+)", 2);
        InternalRow row3 = createRow("100-200", "(\\d+).*", 1);
        InternalRow row4 = createRow("100-200", "([a-z])", 1);
        InternalRow row5 = createRow(null, "([a-z])", 1);
        InternalRow row6 = createRow("100-200", null, 1);
        InternalRow row7 = createRow("100-200", "([a-z])", null);

        BoundReference s = new BoundReference(0, STRING);
        BoundReference p = new BoundReference(1, STRING);
        BoundReference r = new BoundReference(2, INTEGER);

        Expression expr = new RegExpExtract(s, p, r);
        checkEvaluation(expr, "100", row1);
        checkEvaluation(expr, "200", row2);
        checkEvaluation(expr, "100", row3);
        checkEvaluation(expr, "", row4); // will not match anything, empty string get
        checkEvaluation(expr, null, row5);
        checkEvaluation(expr, null, row6);
        checkEvaluation(expr, null, row7);

        Expression expr1 = new RegExpExtract(s, p);
        checkEvaluation(expr1, "100", row1);

        Expression nonNullExpr = new RegExpExtract(Literal.of("100-200"), Literal.of("(\\d+)-(\\d+)"), Literal.of(1));
        checkEvaluation(nonNullExpr, "100", row1);

        // invalid group index
        InternalRow row8 = createRow("100-200", "(\\d+)-(\\d+)", 3);
        InternalRow row9 = createRow("100-200", "(\\d+).*", 2);
        InternalRow row10 = createRow("100-200", "\\d+", 1);
        InternalRow row11 = createRow("100-200", "(\\d+)-(\\d+)", -1);
        InternalRow row12 = createRow("100-200", "\\d+", -1);

        // Test escaping of arguments
        GenerateSafeProjection.get().generate(List.of(new RegExpExtract(Literal.of("\"quote"), Literal.of("\"quote"), Literal.of(1))));
    }


    /*

    */

}
