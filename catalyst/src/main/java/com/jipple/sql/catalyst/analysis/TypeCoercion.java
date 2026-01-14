package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.catalyst.types.DataTypeUtils;
import com.jipple.sql.types.DataType;

import java.util.List;

public class TypeCoercion {

    /**
     * Check whether the given types are equal ignoring nullable, containsNull and valueContainsNull.
     */
    public static boolean haveSameType(List<DataType> types) {
        if (types.size() <= 1) {
            return true;
        } else {
            DataType head = types.get(0);
            return types.stream().allMatch(e -> DataTypeUtils.sameType(e, head));
        }
    }

}
