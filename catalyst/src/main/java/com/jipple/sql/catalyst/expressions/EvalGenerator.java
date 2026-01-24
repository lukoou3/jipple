package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.codegen.GenerateEval;
import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;

public class EvalGenerator extends CodeGeneratorWithInterpretedFallback<Expression, Eval> {
    public static final EvalGenerator INSTANCE = new EvalGenerator();
    private EvalGenerator() {
    }

    public static EvalGenerator get() {
        return INSTANCE;
    }

    @Override
    protected Eval createCodeGeneratedObject(Expression expression) {
        return GenerateEval.get().generate(expression, false /*SQLConf.get.subexpressionEliminationEnabled*/);
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
