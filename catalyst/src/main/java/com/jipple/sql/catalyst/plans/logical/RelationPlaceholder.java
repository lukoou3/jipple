package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RelationPlaceholder extends LeafNode  {
    public final List<Attribute> output;
    public final String name;

    public RelationPlaceholder(List<Attribute> output, String name) {
        this.output = output;
        this.name = name;
    }

    @Override
    public Object[] args() {
        return new Object[]{output, name};
    }

    @Override
    public List<Attribute> output() {
        return output;
    }

    /**
     * Creates a RelationPlaceholder from a list of table attributes.
     * Each attribute in the output lists will have its qualifier set to the corresponding identifier.
     * 
     * @param outputs a list of tuples containing (identifier, list of attributes)
     * @param name the name of the relation (default: "tbl")
     * @return a new RelationPlaceholder instance
     */
    public static RelationPlaceholder fromTableAttrs(List<Tuple2<String, List<Attribute>>> outputs, String name) {
        List<Attribute> result = new ArrayList<>();
        for (Tuple2<String, List<Attribute>> tuple : outputs) {
            String identifier = tuple._1;
            List<Attribute> attributes = tuple._2;
            for (Attribute attr : attributes) {
                result.add(attr.withQualifier(Collections.singletonList(identifier)));
            }
        }
        return new RelationPlaceholder(result, name);
    }

    /**
     * Creates a RelationPlaceholder from a map of table attributes.
     * Each attribute in the output lists will have its qualifier set to the corresponding key.
     * 
     * @param outputs a map from identifier to list of attributes
     * @param name the name of the relation (default: "tbl")
     * @return a new RelationPlaceholder instance
     */
    public static RelationPlaceholder fromTableAttrs(Map<String, List<Attribute>> outputs, String name) {
        return fromTableAttrs(outputs.entrySet().stream().map(entry -> Tuple2.of(entry.getKey(), entry.getValue())).toList(), name);
    }


}
