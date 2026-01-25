package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.JippleDateTimeUtils;
import com.jipple.unsafe.types.UTF8String;

import java.util.Map;
import java.util.function.BiFunction;

public abstract class TruncInstant extends BinaryExpression {
    private Integer _truncLevel;

    protected TruncInstant(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean nullable() {
        return true;
    }

    protected abstract Expression instant();

    protected abstract Expression format();

    /**
     * @param input internalRow (time)
     * @param minLevel Minimum level that can be used for truncation (e.g WEEK for Date input)
     * @param truncFunc function: (time, level) => time
     */
    protected Object evalHelper(InternalRow input, int minLevel, BiFunction<Object, Integer, Object> truncFunc) {
        int level;
        if (format().foldable()) {
            level = truncLevel();
        } else {
            level = JippleDateTimeUtils.parseTruncLevel((UTF8String) format().eval(input));
        }
        if (level < minLevel) {
            return null;
        }
        Object t = instant().eval(input);
        if (t == null) {
            return null;
        }
        return truncFunc.apply(t, level);
    }

    protected ExprCode codeGenHelper(
            CodegenContext ctx,
            ExprCode ev,
            int minLevel,
            boolean orderReversed,
            BiFunction<String, String, String> truncFunc) {
        String dtu = JippleDateTimeUtils.class.getName();
        String javaType = CodeGeneratorUtils.javaType(dataType());
        if (format().foldable()) {
            if (truncLevel() < minLevel) {
                return ev.copy(Block.block(
                        """
                                boolean ${isNull} = true;
                                ${javaType} ${value} = ${defaultValue};
                                """,
                        Map.of(
                                "isNull", ev.isNull,
                                "javaType", javaType,
                                "value", ev.value,
                                "defaultValue", CodeGeneratorUtils.defaultValue(dataType())
                        )
                ));
            }
            ExprCode t = instant().genCode(ctx);
            String truncFuncStr = truncFunc.apply(t.value.toString(), String.valueOf(truncLevel()));
            return ev.copy(Block.block(
                    """
                            ${tCode}
                            boolean ${isNull} = ${tIsNull};
                            ${javaType} ${value} = ${defaultValue};
                            if (!${isNull}) {
                              ${value} = ${dtu}.${truncFuncStr};
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("tCode", t.code),
                            Map.entry("isNull", ev.isNull),
                            Map.entry("tIsNull", t.isNull),
                            Map.entry("javaType", javaType),
                            Map.entry("value", ev.value),
                            Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(dataType())),
                            Map.entry("dtu", dtu),
                            Map.entry("truncFuncStr", truncFuncStr)
                    )
            ));
        }
        return nullSafeCodeGen(ctx, ev, (left, right) -> {
            String form = ctx.freshName("form");
            String dateVal = orderReversed ? right : left;
            String fmt = orderReversed ? left : right;
            String truncFuncStr = truncFunc.apply(dateVal, form);
            return CodeGeneratorUtils.template(
                    """
                            int ${form} = ${dtu}.parseTruncLevel(${fmt});
                            if (${form} < ${minLevel}) {
                              ${isNull} = true;
                            } else {
                              ${value} = ${dtu}.${truncFuncStr};
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("form", form),
                            Map.entry("dtu", dtu),
                            Map.entry("fmt", fmt),
                            Map.entry("minLevel", minLevel),
                            Map.entry("isNull", ev.isNull),
                            Map.entry("value", ev.value),
                            Map.entry("truncFuncStr", truncFuncStr)
                    )
            );
        });
    }

    private int truncLevel() {
        if (_truncLevel == null) {
            _truncLevel = JippleDateTimeUtils.parseTruncLevel((UTF8String) format().eval(InternalRow.EMPTY));
        }
        return _truncLevel;
    }
}
