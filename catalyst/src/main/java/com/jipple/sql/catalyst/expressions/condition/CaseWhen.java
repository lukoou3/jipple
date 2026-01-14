package com.jipple.sql.catalyst.expressions.condition;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class CaseWhen extends Expression {
    public final List<Expression> branches;
    public final Option<Expression> elseValue;

    public CaseWhen(List<Expression> branches, Option<Expression> elseValue) {
        this.branches = branches;
        this.elseValue = elseValue;
    }

    public CaseWhen(List<Expression> branches) {
        this(branches, Option.none());
    }

    @Override
    public Object[] args() {
        return new Object[] { branches, elseValue };
    }

    @Override
    public List<Expression> children() {
        List<Expression> children = new ArrayList<>(branches.size() + (elseValue.isDefined() ? 1 : 0 ));
        children.addAll(branches);
        if (elseValue.isDefined()) {
            children.add(elseValue.get());
        }
        return children;
    }

    @Override
    public boolean nullable() {
        return branches.stream().anyMatch(x -> x.nullable()) || elseValue.map(x -> x.nullable()).getOrElse(true);
    }

    @Override
    public DataType dataType() {
        return branches.get(1).dataType();
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        DataType dataType = branches.get(1).dataType();
        for (int i = 0; i < branches.size(); i += 2) {
            if (!branches.get(i).dataType().equals(BOOLEAN)) {
                return TypeCheckResult.typeCheckFailure("type of predicate expression in CaseWhen should be boolean");
            } else if (!branches.get(i + 1).dataType().equals(dataType)) {
                return TypeCheckResult.typeCheckFailure(String.format("differing types:%s and %s", branches.get(i + 1).dataType(), dataType));
            }
        }
        return TypeCheckResult.typeCheckSuccess();
    }

    @Override
    public Object eval(InternalRow input) {
        for (int i = 0; i < branches.size(); i += 2) {
            if (Boolean.TRUE.equals(branches.get(i).eval(input))) {
                return branches.get(i + 1).eval(input);
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
        for (int i = 0; i < branches.size(); i += 2) {
            sb.append(" when ").append(branches.get(i)).append(" then").append(branches.get(i + 1));
        }
        if (elseValue.isDefined()) {
            sb.append(" else ").append(elseValue.get());
        }
        sb.append(" end");
        return sb.toString();
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        if (elseValue.isDefined()) {
            int size = newChildren.size();
            return new CaseWhen(newChildren.stream().limit(size - 1).collect(Collectors.toList()), Option.some(newChildren.get(size - 1)));
        } else {
            return new CaseWhen(newChildren, Option.none());
        }
    }
}
