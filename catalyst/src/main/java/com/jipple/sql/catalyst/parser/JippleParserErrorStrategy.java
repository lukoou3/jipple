package com.jipple.sql.catalyst.parser;

import org.antlr.v4.runtime.*;

import java.util.Collections;
import java.util.Map;

/**
 * A JippleParserErrorStrategy extends the DefaultErrorStrategy, with special handling on errors.
 * <p>
 * The intention of this class is to provide more information of these errors encountered in
 * ANTLR parser to the downstream consumers, to be able to apply the JippleThrowable error
 * message framework to these exceptions.
 */
public class JippleParserErrorStrategy extends DefaultErrorStrategy {

    private static final Map<String, String> USER_WORD_DICT = Collections.singletonMap("'<EOF>'", "end of input");

    /**
     * Get the user-facing display of the error token.
     */
    @Override
    public String getTokenErrorDisplay(Token t) {
        String tokenName = super.getTokenErrorDisplay(t);
        return USER_WORD_DICT.getOrDefault(tokenName, tokenName);
    }

    @Override
    public void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        JippleRecognitionException exceptionWithErrorClass = new JippleRecognitionException(
                e,
                "PARSE_SYNTAX_ERROR",
                Map.of("error", getTokenErrorDisplay(e.getOffendingToken()), "hint", "")
        );
        recognizer.notifyErrorListeners(e.getOffendingToken(), "", exceptionWithErrorClass);
    }

    @Override
    public void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
        JippleRecognitionException exceptionWithErrorClass = new JippleRecognitionException(
                e,
                "PARSE_SYNTAX_ERROR",
                Map.of("error", getTokenErrorDisplay(e.getOffendingToken()), "hint", "")
        );
        recognizer.notifyErrorListeners(e.getOffendingToken(), "", exceptionWithErrorClass);
    }

    @Override
    public void reportUnwantedToken(Parser recognizer) {
        if (!inErrorRecoveryMode(recognizer)) {
            beginErrorCondition(recognizer);
            String errorTokenDisplay = getTokenErrorDisplay(recognizer.getCurrentToken());
            String hint = ": extra input " + errorTokenDisplay;
            JippleRecognitionException exceptionWithErrorClass = new JippleRecognitionException(
                    "PARSE_SYNTAX_ERROR",
                    Map.of("error", errorTokenDisplay, "hint", hint)
            );
            recognizer.notifyErrorListeners(recognizer.getCurrentToken(), "", exceptionWithErrorClass);
        }
    }

    @Override
    public void reportMissingToken(Parser recognizer) {
        if (!inErrorRecoveryMode(recognizer)) {
            beginErrorCondition(recognizer);
            String expected = getExpectedTokens(recognizer).toString(recognizer.getVocabulary());
            String hint = ": missing " + expected;
            String currentTokenDisplay = getTokenErrorDisplay(recognizer.getCurrentToken());
            JippleRecognitionException exceptionWithErrorClass = new JippleRecognitionException(
                    "PARSE_SYNTAX_ERROR",
                    Map.of("error", currentTokenDisplay, "hint", hint)
            );
            recognizer.notifyErrorListeners(recognizer.getCurrentToken(), "", exceptionWithErrorClass);
        }
    }
}
