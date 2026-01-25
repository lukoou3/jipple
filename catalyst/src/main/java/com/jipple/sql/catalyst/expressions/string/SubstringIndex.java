package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.TernaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.Map;

import static com.jipple.sql.types.DataTypes.STRING;
import static com.jipple.sql.types.DataTypes.INTEGER;

public class SubstringIndex extends TernaryExpression {
    public SubstringIndex(Expression strExpr, Expression delimExpr, Expression countExpr) {
        super(strExpr, delimExpr, countExpr);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(STRING, STRING, INTEGER));
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public String prettyName() {
        return "substring_index";
    }

    @Override
    protected Object nullSafeEval(Object str, Object delim, Object count) {
        return ((UTF8String) str).subStringIndex((UTF8String) delim, (Integer) count);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return defineCodeGen(ctx, ev, (str, delim, count) ->
                CodeGeneratorUtils.template(
                        "${str}.subStringIndex(${delim}, ${count})",
                        Map.of(
                                "str", str,
                                "delim", delim,
                                "count", count
                        )
                ));
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new SubstringIndex(newFirst, newSecond, newThird);
    }
}