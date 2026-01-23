package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A wrapper for generated class, defines a `generate` method so that we can pass extra objects
 * into generated class.
 */
public abstract class GeneratedClass {
    public abstract Object generate(Object[] references);
}

