package com.jipple.sql.catalyst.expressions.codegen;

import java.util.Collections;
import java.util.List;

/**
 * An empty block of java code.
 */
public class EmptyBlock extends Block {
    public static final EmptyBlock INSTANCE = new EmptyBlock();

    private EmptyBlock() {
    }

    @Override
    public String code() {
        return "";
    }

    @Override
    public Object[] args() {
        return new Object[0];
    }

    @Override
    public List<Block> children() {
        return Collections.emptyList();
    }

    @Override
    protected Block withNewChildrenInternal(List<Block> newChildren) {
        return this;
    }
}

