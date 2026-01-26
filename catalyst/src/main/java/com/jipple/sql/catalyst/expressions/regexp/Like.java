package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.StringUtils;
import com.jipple.unsafe.types.UTF8String;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;
import java.util.regex.Pattern;

public class Like extends StringRegexExpression {
    public final char escapeChar;

    public Like(Expression left, Expression right) {
        this(left, right, '\\');
    }

    public Like(Expression left, Expression right, char escapeChar) {
        super(left, right);
        this.escapeChar = escapeChar;
    }

    @Override
    public Object[] args() {
        return new Object[] { left, right, escapeChar };
    }

    @Override
    public String escape(String v) {
        return StringUtils.escapeLikeRegex(v, escapeChar);
    }

    @Override
    public boolean matches(Pattern regex, String str) {
        return regex.matcher(str).matches(); // 完全匹配
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String patternClass = Pattern.class.getName();
        String escapeFunc = StringUtils.class.getName() + ".escapeLikeRegex";

        if (right.foldable()) {
            Object rVal = right.eval();
            if (rVal != null) {
                String regexStr = StringEscapeUtils.escapeJava(escape(((UTF8String) rVal).toString()));
                String pattern = ctx.addMutableState(patternClass, "patternLike", value ->
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
                                  ${value} = ${pattern}.matcher(${evalValue}.toString()).matches();
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
            String pattern = ctx.freshName("pattern");
            String rightStr = ctx.freshName("rightStr");
            // We need to escape the escapeChar to make sure the generated code is valid.
            // Otherwise we'll hit org.codehaus.commons.compiler.CompileException.
            String escapedEscapeChar = StringEscapeUtils.escapeJava(Character.toString(escapeChar));
            return nullSafeCodeGen(ctx, ev, (eval1, eval2) ->
                    CodeGeneratorUtils.template(
                            """
                                    String ${rightStr} = ${eval2}.toString();
                                    ${patternClass} ${pattern} = ${patternClass}.compile(
                                      ${escapeFunc}(${rightStr}, '${escapedEscapeChar}'));
                                    ${value} = ${pattern}.matcher(${eval1}.toString()).matches();
                                    """,
                            Map.of(
                                    "rightStr", rightStr,
                                    "eval2", eval2,
                                    "patternClass", patternClass,
                                    "pattern", pattern,
                                    "escapeFunc", escapeFunc,
                                    "escapedEscapeChar", escapedEscapeChar,
                                    "value", ev.value,
                                    "eval1", eval1
                            )
                    ));
        }
    }

    @Override
    public String toString() {
        if (escapeChar == '\\') {
            return String.format("%s LIKE %s", left, right);
        } else {
            return String.format("%s LIKE %s ESCAPE '%s'", left, right, escapeChar);
        }
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Like(newLeft, newRight, escapeChar);
    }
}
