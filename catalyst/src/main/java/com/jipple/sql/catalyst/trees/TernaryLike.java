package com.jipple.sql.catalyst.trees;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.function.Function;

public interface TernaryLike<BaseType extends TreeNode<BaseType>> {
    BaseType first();
    BaseType second();
    BaseType third();
    BaseType withNewChildInternal(BaseType newFirst, BaseType newSecond, BaseType newThird);

    static <T extends TreeNode<T>, S extends TreeNode<T> & TernaryLike<T>> T mapChildren(S self, Function<T, T> f) {
        T first = self.first();
        T second = self.second();
        T third = self.third();
        T newFirst = f.apply(first);
        newFirst = newFirst.fastEquals(first) ? first : newFirst;
        T newSecond = f.apply(second);
        newSecond = newSecond.fastEquals(second) ? second : newSecond;
        T newThird = f.apply(third);
        newThird = newThird.fastEquals(third) ? third : newThird;
        if (newFirst.fastEquals(first) && newSecond.fastEquals(second) && newThird.fastEquals(third)) {
            return (T) self;
        }
        return self.withNewChildInternal(newFirst, newSecond, newThird);
    }

    static <T extends TreeNode<T>, S extends TreeNode<T> & TernaryLike<T>> T withNewChildrenInternal(S self, List<T> newChildren) {
        Preconditions.checkArgument(newChildren.size() == 3, "Incorrect number of children");
        return self.withNewChildInternal(newChildren.get(0), newChildren.get(1), newChildren.get(2));
    }
}
