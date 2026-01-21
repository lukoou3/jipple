package com.jipple.sql;

import com.jipple.sql.errors.DataTypeErrors;
import com.jipple.sql.types.StructType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents one row of output from a relational operator.  Allows both generic access by ordinal,
 * which will incur boxing overhead for primitives, as well as native primitive access.
 *
 * It is invalid to use the native primitive interface to retrieve a value that is null, instead a
 * user must check `isNullAt` before attempting to retrieve a value that might be null.
 *
 * To create a new Row, use `Row.of()` in Java or `Row.apply()` in Scala.
 *
 * @since 1.3.0
 */
public abstract class Row implements Serializable {
    
    /**
     * This method can be used to construct a [[Row]] with the given values.
     */
    public static Row of(Object... values) {
        return new GenericRow(values);
    }

    /**
     * This method can be used to construct a [[Row]] from a List of values.
     */
    public static Row fromList(List<Object> values) {
        return new GenericRow(values.toArray());
    }

    /** Returns an empty row. */
    public static final Row EMPTY = Row.of();

    /** Number of elements in the Row. */
    public int size() {
        return length();
    }

    /** Number of elements in the Row. */
    public abstract int length();

    /**
     * Schema for the row.
     */
    public StructType schema() {
        return null;
    }

    /**
     * Returns the value at position i. If the value is null, null is returned.
     */
    public Object apply(int i) {
        return get(i);
    }

    /**
     * Returns the value at position i. If the value is null, null is returned. The following
     * is a mapping between Ripple SQL types and return types:
     *
     * {{{
     *   BooleanType -> java.lang.Boolean
     *   ByteType -> java.lang.Byte
     *   ShortType -> java.lang.Short
     *   IntegerType -> java.lang.Integer
     *   LongType -> java.lang.Long
     *   FloatType -> java.lang.Float
     *   DoubleType -> java.lang.Double
     *   StringType -> String
     *   DecimalType -> java.math.BigDecimal
     *
     *   DateType -> java.sql.Date if spark.sql.datetime.java8API.enabled is false
     *   DateType -> java.time.LocalDate if spark.sql.datetime.java8API.enabled is true
     *
     *   TimestampType -> java.sql.Timestamp if spark.sql.datetime.java8API.enabled is false
     *   TimestampType -> java.time.Instant if spark.sql.datetime.java8API.enabled is true
     *
     *   BinaryType -> byte array
     *   ArrayType -> java.util.List (use getList for java.util.List)
     *   MapType -> java.util.Map (use getJavaMap for java.util.Map)
     *   StructType -> com.ripple.sql.Row
     * }}}
     */
    public abstract Object get(int i);

    /** Checks whether the value at position i is null. */
    public boolean isNullAt(int i) {
        return get(i) == null;
    }

    public abstract void setNullAt(int i);

    /**
     * Updates the value at column `i`. Note that after updating, the given value will be kept in this
     * row, and the caller side should guarantee that this value won't be changed afterwards.
     */
    public abstract void update(int i, Object value);

    /**
     * Returns the value at position i as a primitive boolean.
     *
     * @throws ClassCastException when data type does not match.
     * @throws NullPointerException when value is null.
     */
    public boolean getBoolean(int i) {
        if (isNullAt(i)) {
            throw DataTypeErrors.valueIsNullError(i);
        }
        return getAs(i);
    }

    /**
     * Returns the value at position i as a primitive byte.
     *
     * @throws ClassCastException when data type does not match.
     * @throws NullPointerException when value is null.
     */
    public byte getByte(int i) {
        if (isNullAt(i)) {
            throw DataTypeErrors.valueIsNullError(i);
        }
        return getAs(i);
    }

    /**
     * Returns the value at position i as a primitive short.
     *
     * @throws ClassCastException when data type does not match.
     * @throws NullPointerException when value is null.
     */
    public short getShort(int i) {
        if (isNullAt(i)) {
            throw DataTypeErrors.valueIsNullError(i);
        }
        return getAs(i);
    }

