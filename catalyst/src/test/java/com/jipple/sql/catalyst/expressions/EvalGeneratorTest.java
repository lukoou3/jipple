package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.arithmetic.Add;
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

}