package com.jipple.sql.catalyst.parser;

import com.jipple.sql.catalyst.analysis.unresolved.UnresolvedFunction;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAlias;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAttribute;
import com.jipple.sql.catalyst.parser.SqlBaseParser.*;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.OneRowRelation;
import com.jipple.sql.catalyst.plans.logical.Project;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jipple.sql.catalyst.util.JippleParserUtils.withOrigin;
import static com.jipple.sql.types.DataTypes.*;

public class AstBuilder extends SqlBaseBaseVisitor<Object> {

    protected <T> T typedVisit(ParseTree ctx) {
        return (T) ctx.accept(this);
    }


    @Override
    public Expression visitSingleExpression(SingleExpressionContext ctx) {
       return withOrigin(ctx, () -> visitNamedExpression(ctx.namedExpression()));
    }

    @Override
    public List<Expression> visitSingleExpressions(SingleExpressionsContext ctx) {
        return withOrigin(ctx, () -> {
            List<Expression> expressions = visitNamedExpressionSeq(ctx.namedExpressionSeq());
            return expressions.stream().map(e -> {
                if (e instanceof NamedExpression) {
                    return e;
                } else {
                    return new UnresolvedAlias(e);
                }
            }).collect(Collectors.toList());
        });
    }

    @Override
    public LogicalPlan visitSingleStatement(SingleStatementContext ctx) {
        return withOrigin(ctx, () -> (LogicalPlan)visit(ctx.statement()));
    }

    /* ********************************************************************************************
     * Plan parsing
     * ******************************************************************************************** */
    protected LogicalPlan plan(ParserRuleContext tree) {
        return typedVisit(tree);
    }

    @Override
    public LogicalPlan visitQuery(QueryContext ctx) {
        return withOrigin(ctx, () ->
            plan(ctx.queryTerm()).optionalMap(ctx.queryOrganization(), this::withQueryResultClauses)
        );
    }

    /**
     * Add ORDER BY/SORT BY/CLUSTER BY/DISTRIBUTE BY/LIMIT/WINDOWS clauses to the logical plan. These
     * clauses determine the shape (ordering/partitioning/rows) of the query result.
     */
    private LogicalPlan withQueryResultClauses(QueryOrganizationContext ctx, LogicalPlan query) {
        return withOrigin(ctx, () -> {
            // withOrder
            LogicalPlan withOrder = query;
            // LIMIT
            return withOrder;
        });
    }

    @Override
    public LogicalPlan visitRegularQuerySpecification(RegularQuerySpecificationContext ctx) {
        LogicalPlan relation = new OneRowRelation().optional(ctx.fromClause(), () ->
            visitFromClause(ctx.fromClause())
        );

        LogicalPlan withFilter = relation;

        List<Expression> expressions = visitNamedExpressionSeq(ctx.selectClause().namedExpressionSeq());
        // Add aggregation or a project.
        List<Expression> namedExpressions = expressions.stream().map(e -> {
            if (e instanceof NamedExpression) {
                return e;
            } else {
                return new UnresolvedAlias(e);
            }
        }).collect(Collectors.toList());

        if (namedExpressions.size() > 0) {
            return new Project(namedExpressions, withFilter);
        } else {
            return withFilter;
        }
    }

    @Override
    public LogicalPlan visitFromClause(FromClauseContext ctx) {
        return withOrigin(ctx, () -> {
            RelationContext relation = ctx.relation();
            LogicalPlan table = plan(relation);
            //return withJoinRelations(table, relation);
            return table;
        });
    }



    @Override
    public Expression visitNamedExpression(NamedExpressionContext ctx) {
        return withOrigin(ctx, () -> {
            Expression e = expression(ctx.expression());
            if (e == null) {
                System.out.println(ctx.expression().getText());
                throw new RuntimeException("can not parse expression:" + ctx.expression().getText());
            }
            if (ctx.name != null) {
                return new Alias(e, ctx.name.getText());
            } else if (ctx.identifierList() != null) {
                throw new UnsupportedOperationException("can not parse expression:" + ctx.getText());
            } else {
                return e;
            }
        });
    }

