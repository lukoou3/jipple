package com.jipple.sql.catalyst.analysis;

import com.jipple.error.JippleException;
import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.plans.logical.Filter;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.UnresolvedRelation;
import com.jipple.sql.catalyst.util.QuotingUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.t;
import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class CheckAnalysis {

    public static void checkAnalysis(LogicalPlan plan) {
        checkAnalysis0(plan);
        plan.setAnalyzed();
    }

    private static void checkAnalysis0(LogicalPlan plan) {
        // We transform up and order the rules so as to catch the first possible failure instead
        // of the result of cascading resolution failures.
        plan.foreachUp(p -> {
            // Skip already analyzed sub-plans
            if (p.analyzed()) {
                return;
            }
            if (p instanceof UnresolvedRelation u){
                throw new AnalysisException(
                        "TABLE_OR_VIEW_NOT_FOUND",
                        Map.of("relationName", QuotingUtils.quoteNameParts(u.multipartIdentifier)),
                        p.origin());
            }
            //getAllExpressions
            for (Expression expression : p.expressions()) {
                expression.foreachUp(e -> {
                    if (e instanceof Attribute a && !a.resolved()) {
                        throw new AnalysisException(
                                "UNRESOLVED_COLUMN",
                                Map.of("objectName", QuotingUtils.quoteIdentifier(a.name())),
                                p.origin());
                    }
                    TypeCheckResult typeCheckResult = e.checkInputDataTypes();
                    if (typeCheckResult.isFailure()) {
                        if(typeCheckResult instanceof TypeCheckResult.TypeCheckFailure failure) {
                            throw new AnalysisException("cannot resolve '" + e.sql() + "' due to data type mismatch: " + failure.message);
                        }
                    }
                    if (e instanceof Cast c && !c.resolved()) {
                        throw JippleException.internalError(
                                "Found the unresolved Cast: " + c.simpleString(1024),
                                c.origin().getQueryContext(),
                                c.origin().context().summary());
                    }
                });
            }

            if (p instanceof Filter f && !f.condition.dataType().equals(BOOLEAN)) {
                throw new AnalysisException(
                        "DATATYPE_MISMATCH.FILTER_NOT_BOOLEAN",
                        Map.of(
                                "sqlExpr", f.expressions().stream().map(x -> x.sql()).collect(Collectors.joining(", ")),
                        "filter", f.condition.sql(),
                        "type", f.condition.dataType().sql()),
                        p.origin());
            }
        });

        plan.foreachUp(p -> {
            if (!p.resolved()) {
                throw JippleException.internalError(
                        "Found the unresolved operator: " + p.simpleString(4096),
                        p.origin().getQueryContext(),
                        p.origin().context().summary());
            }
        });
    }
}
