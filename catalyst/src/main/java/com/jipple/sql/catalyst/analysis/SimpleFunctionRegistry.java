package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.ExpressionInfo;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;
import com.jipple.tuple.Tuple2;

import java.util.Map;

public class SimpleFunctionRegistry extends SimpleFunctionRegistryBase<Expression> {
    public synchronized SimpleFunctionRegistry clone() {
        SimpleFunctionRegistry registry = new SimpleFunctionRegistry();
        for (Map.Entry<FunctionIdentifier, Tuple2<ExpressionInfo, FunctionBuilder<Expression>>> entry :
            functionBuilders.entrySet()) {
            registry.internalRegisterFunction(entry.getKey(), entry.getValue()._1, entry.getValue()._2);
        }
        return registry;
    }
}
