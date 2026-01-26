package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.Map;
import java.util.regex.Matcher;

import static com.jipple.sql.types.DataTypes.INTEGER;

public class RegExpInStr extends RegExpExtractBase {
    public RegExpInStr(Expression subject, Expression regexp, Expression idx) {
        super(subject, regexp, idx);
    }

    public RegExpInStr(Expression subject, Expression regexp) {
        this(subject, regexp, Literal.of(0));
    }

    @Override
    public DataType dataType() {
        return INTEGER;
    }

    @Override
    public String prettyName() {
        return "regexp_instr";
    }

    @Override
    protected Object nullSafeEval(Object s, Object r, Object i) {
        try {
            Matcher m = getLastMatcher(s, (UTF8String) r);
            if (m.find()) {
                return m.toMatchResult().start() + 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String matcher = ctx.freshName("matcher");
        String setEvNotNull = nullable() ? ev.isNull + " = false;" : "";

        return nullSafeCodeGen(ctx, ev, (subject, regexp, idx) -> {
            return CodeGeneratorUtils.template(
                    """
                    try {
                      ${setEvNotNull}
                      ${initLastMatcherCode}
                      if (${matcher}.find()) {
                        ${evValue} = ${matcher}.toMatchResult().start() + 1;
                      } else {
                        ${evValue} = 0;
                      }
                    } catch (Exception e) {
                      ${evValue} = 0;
                    }
                    """,
                    Map.ofEntries(
                            Map.entry("setEvNotNull", setEvNotNull),
                            Map.entry("initLastMatcherCode", RegExpUtils.initLastMatcherCode(ctx, subject, regexp, matcher, prettyName())),
                            Map.entry("matcher", matcher),
                            Map.entry("evValue", ev.value)
                    )
            );
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new RegExpInStr(newFirst, newSecond, newThird);
    }
}
