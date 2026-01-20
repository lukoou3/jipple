package com.jipple.sql.catalyst.expressions;

import java.util.List;

public abstract class ExpressionsEvaluator {
    public abstract void open(int partitions, int partitionIndex) throws Exception;

    protected void openExprs(List<Expression> exprs, int partitions, int partitionIndex) throws Exception {
        for (Expression expr : exprs) {
            if (expr instanceof RichExpression r) {
                r.open(partitions, partitionIndex);
            }
        }
    }

    public abstract void close() throws Exception;

    protected void closeExprs(List<Expression> exprs) throws Exception {
        for (Expression expr : exprs) {
            if (expr instanceof RichExpression r) {
                r.close();
            }
        }
    }
}
