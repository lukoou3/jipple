package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.error.JippleException;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.trees.TreeNode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.LongType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Expression extends TreeNode<Expression> {
    private Boolean _deterministic;
    private Boolean _resolved;
    private AttributeSet _references;

    public boolean foldable() {
        return false;
    }

    public boolean deterministic() {
        if (_deterministic == null) {
            _deterministic = children().stream().allMatch(x -> x.deterministic());
        }
        return _deterministic;
    }

    public AttributeSet references() {
        if (_references == null) {
            _references = AttributeSet.fromAttributeSets(children().stream().map(x -> x.references()).collect(Collectors.toList()));;
        }
        return _references;
    }

    public abstract boolean nullable();

    public boolean stateful() {
        return false;
    }

    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.none();
    }

    public abstract DataType dataType();

    /** Returns the result of evaluating this expression on a given input Row */
    public abstract Object eval(InternalRow input);

    public Object eval() {
        return eval(null);
    }

    public boolean resolved() {
        if (_resolved == null) {
            _resolved = childrenResolved() && checkInputDataTypes().isSuccess();
        }
        return _resolved;
    }

    public boolean childrenResolved() {
        return children().stream().allMatch(Expression::resolved);
    }

  /**
   * Returns true when two expressions will always compute the same result, even if they differ
   * cosmetically (i.e. capitalization of names in attributes may be different).
   *
   * See [[Canonicalize]] for more details.
   */
    public final boolean semanticEquals(Expression other) {
        // TODO: deterministic && other.deterministic && canonicalized == other.canonicalized
        return deterministic() && other.deterministic() && this.equals(other);
    }

  /**
   * Returns a `hashCode` for the calculation performed by this expression. Unlike the standard
   * `hashCode`, an attempt has been made to eliminate cosmetic differences.
   *
   * See [[Canonicalize]] for more details.
   */
    public int semanticHash() {
        // TODO: canonicalized.hashCode()
        return hashCode();
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

    /**
     * Returns a user-facing string representation of this expression's name.
     * This should usually match the name of the function in SQL.
     */
    public String prettyName() {
        // getTagValue(FunctionRegistry.FUNC_ALIAS).getOrElse(nodeName.toLowerCase(Locale.ROOT))
        return nodeName().toLowerCase();
    }

    protected Stream<Object> flatArguments() {
        return stringArgs().flatMap(x -> {
            if(x instanceof Collection c){
                return c.stream();
            } else if (x instanceof Option o) {
                return o.isDefined() ? Stream.of(o.get()): Stream.empty();
            } else if (x instanceof Iterable iterable) {
                return StreamSupport.stream(iterable.spliterator(), false);
            } else {
                return Stream.of(x);
            }
        });
    }

    protected String typeSuffix() {
        if (resolved()) {
            if (dataType() instanceof LongType) {
                return "L";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    // Marks this as final, Expression.verboseString should never be called, and thus shouldn't be
    // overridden by concrete classes.
    @Override
    public final String verboseString(int maxFields) {
        return simpleString(maxFields);
    }

    @Override
    public String simpleString(int maxFields) {
        return toString();
    }

    @Override
    public String simpleStringWithNodeId() {
        throw JippleException.internalError(nodeName() + " does not implement simpleStringWithNodeId");
    }

    @Override
    public String toString() {
        return prettyName() + flatArguments().map(String::valueOf).collect(Collectors.joining(", ", "(",  ")"));
    }

    /**
     * Returns SQL representation of this expression.  For expressions extending [[NonSQLExpression]],
     * this method may return an arbitrary user facing string.
     */
    public String sql() {
        String childrenSQL = children().stream().map(Expression::sql).collect(Collectors.joining(", "));
        return prettyName() + "(" + childrenSQL + ")";
    }

    /*public void open() {
    }

    public void close() {
    }*/

}
