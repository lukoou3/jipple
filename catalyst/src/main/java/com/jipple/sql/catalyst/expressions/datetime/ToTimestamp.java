package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.JippleDateTimeUtils;
import com.jipple.sql.catalyst.util.TimestampFormatter;
import com.jipple.sql.types.*;
import com.jipple.unsafe.types.UTF8String;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static com.jipple.sql.types.DataTypes.*;

public abstract class ToTimestamp extends TimestampFormatterHelper {
    public ToTimestamp(Expression left, Expression right) {
        super(left, right);
    }

    // The result of the conversion to timestamp is microseconds divided by this factor.
    // For example if the factor is 1000000, the result of the expression is in seconds.
    protected abstract long downScaleFactor();

    @Override
    protected Expression formatString() {
        return right;
    }

    protected boolean forTimestampNTZ(){
        return left().dataType() instanceof TimestampNTZType;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(TypeCollection.of(STRING, DATE, TIMESTAMP, TIMESTAMP_NTZ), STRING));
    }

    @Override
    public DataType dataType() {
        return LONG;
    }

    @Override
    public boolean nullable() {
        // if (failOnError) children.exists(_.nullable) else true
        return true;
    }

    @Override
    public Object eval(InternalRow input) {
        Object t = left.eval(input);
        if (t == null) {
            return null;
        }
        DataType dataType = left.dataType();
        if (dataType instanceof DateType) {
            return JippleDateTimeUtils.daysToMicros((Integer) t, zoneId()) / downScaleFactor();
        } else if (dataType instanceof StringType) {
            Object format = right.eval(input);
            if (format == null) {
                return null;
            } else {
                try {
                    Option<TimestampFormatter> timestampFormatterOption = formatterOption();
                    TimestampFormatter fmt = timestampFormatterOption.isDefined() ? timestampFormatterOption.get() : getFormatter(format.toString());
                    if (forTimestampNTZ()) {
                        return fmt.parseWithoutTimeZone(((UTF8String)t).toString());
                    } else {
                        return fmt.parse(((UTF8String)t).toString()) / downScaleFactor();
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        } else {
            return (Long)t / downScaleFactor();
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String javaType = CodeGeneratorUtils.javaType(dataType());
        String parseErrorBranch = CodeGeneratorUtils.template(
                "${isNull} = true;",
                Map.of("isNull", ev.isNull)
        );
        String parseMethod = forTimestampNTZ() ? "parseWithoutTimeZone" : "parse";
        String downScaleCode = forTimestampNTZ() ? "" : CodeGeneratorUtils.template(
                " / ${factor}",
                Map.of("factor", downScaleFactor())
        );

        DataType leftType = left.dataType();
        if (leftType instanceof StringType) {
            Option<TimestampFormatter> timestampFormatterOption = formatterOption();
            if (timestampFormatterOption.isDefined()) {
                String formatterName = ctx.addReferenceObj(
                        "formatter",
                        timestampFormatterOption.get(),
                        TimestampFormatter.class.getName()
                );
                return nullSafeCodeGen(ctx, ev, (datetimeStr, format) -> CodeGeneratorUtils.template(
                        """
                                try {
                                  ${value} = ${formatter}.${parseMethod}(${datetimeStr}.toString())${downScaleCode};
                                } catch (Exception e) {
                                  ${parseErrorBranch}
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("value", ev.value),
                                Map.entry("formatter", formatterName),
                                Map.entry("parseMethod", parseMethod),
                                Map.entry("datetimeStr", datetimeStr),
                                Map.entry("downScaleCode", downScaleCode),
                                Map.entry("parseErrorBranch", parseErrorBranch)
                        )
                ));
            }
            String zoneId = ctx.addReferenceObj("zoneId", zoneId(), ZoneId.class.getName());
            String timestampFormatterClass = TimestampFormatter.class.getName();
            String timestampFormatter = ctx.freshName("timestampFormatter");
            return nullSafeCodeGen(ctx, ev, (string, format) -> CodeGeneratorUtils.template(
                    """
                            ${timestampFormatterClass} ${timestampFormatter} = ${timestampFormatterClass}.getFormatter(
                              ${format}.toString(),
                              ${zoneId});
                            try {
                              ${value} = ${timestampFormatter}.${parseMethod}(${string}.toString())${downScaleCode};
                            } catch (Exception e) {
                              ${parseErrorBranch}
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("timestampFormatterClass", timestampFormatterClass),
                            Map.entry("timestampFormatter", timestampFormatter),
                            Map.entry("format", format),
                            Map.entry("zoneId", zoneId),
                            Map.entry("value", ev.value),
                            Map.entry("parseMethod", parseMethod),
                            Map.entry("string", string),
                            Map.entry("downScaleCode", downScaleCode),
                            Map.entry("parseErrorBranch", parseErrorBranch)
                    )
            ));
        }
        if (leftType instanceof TimestampType || leftType instanceof TimestampNTZType) {
            ExprCode eval1 = left.genCode(ctx);
            return ev.copy(Block.block(
                    """
                            ${eval1Code}
                            boolean ${isNull} = ${eval1IsNull};
                            ${javaType} ${value} = ${defaultValue};
                            if (!${isNull}) {
                              ${value} = ${eval1Value} / ${downScaleFactor};
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("eval1Code", eval1.code),
                            Map.entry("isNull", ev.isNull),
                            Map.entry("eval1IsNull", eval1.isNull),
                            Map.entry("javaType", javaType),
                            Map.entry("value", ev.value),
                            Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(dataType())),
                            Map.entry("eval1Value", eval1.value),
                            Map.entry("downScaleFactor", downScaleFactor())
                    )
            ));
        }
        if (leftType instanceof DateType) {
            String zoneId = ctx.addReferenceObj("zoneId", zoneId(), ZoneId.class.getName());
            ExprCode eval1 = left.genCode(ctx);
            return ev.copy(Block.block(
                    """
                            ${eval1Code}
                            boolean ${isNull} = ${eval1IsNull};
                            ${javaType} ${value} = ${defaultValue};
                            if (!${isNull}) {
                              ${value} = ${dateTimeUtils}.daysToMicros(${eval1Value}, ${zoneId}) / ${downScaleFactor};
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("eval1Code", eval1.code),
                            Map.entry("isNull", ev.isNull),
                            Map.entry("eval1IsNull", eval1.isNull),
                            Map.entry("javaType", javaType),
                            Map.entry("value", ev.value),
                            Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(dataType())),
                            Map.entry("dateTimeUtils", JippleDateTimeUtils.class.getName()),
                            Map.entry("eval1Value", eval1.value),
                            Map.entry("zoneId", zoneId),
                            Map.entry("downScaleFactor", downScaleFactor())
                    )
            ));
        }
        throw new UnsupportedOperationException(leftType.sql());
    }

}
