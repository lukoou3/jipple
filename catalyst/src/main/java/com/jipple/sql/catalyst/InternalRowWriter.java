package com.jipple.sql.catalyst;

import java.io.Serializable;

@FunctionalInterface
public interface InternalRowWriter extends Serializable {
    void write(InternalRow row, Object v);
}
