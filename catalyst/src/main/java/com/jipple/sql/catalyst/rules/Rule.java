package com.jipple.sql.catalyst.rules;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.trees.TreeNode;

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
}
