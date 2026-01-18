package com.jipple.sql.catalyst.expressions.condition;

import com.google.common.base.Preconditions;
import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion;
import com.jipple.sql.catalyst.expressions.ComplexTypeMergingExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.DataType;
import com.jipple.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class CaseWhen extends ComplexTypeMergingExpression {
    public final List<Tuple2<Expression, Expression>> branches;
    public final Option<Expression> elseValue;

    public CaseWhen(List<Tuple2<Expression, Expression>> branches, Option<Expression> elseValue) {
        this.branches = branches;
        this.elseValue = elseValue;
    }

    public CaseWhen(List<Tuple2<Expression, Expression>> branches) {
        this(branches, Option.none());
    }

    @Override
    public Object[] args() {
        return new Object[] { branches, elseValue };
    }

    @Override
    public List<Expression> children() {
        List<Expression> children = new ArrayList<>(branches.size() * 2 + (elseValue.isDefined() ? 1 : 0 ));
        for (Tuple2<Expression, Expression> branch : branches) {
            children.add(branch._1);
            children.add(branch._2);
        }
        if (elseValue.isDefined()) {
            children.add(elseValue.get());
        }
        return children;
    }

    @Override
    public List<DataType> inputTypesForMerging() {
        List<DataType> inputTypes = new ArrayList<>();
        for (Tuple2<Expression, Expression> branch : branches) {
            inputTypes.add(branch._2.dataType());
        }
        if (elseValue.isDefined()) {
            inputTypes.add(elseValue.get().dataType());
        }
        return inputTypes;
    }

    @Override
    public boolean nullable() {
        return branches.stream().anyMatch(x -> x._2.nullable()) || elseValue.map(x -> x.nullable()).getOrElse(true);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (TypeCoercion.haveSameType(inputTypesForMerging())) {
            // Make sure all branch conditions are boolean types.
            for (int i = 0; i < branches.size(); i++) {
                if (!branches.get(i)._1.dataType().equals(BOOLEAN)) {
                    return TypeCheckResult.typeCheckFailure("type of predicate expression in CaseWhen should be boolean, but find not :" + branches.get(i)._1.sql());
                }
            }
            return TypeCheckResult.typeCheckSuccess();
        } else {
            return TypeCheckResult.typeCheckFailure("differing types in CaseWhen:" + inputTypesForMerging().stream().map(x -> x.sql()).collect(Collectors.joining(", ")));
        }
    }

    @Override
    public Object eval(InternalRow input) {
        Tuple2<Expression, Expression> branche;
        for (int i = 0; i < branches.size(); i ++) {
            branche = branches.get(i);
            if (Boolean.TRUE.equals(branche._1.eval(input))) {
                return branche._2.eval(input);
            }
        }
        if (elseValue.isDefined()) {
            return elseValue.get().eval(input);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("case");
        for (int i = 0; i < branches.size(); i ++) {
            sb.append(" when ").append(branches.get(i)._1).append(" then ").append(branches.get(i)._2);
        }
        if (elseValue.isDefined()) {
            sb.append(" else ").append(elseValue.get());
        }
        sb.append(" end");
        return sb.toString();
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        Preconditions.checkArgument(newChildren.size() == branches.size() * 2 + (elseValue.isDefined() ? 1 : 0));
        List<Tuple2<Expression, Expression>> newBranches = new ArrayList<>(branches.size());
        for (int i = 0; i < branches.size(); i += 2) {
            newBranches.add(Tuple2.of(newChildren.get(i), newChildren.get(i + 1)));
        }
        return new CaseWhen(newBranches, elseValue.isDefined()? Option.of(newChildren.get(newChildren.size() - 1)) : Option.empty());
    }
}
