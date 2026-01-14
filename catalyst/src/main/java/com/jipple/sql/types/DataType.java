package com.jipple.sql.types;

import java.util.stream.IntStream;

import static org.apache.commons.lang3.Strings.CS;

/**
 * The base type of all Spark SQL data types.
 */
public abstract class DataType extends AbstractDataType {

    /**
     * The default size of a value of this data type, used internally for size estimation.
     */
    public abstract int defaultSize();

    /** Name of the type used in JSON serialization. */
    public String typeName() {
        String typeName = this.getClass().getSimpleName();
        typeName = CS.removeEnd(typeName, "$");
        typeName = CS.removeEnd(typeName, "Type");
        typeName = CS.removeEnd(typeName, "UDT");
        return typeName.toLowerCase();
    }

    @Override
    public String simpleString() {
        return typeName();
    }

    public String sql() {
        return simpleString().toUpperCase();
    }

    public boolean sameType(DataType other) {
        return DataType.equalsIgnoreNullability(this, other);
    }

    /**
     * Returns the same data type but set all nullability fields are true
     * (`StructField.nullable`, `ArrayType.containsNull`, and `MapType.valueContainsNull`).
     */
    public abstract DataType asNullable();

    @Override
    public boolean acceptsType(DataType other) {
        return sameType(other);
    }

    @Override
    public DataType defaultConcreteType() {
        return this;
    }


    @Override
    public int hashCode() {
        return simpleString().hashCode();
    }

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

    @Override
    public String toString() {
        return simpleString();
    }

   /*********static method*********/

    /**
     * Compares two types, ignoring nullability of ArrayType, MapType, StructType.
     */
    public static boolean equalsIgnoreNullability(DataType left, DataType right) {
        if (left instanceof ArrayType l && right instanceof ArrayType r) {
            return equalsIgnoreNullability(l.elementType, r.elementType);
        } else if (left instanceof MapType l && right instanceof MapType r) {
            return equalsIgnoreNullability(l.keyType, r.keyType) &&
                    equalsIgnoreNullability(l.valueType, r.valueType);
        } else if (left instanceof StructType l && right instanceof StructType r) {
            return l.fields.length == r.fields.length && IntStream.range(0, l.fields.length)
                    .allMatch(i -> l.fields[i].name.equals(r.fields[i].name) && equalsIgnoreNullability(l.fields[i].dataType, r.fields[i].dataType) );
        } else {
            return left.equals(right);
        }
    }
}
