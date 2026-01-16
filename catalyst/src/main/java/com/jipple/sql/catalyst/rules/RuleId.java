package com.jipple.sql.catalyst.rules;

public class RuleId {
    public static final RuleId UNKNOWN_RULE_ID = new RuleId(-1);
    public final int id;

    public RuleId(int id) {
        this.id = id;
        // Currently, there are more than 128 but less than 192 rules needing an id. However, the
        // requirement can be relaxed when we have more such rules. Note that increasing the max id can
        // result in increased memory consumption from every TreeNode.
        if (id < -1 || id >= 192) {
            throw new IllegalArgumentException("Invalid rule id: " + id);
        }
    }

    public static RuleId of(int id) {
        return new RuleId(id);
    }

    public static RuleId unknown() {
        return UNKNOWN_RULE_ID;
    }
}
