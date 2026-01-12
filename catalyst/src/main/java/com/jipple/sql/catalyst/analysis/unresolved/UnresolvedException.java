package com.jipple.sql.catalyst.analysis.unresolved;

public class UnresolvedException extends RuntimeException {
    public UnresolvedException(String function) {
        super("Invalid call to " + function + " on unresolved object");
    }
}
