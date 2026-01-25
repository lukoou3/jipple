package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.codegen.GenerateSafeProjection;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StructType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SafeProjectionGenerator extends CodeGeneratorWithInterpretedFallback<List<Expression>, Projection> {
    public static final SafeProjectionGenerator INSTANCE = new SafeProjectionGenerator();
    private SafeProjectionGenerator() {}
    public static SafeProjectionGenerator get() {
        return INSTANCE;
    }

    @Override
    protected Projection createCodeGeneratedObject(List<Expression> expressions) {
        return GenerateSafeProjection.get().generate(expressions);
    }

    @Override
    protected Projection createInterpretedObject(List<Expression> expressions) {
        return new InterpretedSafeProjection(expressions);
    }

    /**
     * Returns a SafeProjection for given StructType.
     */
    public Projection create(StructType schema) {
        return create(Arrays.stream(schema.fields).map(field -> field.dataType).toArray(DataType[]::new));
    }

    /**
     * Returns a SafeProjection for given Array of DataTypes.
     */
    public Projection create(DataType[] fields) {
        List<Expression> exprs = new ArrayList<>(fields.length);
        for (int i = 0; i < fields.length; i++) {
            exprs.add(new BoundReference(i, fields[i], true));
        }
        return createObject(exprs);
    }

    /**
     * Returns a SafeProjection for given sequence of Expressions (bounded).
     */
    public Projection create(List<Expression> expressions) {
        return createObject(expressions);
    }

    /**
     * Returns a SafeProjection for given sequence of Expressions, which will be bound to
     * `inputSchema`.
     */
    public Projection create(List<Expression> expressions, List<Attribute> inputSchema) {
        return createObject(BindReferences.bindReferences(expressions, new AttributeSeq(inputSchema)));
    }

}
