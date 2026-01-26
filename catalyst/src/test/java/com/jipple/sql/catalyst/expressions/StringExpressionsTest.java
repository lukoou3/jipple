package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.GenerateSafeProjection;
import com.jipple.sql.catalyst.expressions.collection.Concat;
import com.jipple.sql.catalyst.expressions.string.*;
import com.jipple.sql.types.ArrayType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jipple.sql.types.DataTypes.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void testStringComparison() {
        InternalRow row = createRow("abc", null);
        BoundReference c1 = new BoundReference(0, STRING);
        BoundReference c2 = new BoundReference(1, STRING);

        checkEvaluation(new Contains(c1, Literal.of("b")), true, row);
        checkEvaluation(new Contains(c1, Literal.of("x")), false, row);
        checkEvaluation(new Contains(c2, Literal.of("b")), null, row);
        checkEvaluation(new Contains(c1, Literal.create(null, STRING)), null, row);

        checkEvaluation(new StartsWith(c1, Literal.of("a")), true, row);
        checkEvaluation(new StartsWith(c1, Literal.of("b")), false, row);
        checkEvaluation(new StartsWith(c2, Literal.of("a")), null, row);
        checkEvaluation(new StartsWith(c1, Literal.create(null, STRING)), null, row);

        checkEvaluation(new EndsWith(c1, Literal.of("c")), true, row);
        checkEvaluation(new EndsWith(c1, Literal.of("b")), false, row);
        checkEvaluation(new EndsWith(c2, Literal.of("b")), null, row);
        checkEvaluation(new EndsWith(c1, Literal.create(null, STRING)), null, row);

        // Test escaping of arguments
        GenerateSafeProjection.get().generate(List.of(new Contains(Literal.of("\"quote"), Literal.of("\"quote"))));
        GenerateSafeProjection.get().generate(List.of(new EndsWith(Literal.of("\"quote"), Literal.of("\"quote"))));
        GenerateSafeProjection.get().generate(List.of(new StartsWith(Literal.of("\"quote"), Literal.of("\"quote"))));
    }

    @Test
    public void testSubstring() {
        InternalRow row = createRow("example", "example".getBytes());
        BoundReference s = new BoundReference(0, STRING);
        BoundReference sBytes = new BoundReference(1, BINARY);

        // substring from zero position with less-than-full length
        checkEvaluation(new Substring(s, Literal.create(0, INTEGER), Literal.create(2, INTEGER)), "ex", row);
        checkEvaluation(new Substring(s, Literal.create(1, INTEGER), Literal.create(2, INTEGER)), "ex", row);

        // substring from zero position with full length
        checkEvaluation(new Substring(s, Literal.create(0, INTEGER), Literal.create(7, INTEGER)), "example", row);
        checkEvaluation(new Substring(s, Literal.create(1, INTEGER), Literal.create(7, INTEGER)), "example", row);

        // substring from zero position with greater-than-full length
        checkEvaluation(new Substring(s, Literal.create(0, INTEGER), Literal.create(100, INTEGER)), "example", row);
        checkEvaluation(new Substring(s, Literal.create(1, INTEGER), Literal.create(100, INTEGER)), "example", row);

        // substring from nonzero position with less-than-full length
        checkEvaluation(new Substring(s, Literal.create(2, INTEGER), Literal.create(2, INTEGER)), "xa", row);

        // substring from nonzero position with full length
        checkEvaluation(new Substring(s, Literal.create(2, INTEGER), Literal.create(6, INTEGER)), "xample", row);

        // substring from nonzero position with greater-than-full length
        checkEvaluation(new Substring(s, Literal.create(2, INTEGER), Literal.create(100, INTEGER)), "xample", row);

        // zero-length substring (within string bounds)
        checkEvaluation(new Substring(s, Literal.create(0, INTEGER), Literal.create(0, INTEGER)), "", row);

        // zero-length substring (beyond string bounds)
        checkEvaluation(new Substring(s, Literal.create(100, INTEGER), Literal.create(4, INTEGER)), "", row);

        // substring(null, _, _) -> null
        checkEvaluation(new Substring(new BoundReference(0, STRING), Literal.create(100, INTEGER), Literal.create(4, INTEGER)), null, createRow((String)null));

        // substring(_, null, _) -> null
        checkEvaluation(new Substring(s, Literal.create(null, INTEGER), Literal.create(4, INTEGER)), null, row);

        // substring(_, _, null) -> null
        checkEvaluation(new Substring(s, Literal.create(100, INTEGER), Literal.create(null, INTEGER)), null, row);

        // 2-arg substring from zero position
        checkEvaluation(new Substring(s, Literal.create(0, INTEGER), Literal.create(Integer.MAX_VALUE, INTEGER)), "example", row);
        checkEvaluation(new Substring(s, Literal.create(1, INTEGER), Literal.create(Integer.MAX_VALUE, INTEGER)), "example", row);

        // 2-arg substring from nonzero position
        checkEvaluation(new Substring(s, Literal.create(2, INTEGER), Literal.create(Integer.MAX_VALUE, INTEGER)), "xample", row);

        // Substring with from negative position with negative length
        checkEvaluation(new Substring(s, Literal.create(-1207959552, INTEGER), Literal.create(-1207959552, INTEGER)), "", row);

        BoundReference sNotNull = new BoundReference(0, STRING, false); // assuming non-null string reference

        assertTrue(new Substring(s, Literal.create(0, INTEGER), Literal.create(2, INTEGER)).nullable());
        assertFalse(new Substring(sNotNull, Literal.create(0, INTEGER), Literal.create(2, INTEGER)).nullable());
        assertTrue(new Substring(sNotNull, Literal.create(null, INTEGER), Literal.create(2, INTEGER)).nullable());
        assertTrue(new Substring(sNotNull, Literal.create(0, INTEGER), Literal.create(null, INTEGER)).nullable());

        // Testing byte array substrings
        byte[] bytes = new byte[]{1, 2, 3, 4};
        InternalRow bytesRow = createRow("example", bytes);

        checkEvaluation(new Substring(sBytes, Literal.create(0, INTEGER), Literal.create(2, INTEGER)), new byte[]{1, 2}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(1, INTEGER), Literal.create(2, INTEGER)), new byte[]{1, 2}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(2, INTEGER), Literal.create(2, INTEGER)), new byte[]{2, 3}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(3, INTEGER), Literal.create(2, INTEGER)), new byte[]{3, 4}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(4, INTEGER), Literal.create(2, INTEGER)), new byte[]{4}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(8, INTEGER), Literal.create(2, INTEGER)), new byte[]{}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(-1, INTEGER), Literal.create(2, INTEGER)), new byte[]{4}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(-2, INTEGER), Literal.create(2, INTEGER)), new byte[]{3, 4}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(-3, INTEGER), Literal.create(2, INTEGER)), new byte[]{2, 3}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(-4, INTEGER), Literal.create(2, INTEGER)), new byte[]{1, 2}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(-5, INTEGER), Literal.create(2, INTEGER)), new byte[]{1}, bytesRow);
        checkEvaluation(new Substring(sBytes, Literal.create(-8, INTEGER), Literal.create(2, INTEGER)), new byte[]{}, bytesRow);
    }

    @Test
    public void testReplace() {
        checkEvaluation(new StringReplace(Literal.of("replace"), Literal.of("pl"), Literal.of("123")), "re123ace");
        checkEvaluation(new StringReplace(Literal.of("replace"), Literal.of("pl"), Literal.of("")), "reace");
        checkEvaluation(new StringReplace(Literal.of("replace"), Literal.of(""), Literal.of("123")), "replace");
        checkEvaluation(new StringReplace(Literal.create(null, STRING), Literal.of("pl"), Literal.of("123")), null);
        checkEvaluation(new StringReplace(Literal.of("replace"), Literal.create(null, STRING), Literal.of("123")), null);
        checkEvaluation(new StringReplace(Literal.of("replace"), Literal.of("pl"), Literal.create(null, STRING)), null);
        // test for multiple replace
        checkEvaluation(new StringReplace(Literal.of("abcabc"), Literal.of("b"), Literal.of("12")), "a12ca12c");
        checkEvaluation(new StringReplace(Literal.of("abcdabcd"), Literal.of("bc"), Literal.of("")), "adad");
        // non ascii characters are not allowed in the source code, so we disable the scalastyle.
        checkEvaluation(new StringReplace(Literal.of("花花世界"), Literal.of("花世"), Literal.of("ab")), "花ab界");
    }

    @Test
    public void testTrim() {
        BoundReference s = new BoundReference(0, STRING);

        checkEvaluation(new StringTrim(Literal.of(" aa  ")), "aa", createRow(" abdef "));
        checkEvaluation(new StringTrim(Literal.of("aa"), Literal.of("a")), "", createRow(" abdef "));
        checkEvaluation(new StringTrim(Literal.of(" aabbtrimccc"), Literal.of("ab cd")), "trim", createRow("bdef"));
        checkEvaluation(new StringTrim(Literal.of("a<a >@>.,>"), Literal.of("a.,@<>")), " ", createRow(" abdef "));
        checkEvaluation(new StringTrim(s), "abdef", createRow(" abdef "));
        checkEvaluation(new StringTrim(s, Literal.of("abd")), "ef", createRow("abdefa"));
        checkEvaluation(new StringTrim(s, Literal.of("a")), "bdef", createRow("aaabdefaaaa"));
        checkEvaluation(new StringTrim(s, Literal.of("SLSQ")), "park", createRow("SSparkSQLS"));

        // non ascii characters are not allowed in the source code, so we disable the scalastyle.
        checkEvaluation(new StringTrim(s), "花花世界", createRow("  花花世界 "));
        checkEvaluation(new StringTrim(s, Literal.of("花世界")), "", createRow("花花世界花花"));
        checkEvaluation(new StringTrim(s, Literal.of("花 ")), "世界", createRow(" 花花世界花花"));
        checkEvaluation(new StringTrim(s, Literal.of("花 ")), "世界", createRow(" 花 花 世界 花 花 "));
        checkEvaluation(new StringTrim(s, Literal.of("a花世")), "界", createRow("aa花花世界花花aa"));
        checkEvaluation(new StringTrim(s, Literal.of("a@#( )")), "花花世界花花", createRow("aa()花花世界花花@ #"));
        checkEvaluation(new StringTrim(Literal.of("花trim"), Literal.of("花 ")), "trim", createRow(" abdef "));

        checkEvaluation(new StringTrim(Literal.of("a"), Literal.create(null, STRING)), null);
        checkEvaluation(new StringTrim(Literal.create(null, STRING), Literal.of("a")), null);

        // Test escaping of arguments
        GenerateSafeProjection.get().generate(List.of(new StringTrim(Literal.of("\"quote"), Literal.of("\"quote"))));

        checkEvaluation(new StringTrim(Literal.of("yxTomxx"), Literal.of("xyz")), "Tom");
        checkEvaluation(new StringTrim(Literal.of("xxxbarxxx"), Literal.of("x")), "bar");
    }

    @Test
    public void testLTrim() {
        BoundReference s = new BoundReference(0, STRING);

        checkEvaluation(new StringTrimLeft(Literal.of(" aa  ")), "aa  ", createRow(" abdef "));
        checkEvaluation(new StringTrimLeft(Literal.of("aa"), Literal.of("a")), "", createRow(" abdef "));
        checkEvaluation(new StringTrimLeft(Literal.of("aa "), Literal.of("a ")), "", createRow(" abdef "));
        checkEvaluation(new StringTrimLeft(Literal.of("aabbcaaaa"), Literal.of("ab")), "caaaa", createRow(" abdef "));
        checkEvaluation(new StringTrimLeft(s), "abdef ", createRow(" abdef "));
        checkEvaluation(new StringTrimLeft(s, Literal.of("a")), "bdefa", createRow("abdefa"));
        checkEvaluation(new StringTrimLeft(s, Literal.of("a ")), "bdefaaaa", createRow(" aaabdefaaaa"));
        checkEvaluation(new StringTrimLeft(s, Literal.of("Spk")), "arkSQLS", createRow("SSparkSQLS"));

        // non ascii characters are not allowed in the source code, so we disable the scalastyle.
        checkEvaluation(new StringTrimLeft(s), "花花世界 ", createRow("  花花世界 "));
        checkEvaluation(new StringTrimLeft(s, Literal.of("花")), "世界花花", createRow("花花世界花花"));
        checkEvaluation(new StringTrimLeft(s, Literal.of("花 世")), "界花花", createRow(" 花花世界花花"));
        checkEvaluation(new StringTrimLeft(s, Literal.of("花")), "a花花世界花花 ", createRow("a花花世界花花 "));
        checkEvaluation(new StringTrimLeft(s, Literal.of("a花界")), "世界花花aa", createRow("aa花花世界花花aa"));
        checkEvaluation(new StringTrimLeft(s, Literal.of("a世界")), "花花世界花花", createRow("花花世界花花"));

        checkEvaluation(new StringTrimLeft(Literal.create(null, STRING), Literal.of("a")), null);
        checkEvaluation(new StringTrimLeft(Literal.of("a"), Literal.create(null, STRING)), null);

        // Test escaping of arguments
        GenerateSafeProjection.get().generate(List.of(new StringTrimLeft(Literal.of("\"quote"), Literal.of("\"quote"))));

        checkEvaluation(new StringTrimLeft(Literal.of("zzzytest"), Literal.of("xyz")), "test");
        checkEvaluation(new StringTrimLeft(Literal.of("zzzytestxyz"), Literal.of("xyz")), "testxyz");
        checkEvaluation(new StringTrimLeft(Literal.of("xyxXxyLAST WORD"), Literal.of("xy")), "XxyLAST WORD");
    }

    @Test
    public void testRTrim() {
        BoundReference s = new BoundReference(0, STRING);

        checkEvaluation(new StringTrimRight(Literal.of(" aa  ")), " aa", createRow(" abdef "));
        checkEvaluation(new StringTrimRight(Literal.of("a"), Literal.of("a")), "", createRow(" abdef "));
        checkEvaluation(new StringTrimRight(Literal.of("ab"), Literal.of("ab")), "", createRow(" abdef "));
        checkEvaluation(new StringTrimRight(Literal.of("aabbaaaa %"), Literal.of("a %")), "aabb", createRow("def"));
        checkEvaluation(new StringTrimRight(s), " abdef", createRow(" abdef "));
        checkEvaluation(new StringTrimRight(s, Literal.of("a")), "abdef", createRow("abdefa"));
        checkEvaluation(new StringTrimRight(s, Literal.of("abf de")), "", createRow(" aaabdefaaaa"));
        checkEvaluation(new StringTrimRight(s, Literal.of("S*&")), "SSparkSQL", createRow("SSparkSQLS*"));

        // non ascii characters are not allowed in the source code, so we disable the scalastyle.
        checkEvaluation(new StringTrimRight(Literal.of("a"), Literal.of("花")), "a", createRow(" abdef "));
        checkEvaluation(new StringTrimRight(Literal.of("花"), Literal.of("a")), "花", createRow(" abdef "));
        checkEvaluation(new StringTrimRight(Literal.of("花花世界"), Literal.of("界花世")), "", createRow(" abdef "));
        checkEvaluation(new StringTrimRight(s), "  花花世界", createRow("  花花世界 "));
        checkEvaluation(new StringTrimRight(s, Literal.of("花a#")), "花花世界", createRow("花花世界花花###aa花"));
        checkEvaluation(new StringTrimRight(s, Literal.of("花")), "", createRow("花花花花"));
        checkEvaluation(new StringTrimRight(s, Literal.of("花 界b@")), " 花花世", createRow(" 花花世 b界@花花 "));

        checkEvaluation(new StringTrimRight(Literal.of("a"), Literal.create(null, STRING)), null);
        checkEvaluation(new StringTrimRight(Literal.create(null, STRING), Literal.of("a")), null);

        // Test escaping of arguments
        GenerateSafeProjection.get().generate(List.of(new StringTrimRight(Literal.of("\"quote"), Literal.of("\"quote"))));

        checkEvaluation(new StringTrimRight(Literal.of("testxxzx"), Literal.of("xyz")), "test");
        checkEvaluation(new StringTrimRight(Literal.of("xyztestxxzx"), Literal.of("xyz")), "xyztest");
        checkEvaluation(new StringTrimRight(Literal.of("TURNERyxXxy"), Literal.of("xy")), "TURNERyxX");
    }

    @Test
    public void testRepeat() {
        BoundReference s1 = new BoundReference(0, STRING);
        BoundReference s2 = new BoundReference(1, INTEGER);
        InternalRow row1 = createRow("hi", 2);
        InternalRow row2 = createRow(null, 1);

        checkEvaluation(new StringRepeat(Literal.of("hi"), Literal.of(2)), "hihi", row1);
        checkEvaluation(new StringRepeat(Literal.of("hi"), Literal.of(-1)), "", row1);
        checkEvaluation(new StringRepeat(s1, s2), "hihi", row1);
        checkEvaluation(new StringRepeat(s1, s2), null, row2);

        // Test escaping of arguments
        GenerateSafeProjection.get().generate(List.of(new StringRepeat(Literal.of("\"quote"), Literal.of(2))));
    }



