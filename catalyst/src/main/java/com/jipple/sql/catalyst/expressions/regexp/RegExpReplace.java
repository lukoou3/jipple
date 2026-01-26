package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.QuaternaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.jipple.sql.types.DataTypes.INTEGER;
import static com.jipple.sql.types.DataTypes.STRING;

public class RegExpReplace extends QuaternaryExpression {
    public final Expression subject;
    public final Expression regexp;
    public final Expression rep;
    public final Expression pos;

    public RegExpReplace(Expression subject, Expression regexp, Expression rep, Expression pos) {
        super(subject, regexp, rep, pos);
        this.subject = subject;
        this.regexp = regexp;
        this.rep = rep;
        this.pos = pos;
    }

    public RegExpReplace(Expression subject, Expression regexp, Expression rep) {
        this(subject, regexp, rep, Literal.of(1));
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(STRING, STRING, STRING, INTEGER));
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public String prettyName() {
        return "regexp_replace";
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        TypeCheckResult defaultCheck = super.checkInputDataTypes();
        if (defaultCheck.isFailure()) {
            return defaultCheck;
        }
        if (!pos.foldable()) {
            return TypeCheckResult.dataTypeMismatch("NON_FOLDABLE_INPUT", Map.of("inputName", "position", "inputType", pos.dataType().toString(), "inputExpr", pos.sql()));
        }
        Object posEval = pos.eval();
        if (posEval == null ||  ((Integer) posEval) > 0) {
            return TypeCheckResult.typeCheckSuccess();
        } else {
            return TypeCheckResult.dataTypeMismatch("VALUE_OUT_OF_RANGE", Map.of("inputName", "position", "valueRange", "(0, " + Integer.MAX_VALUE + "]", "currentValue", posEval.toString()));
        }
    }

    // last regex in string, we will update the pattern iff regexp value changed.
    private transient UTF8String lastRegex;
    // last regex pattern, we cache it for performance concern
    private transient Pattern pattern;
    // last replacement string, we don't want to convert a UTF8String => java.langString every time.
    private transient String lastReplacement;
    private transient UTF8String lastReplacementInUTF8;
    // result buffer write by Matcher
    private StringBuffer result = new StringBuffer();

    @Override
    protected Object nullSafeEval(Object s, Object p, Object r, Object i) {
        UTF8String pStr = (UTF8String) p;
        if (!pStr.equals(lastRegex)) {
            var patternAndRegex = RegExpUtils.getPatternAndLastRegex(pStr, prettyName());
            pattern = patternAndRegex._1;
            lastRegex = patternAndRegex._2;
        }

        UTF8String rStr = (UTF8String) r;
        if (!rStr.equals(lastReplacementInUTF8)) {
            // replacement string changed
            lastReplacementInUTF8 = rStr.clone();
            lastReplacement = lastReplacementInUTF8.toString();
        }

        String source = s.toString();
        int position = (Integer) i - 1;
        if (position == 0 || position < source.length()) {
            var m = pattern.matcher(source);
            m.region(position, source.length());
            result.delete(0, result.length());
            while (m.find()) {
                m.appendReplacement(result, lastReplacement);
            }
            m.appendTail(result);
            return UTF8String.fromString(result.toString());
        } else {
            return s;
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String termResult = ctx.freshName("termResult");

        String classNameStringBuffer = StringBuffer.class.getCanonicalName();

        String matcher = ctx.freshName("matcher");
        String source = ctx.freshName("source");
        String position = ctx.freshName("position");

        String termLastReplacement = ctx.addMutableState("String", "lastReplacement");
        String termLastReplacementInUTF8 = ctx.addMutableState("UTF8String", "lastReplacementInUTF8");

        String setEvNotNull = nullable() ? ev.isNull + " = false;" : "";

        return nullSafeCodeGen(ctx, ev, (subject, regexp, rep, pos) -> {
            return CodeGeneratorUtils.template(
                    """
                    ${initLastMatcherCode}
                    if (!${rep}.equals(${termLastReplacementInUTF8})) {
                      // replacement string changed
                      ${termLastReplacementInUTF8} = ${rep}.clone();
                      ${termLastReplacement} = ${termLastReplacementInUTF8}.toString();
                    }
                    String ${source} = ${subject}.toString();
                    int ${position} = ${pos} - 1;
                    if (${position} == 0 || ${position} < ${source}.length()) {
                      ${classNameStringBuffer} ${termResult} = new ${classNameStringBuffer}();
                      ${matcher}.region(${position}, ${source}.length());
        
                      while (${matcher}.find()) {
                        ${matcher}.appendReplacement(${termResult}, ${termLastReplacement});
                      }
                      ${matcher}.appendTail(${termResult});
                      ${evValue} = UTF8String.fromString(${termResult}.toString());
                      ${termResult} = null;
                    } else {
                      ${evValue} = ${subject};
                    }
                    ${setEvNotNull}
                    """,
                    Map.ofEntries(
                            Map.entry("initLastMatcherCode", RegExpUtils.initLastMatcherCode(ctx, subject, regexp, matcher, prettyName())),
                            Map.entry("rep", rep),
                            Map.entry("termLastReplacementInUTF8", termLastReplacementInUTF8),
                            Map.entry("termLastReplacement", termLastReplacement),
                            Map.entry("source", source),
                            Map.entry("position", position),
                            Map.entry("pos", pos),
                            Map.entry("classNameStringBuffer", classNameStringBuffer),
                            Map.entry("termResult", termResult),
                            Map.entry("matcher", matcher),
                            Map.entry("subject", subject),
                            Map.entry("evValue", ev.value),
                            Map.entry("setEvNotNull", setEvNotNull)
                    )
            );
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird, Expression newFourth) {
        return new RegExpReplace(newFirst, newSecond, newThird, newFourth);
    }
}
