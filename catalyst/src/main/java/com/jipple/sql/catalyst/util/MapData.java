package com.jipple.sql.catalyst.util;

import com.jipple.sql.types.DataType;

import java.io.Serializable;
import java.util.function.BiConsumer;

public abstract class MapData implements Serializable {

    public abstract int numElements();

    public abstract ArrayData keyArray();

    public abstract ArrayData valueArray();

    public abstract MapData copy();

    public void foreach(DataType keyType, DataType valueType, BiConsumer<Object, Object> f) {
        int length = numElements();
        ArrayData keys = keyArray();
        ArrayData values = valueArray();
        for (int i = 0; i < length; i++) {
            f.accept(keys.get(i, keyType), values.get(i, valueType));
        }
    }
}
