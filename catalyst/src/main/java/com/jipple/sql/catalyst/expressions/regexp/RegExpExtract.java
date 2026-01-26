package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import static com.jipple.sql.types.DataTypes.STRING;

public class RegExpExtract extends RegExpExtractBase {
    public RegExpExtract(Expression subject, Expression regexp, Expression idx) {
        super(subject, regexp, idx);
    }

    public RegExpExtract(Expression subject, Expression regexp) {
        this(subject, regexp, Literal.of(1));
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public String prettyName() {
        return "regexp_extract";
    }

    @Override
    protected Object nullSafeEval(Object s, Object p, Object r) {
        Matcher m = getLastMatcher(s, (UTF8String) p);
        if (m.find()) {
            MatchResult mr = m.toMatchResult();
            int index = (Integer) r;
            RegExpExtractBase.checkGroupIndex(prettyName(), m.groupCount(), index);
            String group = mr.group(index);
            return group == null ? UTF8String.EMPTY_UTF8 : UTF8String.fromString(group);
        } else {
            return UTF8String.EMPTY_UTF8;
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String classNameRegExpExtractBase = RegExpExtractBase.class.getCanonicalName();
        String matcher = ctx.freshName("matcher");
        String matchResult = ctx.freshName("matchResult");
        String group = ctx.freshName("group");
        String setEvNotNull = nullable() ? ev.isNull + " = false;" : "";

        return nullSafeCodeGen(ctx, ev, (subject, regexp, idx) -> {
            return CodeGeneratorUtils.template(
                    """
                    ${initLastMatcherCode}
                    if (${matcher}.find()) {
                      java.util.regex.MatchResult ${matchResult} = ${matcher}.toMatchResult();
                      ${classNameRegExpExtractBase}.checkGroupIndex("${prettyName}", ${matchResult}.groupCount(), ${idx});
                      String ${group} = ${matchResult}.group(${idx});
                      if (${group} == null) {
                        ${evValue} = UTF8String.EMPTY_UTF8;
                      } else {
                        ${evValue} = UTF8String.fromString(${group});
                      }
                      ${setEvNotNull}
                    } else {
                      ${evValue} = UTF8String.EMPTY_UTF8;
                      ${setEvNotNull}
                    }
                    """,
                    Map.ofEntries(
                            Map.entry("initLastMatcherCode", RegExpUtils.initLastMatcherCode(ctx, subject, regexp, matcher, prettyName())),
                            Map.entry("matcher", matcher),
                            Map.entry("matchResult", matchResult),
                            Map.entry("group", group),
                            Map.entry("classNameRegExpExtractBase", classNameRegExpExtractBase),
                            Map.entry("prettyName", prettyName()),
                            Map.entry("idx", idx),
                            Map.entry("evValue", ev.value),
                            Map.entry("setEvNotNull", setEvNotNull)
                    )
            );
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new RegExpExtract(newFirst, newSecond, newThird);
    }
}
