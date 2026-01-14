package com.jipple.sql.catalyst.types;

import com.jipple.unsafe.types.UTF8String;

import java.util.Comparator;


public class PhysicalStringType extends PhysicalDataType<UTF8String> {
    @Override
    public Comparator<UTF8String> comparator() {
        return Comparator.naturalOrder();
    }
    
    @Override
    public Class<UTF8String> internalTypeClass() {
        return UTF8String.class;
    }
}

