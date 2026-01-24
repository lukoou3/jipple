package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.unsafe.types.UTF8String;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;
import java.util.regex.Pattern;

public class RLike extends StringRegexExpression {
    public RLike(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String escape(String v) {
        return v;
    }

    @Override
    public boolean matches(Pattern regex, String str) {
        return regex.matcher(str).find(0); // 包含匹配
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String patternClass = Pattern.class.getName();

        if (right.foldable()) {
            Object rVal = right.eval();
            if (rVal != null) {
                String regexStr = StringEscapeUtils.escapeJava(((UTF8String) rVal).toString());
                String pattern = ctx.addMutableState(patternClass, "patternRLike", value ->
                        CodeGeneratorUtils.template(
                                "${value} = ${patternClass}.compile(\"${regexStr}\");",
                                Map.of(
                                        "value", value,
                                        "patternClass", patternClass,
                                        "regexStr", regexStr
                                )
                        ));

                // We don't use nullSafeCodeGen here because we don't want to re-evaluate right again.
                ExprCode eval = left.genCode(ctx);
                return ev.copy(Block.block(
                        """
                                ${evalCode}
                                boolean ${isNull} = ${evalIsNull};
                                ${javaType} ${value} = ${defaultValue};
                                if (!${isNull}) {
                                  ${value} = ${pattern}.matcher(${evalValue}.toString()).find(0);
                                }
                                """,
                        Map.of(
                                "evalCode", eval.code,
                                "isNull", ev.isNull,
                                "evalIsNull", eval.isNull,
                                "javaType", CodeGeneratorUtils.javaType(dataType()),
                                "value", ev.value,
                                "defaultValue", CodeGeneratorUtils.defaultValue(dataType()),
                                "pattern", pattern,
                                "evalValue", eval.value
                        )
                ));
            } else {
                return ev.copy(Block.block(
                        """
                                boolean ${isNull} = true;
                                ${javaType} ${value} = ${defaultValue};
                                """,
                        Map.of(
                                "isNull", ev.isNull,
                                "javaType", CodeGeneratorUtils.javaType(dataType()),
                                "value", ev.value,
                                "defaultValue", CodeGeneratorUtils.defaultValue(dataType())
                        )
                ));
            }
        } else {
            String rightStr = ctx.freshName("rightStr");
            String pattern = ctx.freshName("pattern");
            return nullSafeCodeGen(ctx, ev, (eval1, eval2) ->
                    CodeGeneratorUtils.template(
                            """
                                    String ${rightStr} = ${eval2}.toString();
                                    ${patternClass} ${pattern} = ${patternClass}.compile(${rightStr});
                                    ${value} = ${pattern}.matcher(${eval1}.toString()).find(0);
                                    """,
                            Map.of(
                                    "rightStr", rightStr,
                                    "eval2", eval2,
                                    "patternClass", patternClass,
                                    "pattern", pattern,
                                    "value", ev.value,
                                    "eval1", eval1
                            )
                    ));
        }
    }

    @Override
    public String toString() {
        return String.format("RLIKE(%s, %s)", left, right);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new RLike(newLeft, newRight);
    }
}
