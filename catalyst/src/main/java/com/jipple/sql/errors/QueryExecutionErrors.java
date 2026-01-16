package com.jipple.sql.errors;

import com.jipple.error.JippleException;
import com.jipple.sql.types.DataType;

import java.util.Map;

import static com.jipple.sql.types.DataTypes.NULL;

public class QueryExecutionErrors {
    public static JippleException cannotCastFromNullTypeError(DataType to) {
        return new JippleException(
                "CANNOT_CAST_DATATYPE",
                Map.of("sourceType", NULL.typeName(), "targetType", to.typeName()),
                null);
    }
}
