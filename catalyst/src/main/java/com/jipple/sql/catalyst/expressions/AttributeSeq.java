package com.jipple.sql.catalyst.expressions;

import com.google.common.collect.Maps;
import com.jipple.collection.Option;
import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.expressions.named.*;
import com.jipple.sql.types.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class AttributeSeq implements Serializable {
    private final List<Attribute> attrs;
    private final boolean hasThreeOrLessQualifierParts;

    private transient volatile Attribute[] attrsArray;
    private transient volatile Map<ExprId, Integer> exprIdToOrdinal;
    private transient volatile Map<String, List<Attribute>> direct;
    private transient volatile Map<List<String>, List<Attribute>> qualified;
    private transient volatile Map<List<String>, List<Attribute>> qualified3Part;
    private transient volatile Map<List<String>, List<Attribute>> qualified4Part;

    public AttributeSeq(List<Attribute> attrs) {
        this.attrs = attrs;
        this.hasThreeOrLessQualifierParts = attrs.stream()
                .allMatch(a -> qualifierSize(a) <= 3);
    }

    /** Creates a StructType with a schema matching this list of attributes. */
    public StructType toStructType() {
        StructField[] fields = attrs.stream()
                .map(a -> new StructField(a.name(), a.dataType(), a.nullable()))
                .toArray(StructField[]::new);
        return new StructType(fields);
    }

    /** Returns the attribute at the given index. */
    public Attribute apply(int ordinal) {
        return attrsArray()[ordinal];
    }

    /** Returns the index of first attribute with a matching expression id, or -1 if no match exists. */
    public int indexOf(ExprId exprId) {
        Integer index = exprIdToOrdinal().get(exprId);
        return index == null ? -1 : index;
    }

    private Attribute[] attrsArray() {
        Attribute[] local = attrsArray;
        if (local == null) {
            synchronized (this) {
                if (attrsArray == null) {
                    attrsArray = attrs.toArray(new Attribute[0]);
                }
                local = attrsArray;
            }
        }
        return local;
    }

    private Map<ExprId, Integer> exprIdToOrdinal() {
        Map<ExprId, Integer> local = exprIdToOrdinal;
        if (local == null) {
            synchronized (this) {
                if (exprIdToOrdinal == null) {
                    Attribute[] arr = attrsArray();
                    Map<ExprId, Integer> map = Maps.newHashMapWithExpectedSize(arr.length);
                    int index = arr.length - 1;
                    while (index >= 0) {
                        map.put(arr[index].exprId(), index);
                        index -= 1;
                    }
                    exprIdToOrdinal = map;
                }
                local = exprIdToOrdinal;
            }
        }
        return local;
    }

    private Map<String, List<Attribute>> direct() {
        Map<String, List<Attribute>> local = direct;
        if (local == null) {
            synchronized (this) {
                if (direct == null) {
                    Map<String, List<Attribute>> grouped = new HashMap<>();
                    for (Attribute a : attrs) {
                        String key = a.name().toLowerCase(Locale.ROOT);
                        grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
                    }
                    direct = uniqueByStringKey(grouped);
                }
                local = direct;
            }
        }
        return local;
    }

    private Map<List<String>, List<Attribute>> qualified() {
        Map<List<String>, List<Attribute>> local = qualified;
        if (local == null) {
            synchronized (this) {
                if (qualified == null) {
                    Map<List<String>, List<Attribute>> grouped = new HashMap<>();
                    for (Attribute a : attrs) {
                        List<String> qualifier = safeQualifier(a);
                        if (!qualifier.isEmpty()) {
                            String qual = qualifier.get(qualifier.size() - 1).toLowerCase(Locale.ROOT);
                            String name = a.name().toLowerCase(Locale.ROOT);
                            grouped.computeIfAbsent(List.of(qual, name), k -> new ArrayList<>()).add(a);
                        }
                    }
                    qualified = uniqueByListKey(grouped);
                }
                local = qualified;
            }
        }
        return local;
    }

    private Map<List<String>, List<Attribute>> qualified3Part() {
        Map<List<String>, List<Attribute>> local = qualified3Part;
        if (local == null) {
            synchronized (this) {
                if (qualified3Part == null) {
                    Map<List<String>, List<Attribute>> grouped = new HashMap<>();
                    for (Attribute a : attrs) {
                        List<String> qualifier = safeQualifier(a);
                        int size = qualifier.size();
                        if (size >= 2 && size <= 3) {
                            List<String> normalizedQualifier = size == 2 ? qualifier : qualifier.subList(size - 2, size);
                            String db = normalizedQualifier.get(0).toLowerCase(Locale.ROOT);
                            String tbl = normalizedQualifier.get(1).toLowerCase(Locale.ROOT);
                            String name = a.name().toLowerCase(Locale.ROOT);
                            grouped.computeIfAbsent(List.of(db, tbl, name), k -> new ArrayList<>()).add(a);
                        }
                    }
                    qualified3Part = uniqueByListKey(grouped);
                }
                local = qualified3Part;
            }
        }
        return local;
    }

    private Map<List<String>, List<Attribute>> qualified4Part() {
        Map<List<String>, List<Attribute>> local = qualified4Part;
        if (local == null) {
            synchronized (this) {
                if (qualified4Part == null) {
                    Map<List<String>, List<Attribute>> grouped = new HashMap<>();
                    for (Attribute a : attrs) {
                        List<String> qualifier = safeQualifier(a);
                        if (qualifier.size() == 3) {
                            String catalog = qualifier.get(0).toLowerCase(Locale.ROOT);
                            String db = qualifier.get(1).toLowerCase(Locale.ROOT);
                            String tbl = qualifier.get(2).toLowerCase(Locale.ROOT);
                            String name = a.name().toLowerCase(Locale.ROOT);
                            grouped.computeIfAbsent(List.of(catalog, db, tbl, name), k -> new ArrayList<>()).add(a);
                        }
                    }
                    qualified4Part = uniqueByListKey(grouped);
                }
                local = qualified4Part;
            }
        }
        return local;
    }

    private Map<List<String>, List<Attribute>> uniqueByListKey(Map<List<String>, List<Attribute>> input) {
        Map<List<String>, List<Attribute>> unique = new HashMap<>();
        for (Map.Entry<List<String>, List<Attribute>> entry : input.entrySet()) {
            unique.put(entry.getKey(), distinctAttributes(entry.getValue()));
        }
        return unique;
    }

    private Map<String, List<Attribute>> uniqueByStringKey(Map<String, List<Attribute>> input) {
        Map<String, List<Attribute>> unique = new HashMap<>();
        for (Map.Entry<String, List<Attribute>> entry : input.entrySet()) {
            unique.put(entry.getKey(), distinctAttributes(entry.getValue()));
        }
        return unique;
    }

    private static List<Attribute> distinctAttributes(List<Attribute> input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Attribute> set = new LinkedHashSet<>(input);
        return new ArrayList<>(set);
    }

    private static List<String> safeQualifier(Attribute attribute) {
        List<String> qualifier = attribute.qualifier();
        return qualifier == null ? Collections.emptyList() : qualifier;
    }

    private static int qualifierSize(Attribute attribute) {
        List<String> qualifier = attribute.qualifier();
        return qualifier == null ? 0 : qualifier.size();
    }

    /** Match attributes for the case where all qualifiers in attrs have 3 or less parts. */
    private MatchResult matchWithThreeOrLessQualifierParts(List<String> nameParts, Resolver resolver) {
        List<Attribute> candidates = Collections.emptyList();
        List<String> nestedFields = Collections.emptyList();

        if (nameParts.size() >= 4) {
            String catalogPart = nameParts.get(0);
            String dbPart = nameParts.get(1);
            String tblPart = nameParts.get(2);
            String name = nameParts.get(3);
            List<String> key = List.of(
                    catalogPart.toLowerCase(Locale.ROOT),
                    dbPart.toLowerCase(Locale.ROOT),
                    tblPart.toLowerCase(Locale.ROOT),
                    name.toLowerCase(Locale.ROOT));
            List<Attribute> attributes = collectMatches(name, qualified4Part().get(key), resolver)
                    .stream()
                    .filter(a -> {
                        List<String> qualifier = safeQualifier(a);
                        return qualifier.size() == 3
                                && resolver.resolve(catalogPart, qualifier.get(0))
                                && resolver.resolve(dbPart, qualifier.get(1))
                                && resolver.resolve(tblPart, qualifier.get(2));
                    })
                    .collect(Collectors.toList());
            candidates = attributes;
            nestedFields = nameParts.subList(4, nameParts.size());
        }

        if (candidates.isEmpty() && nameParts.size() >= 3) {
            String dbPart = nameParts.get(0);
            String tblPart = nameParts.get(1);
            String name = nameParts.get(2);
            List<String> key = List.of(
                    dbPart.toLowerCase(Locale.ROOT),
                    tblPart.toLowerCase(Locale.ROOT),
                    name.toLowerCase(Locale.ROOT));
            List<Attribute> attributes = collectMatches(name, qualified3Part().get(key), resolver)
                    .stream()
                    .filter(a -> {
                        List<String> qualifier = safeQualifier(a);
                        List<String> normalizedQualifier = qualifier.size() == 2
                                ? qualifier
                                : qualifier.subList(qualifier.size() - 2, qualifier.size());
                        return resolver.resolve(dbPart, normalizedQualifier.get(0))
                                && resolver.resolve(tblPart, normalizedQualifier.get(1));
                    })
                    .collect(Collectors.toList());
            candidates = attributes;
            nestedFields = nameParts.subList(3, nameParts.size());
        }

        if (candidates.isEmpty() && nameParts.size() >= 2) {
            String qualifier = nameParts.get(0);
            String name = nameParts.get(1);
            List<String> key = List.of(
                    qualifier.toLowerCase(Locale.ROOT),
                    name.toLowerCase(Locale.ROOT));
            List<Attribute> attributes = collectMatches(name, qualified().get(key), resolver)
                    .stream()
                    .filter(a -> {
                        List<String> attributeQualifier = safeQualifier(a);
                        return !attributeQualifier.isEmpty()
                                && resolver.resolve(qualifier, attributeQualifier.get(attributeQualifier.size() - 1));
                    })
                    .collect(Collectors.toList());
            candidates = attributes;
            nestedFields = nameParts.subList(2, nameParts.size());
        }

        if (candidates.isEmpty() && !nameParts.isEmpty()) {
            String name = nameParts.get(0);
            candidates = collectMatches(name, direct().get(name.toLowerCase(Locale.ROOT)), resolver);
            nestedFields = nameParts.subList(1, nameParts.size());
        }

        return new MatchResult(candidates, nestedFields);
    }

    /** Match attributes for the case where at least one qualifier in attrs has more than 3 parts. */
    private MatchResult matchWithFourOrMoreQualifierParts(List<String> nameParts, Resolver resolver) {
        List<Attribute> candidates = Collections.emptyList();
        List<String> nestedFields = Collections.emptyList();

        int i = nameParts.size() - 1;
        while (i >= 0 && candidates.isEmpty()) {
            String name = nameParts.get(i);
            List<Attribute> attrsToLookup = direct().get(name.toLowerCase(Locale.ROOT));
            candidates = collectMatches(name, nameParts.subList(0, i), attrsToLookup, resolver);
            if (!candidates.isEmpty()) {
                nestedFields = nameParts.subList(i + 1, nameParts.size());
            }
            i -= 1;
        }

        return new MatchResult(candidates, nestedFields);
    }

    /** Perform attribute resolution given a name and a resolver. */
    public Option<Expression> resolve(List<String> nameParts, Resolver resolver) {
        MatchResult matchResult = hasThreeOrLessQualifierParts
                ? matchWithThreeOrLessQualifierParts(nameParts, resolver)
                : matchWithFourOrMoreQualifierParts(nameParts, resolver);

        List<Attribute> candidates = matchResult.candidates;
        List<String> nestedFields = matchResult.nestedFields;
        List<Attribute> distinctCandidates = distinctAttributes(candidates);

        String name = new UnresolvedAttribute(nameParts).name();
        if (distinctCandidates.size() == 1 && !nestedFields.isEmpty()) {
            Attribute attribute = distinctCandidates.get(0);
            Expression fieldExpr = attribute;
            for (String fieldName : nestedFields) {
                fieldExpr = ExtractValue.apply(fieldExpr, Literal.of(fieldName), resolver);
            }
            return Option.option(new Alias(fieldExpr, nestedFields.get(nestedFields.size() - 1)));
        }

        if (distinctCandidates.size() == 1) {
            return Option.option(distinctCandidates.get(0));
        }

        if (distinctCandidates.isEmpty()) {
            return Option.none();
        }

        String referenceNames = distinctCandidates.stream()
                .map(Attribute::qualifiedName)
                .collect(Collectors.joining(", "));
        throw new AnalysisException(
                "Reference '" + name + "' is ambiguous, could be: " + referenceNames + ".",
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private static List<Attribute> collectMatches(String name, List<Attribute> candidates, Resolver resolver) {
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        List<Attribute> matches = new ArrayList<>();
        for (Attribute a : candidates) {
            if (resolver.resolve(a.name(), name)) {
                matches.add(a.withName(name));
            }
        }
        return matches;
    }

    private static List<Attribute> collectMatches(
            String name,
            List<String> qualifier,
            List<Attribute> candidates,
            Resolver resolver) {
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        List<Attribute> matches = new ArrayList<>();
        for (Attribute a : candidates) {
            if (resolver.resolve(name, a.name()) && matchQualifier(qualifier, safeQualifier(a), resolver)) {
                matches.add(a.withName(name));
            }
        }
        return matches;
    }

    private static boolean matchQualifier(List<String> shorter, List<String> longer, Resolver resolver) {
        if (longer.size() < shorter.size()) {
            return false;
        }
        int offset = longer.size() - shorter.size();
        for (int i = 0; i < shorter.size(); i++) {
            if (!resolver.resolve(longer.get(offset + i), shorter.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static class MatchResult {
        private final List<Attribute> candidates;
        private final List<String> nestedFields;

        private MatchResult(List<Attribute> candidates, List<String> nestedFields) {
            this.candidates = candidates;
            this.nestedFields = nestedFields;
        }
    }
}

