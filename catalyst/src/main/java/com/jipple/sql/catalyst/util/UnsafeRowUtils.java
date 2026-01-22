package com.jipple.sql.catalyst.util;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.UnsafeRow;
import com.jipple.sql.types.BooleanType;
import com.jipple.sql.types.CalendarIntervalType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.Decimal;
import com.jipple.sql.types.DecimalType;
import com.jipple.sql.types.FloatType;
import com.jipple.sql.types.IntegerType;
import com.jipple.sql.types.StructField;
import com.jipple.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

public final class UnsafeRowUtils {
    private UnsafeRowUtils() {
    }

    /**
     * Use the following rules to check the integrity of the UnsafeRow:
     * - schema.fields.length == row.numFields should always be true
     * - UnsafeRow.calculateBitSetWidthInBytes(row.numFields) < row.getSizeInBytes should always be
     *   true if the expectedSchema contains at least one field.
     * - For variable-length fields:
     *   - if null bit says it's null, then
     *     - in general the offset-and-size should be zero
     *     - special case: variable-length DecimalType is considered mutable in UnsafeRow, and to
     *       support that, the offset is set to point to the variable-length part like a non-null
     *       value, while the size is set to zero to signal that it's a null value. The offset
     *       may also be set to zero, in which case this variable-length Decimal no longer supports
     *       being mutable in the UnsafeRow.
     *   - otherwise the field is not null, then extract offset and size:
     *   1) 0 <= size < row.getSizeInBytes should always be true. We can be even more precise than
     *      this, where the upper bound of size can only be as big as the variable length part of
     *      the row.
     *   2) offset should be >= fixed sized part of the row.
     *   3) offset + size should be within the row bounds.
     * - For fixed-length fields that are narrower than 8 bytes (boolean/byte/short/int/float), if
     *   null bit says it's null then don't do anything, else:
     *     check if the unused bits in the field are all zeros. The UnsafeRowWriter's write() methods
     *     make this guarantee.
     * - Check the total length of the row.
     *
     * @param row The input UnsafeRow to be validated
     * @param expectedSchema The expected schema that should match with the UnsafeRow
     * @return None if all the checks pass. An error message if the row is not matched with the schema
     */
    private static Option<String> validateStructuralIntegrityWithReasonImpl(
            UnsafeRow row, StructType expectedSchema) {
        if (expectedSchema.fields.length != row.numFields()) {
            return Option.some("Field length mismatch: expected: " + expectedSchema.fields.length
                    + ", actual: " + row.numFields());
        }
        int bitSetWidthInBytes = UnsafeRow.calculateBitSetWidthInBytes(row.numFields());
        int rowSizeInBytes = row.getSizeInBytes();
        if (expectedSchema.fields.length > 0 && bitSetWidthInBytes >= rowSizeInBytes) {
            return Option.some("rowSizeInBytes should not exceed bitSetWidthInBytes, "
                    + "bitSetWidthInBytes: " + bitSetWidthInBytes + ", rowSizeInBytes: " + rowSizeInBytes);
        }
        int varLenFieldsSizeInBytes = 0;
        StructField[] fields = expectedSchema.fields;
        for (int index = 0; index < fields.length; index++) {
            StructField field = fields[index];
            DataType dataType = field.dataType;
            if (!UnsafeRow.isFixedLength(dataType) && !row.isNullAt(index)) {
                OffsetAndSize offsetAndSize = getOffsetAndSize(row, index);
                if (offsetAndSize.size < 0
                        || offsetAndSize.offset < bitSetWidthInBytes + 8 * row.numFields()
                        || offsetAndSize.offset + offsetAndSize.size > rowSizeInBytes) {
                    return Option.some("Variable-length field validation error: field: " + field
                            + ", index: " + index);
                }
                varLenFieldsSizeInBytes += offsetAndSize.size;
            } else if (UnsafeRow.isFixedLength(dataType) && !row.isNullAt(index)) {
                if (dataType instanceof BooleanType) {
                    if ((row.getLong(index) >> 1) != 0L) {
                        return Option.some("Fixed-length field validation error: field: " + field
                                + ", index: " + index);
                    }
                } else if (dataType instanceof IntegerType) {
                    if ((row.getLong(index) >> 32) != 0L) {
                        return Option.some("Fixed-length field validation error: field: " + field
                                + ", index: " + index);
                    }
                } else if (dataType instanceof FloatType) {
                    if ((row.getLong(index) >> 32) != 0L) {
                        return Option.some("Fixed-length field validation error: field: " + field
                                + ", index: " + index);
                    }
                }
            } else if (row.isNullAt(index)) {
                if (dataType instanceof DecimalType && !UnsafeRow.isFixedLength(dataType)) {
                    OffsetAndSize offsetAndSize = getOffsetAndSize(row, index);
                    if (offsetAndSize.size != 0
                            || (offsetAndSize.offset != 0
                            && (offsetAndSize.offset < bitSetWidthInBytes + 8 * row.numFields()
                            || offsetAndSize.offset > rowSizeInBytes))) {
                        return Option.some("Variable-length decimal field special case validation error: "
                                + "field: " + field + ", index: " + index);
                    }
                } else {
                    if (row.getLong(index) != 0L) {
                        return Option.some("Variable-length offset-size validation error: field: "
                                + field + ", index: " + index);
                    }
                }
            }
        }
        if (bitSetWidthInBytes + 8 * row.numFields() + varLenFieldsSizeInBytes > rowSizeInBytes) {
            return Option.some("Row total length invalid: calculated: "
                    + (bitSetWidthInBytes + 8 * row.numFields() + varLenFieldsSizeInBytes)
                    + " rowSizeInBytes: " + rowSizeInBytes);
        }
        return Option.none();
    }

