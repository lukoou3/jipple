package com.jipple.sql.catalyst.rules;

import com.jipple.sql.AnalysisException;
import com.jipple.sql.SQLConf;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Resolver;
import com.jipple.sql.catalyst.trees.TreeNode;

import java.util.function.Supplier;

public abstract class Rule<TreeType extends TreeNode>   {
    public final String ruleName = initialRuleName();

    // The integer id of a rule, for pruning unnecessary tree traversals.
    protected RuleId ruleId() {
        return RuleId.of(0);
    }

    public abstract TreeType apply(TreeType plan);

    private String initialRuleName() {
        String className = getClass().getName();
        return className.endsWith("$") ? className.substring(0, className.length() - 1) : className;
    }

    /**
     * The active config object within the current scope.
     * See [[SQLConf.get]] for more information.
     */
    protected SQLConf conf(){
        return SQLConf.get();
    }

    protected Resolver resolver() {
        return Resolver.caseInsensitiveResolution();
    }

    /** Catches any AnalysisExceptions thrown by `f` and attaches `t`'s position if any. */
    protected static <A> A withPosition(TreeNode<?> t, Supplier<A> f) {
        try {
            return f.get();
        } catch (AnalysisException a) {
            throw a.withPosition(t.origin());
        }
    }
}
