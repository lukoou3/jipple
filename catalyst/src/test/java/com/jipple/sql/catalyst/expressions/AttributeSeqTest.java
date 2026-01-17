package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.AttributeReference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jipple.sql.types.DataTypes.STRING;
import static org.junit.jupiter.api.Assertions.*;

public class AttributeSeqTest {
    private static final Resolver CASE_INSENSITIVE = (a, b) -> a.equalsIgnoreCase(b);

    @Test
    public void resolveDirectAttribute() {
        Attribute attr = new AttributeReference("col", STRING);
        AttributeSeq seq = new AttributeSeq(List.of(attr));

        Option<Expression> resolved = seq.resolve(List.of("col"), CASE_INSENSITIVE);

        assertTrue(resolved.isDefined());
        assertSame(attr, resolved.get());
    }

    @Test
    public void resolveQualifiedAttribute() {
        Attribute attr = new AttributeReference("col", STRING, List.of("tbl"));
        AttributeSeq seq = new AttributeSeq(List.of(attr));

        Option<Expression> resolved = seq.resolve(List.of("tbl", "col"), CASE_INSENSITIVE);

        assertTrue(resolved.isDefined());
        assertSame(attr, resolved.get());
    }

    @Test
    public void resolveNestedFieldAsAlias() {
        Attribute attr = new AttributeReference("s", STRING);
        AttributeSeq seq = new AttributeSeq(List.of(attr));

        Option<Expression> resolved = seq.resolve(List.of("s", "field"), CASE_INSENSITIVE);

        assertTrue(resolved.isDefined());
        assertTrue(resolved.get() instanceof Alias);
        assertEquals("field", ((Alias) resolved.get()).name());
    }

    @Test
    public void resolveAmbiguousReference() {
        Attribute attr1 = new AttributeReference("col", STRING,  List.of("t1"));
        Attribute attr2 = new AttributeReference("col", STRING,  List.of("t2"));
        AttributeSeq seq = new AttributeSeq(List.of(attr1, attr2));

        assertThrows(AnalysisException.class, () -> seq.resolve(List.of("col"), CASE_INSENSITIVE));
    }


}