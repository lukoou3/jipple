package com.jipple.sql.catalyst.trees;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.util.PlanStringConcat;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TreeNode<BaseType extends TreeNode<BaseType>> implements Serializable {
    private Set<TreeNode<BaseType>> _containsChild;
    private Set<TreeNode<?>> _allChildren;

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

    private Set<TreeNode<?>> allChildren() {
        if (_allChildren == null) {
            _allChildren = new HashSet<>();
            _allChildren.addAll(children());
            _allChildren.addAll(innerChildren());
        }
        return _allChildren;
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

    /**
     * All the nodes that should be shown as a inner nested tree of this node.
     * For example, this can be used to show sub-queries.
     */
    public List<TreeNode<?>> innerChildren(){
        return List.of();
    }


    /** Returns a string representing the arguments to this node, minus any children */
    public String argString(int maxFields) {
        return stringArgs().flatMap(x -> {
            if (x instanceof TreeNode<?> tn && allChildren().contains(tn)) {
                return Stream.empty();
            } else if (x instanceof Option.Some<?> some && some.get() instanceof TreeNode tn) {
                if (allChildren().contains(tn)) {
                    return Stream.empty();
                } else {
                    return Stream.of(tn.simpleString(maxFields));
                }
            } else if (x instanceof TreeNode<?> tn) {
                return Stream.of(tn.simpleString(maxFields));
            } else if (x instanceof Collection l && allChildren().containsAll(l)) {
                return Stream.empty();
            } else if (x instanceof Collection l && l.isEmpty() ) {
                return Stream.empty();
            } else if (x instanceof Object[] array && array.length == 0 ) {
                return Stream.empty();
            } else if (x instanceof Iterable i) {
                return Stream.of(i.toString());
            } else if(x == null || x instanceof Option.None) {
                return Stream.empty();
            } else if (x instanceof Option.Some some) {
                if (some.get() == null) {
                    return Stream.empty();
                } else {
                    return Stream.of(some.get().toString());
                }
            }
            else {
                return Stream.of(x.toString());
            }
        }).collect(Collectors.joining(", "));
    }

    /**
     * ONE line description of this node.
     * @param maxFields Maximum number of fields that will be converted to strings.
     *                  Any elements beyond the limit will be dropped.
     */
    public String simpleString(int maxFields) {
        // s"$nodeName ${argString(maxFields)}".trim
        return nodeName() + " " + argString(maxFields);
    }

    /**
     * ONE line description of this node containing the node identifier.
     * @return
     */
    public abstract String simpleStringWithNodeId();

    /** ONE line description of this node with more information */
    public abstract String verboseString(int maxFields);

    /** ONE line description of this node with some suffix information */
    public String verboseStringWithSuffix(int maxFields) {
        return verboseString(maxFields);
    }

    @Override
    public String toString() {
        return treeString();
    }

    /** Returns a string representation of the nodes in this tree */
    public final String treeString() {
        return treeString(true);
    }

    public final String treeString(boolean verbose) {
        return treeString(verbose, false, 30, false);
    }

    public final String treeString(boolean verbose, boolean addSuffix) {
        return treeString(verbose, addSuffix, 30, false);
    }

    public final String treeString(boolean verbose, boolean addSuffix, int maxFields) {
        return treeString(verbose, addSuffix, maxFields, false);
    }

    public final String treeString(
            boolean verbose,
            boolean addSuffix,
            int maxFields,
            boolean printOperatorId) {
        PlanStringConcat concat = new PlanStringConcat();
        treeString(concat::append, verbose, addSuffix, maxFields, printOperatorId);
        return concat.toString();
    }

    public void treeString(
            Consumer<String> append,
            boolean verbose,
            boolean addSuffix,
            int maxFields,
            boolean printOperatorId) {
        generateTreeString(0, new ArrayList<>(), append, verbose, "", addSuffix, maxFields,
                printOperatorId, 0);
    }

    /**
     * Appends the string representation of this node and its children to the given Writer.
     *
     * The `i`-th element in `lastChildren` indicates whether the ancestor of the current node at
     * depth `i + 1` is the last child of its own parent node.  The depth of the root node is 0, and
     * `lastChildren` for the root node should be empty.
     *
     * Note that this traversal (numbering) order must be the same as [[getNodeNumbered]].
     */
    public void generateTreeString(
            int depth,
            ArrayList<Boolean> lastChildren,
            Consumer<String> append,
            boolean verbose,
            String prefix,
            boolean addSuffix,
            int maxFields,
            boolean printNodeId,
            int indent) {
        for (int i = 0; i < indent; i++) {
            append.accept("   ");
        }
        if (depth > 0) {
            Iterator<Boolean> iter = lastChildren.iterator();
            for (int i = 0; i < lastChildren.size() - 1; i++) {
                boolean isLast = iter.next();
                append.accept(isLast ? "   " : ":  ");
            }
            append.accept(lastChildren.get(lastChildren.size() - 1) ? "+- " : ":- ");
        }

        String str;
        if (verbose) {
            str = addSuffix ? verboseStringWithSuffix(maxFields) : verboseString(maxFields);
        } else {
            if (printNodeId) {
                str = simpleStringWithNodeId();
            } else {
                str = simpleString(maxFields);
            }
        }
        append.accept(prefix);
        append.accept(str);
        append.accept("\n");

        List<TreeNode<?>> innerChildrenLocal = innerChildren();
        List<BaseType> children = children();
        if (!innerChildrenLocal.isEmpty()) {
            lastChildren.add(children.isEmpty());
            lastChildren.add(false);
            List<TreeNode<?>> innerInit = innerChildrenLocal.subList(0, innerChildrenLocal.size() - 1);
            for (TreeNode<?> child : innerInit) {
                child.generateTreeString(
                        depth + 2, lastChildren, append, verbose,
                        "", addSuffix, maxFields, printNodeId, indent);
            }
            lastChildren.remove(lastChildren.size() - 1);
            lastChildren.remove(lastChildren.size() - 1);

            lastChildren.add(children.isEmpty());
            lastChildren.add(true);
            innerChildrenLocal.get(innerChildrenLocal.size() - 1).generateTreeString(
                    depth + 2, lastChildren, append, verbose,
                    "", addSuffix, maxFields, printNodeId, indent);
            lastChildren.remove(lastChildren.size() - 1);
            lastChildren.remove(lastChildren.size() - 1);
        }
        if (!children.isEmpty()) {
            lastChildren.add(false);
            List<BaseType> childrenInit = children.subList(0, children.size() - 1);
            for (BaseType child : childrenInit) {
                child.generateTreeString(
                        depth + 1, lastChildren, append, verbose, prefix, addSuffix,
                        maxFields, printNodeId, indent);
            }
            lastChildren.remove(lastChildren.size() - 1);

            lastChildren.add(true);
            children.get(children.size() - 1).generateTreeString(
                    depth + 1, lastChildren, append, verbose, prefix,
                    addSuffix, maxFields, printNodeId, indent);
            lastChildren.remove(lastChildren.size() - 1);
        }
    }
}
