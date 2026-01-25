package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.TernaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.GenericArrayData;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.ArrayType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.Map;

import static com.jipple.sql.types.DataTypes.STRING;
import static com.jipple.sql.types.DataTypes.INTEGER;

public class StringSplit extends TernaryExpression {
    public StringSplit(Expression str, Expression regex, Expression limit) {
        super(str, regex, limit);
    }

    // Constructor for two arguments (str and regex), using default limit of -1
    public StringSplit(Expression str, Expression regex) {
        this(str, regex, Literal.of(-1));
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(STRING, STRING, INTEGER));
    }

    @Override
    public DataType dataType() {
        return new ArrayType(STRING, false);
    }

    @Override
    public String prettyName() {
        return "split";
    }

    @Override
    protected Object nullSafeEval(Object string, Object regex, Object limit) {
        UTF8String[] strings = ((UTF8String) string).split((UTF8String) regex, (Integer) limit);
        return new GenericArrayData(strings);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String arrayClass = GenericArrayData.class.getName();
        return nullSafeCodeGen(ctx, ev, (str, regex, limit) ->
                // Array in java is covariant, so we don't need to cast UTF8String[] to Object[].
                CodeGeneratorUtils.template(
                        "${value} = new ${arrayClass}(${str}.split(${regex}, ${limit}));",
                        Map.of(
                                "value", ev.value,
                                "arrayClass", arrayClass,
                                "str", str,
                                "regex", regex,
                                "limit", limit
                        )
                ));
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new StringSplit(newFirst, newSecond, newThird);
    }
}