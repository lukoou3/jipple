package com.jipple.sql.errors;

import com.jipple.error.JippleException;
import com.jipple.error.JippleUnsupportedOperationException;

import java.util.Map;

public class DataTypeErrors {

    public static JippleException valueIsNullError(int index) {
        return new JippleException(
                "_LEGACY_ERROR_TEMP_2232",
                Map.of("index", String.valueOf(index)),
                null);
    }

    public static JippleUnsupportedOperationException fieldIndexOnRowWithoutSchemaError() {
        return new JippleUnsupportedOperationException("_LEGACY_ERROR_TEMP_2231", Map.of());
    }

}
