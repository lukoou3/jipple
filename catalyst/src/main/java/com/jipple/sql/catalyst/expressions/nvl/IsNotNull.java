package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.EmptyBlock;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.ExprValue;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.catalyst.expressions.codegen.JavaCode;
import com.jipple.sql.catalyst.expressions.codegen.TrueLiteral;
import com.jipple.sql.types.BooleanType;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class IsNotNull extends UnaryExpression {

    public IsNotNull(Expression child) {
        super(child);
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    public Object eval(InternalRow input) {
        return child.eval(input) != null;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ExprCode eval = child.genCode(ctx);
        ExprValue value;
        Block newCode;
        if (eval.isNull instanceof TrueLiteral) {
            value = FalseLiteral.INSTANCE;
            newCode = EmptyBlock.INSTANCE;
        } else if (eval.isNull instanceof FalseLiteral) {
            value = TrueLiteral.INSTANCE;
            newCode = EmptyBlock.INSTANCE;
        } else {
            String valueName = ctx.freshName("value");
            value = JavaCode.variable(valueName, BooleanType.INSTANCE);
            newCode = Block.block(
                    "boolean ${value} = !${isNull};",
                    java.util.Map.of(
                            "value", value,
                            "isNull", eval.isNull
                    )
            );
        }
        return new ExprCode(eval.code.plus(newCode), FalseLiteral.INSTANCE, value);
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new IsNotNull(newChild);
    }
}
