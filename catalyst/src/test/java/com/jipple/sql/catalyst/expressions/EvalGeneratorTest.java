package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.arithmetic.*;
import com.jipple.sql.catalyst.expressions.predicate.EqualNullSafe;
import com.jipple.sql.catalyst.expressions.predicate.EqualTo;
import com.jipple.sql.catalyst.expressions.predicate.GreaterThanOrEqual;
import com.jipple.sql.catalyst.expressions.string.Substring;
import com.jipple.unsafe.types.UTF8String;
import org.junit.jupiter.api.Test;

import static com.jipple.sql.types.DataTypes.*;
import static org.junit.jupiter.api.Assertions.*;

public class EvalGeneratorTest extends ExpressionEvalHelper {

    @Test
    public void testInterpretedEval() {
        var row = createRow("example", 10);
        var s = new BoundReference(0, STRING);
        Substring substring = new Substring(s, Literal.of(1), Literal.of(2));
        Eval eval = EvalGenerator.get().createInterpretedObject(substring);
        System.out.println(eval.eval(row));
        assertEquals(eval.eval(row), UTF8String.fromString("ex"));
        var i = new BoundReference(1, INTEGER);
        var add = new Add(i, Literal.of(1));
        eval = EvalGenerator.get().createInterpretedObject(add);
        System.out.println(eval.eval(row));
        assertEquals(eval.eval(row), 11);
    }

    @Test
    public void testCatGeneratedEval() {
        var col1 = new BoundReference(0, DOUBLE);
        var col2 = new BoundReference(1, DOUBLE);
        EvalGenerator.get().createCodeGeneratedObject(new GreaterThanOrEqual(col1, col2));
        EvalGenerator.get().createCodeGeneratedObject(new EqualTo(col1, col2));
        EvalGenerator.get().createCodeGeneratedObject(new EqualNullSafe(col1, col2));
        EvalGenerator.get().createCodeGeneratedObject(new Add(col1, col2));
    }

    @Test
    public void testGeneratedEval() {
        var row = createRow(10, 100);
        var col1 = new BoundReference(0, INTEGER);
        var col2 = new BoundReference(1, INTEGER);
        var add = new Add(col1, col2);
        Eval eval = EvalGenerator.get().createCodeGeneratedObject(add);
        System.out.println(eval.eval(row));
        assertEquals(eval.eval(row), 110);
        var l = Literal.of(1);
        var sub = new Subtract(col1, l);
        eval = EvalGenerator.get().createCodeGeneratedObject(sub);
        System.out.println(eval.eval(row));
        assertEquals(eval.eval(row), 9);

        row = createRow(10L, 100L);
        col1 = new BoundReference(0, LONG);
        col2 = new BoundReference(1, LONG);
        add = new Add(col1, col2);
        eval = EvalGenerator.get().createCodeGeneratedObject(add);
        System.out.println(eval.eval(row));
        assertEquals(eval.eval(row), 110L);
        l = Literal.of(1L);
        sub = new Subtract(col1, l);
        eval = EvalGenerator.get().createCodeGeneratedObject(sub);
        System.out.println(eval.eval(row));
        assertEquals(eval.eval(row), 9L);
    }

}