package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.JippleDateTimeUtils;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import java.util.List;

import static com.jipple.sql.types.DataTypes.DATE;
import static com.jipple.sql.types.DataTypes.STRING;

public class TruncDate extends TruncInstant {
    private final Expression date;
    private final Expression format;

    public TruncDate(Expression date, Expression format) {
        super(date, format);
        this.date = date;
        this.format = format;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(DATE, STRING));
    }

    @Override
    public DataType dataType() {
        return DATE;
    }

    @Override
    public String prettyName() {
        return "trunc";
    }

    @Override
    protected Expression instant() {
        return date;
    }

    @Override
    protected Expression format() {
        return format;
    }

    @Override
    public Object eval(InternalRow input) {
        return evalHelper(input, JippleDateTimeUtils.MIN_LEVEL_OF_DATE_TRUNC,
                (d, level) -> JippleDateTimeUtils.truncDate((Integer) d, level));
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return codeGenHelper(ctx, ev, JippleDateTimeUtils.MIN_LEVEL_OF_DATE_TRUNC, false,
                (d, fmt) -> "truncDate(" + d + ", " + fmt + ")");
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new TruncDate(newLeft, newRight);
    }
}
