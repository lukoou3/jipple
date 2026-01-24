package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.error.JippleException;
import com.jipple.sql.SQLConf;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.codegen.*;
import com.jipple.sql.catalyst.trees.CurrentOrigin;
import com.jipple.sql.catalyst.trees.TreeNode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.LongType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.none();
    }

    public abstract DataType dataType();

    /**
     * Returns true if the expression contains mutable state.
     *
     * A stateful expression should never be evaluated multiple times for a single row. This should
     * only be a problem for interpreted execution. This can be prevented by creating fresh copies
     * of the stateful expression before execution. A common example to trigger this issue:
     * {{{
     *   val rand = functions.rand()
     *   df.select(rand, rand) // These 2 rand should not share a state.
     * }}}
     */
    public boolean stateful() {
        return false;
    }

    /**
     * Returns a copy of this expression where all stateful expressions are replaced with fresh
     * uninitialized copies. If the expression contains no stateful expressions then the original
     * expression is returned.
     */
    public Expression freshCopyIfContainsStatefulExpression() {
        List<Expression> childrenIndexedSeq = children();
        List<Expression> newChildren = childrenIndexedSeq.stream()
                .map(Expression::freshCopyIfContainsStatefulExpression)
                .collect(Collectors.toList());
        // A more efficient version of `childrenIndexedSeq.zip(newChildren).exists(_ ne _)`
        boolean anyChildChanged = false;
        int size = newChildren.size();
        int i = 0;
        while (!anyChildChanged && i < size) {
            anyChildChanged |= (childrenIndexedSeq.get(i) != newChildren.get(i));
            i += 1;
        }
        // If the children contain stateful expressions and get copied, or this expression is stateful,
        // copy this expression with the new children.
        if (anyChildChanged || stateful()) {
            return CurrentOrigin.withOrigin(origin(), () -> {
                Expression res = withNewChildrenInternal(newChildren);
                res.copyTagsFrom(this);
                return res;
            });
        } else {
            return this;
        }
    }


    /** Returns the result of evaluating this expression on a given input Row */
    public abstract Object eval(InternalRow input);

    public Object eval() {
        return eval(null);
    }

    /**
     * Returns an [[ExprCode]], that contains the Java source code to generate the result of
     * evaluating the expression on an input row.
     *
     * @param ctx a [[CodegenContext]]
     * @return [[ExprCode]]
     */
    public ExprCode genCode(CodegenContext ctx) {
        ExpressionEquals exprEquals = new ExpressionEquals(this);
        SubExprEliminationState subExprState = ctx.subExprEliminationExprs.get(exprEquals);
        
        if (subExprState != null) {
            // This expression is repeated which means that the code to evaluate it has already been added
            // as a function before. In that case, we just re-use it.
            return new ExprCode(
                    ctx.registerComment(this.toString()),
                    subExprState.eval.isNull,
                    subExprState.eval.value);
        } else {
            String isNull = ctx.freshName("isNull");
            String value = ctx.freshName("value");
            ExprCode eval = doGenCode(ctx, ExprCode.of(
                    JavaCode.isNullVariable(isNull),
                    JavaCode.variable(value, dataType())));
            reduceCodeSize(ctx, eval);
            if (!eval.code.toString().isEmpty()) {
                // Add `this` in the comment.
                Block commentBlock = ctx.registerComment(this.toString());
                return eval.copy(commentBlock.plus(eval.code));
            } else {
                return eval;
            }
        }
    }

    private void reduceCodeSize(CodegenContext ctx, ExprCode eval) {
        // TODO: support whole stage codegen too
        int splitThreshold = SQLConf.get().methodSplitThreshold();
        if (eval.code.length() > splitThreshold && ctx.INPUT_ROW != null && ctx.currentVars == null) {
            String setIsNull;
            if (!(eval.isNull instanceof LiteralValue)) {
                String globalIsNull = ctx.addMutableState(CodeGeneratorUtils.JAVA_BOOLEAN, "globalIsNull");
                ExprValue localIsNull = eval.isNull;
                eval.isNull = JavaCode.isNullGlobal(globalIsNull);
                setIsNull = CodeGeneratorUtils.template(
                        "${globalIsNull} = ${localIsNull};",
                        Map.of(
                                "globalIsNull", globalIsNull,
                                "localIsNull", localIsNull.toString()
                        )
                );
            } else {
                setIsNull = "";
            }

            String javaType = CodeGeneratorUtils.javaType(dataType());
            String newValue = ctx.freshName("value");

            String funcName = ctx.freshName(nodeName());
            String funcFullName = ctx.addNewFunction(funcName,
                    CodeGeneratorUtils.template(
                            """
                                    private ${javaType} ${funcName}(InternalRow ${inputRow}) {
                                      ${evalCode}
                                      ${setIsNull}
                                      return ${evalValue};
                                    }
                                    """,
                            Map.of(
                                    "javaType", javaType,
                                    "funcName", funcName,
                                    "inputRow", ctx.INPUT_ROW,
                                    "evalCode", eval.code.toString(),
                                    "setIsNull", setIsNull,
                                    "evalValue", eval.value.toString()
                            )
                    ));

            eval.value = JavaCode.variable(newValue, dataType());
            eval.code = Block.block(
                    "${javaType} ${newValue} = ${funcFullName}(${inputRow});",
                    Map.of(
                            "javaType", javaType,
                            "newValue", newValue,
                            "funcFullName", funcFullName,
                            "inputRow", ctx.INPUT_ROW
                    )
            );
        }
    }

    /**
     * Returns Java source code that can be compiled to evaluate this expression.
     * The default behavior is to call the eval method of the expression. Concrete expression
     * implementations should override this to do actual code generation.
     *
     * @param ctx a [[CodegenContext]]
     * @param ev an [[ExprCode]] with unique terms.
     * @return an [[ExprCode]] containing the Java source code to generate the given expression
     */
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        // Default implementation: return a simple ExprCode
        // Subclasses should override this method to provide actual code generation
        throw new UnsupportedOperationException("This method must be overridden by all concrete expression:" + getClass());
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
     * Returns an expression where a best effort attempt has been made to transform {@code this}
     * in a way that preserves the result but removes cosmetic variations (case sensitivity,
     * ordering for commutative operations, etc.).
     *
     * {@code deterministic} expressions where {@code this.canonicalized == other.canonicalized}
     * will always evaluate to the same result.
     *
     * The process of canonicalization is a one pass, bottom-up expression tree computation based on
     * canonicalizing children before canonicalizing the current node. There is one exception though,
     * as adjacent, same class {@code CommutativeExpression}s canonicalization happens in a way that
     * calling {@code canonicalized} on the root:
     *   1. Gathers and canonicalizes the non-commutative (or commutative but not same class) child
     *      expressions of the adjacent expressions.
     *   2. Reorder the canonicalized child expressions by their hashcode.
     * This means that the lazy {@code canonicalized} is called and computed only on the root of the
     * adjacent expressions.
     */
    private Expression _canonicalized;

    public Expression canonicalized() {
        if (_canonicalized == null) {
            _canonicalized = withCanonicalizedChildren();
        }
        return _canonicalized;
    }

    /**
     * The default process of canonicalization. It is a one pass, bottom-up expression tree
     * computation based on canonicalizing children before canonicalizing the current node.
     */
    protected final Expression withCanonicalizedChildren() {
        List<Expression> canonicalizedChildren = children().stream()
                .map(Expression::canonicalized)
                .toList();
        return withNewChildren(canonicalizedChildren);
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
