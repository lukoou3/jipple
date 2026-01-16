package com.jipple.sql.catalyst.util;

/**
 * A string concatenator for plan strings.  Uses length from a configured value, and
 *  prints a warning the first time a plan is truncated.
 */
public class PlanStringConcat extends StringConcat {
    public PlanStringConcat() {
        super(10000000);
    }

    @Override
    public String toString() {
        if (atLimit()) {
            /*logWarning(
                    "Truncated the string representation of a plan since it was too long. The " +
                            "plan had length " + length + " and the maximum is " + maxLength + ". This behavior " +
                            "can be adjusted by setting 'MAX_PLAN_STRING_LENGTH'.");*/
            String truncateMsg;
            if (maxLength == 0) {
                truncateMsg = "Truncated plan of " + length + " characters";
            } else {
                truncateMsg = "... " + (length - maxLength) + " more characters";
            }
            java.lang.StringBuilder result = new java.lang.StringBuilder(maxLength + truncateMsg.length());
            strings.forEach(s -> result.append(s));
            result.append(truncateMsg);
            return result.toString();
        } else {
            return super.toString();
        }
    }
}
