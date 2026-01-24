package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.GenericInternalRow;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;
import com.jipple.sql.errors.QueryCompilationErrors;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StringType;
import com.jipple.sql.types.StructField;
import com.jipple.sql.types.StructType;
import com.jipple.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateNamedStruct extends Expression {
    private final List<Expression> children;
    private List<Expression> _nameExprs;
    private List<Expression> _valExprs;
    private List<Object> _names;
    private StructType _dataType;

    public CreateNamedStruct(List<Expression> children) {
        this.children = children;
    }

    @Override
    public Object[] args() {
        return new Object[]{children};
    }

    @Override
    public List<Expression> children() {
        return children;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public boolean foldable() {
        return valExprs().stream().allMatch(Expression::foldable);
    }

    @Override
    public DataType dataType() {
        if (_dataType == null) {
            List<Object> names = names();
            List<Expression> values = valExprs();
            List<StructField> fields = new ArrayList<>(names.size());
            for (int i = 0; i < names.size(); i++) {
                Object name = names.get(i);
                Expression expr = values.get(i);
                fields.add(new StructField(String.valueOf(name), expr.dataType(), expr.nullable()));
            }
            _dataType = new StructType(fields.toArray(new StructField[0]));
        }
        return _dataType;
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (children.size() % 2 != 0) {
            throw QueryCompilationErrors.wrongNumArgsError(prettyName(), List.of(2), children.size());
        }
        List<Expression> nameExprs = nameExprs();
        List<Expression> invalidNames = nameExprs.stream()
                .filter(e -> !e.foldable() || !(e.dataType() instanceof StringType))
                .toList();
        if (!invalidNames.isEmpty()) {
            String inputExprs = invalidNames.stream()
                    .map(Expression::sql)
                    .collect(Collectors.joining(", ", "[", "]"));
            return TypeCheckResult.dataTypeMismatch(
                    "CREATE_NAMED_STRUCT_WITHOUT_FOLDABLE_STRING",
                    Map.of("inputExprs", inputExprs)
            );
        }
        List<Object> names = names();
        if (!names.contains(null)) {
            return TypeCheckResult.typeCheckSuccess();
        }
        String exprName = nameExprs.stream()
                .map(Expression::sql)
                .collect(Collectors.joining(", ", "[", "]"));
        return TypeCheckResult.dataTypeMismatch(
                "UNEXPECTED_NULL",
                Map.of("exprName", exprName)
        );
    }

    /**
     * Returns aliased expressions that could be used to construct a flattened version of this struct.
     */
    public List<NamedExpression> flatten() {
        List<Object> names = names();
        List<Expression> valExprs = valExprs();
        List<NamedExpression> result = new ArrayList<>(valExprs.size());
        for (int i = 0; i < valExprs.size(); i++) {
            result.add(new Alias(valExprs.get(i), String.valueOf(names.get(i))));
        }
        return result;
    }

    @Override
    public Object eval(InternalRow input) {
        List<Expression> valExprs = valExprs();
        Object[] values = new Object[valExprs.size()];
        for (int i = 0; i < valExprs.size(); i++) {
            values[i] = valExprs.get(i).eval(input);
        }
        return new GenericInternalRow(values);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        List<Expression> valExprs = valExprs();
        String rowClass = GenericInternalRow.class.getName();
        String values = ctx.freshName("values");
        List<String> valCodes = new ArrayList<>(valExprs.size());
        for (int i = 0; i < valExprs.size(); i++) {
            Expression expr = valExprs.get(i);
            ExprCode eval = expr.genCode(ctx);
            String code = CodeGeneratorUtils.template(
                    """
                            ${evalCode}
                            if (${isNull}) {
                              ${values}[${index}] = null;
                            } else {
                              ${values}[${index}] = ${value};
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("evalCode", eval.code),
                            Map.entry("isNull", eval.isNull),
                            Map.entry("values", values),
                            Map.entry("index", i),
                            Map.entry("value", eval.value)
                    )
            );
            valCodes.add(code);
        }
        String valuesCode = ctx.splitExpressionsWithCurrentInputs(
                valCodes,
                "createNamedStruct",
                List.of(Tuple2.of("Object[]", values))
        );
        return ev.copy(
                Block.block(
                        """
                                Object[] ${values} = new Object[${size}];
                                ${valuesCode}
                                final InternalRow ${result} = new ${rowClass}(${values});
                                ${values} = null;
                                """,
                        Map.ofEntries(
                                Map.entry("values", values),
                                Map.entry("size", valExprs.size()),
                                Map.entry("valuesCode", valuesCode),
                                Map.entry("result", ev.value),
                                Map.entry("rowClass", rowClass)
                        )
                ),
                FalseLiteral.INSTANCE
        );
    }

    @Override
    public String prettyName() {
        return "named_struct";
    }

    @Override
    public Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new CreateNamedStruct(newChildren);
    }

    private List<Expression> nameExprs() {
        ensureSplitChildren();
        return _nameExprs;
    }

    private List<Expression> valExprs() {
        ensureSplitChildren();
        return _valExprs;
    }

    private List<Object> names() {
        ensureNames();
        return _names;
    }

    private void ensureSplitChildren() {
        if (_nameExprs != null && _valExprs != null) {
            return;
        }
        List<Expression> names = new ArrayList<>();
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < children.size(); i += 2) {
            names.add(children.get(i));
            if (i + 1 < children.size()) {
                values.add(children.get(i + 1));
            }
        }
        this._nameExprs = names;
        this._valExprs = values;
    }

    private void ensureNames() {
        if (_names != null) {
            return;
        }
        ensureSplitChildren();
        List<Object> result = new ArrayList<>(_nameExprs.size());
        for (Expression expr : _nameExprs) {
            result.add(expr.eval(InternalRow.EMPTY));
        }
        _names = result;
    }
}
