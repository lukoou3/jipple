package com.jipple.sql.catalyst.plans.logical;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.AttributeSeq;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Resolver;
import com.jipple.sql.catalyst.plans.QueryPlan;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class LogicalPlan extends QueryPlan<LogicalPlan> {
    private Boolean _resolved;
    private AttributeSeq _childAttributes;
    private AttributeSeq _outputAttributes;

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
