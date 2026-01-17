package com.jipple.sql.catalyst.identifier;

import com.jipple.collection.Option;

import java.util.Objects;

public class FunctionIdentifier {
    public final String funcName;
    public final Option<String> database;

    public FunctionIdentifier(String funcName, Option<String> database) {
        this.funcName = funcName;
        this.database = database;
    }

    public FunctionIdentifier(String funcName) {
        this(funcName, Option.empty());
    }

    public String identifier() {
        return funcName;
    }

    @Override
    public String toString() {
        return database.isDefined()? database.get() + "." + funcName : funcName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionIdentifier other)) {
            return false;
        }
        return Objects.equals(funcName, other.funcName)
            && Objects.equals(database, other.database);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcName, database);
    }
}
