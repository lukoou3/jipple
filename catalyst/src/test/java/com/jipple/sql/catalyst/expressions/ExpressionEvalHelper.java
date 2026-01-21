package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.CatalystTypeConverters;
import com.jipple.sql.catalyst.InternalRow;

public abstract class ExpressionEvalHelper {

    protected InternalRow createRow(Object... values) {
        Object[] converted = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            converted[i] = CatalystTypeConverters.convertToCatalyst(values[i]);
        }
        return InternalRow.of(converted);
    }


}
