package com.jipple.sql.catalyst.analysis;

import com.jipple.collection.Option;
import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.ExpressionInfo;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;

import java.util.List;

/**
 * A catalog for looking up user defined functions, used by an {@link Analyzer}.
 *
 * Note:
 *   1) The implementation should be thread-safe to allow concurrent access.
 *   2) the database name is always case-sensitive here, callers are responsible to
 *      format the database name w.r.t. case-sensitive config.
 */
public interface FunctionRegistryBase<T> {
    default void registerFunction(FunctionIdentifier name, FunctionBuilder<T> builder, String source) {
        ExpressionInfo info = new ExpressionInfo(
            Option.option(builder.getClass().getCanonicalName()).getOrElse(builder.getClass().getName()),
            name.database.orNull(),
            name.funcName,
            null,
            "",
            "",
            "",
            "",
            "",
            "",
            source);
        registerFunction(name, info, builder);
    }

    void registerFunction(
        FunctionIdentifier name,
        ExpressionInfo info,
        FunctionBuilder<T> builder);

    /* Create or replace a temporary function. */
    default void createOrReplaceTempFunction(String name, FunctionBuilder<T> builder, String source) {
        registerFunction(new FunctionIdentifier(name), builder, source);
    }

    T lookupFunction(FunctionIdentifier name, List<Expression> children) throws AnalysisException;

    /* List all of the registered function names. */
    List<FunctionIdentifier> listFunction();

    /* Get the class of the registered function by specified name. */
    Option<ExpressionInfo> lookupFunction(FunctionIdentifier name);

    /* Get the builder of the registered function by specified name. */
    Option<FunctionBuilder<T>> lookupFunctionBuilder(FunctionIdentifier name);

    /** Drop a function and return whether the function existed. */
    boolean dropFunction(FunctionIdentifier name);

    /** Checks if a function with a given name exists. */
    default boolean functionExists(FunctionIdentifier name) {
        return lookupFunction(name).isDefined();
    }

    /** Clear all registered functions. */
    void clear();
}
