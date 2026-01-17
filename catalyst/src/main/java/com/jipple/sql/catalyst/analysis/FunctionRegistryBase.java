package com.jipple.sql.catalyst.analysis;

import com.jipple.collection.Option;
import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.ExpressionDescription;
import com.jipple.sql.catalyst.expressions.ExpressionInfo;
import com.jipple.sql.catalyst.expressions.InheritAnalysisRules;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;
import com.jipple.sql.errors.QueryCompilationErrors;
import com.jipple.tuple.Tuple2;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

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

    /**
     * Return an expression info and a function builder for the function as defined by {@code clazz}
     * using the given name.
     */
    static <T> Tuple2<ExpressionInfo, FunctionBuilder<T>> build(
        Class<T> clazz,
        String name,
        Option<String> since) {
        boolean isRuntime = InheritAnalysisRules.class.isAssignableFrom(clazz);
        Constructor<?>[] constructors = clazz.getConstructors();
        List<Constructor<?>> candidateConstructors;
        if (isRuntime) {
            int maxNumArgs = Arrays.stream(constructors)
                .mapToInt(Constructor::getParameterCount)
                .max()
                .orElse(0);
            candidateConstructors = new ArrayList<>();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() != maxNumArgs) {
                    candidateConstructors.add(constructor);
                }
            }
        } else {
            candidateConstructors = Arrays.asList(constructors);
        }
        if (candidateConstructors.isEmpty()) {
            throw new IllegalArgumentException("Cannot find a valid constructor for " + clazz.getCanonicalName());
        }

        Constructor<?> varargCtor = null;
        for (Constructor<?> constructor : candidateConstructors) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length == 1 && List.class.isAssignableFrom(params[0])) {
                varargCtor = constructor;
                break;
            }
        }
        final Constructor<?> varargConstructor = varargCtor;

        FunctionBuilder<T> builder = expressions -> {
            if (varargConstructor != null) {
                try {
                    return clazz.cast(varargConstructor.newInstance(expressions));
                } catch (Exception e) {
                    throw QueryCompilationErrors.funcBuildError(name, e);
                }
            }

            Class<?>[] params = new Class<?>[expressions.size()];
            Arrays.fill(params, Expression.class);
            Constructor<?> matched = null;
            for (Constructor<?> constructor : candidateConstructors) {
                if (Arrays.equals(constructor.getParameterTypes(), params)) {
                    matched = constructor;
                    break;
                }
            }
            if (matched == null) {
                TreeSet<Integer> validParametersCount = new TreeSet<>();
                for (Constructor<?> constructor : candidateConstructors) {
                    Class<?>[] types = constructor.getParameterTypes();
                    boolean allExpression = true;
                    for (Class<?> type : types) {
                        if (!Expression.class.equals(type)) {
                            allExpression = false;
                            break;
                        }
                    }
                    if (allExpression) {
                        validParametersCount.add(constructor.getParameterCount());
                    }
                }
                throw QueryCompilationErrors.wrongNumArgsError(
                    name,
                    new ArrayList<>(validParametersCount),
                    params.length);
            }

            try {
                return clazz.cast(matched.newInstance(expressions.toArray()));
            } catch (Exception e) {
                throw QueryCompilationErrors.funcBuildError(name, e);
            }
        };

        return Tuple2.of(expressionInfo(clazz, name, since), builder);
    }

    /**
     * Creates an {@link ExpressionInfo} for the function as defined by {@code clazz} using the given name.
     */
    static <T> ExpressionInfo expressionInfo(Class<T> clazz, String name, Option<String> since) {
        ExpressionDescription df = clazz.getAnnotation(ExpressionDescription.class);
        String className = stripTrailingDollar(clazz.getCanonicalName());
        Option<String> safeSince = since == null ? Option.none() : since;
        if (df != null) {
            if (df.extended().isEmpty()) {
                return new ExpressionInfo(
                    className,
                    null,
                    name,
                    df.usage(),
                    df.arguments(),
                    df.examples(),
                    df.note(),
                    df.group(),
                    safeSince.getOrElse(df.since()),
                    df.deprecated(),
                    df.source());
            }
            // Backward compatibility with old ExpressionDescription using extended().
            @SuppressWarnings("deprecation")
            ExpressionInfo info = new ExpressionInfo(className, null, name, df.usage(), df.extended());
            return info;
        }
        return new ExpressionInfo(className, name);
    }

    static String stripTrailingDollar(String className) {
        if (className != null && className.endsWith("$")) {
            return className.substring(0, className.length() - 1);
        }
        return className;
    }
}
