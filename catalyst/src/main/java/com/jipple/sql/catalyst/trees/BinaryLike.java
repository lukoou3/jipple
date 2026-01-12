package com.jipple.sql.catalyst.trees;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.function.Function;

public interface BinaryLike<BaseType extends TreeNode<BaseType>> {
    BaseType left();
    BaseType right();
    BaseType withNewChildInternal(BaseType newLeft, BaseType newRight);

    static <T extends TreeNode<T>, S extends TreeNode<T> & BinaryLike<T>> T mapChildren(S self, Function<T, T> f) {
        T left = self.left();
        T right = self.right();
        T newLeft = f.apply(left);
        newLeft = newLeft.fastEquals(left) ? left : newLeft;
        T newRight = f.apply(right);
        newRight = newRight.fastEquals(right) ? right : newRight;
        if (newLeft.fastEquals(left) && newRight.fastEquals(right)) {
            return (T) self;
        } else {
            return self.withNewChildInternal(newLeft, newRight);
        }
    }

    static <T extends TreeNode<T>, S extends TreeNode<T> & BinaryLike<T>> T withNewChildrenInternal(S self, List<T> newChildren) {
        Preconditions.checkArgument(newChildren.size() == 2, "Incorrect number of children");
        return self.withNewChildInternal(newChildren.get(0), newChildren.get(1));
    }

}
