package com.jipple.sql.catalyst;

import com.jipple.sql.catalyst.expressions.SpecializedGetters;

import java.io.Serializable;

@FunctionalInterface
public interface InternalRowAccessor extends Serializable {
    Object get(SpecializedGetters input, int ordinal);
}
