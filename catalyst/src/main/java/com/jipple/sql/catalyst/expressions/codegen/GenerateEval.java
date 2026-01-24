package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.expressions.BindReferences;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.ExpressionCanonicalizer;
import com.jipple.sql.catalyst.expressions.AttributeSeq;
import com.jipple.sql.catalyst.expressions.Eval;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Generates bytecode that evaluates an Expression on a given input InternalRow.
 */
public class GenerateEval extends CodeGenerator<Expression, Eval> {
    private static final Logger logger = LoggerFactory.getLogger(GenerateEval.class);

    public static final GenerateEval INSTANCE = new GenerateEval();

    private GenerateEval() {
    }

    public static GenerateEval get() {
        return INSTANCE;
    }

    @Override
    protected Expression canonicalize(Expression in) {
        return ExpressionCanonicalizer.canonicalize(in);
    }

    @Override
    protected Expression bind(Expression in, List<Attribute> inputSchema) {
        return BindReferences.bindReference(in, new AttributeSeq(inputSchema));
    }

    public Eval generate(Expression expression, boolean useSubexprElimination) {
        return create(canonicalize(expression), useSubexprElimination);
    }

    @Override
    protected Eval create(Expression expression) {
        return create(expression, false);
    }

    protected Eval create(Expression expression, boolean useSubexprElimination) {
        CodegenContext ctx = newCodeGenContext();
        ExprCode eval = ctx.generateExpressions(List.of(expression), useSubexprElimination).get(0);
        String evalSubexpr = ctx.subexprFunctionsCode();

        String codeBody = CodeGeneratorUtils.template(
                """
                        public SpecificEval generate(Object[] references) {
                          return new SpecificEval(references);
                        }

                        class SpecificEval extends ${evalClassName} {
                          private final Object[] references;
                          ${mutableStates}

                          public SpecificEval(Object[] references) {
                            this.references = references;
                            ${initMutableStates}
                          }

                          public void open(int partitions, int partitionIndex) throws Exception {
                            ${initPartition}
                          }

                          public Object eval(InternalRow ${inputRow}) {
                            ${evalSubexpr}
                            ${evalCode}
                            return ${isNull} ? null : ${value};
                          }

                          public void close() throws Exception {
                            ${closePartition}
                          }

                          ${addedFunctions}
                        }
                        """,
                Map.ofEntries(
                        Map.entry("evalClassName", Eval.class.getName()),
                        Map.entry("mutableStates", ctx.declareMutableStates()),
                        Map.entry("initMutableStates", ctx.initMutableStates()),
                        Map.entry("initPartition", ctx.initPartition()),
                        Map.entry("inputRow", ctx.INPUT_ROW),
                        Map.entry("evalSubexpr", evalSubexpr),
                        Map.entry("evalCode", eval.code.toString()),
                        Map.entry("isNull", eval.isNull.toString()),
                        Map.entry("value", eval.value.toString()),
                        Map.entry("closePartition", ctx.closePartition()),
                        Map.entry("addedFunctions", ctx.declareAddedFunctions())
                )
        );

        CodeAndComment code = CodeFormatter.stripOverlappingComments(
                new CodeAndComment(codeBody, ctx.getPlaceHolderToComments()));
        if (logger.isDebugEnabled()) {
            logger.debug("Generated e '{}':\n{}", expression, CodeFormatter.format(code, -1));
        }

        Tuple2<GeneratedClass, ByteCodeStats> compiled = CodeGeneratorUtils.compile(code);
        return (Eval) compiled._1.generate(ctx.referencesArray());
    }
}
