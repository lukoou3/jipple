package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.TernaryExpression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.types.DataTypes.STRING;

public class StringReplace extends TernaryExpression {

    public StringReplace(Expression srcStr, Expression searchExpr, Expression replaceExpr) {
        super(srcStr, searchExpr, replaceExpr);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(STRING, STRING, STRING));
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public String prettyName() {
        return "replace";
    }

    @Override
    protected Object nullSafeEval(Object srcEval, Object searchEval, Object replaceEval) {
        return ((UTF8String) srcEval).replace((UTF8String) searchEval, (UTF8String) replaceEval);
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new StringReplace(newFirst, newSecond, newThird);
    }
}
