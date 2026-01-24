package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.STRING;

public abstract class String2TrimExpression extends Expression {
    protected final Expression srcStr;
    protected final Option<Expression> trimStr;

    public String2TrimExpression(Expression srcStr, Option<Expression> trimStr) {
        this.srcStr = srcStr;
        this.trimStr = trimStr;
    }

    @Override
    public List<Expression> children() {
        return trimStr.isDefined() ? List.of(srcStr, trimStr.get()) : List.of(srcStr);
    }

    @Override
    public boolean nullable() {
        return children().stream().anyMatch(x -> x.nullable());
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public boolean foldable() {
        return children().stream().allMatch(x -> x.foldable());
    }

    protected abstract UTF8String doEval(UTF8String srcString);

    protected abstract UTF8String doEval(UTF8String srcString, UTF8String trimString);

    protected abstract String trimMethod();

    @Override
    public Object eval(InternalRow input) {
        UTF8String srcString = (UTF8String)srcStr.eval(input);
        if (srcString == null) {
            return null;
        } else if (trimStr.isDefined()) {
            UTF8String trimString = (UTF8String)trimStr.get().eval(input);
            return doEval(srcString, trimString);
        } else {
            return doEval(srcString);
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        List<ExprCode> evals = children().stream()
                .map(child -> child.genCode(ctx))
                .collect(Collectors.toList());
        ExprCode srcString = evals.get(0);

        if (evals.size() == 1) {
            return ev.copy(Block.block(
                    """
                            ${srcCode}
                            boolean ${isNull} = false;
                            UTF8String ${value} = null;
                            if (${srcIsNull}) {
                              ${isNull} = true;
                            } else {
                              ${value} = ${srcValue}.${trimMethod}();
                            }
                            """,
                    Map.of(
                            "srcCode", srcString.code,
                            "isNull", ev.isNull,
                            "value", ev.value,
                            "srcIsNull", srcString.isNull,
                            "srcValue", srcString.value,
                            "trimMethod", trimMethod()
                    )
            ));
        } else {
            ExprCode trimString = evals.get(1);
            return ev.copy(Block.block(
                    """
                            ${srcCode}
                            boolean ${isNull} = false;
                            UTF8String ${value} = null;
                            if (${srcIsNull}) {
                              ${isNull} = true;
                            } else {
                              ${trimCode}
                              if (${trimIsNull}) {
                                ${isNull} = true;
                              } else {
                                ${value} = ${srcValue}.${trimMethod}(${trimValue});
                              }
                            }
                            """,
                    Map.of(
                            "srcCode", srcString.code,
                            "isNull", ev.isNull,
                            "value", ev.value,
                            "srcIsNull", srcString.isNull,
                            "srcValue", srcString.value,
                            "trimCode", trimString.code,
                            "trimIsNull", trimString.isNull,
                            "trimValue", trimString.value,
                            "trimMethod", trimMethod()
                    )
            ));
        }
    }

}
