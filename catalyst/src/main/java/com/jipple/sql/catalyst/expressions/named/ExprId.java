package com.jipple.sql.catalyst.expressions.named;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A globally unique id for a given named expression.
 * Used to identify which attribute output by a relation is being
 * referenced in a subsequent computation.
 *
 * The `id` field is unique within a given JVM, while the `uuid` is used to uniquely identify JVMs.
 */
public class ExprId implements Serializable {
    private static final UUID JVM_ID = UUID.randomUUID();
    private static AtomicLong curId = new AtomicLong();
    public final long id;
    public final UUID jvmId;
    private ExprId(long id, UUID jvmId) {
        this.id = id;
        this.jvmId = jvmId;
    }

    public static ExprId of(long id) {
        return new ExprId(id, JVM_ID);
    }

    public static ExprId of(long id, UUID jvmId) {
        return new ExprId(id, jvmId);
    }

    public static ExprId newExprId() {
        return new ExprId(curId.getAndIncrement(), JVM_ID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExprId exprId = (ExprId) o;
        return id == exprId.id && jvmId.equals(exprId.jvmId);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}


