package com.jipple.sql.catalyst.expressions.codegen;

import java.io.Serializable;

/**
 * Java bytecode statistics of a compiled class by Janino.
 */
public class ByteCodeStats implements Serializable {
    public final int maxMethodCodeSize;
    public final int maxConstPoolSize;
    public final int numInnerClasses;

    public ByteCodeStats(int maxMethodCodeSize, int maxConstPoolSize, int numInnerClasses) {
        this.maxMethodCodeSize = maxMethodCodeSize;
        this.maxConstPoolSize = maxConstPoolSize;
        this.numInnerClasses = numInnerClasses;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ByteCodeStats that = (ByteCodeStats) obj;
        return maxMethodCodeSize == that.maxMethodCodeSize
            && maxConstPoolSize == that.maxConstPoolSize
            && numInnerClasses == that.numInnerClasses;
    }

    @Override
    public int hashCode() {
        int result = maxMethodCodeSize;
        result = 31 * result + maxConstPoolSize;
        result = 31 * result + numInnerClasses;
        return result;
    }

    @Override
    public String toString() {
        return "ByteCodeStats(maxMethodCodeSize=" + maxMethodCodeSize
            + ", maxConstPoolSize=" + maxConstPoolSize
            + ", numInnerClasses=" + numInnerClasses + ")";
    }
}
