package com.jipple.sql.catalyst.types;

import java.util.Comparator;


public abstract class PhysicalDataType<T> {
    public abstract Comparator<T> comparator();
    public abstract Class<T> internalTypeClass();
}

