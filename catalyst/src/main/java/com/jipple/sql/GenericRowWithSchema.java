package com.jipple.sql;

import com.jipple.sql.types.StructType;

/**
 * A row implementation with schema information.
 */
public class GenericRowWithSchema extends GenericRow {
    private final StructType schema;

    /** No-arg constructor for serialization. */
    protected GenericRowWithSchema() {
        this(null, null);
    }

    public GenericRowWithSchema(Object[] values, StructType schema) {
        super(values);
        this.schema = schema;
    }

    @Override
    public StructType schema() {
        return schema;
    }

    @Override
    public int fieldIndex(String name) {
        return schema.fieldIndex(name);
    }
}

