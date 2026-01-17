package com.jipple.tuple;

import java.io.Serializable;

public abstract class Tuple implements Serializable {
    public abstract int getArity();
    public abstract Object getField(int pos);
    public abstract void setField(int pos, Object value);
}
