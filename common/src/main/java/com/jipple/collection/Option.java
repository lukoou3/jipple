package com.jipple.collection;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Option<A> implements Serializable {
    private final static None NONE = new None();

    public static <T> None<T> none() {
        return (None<T>) NONE;
    }

    public static <T> None<T> empty() {
        return none();
    }

    public static <T> Some<T> some(T x) {
        return new Some<>(x);
    }

    public static <T> Option<T> option(T x) {
        return x == null ? NONE : new Some<>(x);
    }

    public static <T> Option<T> of(T x) {
        return option(x);
    }

    public abstract boolean isEmpty();

    public boolean isDefined() {
        return !isEmpty();
    }

    public abstract A get();

    public A getOrElse(A defaultValue) {
        return isEmpty() ? defaultValue : get();
    }

    public A getOrElseGet(Supplier<? extends A> supplier) {
        return isEmpty() ? supplier.get() : get();
    }

    public Option<A> orElse(Option<A> alternative) {
        return isEmpty() ? alternative : this;
    }

    public Option<A> orElseGet(Supplier<? extends Option<A>> supplier) {
        return isEmpty() ? supplier.get() : this;
    }

    public final <B> Option<B> map(Function<? super A, ? extends B> f) {
        if (isEmpty()) {
            return NONE;
        } else {
            return new Some<>(f.apply(get()));
        }
    }

    public final void forEach(Consumer<? super A> f) {
        if (!isEmpty()) {
            f.accept(get());
        }
    }

    public static final class Some<A> extends Option<A> {
        public final A value;

        private Some(A value) {
            this.value = value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public A get() {
            return value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return value.equals(((Some) o).value);
        }
    }

    public static final class None<A> extends Option<A> {

        private None() {
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public A get() {
            throw new NoSuchElementException("None.get");
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof None;
        }

    }
}