    @Override
    public List<Expression> visitNamedExpressionSeq(NamedExpressionSeqContext ctx) {
        return ctx.namedExpression().stream().map(e -> this.<Expression>typedVisit(e)).collect(Collectors.toList());
    }

    /* ********************************************************************************************
     * Expression parsing
     * ******************************************************************************************** */

    /**
     * Create an expression from the given context. This method just passes the context on to the
     * visitor and only takes care of typing (We assume that the visitor returns an Expression here).
     */
    protected Expression expression(ParserRuleContext ctx) {
        return typedVisit(ctx);
    }

    /**
     * Create a unary arithmetic expression. The following arithmetic operators are supported:
     * - Plus: '+'
     * - Minus: '-'
     * - Bitwise Not: '~'
     */
    @Override
    public Expression visitArithmeticUnary(ArithmeticUnaryContext ctx) {
        return withOrigin(ctx, () -> {
            Expression value = expression(ctx.valueExpression());
            switch (ctx.operator.getType()) {
                //case SqlBaseParser.PLUS:
                    //return new UnaryPositive(value);
                //case SqlBaseParser.MINUS:
                    //return new UnaryMinus(value);
                default:
                    throw new ParseException("Unsupported arithmetic operator: " + ctx.operator.getText(),  ctx);
            }
        });
    }

    /**
     * Create an UnresolvedAttribute expression  if it is a regex
     * quoted in ``
     */
    @Override
    public Expression visitColumnReference(ColumnReferenceContext ctx) {
        return withOrigin(ctx, () -> UnresolvedAttribute.quoted(ctx.getText())); //创建列引用
    }

    /**
     * 没在.g4文件定义的函数应该都是这个入口, udf就是这个入口
     * 对应的处理规则在[[ResolveFunctions]]
     * 通过lookupFunction函数能找到我们注册的函数
     * Create a (windowed) Function expression.
     */
    @Override
    public Expression visitFunctionCall(FunctionCallContext ctx) {
        return withOrigin(ctx, () -> {
            // Create the function call.
            String name = ctx.functionName().getText();
            List<Expression> arguments = ctx.argument.stream().map(this::expression).map(e ->
                    // Transform COUNT(*) into COUNT(1).
                    e
            ).collect(Collectors.toList());
            Optional<Expression> filter = Optional.ofNullable(ctx.where).map(this::expression);
            return new UnresolvedFunction(getFunctionIdentifier(ctx.functionName()), arguments, false, filter);
        });
    }

    /**
     * Get a function identifier consist by database (optional) and name.
     */
    protected List<String> getFunctionIdentifier(FunctionNameContext ctx) {
        if (ctx.qualifiedName() != null) {
            return ctx.qualifiedName().identifier().stream().map(i -> i.getText()).collect(Collectors.toList());
        } else {
            return List.of(ctx.getText());
        }
    }

    /**
     * Create a NULL literal expression.
     */
    @Override
    public Object visitNullLiteral(NullLiteralContext ctx) {
        return withOrigin(ctx, () -> Literal.of(null));
    }

    /**
     * Create a Boolean literal expression.
     */
    @Override
    public Object visitBooleanLiteral(BooleanLiteralContext ctx) {
        return withOrigin(ctx, () -> {
            if (Boolean.parseBoolean(ctx.getText())) {
                return Literal.of(true);
            } else {
                return Literal.of(false);
            }
        });
    }

    /**
     * Create an integral literal expression. The code selects the most narrow integral type
     * possible, either a BigDecimal, a Long or an Integer is returned.
     */
    @Override
    public Object visitIntegerLiteral(IntegerLiteralContext ctx) {
        return withOrigin(ctx , () -> {
            BigDecimal v = new BigDecimal(ctx.getText());
            if (noArithmeticException(() -> v.intValueExact())) {
                return Literal.of(v.intValue());
            } else if (noArithmeticException(() -> v.longValueExact())) {
                return Literal.of(v.longValue());
            } else {
                return Literal.of(v.doubleValue());
            }
        });
    }

