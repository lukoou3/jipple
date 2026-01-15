package com.jipple.sql.catalyst.types;

import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.types.DataType;

import java.util.Comparator;

public class PhysicalArrayType extends PhysicalDataType<ArrayData> {
    public final DataType elementType;
    public final boolean containsNull;

    public PhysicalArrayType(DataType elementType, boolean containsNull) {
        this.elementType = elementType;
        this.containsNull = containsNull;
    }

    @Override
    public Comparator<ArrayData> comparator() {
        final Comparator<Object> elementComparator = (Comparator<Object>) PhysicalDataType.of(elementType).comparator();
        return (leftArray,  rightArray) -> {
            int minLength = Math.min(leftArray.numElements(), rightArray.numElements());
            for (int i = 0; i < minLength; i++) {
                boolean isNullLeft = leftArray.isNullAt(i);
                boolean isNullRight = rightArray.isNullAt(i);
                if (isNullLeft && isNullRight) {
                    // Do nothing.
                } else if (isNullLeft) {
                    return -1;
                } else if (isNullRight) {
                    return 1;
                } else {
                    int comp = elementComparator.compare(leftArray.get(i, elementType), rightArray.get(i, elementType));
                    if (comp != 0) {
                        return comp;
                    }
                }
            }
            return leftArray.numElements() - rightArray.numElements();
        };
    }

    @Override
    public Class<ArrayData> internalTypeClass() {
        return ArrayData.class;
    }
}


