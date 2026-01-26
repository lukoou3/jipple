package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.GenericArrayData;
import com.jipple.sql.types.ArrayType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import static com.jipple.sql.types.DataTypes.STRING;

public class RegExpExtractAll extends RegExpExtractBase {
    public RegExpExtractAll(Expression subject, Expression regexp, Expression idx) {
        super(subject, regexp, idx);
    }

    public RegExpExtractAll(Expression subject, Expression regexp) {
        this(subject, regexp, Literal.of(1));
    }

    @Override
    public DataType dataType() {
        return new ArrayType(STRING);
    }

    @Override
    public String prettyName() {
        return "regexp_extract_all";
    }

    @Override
    protected Object nullSafeEval(Object s, Object p, Object r) {
        Matcher m = getLastMatcher(s, (UTF8String) p);
        ArrayList<UTF8String> matchResults = new ArrayList<>();
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            int index = (Integer) r;
            RegExpExtractBase.checkGroupIndex(prettyName(), m.groupCount(), index);
            String group = mr.group(index);
            if (group == null) { // Pattern matched, but it's an optional group
                matchResults.add(UTF8String.EMPTY_UTF8);
            } else {
                matchResults.add(UTF8String.fromString(group));
            }
        }
        return new GenericArrayData(matchResults.toArray(new UTF8String[matchResults.size()]));
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String classNameRegExpExtractBase = RegExpExtractBase.class.getCanonicalName();
        String arrayClass = GenericArrayData.class.getName();
        String matcher = ctx.freshName("matcher");
        String matchResult = ctx.freshName("matchResult");
        String matchResults = ctx.freshName("matchResults");
        String group = ctx.freshName("group");
        String setEvNotNull = nullable() ? ev.isNull + " = false;" : "";

        return nullSafeCodeGen(ctx, ev, (subject, regexp, idx) -> {
            return CodeGeneratorUtils.template(
                    """
                    ${initLastMatcherCode}
                    java.util.ArrayList ${matchResults} = new java.util.ArrayList<UTF8String>();
                    while (${matcher}.find()) {
                      java.util.regex.MatchResult ${matchResult} = ${matcher}.toMatchResult();
                      ${classNameRegExpExtractBase}.checkGroupIndex("${prettyName}", ${matchResult}.groupCount(), ${idx});
                      String ${group} = ${matchResult}.group(${idx});
                      if (${group} == null) {
                        ${matchResults}.add(UTF8String.EMPTY_UTF8);
                      } else {
                        ${matchResults}.add(UTF8String.fromString(${group}));
                      }
                    }
                    ${evValue} = new ${arrayClass}(${matchResults}.toArray(new UTF8String[${matchResults}.size()]));
                    ${setEvNotNull}
                    """,
                    Map.ofEntries(
                            Map.entry("initLastMatcherCode", RegExpUtils.initLastMatcherCode(ctx, subject, regexp, matcher, prettyName())),
                            Map.entry("matcher", matcher),
                            Map.entry("matchResult", matchResult),
                            Map.entry("matchResults", matchResults),
                            Map.entry("group", group),
                            Map.entry("classNameRegExpExtractBase", classNameRegExpExtractBase),
                            Map.entry("prettyName", prettyName()),
                            Map.entry("idx", idx),
                            Map.entry("evValue", ev.value),
                            Map.entry("arrayClass", arrayClass),
                            Map.entry("setEvNotNull", setEvNotNull)
                    )
            );
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new RegExpExtractAll(newFirst, newSecond, newThird);
    }
}
