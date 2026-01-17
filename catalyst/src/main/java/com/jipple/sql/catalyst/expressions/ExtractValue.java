package com.jipple.sql.catalyst.expressions;


import com.jipple.sql.catalyst.expressions.complextype.GetArrayItem;

public class ExtractValue {
    /**
     * Returns the resolved `ExtractValue`. It will return one kind of concrete `ExtractValue`,
     * depend on the type of `child` and `extraction`.
     *
     *   `child`      |    `extraction`    |    concrete `ExtractValue`
     * ----------------------------------------------------------------
     *    Struct      |   Literal String   |        GetStructField
     * Array[Struct]  |   Literal String   |     GetArrayStructFields
     *    Array       |   Integral type    |         GetArrayItem
     *     Map        |   map key type     |         GetMapValue
     */
    public static Expression apply(Expression child, Expression extraction, Resolver resolver) {
        return new GetArrayItem(child, extraction);
    }
}

