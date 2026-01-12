package com.jipple.sql.types;

import java.io.Serializable;

public class StructField implements Serializable {
    public final String name;
    public final DataType dataType;

    public StructField(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
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
