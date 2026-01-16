package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.catalyst.parser.CatalystSqlParser;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.RelationPlaceholder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerTest {
    @Test
    public void testAnalyzerTest() {
        var parser = CatalystSqlParser.getInstance();
        var analyzer = new Analyzer(Map.of("tbl", new RelationPlaceholder("tbl")));
        var plan = parser.parsePlan("""
        select
            x is null a,
            x like 'a' b,
            x not like 'a' c,
            cast(a as int) d,
            cast(a as array<int>) e,
            cast(a as struct<a:int, b:string>) f 
        from tbl where x > 10        
        """);
        System.out.println(plan);
        var plan2 = analyzer.execute(plan);
        System.out.println(plan2);
    }
}