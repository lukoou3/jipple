package com.jipple.sql.types;

import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.AttributeReference;
import com.jipple.util.JippleCollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StructType extends DataType {
    public final StructField[] fields;
    private Set<String> _fieldNamesSet;
    private Map<String, StructField> _nameToField;
    private Map<String, Integer> _nameToIndex;

    public StructType(StructField[] fields) {
        this.fields = fields;
    }

    /** Returns all field names in an array. */
    public String[] fieldNames() {
        return Arrays.stream(fields).map(f -> f.name).toArray(String[]::new);
    }

    /**
     * Returns all field names in an array. This is an alias of `fieldNames`.
     *
     * @since 2.4.0
     */
    public String[] names() {
        return fieldNames();
    }

    private Set<String> fieldNamesSet() {
        if (_fieldNamesSet == null) {
            _fieldNamesSet = Arrays.stream(fields).map(f -> f.name).collect(Collectors.toSet());
        }
        return _fieldNamesSet;
    }

    private Map<String, StructField> nameToField() {
        if (_nameToField == null) {
            _nameToField = Arrays.stream(fields).collect(Collectors.toMap(f -> f.name, f -> f));
        }
        return _nameToField;
    }

    private Map<String, Integer> nameToIndex() {
        if (_nameToIndex == null) {
            _nameToIndex = JippleCollectionUtils.toMapWithIndex(List.of(fieldNames()));
        }
        return _nameToIndex;
    }


    @Override
    public int defaultSize() {
        return Arrays.stream(fields).mapToInt(f -> f.dataType.defaultSize()).sum();
    }

    @Override
    public String simpleString() {
        return String.format("struct<%s>", Arrays.stream(fields).map(f -> f.name + ":" + f.dataType.simpleString()).collect(Collectors.joining(", ")));
    }

    @Override
    public DataType asNullable() {
        StructField[] newFields = Arrays.stream(fields).map(f -> new StructField(f.name, f.dataType.asNullable(), true)).toArray(StructField[]::new);
        return new StructType(newFields);
    }

    public List<Attribute> toAttributes() {
        return Arrays.stream(fields).map(f -> new AttributeReference(f.name, f.dataType, f.nullable)).collect(Collectors.toList());
    }

    /**
     * Returns the index of a given field.
     *
     * @throws IllegalArgumentException if a field with the given name does not exist
     */
    public int fieldIndex(String name) {
        Integer index = nameToIndex().get(name);
        if (index == null) {
            throw new IllegalArgumentException(name + " does not exist. Available: " + String.join(", ", fieldNames()));
        }
        return index;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        StructField[] otherFields = ((StructType) o).fields;
        return Arrays.equals(fields, otherFields);
    }

}
