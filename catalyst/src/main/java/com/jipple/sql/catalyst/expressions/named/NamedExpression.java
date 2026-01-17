package com.jipple.sql.catalyst.expressions.named;

import java.util.ArrayList;
import java.util.List;

public interface NamedExpression {
    String name();

    ExprId exprId();

    /**
     * Returns a dot separated fully qualified name for this attribute.  If the name or any qualifier
     * contains `dots`, it is quoted to avoid confusion.  Given that there can be multiple qualifiers,
     * it is possible that there are other possible way to refer to this attribute.
     */
    default String qualifiedName() {
        List<String> names = new ArrayList<>();
        names.addAll(qualifier());
        names.add(name());
        return String.join(".", names);
    }
    //def qualifiedName: String = (qualifier :+ name).map(quoteIfNeeded).mkString(".")

    /**
     * Optional qualifier for the expression.
     * Qualifier can also contain the fully qualified information, for e.g, Sequence of string
     * containing the database and the table name
     *
     * For now, since we do not allow using original table name to qualify a column name once the
     * table is aliased, this can only be:
     *
     * 1. Empty Seq: when an attribute doesn't have a qualifier,
     *    e.g. top level attributes aliased in the SELECT clause, or column from a LocalRelation.
     * 2. Seq with a Single element: either the table name or the alias name of the table.
     * 3. Seq with 2 elements: database name and table name
     * 4. Seq with 3 elements: catalog name, database name and table name
     */
    List<String> qualifier();

    Attribute toAttribute();

    /** Returns a copy of this expression with a new `exprId`. */
    NamedExpression newInstance();
}
