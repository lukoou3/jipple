package com.jipple.sql.catalyst.parser;

import com.jipple.sql.catalyst.expressions.Expression;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;

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

    protected <T> T parse(String command, Function<SqlBaseParser, T> toResult) {
        SqlBaseLexer lexer = new SqlBaseLexer(new UpperCaseCharStream(CharStreams.fromString(command)));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        parser.legacy_setops_precedence_enbled = false;
        parser.legacy_exponent_literal_as_decimal_enabled = false;
        parser.SQL_standard_keyword_behavior = false;

        return toResult.apply(parser);
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

/*
private[parser] class UpperCaseCharStream(wrapped: CodePointCharStream) extends CharStream {
  override def consume(): Unit = wrapped.consume
  override def getSourceName(): String = wrapped.getSourceName
  override def index(): Int = wrapped.index
  override def mark(): Int = wrapped.mark
  override def release(marker: Int): Unit = wrapped.release(marker)
  override def seek(where: Int): Unit = wrapped.seek(where)
  override def size(): Int = wrapped.size

  override def getText(interval: Interval): String = wrapped.getText(interval)

  override def LA(i: Int): Int = {
    val la = wrapped.LA(i)
    if (la == 0 || la == IntStream.EOF) la
    else Character.toUpperCase(la)
  }
}
* */