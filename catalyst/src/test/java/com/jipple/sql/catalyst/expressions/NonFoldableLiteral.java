package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;

/**
 * A literal value that is not foldable. Used in expression codegen testing to test code path
 * that behave differently based on foldable values.
 */
public class NonFoldableLiteral extends LeafExpression {
    public final Object value;
    public final DataType dataType;

    public NonFoldableLiteral(Object value, DataType dataType) {
        this.value = value;
        this.dataType = dataType;
    }

    @Override
    public Object[] args() {
        return new Object[]{value, dataType};
    }

    @Override
    public boolean foldable() {
        return false;
    }

    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return value != null? value.toString(): "null";
    }

    @Override
    public Object eval(InternalRow input) {
        return value;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return Literal.create(value, dataType).doGenCode(ctx, ev);
    }

    public static NonFoldableLiteral of(Object v) {
        Literal lit = Literal.of(v);
        return new NonFoldableLiteral(lit.value, lit.dataType);
    }

    public static NonFoldableLiteral create(Object v, DataType dataType) {
        Literal lit = Literal.create(v, dataType);
        return new NonFoldableLiteral(lit.value, lit.dataType);
    }
}
