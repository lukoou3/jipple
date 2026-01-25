package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.JippleDateTimeUtils;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.Map;

import static com.jipple.sql.types.DataTypes.DATE;
import static com.jipple.sql.types.DataTypes.STRING;

public class NextDay extends BinaryExpression {
    public NextDay(Expression startDate, Expression dayOfWeek) {
        super(startDate, dayOfWeek);
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
    public boolean nullable() {
        return true;
    }

    @Override
    protected Object nullSafeEval(Object start, Object dayOfW) {
        try {
            int dow = JippleDateTimeUtils.getDayOfWeekFromString((UTF8String) dayOfW);
            int sd = (Integer) start;
            return JippleDateTimeUtils.getNextDateForDayOfWeek(sd, dow);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String nextDayGenCode(ExprCode ev, String dayOfWeekTerm, String sd, String dowS) {
        String dateTimeUtils = JippleDateTimeUtils.class.getName();
        return CodeGeneratorUtils.template(
                """
                        try {
                          int ${dayOfWeekTerm} = ${dateTimeUtils}.getDayOfWeekFromString(${dowS});
                          ${value} = ${dateTimeUtils}.getNextDateForDayOfWeek(${sd}, ${dayOfWeekTerm});
                        } catch (IllegalArgumentException e) {
                          ${isNull} = true;
                        }
                        """,
                Map.ofEntries(
                        Map.entry("dayOfWeekTerm", dayOfWeekTerm),
                        Map.entry("dateTimeUtils", dateTimeUtils),
                        Map.entry("dowS", dowS),
                        Map.entry("value", ev.value),
                        Map.entry("sd", sd),
                        Map.entry("isNull", ev.isNull)
                )
        );
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return nullSafeCodeGen(ctx, ev, (sd, dowS) -> {
            String dayOfWeekTerm = ctx.freshName("dayOfWeek");
            if (right.foldable()) {
                Object input = right.eval(InternalRow.EMPTY);
                if (input == null) {
                    return CodeGeneratorUtils.template(
                            "${isNull} = true;",
                            Map.of("isNull", ev.isNull)
                    );
                }
                try {
                    int dayOfWeekValue = JippleDateTimeUtils.getDayOfWeekFromString((UTF8String) input);
                    return CodeGeneratorUtils.template(
                            "${value} = ${dateTimeUtils}.getNextDateForDayOfWeek(${sd}, ${dayOfWeek});",
                            Map.ofEntries(
                                    Map.entry("value", ev.value),
                                    Map.entry("dateTimeUtils", JippleDateTimeUtils.class.getName()),
                                    Map.entry("sd", sd),
                                    Map.entry("dayOfWeek", dayOfWeekValue)
                            )
                    );
                } catch (IllegalArgumentException e) {
                    return nextDayGenCode(ev, dayOfWeekTerm, sd, dowS);
                }
            }
            return nextDayGenCode(ev, dayOfWeekTerm, sd, dowS);
        });
    }

    @Override
    public String prettyName() {
        return "next_day";
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new NextDay(newLeft, newRight);
    }
}
