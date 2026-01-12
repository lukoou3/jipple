package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.trees.TreeNode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import java.util.List;
import java.util.Optional;

public abstract class Expression extends TreeNode<Expression> {
    private Boolean _deterministic;
    private Boolean _resolved;

    public boolean foldable() {
        return false;
    }

    public boolean deterministic() {
        if (_deterministic == null) {
            _deterministic = children().stream().allMatch(x -> x.deterministic());
        }
        return _deterministic;
    }

    public abstract boolean nullable();

    public boolean stateful() {
        return false;
    }

    public Optional<List<AbstractDataType>> expectsInputTypes() {
        return Optional.empty();
    }

    public abstract DataType dataType();

    /** Returns the result of evaluating this expression on a given input Row */
    public abstract Object eval(InternalRow input);

    public boolean resolved() {
        if (_resolved == null) {
            _resolved = childrenResolved() && checkInputDataTypes().isSuccess();
        }
        return _resolved;
    }

    public boolean childrenResolved() {
        return children().stream().allMatch(Expression::resolved);
    }

    public TypeCheckResult checkInputDataTypes() {
        if (expectsInputTypes().isEmpty()) {
            return TypeCheckResult.typeCheckSuccess();
        }
        List<Expression> inputs = children();
        List<AbstractDataType> expectsInputTypes = expectsInputTypes().get();
        for (int i = 0; i < inputs.size(); i++) {
            if (!expectsInputTypes.get(i).acceptsType(inputs.get(i).dataType())) {
                return TypeCheckResult.typeCheckFailure(
                        "Wrong input type at index " + i + ": " + inputs.get(i).dataType() + " does not match required " + expectsInputTypes.get(i));
            }
        }
        return TypeCheckResult.typeCheckSuccess();
    }

    /*public void open() {
    }

    public void close() {
    }*/

}
