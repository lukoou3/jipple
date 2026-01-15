package com.jipple.sql.catalyst.parser;

import com.jipple.sql.catalyst.analysis.unresolved.UnresolvedFunction;
import com.jipple.sql.catalyst.expressions.*;
import com.jipple.sql.catalyst.expressions.arithmetic.*;
import com.jipple.sql.catalyst.expressions.named.*;
import com.jipple.sql.catalyst.expressions.nvl.*;
import com.jipple.sql.catalyst.expressions.predicate.*;
import com.jipple.sql.catalyst.expressions.regexp.*;
import com.jipple.sql.catalyst.parser.SqlBaseParser.*;
import com.jipple.sql.catalyst.plans.logical.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
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

        // Add where.
        LogicalPlan withFilter = relation.optionalMap(ctx.whereClause(), this::withWhereClause);

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

    /**
     * Create an aliased table reference. This is typically used in FROM clauses.
     */
    @Override
    public LogicalPlan visitTableName(TableNameContext ctx) {
        return withOrigin(ctx, () -> {
            List<String> tableId = visitMultipartIdentifier(ctx.multipartIdentifier());
            LogicalPlan table = mayApplyAliasPlan(ctx.tableAlias(), new UnresolvedRelation(tableId));
            return table;
        });
    }

    /**
     * Create an alias (SubqueryAlias) for a sub-query. This is practically the same as
     * visitAliasedRelation and visitNamedExpression, ANTLR4 however requires us to use 3 different
     * hooks. We could add alias names for output columns, for example:
     * {{{
     *   SELECT col1, col2 FROM testData AS t(col1, col2)
     * }}}
     */
    @Override
    public LogicalPlan visitAliasedQuery(AliasedQueryContext ctx) {
        return withOrigin(ctx, () -> {
            LogicalPlan relation = plan(ctx.query());
            if (ctx.tableAlias().strictIdentifier() == null) {
                return new SubqueryAlias("__auto_generated_subquery_name", relation);
            } else {
                return mayApplyAliasPlan(ctx.tableAlias(), relation);
            }
        });
    }

    /**
     * Create an alias ([[SubqueryAlias]]) for a [[LogicalPlan]].
     */
    private LogicalPlan aliasPlan(TableAliasContext alias, LogicalPlan plan) {
        return new SubqueryAlias(alias.getText(), plan);
    }

    /**
     * If aliases specified in a FROM clause, create a subquery alias ([[SubqueryAlias]]) and
     * column aliases for a [[LogicalPlan]].
     */
    private LogicalPlan mayApplyAliasPlan(TableAliasContext tableAlias, LogicalPlan plan) {
        if (tableAlias.strictIdentifier() != null ) {
            // 定义了别名
            return new SubqueryAlias(tableAlias.strictIdentifier().getText(), plan);
        } else {
            return plan;
        }
    }

    /**
     * Create a logical plan using a where clause.
     */
    private LogicalPlan withWhereClause(WhereClauseContext ctx, LogicalPlan plan) {
        return withOrigin(ctx, () -> new Filter(expression(ctx.booleanExpression()), plan));
    }

    /**
     * Create a multi-part identifier.
     */
    @Override
    public List<String> visitMultipartIdentifier(MultipartIdentifierContext ctx) {
        return withOrigin(ctx, () -> ctx.parts.stream().map(x -> x.getText()).collect(Collectors.toList()));
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
     * Invert a boolean expression.
     */
    @Override
    public Object visitLogicalNot(LogicalNotContext ctx) {
        return withOrigin(ctx, () -> new Not(expression(ctx.booleanExpression())));
    }

    /**
     * Create a predicated expression. A predicated expression is a normal expression with a
     * predicate attached to it, for example:
     * {{{ a + 1 IS NULL }}}
     */
    @Override
    public Expression visitPredicated(PredicatedContext ctx) {
        return withOrigin(ctx, () -> {
            Expression e = expression(ctx.valueExpression());
            if (ctx.predicate() != null) {
                return withPredicate(e, ctx.predicate());
            }
            return e;
        });
    }

    /**
     * Add a predicate to the given expression. Supported expressions are:
     * - (NOT) BETWEEN
     * - (NOT) IN
     * - (NOT) LIKE (ANY | SOME | ALL)
     * - (NOT) RLIKE
     * - IS (NOT) NULL.
     * - IS (NOT) (TRUE | FALSE | UNKNOWN)
     * - IS (NOT) DISTINCT FROM
     */
    private Expression withPredicate(Expression e, PredicateContext ctx) {
        return withOrigin(ctx, () -> {
            // Invert a predicate if it has a valid NOT clause.
            Function<Expression, Expression> invertIfNotDefined = expression -> {
                if (ctx.NOT() == null) {
                    return expression;
                }
                return new Not(expression);
            };

            // Create the predicate.
            switch (ctx.kind.getType()) {
                case SqlBaseParser.BETWEEN:
                    // BETWEEN is translated to lower <= e && e <= upper
                    return invertIfNotDefined.apply(new And(
                            new GreaterThanOrEqual(e, expression(ctx.lower)),
                            new LessThanOrEqual(e, expression(ctx.upper))));
                case SqlBaseParser.IN:
                    List<Expression> values = new ArrayList<>();
                    for (ExpressionContext valueCtx : ctx.expression()) {
                        values.add(expression(valueCtx));
                    }
                    return invertIfNotDefined.apply(new In(e, values));
                case SqlBaseParser.LIKE:
                    String escapeString = null;
                    if (ctx.escapeChar != null) {
                        escapeString = ctx.escapeChar.getText();
                        if (escapeString.length() >= 2 &&
                                escapeString.charAt(0) == escapeString.charAt(escapeString.length() - 1)) {
                            escapeString = escapeString.substring(1, escapeString.length() - 1);
                        }
                        if (escapeString.length() != 1) {
                            throw new ParseException("Invalid escape string. Escape string must contain only one character.", ctx);
                        }
                    }
                    char escapeChar = escapeString == null ? '\\' : escapeString.charAt(0);
                    return invertIfNotDefined.apply(new Like(e, expression(ctx.pattern), escapeChar));
                case SqlBaseParser.RLIKE:
                    return invertIfNotDefined.apply(new RLike(e, expression(ctx.pattern)));
                case SqlBaseParser.NULL:
                    if (ctx.NOT() != null) {
                        return new IsNotNull(e);
                    }
                    return new IsNull(e);
                case SqlBaseParser.TRUE:
                    if (ctx.NOT() == null) {
                        return new EqualNullSafe(e, Literal.of(true));
                    }
                    return new Not(new EqualNullSafe(e, Literal.of(true)));
                case SqlBaseParser.FALSE:
                    if (ctx.NOT() == null) {
                        return new EqualNullSafe(e, Literal.of(false));
                    }
                    return new Not(new EqualNullSafe(e, Literal.of(false)));
                default:
                    throw new ParseException("Unsupported predicate: " + ctx.getText(), ctx);
            }
        });
    }

    /**
     * Combine a number of boolean expressions into a balanced expression tree. These expressions are
     * either combined by a logical {@link And} or a logical {@link Or}.
     *
     * A balanced binary tree is created because regular left recursive trees cause considerable
     * performance degradations and can cause stack overflows.
     */
    @Override
    public Expression visitLogicalBinary(LogicalBinaryContext ctx) {
        return withOrigin(ctx, () -> {
            int expressionType = ctx.operator.getType();
            BiFunction<Expression, Expression, Expression> expressionCombiner;
            switch (expressionType) {
                case SqlBaseParser.AND:
                    expressionCombiner = And::new;
                    break;
                case SqlBaseParser.OR:
                    expressionCombiner = Or::new;
                    break;
                default:
                    throw new ParseException("Unsupported logical operator: " + ctx.operator.getText(), ctx);
            }

            // Collect all similar left hand contexts.
            List<ParserRuleContext> contexts = new ArrayList<>();
            contexts.add(ctx.right);
            ParserRuleContext current = ctx.left;
            while (true) {
                if (current instanceof LogicalBinaryContext lbc && lbc.operator.getType() == expressionType) {
                    contexts.add(lbc.right);
                    current = lbc.left;
                } else {
                    contexts.add(current);
                    break;
                }
            }

            // Reverse the contexts to have them in the same sequence as in the SQL statement & turn them
            // into expressions.
            List<Expression> expressions = new ArrayList<>(contexts.size());
            for (int i = contexts.size() - 1; i >= 0; i--) {
                expressions.add(expression(contexts.get(i)));
            }

            // Create a balanced tree.
            return reduceToExpressionTree(expressions, 0, expressions.size() - 1, expressionCombiner);
        });
    }

    private Expression reduceToExpressionTree(List<Expression> expressions,
                                              int low,
                                              int high,
                                              BiFunction<Expression, Expression, Expression> combiner) {
        int range = high - low;
        if (range == 0) {
            return expressions.get(low);
        } else if (range == 1) {
            return combiner.apply(expressions.get(low), expressions.get(high));
        } else {
            int mid = low + range / 2;
            return combiner.apply(
                    reduceToExpressionTree(expressions, low, mid, combiner),
                    reduceToExpressionTree(expressions, mid + 1, high, combiner));
        }
    }

    /**
     * Create a comparison expression. This compares two expressions. The following comparison
     * operators are supported:
     * - Equal: '=' or '=='
     * - Null-safe Equal: '<=>'
     * - Not Equal: '<>' or '!='
     * - Less than: '<'
     * - Less then or Equal: '<='
     * - Greater than: '>'
     * - Greater then or Equal: '>='
     */
    @Override
    public Expression visitComparison(ComparisonContext ctx) {
        return withOrigin(ctx, () -> {
            Expression left = expression(ctx.left);
            Expression right = expression(ctx.right);
            TerminalNode operator = (TerminalNode)ctx.comparisonOperator().getChild(0);
            switch (operator.getSymbol().getType()) {
                case SqlBaseParser.EQ:
                    return new EqualTo(left, right);
                case SqlBaseParser.NSEQ:
                    return new EqualNullSafe(left, right);
                case SqlBaseParser.NEQ | SqlBaseParser.NEQJ:
                    return new Not(new EqualTo(left, right));
                case SqlBaseParser.LT:
                    return new LessThan(left, right);
                case SqlBaseParser.LTE:
                    return new LessThanOrEqual(left, right);
                case SqlBaseParser.GT:
                    return new GreaterThan(left, right);
                case SqlBaseParser.GTE:
                    return new GreaterThanOrEqual(left, right);
                default:
                    throw new ParseException("Unsupported comparison operator: " + operator.getText(),  ctx);
            }
        });
    }

    /**
     * Create a binary arithmetic expression. The following arithmetic operators are supported:
     * - Multiplication: '*'
     * - Division: '/'
     * - Hive Long Division: 'DIV'
     * - Modulo: '%'
     * - Addition: '+'
     * - Subtraction: '-'
     * - Binary AND: '&'
     * - Binary XOR
     * - Binary OR: '|'
     */
    @Override
    public Expression visitArithmeticBinary(ArithmeticBinaryContext ctx) {
        return withOrigin(ctx, () -> {
            Expression left = expression(ctx.left);
            Expression right = expression(ctx.right);
            switch (ctx.operator.getType()) {
                case SqlBaseParser.ASTERISK:
                    return new Multiply(left, right);
                case SqlBaseParser.SLASH:
                    return new Divide(left, right);
                case SqlBaseParser.PERCENT:
                    return new Remainder(left, right);
                case SqlBaseParser.DIV:
                    return new IntegralDivide(left, right);
                case SqlBaseParser.PLUS:
                    return new Add(left, right);
                    case SqlBaseParser.MINUS:
                        return new Subtract(left, right);
                default:
                    throw new ParseException("Unsupported arithmetic operator: " + ctx.operator.getText(),  ctx);
            }
        });
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
                case SqlBaseParser.PLUS:
                    return new UnaryPositive(value);
                case SqlBaseParser.MINUS:
                    return new UnaryMinus(value);
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

    /**
     * Create a String literal expression.
     */
    @Override
    public Object visitStringLiteral(StringLiteralContext ctx) {
        return withOrigin(ctx, () -> Literal.of(createString(ctx)));
    }

    /**
     * Create a String from a string literal context. This supports multiple consecutive string
     * literals, these are concatenated, for example this expression "'hello' 'world'" will be
     * converted into "helloworld".
     *
     * Special characters can be escaped by using Hive/C-style escaping.
     */
    private String createString(StringLiteralContext ctx) {
        if (false) {
            return ctx.STRING().stream().map(ParserUtils::stringWithoutUnescape).collect(Collectors.joining());
        } else {
            return ctx.STRING().stream().map(ParserUtils::string).collect(Collectors.joining());
        }
    }


}
