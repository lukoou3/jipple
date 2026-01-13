package com.jipple.sql.catalyst.trees;

/**
 * Helper interface for objects that can be traced back to an {@link Origin}.
 */
public interface WithOrigin {
    /**
     * Gets the origin of this object.
     *
     * @return the origin
     */
    Origin origin();
}
