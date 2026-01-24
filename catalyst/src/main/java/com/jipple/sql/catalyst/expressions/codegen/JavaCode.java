package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.types.BooleanType;
import com.jipple.sql.types.DataType;

/**
 * Interface representing an opaque fragment of java code.
 */
public interface JavaCode {
    String code();

    /**
     * Create a java literal.
     */
    static LiteralValue literal(String v, DataType dataType) {
        if (dataType instanceof BooleanType) {
            if ("true".equals(v)) {
                return TrueLiteral.INSTANCE;
            } else if ("false".equals(v)) {
                return FalseLiteral.INSTANCE;
            }
        }
        return new LiteralValue(v, CodeGeneratorUtils.javaClass(dataType));
    }

    /**
     * Create a default literal. This is null for reference types, false for boolean types and
     * -1 for other primitive types.
     */
    static LiteralValue defaultLiteral(DataType dataType) {
        return new LiteralValue(
            CodeGeneratorUtils.defaultValue(dataType, true),
            CodeGeneratorUtils.javaClass(dataType));
    }

    /**
     * Create a local java variable.
     */
    static VariableValue variable(String name, DataType dataType) {
        return variable(name, CodeGeneratorUtils.javaClass(dataType));
    }

    /**
     * Create a local java variable.
     */
    static VariableValue variable(String name, Class<?> javaClass) {
        return new VariableValue(name, javaClass);
    }

    /**
     * Create a local isNull variable.
     */
    static VariableValue isNullVariable(String name) {
        return variable(name, BooleanType.INSTANCE);
    }

    /**
     * Create a global java variable.
     */
    static GlobalValue global(String name, DataType dataType) {
        return global(name, CodeGeneratorUtils.javaClass(dataType));
    }

    /**
     * Create a global java variable.
     */
    static GlobalValue global(String name, Class<?> javaClass) {
        return new GlobalValue(name, javaClass);
    }

    /**
     * Create a global isNull variable.
     */
    static GlobalValue isNullGlobal(String name) {
        return global(name, BooleanType.INSTANCE);
    }

    /**
     * Create an expression fragment.
     */
    static SimpleExprValue expression(String code, DataType dataType) {
        return expression(code, CodeGeneratorUtils.javaClass(dataType));
    }

    /**
     * Create an expression fragment.
     */
    static SimpleExprValue expression(String code, Class<?> javaClass) {
        return new SimpleExprValue(code, javaClass);
    }

    /**
     * Create a isNull expression fragment.
     */
    static SimpleExprValue isNullExpression(String code) {
        return expression(code, BooleanType.INSTANCE);
    }

    /**
     * Create an Inline for Java Class name.
     */
    static Inline javaType(Class<?> javaClass) {
        return new Inline(javaClass.getName());
    }

    /**
     * Create an Inline for Java Type name.
     */
    static Inline javaType(DataType dataType) {
        return new Inline(CodeGeneratorUtils.javaType(dataType));
    }

    /**
     * Create an Inline for boxed Java Type name.
     */
    static Inline boxedType(DataType dataType) {
        return new Inline(CodeGeneratorUtils.boxedType(dataType));
    }
}

