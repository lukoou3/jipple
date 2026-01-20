package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;

import java.util.List;

public class InterpretedEval extends Eval {
    private final Expression expression;

    public InterpretedEval(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void open(int partitions, int partitionIndex) throws Exception {
        openExprs(List.of(expression), partitions, partitionIndex);
    }

    @Override
    public Object eval(InternalRow r) {
        return expression.eval(r);
    }

    @Override
    public void close() throws Exception {
        closeExprs(List.of(expression));
    }
}
