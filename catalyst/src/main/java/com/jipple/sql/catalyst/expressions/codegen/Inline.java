package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A piece of java code snippet inlines all types of input arguments into a string without
 * tracking any reference of JavaCode instances.
 */
public class Inline implements JavaCode {
    private final String codeString;

    public Inline(String codeString) {
        this.codeString = codeString;
    }

    @Override
    public String code() {
        return codeString;
    }

    @Override
    public String toString() {
        return code();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Inline inline = (Inline) obj;
        return codeString != null ? codeString.equals(inline.codeString) : inline.codeString == null;
    }

    @Override
    public int hashCode() {
        return codeString != null ? codeString.hashCode() : 0;
    }
}

