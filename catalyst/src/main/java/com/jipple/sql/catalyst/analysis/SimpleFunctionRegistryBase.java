package com.jipple.sql.catalyst.analysis;

import com.jipple.collection.Option;
import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.ExpressionInfo;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;
import com.jipple.sql.errors.QueryCompilationErrors;
import com.jipple.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple function registry implementation.
 */
public class SimpleFunctionRegistryBase<T> implements FunctionRegistryBase<T> {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleFunctionRegistryBase.class);
    protected final Map<FunctionIdentifier, Tuple2<ExpressionInfo, FunctionBuilder<T>>> functionBuilders = new HashMap<>();

    // Resolution of the function name is always case insensitive, but the database name
    // depends on the caller.
    private FunctionIdentifier normalizeFuncName(FunctionIdentifier name) {
        return new FunctionIdentifier(name.funcName.toLowerCase(Locale.ROOT), name.database);
    }

    @Override
    public void registerFunction(
        FunctionIdentifier name,
        ExpressionInfo info,
        FunctionBuilder<T> builder) {
        FunctionIdentifier normalizedName = normalizeFuncName(name);
        internalRegisterFunction(normalizedName, info, builder);
    }

    /**
     * Perform function registry without any preprocessing.
     * This is used when registering built-in functions and doing {@code FunctionRegistry.clone()}.
     */
    public synchronized void internalRegisterFunction(
        FunctionIdentifier name,
        ExpressionInfo info,
        FunctionBuilder<T> builder) {
        Tuple2<ExpressionInfo, FunctionBuilder<T>> newFunction = Tuple2.of(info, builder);
        Tuple2<ExpressionInfo, FunctionBuilder<T>> previousFunction = functionBuilders.put(name, newFunction);
        if (previousFunction != null && !previousFunction.equals(newFunction)) {
            LOG.warn("The function {} replaced a previously registered function.", name);
        }
    }

    @Override
    public T lookupFunction(FunctionIdentifier name, List<Expression> children) throws AnalysisException {
        FunctionBuilder<T> func;
        synchronized (this) {
            Tuple2<ExpressionInfo, FunctionBuilder<T>> entry = functionBuilders.get(normalizeFuncName(name));
            if (entry == null) {
                throw QueryCompilationErrors.unresolvedRoutineError(name, List.of("system.builtin"));
            }
            func = entry._2;
        }
        return func.apply(children);
    }

    @Override
    public synchronized List<FunctionIdentifier> listFunction() {
        return new ArrayList<>(functionBuilders.keySet());
    }

    @Override
    public synchronized Option<ExpressionInfo> lookupFunction(FunctionIdentifier name) {
        return Option.option(functionBuilders.get(normalizeFuncName(name))).map(entry -> entry._1);
    }

    @Override
    public synchronized Option<FunctionBuilder<T>> lookupFunctionBuilder(FunctionIdentifier name) {
        return Option.option(functionBuilders.get(normalizeFuncName(name))).map(entry -> entry._2);
    }

    @Override
    public synchronized boolean dropFunction(FunctionIdentifier name) {
        return Option.option(functionBuilders.remove(normalizeFuncName(name))).isDefined();
    }

    @Override
    public synchronized void clear() {
        functionBuilders.clear();
    }
}
