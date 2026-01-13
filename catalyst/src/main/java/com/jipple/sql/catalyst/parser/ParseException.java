package com.jipple.sql.catalyst.parser;

import com.jipple.sql.AnalysisException;

/**
 * A [[ParseException]] is an [[AnalysisException]] that is thrown during the parse process. It
 * contains fields and an extended error message that make reporting and diagnosing errors easier.
 */
public class ParseException extends AnalysisException {
    public ParseException(String message) {
        super(message);
    }
}
