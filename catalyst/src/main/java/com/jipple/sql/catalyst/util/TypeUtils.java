package com.jipple.sql.catalyst.util;

import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion;
import com.jipple.sql.catalyst.expressions.OrderUtils;
import com.jipple.sql.catalyst.types.PhysicalDataType;
import com.jipple.sql.types.DataType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Functions to help with checking for valid data types and value comparison of various types.
 */
public class TypeUtils {


    public static TypeCheckResult checkForSameTypeInputExpr(List<DataType> types, String caller) {
        if (TypeCoercion.haveSameType(types)) {
            return TypeCheckResult.typeCheckSuccess();
        } else {
            return TypeCheckResult.dataTypeMismatch("DATA_DIFF_TYPES",
                    Map.of("functionName", caller, "dataType", types.stream().map(DataType::simpleString).collect(Collectors.joining("[", ", ", "]"))));
        }
    }

    public static TypeCheckResult checkForOrderingExpr(DataType dt, String caller) {
      if (OrderUtils.isOrderable(dt)) {
        return TypeCheckResult.typeCheckSuccess();
      } else {
        return TypeCheckResult.dataTypeMismatch(
            "INVALID_ORDERING_TYPE",
            Map.of("functionName", caller, "dataType", dt.simpleString()));
      }
    }

    public static Comparator<Object> getInterpretedComparator(DataType t) {
        return (Comparator<Object>) PhysicalDataType.of(t).comparator();
    }

}
