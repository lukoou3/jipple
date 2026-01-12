package com.jipple.sql.catalyst.parser;

public class CatalystSqlParser extends AbstractSqlParser {
    public static CatalystSqlParser INSTANCE = new CatalystSqlParser();

    public static CatalystSqlParser getInstance() {
        return INSTANCE;
    }

    @Override
    protected AstBuilder astBuilder() {
        return new AstBuilder();
    }
}
