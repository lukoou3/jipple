package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.catalyst.parser.CatalystSqlParser;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.RelationPlaceholder;
import com.jipple.sql.catalyst.rules.RuleExecutor;
import com.jipple.sql.types.StructType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerTest {
    static final Logger LOG = LoggerFactory.getLogger(AnalyzerTest.class);
    @Test
    public void testAnalyzerTest() {
        LOG.info("testAnalyzerTest");
        var parser = CatalystSqlParser.getInstance();
        StructType structType = (StructType) parser.parseDataType("struct<a:int, b:string, c:string, x: bigint>");
        var analyzer = new Analyzer(Map.of("tbl", new RelationPlaceholder(structType.toAttributes(),"tbl")));
        var plan = parser.parsePlan("""
        select
            a + 1,
            a is null a,
            b like 'a' b,
            b not like 'a' c,
            cast(c as int) d,
            substr(c, 1, 3) s
            -- cast(c as array<int>) e,
            -- cast(c as struct<a:int, b:string>) f 
        from tbl where x > 10L        
        """);
        System.out.println(plan);
        var plan2 = analyzer.execute(plan);
        System.out.println(plan2);
    }
}