package com.jipple.sql.catalyst.optimizer.rule;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.*;
import com.jipple.sql.catalyst.expressions.predicate.And;
import com.jipple.sql.catalyst.expressions.predicate.EqualTo;
import com.jipple.sql.catalyst.expressions.predicate.GreaterThanOrEqual;
import com.jipple.sql.catalyst.expressions.regexp.*;
import com.jipple.sql.catalyst.expressions.string.*;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;
import com.jipple.sql.types.StringType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

/**
 * Simplifies LIKE expressions that do not need full regular expressions to evaluate the condition.
 * For example, when the expression is just checking to see if a string starts with a given
 * pattern.
 */
public class LikeSimplification extends Rule<LogicalPlan> {
    // if guards below protect from escapes on trailing %.
    // Cases like "something\%" are not optimized, but this does not affect correctness.
    private static final Pattern STARTS_WITH = Pattern.compile("([^_%]+)%");
    private static final Pattern ENDS_WITH = Pattern.compile("%([^_%]+)");
    private static final Pattern STARTS_AND_ENDS_WITH = Pattern.compile("([^_%]+)%([^_%]+)");
    private static final Pattern CONTAINS = Pattern.compile("%([^_%]+)%");
    private static final Pattern EQUAL_TO = Pattern.compile("([^_%]*)");

    private Option<Expression> simplifyLike(Expression input, String pattern) {
        return simplifyLike(input, pattern, '\\');
    }

    private Option<Expression> simplifyLike(Expression input, String pattern, char escapeChar) {
        if (pattern.contains(String.valueOf(escapeChar))) {
            // There are three different situations when pattern containing escapeChar:
            // 1. pattern contains invalid escape sequence, e.g. 'm\aca'
            // 2. pattern contains escaped wildcard character, e.g. 'ma\%ca'
            // 3. pattern contains escaped escape character, e.g. 'ma\\ca'
            // Although there are patterns can be optimized if we handle the escape first, we just
            // skip this rule if pattern contains any escapeChar for simplicity.
            return Option.none();
        } else {
            Matcher startsWithMatcher = STARTS_WITH.matcher(pattern);
            if (startsWithMatcher.matches()) {
                String prefix = startsWithMatcher.group(1);
                return Option.some(new StartsWith(input, Literal.of(prefix)));
            }

            Matcher endsWithMatcher = ENDS_WITH.matcher(pattern);
            if (endsWithMatcher.matches()) {
                String postfix = endsWithMatcher.group(1);
                return Option.some(new EndsWith(input, Literal.of(postfix)));
            }

            Matcher startsAndEndsWithMatcher = STARTS_AND_ENDS_WITH.matcher(pattern);
            if (startsAndEndsWithMatcher.matches()) {
                String prefix = startsAndEndsWithMatcher.group(1);
                String postfix = startsAndEndsWithMatcher.group(2);
                // 'a%a' pattern is basically same with 'a%' && '%a'.
                // However, the additional `Length` condition is required to prevent 'a' match 'a%a'.
                return Option.some(new And(
                        new GreaterThanOrEqual(new Length(input), Literal.of(prefix.length() + postfix.length())),
                        new And(new StartsWith(input, Literal.of(prefix)), new EndsWith(input, Literal.of(postfix)))
                ));
            }

            Matcher containsMatcher = CONTAINS.matcher(pattern);
            if (containsMatcher.matches()) {
                String infix = containsMatcher.group(1);
                return Option.some(new Contains(input, Literal.of(infix)));
            }

            Matcher equalToMatcher = EQUAL_TO.matcher(pattern);
            if (equalToMatcher.matches()) {
                String str = equalToMatcher.group(1);
                return Option.some(new EqualTo(input, Literal.of(str)));
            }

            return Option.none();
        }
    }

    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformAllExpressions(e -> {
            if (e instanceof Like l && l.right instanceof Literal lit && lit.dataType instanceof StringType) {
                Expression input = l.left;
                char escapeChar = l.escapeChar;
                Object pattern = lit.value;
                if (pattern == null) {
                    // If pattern is null, return null value directly, since "col like null" == null.
                    return Literal.of(null, BOOLEAN);
                } else {
                    return simplifyLike(input, pattern.toString(), escapeChar).getOrElse(e);
                }
            } else {
                return e;
            }
        });
    }
}
