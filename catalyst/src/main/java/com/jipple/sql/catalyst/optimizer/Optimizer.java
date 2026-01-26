package com.jipple.sql.catalyst.optimizer;

import com.jipple.sql.catalyst.optimizer.rule.*;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.RuleExecutor;

import java.util.List;

public class Optimizer extends RuleExecutor<LogicalPlan> {
    protected FixedPoint fixedPoint(){
        // TODO: get conf
        return new FixedPoint(
                100,
                false,
                "jipple.sql.optimizer.maxIterations"
        );
    }

    private List<Batch> defaultBatches() {
        return List.of(
                new Batch("Operator Optimization", fixedPoint(),
                        new OptimizeIn(),
                        new ConstantFolding(),
                        new LikeSimplification()
                ),
                new Batch("Finish Analysis", Once,
                    new EliminateSubqueryAliases(),
                    new ReplaceExpressions()
                )
        );
    }

    @Override
    protected List<Batch> batches() {
        return defaultBatches();
    }
}
