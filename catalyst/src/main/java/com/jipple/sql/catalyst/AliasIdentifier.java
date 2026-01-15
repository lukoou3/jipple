package com.jipple.sql.catalyst;

import java.util.List;

public class AliasIdentifier {
    public final String name;
    public final List<String> qualifier;

    public AliasIdentifier(String name) {
        this(name, List.of());
    }

    public AliasIdentifier(String name, List<String> qualifier) {
        this.name = name;
        this.qualifier = qualifier;
    }

    @Override
    public String toString() {
        return name;
    }
}
