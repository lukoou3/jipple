package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.*;
import com.jipple.sql.catalyst.util.GenericArrayData;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.types.ArrayType;
import com.jipple.sql.types.DataType;
import com.jipple.tuple.Tuple3;

import java.util.List;
import java.util.stream.Stream;

import static com.jipple.sql.types.DataTypes.NULL;
import static com.jipple.sql.types.DataTypes.STRING;

public class CreateArray extends Expression {
    private final List<Expression> children;
    private final boolean useStringTypeWhenEmpty;

    public CreateArray(List<Expression> children) {
        this(children, true);
    }

    public CreateArray(List<Expression> children, boolean useStringTypeWhenEmpty) {
        this.children = children;
        this.useStringTypeWhenEmpty = useStringTypeWhenEmpty;
    }

    @Override
    public Object[] args() {
        return new Object[]{children, useStringTypeWhenEmpty};
    }

    @Override
    public List<Expression> children() {
        return children;
    }

    @Override
    public boolean foldable() {
        return children.stream().allMatch(Expression::foldable);
    }

    @Override
    protected Stream<Object> stringArgs() {
        return super.stringArgs().limit(1);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        return TypeUtils.checkForSameTypeInputExpr(
                children.stream().map(Expression::dataType).toList(),
                prettyName()
        );
    }

    private DataType defaultElementType() {
        return useStringTypeWhenEmpty ? STRING : NULL;
    }

    @Override
    public ArrayType dataType() {
        List<DataType> types = children.stream().map(Expression::dataType).toList();
        Option<DataType> elementType = TypeCoercion.findCommonTypeDifferentOnlyInNullFlags(types);
        DataType resolvedElementType = elementType.isDefined() ? elementType.get() : defaultElementType();
        boolean containsNull = children.stream().anyMatch(Expression::nullable);
        return new ArrayType(resolvedElementType, containsNull);
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public Object eval(InternalRow input) {
        Object[] values = new Object[children.size()];
        for (int i = 0; i < children.size(); i++) {
            values[i] = children.get(i).eval(input);
        }
        return new GenericArrayData(values);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        DataType elementType = dataType().elementType;
        Tuple3<String, String, String> result =
                GenArrayData.genCodeToCreateArrayData(ctx, elementType, children, "createArray");
        String allocation = result._1;
        String assigns = result._2;
        String arrayData = result._3;
        return ev.copy(
                Block.block("${allocation}${assigns}", java.util.Map.of(
                        "allocation", allocation,
                        "assigns", assigns
                )),
                FalseLiteral.INSTANCE,
                JavaCode.variable(arrayData, dataType())
        );
    }

    @Override
    public String prettyName() {
        return "array";
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new CreateArray(newChildren, useStringTypeWhenEmpty);
    }
}
