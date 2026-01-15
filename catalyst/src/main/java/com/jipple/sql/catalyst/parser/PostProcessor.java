package com.jipple.sql.catalyst.parser;

import com.jipple.sql.errors.QueryParsingErrors;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.function.Function;

/**
 * The post-processor validates & cleans-up the parse tree during the parse process.
 */
public class PostProcessor extends SqlBaseBaseListener  {

    /** Throws error message when exiting a explicitly captured wrong identifier rule */
    @Override
    public void exitErrorIdent(SqlBaseParser.ErrorIdentContext ctx) {
        String ident = ctx.getParent().getText();
        throw QueryParsingErrors.invalidIdentifierError(ident, ctx);
    }

    /** Remove the back ticks from an Identifier. */
    @Override
    public void exitQuotedIdentifier(SqlBaseParser.QuotedIdentifierContext ctx) {
        replaceTokenByIdentifier(ctx, 1, token -> {
            // Remove the double back ticks in the string.
            token.setText(token.getText().replace("``", "`"));
            return token;
        });
    }

    /** Treat non-reserved keywords as Identifiers. */
    @Override
    public void exitNonReserved(SqlBaseParser.NonReservedContext ctx) {
        replaceTokenByIdentifier(ctx, 0, token -> token);
    }

    private void replaceTokenByIdentifier(ParserRuleContext ctx, int stripMargins, Function<CommonToken, CommonToken> f) {
        ParserRuleContext parent = ctx.getParent();
        parent.removeLastChild();
        Token token = (Token) ctx.getChild(0).getPayload();
        CommonToken newToken = new CommonToken(
            new org.antlr.v4.runtime.misc.Pair<>(token.getTokenSource(), token.getInputStream()),
            SqlBaseParser.IDENTIFIER,
            token.getChannel(),
            token.getStartIndex() + stripMargins,
            token.getStopIndex() - stripMargins
        );
        parent.addChild(new TerminalNodeImpl(f.apply(newToken)));
    }

}