/*
  test("Substring") {
    val row = create_row("example", "example".toArray.map(_.toByte))

    val s = $"a".string.at(0)

    // substring from zero position with less-than-full length
    checkEvaluation(
      Substring(s, Literal.create(0, IntegerType), Literal.create(2, IntegerType)), "ex", row)
    checkEvaluation(
      Substring(s, Literal.create(1, IntegerType), Literal.create(2, IntegerType)), "ex", row)

    // substring from zero position with full length
    checkEvaluation(
      Substring(s, Literal.create(0, IntegerType), Literal.create(7, IntegerType)), "example", row)
    checkEvaluation(
      Substring(s, Literal.create(1, IntegerType), Literal.create(7, IntegerType)), "example", row)

    // substring from zero position with greater-than-full length
    checkEvaluation(Substring(s, Literal.create(0, IntegerType), Literal.create(100, IntegerType)),
      "example", row)
    checkEvaluation(Substring(s, Literal.create(1, IntegerType), Literal.create(100, IntegerType)),
      "example", row)

    // substring from nonzero position with less-than-full length
    checkEvaluation(Substring(s, Literal.create(2, IntegerType), Literal.create(2, IntegerType)),
      "xa", row)

    // substring from nonzero position with full length
    checkEvaluation(Substring(s, Literal.create(2, IntegerType), Literal.create(6, IntegerType)),
      "xample", row)

    // substring from nonzero position with greater-than-full length
    checkEvaluation(Substring(s, Literal.create(2, IntegerType), Literal.create(100, IntegerType)),
      "xample", row)

    // zero-length substring (within string bounds)
    checkEvaluation(Substring(s, Literal.create(0, IntegerType), Literal.create(0, IntegerType)),
      "", row)

    // zero-length substring (beyond string bounds)
    checkEvaluation(Substring(s, Literal.create(100, IntegerType), Literal.create(4, IntegerType)),
      "", row)

    // substring(null, _, _) -> null
    checkEvaluation(Substring(s, Literal.create(100, IntegerType), Literal.create(4, IntegerType)),
      null, create_row(null))

    // substring(_, null, _) -> null
    checkEvaluation(Substring(s, Literal.create(null, IntegerType), Literal.create(4, IntegerType)),
      null, row)

    // substring(_, _, null) -> null
    checkEvaluation(
      Substring(s, Literal.create(100, IntegerType), Literal.create(null, IntegerType)),
      null,
      row)

    // 2-arg substring from zero position
    checkEvaluation(
      Substring(s, Literal.create(0, IntegerType), Literal.create(Integer.MAX_VALUE, IntegerType)),
      "example",
      row)
    checkEvaluation(
      Substring(s, Literal.create(1, IntegerType), Literal.create(Integer.MAX_VALUE, IntegerType)),
      "example",
      row)

    // 2-arg substring from nonzero position
    checkEvaluation(
      Substring(s, Literal.create(2, IntegerType), Literal.create(Integer.MAX_VALUE, IntegerType)),
      "xample",
      row)

    // Substring with from negative position with negative length
    checkEvaluation(Substring(s, Literal.create(-1207959552, IntegerType),
      Literal.create(-1207959552, IntegerType)), "", row)

    val s_notNull = $"a".string.notNull.at(0)

    assert(Substring(s, Literal.create(0, IntegerType), Literal.create(2, IntegerType)).nullable)
    assert(
      Substring(s_notNull, Literal.create(0, IntegerType), Literal.create(2, IntegerType)).nullable
        === false)
    assert(Substring(s_notNull,
      Literal.create(null, IntegerType), Literal.create(2, IntegerType)).nullable)
    assert(Substring(s_notNull,
      Literal.create(0, IntegerType), Literal.create(null, IntegerType)).nullable)

    checkEvaluation(s.substr(0, 2), "ex", row)
    checkEvaluation(s.substr(0), "example", row)
    checkEvaluation(s.substring(0, 2), "ex", row)
    checkEvaluation(s.substring(0), "example", row)

    val bytes = Array[Byte](1, 2, 3, 4)
    checkEvaluation(Substring(bytes, 0, 2), Array[Byte](1, 2))
    checkEvaluation(Substring(bytes, 1, 2), Array[Byte](1, 2))
    checkEvaluation(Substring(bytes, 2, 2), Array[Byte](2, 3))
    checkEvaluation(Substring(bytes, 3, 2), Array[Byte](3, 4))
    checkEvaluation(Substring(bytes, 4, 2), Array[Byte](4))
    checkEvaluation(Substring(bytes, 8, 2), Array.empty[Byte])
    checkEvaluation(Substring(bytes, -1, 2), Array[Byte](4))
    checkEvaluation(Substring(bytes, -2, 2), Array[Byte](3, 4))
    checkEvaluation(Substring(bytes, -3, 2), Array[Byte](2, 3))
    checkEvaluation(Substring(bytes, -4, 2), Array[Byte](1, 2))
    checkEvaluation(Substring(bytes, -5, 2), Array[Byte](1))
    checkEvaluation(Substring(bytes, -8, 2), Array.empty[Byte])
  }
* */
}
