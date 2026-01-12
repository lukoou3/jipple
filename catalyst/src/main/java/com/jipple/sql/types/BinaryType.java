package com.jipple.sql.types;

public class BinaryType extends AtomicType {

    private BinaryType() {
    }

    /**
     * The default size of a value of the BinaryType is 100 bytes.
     */
    @Override
    public int defaultSize() {
        return 100;
    }

    @Override
    public DataType asNullable() {
        return this;
    }

}
