package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.util.TimestampFormatter;
import com.jipple.sql.types.*;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

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
            //daysToMicros((Integer)t, zoneId()) / downScaleFactor();
            return 0L;
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
                        return fmt.parse(((UTF8String)t).toString());
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        } else {
            return (Long)t / downScaleFactor();
        }
    }

}
