package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion;
import com.jipple.sql.types.DataType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A trait used for resolving nullable flags, including `nullable`, `containsNull` of [[ArrayType]]
 * and `valueContainsNull` of [[MapType]], containsNull, valueContainsNull flags of the output date
 * type. This is usually utilized by the expressions (e.g. [[CaseWhen]]) that combine data from
 * multiple child expressions of non-primitive types.
 */
public abstract class ComplexTypeMergingExpression extends Expression {
    private List<DataType> _inputTypesForMerging;
    private DataType _internalDataType;

    public List<DataType> inputTypesForMerging() {
        if (_inputTypesForMerging == null) {
            _inputTypesForMerging = children().stream().map(Expression::dataType).collect(Collectors.toList());
        }
        return _inputTypesForMerging;
    }

    protected void dataTypeCheck() {
        List<DataType> inputTypes = inputTypesForMerging();
        if (inputTypes.isEmpty()) {
            throw new IllegalArgumentException("The collection of input data types must not be empty.");
        }
        if (!TypeCoercion.haveSameType(inputTypes)) {
            String types = inputTypes.stream().map(DataType::toString).collect(Collectors.joining("\n\t"));
            throw new IllegalArgumentException(
                    "All input types must be the same except nullable, containsNull, valueContainsNull flags. "
                            + "The expression is: " + this + ". "
                            + "The input types found are\n\t" + types + ".");
        }
    }

    private DataType internalDataType() {
        if (_internalDataType == null) {
            dataTypeCheck();
            List<DataType> inputTypes = inputTypesForMerging();
            DataType mergedType = inputTypes.get(0);
            for (int i = 1; i < inputTypes.size(); i++) {
                mergedType = TypeCoercion.findCommonTypeDifferentOnlyInNullFlags(mergedType, inputTypes.get(i)).get();
            }
            _internalDataType = mergedType;
        }
        return _internalDataType;
    }

    @Override
    public DataType dataType() {
        return internalDataType();
    }
}
