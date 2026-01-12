package com.jipple.sql;

import com.jipple.sql.catalyst.parser.CatalystSqlParser;

public class AppTest {

    public static void main(String[] args) {
        var parser = CatalystSqlParser.getInstance();
        var expr = parser.parseExpression("a");
        System.out.println(expr);
        var exprs = parser.parseExpressions("a, b");
        System.out.println(exprs);
    }

}
