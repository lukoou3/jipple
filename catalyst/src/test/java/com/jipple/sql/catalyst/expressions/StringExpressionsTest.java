package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.collection.Concat;
import com.jipple.sql.catalyst.expressions.string.ConcatWs;
import com.jipple.sql.types.ArrayType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jipple.sql.types.DataTypes.STRING;

public class StringExpressionsTest extends ExpressionEvalHelper {

    private void testConcatEval(String ... inputs) {
        boolean hasNull = Arrays.stream(inputs).anyMatch(input -> input == null);
        String expected = hasNull ? null : String.join("", inputs);
        checkEvaluation(new Concat(Arrays.stream(inputs).map(s  -> Literal.create(s, STRING)).collect(Collectors.toList())) , expected);
        checkEvaluation(new Concat(IntStream.range(0, inputs.length).mapToObj(i  -> new BoundReference(i, STRING)).collect(Collectors.toList())) , expected, createRow(inputs));
    }

    @Test
    public void testConcat() {
        testConcatEval();
        testConcatEval((String) null);
        testConcatEval("");
        testConcatEval("ab");
        testConcatEval("a", "b");
        testConcatEval("a", "b", "C");
        testConcatEval("a", null, "C");
        testConcatEval("a", null, null);
        testConcatEval(null, null, null);

        testConcatEval("数据", null, "砖头");
        testConcatEval("a", "b" , "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
    }

    private void testConcatWs(String expected, String sep, Object ... inputs) {
        List<Expression> children = new ArrayList<>();
        children.add(Literal.create(sep, STRING));
        for (Object input : inputs) {
            if (input == null) {
                children.add(Literal.create(null, STRING));
            } else if (input instanceof List l) {
                children.add(Literal.create(l, new ArrayType(STRING)));
            } else if (input instanceof String) {
                children.add(Literal.create(input, STRING));
            } else {
                throw new IllegalArgumentException(String.valueOf(input));
            }
        }
        checkEvaluation(new ConcatWs(children), expected);
        children = new ArrayList<>();
        children.add(Literal.create(sep, STRING));
        for (int i = 0; i < inputs.length; i++) {
            Object input = inputs[i];
            if (input == null || input instanceof String) {
                children.add(new BoundReference(i, STRING));
            } else if (input instanceof List) {
                children.add(new BoundReference(i, new ArrayType(STRING)));
            } else {
                throw new IllegalArgumentException(String.valueOf(input));
            }
        }
        checkEvaluation(new ConcatWs(children), expected, createRow(inputs));
    }

    @Test
    public void testConcatWs() {
        testConcatWs(null, null);
        testConcatWs(null, null, "a", "b");
        testConcatWs("", "");
        testConcatWs("ab", "哈哈", "ab");
        testConcatWs("a哈哈b", "哈哈", "a", "b");
        testConcatWs("a哈哈b", "哈哈", "a", null, "b");
        testConcatWs("a哈哈b哈哈c", "哈哈", null, "a", null, "b", "c");

        testConcatWs("ab", "哈哈", List.of("ab"));
        testConcatWs("a哈哈b", "哈哈", List.of("a", "b"));
        testConcatWs("a哈哈b哈哈c哈哈d", "哈哈", Arrays.asList("a", null, "b"), null, "c", Arrays.asList(null, "d"));
        testConcatWs("a哈哈b哈哈c", "哈哈", Arrays.asList("a", null, "b"), null, "c", List.of());
        testConcatWs("a哈哈b哈哈c", "哈哈", Arrays.asList("a", null, "b"), null, "c", Arrays.asList((String)null));

        StringBuilder sb = new StringBuilder();
        Object[] inputs = new Object[30];
        for (int i = 0; i < inputs.length; i++) {
            if (i != 0) {
                sb.append("_");
            }
            sb.append(i);
            inputs[i] = String.valueOf(i);
        }
        testConcatWs(sb.toString(), "_", inputs);
    }

/*
  test("StringComparison") {
    val row = create_row("abc", null)
    val c1 = $"a".string.at(0)
    val c2 = $"a".string.at(1)

    checkEvaluation(c1 contains "b", true, row)
    checkEvaluation(c1 contains "x", false, row)
    checkEvaluation(c2 contains "b", null, row)
    checkEvaluation(c1 contains Literal.create(null, StringType), null, row)

    checkEvaluation(c1 startsWith "a", true, row)
    checkEvaluation(c1 startsWith "b", false, row)
    checkEvaluation(c2 startsWith "a", null, row)
    checkEvaluation(c1 startsWith Literal.create(null, StringType), null, row)

    checkEvaluation(c1 endsWith "c", true, row)
    checkEvaluation(c1 endsWith "b", false, row)
    checkEvaluation(c2 endsWith "b", null, row)
    checkEvaluation(c1 endsWith Literal.create(null, StringType), null, row)

    // Test escaping of arguments
    GenerateUnsafeProjection.generate(Contains(Literal("\"quote"), Literal("\"quote")) :: Nil)
    GenerateUnsafeProjection.generate(EndsWith(Literal("\"quote"), Literal("\"quote")) :: Nil)
    GenerateUnsafeProjection.generate(StartsWith(Literal("\"quote"), Literal("\"quote")) :: Nil)
  }
* */
}
