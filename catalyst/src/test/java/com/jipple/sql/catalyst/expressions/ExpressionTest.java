package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.arithmetic.*;
import com.jipple.sql.catalyst.expressions.nvl.*;
import com.jipple.sql.catalyst.expressions.string.*;
import org.junit.jupiter.api.Test;

import static com.jipple.sql.types.DataTypes.*;

public class ExpressionTest {

    @Test
    public void testTrait() throws Exception {
        BoundReference boundReference1 = new BoundReference(0, INTEGER);
        BoundReference boundReference2 = new BoundReference(1, INTEGER);
        BoundReference boundReference3 = new BoundReference(2, STRING);
        IsNull expr1 = new IsNull(boundReference1);
        StringRepeat expr2 = new StringRepeat(boundReference1, boundReference2);
        System.out.println(expr1.child);
        System.out.println(expr1.children());
        System.out.println(expr1.mapChildren(x -> boundReference3));
        System.out.println(expr2.left());
        System.out.println(expr2.right());
        System.out.println(expr2.children());
        System.out.println(expr2.mapChildren(x -> boundReference3));
    }

    @Test
    public void testTrueEq() {
        System.out.println(Boolean.TRUE.equals(null)); // null 不instanceof 任何类
        System.out.println(null instanceof Boolean);
        System.out.println(null instanceof ExpressionTest);
        System.out.println(Boolean.TRUE.equals(true));
        System.out.println(Boolean.TRUE.equals(false));
    }

    @Test
    public void testAdd() {
        Expression expression = new Add(new BoundReference(0, LONG), new Literal(100L, LONG));
        System.out.println(expression);
        InternalRow row = new GenericInternalRow(1);
        System.out.println(expression.eval(row));
        row.update(0, 1L);
        System.out.println(expression.eval(row) + ", " + expression.eval(row).getClass());
        row.update(0, 11L);
        System.out.println(expression.eval(row) + ", " + expression.eval(row).getClass());
    }


}
