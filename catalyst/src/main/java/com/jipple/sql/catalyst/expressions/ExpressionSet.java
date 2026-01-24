package com.jipple.sql.catalyst.expressions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A Set where membership is determined based on determinacy and a canonical representation of
 * an Expression (i.e. one that attempts to ignore cosmetic differences).
 *
 * For non-deterministic expressions, they are always considered as not contained in the Set.
 * On adding a non-deterministic expression, simply append it to the original expressions.
 *
 * The constructor of this class is protected so caller can only initialize an Expression from
 * empty, then build it using add and remove methods.
 */
public class ExpressionSet implements Iterable<Expression>, Cloneable {
    private final Set<Expression> baseSet;
    private List<Expression> originals;

    protected ExpressionSet() {
        this(new HashSet<>(), new ArrayList<>());
    }

    protected ExpressionSet(Set<Expression> baseSet, List<Expression> originals) {
        this.baseSet = baseSet;
        this.originals = originals;
    }

    /** Constructs a new ExpressionSet by applying canonicalized to expressions. */
    public static ExpressionSet apply(Iterable<Expression> expressions) {
        ExpressionSet set = new ExpressionSet();
        for (Expression expression : expressions) {
            set.add(expression);
        }
        return set;
    }

    public static ExpressionSet apply() {
        return new ExpressionSet();
    }

    public ExpressionSet empty() {
        return new ExpressionSet();
    }

    protected void add(Expression e) {
        if (!e.deterministic()) {
            originals.add(e);
        } else if (!baseSet.contains(e.canonicalized())) {
            baseSet.add(e.canonicalized());
            originals.add(e);
        }
    }

    protected void remove(Expression e) {
        if (e.deterministic()) {
            baseSet.remove(e.canonicalized());
            originals = originals.stream()
                    .filter(x -> !x.semanticEquals(e))
                    .toList();
        }
    }

    public boolean contains(Expression elem) {
        return baseSet.contains(elem.canonicalized());
    }

    public ExpressionSet filter(Predicate<Expression> p) {
        Set<Expression> newBaseSet = new HashSet<>();
        for (Expression e : baseSet) {
            if (p.test(e)) {
                newBaseSet.add(e);
            }
        }
        List<Expression> newOriginals = new ArrayList<>();
        for (Expression e : originals) {
            if (p.test(e.canonicalized())) {
                newOriginals.add(e);
            }
        }
        return new ExpressionSet(newBaseSet, newOriginals);
    }

    public ExpressionSet filterNot(Predicate<Expression> p) {
        return filter(p.negate());
    }

    public ExpressionSet plus(Expression elem) {
        ExpressionSet newSet = clone();
        newSet.add(elem);
        return newSet;
    }

    public ExpressionSet plusAll(Iterable<Expression> elems) {
        ExpressionSet newSet = clone();
        for (Expression e : elems) {
            newSet.add(e);
        }
        return newSet;
    }

    public ExpressionSet minus(Expression elem) {
        ExpressionSet newSet = clone();
        newSet.remove(elem);
        return newSet;
    }

    public ExpressionSet minusAll(Iterable<Expression> elems) {
        ExpressionSet newSet = clone();
        for (Expression e : elems) {
            newSet.remove(e);
        }
        return newSet;
    }

    public ExpressionSet map(Function<Expression, Expression> f) {
        ExpressionSet newSet = new ExpressionSet();
        for (Expression elem : this) {
            newSet.add(f.apply(elem));
        }
        return newSet;
    }

    public ExpressionSet flatMap(Function<Expression, Iterable<Expression>> f) {
        ExpressionSet newSet = new ExpressionSet();
        for (Expression elem : this) {
            for (Expression mapped : f.apply(elem)) {
                newSet.add(mapped);
            }
        }
        return newSet;
    }

    @Override
    public Iterator<Expression> iterator() {
        return originals.iterator();
    }

    public boolean apply(Expression elem) {
        return contains(elem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ExpressionSet other)) {
            return false;
        }
        return Objects.equals(this.baseSet, other.baseSet);
    }

    @Override
    public int hashCode() {
        return baseSet.hashCode();
    }

    @Override
    public ExpressionSet clone() {
        return new ExpressionSet(new HashSet<>(baseSet), new ArrayList<>(originals));
    }

    /**
     * Returns a string containing both the post canonicalized expressions and the original
     * expressions in this set.
     */
    public String toDebugString() {
        return "baseSet: " + String.join(", ", baseSet.stream().map(Object::toString).toList())
                + "\noriginals: " + String.join(", ", originals.stream().map(Object::toString).toList());
    }
}