    /**
     * Returns the value at position i as a primitive int.
     *
     * @throws ClassCastException when data type does not match.
     * @throws NullPointerException when value is null.
     */
    public int getInt(int i) {
        if (isNullAt(i)) {
            throw DataTypeErrors.valueIsNullError(i);
        }
        return getAs(i);
    }

    /**
     * Returns the value at position i as a primitive long.
     *
     * @throws ClassCastException when data type does not match.
     * @throws NullPointerException when value is null.
     */
    public long getLong(int i) {
        if (isNullAt(i)) {
            throw DataTypeErrors.valueIsNullError(i);
        }
        return getAs(i);
    }

    /**
     * Returns the value at position i as a primitive float.
     *
     * @throws ClassCastException when data type does not match.
     * @throws NullPointerException when value is null.
     */
    public float getFloat(int i) {
        if (isNullAt(i)) {
            throw DataTypeErrors.valueIsNullError(i);
        }
        return getAs(i);
    }

    /**
     * Returns the value at position i as a primitive double.
     *
     * @throws ClassCastException when data type does not match.
     * @throws NullPointerException when value is null.
     */
    public double getDouble(int i) {
        if (isNullAt(i)) {
            throw DataTypeErrors.valueIsNullError(i);
        }
        return getAs(i);
    }

    /**
     * Returns the value at position i as a String object.
     *
     * @throws ClassCastException when data type does not match.
     */
    public String getString(int i) {
        return getAs(i);
    }

    /**
     * Returns the value at position i of decimal type as java.math.BigDecimal.
     *
     * @throws ClassCastException when data type does not match.
     */
    public BigDecimal getDecimal(int i) {
        return getAs(i);
    }

    /**
     * Returns the value at position i of date type as java.sql.Date.
     *
     * @throws ClassCastException when data type does not match.
     */
    public java.sql.Date getDate(int i) {
        return getAs(i);
    }

    /**
     * Returns the value at position i of date type as java.time.LocalDate.
     *
     * @throws ClassCastException when data type does not match.
     */
    public java.time.LocalDate getLocalDate(int i) {
        return getAs(i);
    }

    /**
     * Returns the value at position i of date type as java.sql.Timestamp.
     *
     * @throws ClassCastException when data type does not match.
     */
    public java.sql.Timestamp getTimestamp(int i) {
        return getAs(i);
    }

    /**
     * Returns the value at position i of date type as java.time.Instant.
     *
     * @throws ClassCastException when data type does not match.
     */
    public java.time.Instant getInstant(int i) {
        return getAs(i);
    }

