package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.TimeZoneAwareExpression;
import com.jipple.sql.catalyst.util.TimestampFormatter;

public abstract class TimestampFormatterHelper extends BinaryExpression implements TimeZoneAwareExpression {
    private Option<TimestampFormatter> _formatterOption;

    public TimestampFormatterHelper(Expression left, Expression right) {
        super(left, right);
    }

    protected abstract Expression formatString();

    protected Option<TimestampFormatter> formatterOption() {
        if (_formatterOption == null) {
            if (formatString().foldable()) {
                _formatterOption = Option.of(formatString().eval()).map(fmt -> getFormatter(fmt.toString()));
            } else {
                _formatterOption = Option.none();
            }
        }
        return _formatterOption;
    }

    protected final TimestampFormatter getFormatter(String fmt) {
        return TimestampFormatter.getFormatter(Option.some(fmt), zoneId());
    }

}
