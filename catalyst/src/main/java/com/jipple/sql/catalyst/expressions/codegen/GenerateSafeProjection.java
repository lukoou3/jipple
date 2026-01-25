package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.*;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generates byte code that produces a [[InternalRow]] object (not an [[UnsafeRow]]) that can update
 * itself based on a new input [[InternalRow]] for a fixed set of [[Expression Expressions]].
 */
public class GenerateSafeProjection  extends CodeGenerator<List<Expression>, Projection> {
    private static final Logger logger = LoggerFactory.getLogger(GenerateSafeProjection.class);

    @Override
    protected List<Expression> canonicalize(List<Expression> in) {
        return in.stream().map(ExpressionCanonicalizer::canonicalize).collect(Collectors.toList());
    }

    @Override
    protected List<Expression> bind(List<Expression> in, List<Attribute> inputSchema) {
        return BindReferences.bindReferences(in, new AttributeSeq(inputSchema));
    }

    @Override
    protected Projection create(List<Expression> expressions) {
        CodegenContext ctx = newCodeGenContext();
        ;
        List<String> expressionCodes = IntStream.range(0, expressions.size()).mapToObj(i -> {
            Expression e = expressions.get(i);
            if (e instanceof NoOp) {
                return "";
            } else {
                ExprCode evaluationCode = e.genCode(ctx);
                return evaluationCode.code +
                    CodeGeneratorUtils.template("""
                      if (${evaluationCodeIsNull}) {
                        mutableRow.setNullAt(${i});
                      } else {
                        ${setColumnCode};
                      }
                      """,
                      Map.of(
                          "evaluationCodeIsNull", evaluationCode.isNull,
                          "i", i,
                          "setColumnCode", CodeGeneratorUtils.setColumn("mutableRow", e.dataType(), i, evaluationCode.value.toString())
                      ));
            }
        }).collect(Collectors.toList());

        String allExpressions = ctx.splitExpressionsWithCurrentInputs(expressionCodes);

        String codeBody = CodeGeneratorUtils.template("""
          public java.lang.Object generate(Object[] references) {
            return new SpecificSafeProjection(references);
          }

          class SpecificSafeProjection extends ${projectionClassName} {

            private Object[] references;
            private InternalRow mutableRow;
            ${mutableStates}

            public SpecificSafeProjection(Object[] references) {
              this.references = references;
              mutableRow = (InternalRow) references[references.length - 1];
              ${initMutableStates}
            }

            public void open(int partitions, int partitionIndex) throws Exception {
              ${initPartition}
            }

            public InternalRow apply(InternalRow ${inputRow}) {
              ${allExpressions}
              return mutableRow;
            }

            public void close() throws Exception {
              ${closePartition}
            }

            ${addedFunctions}
          }
        """, Map.ofEntries(
            Map.entry("projectionClassName", Projection.class.getName()),
            Map.entry("mutableStates", ctx.declareMutableStates()),
            Map.entry("initMutableStates", ctx.initMutableStates()),
            Map.entry("initPartition", ctx.initPartition()),
            Map.entry("inputRow", ctx.INPUT_ROW),
            Map.entry("allExpressions", allExpressions),
            Map.entry("closePartition", ctx.closePartition()),
            Map.entry("addedFunctions", ctx.declareAddedFunctions())
        ));

        CodeAndComment code = CodeFormatter.stripOverlappingComments(
            new CodeAndComment(codeBody, ctx.getPlaceHolderToComments()));
        if (logger.isDebugEnabled()) {
            logger.debug("Generated code for '{}':\n{}", expressions.stream().map(Expression::toString).collect(Collectors.joining(",")), CodeFormatter.format(code));
        }

        Tuple2<GeneratedClass, ByteCodeStats> compiled = CodeGeneratorUtils.compile(code);
        InternalRow resultRow = new SpecificInternalRow(expressions.stream().map(Expression::dataType).collect(Collectors.toList()));
        List refs = new ArrayList<>(ctx.references);
        refs.add(resultRow);
        return (Projection) compiled._1.generate(refs.toArray());
    }

}