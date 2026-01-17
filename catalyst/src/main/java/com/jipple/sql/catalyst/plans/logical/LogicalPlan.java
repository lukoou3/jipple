package com.jipple.sql.catalyst.plans.logical;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.AttributeSeq;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Resolver;
import com.jipple.sql.catalyst.plans.QueryPlan;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class LogicalPlan extends QueryPlan<LogicalPlan> {
    private Boolean _resolved;
    private AttributeSeq _childAttributes;
    private AttributeSeq _outputAttributes;
    private boolean _analyzed = false;


    public void setAnalyzed() {
        if (!_analyzed) {
            _analyzed = true;
            children().forEach(LogicalPlan::setAnalyzed);
        }
    }

    public boolean analyzed() {
        return _analyzed;
    }

    public boolean resolved() {
        if (_resolved == null) {
            _resolved = expressions().stream().allMatch(Expression::resolved) && childrenResolved();
        }
        return _resolved;
    }

    public boolean childrenResolved() {
        return children().stream().allMatch(LogicalPlan::resolved);
    }

    private AttributeSeq childAttributes() {
        if (_childAttributes == null) {
            _childAttributes = new AttributeSeq(children().stream().flatMap(p -> p.output().stream()).collect(Collectors.toList()));
        }
        return _childAttributes;
    }

    private AttributeSeq outputAttributes() {
        if (_outputAttributes == null) {
            _outputAttributes = new AttributeSeq(output());
        }
        return _outputAttributes;
    }

    /**
     * Recursively transforms the expressions of a tree, skipping nodes that have already
     * been analyzed.
     */
    public LogicalPlan resolveExpressionsUp(Function<Expression, Expression> f) {
        return resolveOperatorsUp(p -> p.transformExpressions(f));
    }

    /**
     * Returns a copy of this node where `rule` has been recursively applied first to all of its
     * children and then itself (post-order, bottom-up). When `rule` does not apply to a given node,
     * it is left unchanged.  This function is similar to `transformUp`, but skips sub-trees that
     * have already been marked as analyzed.
     *
     * @param rule the function use to transform this nodes children
     */
    public final LogicalPlan resolveOperatorsUp(Function<LogicalPlan, LogicalPlan> rule) {
        if (analyzed()) {
            return this;
        }
        LogicalPlan afterRuleOnChildren = mapChildren(x -> x.resolveOperatorsUp(rule));
        if (this.fastEquals(afterRuleOnChildren)) {
            return rule.apply(this);
        } else {
            return rule.apply(afterRuleOnChildren);
        }
    }

    /**
     * Optionally resolves the given strings to a [[NamedExpression]] using the input from all child
     * nodes of this LogicalPlan. The attribute is expressed as
     * string in the following form: `[scope].AttributeName.[nested].[fields]...`.
     */
    public Option<Expression> resolveChildren(List<String> nameParts, Resolver resolver) {
        return childAttributes().resolve(nameParts, resolver);
    }

    /**
     * Optionally resolves the given strings to a [[NamedExpression]] based on the output of this
     * LogicalPlan. The attribute is expressed as string in the following form:
     * `[scope].AttributeName.[nested].[fields]...`.
     */
    public Option<Expression> resolve(List<String> nameParts, Resolver resolver) {
        return outputAttributes().resolve(nameParts, resolver);
    }

    @Override
    protected String statePrefix() {
        return !resolved() ? "'" : super.statePrefix();
    }

    /**
     * Create a plan using the block of code when the given context exists. Otherwise return the
     * original plan.
     */
    public LogicalPlan optional(Object ctx, Supplier<LogicalPlan> f) {
        return ctx != null ? f.get() : this;
    }


    /**
     * Map a [[LogicalPlan]] to another [[LogicalPlan]] if the passed context exists using the
     * passed function. The original plan is returned when the context does not exist.
     */
    public <C> LogicalPlan optionalMap(C ctx, BiFunction<C, LogicalPlan, LogicalPlan> f) {
        return ctx != null ? f.apply(ctx, this) : this;
    }

}
