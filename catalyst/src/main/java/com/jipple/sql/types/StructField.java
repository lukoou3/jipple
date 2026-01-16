package com.jipple.sql.types;

import java.io.Serializable;
import java.util.Objects;

public class StructField implements Serializable {
    public final String name;
    public final DataType dataType;
    public final boolean nullable;

    public StructField(String name, DataType dataType) {
        this(name, dataType, true);
    }

    public StructField(String name, DataType dataType, boolean nullable) {
        this.name = name;
        this.dataType = dataType;
        this.nullable = nullable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dataType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StructField structField = (StructField) o;
        return name.equals(structField.name)
                && dataType.equals(structField.dataType);
    }

    @Override
    public String toString() {
        return "StructField{" +
                "name='" + name + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
