package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.NULL;

/**
 * A place holder expressions used in code-gen, it does not change the corresponding value
 * in the row.
 */
public class NoOp extends LeafExpression {
    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public DataType dataType() {
        return NULL;
    }

    @Override
    public boolean foldable() {
        return false;
    }

    @Override
    public final Object eval(InternalRow input) {
        throw QueryExecutionErrors.cannotEvaluateExpressionError(this);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        throw QueryExecutionErrors.cannotGenerateCodeForExpressionError(this);
    }
}
