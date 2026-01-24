package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.types.*;

import java.util.Comparator;

import static com.jipple.sql.types.DataTypes.ANY;
import static com.jipple.sql.types.DataTypes.BOOLEAN;

public abstract class BinaryComparison extends BinaryOperator {
    Comparator<Object> _comparator;

    public BinaryComparison(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return ANY;
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        TypeCheckResult checkResult = super.checkInputDataTypes();
        if(checkResult.isSuccess()){
            if(!(left.dataType() instanceof AtomicType)){
                return TypeCheckResult.typeCheckFailure("not support ordering on type " + left.dataType());
            }
        }
        return checkResult;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        DataType leftType = left.dataType();
        if (CodeGeneratorUtils.isPrimitiveType(leftType)
                && !(leftType instanceof BooleanType)
                && !(leftType instanceof FloatType)
                && !(leftType instanceof DoubleType)) {
            // faster version
            return defineCodeGen(ctx, ev, (c1, c2) -> String.format("%s %s %s", c1, symbol(), c2));
        } else {
            return defineCodeGen(ctx, ev, (c1, c2) -> String.format("%s %s 0" , ctx.genComp(leftType, c1, c2), symbol()));
        }
    }

    protected Comparator<Object> comparator(){
        if(_comparator == null){
            _comparator = TypeUtils.getInterpretedComparator(left.dataType());
        }
        return _comparator;
    }

}
