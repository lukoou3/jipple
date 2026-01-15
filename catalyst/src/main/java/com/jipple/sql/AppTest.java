package com.jipple.sql;

import com.jipple.sql.catalyst.parser.CatalystSqlParser;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.util.TypeUtils;

import java.util.Comparator;

import static com.jipple.sql.types.DataTypes.INTEGER;

public class AppTest {

    public static void main(String[] args) {
        var parser = CatalystSqlParser.getInstance();
        var expr = parser.parseExpression("a");
        System.out.println(expr);
        var exprs = parser.parseExpressions("a, b");
        System.out.println(exprs);
        var plan = parser.parsePlan("select a, b, 1 c, 2, nvl(d, 0) d, 1 + 2 e");
        System.out.println(plan);
        plan = parser.parsePlan("select x is null a, x like 'a' b, x not like 'a' c from tab where x > 10");
        System.out.println(plan);
    }

}