    /**
     * Wrapper of validateStructuralIntegrityWithReasonImpl, add more information for debugging.
     *
     * @param row The input UnsafeRow to be validated
     * @param expectedSchema The expected schema that should match with the UnsafeRow
     * @return None if all the checks pass. An error message if the row is not matched with the schema
     */
    public static Option<String> validateStructuralIntegrityWithReason(
            UnsafeRow row, StructType expectedSchema) {
        return validateStructuralIntegrityWithReasonImpl(row, expectedSchema).map(
                errorMessage -> "Error message is: " + errorMessage + ", UnsafeRow status: "
                        + getStructuralIntegrityStatus(row, expectedSchema));
    }

    public static OffsetAndSize getOffsetAndSize(UnsafeRow row, int index) {
        long offsetAndSize = row.getLong(index);
        int offset = (int) (offsetAndSize >> 32);
        int size = (int) offsetAndSize;
        return new OffsetAndSize(offset, size);
    }

    /**
     * Returns a Boolean indicating whether one should avoid calling
     * UnsafeRow.setNullAt for a field of the given data type.
     * Fields of type DecimalType (with precision
     * greater than Decimal.MAX_LONG_DIGITS) and CalendarIntervalType use
     * pointers into the variable length region, and those pointers should
     * never get zeroed out (setNullAt will zero out those pointers) because UnsafeRow
     * may do in-place update for these 2 types even though they are not primitive.
     *
     * When avoidSetNullAt returns true, callers should not use
     * UnsafeRow#setNullAt for fields of that data type, but instead pass
     * a null value to the appropriate set method, e.g.:
     *
     *   row.setDecimal(ordinal, null, precision)
     *
     * Even though only UnsafeRow has this limitation, it's safe to extend this rule
     * to all subclasses of InternalRow, since you don't always know the concrete type
     * of the row you are dealing with, and all subclasses of InternalRow will
     * handle a null value appropriately.
     */
    public static boolean avoidSetNullAt(DataType dt) {
        if (dt instanceof DecimalType) {
            DecimalType decimalType = (DecimalType) dt;
            return decimalType.precision() > Decimal.MAX_LONG_DIGITS;
        }
        if (dt instanceof CalendarIntervalType) {
            return true;
        }
        return false;
    }

    public static String getStructuralIntegrityStatus(UnsafeRow row, StructType expectedSchema) {
        int minLength = Math.min(row.numFields(), expectedSchema.fields.length);
        List<String> fieldStatusArr = new ArrayList<>(minLength);
        for (int index = 0; index < minLength; index++) {
            StructField field = expectedSchema.fields[index];
            String offsetAndSizeStr = "";
            if (!UnsafeRow.isFixedLength(field.dataType)) {
                OffsetAndSize offsetAndSize = getOffsetAndSize(row, index);
                offsetAndSizeStr = "offset: " + offsetAndSize.offset + ", size: " + offsetAndSize.size;
            }
            fieldStatusArr.add("[UnsafeRowFieldStatus] index: " + index
                    + ", expectedFieldType: " + field.dataType
                    + ", isNull: " + row.isNullAt(index)
                    + ", isFixedLength: " + UnsafeRow.isFixedLength(field.dataType)
                    + ". " + offsetAndSizeStr);
        }

        return "[UnsafeRowStatus] expectedSchema: " + expectedSchema
                + ", expectedSchemaNumFields: " + expectedSchema.fields.length
                + ", numFields: " + row.numFields()
                + ", bitSetWidthInBytes: " + UnsafeRow.calculateBitSetWidthInBytes(row.numFields())
                + ", rowSizeInBytes: " + row.getSizeInBytes()
                + "\nfieldStatus:\n" + String.join("\n", fieldStatusArr);
    }

    public static final class OffsetAndSize {
        public final int offset;
        public final int size;

        public OffsetAndSize(int offset, int size) {
            this.offset = offset;
            this.size = size;
        }
    }
}
