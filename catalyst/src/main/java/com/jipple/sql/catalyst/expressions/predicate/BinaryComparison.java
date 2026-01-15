package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.AtomicType;
import com.jipple.sql.types.DataType;

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

    protected Comparator<Object> comparator(){
        if(_comparator == null){
            _comparator = TypeUtils.getInterpretedComparator(left.dataType());
        }
        return _comparator;
    }

}
