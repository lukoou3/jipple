package com.jipple.sql.catalyst.trees;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.function.Function;

public interface UnaryLike<BaseType extends TreeNode<BaseType>> {
    BaseType child();
    BaseType withNewChildInternal(BaseType newChild);

    static <T extends TreeNode<T>, S extends TreeNode<T> & UnaryLike<T>> T mapChildren(S self, Function<T, T> f) {
        T child = self.child();
        T newChild = f.apply(child);
        if (newChild.fastEquals(child)) {
            return (T) self;
        } else {
            return self.withNewChildInternal(newChild);
        }
    }


    static <T extends TreeNode<T>, S extends TreeNode<T> & UnaryLike<T>> T withNewChildrenInternal(S self, List<T> newChildren) {
        Preconditions.checkArgument(newChildren.size() == 1, "Incorrect number of children");
        return self.withNewChildInternal(newChildren.get(0));
    }
}
