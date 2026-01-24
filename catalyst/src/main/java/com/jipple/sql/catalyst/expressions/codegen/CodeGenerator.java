package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.expressions.GenericInternalRow;
import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;

/**
 * A base class for generators of byte code to perform expression evaluation.
 * Includes a set of helpers for referring to Catalyst types and building trees
 * that perform evaluation of individual expressions.
 */
public abstract class CodeGenerator<InType, OutType> {

    protected final String genericMutableRowType = GenericInternalRow.class.getName();

    /**
     * Generates a class for a given input expression. Called when there is not cached code
     * already available.
     */
    protected abstract OutType create(InType in);

    /**
     * Canonicalizes an input expression. Used to avoid double caching expressions that differ only
     * cosmetically.
     */
    protected abstract InType canonicalize(InType in);

    /**
     * Binds an input expression to a given input schema.
     */
    protected abstract InType bind(InType in, List<Attribute> inputSchema);

    /**
     * Generates the requested evaluator binding the given expression(s) to the inputSchema.
     */
    public OutType generate(InType expressions, List<Attribute> inputSchema) {
        return generate(bind(expressions, inputSchema));
    }

    /**
     * Generates the requested evaluator given already bound expression(s).
     */
    public OutType generate(InType expressions) {
        return create(canonicalize(expressions));
    }

    /**
     * Create a new codegen context for expression evaluator, used to store those
     * expressions that don't support codegen.
     */
    public CodegenContext newCodeGenContext() {
        return new CodegenContext();
    }
}
