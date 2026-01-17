package com.jipple.tuple;

import java.util.Objects;

public final class Tuple3<T1, T2, T3> extends Tuple {
    public T1 _1;
    public T2 _2;
    public T3 _3;

    public Tuple3() {}

    public Tuple3(T1 _1, T2 _2, T3 _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 _1, T2 _2, T3 _3) {
        return new Tuple3<>(_1, _2, _3);
    }

    @Override
    public int getArity() {
        return 3;
    }

    @Override
    public Object getField(int pos) {
        switch (pos) {
            case 0:
                return this._1;
            case 1:
                return this._2;
            case 2:
                return this._3;
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
            case 2:
                this._3 = (T3) value;
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
        if (!(o instanceof Tuple3<?, ?, ?> other)) {
            return false;
        }
        return Objects.equals(_1, other._1)
            && Objects.equals(_2, other._2)
            && Objects.equals(_3, other._3);
    }

    @Override
    public int hashCode() {
        int result = (_1 != null) ? _1.hashCode() : 0;
        result = 31 * result + ((_2 != null) ? _2.hashCode() : 0);
        result = 31 * result + ((_3 != null) ? _3.hashCode() : 0);
        return result;
    }
}
