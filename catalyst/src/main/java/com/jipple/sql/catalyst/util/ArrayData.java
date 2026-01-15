package com.jipple.sql.catalyst.util;

import com.jipple.sql.catalyst.expressions.SpecializedGetters;

import java.io.Serializable;

public abstract class ArrayData implements SpecializedGetters, Serializable {
    public abstract int numElements();

}
