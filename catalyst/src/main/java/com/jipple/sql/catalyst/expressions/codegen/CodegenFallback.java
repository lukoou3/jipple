package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.LeafExpression;
import com.jipple.sql.catalyst.expressions.RichExpression;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A trait that can be used to provide a fallback mode for expression code generation.
 */
public interface CodegenFallback {
    static ExprCode doGenCode(Expression self, CodegenContext ctx, ExprCode ev) {
        String input = self instanceof LeafExpression ? "null" : ctx.INPUT_ROW;
        int idx = ctx.references.size();
        ctx.references.add(self);
        AtomicInteger childIndex = new AtomicInteger(idx);
        String exprRef = CodeGeneratorUtils.template(
                "((Expression) references[${idx}])",
                java.util.Map.of("idx", idx)
        );
        self.foreach(node -> {
            if (node instanceof RichExpression richExpression) {
                ctx.references.add(richExpression);
                int currentIndex = childIndex.incrementAndGet();
                String richRef = CodeGeneratorUtils.template(
                        "((RichExpression) references[${idx}])",
                        java.util.Map.of("idx", currentIndex)
                );
                ctx.addPartitionInitializationStatement(CodeGeneratorUtils.template(
                        """
                                ${richExpression}.open(partitions, partitionIndex);
                                """,
                        java.util.Map.of("richExpression", richRef)
                ));
                ctx.addPartitionClosureStatement(CodeGeneratorUtils.template(
                        "${richExpression}.close();",
                        java.util.Map.of("richExpression", richRef)
                ));
            }
        });

        String objectTerm = ctx.freshName("obj");
        Block placeHolder = ctx.registerComment(self.toString());
        String javaType = CodeGeneratorUtils.javaType(self.dataType());
        if (self.nullable()) {
            return ev.copy(Block.block(
                    """
                            ${placeHolder}
                            Object ${objectTerm} = ${exprRef}.eval(${input});
                            boolean ${isNull} = ${objectTerm} == null;
                            ${javaType} ${value} = ${defaultValue};
                            if (!${isNull}) {
                              ${value} = (${boxedType}) ${objectTerm};
                            }
                            """,
                    java.util.Map.ofEntries(
                            java.util.Map.entry("placeHolder", placeHolder),
                            java.util.Map.entry("objectTerm", objectTerm),
                            java.util.Map.entry("exprRef", exprRef),
                            java.util.Map.entry("input", input),
                            java.util.Map.entry("isNull", ev.isNull),
                            java.util.Map.entry("javaType", javaType),
                            java.util.Map.entry("value", ev.value),
                            java.util.Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(self.dataType())),
                            java.util.Map.entry("boxedType", CodeGeneratorUtils.boxedType(self.dataType()))
                    )
            ));
        }
        return ev.copy(
                Block.block(
                        """
                                ${placeHolder}
                                Object ${objectTerm} = ${exprRef}.eval(${input});
                                ${javaType} ${value} = (${boxedType}) ${objectTerm};
                                """,
                        java.util.Map.ofEntries(
                                java.util.Map.entry("placeHolder", placeHolder),
                                java.util.Map.entry("objectTerm", objectTerm),
                                java.util.Map.entry("exprRef", exprRef),
                                java.util.Map.entry("input", input),
                                java.util.Map.entry("javaType", javaType),
                                java.util.Map.entry("value", ev.value),
                                java.util.Map.entry("boxedType", CodeGeneratorUtils.boxedType(self.dataType()))
                        )
                ),
                FalseLiteral.INSTANCE
        );
    }

}
