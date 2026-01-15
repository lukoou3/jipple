package com.jipple.sql.errors;

import com.jipple.sql.catalyst.parser.ParseException;
import com.jipple.sql.catalyst.parser.SqlBaseParser;

import java.util.Map;

public class QueryParsingErrors {

    public static ParseException invalidIdentifierError(String ident, SqlBaseParser.ErrorIdentContext ctx) {
        return new ParseException(
                "INVALID_IDENTIFIER",
                Map.of("ident", ident),
                ctx
        );
    }

}
