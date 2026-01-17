package com.jipple.sql.types;

import java.io.Serializable;

/**
 * A non-concrete data type, reserved for internal uses.
 */
public abstract class AbstractDataType implements Serializable {

    /**
     * The default concrete type to use if we want to cast a null literal into this type.
     */
    public abstract DataType defaultConcreteType();

    /**
     * Returns true if {@code other} is an acceptable input type for a function that expects this,
     * possibly abstract DataType.
     * <p>
     * Examples:
     * <pre>{@code
     *   // this should return true
     *   DecimalType.acceptsType(DecimalType(10, 2))
     *
     *   // this should return true as well
     *   NumericType.acceptsType(DecimalType(10, 2))
     * }</pre>
     */
    public abstract boolean acceptsType(DataType other);

    /**
     * Readable string representation for the type.
     */
    public abstract String simpleString();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return true;
    }

}
