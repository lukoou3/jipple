package com.jipple.sql.catalyst.analysis;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.ExpressionInfo;
import com.jipple.sql.catalyst.expressions.arithmetic.*;
import com.jipple.sql.catalyst.expressions.condition.*;
import com.jipple.sql.catalyst.expressions.nvl.*;
import com.jipple.sql.catalyst.expressions.regexp.*;
import com.jipple.sql.catalyst.expressions.string.Substring;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;
import com.jipple.tuple.Tuple2;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FunctionRegistry {
    private FunctionRegistry() {
    }

    public static final Map<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> expressions;

    public static final SimpleFunctionRegistry builtin;

    static {
        Map<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> map = new LinkedHashMap<>();
        @SuppressWarnings("unchecked")
        Tuple2<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>>[] entries = new Tuple2[]{
                expression("coalesce", Coalesce.class),
                expression("if", If.class),
                expression("isnull", IsNull.class),
                expression("isnotnull", IsNotNull.class),
                expression("nvl", Nvl.class),

                expression("+", Add.class),
                expression("-", Subtract.class),
                expression("*", Multiply.class),
                expression("/", Divide.class),
                expression("div", IntegralDivide.class),
                expression("%", Remainder.class),

                expression("like", Like.class),
                expression("rlike", RLike.class),
                expression("regexp_like", RLike.class, true, Option.some("3.2.0")),
                expression("regexp", RLike.class, true, Option.some("3.2.0")),
                expression("substr", Substring.class),
                expression("substring", Substring.class),
        };
        for (Tuple2<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> entry : entries) {
            putExpression(map, entry);
        }

        expressions = Collections.unmodifiableMap(map);

        SimpleFunctionRegistry registry = new SimpleFunctionRegistry();
        for (Map.Entry<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> entry : expressions.entrySet()) {
            registry.internalRegisterFunction(new FunctionIdentifier(entry.getKey()), entry.getValue()._1, entry.getValue()._2);
        }
        builtin = registry;
    }

    /**
     * Create a SQL function builder and corresponding {@link ExpressionInfo}.
     *
     * @param name The function name.
     * @param setAlias The alias name used in SQL representation string.
     * @param since The Spark version since the function is added.
     * @param clazz The actual expression class.
     * @return (function name, (expression information, function builder))
     */
    public static Tuple2<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> expression(
            String name,
            Class<? extends Expression> clazz,
            boolean setAlias,
            Option<String> since) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        Tuple2<ExpressionInfo, FunctionBuilder<? extends Expression>> built =
                (Tuple2<ExpressionInfo, FunctionBuilder<? extends Expression>>) (Tuple2<?, ?>)
                        FunctionRegistryBase.build((Class) clazz, name, since);
        FunctionBuilder<Expression> builder = expressionsList -> {
            Expression expr = built._2.apply(expressionsList);
            // 这个先不实现
            // if (setAlias) expr.setTagValue(FUNC_ALIAS, name)
            return expr;
        };
        return Tuple2.of(name, Tuple2.of(built._1, builder));
    }

    public static Tuple2<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> expression(
            String name,
            Class<? extends Expression> clazz) {
        return expression(name, clazz, false, Option.none());
    }

    public static Tuple2<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> expression(
            String name,
            Class<? extends Expression> clazz,
            boolean setAlias) {
        return expression(name, clazz, setAlias, Option.none());
    }

    private static void putExpression(
            Map<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> map,
            Tuple2<String, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> entry) {
        map.put(entry._1, entry._2);
    }
}
