package com.jipple.sql.catalyst.expressions.codegen;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * A WeakReference that has a stable hash-key.
 * When the referent is still alive we use the referent for equality.
 * Once it is dead we fall back to referential equality.
 */
public final class HashableWeakReference extends WeakReference<Object> {
    private final int hash;

    public HashableWeakReference(Object referent, ReferenceQueue<Object> queue) {
        super(referent, queue);
        this.hash = referent.hashCode();
    }

    public HashableWeakReference(Object referent) {
        this(referent, null);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HashableWeakReference)) {
            return false;
        }
        HashableWeakReference other = (HashableWeakReference) obj;
        if (this == other) {
            return true;
        }
        Object referent = get();
        Object otherReferent = other.get();
        return referent != null && otherReferent != null && Objects.equals(referent, otherReferent);
    }
}
