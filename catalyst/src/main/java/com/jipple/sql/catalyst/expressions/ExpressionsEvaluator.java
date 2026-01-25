package com.jipple.sql.catalyst.expressions;

import java.util.List;

public abstract class ExpressionsEvaluator {
    public void open(int partitions, int partitionIndex) throws Exception {}

    protected final void openExprs(List<Expression> exprs, int partitions, int partitionIndex) throws Exception {
        for (Expression expr : exprs) {
            expr.foreach(e -> {
                if (e instanceof RichExpression r) {
                    try {
                        r.open(partitions, partitionIndex);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    public void close() throws Exception {}

    protected final void closeExprs(List<Expression> exprs) throws Exception {
        for (Expression expr : exprs) {
            expr.foreach(e -> {
                if (e instanceof RichExpression r) {
                    try {
                        r.close();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    // 不需要，都是sql生成Expression
    /*protected List<Expression> prepareExpressions(List<Expression> expressions, boolean subExprEliminationEnabled) {
        List<Expression> cleanedExpressions = expressions.stream().map(e -> e.freshCopyIfContainsStatefulExpression()).collect(Collectors.toList());
        if (subExprEliminationEnabled) {
            //return runtime.proxyExpressions(cleanedExpressions);
            return cleanedExpressions;
        } else {
            return cleanedExpressions;
        }
    }*/
}
