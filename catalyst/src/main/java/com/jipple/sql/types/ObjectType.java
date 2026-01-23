package com.jipple.sql.types;

/**
 * Represents a JVM object that is passing through Spark SQL expression evaluation.
 */
public class ObjectType extends DataType {
    public final Class<?> cls;

    public ObjectType(Class<?> cls) {
        this.cls = cls;
    }

    @Override
    public int defaultSize() {
        return 4096;
    }

    @Override
    public DataType asNullable() {
        return this;
    }

    @Override
    public String simpleString() {
        return cls.getName();
    }

    @Override
    public boolean acceptsType(DataType other) {
        return other instanceof ObjectType && cls.isAssignableFrom(((ObjectType) other).cls);
    }
}

