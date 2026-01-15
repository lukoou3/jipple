package com.jipple.sql.catalyst.parser;

import com.jipple.sql.catalyst.trees.Origin;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * The ParseErrorListener converts parse errors into ParseExceptions.
 */
public class ParseErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        Origin start;
        Origin stop;
        if (offendingSymbol instanceof CommonToken token) {
            start = new Origin(line, token.getCharPositionInLine());
            int length = token.getStopIndex() - token.getStartIndex() + 1;
            stop = new Origin(line, token.getCharPositionInLine() + length);
        } else {
            start = new Origin(line, charPositionInLine);
            stop = start;
        }
        if (e instanceof JippleRecognitionException sre && sre.errorClass != null){
            throw new ParseException(null, start, stop, sre.errorClass, sre.messageParameters);
        } else {
            throw new ParseException(null, msg, start, stop);
        }
    }
}
