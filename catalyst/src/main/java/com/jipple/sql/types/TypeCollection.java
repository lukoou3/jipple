package com.jipple.sql.types;

import java.util.List;
import java.util.stream.Collectors;

public class TypeCollection extends AbstractDataType {
    public final List<AbstractDataType> types;

    public TypeCollection(List<AbstractDataType> types) {
        assert types.size() > 0;
        this.types = types;
    }

    public static TypeCollection of(AbstractDataType... types) {
        return new TypeCollection(List.of(types));
    }

    @Override
    public DataType defaultConcreteType() {
        return types.get(0).defaultConcreteType();
    }

    @Override
    public boolean acceptsType(DataType other) {
        return types.stream().anyMatch(t -> t.acceptsType(other));
    }

    @Override
    public String simpleString() {
        return types.stream().map(AbstractDataType::simpleString).collect(Collectors.joining(" or ", "(",  ")"));
    }
}
