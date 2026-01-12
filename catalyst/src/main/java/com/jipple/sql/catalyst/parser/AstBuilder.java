package com.jipple.sql.catalyst.parser;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAlias;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAttribute;
import com.jipple.sql.catalyst.parser.SqlBaseParser.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.stream.Collectors;

public class AstBuilder extends SqlBaseBaseVisitor<Object> {

    protected <T> T typedVisit(ParseTree ctx) {
        return (T) ctx.accept(this);
    }


    @Override
    public Expression visitSingleExpression(SingleExpressionContext ctx) {
        return visitNamedExpression(ctx.namedExpression());
    }

    @Override
    public List<Expression> visitSingleExpressions(SingleExpressionsContext ctx) {
        List<Expression> expressions = visitNamedExpressionSeq(ctx.namedExpressionSeq());
        return expressions.stream().map(e -> {
            if (e instanceof NamedExpression) {
                return e;
            } else {
                return new UnresolvedAlias(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Expression visitNamedExpression(NamedExpressionContext ctx) {
        Expression e = expression(ctx.expression());
        if (e == null) {
            System.out.println(ctx.expression().getText());
            throw new RuntimeException("can not parse expression:" + ctx.expression().getText());
        }
        if (ctx.name != null) {
            return e;
        } else if (ctx.identifierList() != null) {
            return e;
        } else {
            return e;
        }
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
     * Create an UnresolvedAttribute expression  if it is a regex
     * quoted in ``
     */
    @Override
    public Expression visitColumnReference(ColumnReferenceContext ctx) {
        return UnresolvedAttribute.quoted(ctx.getText()); //创建列引用
    }

}
