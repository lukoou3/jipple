package com.jipple.sql.execution;

import com.jipple.sql.catalyst.analysis.FunctionRegistry;
import com.jipple.sql.catalyst.parser.CatalystSqlParser;
import com.jipple.sql.catalyst.plans.logical.RelationPlaceholder;
import com.jipple.sql.types.StructType;
import org.junit.jupiter.api.Test;

import java.util.Map;

class QueryExecutionTest {

    @Test
    void testQueryExecution() {
        var parser = CatalystSqlParser.getInstance();
        StructType structType = (StructType) parser.parseDataType("struct<a:int, b:string, c:string, x: bigint>");
        var plan = parser.parsePlan("""
        select
            a + 1L,
            a is null a,
            b like 'a' b,
            b not like 'a' c,
            cast(c as int) d,
            substr(c, 1, 3) e,
            nvl(c, '') f
        from tbl where x > 10        
        """);
        var queryExecution = new QueryExecution(Map.of("tbl", new RelationPlaceholder(structType.toAttributes(), "tbl")), FunctionRegistry.builtin.clone(), plan);
        var analyzed = queryExecution.analyzed();
        var optimized = queryExecution.optimizedPlan();
        System.out.println(plan);
        System.out.println(analyzed);
        System.out.println(optimized);
    }

}