    /**
     * Returns the value at position i of array type as `java.util.List`.
     *
     * @throws ClassCastException when data type does not match.
     */
    public <T> List<T> getList(int i) {
        Object value = get(i);
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            return (List<T>) value;
        }
        if (value instanceof Collection) {
            return new ArrayList<>((Collection<T>) value);
        }
        throw new ClassCastException("Cannot cast " + value.getClass() + " to List");
    }

    /**
     * Returns the value at position i of map type as a `java.util.Map`.
     *
     * @throws ClassCastException when data type does not match.
     */
    public <K, V> Map<K, V> getJavaMap(int i) {
        Object value = get(i);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            return (Map<K, V>) value;
        }
        throw new ClassCastException("Cannot cast " + value.getClass() + " to Map");
    }

    /**
     * Returns the value at position i of struct type as a [[Row]] object.
     *
     * @throws ClassCastException when data type does not match.
     */
    public Row getStruct(int i) {
        return getAs(i);
    }

    /**
     * Returns the value at position i.
     * For primitive types if value is null it returns 'zero value' specific for primitive
     * i.e. 0 for Int - use isNullAt to ensure that value is not null
     *
     * @throws ClassCastException when data type does not match.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAs(int i) {
        return (T) get(i);
    }

    /**
     * Returns the value of a given fieldName.
     * For primitive types if value is null it returns 'zero value' specific for primitive
     * i.e. 0 for Int - use isNullAt to ensure that value is not null
     *
     * @throws UnsupportedOperationException when schema is not defined.
     * @throws IllegalArgumentException when fieldName do not exist.
     * @throws ClassCastException when data type does not match.
     */
    public <T> T getAs(String fieldName) {
        return getAs(fieldIndex(fieldName));
    }

    /**
     * Returns the index of a given field name.
     *
     * @throws UnsupportedOperationException when schema is not defined.
     * @throws IllegalArgumentException when a field `name` does not exist.
     */
    public int fieldIndex(String name) {
        throw DataTypeErrors.fieldIndexOnRowWithoutSchemaError();
    }

    /**
     * Returns a Map consisting of names and values for the requested fieldNames
     * For primitive types if value is null it returns 'zero value' specific for primitive
     * i.e. 0 for Int - use isNullAt to ensure that value is not null
     *
     * @throws UnsupportedOperationException when schema is not defined.
     * @throws IllegalArgumentException when fieldName do not exist.
     * @throws ClassCastException when data type does not match.
     */
    public <T> Map<String, T> getValuesMap(List<String> fieldNames) {
        Map<String, T> result = new LinkedHashMap<>();
        for (String name : fieldNames) {
            result.put(name, getAs(name));
        }
        return result;
    }

    @Override
    public String toString() {
        return mkString("[", ",", "]");
    }

    /**
     * Make a copy of the current [[Row]] object.
     */
    public abstract Row copy();

    /** Returns true if there are any NULL values in this row. */
    public boolean anyNull() {
        int len = length();
        for (int i = 0; i < len; i++) {
            if (isNullAt(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Row)) {
            return false;
        }
        Row other = (Row) o;

        if (other == null) {
            return false;
        }

        if (length() != other.length()) {
            return false;
        }

        int len = length();
        for (int i = 0; i < len; i++) {
            if (isNullAt(i) != other.isNullAt(i)) {
                return false;
            }
            if (!isNullAt(i)) {
                Object o1 = get(i);
                Object o2 = other.get(i);
                if (o1 instanceof byte[]) {
                    if (!(o2 instanceof byte[]) ||
                            !Arrays.equals((byte[]) o1, (byte[]) o2)) {
                        return false;
                    }
                } else if (o1 instanceof Float && Float.isNaN((Float) o1)) {
                    if (!(o2 instanceof Float) || !Float.isNaN((Float) o2)) {
                        return false;
                    }
                } else if (o1 instanceof Double && Double.isNaN((Double) o1)) {
                    if (!(o2 instanceof Double) || !Double.isNaN((Double) o2)) {
                        return false;
                    }
                } else if (o1 instanceof BigDecimal && o2 instanceof BigDecimal) {
                    if (((BigDecimal) o1).compareTo((BigDecimal) o2) != 0) {
                        return false;
                    }
                } else if (!Objects.equals(o1, o2)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // Using a simple hash code implementation similar to Scala's Seq
        int result = 1;
        int len = length();
        for (int i = 0; i < len; i++) {
            Object element = get(i);
            int elementHash = element == null ? 0 : element.hashCode();
            result = 31 * result + elementHash;
        }
        return result;
    }

    /**
     * Return a List representing the row. Elements are placed in the same order in the List.
     */
    public List<Object> toList() {
        int n = length();
        List<Object> values = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            values.add(get(i));
        }
        return values;
    }

    /** Displays all elements of this sequence in a string (without a separator). */
    public String mkString() {
        return mkString("", "", "");
    }

    /** Displays all elements of this sequence in a string using a separator string. */
    public String mkString(String sep) {
        return mkString("", sep, "");
    }

    /**
     * Displays all elements of this traversable or iterator in a string using
     * start, end, and separator strings.
     */
    public String mkString(String start, String sep, String end) {
        int n = length();
        StringBuilder builder = new StringBuilder();
        builder.append(start);
        if (n > 0) {
            builder.append(get(0));
            for (int i = 1; i < n; i++) {
                builder.append(sep);
                builder.append(get(i));
            }
        }
        builder.append(end);
        return builder.toString();
    }

}

