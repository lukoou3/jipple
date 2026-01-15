package com.jipple.sql.catalyst.parser;

import com.jipple.error.JippleThrowable;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.trees.WithOrigin;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractSqlParser {
    protected abstract AstBuilder astBuilder();

    public Expression parseExpression(String sqlText) {
        return parse(sqlText, parser -> {
            var ctx = parser.singleExpression();
            return astBuilder().visitSingleExpression(ctx);
        });
    }

    public List<Expression> parseExpressions(String sqlText) {
        return parse(sqlText, parser -> {
            var ctx = parser.singleExpressions();
            return astBuilder().visitSingleExpressions(ctx);
        });
    }


    /** Creates LogicalPlan for a given SQL string of query. */
    public LogicalPlan parseQuery(String sqlText) {
        return parse(sqlText, parser -> {
            var ctx = parser.query();
            return astBuilder().visitQuery(ctx);
        });
    }

    /** Creates LogicalPlan for a given SQL string. */
    public LogicalPlan parsePlan(String sqlText) {
        return parse(sqlText, parser -> {
            var ctx = parser.singleStatement();
            return astBuilder().visitSingleStatement(ctx);
        });
    }

    protected <T> T parse(String command, Function<SqlBaseParser, T> toResult) {
        SqlBaseLexer lexer = new SqlBaseLexer(new UpperCaseCharStream(CharStreams.fromString(command)));
        ParseErrorListener parseErrorListener = new ParseErrorListener();
        PostProcessor postProcessor = new PostProcessor();
        lexer.removeErrorListeners();
        lexer.addErrorListener(parseErrorListener);

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        parser.addParseListener(postProcessor);
        parser.removeErrorListeners();
        parser.addErrorListener(parseErrorListener);
        parser.legacy_setops_precedence_enbled = false;
        parser.legacy_exponent_literal_as_decimal_enabled = false;
        parser.SQL_standard_keyword_behavior = false;

        // https://github.com/antlr/antlr4/issues/192#issuecomment-15238595
        // Save a great deal of time on correct inputs by using a two-stage parsing strategy.
        try {
            try {
                // 第一阶段：使用较快的 SLL 模式 + BailErrorStrategy（快速失败策略）
                parser.setErrorHandler(new JippleParserBailErrorStrategy());
                parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
                return toResult.apply(parser);  // 注意：这里假设 toResult 返回结果
            }
            catch (ParseCancellationException e) {
                // SLL 失败，回退到第二阶段：完整 LL 模式 + 正常错误恢复策略
                tokenStream.seek(0);           // 重置输入流到开头
                parser.reset();                // 重置解析器状态
                // 再次尝试解析
                parser.setErrorHandler(new JippleParserErrorStrategy());
                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                return toResult.apply(parser);
            }
        }
        catch (ParseException e) {
            if (e.command != null) {
                throw e;
            } else {
                throw e.withCommand(command);
            }
        }
        catch (Exception e) {
            if (e instanceof JippleThrowable && e instanceof WithOrigin) {
                JippleThrowable jippleThrowable = (JippleThrowable) e;
                WithOrigin withOrigin = (WithOrigin) e;
                throw new ParseException(
                        command,
                        e.getMessage(),
                        withOrigin.origin(),
                        withOrigin.origin(),
                        jippleThrowable.getErrorClass(),
                        jippleThrowable.getMessageParameters(),
                        jippleThrowable.getQueryContext()
                );
            }
            throw e;
        }
    }

    private static class UpperCaseCharStream implements CharStream {
        private final CodePointCharStream wrapped;

        private UpperCaseCharStream(CodePointCharStream wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public String getText(Interval interval) {
            return wrapped.getText(interval);
        }

        @Override
        public void consume() {
            wrapped.consume();
        }

        @Override
        public int LA(int i) {
            int la = wrapped.LA(i);
            return la == 0 || la == IntStream.EOF ? la : Character.toUpperCase(la);
        }

        @Override
        public int mark() {
            return wrapped.mark();
        }

        @Override
        public void release(int marker) {
            wrapped.release(marker);
        }

        @Override
        public int index() {
            return wrapped.index();
        }

        @Override
        public void seek(int i) {
            wrapped.seek(i);
        }

        @Override
        public int size() {
            return wrapped.size();
        }

        @Override
        public String getSourceName() {
            return wrapped.getSourceName();
        }
    }
}
