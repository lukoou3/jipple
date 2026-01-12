package com.jipple.sql.types;

public class ArrayType extends DataType {
    public DataType elementType;
    public boolean containsNull;

    public ArrayType(DataType elementType ) {
        this(elementType, true);
    }

    public ArrayType(DataType elementType, boolean containsNull) {
        this.elementType = elementType;
        this.containsNull = containsNull;
    }

    @Override
    public int defaultSize() {
        return 1 * elementType.defaultSize();
    }

    @Override
    public DataType asNullable() {
        return new ArrayType(elementType.asNullable(), true);
    }

    @Override
    public String simpleString() {
        return String.format("array<%s>", elementType.simpleString());
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        ArrayType arrayType = (ArrayType) o;
        return elementType.equals(arrayType.elementType) && containsNull == arrayType.containsNull;
    }

}
