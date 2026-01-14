package com.jipple.sql.types;

import java.util.Objects;

public class MapType extends DataType {
    public final DataType keyType;
    public final DataType valueType;
    public final boolean valueContainsNull;

    public MapType(DataType keyType, DataType valueType, boolean valueContainsNull) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.valueContainsNull = valueContainsNull;
    }

    @Override
    public int defaultSize() {
        return 1 * (keyType.defaultSize() + valueType.defaultSize());
    }

    @Override
    public DataType asNullable() {
        return new MapType(keyType.asNullable(), valueType.asNullable(), true);
    }

    @Override
    public String simpleString() {
        return "map<" + keyType.simpleString() + ", " + valueType.simpleString() + ">";
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyType, valueType, valueContainsNull);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        MapType other = (MapType) o;
        return keyType.equals(other.keyType) && valueType.equals(other.valueType) && valueContainsNull == other.valueContainsNull;
    }
}
