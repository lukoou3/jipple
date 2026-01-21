package com.jipple.sql.catalyst.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A MapData implementation backed by two ArrayData (keys and values).
 */
public class ArrayBasedMapData extends MapData {
    private final ArrayData keyArray;
    private final ArrayData valueArray;

    public ArrayBasedMapData(ArrayData keyArray, ArrayData valueArray) {
        this.keyArray = keyArray;
        this.valueArray = valueArray;
    }

    /**
     * Creates an ArrayBasedMapData from a Map, applying key and value conversion functions.
     */
    public static <K, V> ArrayBasedMapData fromMap(
            Map<K, V> map,
            Function<Object, Object> keyFunction,
            Function<Object, Object> valueFunction) {
        if (map == null || map.isEmpty()) {
            return new ArrayBasedMapData(new GenericArrayData(new Object[0]), new GenericArrayData(new Object[0]));
        }
        
        Object[] keys = new Object[map.size()];
        Object[] values = new Object[map.size()];
        int i = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keys[i] = keyFunction.apply(entry.getKey());
            values[i] = valueFunction.apply(entry.getValue());
            i++;
        }
        return new ArrayBasedMapData(new GenericArrayData(keys), new GenericArrayData(values));
    }

    @Override
    public int numElements() {
        return keyArray.numElements();
    }

    @Override
    public ArrayData keyArray() {
        return keyArray;
    }

    @Override
    public ArrayData valueArray() {
        return valueArray;
    }

    @Override
    public MapData copy() {
        return new ArrayBasedMapData(keyArray.copy(), valueArray.copy());
    }
}

