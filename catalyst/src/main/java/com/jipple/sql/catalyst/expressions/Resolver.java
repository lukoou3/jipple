package com.jipple.sql.catalyst.expressions;

@FunctionalInterface
public interface Resolver {
    Resolver CaseInsensitive = (a, b) -> a.equalsIgnoreCase(b);
    Resolver CaseSensitive = (a, b) -> a.equals(b);

    boolean resolve(String a, String b);

    static Resolver caseInsensitiveResolution() {
        return CaseInsensitive;
    }

    static Resolver caseSensitiveResolution() {
        return CaseSensitive;
    }

}

