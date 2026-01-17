package com.jipple.tuple;

import java.util.Objects;

public final class Tuple5<T1, T2, T3, T4, T5> extends Tuple {
    public T1 _1;
    public T2 _2;
    public T3 _3;
    public T4 _4;
    public T5 _5;

    public Tuple5() {}

    public Tuple5(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
    }

    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(
        T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
        return new Tuple5<>(_1, _2, _3, _4, _5);
    }

    @Override
    public int getArity() {
        return 5;
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
            case 3:
                return this._4;
            case 4:
                return this._5;
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
            case 3:
                this._4 = (T4) value;
                break;
            case 4:
                this._5 = (T5) value;
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
        if (!(o instanceof Tuple5<?, ?, ?, ?, ?> other)) {
            return false;
        }
        return Objects.equals(_1, other._1)
            && Objects.equals(_2, other._2)
            && Objects.equals(_3, other._3)
            && Objects.equals(_4, other._4)
            && Objects.equals(_5, other._5);
    }

    @Override
    public int hashCode() {
        int result = (_1 != null) ? _1.hashCode() : 0;
        result = 31 * result + ((_2 != null) ? _2.hashCode() : 0);
        result = 31 * result + ((_3 != null) ? _3.hashCode() : 0);
        result = 31 * result + ((_4 != null) ? _4.hashCode() : 0);
        result = 31 * result + ((_5 != null) ? _5.hashCode() : 0);
        return result;
    }
}
