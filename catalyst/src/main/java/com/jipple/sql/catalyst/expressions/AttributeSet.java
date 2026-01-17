package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.AttributeReference;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;
import org.apache.commons.collections4.IteratorUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A Set designed to hold {@link AttributeReference} objects, that performs equality checking using
 * expression id instead of standard java equality. Using expression id means that these
 * sets will correctly test for membership, even when the AttributeReferences in question differ
 * cosmetically (e.g., the names have different capitalizations).
 *
 * Note that we do not override equality for Attribute references as it is really weird when
 * {@code AttributeReference("a"...) == AttributeReference("b", ...)}. This tactic leads to broken tests,
 * and also makes doing transformations hard (we always try keep older trees instead of new ones
 * when the transformation was a no-op).
 */
public final class AttributeSet implements Iterable<Attribute>, Serializable {
    /** Returns an empty {@link AttributeSet}. */
    public static final AttributeSet empty = AttributeSet.of(Collections.emptyList());

    private final LinkedHashSet<AttributeEquals> baseSet;

    private AttributeSet(LinkedHashSet<AttributeEquals> baseSet) {
        this.baseSet = baseSet;
    }

    /** Constructs a new {@link AttributeSet} that contains a single {@link Attribute}. */
    public static AttributeSet of(Attribute attribute) {
        LinkedHashSet<AttributeEquals> baseSet = new LinkedHashSet<>();
        baseSet.add(new AttributeEquals(attribute));
        return new AttributeSet(baseSet);
    }

    /** Constructs a new {@link AttributeSet} given a sequence of {@link Expression Expressions}. */
    public static AttributeSet of(Iterable<Expression> baseSet) {
        List<AttributeSet> sets = new ArrayList<>();
        for (Expression expression : baseSet) {
            sets.add(expression.references());
        }
        return fromAttributeSets(sets);
    }

    /** Constructs a new {@link AttributeSet} given a sequence of {@link AttributeSet}s. */
    public static AttributeSet fromAttributeSets(Iterable<AttributeSet> sets) {
        LinkedHashSet<AttributeEquals> baseSet = new LinkedHashSet<>();
        for (AttributeSet set : sets) {
            baseSet.addAll(set.baseSet);
        }
        return new AttributeSet(baseSet);
    }

    @Override
    public int hashCode() {
        return baseSet.hashCode();
    }

    /** Returns true if the members of this AttributeSet and other are the same. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AttributeSet otherSet)) {
            return false;
        }
        if (otherSet.size() != baseSet.size()) {
            return false;
        }
        for (AttributeEquals attributeEquals : baseSet) {
            if (!otherSet.contains(attributeEquals.a)) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return baseSet.size();
    }

    /** Returns true if this set contains an Attribute with the same expression id as {@code elem}. */
    public boolean contains(NamedExpression elem) {
        return baseSet.contains(new AttributeEquals(elem.toAttribute()));
    }

    /** Returns a new {@link AttributeSet} that contains {@code elem} in addition to the current elements. */
    public AttributeSet plus(Attribute elem) {
        LinkedHashSet<AttributeEquals> next = new LinkedHashSet<>(baseSet);
        next.add(new AttributeEquals(elem));
        return new AttributeSet(next);
    }

    /** Returns a new {@link AttributeSet} that does not contain {@code elem}. */
    public AttributeSet minus(Attribute elem) {
        LinkedHashSet<AttributeEquals> next = new LinkedHashSet<>(baseSet);
        next.remove(new AttributeEquals(elem));
        return new AttributeSet(next);
    }

    /** Returns an iterator containing all of the attributes in the set. */
    @Override
    public Iterator<Attribute> iterator() {
        Iterator<AttributeEquals> iterator = baseSet.iterator();
        return IteratorUtils.transformedIterator(iterator, x -> x.a);
    }

    /**
     * Returns true if the {@link Attribute Attributes} in this set are a subset of the Attributes in
     * {@code other}.
     */
    public boolean subsetOf(AttributeSet other) {
        return other != null && other.baseSet.containsAll(this.baseSet);
    }

    /**
     * Returns a new {@link AttributeSet} that does not contain any of the {@link Attribute Attributes} found
     * in {@code other}.
     */
    public AttributeSet minusAll(Iterable<NamedExpression> other) {
        LinkedHashSet<AttributeEquals> next = new LinkedHashSet<>(baseSet);
        for (NamedExpression namedExpression : other) {
            next.remove(new AttributeEquals(namedExpression.toAttribute()));
        }
        return new AttributeSet(next);
    }

    /**
     * Returns a new {@link AttributeSet} that contains all of the {@link Attribute Attributes} found
     * in {@code other}.
     */
    public AttributeSet union(AttributeSet other) {
        LinkedHashSet<AttributeEquals> next = new LinkedHashSet<>(baseSet);
        next.addAll(other.baseSet);
        return new AttributeSet(next);
    }

    /**
     * Returns a new {@link AttributeSet} contain only the {@link Attribute Attributes} where {@code f} evaluates to
     * true.
     */
    public AttributeSet filter(Predicate<Attribute> f) {
        LinkedHashSet<AttributeEquals> next = new LinkedHashSet<>();
        for (AttributeEquals attributeEquals : baseSet) {
            if (f.test(attributeEquals.a)) {
                next.add(attributeEquals);
            }
        }
        return new AttributeSet(next);
    }

    /**
     * Returns a new {@link AttributeSet} that only contains {@link Attribute Attributes} that are found in
     * {@code this} and {@code other}.
     */
    public AttributeSet intersect(AttributeSet other) {
        LinkedHashSet<AttributeEquals> next = new LinkedHashSet<>(baseSet);
        next.retainAll(other.baseSet);
        return new AttributeSet(next);
    }

    public void foreach(Consumer<Attribute> f) {
        for (AttributeEquals attributeEquals : baseSet) {
            f.accept(attributeEquals.a);
        }
    }

    public List<Attribute> toSeq() {
        List<Attribute> attributes = new ArrayList<>();
        for (AttributeEquals attributeEquals : baseSet) {
            attributes.add(attributeEquals.a);
        }
        attributes.sort(Comparator
            .comparing(Attribute::name)
            .thenComparingLong(attr -> attr.exprId().id)
            .thenComparing(attr -> attr.exprId().jvmId.toString()));
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (AttributeEquals attributeEquals : baseSet) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(attributeEquals.a);
            first = false;
        }
        builder.append("}");
        return builder.toString();
    }

    public boolean isEmpty() {
        return baseSet.isEmpty();
    }

    private static final class AttributeEquals implements Serializable {
        private final Attribute a;

        private AttributeEquals(Attribute a) {
            this.a = a;
        }

        @Override
        public int hashCode() {
            if (a instanceof AttributeReference reference) {
                return reference.exprId.hashCode();
            }
            return a.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttributeEquals that)) {
                return false;
            }
            if (a instanceof AttributeReference a1 && that.a instanceof AttributeReference a2) {
                return Objects.equals(a1.exprId, a2.exprId);
            }
            return Objects.equals(a, that.a);
        }
    }
}
