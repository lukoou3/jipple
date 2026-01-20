package com.jipple.sql.catalyst.expressions;

/**
 * An base interface for all rich expressions. This class defines methods for the life cycle of the expressions
 */
public interface RichExpression {
    void open(int partitions, int partitionIndex) throws Exception;
    void close() throws Exception;
}
