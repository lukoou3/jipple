package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;

public abstract class Projection extends ExpressionsEvaluator {
    public abstract Object apply(InternalRow row);

    public static final Projection IDENTITY_PROJECTION = new IdentityProjection();
    public static Projection identityProjection() {
        return IDENTITY_PROJECTION;
    }

    public static class IdentityProjection extends Projection {
        @Override
        public Object apply(InternalRow row) {
            return row;
        }
    }
}
