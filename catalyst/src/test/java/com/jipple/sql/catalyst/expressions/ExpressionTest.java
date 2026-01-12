package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.nvl.IsNull;
import com.jipple.sql.catalyst.expressions.string.StringRepeat;
import org.junit.jupiter.api.Test;

public class ExpressionTest {

    @Test
    public void testTrait() throws Exception {
        BoundReference boundReference1 = new BoundReference(0, null);
        BoundReference boundReference2 = new BoundReference(1, null);
        BoundReference boundReference3 = new BoundReference(2, null);
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


}
