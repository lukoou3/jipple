package com.jipple.sql;

import com.jipple.sql.catalyst.QueryPlanningTracker;
import com.jipple.sql.catalyst.analysis.FunctionRegistry;
import com.jipple.sql.catalyst.analysis.SimpleFunctionRegistry;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;
import com.jipple.sql.catalyst.parser.CatalystSqlParser;
import com.jipple.sql.catalyst.plans.logical.*;
import com.jipple.sql.catalyst.types.DataTypeUtils;
import com.jipple.sql.execution.QueryExecution;
import com.jipple.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class JippleSession {
    private static final Logger log = LoggerFactory.getLogger(JippleSession.class);
    
    private static volatile JippleSession instance;
    
    private final SQLConf sqlConf;
    private final CatalystSqlParser sqlParser;
    private final SimpleFunctionRegistry functionRegistry;
    private final Map<String, LogicalPlan> lookupTables;

    private JippleSession(SQLConf sqlConf) {
        this.sqlConf = sqlConf;
        this.sqlParser = new CatalystSqlParser();
        SQLConf.setSQLConfGetter(() -> sqlConf);
        this.functionRegistry = FunctionRegistry.builtin.clone();
        this.lookupTables = getLookupTables();
    }

    public static JippleSession get() {
        return get(SQLConf.get());
    }

    public static JippleSession get(SQLConf sqlConf) {
        if (instance == null) {
            synchronized (JippleSession.class) {
                if (instance == null) {
                    instance = new JippleSession(sqlConf);
                }
            }
        }
        return instance;
    }

    public LogicalPlan sqlPlan(String sqlText, StructType schema) {
        return sqlPlan(sqlText, Map.of("tbl", new RelationPlaceholder(schema.toAttributes(), "tbl")));
    }

    public LogicalPlan sqlPlan(String sqlText, LogicalPlan child) {
        return sqlPlan(sqlText, Map.of("tbl", child));
    }

    public LogicalPlan sqlPlan(String sqlText, List<Map.Entry<String, StructType>> schemas) {
        Map<String, LogicalPlan> tempViews = schemas.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new RelationPlaceholder(entry.getValue().toAttributes(), entry.getKey())
                ));
        return sqlPlan(sqlText, tempViews);
    }

    public LogicalPlan sqlPlan(String sqlText, Map<String, LogicalPlan> tempViews) {
        QueryPlanningTracker tracker = new QueryPlanningTracker();
        LogicalPlan plan = tracker.measurePhase(QueryPlanningTracker.PARSING, () ->
                sqlParser.parsePlan(sqlText)
        );
        Map<String, LogicalPlan> combinedViews = new HashMap<>(lookupTables);
        combinedViews.putAll(tempViews);
        QueryExecution logicalPlan = new QueryExecution(combinedViews, functionRegistry, plan, tracker);
        logicalPlan.assertAnalyzed();
        log.info("sqlPlan for {} :", sqlText);
        log.info("analyzed plan:\n{}", logicalPlan.analyzed());
        log.info("optimized plan:\n{}", logicalPlan.optimizedPlan());
        return logicalPlan.optimizedPlan();
    }

    public Project selectExprs(List<String> exprs, StructType schema) {
        QueryPlanningTracker tracker = new QueryPlanningTracker();
        List<Expression> expressions = tracker.measurePhase(QueryPlanningTracker.PARSING, () -> {
            return exprs.stream().map(expr -> {
                Expression e = sqlParser.parseExpression(expr);
                if (e instanceof NamedExpression) {
                    return e;
                } else {
                    throw new IllegalArgumentException(e + " is not a named expression");
                }
            }).collect(Collectors.toList());
        });
        LogicalPlan plan = new Project(expressions, new RelationPlaceholder(schema.toAttributes(), "tbl"));
        QueryExecution logicalPlan = new QueryExecution(Collections.emptyMap(), functionRegistry, plan, tracker);
        logicalPlan.assertAnalyzed();
        return (Project) logicalPlan.optimizedPlan();
    }

    public Project selectExprs(String exprs, StructType schema) {
        QueryPlanningTracker tracker = new QueryPlanningTracker();
        List<Expression> expressions = tracker.measurePhase(QueryPlanningTracker.PARSING, () ->
                sqlParser.parseExpressions(exprs)
        );
        LogicalPlan plan = new Project(expressions, new RelationPlaceholder(schema.toAttributes(), "tbl"));
        QueryExecution logicalPlan = new QueryExecution(Collections.emptyMap(), functionRegistry, plan, tracker);
        logicalPlan.assertAnalyzed();
        return (Project) logicalPlan.optimizedPlan();
    }

    public Filter parseFilter(String condition, StructType schema) {
        return parseFilter(condition, new RelationPlaceholder(schema.toAttributes(), "tbl"));
    }

    public Filter parseFilter(String condition, LogicalPlan child) {
        QueryPlanningTracker tracker = new QueryPlanningTracker();
        Expression expression = tracker.measurePhase(QueryPlanningTracker.PARSING, () ->
                sqlParser.parseExpression(condition)
        );
        LogicalPlan plan = new Filter(expression, child);
        QueryExecution logicalPlan = new QueryExecution(Collections.emptyMap(), functionRegistry, plan, tracker);
        logicalPlan.assertAnalyzed();
        return (Filter) logicalPlan.optimizedPlan();
    }

    public Filter parseCombinedFilter(String condition, List<Map.Entry<String, StructType>> schemas) {
        QueryPlanningTracker tracker = new QueryPlanningTracker();
        Expression expression = tracker.measurePhase(QueryPlanningTracker.PARSING, () ->
                sqlParser.parseExpression(condition)
        );
        List<com.jipple.tuple.Tuple2<String, List<com.jipple.sql.catalyst.expressions.named.Attribute>>> outputs = schemas.stream()
                .map(entry -> com.jipple.tuple.Tuple2.of(entry.getKey(), entry.getValue().toAttributes()))
                .collect(Collectors.toList());
        LogicalPlan plan = new Filter(expression, RelationPlaceholder.fromTableAttrs(outputs, "tbl"));
        QueryExecution logicalPlan = new QueryExecution(Collections.emptyMap(), functionRegistry, plan, tracker);
        logicalPlan.assertAnalyzed();
        return (Filter) logicalPlan.optimizedPlan();
    }

    public Expr parseExpr(String sql, StructType schema) {
        return parseExpr(sql, DataTypeUtils.toAttributes(schema));
    }

    public Expr parseExpr(String sql, List<Attribute> schema) {
        QueryPlanningTracker tracker = new QueryPlanningTracker();
        Expression expression = tracker.measurePhase(QueryPlanningTracker.PARSING, () ->
                sqlParser.parseExpression(sql)
        );
        Expression e;
        if (expression instanceof NamedExpression) {
            e = expression;
        } else {
            e = new Alias(expression, "v");
        }
        LogicalPlan plan = new Expr(e, new RelationPlaceholder(schema, "tbl"));
        QueryExecution logicalPlan = new QueryExecution(Collections.emptyMap(), functionRegistry, plan, tracker);
        logicalPlan.assertAnalyzed();
        return (Expr) logicalPlan.optimizedPlan();
    }

    private Map<String, LogicalPlan> getLookupTables() {
        return Collections.emptyMap();
    }
}

