package com.jipple.sql.catalyst.parser;

import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.Map;

/**
 * A [[RippleRecognitionException]] extends the [[RecognitionException]] with more information
 * including the error class and parameters for the error message, which align with the interface
 * of [[RippleThrowableHelper]].
 */
public class JippleRecognitionException extends RecognitionException {
    public final String errorClass;
    public final Map<String, String> messageParameters;

    public JippleRecognitionException(String message, Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
        this(message, recognizer, input, ctx, null, null);
    }

    public JippleRecognitionException(String message, Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx, String errorClass, Map<String, String> messageParameters) {
        super(message, recognizer, input, ctx);
        this.errorClass = errorClass;
        this.messageParameters = messageParameters;
    }

    public JippleRecognitionException(RecognitionException recognitionException, String errorClass, Map<String, String> messageParameters){
        this(recognitionException.getMessage(), recognitionException.getRecognizer(), recognitionException.getInputStream(),
                (recognitionException.getCtx() instanceof ParserRuleContext) ? (ParserRuleContext) recognitionException.getCtx() : null,
                errorClass, messageParameters
        );
    }

    public JippleRecognitionException(String errorClass, Map<String, String> messageParameter) {
        this("", null, null, null, errorClass, messageParameter);
    }
}

