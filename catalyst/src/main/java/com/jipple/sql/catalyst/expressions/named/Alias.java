package com.jipple.sql.catalyst.expressions.named;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;

import java.util.Collections;
import java.util.List;

public class Alias extends UnaryExpression implements NamedExpression {
    public final String name;
    public final ExprId exprId;
    public final List<String> qualifier;

    public Alias(Expression child, String name) {
        this(child, name, ExprId.newExprId(), Collections.emptyList());
    }

    public Alias(Expression child, String name, List<String> qualifier) {
        this(child, name, ExprId.newExprId(), qualifier);
    }

    public Alias(Expression child, String name, ExprId exprId, List<String> qualifier) {
        super(child);
        this.name = name;
        this.exprId = exprId;
        this.qualifier = qualifier;
    }

    @Override
    public Object[] args() {
        return new Object[] {child, name, exprId, qualifier};
    }

    // Alias(Generator, xx) need to be transformed into Generate(generator, ...)
    // override lazy val resolved = childrenResolved && checkInputDataTypes().isSuccess && !child.isInstanceOf[Generator]
    @Override
    public DataType dataType() {
        return child.dataType();
    }

    @Override
    public boolean nullable() {
        return child.nullable();
    }

    @Override
    public Object eval(InternalRow input) {
        return child.eval(input);
    }

    /**
     * Just a simple passthrough for code generation.
     */
    @Override
    public ExprCode genCode(CodegenContext ctx) {
        return child.genCode(ctx);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        throw new IllegalStateException("Alias.doGenCode should not be called.");
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new Alias(newChild, name, exprId, qualifier);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ExprId exprId() {
        return exprId;
    }

    @Override
    public List<String> qualifier() {
        return qualifier;
    }

    @Override
    public Attribute toAttribute() {
        if (resolved()) {
            return new AttributeReference(name, child.dataType(), child.nullable(), exprId, qualifier);
        } else {
            return UnresolvedAttribute.quoted(name);
        }
    }

    @Override
    public Alias newInstance() {
        return new Alias(child, name, qualifier);
    }

    /** Used to signal the column used to calculate an eventTime watermark (e.g. a#1-T{delayMs}) */
    private String delaySuffix() {
        return "";
    }

    @Override
    public String toString() {
        return child + " AS " + name + "#" + exprId.id + typeSuffix() + delaySuffix();
    }
}
