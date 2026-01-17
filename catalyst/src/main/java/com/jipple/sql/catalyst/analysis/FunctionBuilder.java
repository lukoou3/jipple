package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.catalyst.expressions.Expression;

import java.util.List;

@FunctionalInterface
public interface FunctionBuilder<T> {
    T apply(List<Expression> expressions);
}
