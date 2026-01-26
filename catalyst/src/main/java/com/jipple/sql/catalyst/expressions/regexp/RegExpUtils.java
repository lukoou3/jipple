package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.tuple.Tuple2;
import com.jipple.unsafe.types.UTF8String;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegExpUtils {

    public static String initLastMatcherCode(CodegenContext ctx, String subject, String regexp, String matcher, String prettyName) {
        String classNamePattern = Pattern.class.getCanonicalName();
        String termLastRegex = ctx.addMutableState("UTF8String", "lastRegex");
        String termPattern = ctx.addMutableState(classNamePattern, "pattern");

        return CodeGeneratorUtils.template(
                """
                if (!${regexp}.equals(${termLastRegex})) {
                  // regex value changed
                  try {
                    UTF8String r = ${regexp}.clone();
                    ${termPattern} = ${classNamePattern}.compile(r.toString());
                    ${termLastRegex} = r;
                  } catch (java.util.regex.PatternSyntaxException e) {
                    throw QueryExecutionErrors.invalidPatternError("${prettyName}", e.getPattern(), e);
                  }
                }
                java.util.regex.Matcher ${matcher} = ${termPattern}.matcher(${subject}.toString());
                """,
                Map.of(
                        "regexp", regexp,
                        "termLastRegex", termLastRegex,
                        "classNamePattern", classNamePattern,
                        "termPattern", termPattern,
                        "prettyName", prettyName,
                        "matcher", matcher,
                        "subject", subject
                )
        );
    }

    public static Tuple2<Pattern, UTF8String> getPatternAndLastRegex(UTF8String p, String prettyName) {
        UTF8String r = p.clone();
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(r.toString());
        } catch (PatternSyntaxException e) {
            throw QueryExecutionErrors.invalidPatternError(prettyName, e.getPattern(), e);
        }
        return new Tuple2<>(pattern, r);
    }
}
