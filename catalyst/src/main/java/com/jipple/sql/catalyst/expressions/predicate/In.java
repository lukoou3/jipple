package com.jipple.sql.catalyst.expressions.predicate;

import com.google.common.base.Preconditions;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.types.AtomicType;
import com.jipple.sql.types.DataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class In extends Expression {
    public final Expression value;
    public final List<Expression> list;
    Comparator<Object> _comparator;

    public In(Expression value, List<Expression> list) {
        Preconditions.checkArgument(list != null, "list should not be null");
        this.value = value;
        this.list = list;
    }

    @Override
    public Object[] args() {
        return new Object[] {value, list};
    }

    @Override
    public List<Expression> children() {
        List<Expression> children = new ArrayList<>(list.size() + 1);
        children.add(value);
        children.addAll(list);
        return children;
    }

    @Override
    public boolean foldable() {
        return children().stream().allMatch(x -> x.foldable());
    }

    @Override
    public boolean nullable() {
        return children().stream().anyMatch(x -> x.nullable());
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        boolean mismatchOpt = list.stream().anyMatch(l -> !DataType.equalsStructurally(l.dataType(), value.dataType(),  true));
        if (mismatchOpt) {
            return TypeCheckResult.dataTypeMismatch("DATA_DIFF_TYPES",
                    Map.of("functionName", prettyName(), "dataType", children().stream().map(child -> child.dataType().simpleString()).collect(Collectors.joining("[", ", ", "]"))));
        }
        if(!(value.dataType() instanceof AtomicType)){
            return TypeCheckResult.typeCheckFailure("not support order type:" + value.dataType());
        }
        return TypeCheckResult.typeCheckSuccess();
    }

    @Override
    public String toString() {
        return value + " IN " + list.stream().map(Expression::toString).collect(Collectors.joining(",", "(", ")"));
    }

    private Comparator<Object> comparator(){
        if(_comparator == null){
            _comparator = TypeUtils.getInterpretedComparator(value.dataType());
        }
        return _comparator;
    }

    @Override
    public Object eval(InternalRow input) {
        if (list.isEmpty()) {
            return false;
        } else {
            Object evaluatedValue = value.eval(input);
            if (evaluatedValue == null) {
                return null;
            } else {
                boolean hasNull = false;
                Comparator<Object> comparator = comparator();
                for (Expression e : list) {
                    Object v = e.eval(input);
                    if (v == null) {
                        hasNull = true;
                    } else if (comparator.compare(v, evaluatedValue) == 0) {
                        return true;
                    }
                }
                return hasNull ? null : false;
            }
        }
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new In(newChildren.get(0), newChildren.subList(1, newChildren.size()).stream().toList());
    }
}