    private boolean noArithmeticException(Runnable body) {
        try {
            body.run();
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    /**
     * Create a decimal literal for a regular decimal number.
     */
    @Override
    public Object visitDecimalLiteral(DecimalLiteralContext ctx) {
        return withOrigin(ctx, () ->
            numericLiteral(ctx, ctx.getText(), new BigDecimal(Double.MIN_VALUE), new BigDecimal(Double.MAX_VALUE), DOUBLE.simpleString(), Double::parseDouble)
        );
    }

    /** Create a numeric literal expression. */
    private Literal numericLiteral(NumberContext ctx, String rawStrippedQualifier, BigDecimal minValue, BigDecimal maxValue, String typeName, Function<String, Object> converter) {
        try {
            BigDecimal rawBigDecimal = new BigDecimal(rawStrippedQualifier);
            if (rawBigDecimal.compareTo(minValue) < 0 || rawBigDecimal.compareTo(maxValue) > 0) {
                throw new ParseException(String.format("Numeric literal %s does not fit in range [%s, %s] for type %s", rawStrippedQualifier, minValue, maxValue, typeName), ctx);
            }
            return Literal.of(converter.apply(rawStrippedQualifier));
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage(), ctx);
        }
    }

    /**
     * Create a Byte Literal expression.
     */
    @Override
    public Object visitTinyIntLiteral(TinyIntLiteralContext ctx) {
        return withOrigin(ctx, () -> {
            String text = ctx.getText();
            String rawStrippedQualifier = text.substring(0, text.length() - 1);
            return numericLiteral(ctx, rawStrippedQualifier, new BigDecimal(Integer.MIN_VALUE), new BigDecimal(Integer.MAX_VALUE), INTEGER.simpleString(), Integer::parseInt);
        });
    }

    /**
     * Create a Short Literal expression.
     */
    @Override
    public Object visitSmallIntLiteral(SmallIntLiteralContext ctx) {
        return withOrigin(ctx, () -> {
            String text = ctx.getText();
            String rawStrippedQualifier = text.substring(0, text.length() - 1);
            return numericLiteral(ctx, rawStrippedQualifier, new BigDecimal(Integer.MIN_VALUE), new BigDecimal(Integer.MAX_VALUE), INTEGER.simpleString(), Integer::parseInt);
        });
    }

    /**
     * Create a Long Literal expression.
     */
    @Override
    public Object visitBigIntLiteral(BigIntLiteralContext ctx) {
        return withOrigin(ctx, () -> {
            String text = ctx.getText();
            String rawStrippedQualifier = text.substring(0, text.length() - 1);
            return numericLiteral(ctx, rawStrippedQualifier, new BigDecimal(Long.MIN_VALUE), new BigDecimal(Long.MAX_VALUE), LONG.simpleString(), Long::parseLong);
        });
    }

    /**
     * Create a Float Literal expression.
     */
    @Override
    public Object visitFloatLiteral(FloatLiteralContext ctx) {
        return withOrigin(ctx, () -> {
            String text = ctx.getText();
            String rawStrippedQualifier = text.substring(0, text.length() - 1);
            return numericLiteral(ctx, rawStrippedQualifier, new BigDecimal(Float.MIN_VALUE), new BigDecimal(Float.MAX_VALUE), FLOAT.simpleString(), Float::parseFloat);
        });
    }

    /**
     * Create a Double Literal expression.
     */
    @Override
    public Object visitDoubleLiteral(DoubleLiteralContext ctx) {
        return withOrigin(ctx, () -> {
            String text = ctx.getText();
            String rawStrippedQualifier = text.substring(0, text.length() - 1);
            return numericLiteral(ctx, rawStrippedQualifier, new BigDecimal(Double.MIN_VALUE), new BigDecimal(Double.MAX_VALUE), DOUBLE.simpleString(), Double::parseDouble);
        });
    }

}
