package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.SQLConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A codegen object generator which creates objects with codegen path first. Once any compile
 * error happens, it can fallback to interpreted implementation. In tests, we can use a SQL config
 * `SQLConf.CODEGEN_FACTORY_MODE` to control fallback behavior.
 */
public abstract class CodeGeneratorWithInterpretedFallback<IN, OUT> {
    private static final Logger LOG = LoggerFactory.getLogger(CodeGeneratorWithInterpretedFallback.class);

    // We are allowed to choose codegen-only or no-codegen modes if under tests.
    public OUT createObject(IN in) {
        return createObject(in, SQLConf.get().codegenFactoryMode());
    }

    public OUT createObject(IN in, CodegenObjectFactoryMode fallbackMode) {
        switch (fallbackMode) {
            case CODEGEN_ONLY:
                return createCodeGeneratedObject(in);
            case NO_CODEGEN:
                return createInterpretedObject(in);
            default:
                try {
                    return createCodeGeneratedObject(in);
                } catch (Exception e) {
                    LOG.warn("Code generation failed, falling back to interpreted mode", e);
                    return createInterpretedObject(in);
                }
        }
    }

    protected abstract OUT createCodeGeneratedObject(IN in);

    protected abstract OUT createInterpretedObject(IN in);
}