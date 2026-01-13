package com.jipple.sql.catalyst.trees;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TreeNode<BaseType extends TreeNode<BaseType>> implements Serializable {
    private Set<TreeNode<BaseType>> _containsChild;

    public abstract Object[] args();

    public abstract List<BaseType> children();

    public Set<TreeNode<BaseType>> containsChild() {
        if (_containsChild == null) {
            List<BaseType> children = children();
            _containsChild = new HashSet<>();
            for (BaseType child : children) {
                _containsChild.add(child);
            }
        }
        return _containsChild;
    }

    public final BaseType withNewChildren(List<BaseType> newChildren) {
        List<BaseType> children = children();
        assert newChildren.size() == children.size();
        if (children.isEmpty() || childrenFastEquals(newChildren, children)) {
            return self();
        } else {
            BaseType res = withNewChildrenInternal(newChildren);
            return res;
        }
    }

    protected abstract BaseType withNewChildrenInternal(List<BaseType> newChildren);

    public BaseType transformUp(Function<BaseType, BaseType> rule) {
        BaseType afterRuleOnChildren = mapChildren(x -> x.transformUp(rule));
        if (this.fastEquals(afterRuleOnChildren)) {
            return rule.apply(self());
        } else {
            return rule.apply(afterRuleOnChildren);
        }
    }

    public BaseType mapChildren(Function<BaseType, BaseType> f) {
        if (!containsChild().isEmpty()) {
            return withNewChildren(children().stream().map(f).collect(Collectors.toList()));
        } else {
            return self();
        }
    }

    private boolean childrenFastEquals(List<BaseType> originalChildren, List<BaseType> newChildren) {
        int size = originalChildren.size();
        for (int i = 0; i < size; i++) {
            if (!originalChildren.get(i).fastEquals(newChildren.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected <B> B[] mapProductIterator(Function<Object, B> f) {
        Object[] args = args();
        B[] arr = (B[]) new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            arr[i] = f.apply(args[i]);
        }
        return arr;
    }

    /**
     * Returns the name of this type of TreeNode.  Defaults to the class name.
     * Note that we remove the "Exec" suffix for physical operators here.
     */
    public String nodeName() {
        return getClass().getSimpleName().replaceAll("Exec$", "");
    }

    /**
     * The arguments that should be included in the arg string.  Defaults to the `productIterator`.
     */
    protected Stream<Object> stringArgs() {
        return Arrays.stream(args());
    }

    BaseType self() {
        return (BaseType) this;
    }

    public boolean fastEquals(TreeNode<?> other) {
        return this == other || this.equals(other);
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(args());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Object[] args = args();
        Object[] otherArgs = ((TreeNode) obj).args();
        if (args.length != otherArgs.length) {
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (!args[i].equals(otherArgs[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + Arrays.toString(args()) + ")";
    }
}
