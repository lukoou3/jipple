package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;

public class EvalGenerator extends CodeGeneratorWithInterpretedFallback<Expression, Eval> {
    @Override
    protected Eval createCodeGeneratedObject(Expression expression) {
        return null;
    }

    @Override
    protected Eval createInterpretedObject(Expression expression) {
        return new InterpretedEval(expression);
    }

    /**
     * Returns a BaseEval for an Expression, which will be bound to `inputSchema`.
     */
    public Eval create(Expression expression, List<Attribute> inputSchema) {
        return createObject(BindReferences.bindReference(expression, new AttributeSeq(inputSchema)));
    }

    /**
     * Returns a BaseEval for a given bound Expression.
     */
    public Eval create(Expression expression) {
        return createObject(expression);
    }
}
