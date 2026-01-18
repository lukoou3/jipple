package com.jipple.tuple;

import java.util.Objects;

public final class Tuple2<T1, T2> extends Tuple {
    public T1 _1;
    public T2 _2;

    public Tuple2() {}

    public Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public static <T1, T2> Tuple2<T1, T2> of(T1 _1, T2 _2) {
        return new Tuple2<>(_1, _2);
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public Object getField(int pos) {
        switch (pos) {
            case 0:
                return this._1;
            case 1:
                return this._2;
            default:
                throw new IndexOutOfBoundsException(String.valueOf(pos));
        }
    }

    @Override
    public void setField(int pos, Object value) {
        switch (pos) {
            case 0:
                this._1 = (T1) value;
                break;
            case 1:
                this._2 = (T2) value;
                break;
            default:
                throw new IndexOutOfBoundsException(String.valueOf(pos));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple2<?, ?> other)) {
            return false;
        }
        return Objects.equals(_1, other._1) && Objects.equals(_2, other._2);
    }

    @Override
    public int hashCode() {
        int result = (_1 != null) ? _1.hashCode() : 0;
        result = 31 * result + ((_2 != null) ? _2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + _1 + "," + _2 + ")";
    }
}
