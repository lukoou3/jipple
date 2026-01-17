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

    /**
     * Returns true if the two data types share the same "shape", i.e. the types
     * are the same, but the field names don't need to be the same.
     *
     * @param from the first data type
     * @param to the second data type
     * @param ignoreNullability whether to ignore nullability when comparing the types
     */
    public static boolean equalsStructurally(DataType from, DataType to, boolean ignoreNullability) {
        if (from instanceof ArrayType left && to instanceof ArrayType right) {
            return equalsStructurally(left.elementType, right.elementType, ignoreNullability) &&
                   (ignoreNullability || left.containsNull == right.containsNull);
        } else if (from instanceof MapType left && to instanceof MapType right) {
            return equalsStructurally(left.keyType, right.keyType, ignoreNullability) &&
                   equalsStructurally(left.valueType, right.valueType, ignoreNullability) &&
                   (ignoreNullability || left.valueContainsNull == right.valueContainsNull);
        } else if (from instanceof StructType fromStruct && to instanceof StructType toStruct) {
            if (fromStruct.fields.length != toStruct.fields.length) {
                return false;
            }
            for (int i = 0; i < fromStruct.fields.length; i++) {
                StructField l = fromStruct.fields[i];
                StructField r = toStruct.fields[i];
                if (!equalsStructurally(l.dataType, r.dataType, ignoreNullability)) {
                    return false;
                }
                if (!ignoreNullability && l.nullable != r.nullable) {
                    return false;
                }
            }
            return true;
        } else {
            return from.equals(to);
        }
    }

    /**
     * Returns true if the two data types share the same "shape", i.e. the types
     * are the same, but the field names don't need to be the same.
     * Uses default value false for ignoreNullability.
     *
     * @param from the first data type
     * @param to the second data type
     */
    public static boolean equalsStructurally(DataType from, DataType to) {
        return equalsStructurally(from, to, false);
    }
}
