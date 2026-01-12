package com.jipple.sql.catalyst.parser;

import org.junit.jupiter.api.Test;

public class CatalystSqlParserTest {

    @Test
    public void testParseExpression() {
        var parser = CatalystSqlParser.getInstance();
        var expr = parser.parseExpression("a");
        System.out.println(expr);
        var exprs = parser.parseExpressions("a, b");
        System.out.println(exprs);
    }

}