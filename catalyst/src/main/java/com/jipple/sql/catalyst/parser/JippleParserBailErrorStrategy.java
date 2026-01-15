package com.jipple.sql.catalyst.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * Inspired by org.antlr.v4.runtime.BailErrorStrategy, which is used in two-stage parsing:
 * This error strategy allows the first stage of two-stage parsing to immediately terminate
 * if an error is encountered, and immediately fall back to the second stage. In addition to
 * avoiding wasted work by attempting to recover from errors here, the empty implementation
 * of sync improves the performance of the first stage.
 */
public class JippleParserBailErrorStrategy extends JippleParserErrorStrategy {

    /**
     * Instead of recovering from exception e, re-throw it wrapped
     * in a ParseCancellationException so it is not caught by the
     * rule function catches. Use Exception.getCause() to get the
     * original RecognitionException.
     */
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        ParserRuleContext context = recognizer.getContext();
        while (context != null) {
            context.exception = e;
            context = context.getParent();
        }
        throw new ParseCancellationException(e);
    }

    /**
     * Make sure we don't attempt to recover inline; if the parser
     * successfully recovers, it won't throw an exception.
     */
    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        InputMismatchException e = new InputMismatchException(recognizer);
        ParserRuleContext context = recognizer.getContext();
        while (context != null) {
            context.exception = e;
            context = context.getParent();
        }
        throw new ParseCancellationException(e);
    }

    /**
     * Make sure we don't attempt to recover from problems in subrules.
     */
    @Override
    public void sync(Parser recognizer) {
        // 故意留空，不做任何同步/错误恢复尝试
    }
}
