package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.identifier.AliasIdentifier;
import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubqueryAlias extends UnaryNode {
    public final AliasIdentifier identifier;

    public SubqueryAlias(AliasIdentifier identifier, LogicalPlan child) {
        super(child);
        this.identifier = identifier;
    }

    public SubqueryAlias(String identifier, LogicalPlan child) {
        this(new AliasIdentifier(identifier), child);
    }

    @Override
    public Object[] args() {
        return new Object[] { identifier, child };
    }

    public String alias() {
        return identifier.name;
    }

    @Override
    public List<Attribute> output() {
        List<String> qualifierList = new ArrayList<>();
        qualifierList.addAll(identifier.qualifier);
        qualifierList.add(alias());
        return child.output().stream().map(e -> e.withQualifier(qualifierList)).collect(Collectors.toList());
    }

    @Override
    public LogicalPlan withNewChildInternal(LogicalPlan newChild) {
        return new SubqueryAlias(identifier, newChild);
    }
}
