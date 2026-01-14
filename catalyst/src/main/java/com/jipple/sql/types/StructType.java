package com.jipple.sql.types;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StructType extends DataType {
    public final StructField[] fields;

    public StructType(StructField[] fields) {
        this.fields = fields;
    }

    @Override
    public int defaultSize() {
        return Arrays.stream(fields).mapToInt(f -> f.dataType.defaultSize()).sum();
    }

    @Override
    public String simpleString() {
        return String.format("struct<%s>", Arrays.stream(fields).map(f -> f.name + ":" + f.dataType.simpleString()).collect(Collectors.joining(", ")));
    }

    @Override
    public DataType asNullable() {
        return null;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        StructField[] otherFields = ((StructType) o).fields;
        return Arrays.equals(fields, otherFields);
    }

}
