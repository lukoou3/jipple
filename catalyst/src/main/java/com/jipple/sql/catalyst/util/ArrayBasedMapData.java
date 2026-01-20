package com.jipple.sql.catalyst.util;

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

