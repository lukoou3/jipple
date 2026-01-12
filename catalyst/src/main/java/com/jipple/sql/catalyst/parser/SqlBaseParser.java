// Generated from D:\IdeaWorkspace\jipple\catalyst\src\main\antlr4\com\jipple\sql\catalyst\parser\SqlBase.g4 by ANTLR 4.7
package com.jipple.sql.catalyst.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SqlBaseParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		ADD=10, AFTER=11, ALL=12, ALTER=13, ANALYZE=14, AND=15, ANTI=16, ANY=17, 
		ANY_VALUE=18, ARCHIVE=19, ARRAY=20, AS=21, ASC=22, AT=23, AUTHORIZATION=24, 
		BETWEEN=25, BOTH=26, BUCKET=27, BUCKETS=28, BY=29, CACHE=30, CASCADE=31, 
		CASE=32, CAST=33, CHANGE=34, CHECK=35, CLEAR=36, CLUSTER=37, CLUSTERED=38, 
		CODEGEN=39, COLLATE=40, COLLECTION=41, COLUMN=42, COLUMNS=43, COMMENT=44, 
		COMMIT=45, COMPACT=46, COMPACTIONS=47, COMPUTE=48, CONCATENATE=49, CONSTRAINT=50, 
		COST=51, CREATE=52, CROSS=53, CUBE=54, CURRENT=55, CURRENT_DATE=56, CURRENT_TIME=57, 
		CURRENT_TIMESTAMP=58, CURRENT_USER=59, DATA=60, DATABASE=61, DATABASES=62, 
		DBPROPERTIES=63, DEFINED=64, DELETE=65, DELIMITED=66, DESC=67, DESCRIBE=68, 
		DFS=69, DIRECTORIES=70, DIRECTORY=71, DISTINCT=72, DISTRIBUTE=73, DIV=74, 
		DROP=75, ELSE=76, END=77, ESCAPE=78, ESCAPED=79, EXCEPT=80, EXCHANGE=81, 
		EXISTS=82, EXPLAIN=83, EXPORT=84, EXTENDED=85, EXTERNAL=86, EXTRACT=87, 
		FALSE=88, FETCH=89, FIELDS=90, FILTER=91, FILEFORMAT=92, FIRST=93, FOLLOWING=94, 
		FOR=95, FOREIGN=96, FORMAT=97, FORMATTED=98, FROM=99, FULL=100, FUNCTION=101, 
		FUNCTIONS=102, GLOBAL=103, GRANT=104, GROUP=105, GROUPING=106, HAVING=107, 
		IF=108, IGNORE=109, IMPORT=110, IN=111, INDEX=112, INDEXES=113, INNER=114, 
		INPATH=115, INPUTFORMAT=116, INSERT=117, INTERSECT=118, INTERVAL=119, 
		INTO=120, IS=121, ITEMS=122, JOIN=123, KEYS=124, LAST=125, LATERAL=126, 
		LAZY=127, LEADING=128, LEFT=129, LIKE=130, LIMIT=131, LINES=132, LIST=133, 
		LOAD=134, LOCAL=135, LOCATION=136, LOCK=137, LOCKS=138, LOGICAL=139, MACRO=140, 
		MAP=141, MATCHED=142, MERGE=143, MSCK=144, NAMESPACE=145, NAMESPACES=146, 
		NATURAL=147, NO=148, NOT=149, NULL=150, NULLS=151, OF=152, ON=153, ONLY=154, 
		OPTION=155, OPTIONS=156, OR=157, ORDER=158, OUT=159, OUTER=160, OUTPUTFORMAT=161, 
		OVER=162, OVERLAPS=163, OVERLAY=164, OVERWRITE=165, PARTITION=166, PARTITIONED=167, 
		PARTITIONS=168, PERCENTLIT=169, PIVOT=170, PLACING=171, POSITION=172, 
		PRECEDING=173, PRIMARY=174, PRINCIPALS=175, PROPERTIES=176, PURGE=177, 
		QUERY=178, RANGE=179, RECORDREADER=180, RECORDWRITER=181, RECOVER=182, 
		REDUCE=183, REFERENCES=184, REFRESH=185, RENAME=186, REPAIR=187, REPLACE=188, 
		RESET=189, RESTRICT=190, REVOKE=191, RIGHT=192, RLIKE=193, ROLE=194, ROLES=195, 
		ROLLBACK=196, ROLLUP=197, ROW=198, ROWS=199, SCHEMA=200, SELECT=201, SEMI=202, 
		SEPARATED=203, SERDE=204, SERDEPROPERTIES=205, SESSION_USER=206, SET=207, 
		SETMINUS=208, SETS=209, SHOW=210, SKEWED=211, SOME=212, SORT=213, SORTED=214, 
		START=215, STATISTICS=216, STORED=217, STRATIFY=218, STRUCT=219, SUBSTR=220, 
		SUBSTRING=221, TABLE=222, TABLES=223, TABLESAMPLE=224, TBLPROPERTIES=225, 
		TEMPORARY=226, TERMINATED=227, THEN=228, TIME=229, TO=230, TOUCH=231, 
		TRAILING=232, TRANSACTION=233, TRANSACTIONS=234, TRANSFORM=235, TRIM=236, 
		TRUE=237, TRUNCATE=238, TYPE=239, UNARCHIVE=240, UNBOUNDED=241, UNCACHE=242, 
		UNION=243, UNIQUE=244, UNKNOWN=245, UNLOCK=246, UNSET=247, UPDATE=248, 
		USE=249, USER=250, USING=251, VALUES=252, VIEW=253, VIEWS=254, WHEN=255, 
		WHERE=256, WINDOW=257, WITH=258, ZONE=259, EQ=260, NSEQ=261, NEQ=262, 
		NEQJ=263, LT=264, LTE=265, GT=266, GTE=267, PLUS=268, MINUS=269, ASTERISK=270, 
		SLASH=271, PERCENT=272, TILDE=273, AMPERSAND=274, PIPE=275, CONCAT_PIPE=276, 
		HAT=277, STRING=278, BIGINT_LITERAL=279, SMALLINT_LITERAL=280, TINYINT_LITERAL=281, 
		INTEGER_VALUE=282, EXPONENT_VALUE=283, DECIMAL_VALUE=284, FLOAT_LITERAL=285, 
		DOUBLE_LITERAL=286, BIGDECIMAL_LITERAL=287, IDENTIFIER=288, BACKQUOTED_IDENTIFIER=289, 
		SIMPLE_COMMENT=290, BRACKETED_COMMENT=291, WS=292, UNRECOGNIZED=293;
	public static final int
		RULE_singleStatement = 0, RULE_singleExpression = 1, RULE_singleExpressions = 2, 
		RULE_singleOrderBy = 3, RULE_singlePartitionBy = 4, RULE_singleTableIdentifier = 5, 
		RULE_singleMultipartIdentifier = 6, RULE_singleFunctionIdentifier = 7, 
		RULE_singleDataType = 8, RULE_singleTableSchema = 9, RULE_statement = 10, 
		RULE_commentSpec = 11, RULE_query = 12, RULE_queryOrganization = 13, RULE_queryTerm = 14, 
		RULE_queryPrimary = 15, RULE_partitionItems = 16, RULE_sortItems = 17, 
		RULE_sortItem = 18, RULE_querySpecification = 19, RULE_selectClause = 20, 
		RULE_whereClause = 21, RULE_havingClause = 22, RULE_fromClause = 23, RULE_lateralView = 24, 
		RULE_relation = 25, RULE_joinRelation = 26, RULE_joinType = 27, RULE_joinCriteria = 28, 
		RULE_relationPrimary = 29, RULE_inlineTable = 30, RULE_aggregationClause = 31, 
		RULE_identifierList = 32, RULE_identifierSeq = 33, RULE_tableAlias = 34, 
		RULE_multipartIdentifier = 35, RULE_tableIdentifier = 36, RULE_functionIdentifier = 37, 
		RULE_namedExpression = 38, RULE_namedExpressionSeq = 39, RULE_expression = 40, 
		RULE_booleanExpression = 41, RULE_predicate = 42, RULE_valueExpression = 43, 
		RULE_primaryExpression = 44, RULE_constant = 45, RULE_comparisonOperator = 46, 
		RULE_booleanValue = 47, RULE_interval = 48, RULE_errorCapturingMultiUnitsInterval = 49, 
		RULE_multiUnitsInterval = 50, RULE_errorCapturingUnitToUnitInterval = 51, 
		RULE_unitToUnitInterval = 52, RULE_intervalValue = 53, RULE_dataType = 54, 
		RULE_colTypeList = 55, RULE_colType = 56, RULE_complexColTypeList = 57, 
		RULE_complexColType = 58, RULE_whenClause = 59, RULE_functionName = 60, 
		RULE_qualifiedName = 61, RULE_errorCapturingIdentifier = 62, RULE_errorCapturingIdentifierExtra = 63, 
		RULE_identifier = 64, RULE_strictIdentifier = 65, RULE_quotedIdentifier = 66, 
		RULE_number = 67, RULE_ansiNonReserved = 68, RULE_strictNonReserved = 69, 
		RULE_nonReserved = 70;
	public static final String[] ruleNames = {
		"singleStatement", "singleExpression", "singleExpressions", "singleOrderBy", 
		"singlePartitionBy", "singleTableIdentifier", "singleMultipartIdentifier", 
		"singleFunctionIdentifier", "singleDataType", "singleTableSchema", "statement", 
		"commentSpec", "query", "queryOrganization", "queryTerm", "queryPrimary", 
		"partitionItems", "sortItems", "sortItem", "querySpecification", "selectClause", 
		"whereClause", "havingClause", "fromClause", "lateralView", "relation", 
		"joinRelation", "joinType", "joinCriteria", "relationPrimary", "inlineTable", 
		"aggregationClause", "identifierList", "identifierSeq", "tableAlias", 
		"multipartIdentifier", "tableIdentifier", "functionIdentifier", "namedExpression", 
		"namedExpressionSeq", "expression", "booleanExpression", "predicate", 
		"valueExpression", "primaryExpression", "constant", "comparisonOperator", 
		"booleanValue", "interval", "errorCapturingMultiUnitsInterval", "multiUnitsInterval", 
		"errorCapturingUnitToUnitInterval", "unitToUnitInterval", "intervalValue", 
		"dataType", "colTypeList", "colType", "complexColTypeList", "complexColType", 
		"whenClause", "functionName", "qualifiedName", "errorCapturingIdentifier", 
		"errorCapturingIdentifierExtra", "identifier", "strictIdentifier", "quotedIdentifier", 
		"number", "ansiNonReserved", "strictNonReserved", "nonReserved"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "','", "'('", "')'", "'.'", "'->'", "'['", "']'", "':'", 
		"'ADD'", "'AFTER'", "'ALL'", "'ALTER'", "'ANALYZE'", "'AND'", "'ANTI'", 
		"'ANY'", "'ANY_VALUE'", "'ARCHIVE'", "'ARRAY'", "'AS'", "'ASC'", "'AT'", 
		"'AUTHORIZATION'", "'BETWEEN'", "'BOTH'", "'BUCKET'", "'BUCKETS'", "'BY'", 
		"'CACHE'", "'CASCADE'", "'CASE'", "'CAST'", "'CHANGE'", "'CHECK'", "'CLEAR'", 
		"'CLUSTER'", "'CLUSTERED'", "'CODEGEN'", "'COLLATE'", "'COLLECTION'", 
		"'COLUMN'", "'COLUMNS'", "'COMMENT'", "'COMMIT'", "'COMPACT'", "'COMPACTIONS'", 
		"'COMPUTE'", "'CONCATENATE'", "'CONSTRAINT'", "'COST'", "'CREATE'", "'CROSS'", 
		"'CUBE'", "'CURRENT'", "'CURRENT_DATE'", "'CURRENT_TIME'", "'CURRENT_TIMESTAMP'", 
		"'CURRENT_USER'", "'DATA'", "'DATABASE'", null, "'DBPROPERTIES'", "'DEFINED'", 
		"'DELETE'", "'DELIMITED'", "'DESC'", "'DESCRIBE'", "'DFS'", "'DIRECTORIES'", 
		"'DIRECTORY'", "'DISTINCT'", "'DISTRIBUTE'", "'DIV'", "'DROP'", "'ELSE'", 
		"'END'", "'ESCAPE'", "'ESCAPED'", "'EXCEPT'", "'EXCHANGE'", "'EXISTS'", 
		"'EXPLAIN'", "'EXPORT'", "'EXTENDED'", "'EXTERNAL'", "'EXTRACT'", "'FALSE'", 
		"'FETCH'", "'FIELDS'", "'FILTER'", "'FILEFORMAT'", "'FIRST'", "'FOLLOWING'", 
		"'FOR'", "'FOREIGN'", "'FORMAT'", "'FORMATTED'", "'FROM'", "'FULL'", "'FUNCTION'", 
		"'FUNCTIONS'", "'GLOBAL'", "'GRANT'", "'GROUP'", "'GROUPING'", "'HAVING'", 
		"'IF'", "'IGNORE'", "'IMPORT'", "'IN'", "'INDEX'", "'INDEXES'", "'INNER'", 
		"'INPATH'", "'INPUTFORMAT'", "'INSERT'", "'INTERSECT'", "'INTERVAL'", 
		"'INTO'", "'IS'", "'ITEMS'", "'JOIN'", "'KEYS'", "'LAST'", "'LATERAL'", 
		"'LAZY'", "'LEADING'", "'LEFT'", "'LIKE'", "'LIMIT'", "'LINES'", "'LIST'", 
		"'LOAD'", "'LOCAL'", "'LOCATION'", "'LOCK'", "'LOCKS'", "'LOGICAL'", "'MACRO'", 
		"'MAP'", "'MATCHED'", "'MERGE'", "'MSCK'", "'NAMESPACE'", "'NAMESPACES'", 
		"'NATURAL'", "'NO'", null, "'NULL'", "'NULLS'", "'OF'", "'ON'", "'ONLY'", 
		"'OPTION'", "'OPTIONS'", "'OR'", "'ORDER'", "'OUT'", "'OUTER'", "'OUTPUTFORMAT'", 
		"'OVER'", "'OVERLAPS'", "'OVERLAY'", "'OVERWRITE'", "'PARTITION'", "'PARTITIONED'", 
		"'PARTITIONS'", "'PERCENT'", "'PIVOT'", "'PLACING'", "'POSITION'", "'PRECEDING'", 
		"'PRIMARY'", "'PRINCIPALS'", "'PROPERTIES'", "'PURGE'", "'QUERY'", "'RANGE'", 
		"'RECORDREADER'", "'RECORDWRITER'", "'RECOVER'", "'REDUCE'", "'REFERENCES'", 
		"'REFRESH'", "'RENAME'", "'REPAIR'", "'REPLACE'", "'RESET'", "'RESTRICT'", 
		"'REVOKE'", "'RIGHT'", null, "'ROLE'", "'ROLES'", "'ROLLBACK'", "'ROLLUP'", 
		"'ROW'", "'ROWS'", "'SCHEMA'", "'SELECT'", "'SEMI'", "'SEPARATED'", "'SERDE'", 
		"'SERDEPROPERTIES'", "'SESSION_USER'", "'SET'", "'MINUS'", "'SETS'", "'SHOW'", 
		"'SKEWED'", "'SOME'", "'SORT'", "'SORTED'", "'START'", "'STATISTICS'", 
		"'STORED'", "'STRATIFY'", "'STRUCT'", "'SUBSTR'", "'SUBSTRING'", "'TABLE'", 
		"'TABLES'", "'TABLESAMPLE'", "'TBLPROPERTIES'", null, "'TERMINATED'", 
		"'THEN'", "'TIME'", "'TO'", "'TOUCH'", "'TRAILING'", "'TRANSACTION'", 
		"'TRANSACTIONS'", "'TRANSFORM'", "'TRIM'", "'TRUE'", "'TRUNCATE'", "'TYPE'", 
		"'UNARCHIVE'", "'UNBOUNDED'", "'UNCACHE'", "'UNION'", "'UNIQUE'", "'UNKNOWN'", 
		"'UNLOCK'", "'UNSET'", "'UPDATE'", "'USE'", "'USER'", "'USING'", "'VALUES'", 
		"'VIEW'", "'VIEWS'", "'WHEN'", "'WHERE'", "'WINDOW'", "'WITH'", "'ZONE'", 
		null, "'<=>'", "'<>'", "'!='", "'<'", null, "'>'", null, "'+'", "'-'", 
		"'*'", "'/'", "'%'", "'~'", "'&'", "'|'", "'||'", "'^'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, "ADD", "AFTER", 
		"ALL", "ALTER", "ANALYZE", "AND", "ANTI", "ANY", "ANY_VALUE", "ARCHIVE", 
		"ARRAY", "AS", "ASC", "AT", "AUTHORIZATION", "BETWEEN", "BOTH", "BUCKET", 
		"BUCKETS", "BY", "CACHE", "CASCADE", "CASE", "CAST", "CHANGE", "CHECK", 
		"CLEAR", "CLUSTER", "CLUSTERED", "CODEGEN", "COLLATE", "COLLECTION", "COLUMN", 
		"COLUMNS", "COMMENT", "COMMIT", "COMPACT", "COMPACTIONS", "COMPUTE", "CONCATENATE", 
		"CONSTRAINT", "COST", "CREATE", "CROSS", "CUBE", "CURRENT", "CURRENT_DATE", 
		"CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DATA", "DATABASE", 
		"DATABASES", "DBPROPERTIES", "DEFINED", "DELETE", "DELIMITED", "DESC", 
		"DESCRIBE", "DFS", "DIRECTORIES", "DIRECTORY", "DISTINCT", "DISTRIBUTE", 
		"DIV", "DROP", "ELSE", "END", "ESCAPE", "ESCAPED", "EXCEPT", "EXCHANGE", 
		"EXISTS", "EXPLAIN", "EXPORT", "EXTENDED", "EXTERNAL", "EXTRACT", "FALSE", 
		"FETCH", "FIELDS", "FILTER", "FILEFORMAT", "FIRST", "FOLLOWING", "FOR", 
		"FOREIGN", "FORMAT", "FORMATTED", "FROM", "FULL", "FUNCTION", "FUNCTIONS", 
		"GLOBAL", "GRANT", "GROUP", "GROUPING", "HAVING", "IF", "IGNORE", "IMPORT", 
		"IN", "INDEX", "INDEXES", "INNER", "INPATH", "INPUTFORMAT", "INSERT", 
		"INTERSECT", "INTERVAL", "INTO", "IS", "ITEMS", "JOIN", "KEYS", "LAST", 
		"LATERAL", "LAZY", "LEADING", "LEFT", "LIKE", "LIMIT", "LINES", "LIST", 
		"LOAD", "LOCAL", "LOCATION", "LOCK", "LOCKS", "LOGICAL", "MACRO", "MAP", 
		"MATCHED", "MERGE", "MSCK", "NAMESPACE", "NAMESPACES", "NATURAL", "NO", 
		"NOT", "NULL", "NULLS", "OF", "ON", "ONLY", "OPTION", "OPTIONS", "OR", 
		"ORDER", "OUT", "OUTER", "OUTPUTFORMAT", "OVER", "OVERLAPS", "OVERLAY", 
		"OVERWRITE", "PARTITION", "PARTITIONED", "PARTITIONS", "PERCENTLIT", "PIVOT", 
		"PLACING", "POSITION", "PRECEDING", "PRIMARY", "PRINCIPALS", "PROPERTIES", 
		"PURGE", "QUERY", "RANGE", "RECORDREADER", "RECORDWRITER", "RECOVER", 
		"REDUCE", "REFERENCES", "REFRESH", "RENAME", "REPAIR", "REPLACE", "RESET", 
		"RESTRICT", "REVOKE", "RIGHT", "RLIKE", "ROLE", "ROLES", "ROLLBACK", "ROLLUP", 
		"ROW", "ROWS", "SCHEMA", "SELECT", "SEMI", "SEPARATED", "SERDE", "SERDEPROPERTIES", 
		"SESSION_USER", "SET", "SETMINUS", "SETS", "SHOW", "SKEWED", "SOME", "SORT", 
		"SORTED", "START", "STATISTICS", "STORED", "STRATIFY", "STRUCT", "SUBSTR", 
		"SUBSTRING", "TABLE", "TABLES", "TABLESAMPLE", "TBLPROPERTIES", "TEMPORARY", 
		"TERMINATED", "THEN", "TIME", "TO", "TOUCH", "TRAILING", "TRANSACTION", 
		"TRANSACTIONS", "TRANSFORM", "TRIM", "TRUE", "TRUNCATE", "TYPE", "UNARCHIVE", 
		"UNBOUNDED", "UNCACHE", "UNION", "UNIQUE", "UNKNOWN", "UNLOCK", "UNSET", 
		"UPDATE", "USE", "USER", "USING", "VALUES", "VIEW", "VIEWS", "WHEN", "WHERE", 
		"WINDOW", "WITH", "ZONE", "EQ", "NSEQ", "NEQ", "NEQJ", "LT", "LTE", "GT", 
		"GTE", "PLUS", "MINUS", "ASTERISK", "SLASH", "PERCENT", "TILDE", "AMPERSAND", 
		"PIPE", "CONCAT_PIPE", "HAT", "STRING", "BIGINT_LITERAL", "SMALLINT_LITERAL", 
		"TINYINT_LITERAL", "INTEGER_VALUE", "EXPONENT_VALUE", "DECIMAL_VALUE", 
		"FLOAT_LITERAL", "DOUBLE_LITERAL", "BIGDECIMAL_LITERAL", "IDENTIFIER", 
		"BACKQUOTED_IDENTIFIER", "SIMPLE_COMMENT", "BRACKETED_COMMENT", "WS", 
		"UNRECOGNIZED"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SqlBase.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	  /**
	   * When false, INTERSECT is given the greater precedence over the other set
	   * operations (UNION, EXCEPT and MINUS) as per the SQL standard.
	   */
	  public boolean legacy_setops_precedence_enbled = false;

	  /**
	   * When false, a literal with an exponent would be converted into
	   * double type rather than decimal type.
	   */
	  public boolean legacy_exponent_literal_as_decimal_enabled = false;

	  /**
	   * When true, the behavior of keywords follows ANSI SQL standard.
	   */
	  public boolean SQL_standard_keyword_behavior = false;

	public SqlBaseParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SingleStatementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleStatementContext singleStatement() throws RecognitionException {
		SingleStatementContext _localctx = new SingleStatementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_singleStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			statement();
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(143);
				match(T__0);
				}
				}
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(149);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleExpressionContext extends ParserRuleContext {
		public NamedExpressionContext namedExpression() {
			return getRuleContext(NamedExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleExpressionContext singleExpression() throws RecognitionException {
		SingleExpressionContext _localctx = new SingleExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_singleExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			namedExpression();
			setState(152);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleExpressionsContext extends ParserRuleContext {
		public NamedExpressionSeqContext namedExpressionSeq() {
			return getRuleContext(NamedExpressionSeqContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleExpressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleExpressions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleExpressions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleExpressionsContext singleExpressions() throws RecognitionException {
		SingleExpressionsContext _localctx = new SingleExpressionsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_singleExpressions);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			namedExpressionSeq();
			setState(155);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleOrderByContext extends ParserRuleContext {
		public SortItemsContext sortItems() {
			return getRuleContext(SortItemsContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleOrderByContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleOrderBy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleOrderBy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleOrderBy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleOrderBy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleOrderByContext singleOrderBy() throws RecognitionException {
		SingleOrderByContext _localctx = new SingleOrderByContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_singleOrderBy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			sortItems();
			setState(158);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SinglePartitionByContext extends ParserRuleContext {
		public PartitionItemsContext partitionItems() {
			return getRuleContext(PartitionItemsContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SinglePartitionByContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singlePartitionBy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSinglePartitionBy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSinglePartitionBy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSinglePartitionBy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SinglePartitionByContext singlePartitionBy() throws RecognitionException {
		SinglePartitionByContext _localctx = new SinglePartitionByContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_singlePartitionBy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			partitionItems();
			setState(161);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleTableIdentifierContext extends ParserRuleContext {
		public TableIdentifierContext tableIdentifier() {
			return getRuleContext(TableIdentifierContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleTableIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleTableIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleTableIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleTableIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleTableIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleTableIdentifierContext singleTableIdentifier() throws RecognitionException {
		SingleTableIdentifierContext _localctx = new SingleTableIdentifierContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_singleTableIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			tableIdentifier();
			setState(164);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleMultipartIdentifierContext extends ParserRuleContext {
		public MultipartIdentifierContext multipartIdentifier() {
			return getRuleContext(MultipartIdentifierContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleMultipartIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleMultipartIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleMultipartIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleMultipartIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleMultipartIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleMultipartIdentifierContext singleMultipartIdentifier() throws RecognitionException {
		SingleMultipartIdentifierContext _localctx = new SingleMultipartIdentifierContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_singleMultipartIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			multipartIdentifier();
			setState(167);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleFunctionIdentifierContext extends ParserRuleContext {
		public FunctionIdentifierContext functionIdentifier() {
			return getRuleContext(FunctionIdentifierContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleFunctionIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleFunctionIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleFunctionIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleFunctionIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleFunctionIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleFunctionIdentifierContext singleFunctionIdentifier() throws RecognitionException {
		SingleFunctionIdentifierContext _localctx = new SingleFunctionIdentifierContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_singleFunctionIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			functionIdentifier();
			setState(170);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleDataTypeContext extends ParserRuleContext {
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleDataTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleDataType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleDataType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleDataType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleDataType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleDataTypeContext singleDataType() throws RecognitionException {
		SingleDataTypeContext _localctx = new SingleDataTypeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_singleDataType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			dataType();
			setState(173);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleTableSchemaContext extends ParserRuleContext {
		public ColTypeListContext colTypeList() {
			return getRuleContext(ColTypeListContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SqlBaseParser.EOF, 0); }
		public SingleTableSchemaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleTableSchema; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSingleTableSchema(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSingleTableSchema(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSingleTableSchema(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleTableSchemaContext singleTableSchema() throws RecognitionException {
		SingleTableSchemaContext _localctx = new SingleTableSchemaContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_singleTableSchema);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			colTypeList();
			setState(176);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StatementDefaultContext extends StatementContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public StatementDefaultContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterStatementDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitStatementDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitStatementDefault(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_statement);
		try {
			_localctx = new StatementDefaultContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			query();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommentSpecContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(SqlBaseParser.COMMENT, 0); }
		public TerminalNode STRING() { return getToken(SqlBaseParser.STRING, 0); }
		public CommentSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commentSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterCommentSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitCommentSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitCommentSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentSpecContext commentSpec() throws RecognitionException {
		CommentSpecContext _localctx = new CommentSpecContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_commentSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			match(COMMENT);
			setState(181);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryContext extends ParserRuleContext {
		public QueryTermContext queryTerm() {
			return getRuleContext(QueryTermContext.class,0);
		}
		public QueryOrganizationContext queryOrganization() {
			return getRuleContext(QueryOrganizationContext.class,0);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_query);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			queryTerm(0);
			setState(184);
			queryOrganization();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryOrganizationContext extends ParserRuleContext {
		public SortItemsContext orders;
		public ExpressionContext limit;
		public TerminalNode ORDER() { return getToken(SqlBaseParser.ORDER, 0); }
		public TerminalNode BY() { return getToken(SqlBaseParser.BY, 0); }
		public TerminalNode LIMIT() { return getToken(SqlBaseParser.LIMIT, 0); }
		public SortItemsContext sortItems() {
			return getRuleContext(SortItemsContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public QueryOrganizationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryOrganization; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterQueryOrganization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitQueryOrganization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitQueryOrganization(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryOrganizationContext queryOrganization() throws RecognitionException {
		QueryOrganizationContext _localctx = new QueryOrganizationContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_queryOrganization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(186);
				match(ORDER);
				setState(187);
				match(BY);
				setState(188);
				((QueryOrganizationContext)_localctx).orders = sortItems();
				}
			}

			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(191);
				match(LIMIT);
				setState(192);
				((QueryOrganizationContext)_localctx).limit = expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryTermContext extends ParserRuleContext {
		public QueryTermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryTerm; }
	 
		public QueryTermContext() { }
		public void copyFrom(QueryTermContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class QueryTermDefaultContext extends QueryTermContext {
		public QueryPrimaryContext queryPrimary() {
			return getRuleContext(QueryPrimaryContext.class,0);
		}
		public QueryTermDefaultContext(QueryTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterQueryTermDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitQueryTermDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitQueryTermDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SetOperationContext extends QueryTermContext {
		public QueryTermContext left;
		public Token operator;
		public QueryTermContext right;
		public List<QueryTermContext> queryTerm() {
			return getRuleContexts(QueryTermContext.class);
		}
		public QueryTermContext queryTerm(int i) {
			return getRuleContext(QueryTermContext.class,i);
		}
		public TerminalNode UNION() { return getToken(SqlBaseParser.UNION, 0); }
		public TerminalNode ALL() { return getToken(SqlBaseParser.ALL, 0); }
		public SetOperationContext(QueryTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSetOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSetOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSetOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryTermContext queryTerm() throws RecognitionException {
		return queryTerm(0);
	}

	private QueryTermContext queryTerm(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		QueryTermContext _localctx = new QueryTermContext(_ctx, _parentState);
		QueryTermContext _prevctx = _localctx;
		int _startState = 28;
		enterRecursionRule(_localctx, 28, RULE_queryTerm, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new QueryTermDefaultContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(196);
			queryPrimary();
			}
			_ctx.stop = _input.LT(-1);
			setState(206);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new SetOperationContext(new QueryTermContext(_parentctx, _parentState));
					((SetOperationContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_queryTerm);
					setState(198);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(199);
					((SetOperationContext)_localctx).operator = match(UNION);
					setState(201);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ALL) {
						{
						setState(200);
						match(ALL);
						}
					}

					setState(203);
					((SetOperationContext)_localctx).right = queryTerm(2);
					}
					} 
				}
				setState(208);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class QueryPrimaryContext extends ParserRuleContext {
		public QueryPrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryPrimary; }
	 
		public QueryPrimaryContext() { }
		public void copyFrom(QueryPrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class QueryPrimaryDefaultContext extends QueryPrimaryContext {
		public QuerySpecificationContext querySpecification() {
			return getRuleContext(QuerySpecificationContext.class,0);
		}
		public QueryPrimaryDefaultContext(QueryPrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterQueryPrimaryDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitQueryPrimaryDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitQueryPrimaryDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InlineTableDefault1Context extends QueryPrimaryContext {
		public InlineTableContext inlineTable() {
			return getRuleContext(InlineTableContext.class,0);
		}
		public InlineTableDefault1Context(QueryPrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterInlineTableDefault1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitInlineTableDefault1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitInlineTableDefault1(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TableContext extends QueryPrimaryContext {
		public TerminalNode TABLE() { return getToken(SqlBaseParser.TABLE, 0); }
		public MultipartIdentifierContext multipartIdentifier() {
			return getRuleContext(MultipartIdentifierContext.class,0);
		}
		public TableContext(QueryPrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitTable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitTable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryPrimaryContext queryPrimary() throws RecognitionException {
		QueryPrimaryContext _localctx = new QueryPrimaryContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_queryPrimary);
		try {
			setState(213);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT:
				_localctx = new QueryPrimaryDefaultContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				querySpecification();
				}
				break;
			case TABLE:
				_localctx = new TableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(210);
				match(TABLE);
				setState(211);
				multipartIdentifier();
				}
				break;
			case VALUES:
				_localctx = new InlineTableDefault1Context(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(212);
				inlineTable();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PartitionItemsContext extends ParserRuleContext {
		public ExpressionContext expression;
		public List<ExpressionContext> partition = new ArrayList<ExpressionContext>();
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public PartitionItemsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_partitionItems; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterPartitionItems(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitPartitionItems(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitPartitionItems(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PartitionItemsContext partitionItems() throws RecognitionException {
		PartitionItemsContext _localctx = new PartitionItemsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_partitionItems);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			((PartitionItemsContext)_localctx).expression = expression();
			((PartitionItemsContext)_localctx).partition.add(((PartitionItemsContext)_localctx).expression);
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(216);
				match(T__1);
				setState(217);
				((PartitionItemsContext)_localctx).expression = expression();
				((PartitionItemsContext)_localctx).partition.add(((PartitionItemsContext)_localctx).expression);
				}
				}
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SortItemsContext extends ParserRuleContext {
		public SortItemContext sortItem;
		public List<SortItemContext> order = new ArrayList<SortItemContext>();
		public List<SortItemContext> sortItem() {
			return getRuleContexts(SortItemContext.class);
		}
		public SortItemContext sortItem(int i) {
			return getRuleContext(SortItemContext.class,i);
		}
		public SortItemsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sortItems; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSortItems(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSortItems(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSortItems(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SortItemsContext sortItems() throws RecognitionException {
		SortItemsContext _localctx = new SortItemsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_sortItems);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			((SortItemsContext)_localctx).sortItem = sortItem();
			((SortItemsContext)_localctx).order.add(((SortItemsContext)_localctx).sortItem);
			setState(228);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(224);
				match(T__1);
				setState(225);
				((SortItemsContext)_localctx).sortItem = sortItem();
				((SortItemsContext)_localctx).order.add(((SortItemsContext)_localctx).sortItem);
				}
				}
				setState(230);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SortItemContext extends ParserRuleContext {
		public Token ordering;
		public Token nullOrder;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode NULLS() { return getToken(SqlBaseParser.NULLS, 0); }
		public TerminalNode ASC() { return getToken(SqlBaseParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(SqlBaseParser.DESC, 0); }
		public TerminalNode LAST() { return getToken(SqlBaseParser.LAST, 0); }
		public TerminalNode FIRST() { return getToken(SqlBaseParser.FIRST, 0); }
		public SortItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sortItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSortItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSortItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSortItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SortItemContext sortItem() throws RecognitionException {
		SortItemContext _localctx = new SortItemContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_sortItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			expression();
			setState(233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASC || _la==DESC) {
				{
				setState(232);
				((SortItemContext)_localctx).ordering = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==ASC || _la==DESC) ) {
					((SortItemContext)_localctx).ordering = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NULLS) {
				{
				setState(235);
				match(NULLS);
				setState(236);
				((SortItemContext)_localctx).nullOrder = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==FIRST || _la==LAST) ) {
					((SortItemContext)_localctx).nullOrder = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QuerySpecificationContext extends ParserRuleContext {
		public QuerySpecificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_querySpecification; }
	 
		public QuerySpecificationContext() { }
		public void copyFrom(QuerySpecificationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RegularQuerySpecificationContext extends QuerySpecificationContext {
		public SelectClauseContext selectClause() {
			return getRuleContext(SelectClauseContext.class,0);
		}
		public FromClauseContext fromClause() {
			return getRuleContext(FromClauseContext.class,0);
		}
		public List<LateralViewContext> lateralView() {
			return getRuleContexts(LateralViewContext.class);
		}
		public LateralViewContext lateralView(int i) {
			return getRuleContext(LateralViewContext.class,i);
		}
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public AggregationClauseContext aggregationClause() {
			return getRuleContext(AggregationClauseContext.class,0);
		}
		public HavingClauseContext havingClause() {
			return getRuleContext(HavingClauseContext.class,0);
		}
		public RegularQuerySpecificationContext(QuerySpecificationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterRegularQuerySpecification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitRegularQuerySpecification(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitRegularQuerySpecification(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QuerySpecificationContext querySpecification() throws RecognitionException {
		QuerySpecificationContext _localctx = new QuerySpecificationContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_querySpecification);
		try {
			int _alt;
			_localctx = new RegularQuerySpecificationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
			selectClause();
			setState(241);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(240);
				fromClause();
				}
				break;
			}
			setState(246);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(243);
					lateralView();
					}
					} 
				}
				setState(248);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			setState(250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(249);
				whereClause();
				}
				break;
			}
			setState(253);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(252);
				aggregationClause();
				}
				break;
			}
			setState(256);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(255);
				havingClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectClauseContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(SqlBaseParser.SELECT, 0); }
		public NamedExpressionSeqContext namedExpressionSeq() {
			return getRuleContext(NamedExpressionSeqContext.class,0);
		}
		public SelectClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSelectClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSelectClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSelectClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectClauseContext selectClause() throws RecognitionException {
		SelectClauseContext _localctx = new SelectClauseContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_selectClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(SELECT);
			setState(259);
			namedExpressionSeq();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereClauseContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(SqlBaseParser.WHERE, 0); }
		public BooleanExpressionContext booleanExpression() {
			return getRuleContext(BooleanExpressionContext.class,0);
		}
		public WhereClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterWhereClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitWhereClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitWhereClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereClauseContext whereClause() throws RecognitionException {
		WhereClauseContext _localctx = new WhereClauseContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_whereClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(WHERE);
			setState(262);
			booleanExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HavingClauseContext extends ParserRuleContext {
		public TerminalNode HAVING() { return getToken(SqlBaseParser.HAVING, 0); }
		public BooleanExpressionContext booleanExpression() {
			return getRuleContext(BooleanExpressionContext.class,0);
		}
		public HavingClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_havingClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterHavingClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitHavingClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitHavingClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HavingClauseContext havingClause() throws RecognitionException {
		HavingClauseContext _localctx = new HavingClauseContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_havingClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(264);
			match(HAVING);
			setState(265);
			booleanExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FromClauseContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(SqlBaseParser.FROM, 0); }
		public RelationContext relation() {
			return getRuleContext(RelationContext.class,0);
		}
		public FromClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fromClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterFromClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitFromClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitFromClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FromClauseContext fromClause() throws RecognitionException {
		FromClauseContext _localctx = new FromClauseContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_fromClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			match(FROM);
			setState(268);
			relation();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LateralViewContext extends ParserRuleContext {
		public IdentifierContext tblName;
		public IdentifierContext identifier;
		public List<IdentifierContext> colName = new ArrayList<IdentifierContext>();
		public TerminalNode LATERAL() { return getToken(SqlBaseParser.LATERAL, 0); }
		public TerminalNode VIEW() { return getToken(SqlBaseParser.VIEW, 0); }
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode OUTER() { return getToken(SqlBaseParser.OUTER, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode AS() { return getToken(SqlBaseParser.AS, 0); }
		public LateralViewContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lateralView; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterLateralView(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitLateralView(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitLateralView(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LateralViewContext lateralView() throws RecognitionException {
		LateralViewContext _localctx = new LateralViewContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_lateralView);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(270);
			match(LATERAL);
			setState(271);
			match(VIEW);
			setState(273);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(272);
				match(OUTER);
				}
				break;
			}
			setState(275);
			qualifiedName();
			setState(276);
			match(T__2);
			setState(285);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(277);
				expression();
				setState(282);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(278);
					match(T__1);
					setState(279);
					expression();
					}
					}
					setState(284);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(287);
			match(T__3);
			setState(288);
			((LateralViewContext)_localctx).tblName = identifier();
			setState(300);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(290);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(289);
					match(AS);
					}
					break;
				}
				setState(292);
				((LateralViewContext)_localctx).identifier = identifier();
				((LateralViewContext)_localctx).colName.add(((LateralViewContext)_localctx).identifier);
				setState(297);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(293);
						match(T__1);
						setState(294);
						((LateralViewContext)_localctx).identifier = identifier();
						((LateralViewContext)_localctx).colName.add(((LateralViewContext)_localctx).identifier);
						}
						} 
					}
					setState(299);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationContext extends ParserRuleContext {
		public RelationPrimaryContext relationPrimary() {
			return getRuleContext(RelationPrimaryContext.class,0);
		}
		public List<JoinRelationContext> joinRelation() {
			return getRuleContexts(JoinRelationContext.class);
		}
		public JoinRelationContext joinRelation(int i) {
			return getRuleContext(JoinRelationContext.class,i);
		}
		public RelationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitRelation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationContext relation() throws RecognitionException {
		RelationContext _localctx = new RelationContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_relation);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			relationPrimary();
			setState(306);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(303);
					joinRelation();
					}
					} 
				}
				setState(308);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JoinRelationContext extends ParserRuleContext {
		public RelationPrimaryContext right;
		public TerminalNode JOIN() { return getToken(SqlBaseParser.JOIN, 0); }
		public RelationPrimaryContext relationPrimary() {
			return getRuleContext(RelationPrimaryContext.class,0);
		}
		public JoinTypeContext joinType() {
			return getRuleContext(JoinTypeContext.class,0);
		}
		public JoinCriteriaContext joinCriteria() {
			return getRuleContext(JoinCriteriaContext.class,0);
		}
		public JoinRelationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_joinRelation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterJoinRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitJoinRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitJoinRelation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JoinRelationContext joinRelation() throws RecognitionException {
		JoinRelationContext _localctx = new JoinRelationContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_joinRelation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(309);
			joinType();
			}
			setState(310);
			match(JOIN);
			setState(311);
			((JoinRelationContext)_localctx).right = relationPrimary();
			setState(313);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(312);
				joinCriteria();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JoinTypeContext extends ParserRuleContext {
		public TerminalNode INNER() { return getToken(SqlBaseParser.INNER, 0); }
		public TerminalNode LEFT() { return getToken(SqlBaseParser.LEFT, 0); }
		public TerminalNode OUTER() { return getToken(SqlBaseParser.OUTER, 0); }
		public TerminalNode RIGHT() { return getToken(SqlBaseParser.RIGHT, 0); }
		public TerminalNode FULL() { return getToken(SqlBaseParser.FULL, 0); }
		public JoinTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_joinType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterJoinType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitJoinType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitJoinType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JoinTypeContext joinType() throws RecognitionException {
		JoinTypeContext _localctx = new JoinTypeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_joinType);
		int _la;
		try {
			setState(330);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INNER:
			case JOIN:
				enterOuterAlt(_localctx, 1);
				{
				setState(316);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INNER) {
					{
					setState(315);
					match(INNER);
					}
				}

				}
				break;
			case LEFT:
				enterOuterAlt(_localctx, 2);
				{
				setState(318);
				match(LEFT);
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(319);
					match(OUTER);
					}
				}

				}
				break;
			case RIGHT:
				enterOuterAlt(_localctx, 3);
				{
				setState(322);
				match(RIGHT);
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(323);
					match(OUTER);
					}
				}

				}
				break;
			case FULL:
				enterOuterAlt(_localctx, 4);
				{
				setState(326);
				match(FULL);
				setState(328);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(327);
					match(OUTER);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JoinCriteriaContext extends ParserRuleContext {
		public TerminalNode ON() { return getToken(SqlBaseParser.ON, 0); }
		public BooleanExpressionContext booleanExpression() {
			return getRuleContext(BooleanExpressionContext.class,0);
		}
		public JoinCriteriaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_joinCriteria; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterJoinCriteria(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitJoinCriteria(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitJoinCriteria(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JoinCriteriaContext joinCriteria() throws RecognitionException {
		JoinCriteriaContext _localctx = new JoinCriteriaContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_joinCriteria);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			match(ON);
			setState(333);
			booleanExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationPrimaryContext extends ParserRuleContext {
		public RelationPrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationPrimary; }
	 
		public RelationPrimaryContext() { }
		public void copyFrom(RelationPrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class InlineTableDefault2Context extends RelationPrimaryContext {
		public InlineTableContext inlineTable() {
			return getRuleContext(InlineTableContext.class,0);
		}
		public InlineTableDefault2Context(RelationPrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterInlineTableDefault2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitInlineTableDefault2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitInlineTableDefault2(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AliasedQueryContext extends RelationPrimaryContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public TableAliasContext tableAlias() {
			return getRuleContext(TableAliasContext.class,0);
		}
		public AliasedQueryContext(RelationPrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterAliasedQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitAliasedQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitAliasedQuery(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TableNameContext extends RelationPrimaryContext {
		public MultipartIdentifierContext multipartIdentifier() {
			return getRuleContext(MultipartIdentifierContext.class,0);
		}
		public TableAliasContext tableAlias() {
			return getRuleContext(TableAliasContext.class,0);
		}
		public TableNameContext(RelationPrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterTableName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitTableName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitTableName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationPrimaryContext relationPrimary() throws RecognitionException {
		RelationPrimaryContext _localctx = new RelationPrimaryContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_relationPrimary);
		try {
			setState(344);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				_localctx = new TableNameContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(335);
				multipartIdentifier();
				setState(336);
				tableAlias();
				}
				break;
			case 2:
				_localctx = new AliasedQueryContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(338);
				match(T__2);
				setState(339);
				query();
				setState(340);
				match(T__3);
				setState(341);
				tableAlias();
				}
				break;
			case 3:
				_localctx = new InlineTableDefault2Context(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(343);
				inlineTable();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InlineTableContext extends ParserRuleContext {
		public TerminalNode VALUES() { return getToken(SqlBaseParser.VALUES, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TableAliasContext tableAlias() {
			return getRuleContext(TableAliasContext.class,0);
		}
		public InlineTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inlineTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterInlineTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitInlineTable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitInlineTable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InlineTableContext inlineTable() throws RecognitionException {
		InlineTableContext _localctx = new InlineTableContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_inlineTable);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			match(VALUES);
			setState(347);
			expression();
			setState(352);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(348);
					match(T__1);
					setState(349);
					expression();
					}
					} 
				}
				setState(354);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			}
			setState(355);
			tableAlias();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggregationClauseContext extends ParserRuleContext {
		public ExpressionContext expression;
		public List<ExpressionContext> groupingExpressions = new ArrayList<ExpressionContext>();
		public TerminalNode GROUP() { return getToken(SqlBaseParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(SqlBaseParser.BY, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public AggregationClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregationClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterAggregationClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitAggregationClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitAggregationClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregationClauseContext aggregationClause() throws RecognitionException {
		AggregationClauseContext _localctx = new AggregationClauseContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_aggregationClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(357);
			match(GROUP);
			setState(358);
			match(BY);
			setState(359);
			((AggregationClauseContext)_localctx).expression = expression();
			((AggregationClauseContext)_localctx).groupingExpressions.add(((AggregationClauseContext)_localctx).expression);
			setState(364);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(360);
					match(T__1);
					setState(361);
					((AggregationClauseContext)_localctx).expression = expression();
					((AggregationClauseContext)_localctx).groupingExpressions.add(((AggregationClauseContext)_localctx).expression);
					}
					} 
				}
				setState(366);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierListContext extends ParserRuleContext {
		public IdentifierSeqContext identifierSeq() {
			return getRuleContext(IdentifierSeqContext.class,0);
		}
		public IdentifierListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitIdentifierList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitIdentifierList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierListContext identifierList() throws RecognitionException {
		IdentifierListContext _localctx = new IdentifierListContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_identifierList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(367);
			match(T__2);
			setState(368);
			identifierSeq();
			setState(369);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierSeqContext extends ParserRuleContext {
		public ErrorCapturingIdentifierContext errorCapturingIdentifier;
		public List<ErrorCapturingIdentifierContext> ident = new ArrayList<ErrorCapturingIdentifierContext>();
		public List<ErrorCapturingIdentifierContext> errorCapturingIdentifier() {
			return getRuleContexts(ErrorCapturingIdentifierContext.class);
		}
		public ErrorCapturingIdentifierContext errorCapturingIdentifier(int i) {
			return getRuleContext(ErrorCapturingIdentifierContext.class,i);
		}
		public IdentifierSeqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierSeq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterIdentifierSeq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitIdentifierSeq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitIdentifierSeq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierSeqContext identifierSeq() throws RecognitionException {
		IdentifierSeqContext _localctx = new IdentifierSeqContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_identifierSeq);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			((IdentifierSeqContext)_localctx).errorCapturingIdentifier = errorCapturingIdentifier();
			((IdentifierSeqContext)_localctx).ident.add(((IdentifierSeqContext)_localctx).errorCapturingIdentifier);
			setState(376);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(372);
				match(T__1);
				setState(373);
				((IdentifierSeqContext)_localctx).errorCapturingIdentifier = errorCapturingIdentifier();
				((IdentifierSeqContext)_localctx).ident.add(((IdentifierSeqContext)_localctx).errorCapturingIdentifier);
				}
				}
				setState(378);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableAliasContext extends ParserRuleContext {
		public StrictIdentifierContext strictIdentifier() {
			return getRuleContext(StrictIdentifierContext.class,0);
		}
		public TerminalNode AS() { return getToken(SqlBaseParser.AS, 0); }
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TableAliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableAlias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterTableAlias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitTableAlias(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitTableAlias(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableAliasContext tableAlias() throws RecognitionException {
		TableAliasContext _localctx = new TableAliasContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_tableAlias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(386);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				{
				setState(380);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					setState(379);
					match(AS);
					}
					break;
				}
				setState(382);
				strictIdentifier();
				setState(384);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(383);
					identifierList();
					}
					break;
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultipartIdentifierContext extends ParserRuleContext {
		public ErrorCapturingIdentifierContext errorCapturingIdentifier;
		public List<ErrorCapturingIdentifierContext> parts = new ArrayList<ErrorCapturingIdentifierContext>();
		public List<ErrorCapturingIdentifierContext> errorCapturingIdentifier() {
			return getRuleContexts(ErrorCapturingIdentifierContext.class);
		}
		public ErrorCapturingIdentifierContext errorCapturingIdentifier(int i) {
			return getRuleContext(ErrorCapturingIdentifierContext.class,i);
		}
		public MultipartIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multipartIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterMultipartIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitMultipartIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitMultipartIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultipartIdentifierContext multipartIdentifier() throws RecognitionException {
		MultipartIdentifierContext _localctx = new MultipartIdentifierContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_multipartIdentifier);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			((MultipartIdentifierContext)_localctx).errorCapturingIdentifier = errorCapturingIdentifier();
			((MultipartIdentifierContext)_localctx).parts.add(((MultipartIdentifierContext)_localctx).errorCapturingIdentifier);
			setState(393);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(389);
					match(T__4);
					setState(390);
					((MultipartIdentifierContext)_localctx).errorCapturingIdentifier = errorCapturingIdentifier();
					((MultipartIdentifierContext)_localctx).parts.add(((MultipartIdentifierContext)_localctx).errorCapturingIdentifier);
					}
					} 
				}
				setState(395);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableIdentifierContext extends ParserRuleContext {
		public ErrorCapturingIdentifierContext db;
		public ErrorCapturingIdentifierContext table;
		public List<ErrorCapturingIdentifierContext> errorCapturingIdentifier() {
			return getRuleContexts(ErrorCapturingIdentifierContext.class);
		}
		public ErrorCapturingIdentifierContext errorCapturingIdentifier(int i) {
			return getRuleContext(ErrorCapturingIdentifierContext.class,i);
		}
		public TableIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterTableIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitTableIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitTableIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableIdentifierContext tableIdentifier() throws RecognitionException {
		TableIdentifierContext _localctx = new TableIdentifierContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_tableIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(396);
				((TableIdentifierContext)_localctx).db = errorCapturingIdentifier();
				setState(397);
				match(T__4);
				}
				break;
			}
			setState(401);
			((TableIdentifierContext)_localctx).table = errorCapturingIdentifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionIdentifierContext extends ParserRuleContext {
		public ErrorCapturingIdentifierContext db;
		public ErrorCapturingIdentifierContext function;
		public List<ErrorCapturingIdentifierContext> errorCapturingIdentifier() {
			return getRuleContexts(ErrorCapturingIdentifierContext.class);
		}
		public ErrorCapturingIdentifierContext errorCapturingIdentifier(int i) {
			return getRuleContext(ErrorCapturingIdentifierContext.class,i);
		}
		public FunctionIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterFunctionIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitFunctionIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitFunctionIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionIdentifierContext functionIdentifier() throws RecognitionException {
		FunctionIdentifierContext _localctx = new FunctionIdentifierContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_functionIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(406);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(403);
				((FunctionIdentifierContext)_localctx).db = errorCapturingIdentifier();
				setState(404);
				match(T__4);
				}
				break;
			}
			setState(408);
			((FunctionIdentifierContext)_localctx).function = errorCapturingIdentifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NamedExpressionContext extends ParserRuleContext {
		public ErrorCapturingIdentifierContext name;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TerminalNode AS() { return getToken(SqlBaseParser.AS, 0); }
		public ErrorCapturingIdentifierContext errorCapturingIdentifier() {
			return getRuleContext(ErrorCapturingIdentifierContext.class,0);
		}
		public NamedExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namedExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterNamedExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitNamedExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitNamedExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamedExpressionContext namedExpression() throws RecognitionException {
		NamedExpressionContext _localctx = new NamedExpressionContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_namedExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(410);
			expression();
			setState(418);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(412);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
				case 1:
					{
					setState(411);
					match(AS);
					}
					break;
				}
				setState(416);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
				case 1:
					{
					setState(414);
					((NamedExpressionContext)_localctx).name = errorCapturingIdentifier();
					}
					break;
				case 2:
					{
					setState(415);
					identifierList();
					}
					break;
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NamedExpressionSeqContext extends ParserRuleContext {
		public List<NamedExpressionContext> namedExpression() {
			return getRuleContexts(NamedExpressionContext.class);
		}
		public NamedExpressionContext namedExpression(int i) {
			return getRuleContext(NamedExpressionContext.class,i);
		}
		public NamedExpressionSeqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namedExpressionSeq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterNamedExpressionSeq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitNamedExpressionSeq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitNamedExpressionSeq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamedExpressionSeqContext namedExpressionSeq() throws RecognitionException {
		NamedExpressionSeqContext _localctx = new NamedExpressionSeqContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_namedExpressionSeq);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			namedExpression();
			setState(425);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(421);
					match(T__1);
					setState(422);
					namedExpression();
					}
					} 
				}
				setState(427);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public BooleanExpressionContext booleanExpression() {
			return getRuleContext(BooleanExpressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			booleanExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanExpressionContext extends ParserRuleContext {
		public BooleanExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanExpression; }
	 
		public BooleanExpressionContext() { }
		public void copyFrom(BooleanExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LogicalNotContext extends BooleanExpressionContext {
		public TerminalNode NOT() { return getToken(SqlBaseParser.NOT, 0); }
		public BooleanExpressionContext booleanExpression() {
			return getRuleContext(BooleanExpressionContext.class,0);
		}
		public LogicalNotContext(BooleanExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterLogicalNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitLogicalNot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitLogicalNot(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PredicatedContext extends BooleanExpressionContext {
		public ValueExpressionContext valueExpression() {
			return getRuleContext(ValueExpressionContext.class,0);
		}
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public PredicatedContext(BooleanExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterPredicated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitPredicated(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitPredicated(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalBinaryContext extends BooleanExpressionContext {
		public BooleanExpressionContext left;
		public Token operator;
		public BooleanExpressionContext right;
		public List<BooleanExpressionContext> booleanExpression() {
			return getRuleContexts(BooleanExpressionContext.class);
		}
		public BooleanExpressionContext booleanExpression(int i) {
			return getRuleContext(BooleanExpressionContext.class,i);
		}
		public TerminalNode AND() { return getToken(SqlBaseParser.AND, 0); }
		public TerminalNode OR() { return getToken(SqlBaseParser.OR, 0); }
		public LogicalBinaryContext(BooleanExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterLogicalBinary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitLogicalBinary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitLogicalBinary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanExpressionContext booleanExpression() throws RecognitionException {
		return booleanExpression(0);
	}

	private BooleanExpressionContext booleanExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		BooleanExpressionContext _localctx = new BooleanExpressionContext(_ctx, _parentState);
		BooleanExpressionContext _prevctx = _localctx;
		int _startState = 82;
		enterRecursionRule(_localctx, 82, RULE_booleanExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(437);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				{
				_localctx = new LogicalNotContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(431);
				match(NOT);
				setState(432);
				booleanExpression(4);
				}
				break;
			case 2:
				{
				_localctx = new PredicatedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(433);
				valueExpression(0);
				setState(435);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
				case 1:
					{
					setState(434);
					predicate();
					}
					break;
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(447);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(445);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
					case 1:
						{
						_localctx = new LogicalBinaryContext(new BooleanExpressionContext(_parentctx, _parentState));
						((LogicalBinaryContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpression);
						setState(439);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(440);
						((LogicalBinaryContext)_localctx).operator = match(AND);
						setState(441);
						((LogicalBinaryContext)_localctx).right = booleanExpression(3);
						}
						break;
					case 2:
						{
						_localctx = new LogicalBinaryContext(new BooleanExpressionContext(_parentctx, _parentState));
						((LogicalBinaryContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpression);
						setState(442);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(443);
						((LogicalBinaryContext)_localctx).operator = match(OR);
						setState(444);
						((LogicalBinaryContext)_localctx).right = booleanExpression(2);
						}
						break;
					}
					} 
				}
				setState(449);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public Token kind;
		public ValueExpressionContext lower;
		public ValueExpressionContext upper;
		public ValueExpressionContext pattern;
		public Token quantifier;
		public Token escapeChar;
		public TerminalNode AND() { return getToken(SqlBaseParser.AND, 0); }
		public TerminalNode BETWEEN() { return getToken(SqlBaseParser.BETWEEN, 0); }
		public List<ValueExpressionContext> valueExpression() {
			return getRuleContexts(ValueExpressionContext.class);
		}
		public ValueExpressionContext valueExpression(int i) {
			return getRuleContext(ValueExpressionContext.class,i);
		}
		public TerminalNode NOT() { return getToken(SqlBaseParser.NOT, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode IN() { return getToken(SqlBaseParser.IN, 0); }
		public TerminalNode RLIKE() { return getToken(SqlBaseParser.RLIKE, 0); }
		public TerminalNode LIKE() { return getToken(SqlBaseParser.LIKE, 0); }
		public TerminalNode ANY() { return getToken(SqlBaseParser.ANY, 0); }
		public TerminalNode SOME() { return getToken(SqlBaseParser.SOME, 0); }
		public TerminalNode ALL() { return getToken(SqlBaseParser.ALL, 0); }
		public TerminalNode ESCAPE() { return getToken(SqlBaseParser.ESCAPE, 0); }
		public TerminalNode STRING() { return getToken(SqlBaseParser.STRING, 0); }
		public TerminalNode IS() { return getToken(SqlBaseParser.IS, 0); }
		public TerminalNode NULL() { return getToken(SqlBaseParser.NULL, 0); }
		public TerminalNode TRUE() { return getToken(SqlBaseParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(SqlBaseParser.FALSE, 0); }
		public TerminalNode UNKNOWN() { return getToken(SqlBaseParser.UNKNOWN, 0); }
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_predicate);
		int _la;
		try {
			setState(517);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(451);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(450);
					match(NOT);
					}
				}

				setState(453);
				((PredicateContext)_localctx).kind = match(BETWEEN);
				setState(454);
				((PredicateContext)_localctx).lower = valueExpression(0);
				setState(455);
				match(AND);
				setState(456);
				((PredicateContext)_localctx).upper = valueExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(459);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(458);
					match(NOT);
					}
				}

				setState(461);
				((PredicateContext)_localctx).kind = match(IN);
				setState(462);
				match(T__2);
				setState(463);
				expression();
				setState(468);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(464);
					match(T__1);
					setState(465);
					expression();
					}
					}
					setState(470);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(471);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(474);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(473);
					match(NOT);
					}
				}

				setState(476);
				((PredicateContext)_localctx).kind = match(RLIKE);
				setState(477);
				((PredicateContext)_localctx).pattern = valueExpression(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(479);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(478);
					match(NOT);
					}
				}

				setState(481);
				((PredicateContext)_localctx).kind = match(LIKE);
				setState(482);
				((PredicateContext)_localctx).quantifier = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==ALL || _la==ANY || _la==SOME) ) {
					((PredicateContext)_localctx).quantifier = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(496);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(483);
					match(T__2);
					setState(484);
					match(T__3);
					}
					break;
				case 2:
					{
					setState(485);
					match(T__2);
					setState(486);
					expression();
					setState(491);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__1) {
						{
						{
						setState(487);
						match(T__1);
						setState(488);
						expression();
						}
						}
						setState(493);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(494);
					match(T__3);
					}
					break;
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(499);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(498);
					match(NOT);
					}
				}

				setState(501);
				((PredicateContext)_localctx).kind = match(LIKE);
				setState(502);
				((PredicateContext)_localctx).pattern = valueExpression(0);
				setState(505);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
				case 1:
					{
					setState(503);
					match(ESCAPE);
					setState(504);
					((PredicateContext)_localctx).escapeChar = match(STRING);
					}
					break;
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(507);
				match(IS);
				setState(509);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(508);
					match(NOT);
					}
				}

				setState(511);
				((PredicateContext)_localctx).kind = match(NULL);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(512);
				match(IS);
				setState(514);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(513);
					match(NOT);
					}
				}

				setState(516);
				((PredicateContext)_localctx).kind = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==FALSE || _la==TRUE || _la==UNKNOWN) ) {
					((PredicateContext)_localctx).kind = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueExpressionContext extends ParserRuleContext {
		public ValueExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_valueExpression; }
	 
		public ValueExpressionContext() { }
		public void copyFrom(ValueExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ValueExpressionDefaultContext extends ValueExpressionContext {
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public ValueExpressionDefaultContext(ValueExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterValueExpressionDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitValueExpressionDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitValueExpressionDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ComparisonContext extends ValueExpressionContext {
		public ValueExpressionContext left;
		public ValueExpressionContext right;
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public List<ValueExpressionContext> valueExpression() {
			return getRuleContexts(ValueExpressionContext.class);
		}
		public ValueExpressionContext valueExpression(int i) {
			return getRuleContext(ValueExpressionContext.class,i);
		}
		public ComparisonContext(ValueExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitComparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitComparison(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticBinaryContext extends ValueExpressionContext {
		public ValueExpressionContext left;
		public Token operator;
		public ValueExpressionContext right;
		public List<ValueExpressionContext> valueExpression() {
			return getRuleContexts(ValueExpressionContext.class);
		}
		public ValueExpressionContext valueExpression(int i) {
			return getRuleContext(ValueExpressionContext.class,i);
		}
		public TerminalNode ASTERISK() { return getToken(SqlBaseParser.ASTERISK, 0); }
		public TerminalNode SLASH() { return getToken(SqlBaseParser.SLASH, 0); }
		public TerminalNode PERCENT() { return getToken(SqlBaseParser.PERCENT, 0); }
		public TerminalNode DIV() { return getToken(SqlBaseParser.DIV, 0); }
		public TerminalNode PLUS() { return getToken(SqlBaseParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public TerminalNode CONCAT_PIPE() { return getToken(SqlBaseParser.CONCAT_PIPE, 0); }
		public TerminalNode AMPERSAND() { return getToken(SqlBaseParser.AMPERSAND, 0); }
		public TerminalNode HAT() { return getToken(SqlBaseParser.HAT, 0); }
		public TerminalNode PIPE() { return getToken(SqlBaseParser.PIPE, 0); }
		public ArithmeticBinaryContext(ValueExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterArithmeticBinary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitArithmeticBinary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitArithmeticBinary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticUnaryContext extends ValueExpressionContext {
		public Token operator;
		public ValueExpressionContext valueExpression() {
			return getRuleContext(ValueExpressionContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public TerminalNode PLUS() { return getToken(SqlBaseParser.PLUS, 0); }
		public TerminalNode TILDE() { return getToken(SqlBaseParser.TILDE, 0); }
		public ArithmeticUnaryContext(ValueExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterArithmeticUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitArithmeticUnary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitArithmeticUnary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueExpressionContext valueExpression() throws RecognitionException {
		return valueExpression(0);
	}

	private ValueExpressionContext valueExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ValueExpressionContext _localctx = new ValueExpressionContext(_ctx, _parentState);
		ValueExpressionContext _prevctx = _localctx;
		int _startState = 86;
		enterRecursionRule(_localctx, 86, RULE_valueExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(523);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				_localctx = new ValueExpressionDefaultContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(520);
				primaryExpression(0);
				}
				break;
			case 2:
				{
				_localctx = new ArithmeticUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(521);
				((ArithmeticUnaryContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 268)) & ~0x3f) == 0 && ((1L << (_la - 268)) & ((1L << (PLUS - 268)) | (1L << (MINUS - 268)) | (1L << (TILDE - 268)))) != 0)) ) {
					((ArithmeticUnaryContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(522);
				valueExpression(7);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(546);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(544);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticBinaryContext(new ValueExpressionContext(_parentctx, _parentState));
						((ArithmeticBinaryContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_valueExpression);
						setState(525);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(526);
						((ArithmeticBinaryContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==DIV || ((((_la - 270)) & ~0x3f) == 0 && ((1L << (_la - 270)) & ((1L << (ASTERISK - 270)) | (1L << (SLASH - 270)) | (1L << (PERCENT - 270)))) != 0)) ) {
							((ArithmeticBinaryContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(527);
						((ArithmeticBinaryContext)_localctx).right = valueExpression(7);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticBinaryContext(new ValueExpressionContext(_parentctx, _parentState));
						((ArithmeticBinaryContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_valueExpression);
						setState(528);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(529);
						((ArithmeticBinaryContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 268)) & ~0x3f) == 0 && ((1L << (_la - 268)) & ((1L << (PLUS - 268)) | (1L << (MINUS - 268)) | (1L << (CONCAT_PIPE - 268)))) != 0)) ) {
							((ArithmeticBinaryContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(530);
						((ArithmeticBinaryContext)_localctx).right = valueExpression(6);
						}
						break;
					case 3:
						{
						_localctx = new ArithmeticBinaryContext(new ValueExpressionContext(_parentctx, _parentState));
						((ArithmeticBinaryContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_valueExpression);
						setState(531);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(532);
						((ArithmeticBinaryContext)_localctx).operator = match(AMPERSAND);
						setState(533);
						((ArithmeticBinaryContext)_localctx).right = valueExpression(5);
						}
						break;
					case 4:
						{
						_localctx = new ArithmeticBinaryContext(new ValueExpressionContext(_parentctx, _parentState));
						((ArithmeticBinaryContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_valueExpression);
						setState(534);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(535);
						((ArithmeticBinaryContext)_localctx).operator = match(HAT);
						setState(536);
						((ArithmeticBinaryContext)_localctx).right = valueExpression(4);
						}
						break;
					case 5:
						{
						_localctx = new ArithmeticBinaryContext(new ValueExpressionContext(_parentctx, _parentState));
						((ArithmeticBinaryContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_valueExpression);
						setState(537);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(538);
						((ArithmeticBinaryContext)_localctx).operator = match(PIPE);
						setState(539);
						((ArithmeticBinaryContext)_localctx).right = valueExpression(3);
						}
						break;
					case 6:
						{
						_localctx = new ComparisonContext(new ValueExpressionContext(_parentctx, _parentState));
						((ComparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_valueExpression);
						setState(540);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(541);
						comparisonOperator();
						setState(542);
						((ComparisonContext)_localctx).right = valueExpression(2);
						}
						break;
					}
					} 
				}
				setState(548);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PrimaryExpressionContext extends ParserRuleContext {
		public PrimaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpression; }
	 
		public PrimaryExpressionContext() { }
		public void copyFrom(PrimaryExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StructContext extends PrimaryExpressionContext {
		public NamedExpressionContext namedExpression;
		public List<NamedExpressionContext> argument = new ArrayList<NamedExpressionContext>();
		public TerminalNode STRUCT() { return getToken(SqlBaseParser.STRUCT, 0); }
		public List<NamedExpressionContext> namedExpression() {
			return getRuleContexts(NamedExpressionContext.class);
		}
		public NamedExpressionContext namedExpression(int i) {
			return getRuleContext(NamedExpressionContext.class,i);
		}
		public StructContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterStruct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitStruct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitStruct(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DereferenceContext extends PrimaryExpressionContext {
		public PrimaryExpressionContext base;
		public IdentifierContext fieldName;
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public DereferenceContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterDereference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitDereference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitDereference(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SimpleCaseContext extends PrimaryExpressionContext {
		public ExpressionContext value;
		public ExpressionContext elseExpression;
		public TerminalNode CASE() { return getToken(SqlBaseParser.CASE, 0); }
		public TerminalNode END() { return getToken(SqlBaseParser.END, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<WhenClauseContext> whenClause() {
			return getRuleContexts(WhenClauseContext.class);
		}
		public WhenClauseContext whenClause(int i) {
			return getRuleContext(WhenClauseContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(SqlBaseParser.ELSE, 0); }
		public SimpleCaseContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSimpleCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSimpleCase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSimpleCase(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ColumnReferenceContext extends PrimaryExpressionContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ColumnReferenceContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterColumnReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitColumnReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitColumnReference(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RowConstructorContext extends PrimaryExpressionContext {
		public List<NamedExpressionContext> namedExpression() {
			return getRuleContexts(NamedExpressionContext.class);
		}
		public NamedExpressionContext namedExpression(int i) {
			return getRuleContext(NamedExpressionContext.class,i);
		}
		public RowConstructorContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterRowConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitRowConstructor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitRowConstructor(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LastContext extends PrimaryExpressionContext {
		public TerminalNode LAST() { return getToken(SqlBaseParser.LAST, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IGNORE() { return getToken(SqlBaseParser.IGNORE, 0); }
		public TerminalNode NULLS() { return getToken(SqlBaseParser.NULLS, 0); }
		public LastContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterLast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitLast(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitLast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StarContext extends PrimaryExpressionContext {
		public TerminalNode ASTERISK() { return getToken(SqlBaseParser.ASTERISK, 0); }
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public StarContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterStar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitStar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitStar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SubscriptContext extends PrimaryExpressionContext {
		public PrimaryExpressionContext value;
		public ValueExpressionContext index;
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public ValueExpressionContext valueExpression() {
			return getRuleContext(ValueExpressionContext.class,0);
		}
		public SubscriptContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSubscript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSubscript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSubscript(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SubstringContext extends PrimaryExpressionContext {
		public ValueExpressionContext str;
		public ValueExpressionContext pos;
		public ValueExpressionContext len;
		public TerminalNode SUBSTR() { return getToken(SqlBaseParser.SUBSTR, 0); }
		public TerminalNode SUBSTRING() { return getToken(SqlBaseParser.SUBSTRING, 0); }
		public List<ValueExpressionContext> valueExpression() {
			return getRuleContexts(ValueExpressionContext.class);
		}
		public ValueExpressionContext valueExpression(int i) {
			return getRuleContext(ValueExpressionContext.class,i);
		}
		public TerminalNode FROM() { return getToken(SqlBaseParser.FROM, 0); }
		public TerminalNode FOR() { return getToken(SqlBaseParser.FOR, 0); }
		public SubstringContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSubstring(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSubstring(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSubstring(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CastContext extends PrimaryExpressionContext {
		public TerminalNode CAST() { return getToken(SqlBaseParser.CAST, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode AS() { return getToken(SqlBaseParser.AS, 0); }
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public CastContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterCast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitCast(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitCast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConstantDefaultContext extends PrimaryExpressionContext {
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public ConstantDefaultContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterConstantDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitConstantDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitConstantDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LambdaContext extends PrimaryExpressionContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LambdaContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterLambda(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitLambda(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitLambda(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesizedExpressionContext extends PrimaryExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParenthesizedExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterParenthesizedExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitParenthesizedExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitParenthesizedExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExtractContext extends PrimaryExpressionContext {
		public IdentifierContext field;
		public ValueExpressionContext source;
		public TerminalNode EXTRACT() { return getToken(SqlBaseParser.EXTRACT, 0); }
		public TerminalNode FROM() { return getToken(SqlBaseParser.FROM, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ValueExpressionContext valueExpression() {
			return getRuleContext(ValueExpressionContext.class,0);
		}
		public ExtractContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterExtract(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitExtract(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitExtract(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Any_valueContext extends PrimaryExpressionContext {
		public TerminalNode ANY_VALUE() { return getToken(SqlBaseParser.ANY_VALUE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IGNORE() { return getToken(SqlBaseParser.IGNORE, 0); }
		public TerminalNode NULLS() { return getToken(SqlBaseParser.NULLS, 0); }
		public Any_valueContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterAny_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitAny_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitAny_value(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TrimContext extends PrimaryExpressionContext {
		public Token trimOption;
		public ValueExpressionContext trimStr;
		public ValueExpressionContext srcStr;
		public TerminalNode TRIM() { return getToken(SqlBaseParser.TRIM, 0); }
		public TerminalNode FROM() { return getToken(SqlBaseParser.FROM, 0); }
		public List<ValueExpressionContext> valueExpression() {
			return getRuleContexts(ValueExpressionContext.class);
		}
		public ValueExpressionContext valueExpression(int i) {
			return getRuleContext(ValueExpressionContext.class,i);
		}
		public TerminalNode BOTH() { return getToken(SqlBaseParser.BOTH, 0); }
		public TerminalNode LEADING() { return getToken(SqlBaseParser.LEADING, 0); }
		public TerminalNode TRAILING() { return getToken(SqlBaseParser.TRAILING, 0); }
		public TrimContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterTrim(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitTrim(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitTrim(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionCallContext extends PrimaryExpressionContext {
		public ExpressionContext expression;
		public List<ExpressionContext> argument = new ArrayList<ExpressionContext>();
		public BooleanExpressionContext where;
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public TerminalNode FILTER() { return getToken(SqlBaseParser.FILTER, 0); }
		public TerminalNode WHERE() { return getToken(SqlBaseParser.WHERE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public BooleanExpressionContext booleanExpression() {
			return getRuleContext(BooleanExpressionContext.class,0);
		}
		public FunctionCallContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SearchedCaseContext extends PrimaryExpressionContext {
		public ExpressionContext elseExpression;
		public TerminalNode CASE() { return getToken(SqlBaseParser.CASE, 0); }
		public TerminalNode END() { return getToken(SqlBaseParser.END, 0); }
		public List<WhenClauseContext> whenClause() {
			return getRuleContexts(WhenClauseContext.class);
		}
		public WhenClauseContext whenClause(int i) {
			return getRuleContext(WhenClauseContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(SqlBaseParser.ELSE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SearchedCaseContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSearchedCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSearchedCase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSearchedCase(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PositionContext extends PrimaryExpressionContext {
		public ValueExpressionContext substr;
		public ValueExpressionContext str;
		public TerminalNode POSITION() { return getToken(SqlBaseParser.POSITION, 0); }
		public TerminalNode IN() { return getToken(SqlBaseParser.IN, 0); }
		public List<ValueExpressionContext> valueExpression() {
			return getRuleContexts(ValueExpressionContext.class);
		}
		public ValueExpressionContext valueExpression(int i) {
			return getRuleContext(ValueExpressionContext.class,i);
		}
		public PositionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterPosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitPosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitPosition(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FirstContext extends PrimaryExpressionContext {
		public TerminalNode FIRST() { return getToken(SqlBaseParser.FIRST, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IGNORE() { return getToken(SqlBaseParser.IGNORE, 0); }
		public TerminalNode NULLS() { return getToken(SqlBaseParser.NULLS, 0); }
		public FirstContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterFirst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitFirst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitFirst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExpressionContext primaryExpression() throws RecognitionException {
		return primaryExpression(0);
	}

	private PrimaryExpressionContext primaryExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PrimaryExpressionContext _localctx = new PrimaryExpressionContext(_ctx, _parentState);
		PrimaryExpressionContext _prevctx = _localctx;
		int _startState = 88;
		enterRecursionRule(_localctx, 88, RULE_primaryExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(717);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				{
				_localctx = new SearchedCaseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(550);
				match(CASE);
				setState(552); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(551);
					whenClause();
					}
					}
					setState(554); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WHEN );
				setState(558);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(556);
					match(ELSE);
					setState(557);
					((SearchedCaseContext)_localctx).elseExpression = expression();
					}
				}

				setState(560);
				match(END);
				}
				break;
			case 2:
				{
				_localctx = new SimpleCaseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(562);
				match(CASE);
				setState(563);
				((SimpleCaseContext)_localctx).value = expression();
				setState(565); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(564);
					whenClause();
					}
					}
					setState(567); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WHEN );
				setState(571);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(569);
					match(ELSE);
					setState(570);
					((SimpleCaseContext)_localctx).elseExpression = expression();
					}
				}

				setState(573);
				match(END);
				}
				break;
			case 3:
				{
				_localctx = new CastContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(575);
				match(CAST);
				setState(576);
				match(T__2);
				setState(577);
				expression();
				setState(578);
				match(AS);
				setState(579);
				dataType();
				setState(580);
				match(T__3);
				}
				break;
			case 4:
				{
				_localctx = new StructContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(582);
				match(STRUCT);
				setState(583);
				match(T__2);
				setState(592);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
				case 1:
					{
					setState(584);
					((StructContext)_localctx).namedExpression = namedExpression();
					((StructContext)_localctx).argument.add(((StructContext)_localctx).namedExpression);
					setState(589);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__1) {
						{
						{
						setState(585);
						match(T__1);
						setState(586);
						((StructContext)_localctx).namedExpression = namedExpression();
						((StructContext)_localctx).argument.add(((StructContext)_localctx).namedExpression);
						}
						}
						setState(591);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					break;
				}
				setState(594);
				match(T__3);
				}
				break;
			case 5:
				{
				_localctx = new FirstContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(595);
				match(FIRST);
				setState(596);
				match(T__2);
				setState(597);
				expression();
				setState(600);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IGNORE) {
					{
					setState(598);
					match(IGNORE);
					setState(599);
					match(NULLS);
					}
				}

				setState(602);
				match(T__3);
				}
				break;
			case 6:
				{
				_localctx = new Any_valueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(604);
				match(ANY_VALUE);
				setState(605);
				match(T__2);
				setState(606);
				expression();
				setState(609);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IGNORE) {
					{
					setState(607);
					match(IGNORE);
					setState(608);
					match(NULLS);
					}
				}

				setState(611);
				match(T__3);
				}
				break;
			case 7:
				{
				_localctx = new LastContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(613);
				match(LAST);
				setState(614);
				match(T__2);
				setState(615);
				expression();
				setState(618);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IGNORE) {
					{
					setState(616);
					match(IGNORE);
					setState(617);
					match(NULLS);
					}
				}

				setState(620);
				match(T__3);
				}
				break;
			case 8:
				{
				_localctx = new PositionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(622);
				match(POSITION);
				setState(623);
				match(T__2);
				setState(624);
				((PositionContext)_localctx).substr = valueExpression(0);
				setState(625);
				match(IN);
				setState(626);
				((PositionContext)_localctx).str = valueExpression(0);
				setState(627);
				match(T__3);
				}
				break;
			case 9:
				{
				_localctx = new ConstantDefaultContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(629);
				constant();
				}
				break;
			case 10:
				{
				_localctx = new StarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(630);
				match(ASTERISK);
				}
				break;
			case 11:
				{
				_localctx = new StarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(631);
				qualifiedName();
				setState(632);
				match(T__4);
				setState(633);
				match(ASTERISK);
				}
				break;
			case 12:
				{
				_localctx = new RowConstructorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(635);
				match(T__2);
				setState(636);
				namedExpression();
				setState(639); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(637);
					match(T__1);
					setState(638);
					namedExpression();
					}
					}
					setState(641); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__1 );
				setState(643);
				match(T__3);
				}
				break;
			case 13:
				{
				_localctx = new FunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(645);
				functionName();
				setState(646);
				match(T__2);
				setState(655);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
				case 1:
					{
					setState(647);
					((FunctionCallContext)_localctx).expression = expression();
					((FunctionCallContext)_localctx).argument.add(((FunctionCallContext)_localctx).expression);
					setState(652);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__1) {
						{
						{
						setState(648);
						match(T__1);
						setState(649);
						((FunctionCallContext)_localctx).expression = expression();
						((FunctionCallContext)_localctx).argument.add(((FunctionCallContext)_localctx).expression);
						}
						}
						setState(654);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					break;
				}
				setState(657);
				match(T__3);
				setState(664);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
				case 1:
					{
					setState(658);
					match(FILTER);
					setState(659);
					match(T__2);
					setState(660);
					match(WHERE);
					setState(661);
					((FunctionCallContext)_localctx).where = booleanExpression(0);
					setState(662);
					match(T__3);
					}
					break;
				}
				}
				break;
			case 14:
				{
				_localctx = new LambdaContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(666);
				identifier();
				setState(667);
				match(T__5);
				setState(668);
				expression();
				}
				break;
			case 15:
				{
				_localctx = new LambdaContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(670);
				match(T__2);
				setState(671);
				identifier();
				setState(674); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(672);
					match(T__1);
					setState(673);
					identifier();
					}
					}
					setState(676); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__1 );
				setState(678);
				match(T__3);
				setState(679);
				match(T__5);
				setState(680);
				expression();
				}
				break;
			case 16:
				{
				_localctx = new ColumnReferenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(682);
				identifier();
				}
				break;
			case 17:
				{
				_localctx = new ParenthesizedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(683);
				match(T__2);
				setState(684);
				expression();
				setState(685);
				match(T__3);
				}
				break;
			case 18:
				{
				_localctx = new ExtractContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(687);
				match(EXTRACT);
				setState(688);
				match(T__2);
				setState(689);
				((ExtractContext)_localctx).field = identifier();
				setState(690);
				match(FROM);
				setState(691);
				((ExtractContext)_localctx).source = valueExpression(0);
				setState(692);
				match(T__3);
				}
				break;
			case 19:
				{
				_localctx = new SubstringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(694);
				_la = _input.LA(1);
				if ( !(_la==SUBSTR || _la==SUBSTRING) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(695);
				match(T__2);
				setState(696);
				((SubstringContext)_localctx).str = valueExpression(0);
				setState(697);
				_la = _input.LA(1);
				if ( !(_la==T__1 || _la==FROM) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(698);
				((SubstringContext)_localctx).pos = valueExpression(0);
				setState(701);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__1 || _la==FOR) {
					{
					setState(699);
					_la = _input.LA(1);
					if ( !(_la==T__1 || _la==FOR) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(700);
					((SubstringContext)_localctx).len = valueExpression(0);
					}
				}

				setState(703);
				match(T__3);
				}
				break;
			case 20:
				{
				_localctx = new TrimContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(705);
				match(TRIM);
				setState(706);
				match(T__2);
				setState(708);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
				case 1:
					{
					setState(707);
					((TrimContext)_localctx).trimOption = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==BOTH || _la==LEADING || _la==TRAILING) ) {
						((TrimContext)_localctx).trimOption = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(711);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
				case 1:
					{
					setState(710);
					((TrimContext)_localctx).trimStr = valueExpression(0);
					}
					break;
				}
				setState(713);
				match(FROM);
				setState(714);
				((TrimContext)_localctx).srcStr = valueExpression(0);
				setState(715);
				match(T__3);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(729);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(727);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
					case 1:
						{
						_localctx = new SubscriptContext(new PrimaryExpressionContext(_parentctx, _parentState));
						((SubscriptContext)_localctx).value = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
						setState(719);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(720);
						match(T__6);
						setState(721);
						((SubscriptContext)_localctx).index = valueExpression(0);
						setState(722);
						match(T__7);
						}
						break;
					case 2:
						{
						_localctx = new DereferenceContext(new PrimaryExpressionContext(_parentctx, _parentState));
						((DereferenceContext)_localctx).base = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
						setState(724);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(725);
						match(T__4);
						setState(726);
						((DereferenceContext)_localctx).fieldName = identifier();
						}
						break;
					}
					} 
				}
				setState(731);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ConstantContext extends ParserRuleContext {
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
	 
		public ConstantContext() { }
		public void copyFrom(ConstantContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NullLiteralContext extends ConstantContext {
		public TerminalNode NULL() { return getToken(SqlBaseParser.NULL, 0); }
		public NullLiteralContext(ConstantContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterNullLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitNullLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitNullLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringLiteralContext extends ConstantContext {
		public List<TerminalNode> STRING() { return getTokens(SqlBaseParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(SqlBaseParser.STRING, i);
		}
		public StringLiteralContext(ConstantContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntervalLiteralContext extends ConstantContext {
		public IntervalContext interval() {
			return getRuleContext(IntervalContext.class,0);
		}
		public IntervalLiteralContext(ConstantContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterIntervalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitIntervalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitIntervalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericLiteralContext extends ConstantContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public NumericLiteralContext(ConstantContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterNumericLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitNumericLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitNumericLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanLiteralContext extends ConstantContext {
		public BooleanValueContext booleanValue() {
			return getRuleContext(BooleanValueContext.class,0);
		}
		public BooleanLiteralContext(ConstantContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitBooleanLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_constant);
		try {
			int _alt;
			setState(741);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				_localctx = new NullLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(732);
				match(NULL);
				}
				break;
			case 2:
				_localctx = new IntervalLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(733);
				interval();
				}
				break;
			case 3:
				_localctx = new NumericLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(734);
				number();
				}
				break;
			case 4:
				_localctx = new BooleanLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(735);
				booleanValue();
				}
				break;
			case 5:
				_localctx = new StringLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(737); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(736);
						match(STRING);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(739); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,81,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonOperatorContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(SqlBaseParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(SqlBaseParser.NEQ, 0); }
		public TerminalNode NEQJ() { return getToken(SqlBaseParser.NEQJ, 0); }
		public TerminalNode LT() { return getToken(SqlBaseParser.LT, 0); }
		public TerminalNode LTE() { return getToken(SqlBaseParser.LTE, 0); }
		public TerminalNode GT() { return getToken(SqlBaseParser.GT, 0); }
		public TerminalNode GTE() { return getToken(SqlBaseParser.GTE, 0); }
		public TerminalNode NSEQ() { return getToken(SqlBaseParser.NSEQ, 0); }
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitComparisonOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(743);
			_la = _input.LA(1);
			if ( !(((((_la - 260)) & ~0x3f) == 0 && ((1L << (_la - 260)) & ((1L << (EQ - 260)) | (1L << (NSEQ - 260)) | (1L << (NEQ - 260)) | (1L << (NEQJ - 260)) | (1L << (LT - 260)) | (1L << (LTE - 260)) | (1L << (GT - 260)) | (1L << (GTE - 260)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanValueContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(SqlBaseParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(SqlBaseParser.FALSE, 0); }
		public BooleanValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterBooleanValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitBooleanValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitBooleanValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanValueContext booleanValue() throws RecognitionException {
		BooleanValueContext _localctx = new BooleanValueContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_booleanValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(745);
			_la = _input.LA(1);
			if ( !(_la==FALSE || _la==TRUE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntervalContext extends ParserRuleContext {
		public TerminalNode INTERVAL() { return getToken(SqlBaseParser.INTERVAL, 0); }
		public ErrorCapturingMultiUnitsIntervalContext errorCapturingMultiUnitsInterval() {
			return getRuleContext(ErrorCapturingMultiUnitsIntervalContext.class,0);
		}
		public ErrorCapturingUnitToUnitIntervalContext errorCapturingUnitToUnitInterval() {
			return getRuleContext(ErrorCapturingUnitToUnitIntervalContext.class,0);
		}
		public IntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitInterval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitInterval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntervalContext interval() throws RecognitionException {
		IntervalContext _localctx = new IntervalContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_interval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(747);
			match(INTERVAL);
			setState(750);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				{
				setState(748);
				errorCapturingMultiUnitsInterval();
				}
				break;
			case 2:
				{
				setState(749);
				errorCapturingUnitToUnitInterval();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorCapturingMultiUnitsIntervalContext extends ParserRuleContext {
		public MultiUnitsIntervalContext multiUnitsInterval() {
			return getRuleContext(MultiUnitsIntervalContext.class,0);
		}
		public UnitToUnitIntervalContext unitToUnitInterval() {
			return getRuleContext(UnitToUnitIntervalContext.class,0);
		}
		public ErrorCapturingMultiUnitsIntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_errorCapturingMultiUnitsInterval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterErrorCapturingMultiUnitsInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitErrorCapturingMultiUnitsInterval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitErrorCapturingMultiUnitsInterval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ErrorCapturingMultiUnitsIntervalContext errorCapturingMultiUnitsInterval() throws RecognitionException {
		ErrorCapturingMultiUnitsIntervalContext _localctx = new ErrorCapturingMultiUnitsIntervalContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_errorCapturingMultiUnitsInterval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(752);
			multiUnitsInterval();
			setState(754);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				{
				setState(753);
				unitToUnitInterval();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiUnitsIntervalContext extends ParserRuleContext {
		public IdentifierContext identifier;
		public List<IdentifierContext> unit = new ArrayList<IdentifierContext>();
		public List<IntervalValueContext> intervalValue() {
			return getRuleContexts(IntervalValueContext.class);
		}
		public IntervalValueContext intervalValue(int i) {
			return getRuleContext(IntervalValueContext.class,i);
		}
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public MultiUnitsIntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiUnitsInterval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterMultiUnitsInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitMultiUnitsInterval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitMultiUnitsInterval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiUnitsIntervalContext multiUnitsInterval() throws RecognitionException {
		MultiUnitsIntervalContext _localctx = new MultiUnitsIntervalContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_multiUnitsInterval);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(759); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(756);
					intervalValue();
					setState(757);
					((MultiUnitsIntervalContext)_localctx).identifier = identifier();
					((MultiUnitsIntervalContext)_localctx).unit.add(((MultiUnitsIntervalContext)_localctx).identifier);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(761); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,85,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorCapturingUnitToUnitIntervalContext extends ParserRuleContext {
		public UnitToUnitIntervalContext body;
		public MultiUnitsIntervalContext error1;
		public UnitToUnitIntervalContext error2;
		public List<UnitToUnitIntervalContext> unitToUnitInterval() {
			return getRuleContexts(UnitToUnitIntervalContext.class);
		}
		public UnitToUnitIntervalContext unitToUnitInterval(int i) {
			return getRuleContext(UnitToUnitIntervalContext.class,i);
		}
		public MultiUnitsIntervalContext multiUnitsInterval() {
			return getRuleContext(MultiUnitsIntervalContext.class,0);
		}
		public ErrorCapturingUnitToUnitIntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_errorCapturingUnitToUnitInterval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterErrorCapturingUnitToUnitInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitErrorCapturingUnitToUnitInterval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitErrorCapturingUnitToUnitInterval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ErrorCapturingUnitToUnitIntervalContext errorCapturingUnitToUnitInterval() throws RecognitionException {
		ErrorCapturingUnitToUnitIntervalContext _localctx = new ErrorCapturingUnitToUnitIntervalContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_errorCapturingUnitToUnitInterval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(763);
			((ErrorCapturingUnitToUnitIntervalContext)_localctx).body = unitToUnitInterval();
			setState(766);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				{
				setState(764);
				((ErrorCapturingUnitToUnitIntervalContext)_localctx).error1 = multiUnitsInterval();
				}
				break;
			case 2:
				{
				setState(765);
				((ErrorCapturingUnitToUnitIntervalContext)_localctx).error2 = unitToUnitInterval();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnitToUnitIntervalContext extends ParserRuleContext {
		public IntervalValueContext value;
		public IdentifierContext from;
		public IdentifierContext to;
		public TerminalNode TO() { return getToken(SqlBaseParser.TO, 0); }
		public IntervalValueContext intervalValue() {
			return getRuleContext(IntervalValueContext.class,0);
		}
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public UnitToUnitIntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unitToUnitInterval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterUnitToUnitInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitUnitToUnitInterval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitUnitToUnitInterval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnitToUnitIntervalContext unitToUnitInterval() throws RecognitionException {
		UnitToUnitIntervalContext _localctx = new UnitToUnitIntervalContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_unitToUnitInterval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
			((UnitToUnitIntervalContext)_localctx).value = intervalValue();
			setState(769);
			((UnitToUnitIntervalContext)_localctx).from = identifier();
			setState(770);
			match(TO);
			setState(771);
			((UnitToUnitIntervalContext)_localctx).to = identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntervalValueContext extends ParserRuleContext {
		public TerminalNode INTEGER_VALUE() { return getToken(SqlBaseParser.INTEGER_VALUE, 0); }
		public TerminalNode DECIMAL_VALUE() { return getToken(SqlBaseParser.DECIMAL_VALUE, 0); }
		public TerminalNode PLUS() { return getToken(SqlBaseParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public TerminalNode STRING() { return getToken(SqlBaseParser.STRING, 0); }
		public IntervalValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intervalValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterIntervalValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitIntervalValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitIntervalValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntervalValueContext intervalValue() throws RecognitionException {
		IntervalValueContext _localctx = new IntervalValueContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_intervalValue);
		int _la;
		try {
			setState(778);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case INTEGER_VALUE:
			case DECIMAL_VALUE:
				enterOuterAlt(_localctx, 1);
				{
				setState(774);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PLUS || _la==MINUS) {
					{
					setState(773);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==MINUS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(776);
				_la = _input.LA(1);
				if ( !(_la==INTEGER_VALUE || _la==DECIMAL_VALUE) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(777);
				match(STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DataTypeContext extends ParserRuleContext {
		public DataTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataType; }
	 
		public DataTypeContext() { }
		public void copyFrom(DataTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ComplexDataTypeContext extends DataTypeContext {
		public Token complex;
		public List<DataTypeContext> dataType() {
			return getRuleContexts(DataTypeContext.class);
		}
		public DataTypeContext dataType(int i) {
			return getRuleContext(DataTypeContext.class,i);
		}
		public TerminalNode ARRAY() { return getToken(SqlBaseParser.ARRAY, 0); }
		public TerminalNode MAP() { return getToken(SqlBaseParser.MAP, 0); }
		public TerminalNode STRUCT() { return getToken(SqlBaseParser.STRUCT, 0); }
		public TerminalNode NEQ() { return getToken(SqlBaseParser.NEQ, 0); }
		public ComplexColTypeListContext complexColTypeList() {
			return getRuleContext(ComplexColTypeListContext.class,0);
		}
		public ComplexDataTypeContext(DataTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterComplexDataType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitComplexDataType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitComplexDataType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrimitiveDataTypeContext extends DataTypeContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public List<TerminalNode> INTEGER_VALUE() { return getTokens(SqlBaseParser.INTEGER_VALUE); }
		public TerminalNode INTEGER_VALUE(int i) {
			return getToken(SqlBaseParser.INTEGER_VALUE, i);
		}
		public PrimitiveDataTypeContext(DataTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterPrimitiveDataType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitPrimitiveDataType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitPrimitiveDataType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataTypeContext dataType() throws RecognitionException {
		DataTypeContext _localctx = new DataTypeContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_dataType);
		int _la;
		try {
			setState(814);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				_localctx = new ComplexDataTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(780);
				((ComplexDataTypeContext)_localctx).complex = match(ARRAY);
				setState(781);
				match(LT);
				setState(782);
				dataType();
				setState(783);
				match(GT);
				}
				break;
			case 2:
				_localctx = new ComplexDataTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(785);
				((ComplexDataTypeContext)_localctx).complex = match(MAP);
				setState(786);
				match(LT);
				setState(787);
				dataType();
				setState(788);
				match(T__1);
				setState(789);
				dataType();
				setState(790);
				match(GT);
				}
				break;
			case 3:
				_localctx = new ComplexDataTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(792);
				((ComplexDataTypeContext)_localctx).complex = match(STRUCT);
				setState(799);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LT:
					{
					setState(793);
					match(LT);
					setState(795);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
					case 1:
						{
						setState(794);
						complexColTypeList();
						}
						break;
					}
					setState(797);
					match(GT);
					}
					break;
				case NEQ:
					{
					setState(798);
					match(NEQ);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 4:
				_localctx = new PrimitiveDataTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(801);
				identifier();
				setState(812);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(802);
					match(T__2);
					setState(803);
					match(INTEGER_VALUE);
					setState(808);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__1) {
						{
						{
						setState(804);
						match(T__1);
						setState(805);
						match(INTEGER_VALUE);
						}
						}
						setState(810);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(811);
					match(T__3);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColTypeListContext extends ParserRuleContext {
		public List<ColTypeContext> colType() {
			return getRuleContexts(ColTypeContext.class);
		}
		public ColTypeContext colType(int i) {
			return getRuleContext(ColTypeContext.class,i);
		}
		public ColTypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colTypeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterColTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitColTypeList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitColTypeList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColTypeListContext colTypeList() throws RecognitionException {
		ColTypeListContext _localctx = new ColTypeListContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_colTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			colType();
			setState(821);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(817);
				match(T__1);
				setState(818);
				colType();
				}
				}
				setState(823);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColTypeContext extends ParserRuleContext {
		public ErrorCapturingIdentifierContext colName;
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public ErrorCapturingIdentifierContext errorCapturingIdentifier() {
			return getRuleContext(ErrorCapturingIdentifierContext.class,0);
		}
		public TerminalNode NOT() { return getToken(SqlBaseParser.NOT, 0); }
		public TerminalNode NULL() { return getToken(SqlBaseParser.NULL, 0); }
		public CommentSpecContext commentSpec() {
			return getRuleContext(CommentSpecContext.class,0);
		}
		public ColTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterColType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitColType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitColType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColTypeContext colType() throws RecognitionException {
		ColTypeContext _localctx = new ColTypeContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_colType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(824);
			((ColTypeContext)_localctx).colName = errorCapturingIdentifier();
			setState(825);
			dataType();
			setState(828);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(826);
				match(NOT);
				setState(827);
				match(NULL);
				}
			}

			setState(831);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(830);
				commentSpec();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComplexColTypeListContext extends ParserRuleContext {
		public List<ComplexColTypeContext> complexColType() {
			return getRuleContexts(ComplexColTypeContext.class);
		}
		public ComplexColTypeContext complexColType(int i) {
			return getRuleContext(ComplexColTypeContext.class,i);
		}
		public ComplexColTypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_complexColTypeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterComplexColTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitComplexColTypeList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitComplexColTypeList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComplexColTypeListContext complexColTypeList() throws RecognitionException {
		ComplexColTypeListContext _localctx = new ComplexColTypeListContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_complexColTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(833);
			complexColType();
			setState(838);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(834);
				match(T__1);
				setState(835);
				complexColType();
				}
				}
				setState(840);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComplexColTypeContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public TerminalNode NOT() { return getToken(SqlBaseParser.NOT, 0); }
		public TerminalNode NULL() { return getToken(SqlBaseParser.NULL, 0); }
		public CommentSpecContext commentSpec() {
			return getRuleContext(CommentSpecContext.class,0);
		}
		public ComplexColTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_complexColType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterComplexColType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitComplexColType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitComplexColType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComplexColTypeContext complexColType() throws RecognitionException {
		ComplexColTypeContext _localctx = new ComplexColTypeContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_complexColType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(841);
			identifier();
			setState(842);
			match(T__8);
			setState(843);
			dataType();
			setState(846);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(844);
				match(NOT);
				setState(845);
				match(NULL);
				}
			}

			setState(849);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(848);
				commentSpec();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhenClauseContext extends ParserRuleContext {
		public ExpressionContext condition;
		public ExpressionContext result;
		public TerminalNode WHEN() { return getToken(SqlBaseParser.WHEN, 0); }
		public TerminalNode THEN() { return getToken(SqlBaseParser.THEN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public WhenClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whenClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterWhenClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitWhenClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitWhenClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhenClauseContext whenClause() throws RecognitionException {
		WhenClauseContext _localctx = new WhenClauseContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_whenClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(851);
			match(WHEN);
			setState(852);
			((WhenClauseContext)_localctx).condition = expression();
			setState(853);
			match(THEN);
			setState(854);
			((WhenClauseContext)_localctx).result = expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionNameContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public TerminalNode FILTER() { return getToken(SqlBaseParser.FILTER, 0); }
		public TerminalNode LEFT() { return getToken(SqlBaseParser.LEFT, 0); }
		public TerminalNode RIGHT() { return getToken(SqlBaseParser.RIGHT, 0); }
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitFunctionName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitFunctionName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_functionName);
		try {
			setState(860);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(856);
				qualifiedName();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(857);
				match(FILTER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(858);
				match(LEFT);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(859);
				match(RIGHT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QualifiedNameContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public QualifiedNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterQualifiedName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitQualifiedName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitQualifiedName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QualifiedNameContext qualifiedName() throws RecognitionException {
		QualifiedNameContext _localctx = new QualifiedNameContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_qualifiedName);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(862);
			identifier();
			setState(867);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,101,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(863);
					match(T__4);
					setState(864);
					identifier();
					}
					} 
				}
				setState(869);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,101,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorCapturingIdentifierContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ErrorCapturingIdentifierExtraContext errorCapturingIdentifierExtra() {
			return getRuleContext(ErrorCapturingIdentifierExtraContext.class,0);
		}
		public ErrorCapturingIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_errorCapturingIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterErrorCapturingIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitErrorCapturingIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitErrorCapturingIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ErrorCapturingIdentifierContext errorCapturingIdentifier() throws RecognitionException {
		ErrorCapturingIdentifierContext _localctx = new ErrorCapturingIdentifierContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_errorCapturingIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(870);
			identifier();
			setState(871);
			errorCapturingIdentifierExtra();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorCapturingIdentifierExtraContext extends ParserRuleContext {
		public ErrorCapturingIdentifierExtraContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_errorCapturingIdentifierExtra; }
	 
		public ErrorCapturingIdentifierExtraContext() { }
		public void copyFrom(ErrorCapturingIdentifierExtraContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ErrorIdentContext extends ErrorCapturingIdentifierExtraContext {
		public List<TerminalNode> MINUS() { return getTokens(SqlBaseParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(SqlBaseParser.MINUS, i);
		}
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public ErrorIdentContext(ErrorCapturingIdentifierExtraContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterErrorIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitErrorIdent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitErrorIdent(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RealIdentContext extends ErrorCapturingIdentifierExtraContext {
		public RealIdentContext(ErrorCapturingIdentifierExtraContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterRealIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitRealIdent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitRealIdent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ErrorCapturingIdentifierExtraContext errorCapturingIdentifierExtra() throws RecognitionException {
		ErrorCapturingIdentifierExtraContext _localctx = new ErrorCapturingIdentifierExtraContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_errorCapturingIdentifierExtra);
		try {
			int _alt;
			setState(880);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				_localctx = new ErrorIdentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(875); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(873);
						match(MINUS);
						setState(874);
						identifier();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(877); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,102,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				_localctx = new RealIdentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public StrictIdentifierContext strictIdentifier() {
			return getRuleContext(StrictIdentifierContext.class,0);
		}
		public StrictNonReservedContext strictNonReserved() {
			return getRuleContext(StrictNonReservedContext.class,0);
		}
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_identifier);
		try {
			setState(885);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(882);
				strictIdentifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(883);
				if (!(!SQL_standard_keyword_behavior)) throw new FailedPredicateException(this, "!SQL_standard_keyword_behavior");
				setState(884);
				strictNonReserved();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StrictIdentifierContext extends ParserRuleContext {
		public StrictIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strictIdentifier; }
	 
		public StrictIdentifierContext() { }
		public void copyFrom(StrictIdentifierContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class QuotedIdentifierAlternativeContext extends StrictIdentifierContext {
		public QuotedIdentifierContext quotedIdentifier() {
			return getRuleContext(QuotedIdentifierContext.class,0);
		}
		public QuotedIdentifierAlternativeContext(StrictIdentifierContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterQuotedIdentifierAlternative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitQuotedIdentifierAlternative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitQuotedIdentifierAlternative(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnquotedIdentifierContext extends StrictIdentifierContext {
		public TerminalNode IDENTIFIER() { return getToken(SqlBaseParser.IDENTIFIER, 0); }
		public AnsiNonReservedContext ansiNonReserved() {
			return getRuleContext(AnsiNonReservedContext.class,0);
		}
		public NonReservedContext nonReserved() {
			return getRuleContext(NonReservedContext.class,0);
		}
		public UnquotedIdentifierContext(StrictIdentifierContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterUnquotedIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitUnquotedIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitUnquotedIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrictIdentifierContext strictIdentifier() throws RecognitionException {
		StrictIdentifierContext _localctx = new StrictIdentifierContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_strictIdentifier);
		try {
			setState(893);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				_localctx = new UnquotedIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(887);
				match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new QuotedIdentifierAlternativeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(888);
				quotedIdentifier();
				}
				break;
			case 3:
				_localctx = new UnquotedIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(889);
				if (!(SQL_standard_keyword_behavior)) throw new FailedPredicateException(this, "SQL_standard_keyword_behavior");
				setState(890);
				ansiNonReserved();
				}
				break;
			case 4:
				_localctx = new UnquotedIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(891);
				if (!(!SQL_standard_keyword_behavior)) throw new FailedPredicateException(this, "!SQL_standard_keyword_behavior");
				setState(892);
				nonReserved();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QuotedIdentifierContext extends ParserRuleContext {
		public TerminalNode BACKQUOTED_IDENTIFIER() { return getToken(SqlBaseParser.BACKQUOTED_IDENTIFIER, 0); }
		public QuotedIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quotedIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterQuotedIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitQuotedIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitQuotedIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QuotedIdentifierContext quotedIdentifier() throws RecognitionException {
		QuotedIdentifierContext _localctx = new QuotedIdentifierContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_quotedIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(895);
			match(BACKQUOTED_IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
	 
		public NumberContext() { }
		public void copyFrom(NumberContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DecimalLiteralContext extends NumberContext {
		public TerminalNode DECIMAL_VALUE() { return getToken(SqlBaseParser.DECIMAL_VALUE, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public DecimalLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterDecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitDecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitDecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BigIntLiteralContext extends NumberContext {
		public TerminalNode BIGINT_LITERAL() { return getToken(SqlBaseParser.BIGINT_LITERAL, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public BigIntLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterBigIntLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitBigIntLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitBigIntLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TinyIntLiteralContext extends NumberContext {
		public TerminalNode TINYINT_LITERAL() { return getToken(SqlBaseParser.TINYINT_LITERAL, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public TinyIntLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterTinyIntLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitTinyIntLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitTinyIntLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LegacyDecimalLiteralContext extends NumberContext {
		public TerminalNode EXPONENT_VALUE() { return getToken(SqlBaseParser.EXPONENT_VALUE, 0); }
		public TerminalNode DECIMAL_VALUE() { return getToken(SqlBaseParser.DECIMAL_VALUE, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public LegacyDecimalLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterLegacyDecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitLegacyDecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitLegacyDecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BigDecimalLiteralContext extends NumberContext {
		public TerminalNode BIGDECIMAL_LITERAL() { return getToken(SqlBaseParser.BIGDECIMAL_LITERAL, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public BigDecimalLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterBigDecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitBigDecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitBigDecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExponentLiteralContext extends NumberContext {
		public TerminalNode EXPONENT_VALUE() { return getToken(SqlBaseParser.EXPONENT_VALUE, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public ExponentLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterExponentLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitExponentLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitExponentLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DoubleLiteralContext extends NumberContext {
		public TerminalNode DOUBLE_LITERAL() { return getToken(SqlBaseParser.DOUBLE_LITERAL, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public DoubleLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterDoubleLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitDoubleLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitDoubleLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntegerLiteralContext extends NumberContext {
		public TerminalNode INTEGER_VALUE() { return getToken(SqlBaseParser.INTEGER_VALUE, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public IntegerLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterIntegerLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitIntegerLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitIntegerLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FloatLiteralContext extends NumberContext {
		public TerminalNode FLOAT_LITERAL() { return getToken(SqlBaseParser.FLOAT_LITERAL, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public FloatLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterFloatLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitFloatLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitFloatLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SmallIntLiteralContext extends NumberContext {
		public TerminalNode SMALLINT_LITERAL() { return getToken(SqlBaseParser.SMALLINT_LITERAL, 0); }
		public TerminalNode MINUS() { return getToken(SqlBaseParser.MINUS, 0); }
		public SmallIntLiteralContext(NumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterSmallIntLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitSmallIntLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitSmallIntLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_number);
		int _la;
		try {
			setState(940);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
			case 1:
				_localctx = new ExponentLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(897);
				if (!(!legacy_exponent_literal_as_decimal_enabled)) throw new FailedPredicateException(this, "!legacy_exponent_literal_as_decimal_enabled");
				setState(899);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(898);
					match(MINUS);
					}
				}

				setState(901);
				match(EXPONENT_VALUE);
				}
				break;
			case 2:
				_localctx = new DecimalLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(902);
				if (!(!legacy_exponent_literal_as_decimal_enabled)) throw new FailedPredicateException(this, "!legacy_exponent_literal_as_decimal_enabled");
				setState(904);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(903);
					match(MINUS);
					}
				}

				setState(906);
				match(DECIMAL_VALUE);
				}
				break;
			case 3:
				_localctx = new LegacyDecimalLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(907);
				if (!(legacy_exponent_literal_as_decimal_enabled)) throw new FailedPredicateException(this, "legacy_exponent_literal_as_decimal_enabled");
				setState(909);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(908);
					match(MINUS);
					}
				}

				setState(911);
				_la = _input.LA(1);
				if ( !(_la==EXPONENT_VALUE || _la==DECIMAL_VALUE) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 4:
				_localctx = new IntegerLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(913);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(912);
					match(MINUS);
					}
				}

				setState(915);
				match(INTEGER_VALUE);
				}
				break;
			case 5:
				_localctx = new BigIntLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(917);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(916);
					match(MINUS);
					}
				}

				setState(919);
				match(BIGINT_LITERAL);
				}
				break;
			case 6:
				_localctx = new SmallIntLiteralContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(921);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(920);
					match(MINUS);
					}
				}

				setState(923);
				match(SMALLINT_LITERAL);
				}
				break;
			case 7:
				_localctx = new TinyIntLiteralContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(925);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(924);
					match(MINUS);
					}
				}

				setState(927);
				match(TINYINT_LITERAL);
				}
				break;
			case 8:
				_localctx = new DoubleLiteralContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(929);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(928);
					match(MINUS);
					}
				}

				setState(931);
				match(DOUBLE_LITERAL);
				}
				break;
			case 9:
				_localctx = new FloatLiteralContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(933);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(932);
					match(MINUS);
					}
				}

				setState(935);
				match(FLOAT_LITERAL);
				}
				break;
			case 10:
				_localctx = new BigDecimalLiteralContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(937);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(936);
					match(MINUS);
					}
				}

				setState(939);
				match(BIGDECIMAL_LITERAL);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AnsiNonReservedContext extends ParserRuleContext {
		public TerminalNode ADD() { return getToken(SqlBaseParser.ADD, 0); }
		public TerminalNode AFTER() { return getToken(SqlBaseParser.AFTER, 0); }
		public TerminalNode ALTER() { return getToken(SqlBaseParser.ALTER, 0); }
		public TerminalNode ANALYZE() { return getToken(SqlBaseParser.ANALYZE, 0); }
		public TerminalNode ANTI() { return getToken(SqlBaseParser.ANTI, 0); }
		public TerminalNode ARCHIVE() { return getToken(SqlBaseParser.ARCHIVE, 0); }
		public TerminalNode ARRAY() { return getToken(SqlBaseParser.ARRAY, 0); }
		public TerminalNode ASC() { return getToken(SqlBaseParser.ASC, 0); }
		public TerminalNode AT() { return getToken(SqlBaseParser.AT, 0); }
		public TerminalNode BETWEEN() { return getToken(SqlBaseParser.BETWEEN, 0); }
		public TerminalNode BUCKET() { return getToken(SqlBaseParser.BUCKET, 0); }
		public TerminalNode BUCKETS() { return getToken(SqlBaseParser.BUCKETS, 0); }
		public TerminalNode BY() { return getToken(SqlBaseParser.BY, 0); }
		public TerminalNode CACHE() { return getToken(SqlBaseParser.CACHE, 0); }
		public TerminalNode CASCADE() { return getToken(SqlBaseParser.CASCADE, 0); }
		public TerminalNode CHANGE() { return getToken(SqlBaseParser.CHANGE, 0); }
		public TerminalNode CLEAR() { return getToken(SqlBaseParser.CLEAR, 0); }
		public TerminalNode CLUSTER() { return getToken(SqlBaseParser.CLUSTER, 0); }
		public TerminalNode CLUSTERED() { return getToken(SqlBaseParser.CLUSTERED, 0); }
		public TerminalNode CODEGEN() { return getToken(SqlBaseParser.CODEGEN, 0); }
		public TerminalNode COLLECTION() { return getToken(SqlBaseParser.COLLECTION, 0); }
		public TerminalNode COLUMNS() { return getToken(SqlBaseParser.COLUMNS, 0); }
		public TerminalNode COMMENT() { return getToken(SqlBaseParser.COMMENT, 0); }
		public TerminalNode COMMIT() { return getToken(SqlBaseParser.COMMIT, 0); }
		public TerminalNode COMPACT() { return getToken(SqlBaseParser.COMPACT, 0); }
		public TerminalNode COMPACTIONS() { return getToken(SqlBaseParser.COMPACTIONS, 0); }
		public TerminalNode COMPUTE() { return getToken(SqlBaseParser.COMPUTE, 0); }
		public TerminalNode CONCATENATE() { return getToken(SqlBaseParser.CONCATENATE, 0); }
		public TerminalNode COST() { return getToken(SqlBaseParser.COST, 0); }
		public TerminalNode CUBE() { return getToken(SqlBaseParser.CUBE, 0); }
		public TerminalNode CURRENT() { return getToken(SqlBaseParser.CURRENT, 0); }
		public TerminalNode DATA() { return getToken(SqlBaseParser.DATA, 0); }
		public TerminalNode DATABASE() { return getToken(SqlBaseParser.DATABASE, 0); }
		public TerminalNode DATABASES() { return getToken(SqlBaseParser.DATABASES, 0); }
		public TerminalNode DBPROPERTIES() { return getToken(SqlBaseParser.DBPROPERTIES, 0); }
		public TerminalNode DEFINED() { return getToken(SqlBaseParser.DEFINED, 0); }
		public TerminalNode DELETE() { return getToken(SqlBaseParser.DELETE, 0); }
		public TerminalNode DELIMITED() { return getToken(SqlBaseParser.DELIMITED, 0); }
		public TerminalNode DESC() { return getToken(SqlBaseParser.DESC, 0); }
		public TerminalNode DESCRIBE() { return getToken(SqlBaseParser.DESCRIBE, 0); }
		public TerminalNode DFS() { return getToken(SqlBaseParser.DFS, 0); }
		public TerminalNode DIRECTORIES() { return getToken(SqlBaseParser.DIRECTORIES, 0); }
		public TerminalNode DIRECTORY() { return getToken(SqlBaseParser.DIRECTORY, 0); }
		public TerminalNode DISTRIBUTE() { return getToken(SqlBaseParser.DISTRIBUTE, 0); }
		public TerminalNode DIV() { return getToken(SqlBaseParser.DIV, 0); }
		public TerminalNode DROP() { return getToken(SqlBaseParser.DROP, 0); }
		public TerminalNode ESCAPED() { return getToken(SqlBaseParser.ESCAPED, 0); }
		public TerminalNode EXCHANGE() { return getToken(SqlBaseParser.EXCHANGE, 0); }
		public TerminalNode EXISTS() { return getToken(SqlBaseParser.EXISTS, 0); }
		public TerminalNode EXPLAIN() { return getToken(SqlBaseParser.EXPLAIN, 0); }
		public TerminalNode EXPORT() { return getToken(SqlBaseParser.EXPORT, 0); }
		public TerminalNode EXTENDED() { return getToken(SqlBaseParser.EXTENDED, 0); }
		public TerminalNode EXTERNAL() { return getToken(SqlBaseParser.EXTERNAL, 0); }
		public TerminalNode EXTRACT() { return getToken(SqlBaseParser.EXTRACT, 0); }
		public TerminalNode FIELDS() { return getToken(SqlBaseParser.FIELDS, 0); }
		public TerminalNode FILEFORMAT() { return getToken(SqlBaseParser.FILEFORMAT, 0); }
		public TerminalNode FIRST() { return getToken(SqlBaseParser.FIRST, 0); }
		public TerminalNode FOLLOWING() { return getToken(SqlBaseParser.FOLLOWING, 0); }
		public TerminalNode FORMAT() { return getToken(SqlBaseParser.FORMAT, 0); }
		public TerminalNode FORMATTED() { return getToken(SqlBaseParser.FORMATTED, 0); }
		public TerminalNode FUNCTION() { return getToken(SqlBaseParser.FUNCTION, 0); }
		public TerminalNode FUNCTIONS() { return getToken(SqlBaseParser.FUNCTIONS, 0); }
		public TerminalNode GLOBAL() { return getToken(SqlBaseParser.GLOBAL, 0); }
		public TerminalNode GROUPING() { return getToken(SqlBaseParser.GROUPING, 0); }
		public TerminalNode IF() { return getToken(SqlBaseParser.IF, 0); }
		public TerminalNode IGNORE() { return getToken(SqlBaseParser.IGNORE, 0); }
		public TerminalNode IMPORT() { return getToken(SqlBaseParser.IMPORT, 0); }
		public TerminalNode INDEX() { return getToken(SqlBaseParser.INDEX, 0); }
		public TerminalNode INDEXES() { return getToken(SqlBaseParser.INDEXES, 0); }
		public TerminalNode INPATH() { return getToken(SqlBaseParser.INPATH, 0); }
		public TerminalNode INPUTFORMAT() { return getToken(SqlBaseParser.INPUTFORMAT, 0); }
		public TerminalNode INSERT() { return getToken(SqlBaseParser.INSERT, 0); }
		public TerminalNode INTERVAL() { return getToken(SqlBaseParser.INTERVAL, 0); }
		public TerminalNode ITEMS() { return getToken(SqlBaseParser.ITEMS, 0); }
		public TerminalNode KEYS() { return getToken(SqlBaseParser.KEYS, 0); }
		public TerminalNode LAST() { return getToken(SqlBaseParser.LAST, 0); }
		public TerminalNode LATERAL() { return getToken(SqlBaseParser.LATERAL, 0); }
		public TerminalNode LAZY() { return getToken(SqlBaseParser.LAZY, 0); }
		public TerminalNode LIKE() { return getToken(SqlBaseParser.LIKE, 0); }
		public TerminalNode LIMIT() { return getToken(SqlBaseParser.LIMIT, 0); }
		public TerminalNode LINES() { return getToken(SqlBaseParser.LINES, 0); }
		public TerminalNode LIST() { return getToken(SqlBaseParser.LIST, 0); }
		public TerminalNode LOAD() { return getToken(SqlBaseParser.LOAD, 0); }
		public TerminalNode LOCAL() { return getToken(SqlBaseParser.LOCAL, 0); }
		public TerminalNode LOCATION() { return getToken(SqlBaseParser.LOCATION, 0); }
		public TerminalNode LOCK() { return getToken(SqlBaseParser.LOCK, 0); }
		public TerminalNode LOCKS() { return getToken(SqlBaseParser.LOCKS, 0); }
		public TerminalNode LOGICAL() { return getToken(SqlBaseParser.LOGICAL, 0); }
		public TerminalNode MACRO() { return getToken(SqlBaseParser.MACRO, 0); }
		public TerminalNode MAP() { return getToken(SqlBaseParser.MAP, 0); }
		public TerminalNode MATCHED() { return getToken(SqlBaseParser.MATCHED, 0); }
		public TerminalNode MERGE() { return getToken(SqlBaseParser.MERGE, 0); }
		public TerminalNode MSCK() { return getToken(SqlBaseParser.MSCK, 0); }
		public TerminalNode NAMESPACE() { return getToken(SqlBaseParser.NAMESPACE, 0); }
		public TerminalNode NAMESPACES() { return getToken(SqlBaseParser.NAMESPACES, 0); }
		public TerminalNode NO() { return getToken(SqlBaseParser.NO, 0); }
		public TerminalNode NULLS() { return getToken(SqlBaseParser.NULLS, 0); }
		public TerminalNode OF() { return getToken(SqlBaseParser.OF, 0); }
		public TerminalNode OPTION() { return getToken(SqlBaseParser.OPTION, 0); }
		public TerminalNode OPTIONS() { return getToken(SqlBaseParser.OPTIONS, 0); }
		public TerminalNode OUT() { return getToken(SqlBaseParser.OUT, 0); }
		public TerminalNode OUTPUTFORMAT() { return getToken(SqlBaseParser.OUTPUTFORMAT, 0); }
		public TerminalNode OVER() { return getToken(SqlBaseParser.OVER, 0); }
		public TerminalNode OVERLAY() { return getToken(SqlBaseParser.OVERLAY, 0); }
		public TerminalNode OVERWRITE() { return getToken(SqlBaseParser.OVERWRITE, 0); }
		public TerminalNode PARTITION() { return getToken(SqlBaseParser.PARTITION, 0); }
		public TerminalNode PARTITIONED() { return getToken(SqlBaseParser.PARTITIONED, 0); }
		public TerminalNode PARTITIONS() { return getToken(SqlBaseParser.PARTITIONS, 0); }
		public TerminalNode PERCENTLIT() { return getToken(SqlBaseParser.PERCENTLIT, 0); }
		public TerminalNode PIVOT() { return getToken(SqlBaseParser.PIVOT, 0); }
		public TerminalNode PLACING() { return getToken(SqlBaseParser.PLACING, 0); }
		public TerminalNode POSITION() { return getToken(SqlBaseParser.POSITION, 0); }
		public TerminalNode PRECEDING() { return getToken(SqlBaseParser.PRECEDING, 0); }
		public TerminalNode PRINCIPALS() { return getToken(SqlBaseParser.PRINCIPALS, 0); }
		public TerminalNode PROPERTIES() { return getToken(SqlBaseParser.PROPERTIES, 0); }
		public TerminalNode PURGE() { return getToken(SqlBaseParser.PURGE, 0); }
		public TerminalNode QUERY() { return getToken(SqlBaseParser.QUERY, 0); }
		public TerminalNode RANGE() { return getToken(SqlBaseParser.RANGE, 0); }
		public TerminalNode RECORDREADER() { return getToken(SqlBaseParser.RECORDREADER, 0); }
		public TerminalNode RECORDWRITER() { return getToken(SqlBaseParser.RECORDWRITER, 0); }
		public TerminalNode RECOVER() { return getToken(SqlBaseParser.RECOVER, 0); }
		public TerminalNode REDUCE() { return getToken(SqlBaseParser.REDUCE, 0); }
		public TerminalNode REFRESH() { return getToken(SqlBaseParser.REFRESH, 0); }
		public TerminalNode RENAME() { return getToken(SqlBaseParser.RENAME, 0); }
		public TerminalNode REPAIR() { return getToken(SqlBaseParser.REPAIR, 0); }
		public TerminalNode REPLACE() { return getToken(SqlBaseParser.REPLACE, 0); }
		public TerminalNode RESET() { return getToken(SqlBaseParser.RESET, 0); }
		public TerminalNode RESTRICT() { return getToken(SqlBaseParser.RESTRICT, 0); }
		public TerminalNode REVOKE() { return getToken(SqlBaseParser.REVOKE, 0); }
		public TerminalNode RLIKE() { return getToken(SqlBaseParser.RLIKE, 0); }
		public TerminalNode ROLE() { return getToken(SqlBaseParser.ROLE, 0); }
		public TerminalNode ROLES() { return getToken(SqlBaseParser.ROLES, 0); }
		public TerminalNode ROLLBACK() { return getToken(SqlBaseParser.ROLLBACK, 0); }
		public TerminalNode ROLLUP() { return getToken(SqlBaseParser.ROLLUP, 0); }
		public TerminalNode ROW() { return getToken(SqlBaseParser.ROW, 0); }
		public TerminalNode ROWS() { return getToken(SqlBaseParser.ROWS, 0); }
		public TerminalNode SCHEMA() { return getToken(SqlBaseParser.SCHEMA, 0); }
		public TerminalNode SEMI() { return getToken(SqlBaseParser.SEMI, 0); }
		public TerminalNode SEPARATED() { return getToken(SqlBaseParser.SEPARATED, 0); }
		public TerminalNode SERDE() { return getToken(SqlBaseParser.SERDE, 0); }
		public TerminalNode SERDEPROPERTIES() { return getToken(SqlBaseParser.SERDEPROPERTIES, 0); }
		public TerminalNode SET() { return getToken(SqlBaseParser.SET, 0); }
		public TerminalNode SETMINUS() { return getToken(SqlBaseParser.SETMINUS, 0); }
		public TerminalNode SETS() { return getToken(SqlBaseParser.SETS, 0); }
		public TerminalNode SHOW() { return getToken(SqlBaseParser.SHOW, 0); }
		public TerminalNode SKEWED() { return getToken(SqlBaseParser.SKEWED, 0); }
		public TerminalNode SORT() { return getToken(SqlBaseParser.SORT, 0); }
		public TerminalNode SORTED() { return getToken(SqlBaseParser.SORTED, 0); }
		public TerminalNode START() { return getToken(SqlBaseParser.START, 0); }
		public TerminalNode STATISTICS() { return getToken(SqlBaseParser.STATISTICS, 0); }
		public TerminalNode STORED() { return getToken(SqlBaseParser.STORED, 0); }
		public TerminalNode STRATIFY() { return getToken(SqlBaseParser.STRATIFY, 0); }
		public TerminalNode STRUCT() { return getToken(SqlBaseParser.STRUCT, 0); }
		public TerminalNode SUBSTR() { return getToken(SqlBaseParser.SUBSTR, 0); }
		public TerminalNode SUBSTRING() { return getToken(SqlBaseParser.SUBSTRING, 0); }
		public TerminalNode TABLES() { return getToken(SqlBaseParser.TABLES, 0); }
		public TerminalNode TABLESAMPLE() { return getToken(SqlBaseParser.TABLESAMPLE, 0); }
		public TerminalNode TBLPROPERTIES() { return getToken(SqlBaseParser.TBLPROPERTIES, 0); }
		public TerminalNode TEMPORARY() { return getToken(SqlBaseParser.TEMPORARY, 0); }
		public TerminalNode TERMINATED() { return getToken(SqlBaseParser.TERMINATED, 0); }
		public TerminalNode TOUCH() { return getToken(SqlBaseParser.TOUCH, 0); }
		public TerminalNode TRANSACTION() { return getToken(SqlBaseParser.TRANSACTION, 0); }
		public TerminalNode TRANSACTIONS() { return getToken(SqlBaseParser.TRANSACTIONS, 0); }
		public TerminalNode TRANSFORM() { return getToken(SqlBaseParser.TRANSFORM, 0); }
		public TerminalNode TRIM() { return getToken(SqlBaseParser.TRIM, 0); }
		public TerminalNode TRUE() { return getToken(SqlBaseParser.TRUE, 0); }
		public TerminalNode TRUNCATE() { return getToken(SqlBaseParser.TRUNCATE, 0); }
		public TerminalNode TYPE() { return getToken(SqlBaseParser.TYPE, 0); }
		public TerminalNode UNARCHIVE() { return getToken(SqlBaseParser.UNARCHIVE, 0); }
		public TerminalNode UNBOUNDED() { return getToken(SqlBaseParser.UNBOUNDED, 0); }
		public TerminalNode UNCACHE() { return getToken(SqlBaseParser.UNCACHE, 0); }
		public TerminalNode UNLOCK() { return getToken(SqlBaseParser.UNLOCK, 0); }
		public TerminalNode UNSET() { return getToken(SqlBaseParser.UNSET, 0); }
		public TerminalNode UPDATE() { return getToken(SqlBaseParser.UPDATE, 0); }
		public TerminalNode USE() { return getToken(SqlBaseParser.USE, 0); }
		public TerminalNode VALUES() { return getToken(SqlBaseParser.VALUES, 0); }
		public TerminalNode VIEW() { return getToken(SqlBaseParser.VIEW, 0); }
		public TerminalNode VIEWS() { return getToken(SqlBaseParser.VIEWS, 0); }
		public TerminalNode WINDOW() { return getToken(SqlBaseParser.WINDOW, 0); }
		public TerminalNode ZONE() { return getToken(SqlBaseParser.ZONE, 0); }
		public AnsiNonReservedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ansiNonReserved; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterAnsiNonReserved(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitAnsiNonReserved(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitAnsiNonReserved(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnsiNonReservedContext ansiNonReserved() throws RecognitionException {
		AnsiNonReservedContext _localctx = new AnsiNonReservedContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_ansiNonReserved);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(942);
			_la = _input.LA(1);
			if ( !(((((_la - 10)) & ~0x3f) == 0 && ((1L << (_la - 10)) & ((1L << (ADD - 10)) | (1L << (AFTER - 10)) | (1L << (ALTER - 10)) | (1L << (ANALYZE - 10)) | (1L << (ANTI - 10)) | (1L << (ARCHIVE - 10)) | (1L << (ARRAY - 10)) | (1L << (ASC - 10)) | (1L << (AT - 10)) | (1L << (BETWEEN - 10)) | (1L << (BUCKET - 10)) | (1L << (BUCKETS - 10)) | (1L << (BY - 10)) | (1L << (CACHE - 10)) | (1L << (CASCADE - 10)) | (1L << (CHANGE - 10)) | (1L << (CLEAR - 10)) | (1L << (CLUSTER - 10)) | (1L << (CLUSTERED - 10)) | (1L << (CODEGEN - 10)) | (1L << (COLLECTION - 10)) | (1L << (COLUMNS - 10)) | (1L << (COMMENT - 10)) | (1L << (COMMIT - 10)) | (1L << (COMPACT - 10)) | (1L << (COMPACTIONS - 10)) | (1L << (COMPUTE - 10)) | (1L << (CONCATENATE - 10)) | (1L << (COST - 10)) | (1L << (CUBE - 10)) | (1L << (CURRENT - 10)) | (1L << (DATA - 10)) | (1L << (DATABASE - 10)) | (1L << (DATABASES - 10)) | (1L << (DBPROPERTIES - 10)) | (1L << (DEFINED - 10)) | (1L << (DELETE - 10)) | (1L << (DELIMITED - 10)) | (1L << (DESC - 10)) | (1L << (DESCRIBE - 10)) | (1L << (DFS - 10)) | (1L << (DIRECTORIES - 10)) | (1L << (DIRECTORY - 10)) | (1L << (DISTRIBUTE - 10)))) != 0) || ((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & ((1L << (DIV - 74)) | (1L << (DROP - 74)) | (1L << (ESCAPED - 74)) | (1L << (EXCHANGE - 74)) | (1L << (EXISTS - 74)) | (1L << (EXPLAIN - 74)) | (1L << (EXPORT - 74)) | (1L << (EXTENDED - 74)) | (1L << (EXTERNAL - 74)) | (1L << (EXTRACT - 74)) | (1L << (FIELDS - 74)) | (1L << (FILEFORMAT - 74)) | (1L << (FIRST - 74)) | (1L << (FOLLOWING - 74)) | (1L << (FORMAT - 74)) | (1L << (FORMATTED - 74)) | (1L << (FUNCTION - 74)) | (1L << (FUNCTIONS - 74)) | (1L << (GLOBAL - 74)) | (1L << (GROUPING - 74)) | (1L << (IF - 74)) | (1L << (IGNORE - 74)) | (1L << (IMPORT - 74)) | (1L << (INDEX - 74)) | (1L << (INDEXES - 74)) | (1L << (INPATH - 74)) | (1L << (INPUTFORMAT - 74)) | (1L << (INSERT - 74)) | (1L << (INTERVAL - 74)) | (1L << (ITEMS - 74)) | (1L << (KEYS - 74)) | (1L << (LAST - 74)) | (1L << (LATERAL - 74)) | (1L << (LAZY - 74)) | (1L << (LIKE - 74)) | (1L << (LIMIT - 74)) | (1L << (LINES - 74)) | (1L << (LIST - 74)) | (1L << (LOAD - 74)) | (1L << (LOCAL - 74)) | (1L << (LOCATION - 74)) | (1L << (LOCK - 74)))) != 0) || ((((_la - 138)) & ~0x3f) == 0 && ((1L << (_la - 138)) & ((1L << (LOCKS - 138)) | (1L << (LOGICAL - 138)) | (1L << (MACRO - 138)) | (1L << (MAP - 138)) | (1L << (MATCHED - 138)) | (1L << (MERGE - 138)) | (1L << (MSCK - 138)) | (1L << (NAMESPACE - 138)) | (1L << (NAMESPACES - 138)) | (1L << (NO - 138)) | (1L << (NULLS - 138)) | (1L << (OF - 138)) | (1L << (OPTION - 138)) | (1L << (OPTIONS - 138)) | (1L << (OUT - 138)) | (1L << (OUTPUTFORMAT - 138)) | (1L << (OVER - 138)) | (1L << (OVERLAY - 138)) | (1L << (OVERWRITE - 138)) | (1L << (PARTITION - 138)) | (1L << (PARTITIONED - 138)) | (1L << (PARTITIONS - 138)) | (1L << (PERCENTLIT - 138)) | (1L << (PIVOT - 138)) | (1L << (PLACING - 138)) | (1L << (POSITION - 138)) | (1L << (PRECEDING - 138)) | (1L << (PRINCIPALS - 138)) | (1L << (PROPERTIES - 138)) | (1L << (PURGE - 138)) | (1L << (QUERY - 138)) | (1L << (RANGE - 138)) | (1L << (RECORDREADER - 138)) | (1L << (RECORDWRITER - 138)) | (1L << (RECOVER - 138)) | (1L << (REDUCE - 138)) | (1L << (REFRESH - 138)) | (1L << (RENAME - 138)) | (1L << (REPAIR - 138)) | (1L << (REPLACE - 138)) | (1L << (RESET - 138)) | (1L << (RESTRICT - 138)) | (1L << (REVOKE - 138)) | (1L << (RLIKE - 138)) | (1L << (ROLE - 138)) | (1L << (ROLES - 138)) | (1L << (ROLLBACK - 138)) | (1L << (ROLLUP - 138)) | (1L << (ROW - 138)) | (1L << (ROWS - 138)) | (1L << (SCHEMA - 138)))) != 0) || ((((_la - 202)) & ~0x3f) == 0 && ((1L << (_la - 202)) & ((1L << (SEMI - 202)) | (1L << (SEPARATED - 202)) | (1L << (SERDE - 202)) | (1L << (SERDEPROPERTIES - 202)) | (1L << (SET - 202)) | (1L << (SETMINUS - 202)) | (1L << (SETS - 202)) | (1L << (SHOW - 202)) | (1L << (SKEWED - 202)) | (1L << (SORT - 202)) | (1L << (SORTED - 202)) | (1L << (START - 202)) | (1L << (STATISTICS - 202)) | (1L << (STORED - 202)) | (1L << (STRATIFY - 202)) | (1L << (STRUCT - 202)) | (1L << (SUBSTR - 202)) | (1L << (SUBSTRING - 202)) | (1L << (TABLES - 202)) | (1L << (TABLESAMPLE - 202)) | (1L << (TBLPROPERTIES - 202)) | (1L << (TEMPORARY - 202)) | (1L << (TERMINATED - 202)) | (1L << (TOUCH - 202)) | (1L << (TRANSACTION - 202)) | (1L << (TRANSACTIONS - 202)) | (1L << (TRANSFORM - 202)) | (1L << (TRIM - 202)) | (1L << (TRUE - 202)) | (1L << (TRUNCATE - 202)) | (1L << (TYPE - 202)) | (1L << (UNARCHIVE - 202)) | (1L << (UNBOUNDED - 202)) | (1L << (UNCACHE - 202)) | (1L << (UNLOCK - 202)) | (1L << (UNSET - 202)) | (1L << (UPDATE - 202)) | (1L << (USE - 202)) | (1L << (VALUES - 202)) | (1L << (VIEW - 202)) | (1L << (VIEWS - 202)) | (1L << (WINDOW - 202)) | (1L << (ZONE - 202)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StrictNonReservedContext extends ParserRuleContext {
		public TerminalNode ANTI() { return getToken(SqlBaseParser.ANTI, 0); }
		public TerminalNode CROSS() { return getToken(SqlBaseParser.CROSS, 0); }
		public TerminalNode EXCEPT() { return getToken(SqlBaseParser.EXCEPT, 0); }
		public TerminalNode FULL() { return getToken(SqlBaseParser.FULL, 0); }
		public TerminalNode INNER() { return getToken(SqlBaseParser.INNER, 0); }
		public TerminalNode INTERSECT() { return getToken(SqlBaseParser.INTERSECT, 0); }
		public TerminalNode JOIN() { return getToken(SqlBaseParser.JOIN, 0); }
		public TerminalNode LEFT() { return getToken(SqlBaseParser.LEFT, 0); }
		public TerminalNode NATURAL() { return getToken(SqlBaseParser.NATURAL, 0); }
		public TerminalNode ON() { return getToken(SqlBaseParser.ON, 0); }
		public TerminalNode RIGHT() { return getToken(SqlBaseParser.RIGHT, 0); }
		public TerminalNode SEMI() { return getToken(SqlBaseParser.SEMI, 0); }
		public TerminalNode SETMINUS() { return getToken(SqlBaseParser.SETMINUS, 0); }
		public TerminalNode UNION() { return getToken(SqlBaseParser.UNION, 0); }
		public TerminalNode USING() { return getToken(SqlBaseParser.USING, 0); }
		public StrictNonReservedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strictNonReserved; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterStrictNonReserved(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitStrictNonReserved(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitStrictNonReserved(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrictNonReservedContext strictNonReserved() throws RecognitionException {
		StrictNonReservedContext _localctx = new StrictNonReservedContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_strictNonReserved);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(944);
			_la = _input.LA(1);
			if ( !(_la==ANTI || _la==CROSS || ((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & ((1L << (EXCEPT - 80)) | (1L << (FULL - 80)) | (1L << (INNER - 80)) | (1L << (INTERSECT - 80)) | (1L << (JOIN - 80)) | (1L << (LEFT - 80)))) != 0) || ((((_la - 147)) & ~0x3f) == 0 && ((1L << (_la - 147)) & ((1L << (NATURAL - 147)) | (1L << (ON - 147)) | (1L << (RIGHT - 147)) | (1L << (SEMI - 147)) | (1L << (SETMINUS - 147)))) != 0) || _la==UNION || _la==USING) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NonReservedContext extends ParserRuleContext {
		public TerminalNode ADD() { return getToken(SqlBaseParser.ADD, 0); }
		public TerminalNode AFTER() { return getToken(SqlBaseParser.AFTER, 0); }
		public TerminalNode ALL() { return getToken(SqlBaseParser.ALL, 0); }
		public TerminalNode ALTER() { return getToken(SqlBaseParser.ALTER, 0); }
		public TerminalNode ANALYZE() { return getToken(SqlBaseParser.ANALYZE, 0); }
		public TerminalNode AND() { return getToken(SqlBaseParser.AND, 0); }
		public TerminalNode ANY() { return getToken(SqlBaseParser.ANY, 0); }
		public TerminalNode ARCHIVE() { return getToken(SqlBaseParser.ARCHIVE, 0); }
		public TerminalNode ARRAY() { return getToken(SqlBaseParser.ARRAY, 0); }
		public TerminalNode AS() { return getToken(SqlBaseParser.AS, 0); }
		public TerminalNode ASC() { return getToken(SqlBaseParser.ASC, 0); }
		public TerminalNode AT() { return getToken(SqlBaseParser.AT, 0); }
		public TerminalNode AUTHORIZATION() { return getToken(SqlBaseParser.AUTHORIZATION, 0); }
		public TerminalNode BETWEEN() { return getToken(SqlBaseParser.BETWEEN, 0); }
		public TerminalNode BOTH() { return getToken(SqlBaseParser.BOTH, 0); }
		public TerminalNode BUCKET() { return getToken(SqlBaseParser.BUCKET, 0); }
		public TerminalNode BUCKETS() { return getToken(SqlBaseParser.BUCKETS, 0); }
		public TerminalNode BY() { return getToken(SqlBaseParser.BY, 0); }
		public TerminalNode CACHE() { return getToken(SqlBaseParser.CACHE, 0); }
		public TerminalNode CASCADE() { return getToken(SqlBaseParser.CASCADE, 0); }
		public TerminalNode CASE() { return getToken(SqlBaseParser.CASE, 0); }
		public TerminalNode CAST() { return getToken(SqlBaseParser.CAST, 0); }
		public TerminalNode CHANGE() { return getToken(SqlBaseParser.CHANGE, 0); }
		public TerminalNode CHECK() { return getToken(SqlBaseParser.CHECK, 0); }
		public TerminalNode CLEAR() { return getToken(SqlBaseParser.CLEAR, 0); }
		public TerminalNode CLUSTER() { return getToken(SqlBaseParser.CLUSTER, 0); }
		public TerminalNode CLUSTERED() { return getToken(SqlBaseParser.CLUSTERED, 0); }
		public TerminalNode CODEGEN() { return getToken(SqlBaseParser.CODEGEN, 0); }
		public TerminalNode COLLATE() { return getToken(SqlBaseParser.COLLATE, 0); }
		public TerminalNode COLLECTION() { return getToken(SqlBaseParser.COLLECTION, 0); }
		public TerminalNode COLUMN() { return getToken(SqlBaseParser.COLUMN, 0); }
		public TerminalNode COLUMNS() { return getToken(SqlBaseParser.COLUMNS, 0); }
		public TerminalNode COMMENT() { return getToken(SqlBaseParser.COMMENT, 0); }
		public TerminalNode COMMIT() { return getToken(SqlBaseParser.COMMIT, 0); }
		public TerminalNode COMPACT() { return getToken(SqlBaseParser.COMPACT, 0); }
		public TerminalNode COMPACTIONS() { return getToken(SqlBaseParser.COMPACTIONS, 0); }
		public TerminalNode COMPUTE() { return getToken(SqlBaseParser.COMPUTE, 0); }
		public TerminalNode CONCATENATE() { return getToken(SqlBaseParser.CONCATENATE, 0); }
		public TerminalNode CONSTRAINT() { return getToken(SqlBaseParser.CONSTRAINT, 0); }
		public TerminalNode COST() { return getToken(SqlBaseParser.COST, 0); }
		public TerminalNode CREATE() { return getToken(SqlBaseParser.CREATE, 0); }
		public TerminalNode CUBE() { return getToken(SqlBaseParser.CUBE, 0); }
		public TerminalNode CURRENT() { return getToken(SqlBaseParser.CURRENT, 0); }
		public TerminalNode CURRENT_DATE() { return getToken(SqlBaseParser.CURRENT_DATE, 0); }
		public TerminalNode CURRENT_TIME() { return getToken(SqlBaseParser.CURRENT_TIME, 0); }
		public TerminalNode CURRENT_TIMESTAMP() { return getToken(SqlBaseParser.CURRENT_TIMESTAMP, 0); }
		public TerminalNode CURRENT_USER() { return getToken(SqlBaseParser.CURRENT_USER, 0); }
		public TerminalNode DATA() { return getToken(SqlBaseParser.DATA, 0); }
		public TerminalNode DATABASE() { return getToken(SqlBaseParser.DATABASE, 0); }
		public TerminalNode DATABASES() { return getToken(SqlBaseParser.DATABASES, 0); }
		public TerminalNode DBPROPERTIES() { return getToken(SqlBaseParser.DBPROPERTIES, 0); }
		public TerminalNode DEFINED() { return getToken(SqlBaseParser.DEFINED, 0); }
		public TerminalNode DELETE() { return getToken(SqlBaseParser.DELETE, 0); }
		public TerminalNode DELIMITED() { return getToken(SqlBaseParser.DELIMITED, 0); }
		public TerminalNode DESC() { return getToken(SqlBaseParser.DESC, 0); }
		public TerminalNode DESCRIBE() { return getToken(SqlBaseParser.DESCRIBE, 0); }
		public TerminalNode DFS() { return getToken(SqlBaseParser.DFS, 0); }
		public TerminalNode DIRECTORIES() { return getToken(SqlBaseParser.DIRECTORIES, 0); }
		public TerminalNode DIRECTORY() { return getToken(SqlBaseParser.DIRECTORY, 0); }
		public TerminalNode DISTINCT() { return getToken(SqlBaseParser.DISTINCT, 0); }
		public TerminalNode DISTRIBUTE() { return getToken(SqlBaseParser.DISTRIBUTE, 0); }
		public TerminalNode DIV() { return getToken(SqlBaseParser.DIV, 0); }
		public TerminalNode DROP() { return getToken(SqlBaseParser.DROP, 0); }
		public TerminalNode ELSE() { return getToken(SqlBaseParser.ELSE, 0); }
		public TerminalNode END() { return getToken(SqlBaseParser.END, 0); }
		public TerminalNode ESCAPE() { return getToken(SqlBaseParser.ESCAPE, 0); }
		public TerminalNode ESCAPED() { return getToken(SqlBaseParser.ESCAPED, 0); }
		public TerminalNode EXCHANGE() { return getToken(SqlBaseParser.EXCHANGE, 0); }
		public TerminalNode EXISTS() { return getToken(SqlBaseParser.EXISTS, 0); }
		public TerminalNode EXPLAIN() { return getToken(SqlBaseParser.EXPLAIN, 0); }
		public TerminalNode EXPORT() { return getToken(SqlBaseParser.EXPORT, 0); }
		public TerminalNode EXTENDED() { return getToken(SqlBaseParser.EXTENDED, 0); }
		public TerminalNode EXTERNAL() { return getToken(SqlBaseParser.EXTERNAL, 0); }
		public TerminalNode EXTRACT() { return getToken(SqlBaseParser.EXTRACT, 0); }
		public TerminalNode FALSE() { return getToken(SqlBaseParser.FALSE, 0); }
		public TerminalNode FETCH() { return getToken(SqlBaseParser.FETCH, 0); }
		public TerminalNode FILTER() { return getToken(SqlBaseParser.FILTER, 0); }
		public TerminalNode FIELDS() { return getToken(SqlBaseParser.FIELDS, 0); }
		public TerminalNode FILEFORMAT() { return getToken(SqlBaseParser.FILEFORMAT, 0); }
		public TerminalNode FIRST() { return getToken(SqlBaseParser.FIRST, 0); }
		public TerminalNode FOLLOWING() { return getToken(SqlBaseParser.FOLLOWING, 0); }
		public TerminalNode FOR() { return getToken(SqlBaseParser.FOR, 0); }
		public TerminalNode FOREIGN() { return getToken(SqlBaseParser.FOREIGN, 0); }
		public TerminalNode FORMAT() { return getToken(SqlBaseParser.FORMAT, 0); }
		public TerminalNode FORMATTED() { return getToken(SqlBaseParser.FORMATTED, 0); }
		public TerminalNode FROM() { return getToken(SqlBaseParser.FROM, 0); }
		public TerminalNode FUNCTION() { return getToken(SqlBaseParser.FUNCTION, 0); }
		public TerminalNode FUNCTIONS() { return getToken(SqlBaseParser.FUNCTIONS, 0); }
		public TerminalNode GLOBAL() { return getToken(SqlBaseParser.GLOBAL, 0); }
		public TerminalNode GRANT() { return getToken(SqlBaseParser.GRANT, 0); }
		public TerminalNode GROUP() { return getToken(SqlBaseParser.GROUP, 0); }
		public TerminalNode GROUPING() { return getToken(SqlBaseParser.GROUPING, 0); }
		public TerminalNode HAVING() { return getToken(SqlBaseParser.HAVING, 0); }
		public TerminalNode IF() { return getToken(SqlBaseParser.IF, 0); }
		public TerminalNode IGNORE() { return getToken(SqlBaseParser.IGNORE, 0); }
		public TerminalNode IMPORT() { return getToken(SqlBaseParser.IMPORT, 0); }
		public TerminalNode IN() { return getToken(SqlBaseParser.IN, 0); }
		public TerminalNode INDEX() { return getToken(SqlBaseParser.INDEX, 0); }
		public TerminalNode INDEXES() { return getToken(SqlBaseParser.INDEXES, 0); }
		public TerminalNode INPATH() { return getToken(SqlBaseParser.INPATH, 0); }
		public TerminalNode INPUTFORMAT() { return getToken(SqlBaseParser.INPUTFORMAT, 0); }
		public TerminalNode INSERT() { return getToken(SqlBaseParser.INSERT, 0); }
		public TerminalNode INTERVAL() { return getToken(SqlBaseParser.INTERVAL, 0); }
		public TerminalNode INTO() { return getToken(SqlBaseParser.INTO, 0); }
		public TerminalNode IS() { return getToken(SqlBaseParser.IS, 0); }
		public TerminalNode ITEMS() { return getToken(SqlBaseParser.ITEMS, 0); }
		public TerminalNode KEYS() { return getToken(SqlBaseParser.KEYS, 0); }
		public TerminalNode LAST() { return getToken(SqlBaseParser.LAST, 0); }
		public TerminalNode LATERAL() { return getToken(SqlBaseParser.LATERAL, 0); }
		public TerminalNode LAZY() { return getToken(SqlBaseParser.LAZY, 0); }
		public TerminalNode LEADING() { return getToken(SqlBaseParser.LEADING, 0); }
		public TerminalNode LIKE() { return getToken(SqlBaseParser.LIKE, 0); }
		public TerminalNode LIMIT() { return getToken(SqlBaseParser.LIMIT, 0); }
		public TerminalNode LINES() { return getToken(SqlBaseParser.LINES, 0); }
		public TerminalNode LIST() { return getToken(SqlBaseParser.LIST, 0); }
		public TerminalNode LOAD() { return getToken(SqlBaseParser.LOAD, 0); }
		public TerminalNode LOCAL() { return getToken(SqlBaseParser.LOCAL, 0); }
		public TerminalNode LOCATION() { return getToken(SqlBaseParser.LOCATION, 0); }
		public TerminalNode LOCK() { return getToken(SqlBaseParser.LOCK, 0); }
		public TerminalNode LOCKS() { return getToken(SqlBaseParser.LOCKS, 0); }
		public TerminalNode LOGICAL() { return getToken(SqlBaseParser.LOGICAL, 0); }
		public TerminalNode MACRO() { return getToken(SqlBaseParser.MACRO, 0); }
		public TerminalNode MAP() { return getToken(SqlBaseParser.MAP, 0); }
		public TerminalNode MATCHED() { return getToken(SqlBaseParser.MATCHED, 0); }
		public TerminalNode MERGE() { return getToken(SqlBaseParser.MERGE, 0); }
		public TerminalNode MSCK() { return getToken(SqlBaseParser.MSCK, 0); }
		public TerminalNode NAMESPACE() { return getToken(SqlBaseParser.NAMESPACE, 0); }
		public TerminalNode NAMESPACES() { return getToken(SqlBaseParser.NAMESPACES, 0); }
		public TerminalNode NO() { return getToken(SqlBaseParser.NO, 0); }
		public TerminalNode NOT() { return getToken(SqlBaseParser.NOT, 0); }
		public TerminalNode NULL() { return getToken(SqlBaseParser.NULL, 0); }
		public TerminalNode NULLS() { return getToken(SqlBaseParser.NULLS, 0); }
		public TerminalNode OF() { return getToken(SqlBaseParser.OF, 0); }
		public TerminalNode ONLY() { return getToken(SqlBaseParser.ONLY, 0); }
		public TerminalNode OPTION() { return getToken(SqlBaseParser.OPTION, 0); }
		public TerminalNode OPTIONS() { return getToken(SqlBaseParser.OPTIONS, 0); }
		public TerminalNode OR() { return getToken(SqlBaseParser.OR, 0); }
		public TerminalNode ORDER() { return getToken(SqlBaseParser.ORDER, 0); }
		public TerminalNode OUT() { return getToken(SqlBaseParser.OUT, 0); }
		public TerminalNode OUTER() { return getToken(SqlBaseParser.OUTER, 0); }
		public TerminalNode OUTPUTFORMAT() { return getToken(SqlBaseParser.OUTPUTFORMAT, 0); }
		public TerminalNode OVER() { return getToken(SqlBaseParser.OVER, 0); }
		public TerminalNode OVERLAPS() { return getToken(SqlBaseParser.OVERLAPS, 0); }
		public TerminalNode OVERLAY() { return getToken(SqlBaseParser.OVERLAY, 0); }
		public TerminalNode OVERWRITE() { return getToken(SqlBaseParser.OVERWRITE, 0); }
		public TerminalNode PARTITION() { return getToken(SqlBaseParser.PARTITION, 0); }
		public TerminalNode PARTITIONED() { return getToken(SqlBaseParser.PARTITIONED, 0); }
		public TerminalNode PARTITIONS() { return getToken(SqlBaseParser.PARTITIONS, 0); }
		public TerminalNode PERCENTLIT() { return getToken(SqlBaseParser.PERCENTLIT, 0); }
		public TerminalNode PIVOT() { return getToken(SqlBaseParser.PIVOT, 0); }
		public TerminalNode PLACING() { return getToken(SqlBaseParser.PLACING, 0); }
		public TerminalNode POSITION() { return getToken(SqlBaseParser.POSITION, 0); }
		public TerminalNode PRECEDING() { return getToken(SqlBaseParser.PRECEDING, 0); }
		public TerminalNode PRIMARY() { return getToken(SqlBaseParser.PRIMARY, 0); }
		public TerminalNode PRINCIPALS() { return getToken(SqlBaseParser.PRINCIPALS, 0); }
		public TerminalNode PROPERTIES() { return getToken(SqlBaseParser.PROPERTIES, 0); }
		public TerminalNode PURGE() { return getToken(SqlBaseParser.PURGE, 0); }
		public TerminalNode QUERY() { return getToken(SqlBaseParser.QUERY, 0); }
		public TerminalNode RANGE() { return getToken(SqlBaseParser.RANGE, 0); }
		public TerminalNode RECORDREADER() { return getToken(SqlBaseParser.RECORDREADER, 0); }
		public TerminalNode RECORDWRITER() { return getToken(SqlBaseParser.RECORDWRITER, 0); }
		public TerminalNode RECOVER() { return getToken(SqlBaseParser.RECOVER, 0); }
		public TerminalNode REDUCE() { return getToken(SqlBaseParser.REDUCE, 0); }
		public TerminalNode REFERENCES() { return getToken(SqlBaseParser.REFERENCES, 0); }
		public TerminalNode REFRESH() { return getToken(SqlBaseParser.REFRESH, 0); }
		public TerminalNode RENAME() { return getToken(SqlBaseParser.RENAME, 0); }
		public TerminalNode REPAIR() { return getToken(SqlBaseParser.REPAIR, 0); }
		public TerminalNode REPLACE() { return getToken(SqlBaseParser.REPLACE, 0); }
		public TerminalNode RESET() { return getToken(SqlBaseParser.RESET, 0); }
		public TerminalNode RESTRICT() { return getToken(SqlBaseParser.RESTRICT, 0); }
		public TerminalNode REVOKE() { return getToken(SqlBaseParser.REVOKE, 0); }
		public TerminalNode RLIKE() { return getToken(SqlBaseParser.RLIKE, 0); }
		public TerminalNode ROLE() { return getToken(SqlBaseParser.ROLE, 0); }
		public TerminalNode ROLES() { return getToken(SqlBaseParser.ROLES, 0); }
		public TerminalNode ROLLBACK() { return getToken(SqlBaseParser.ROLLBACK, 0); }
		public TerminalNode ROLLUP() { return getToken(SqlBaseParser.ROLLUP, 0); }
		public TerminalNode ROW() { return getToken(SqlBaseParser.ROW, 0); }
		public TerminalNode ROWS() { return getToken(SqlBaseParser.ROWS, 0); }
		public TerminalNode SCHEMA() { return getToken(SqlBaseParser.SCHEMA, 0); }
		public TerminalNode SELECT() { return getToken(SqlBaseParser.SELECT, 0); }
		public TerminalNode SEPARATED() { return getToken(SqlBaseParser.SEPARATED, 0); }
		public TerminalNode SERDE() { return getToken(SqlBaseParser.SERDE, 0); }
		public TerminalNode SERDEPROPERTIES() { return getToken(SqlBaseParser.SERDEPROPERTIES, 0); }
		public TerminalNode SESSION_USER() { return getToken(SqlBaseParser.SESSION_USER, 0); }
		public TerminalNode SET() { return getToken(SqlBaseParser.SET, 0); }
		public TerminalNode SETS() { return getToken(SqlBaseParser.SETS, 0); }
		public TerminalNode SHOW() { return getToken(SqlBaseParser.SHOW, 0); }
		public TerminalNode SKEWED() { return getToken(SqlBaseParser.SKEWED, 0); }
		public TerminalNode SOME() { return getToken(SqlBaseParser.SOME, 0); }
		public TerminalNode SORT() { return getToken(SqlBaseParser.SORT, 0); }
		public TerminalNode SORTED() { return getToken(SqlBaseParser.SORTED, 0); }
		public TerminalNode START() { return getToken(SqlBaseParser.START, 0); }
		public TerminalNode STATISTICS() { return getToken(SqlBaseParser.STATISTICS, 0); }
		public TerminalNode STORED() { return getToken(SqlBaseParser.STORED, 0); }
		public TerminalNode STRATIFY() { return getToken(SqlBaseParser.STRATIFY, 0); }
		public TerminalNode STRUCT() { return getToken(SqlBaseParser.STRUCT, 0); }
		public TerminalNode SUBSTR() { return getToken(SqlBaseParser.SUBSTR, 0); }
		public TerminalNode SUBSTRING() { return getToken(SqlBaseParser.SUBSTRING, 0); }
		public TerminalNode TABLE() { return getToken(SqlBaseParser.TABLE, 0); }
		public TerminalNode TABLES() { return getToken(SqlBaseParser.TABLES, 0); }
		public TerminalNode TABLESAMPLE() { return getToken(SqlBaseParser.TABLESAMPLE, 0); }
		public TerminalNode TBLPROPERTIES() { return getToken(SqlBaseParser.TBLPROPERTIES, 0); }
		public TerminalNode TEMPORARY() { return getToken(SqlBaseParser.TEMPORARY, 0); }
		public TerminalNode TERMINATED() { return getToken(SqlBaseParser.TERMINATED, 0); }
		public TerminalNode THEN() { return getToken(SqlBaseParser.THEN, 0); }
		public TerminalNode TIME() { return getToken(SqlBaseParser.TIME, 0); }
		public TerminalNode TO() { return getToken(SqlBaseParser.TO, 0); }
		public TerminalNode TOUCH() { return getToken(SqlBaseParser.TOUCH, 0); }
		public TerminalNode TRAILING() { return getToken(SqlBaseParser.TRAILING, 0); }
		public TerminalNode TRANSACTION() { return getToken(SqlBaseParser.TRANSACTION, 0); }
		public TerminalNode TRANSACTIONS() { return getToken(SqlBaseParser.TRANSACTIONS, 0); }
		public TerminalNode TRANSFORM() { return getToken(SqlBaseParser.TRANSFORM, 0); }
		public TerminalNode TRIM() { return getToken(SqlBaseParser.TRIM, 0); }
		public TerminalNode TRUE() { return getToken(SqlBaseParser.TRUE, 0); }
		public TerminalNode TRUNCATE() { return getToken(SqlBaseParser.TRUNCATE, 0); }
		public TerminalNode TYPE() { return getToken(SqlBaseParser.TYPE, 0); }
		public TerminalNode UNARCHIVE() { return getToken(SqlBaseParser.UNARCHIVE, 0); }
		public TerminalNode UNBOUNDED() { return getToken(SqlBaseParser.UNBOUNDED, 0); }
		public TerminalNode UNCACHE() { return getToken(SqlBaseParser.UNCACHE, 0); }
		public TerminalNode UNIQUE() { return getToken(SqlBaseParser.UNIQUE, 0); }
		public TerminalNode UNKNOWN() { return getToken(SqlBaseParser.UNKNOWN, 0); }
		public TerminalNode UNLOCK() { return getToken(SqlBaseParser.UNLOCK, 0); }
		public TerminalNode UNSET() { return getToken(SqlBaseParser.UNSET, 0); }
		public TerminalNode UPDATE() { return getToken(SqlBaseParser.UPDATE, 0); }
		public TerminalNode USE() { return getToken(SqlBaseParser.USE, 0); }
		public TerminalNode USER() { return getToken(SqlBaseParser.USER, 0); }
		public TerminalNode VALUES() { return getToken(SqlBaseParser.VALUES, 0); }
		public TerminalNode VIEW() { return getToken(SqlBaseParser.VIEW, 0); }
		public TerminalNode VIEWS() { return getToken(SqlBaseParser.VIEWS, 0); }
		public TerminalNode WHEN() { return getToken(SqlBaseParser.WHEN, 0); }
		public TerminalNode WHERE() { return getToken(SqlBaseParser.WHERE, 0); }
		public TerminalNode WINDOW() { return getToken(SqlBaseParser.WINDOW, 0); }
		public TerminalNode WITH() { return getToken(SqlBaseParser.WITH, 0); }
		public TerminalNode ZONE() { return getToken(SqlBaseParser.ZONE, 0); }
		public NonReservedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonReserved; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).enterNonReserved(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SqlBaseListener ) ((SqlBaseListener)listener).exitNonReserved(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SqlBaseVisitor ) return ((SqlBaseVisitor<? extends T>)visitor).visitNonReserved(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NonReservedContext nonReserved() throws RecognitionException {
		NonReservedContext _localctx = new NonReservedContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_nonReserved);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(946);
			_la = _input.LA(1);
			if ( !(((((_la - 10)) & ~0x3f) == 0 && ((1L << (_la - 10)) & ((1L << (ADD - 10)) | (1L << (AFTER - 10)) | (1L << (ALL - 10)) | (1L << (ALTER - 10)) | (1L << (ANALYZE - 10)) | (1L << (AND - 10)) | (1L << (ANY - 10)) | (1L << (ARCHIVE - 10)) | (1L << (ARRAY - 10)) | (1L << (AS - 10)) | (1L << (ASC - 10)) | (1L << (AT - 10)) | (1L << (AUTHORIZATION - 10)) | (1L << (BETWEEN - 10)) | (1L << (BOTH - 10)) | (1L << (BUCKET - 10)) | (1L << (BUCKETS - 10)) | (1L << (BY - 10)) | (1L << (CACHE - 10)) | (1L << (CASCADE - 10)) | (1L << (CASE - 10)) | (1L << (CAST - 10)) | (1L << (CHANGE - 10)) | (1L << (CHECK - 10)) | (1L << (CLEAR - 10)) | (1L << (CLUSTER - 10)) | (1L << (CLUSTERED - 10)) | (1L << (CODEGEN - 10)) | (1L << (COLLATE - 10)) | (1L << (COLLECTION - 10)) | (1L << (COLUMN - 10)) | (1L << (COLUMNS - 10)) | (1L << (COMMENT - 10)) | (1L << (COMMIT - 10)) | (1L << (COMPACT - 10)) | (1L << (COMPACTIONS - 10)) | (1L << (COMPUTE - 10)) | (1L << (CONCATENATE - 10)) | (1L << (CONSTRAINT - 10)) | (1L << (COST - 10)) | (1L << (CREATE - 10)) | (1L << (CUBE - 10)) | (1L << (CURRENT - 10)) | (1L << (CURRENT_DATE - 10)) | (1L << (CURRENT_TIME - 10)) | (1L << (CURRENT_TIMESTAMP - 10)) | (1L << (CURRENT_USER - 10)) | (1L << (DATA - 10)) | (1L << (DATABASE - 10)) | (1L << (DATABASES - 10)) | (1L << (DBPROPERTIES - 10)) | (1L << (DEFINED - 10)) | (1L << (DELETE - 10)) | (1L << (DELIMITED - 10)) | (1L << (DESC - 10)) | (1L << (DESCRIBE - 10)) | (1L << (DFS - 10)) | (1L << (DIRECTORIES - 10)) | (1L << (DIRECTORY - 10)) | (1L << (DISTINCT - 10)) | (1L << (DISTRIBUTE - 10)))) != 0) || ((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & ((1L << (DIV - 74)) | (1L << (DROP - 74)) | (1L << (ELSE - 74)) | (1L << (END - 74)) | (1L << (ESCAPE - 74)) | (1L << (ESCAPED - 74)) | (1L << (EXCHANGE - 74)) | (1L << (EXISTS - 74)) | (1L << (EXPLAIN - 74)) | (1L << (EXPORT - 74)) | (1L << (EXTENDED - 74)) | (1L << (EXTERNAL - 74)) | (1L << (EXTRACT - 74)) | (1L << (FALSE - 74)) | (1L << (FETCH - 74)) | (1L << (FIELDS - 74)) | (1L << (FILTER - 74)) | (1L << (FILEFORMAT - 74)) | (1L << (FIRST - 74)) | (1L << (FOLLOWING - 74)) | (1L << (FOR - 74)) | (1L << (FOREIGN - 74)) | (1L << (FORMAT - 74)) | (1L << (FORMATTED - 74)) | (1L << (FROM - 74)) | (1L << (FUNCTION - 74)) | (1L << (FUNCTIONS - 74)) | (1L << (GLOBAL - 74)) | (1L << (GRANT - 74)) | (1L << (GROUP - 74)) | (1L << (GROUPING - 74)) | (1L << (HAVING - 74)) | (1L << (IF - 74)) | (1L << (IGNORE - 74)) | (1L << (IMPORT - 74)) | (1L << (IN - 74)) | (1L << (INDEX - 74)) | (1L << (INDEXES - 74)) | (1L << (INPATH - 74)) | (1L << (INPUTFORMAT - 74)) | (1L << (INSERT - 74)) | (1L << (INTERVAL - 74)) | (1L << (INTO - 74)) | (1L << (IS - 74)) | (1L << (ITEMS - 74)) | (1L << (KEYS - 74)) | (1L << (LAST - 74)) | (1L << (LATERAL - 74)) | (1L << (LAZY - 74)) | (1L << (LEADING - 74)) | (1L << (LIKE - 74)) | (1L << (LIMIT - 74)) | (1L << (LINES - 74)) | (1L << (LIST - 74)) | (1L << (LOAD - 74)) | (1L << (LOCAL - 74)) | (1L << (LOCATION - 74)) | (1L << (LOCK - 74)))) != 0) || ((((_la - 138)) & ~0x3f) == 0 && ((1L << (_la - 138)) & ((1L << (LOCKS - 138)) | (1L << (LOGICAL - 138)) | (1L << (MACRO - 138)) | (1L << (MAP - 138)) | (1L << (MATCHED - 138)) | (1L << (MERGE - 138)) | (1L << (MSCK - 138)) | (1L << (NAMESPACE - 138)) | (1L << (NAMESPACES - 138)) | (1L << (NO - 138)) | (1L << (NOT - 138)) | (1L << (NULL - 138)) | (1L << (NULLS - 138)) | (1L << (OF - 138)) | (1L << (ONLY - 138)) | (1L << (OPTION - 138)) | (1L << (OPTIONS - 138)) | (1L << (OR - 138)) | (1L << (ORDER - 138)) | (1L << (OUT - 138)) | (1L << (OUTER - 138)) | (1L << (OUTPUTFORMAT - 138)) | (1L << (OVER - 138)) | (1L << (OVERLAPS - 138)) | (1L << (OVERLAY - 138)) | (1L << (OVERWRITE - 138)) | (1L << (PARTITION - 138)) | (1L << (PARTITIONED - 138)) | (1L << (PARTITIONS - 138)) | (1L << (PERCENTLIT - 138)) | (1L << (PIVOT - 138)) | (1L << (PLACING - 138)) | (1L << (POSITION - 138)) | (1L << (PRECEDING - 138)) | (1L << (PRIMARY - 138)) | (1L << (PRINCIPALS - 138)) | (1L << (PROPERTIES - 138)) | (1L << (PURGE - 138)) | (1L << (QUERY - 138)) | (1L << (RANGE - 138)) | (1L << (RECORDREADER - 138)) | (1L << (RECORDWRITER - 138)) | (1L << (RECOVER - 138)) | (1L << (REDUCE - 138)) | (1L << (REFERENCES - 138)) | (1L << (REFRESH - 138)) | (1L << (RENAME - 138)) | (1L << (REPAIR - 138)) | (1L << (REPLACE - 138)) | (1L << (RESET - 138)) | (1L << (RESTRICT - 138)) | (1L << (REVOKE - 138)) | (1L << (RLIKE - 138)) | (1L << (ROLE - 138)) | (1L << (ROLES - 138)) | (1L << (ROLLBACK - 138)) | (1L << (ROLLUP - 138)) | (1L << (ROW - 138)) | (1L << (ROWS - 138)) | (1L << (SCHEMA - 138)) | (1L << (SELECT - 138)))) != 0) || ((((_la - 203)) & ~0x3f) == 0 && ((1L << (_la - 203)) & ((1L << (SEPARATED - 203)) | (1L << (SERDE - 203)) | (1L << (SERDEPROPERTIES - 203)) | (1L << (SESSION_USER - 203)) | (1L << (SET - 203)) | (1L << (SETS - 203)) | (1L << (SHOW - 203)) | (1L << (SKEWED - 203)) | (1L << (SOME - 203)) | (1L << (SORT - 203)) | (1L << (SORTED - 203)) | (1L << (START - 203)) | (1L << (STATISTICS - 203)) | (1L << (STORED - 203)) | (1L << (STRATIFY - 203)) | (1L << (STRUCT - 203)) | (1L << (SUBSTR - 203)) | (1L << (SUBSTRING - 203)) | (1L << (TABLE - 203)) | (1L << (TABLES - 203)) | (1L << (TABLESAMPLE - 203)) | (1L << (TBLPROPERTIES - 203)) | (1L << (TEMPORARY - 203)) | (1L << (TERMINATED - 203)) | (1L << (THEN - 203)) | (1L << (TIME - 203)) | (1L << (TO - 203)) | (1L << (TOUCH - 203)) | (1L << (TRAILING - 203)) | (1L << (TRANSACTION - 203)) | (1L << (TRANSACTIONS - 203)) | (1L << (TRANSFORM - 203)) | (1L << (TRIM - 203)) | (1L << (TRUE - 203)) | (1L << (TRUNCATE - 203)) | (1L << (TYPE - 203)) | (1L << (UNARCHIVE - 203)) | (1L << (UNBOUNDED - 203)) | (1L << (UNCACHE - 203)) | (1L << (UNIQUE - 203)) | (1L << (UNKNOWN - 203)) | (1L << (UNLOCK - 203)) | (1L << (UNSET - 203)) | (1L << (UPDATE - 203)) | (1L << (USE - 203)) | (1L << (USER - 203)) | (1L << (VALUES - 203)) | (1L << (VIEW - 203)) | (1L << (VIEWS - 203)) | (1L << (WHEN - 203)) | (1L << (WHERE - 203)) | (1L << (WINDOW - 203)) | (1L << (WITH - 203)) | (1L << (ZONE - 203)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 14:
			return queryTerm_sempred((QueryTermContext)_localctx, predIndex);
		case 41:
			return booleanExpression_sempred((BooleanExpressionContext)_localctx, predIndex);
		case 43:
			return valueExpression_sempred((ValueExpressionContext)_localctx, predIndex);
		case 44:
			return primaryExpression_sempred((PrimaryExpressionContext)_localctx, predIndex);
		case 64:
			return identifier_sempred((IdentifierContext)_localctx, predIndex);
		case 65:
			return strictIdentifier_sempred((StrictIdentifierContext)_localctx, predIndex);
		case 67:
			return number_sempred((NumberContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean queryTerm_sempred(QueryTermContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean booleanExpression_sempred(BooleanExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean valueExpression_sempred(ValueExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		case 7:
			return precpred(_ctx, 2);
		case 8:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean primaryExpression_sempred(PrimaryExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 7);
		case 10:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean identifier_sempred(IdentifierContext _localctx, int predIndex) {
		switch (predIndex) {
		case 11:
			return !SQL_standard_keyword_behavior;
		}
		return true;
	}
	private boolean strictIdentifier_sempred(StrictIdentifierContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return SQL_standard_keyword_behavior;
		case 13:
			return !SQL_standard_keyword_behavior;
		}
		return true;
	}
	private boolean number_sempred(NumberContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return !legacy_exponent_literal_as_decimal_enabled;
		case 15:
			return !legacy_exponent_literal_as_decimal_enabled;
		case 16:
			return legacy_exponent_literal_as_decimal_enabled;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\u0127\u03b7\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\3\2"+
		"\3\2\7\2\u0093\n\2\f\2\16\2\u0096\13\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4"+
		"\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3"+
		"\n\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\5"+
		"\17\u00c0\n\17\3\17\3\17\5\17\u00c4\n\17\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\5\20\u00cc\n\20\3\20\7\20\u00cf\n\20\f\20\16\20\u00d2\13\20\3\21\3\21"+
		"\3\21\3\21\5\21\u00d8\n\21\3\22\3\22\3\22\7\22\u00dd\n\22\f\22\16\22\u00e0"+
		"\13\22\3\23\3\23\3\23\7\23\u00e5\n\23\f\23\16\23\u00e8\13\23\3\24\3\24"+
		"\5\24\u00ec\n\24\3\24\3\24\5\24\u00f0\n\24\3\25\3\25\5\25\u00f4\n\25\3"+
		"\25\7\25\u00f7\n\25\f\25\16\25\u00fa\13\25\3\25\5\25\u00fd\n\25\3\25\5"+
		"\25\u0100\n\25\3\25\5\25\u0103\n\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30"+
		"\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\5\32\u0114\n\32\3\32\3\32\3\32"+
		"\3\32\3\32\7\32\u011b\n\32\f\32\16\32\u011e\13\32\5\32\u0120\n\32\3\32"+
		"\3\32\3\32\5\32\u0125\n\32\3\32\3\32\3\32\7\32\u012a\n\32\f\32\16\32\u012d"+
		"\13\32\5\32\u012f\n\32\3\33\3\33\7\33\u0133\n\33\f\33\16\33\u0136\13\33"+
		"\3\34\3\34\3\34\3\34\5\34\u013c\n\34\3\35\5\35\u013f\n\35\3\35\3\35\5"+
		"\35\u0143\n\35\3\35\3\35\5\35\u0147\n\35\3\35\3\35\5\35\u014b\n\35\5\35"+
		"\u014d\n\35\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\5\37\u015b\n\37\3 \3 \3 \3 \7 \u0161\n \f \16 \u0164\13 \3 \3 \3!\3!"+
		"\3!\3!\3!\7!\u016d\n!\f!\16!\u0170\13!\3\"\3\"\3\"\3\"\3#\3#\3#\7#\u0179"+
		"\n#\f#\16#\u017c\13#\3$\5$\u017f\n$\3$\3$\5$\u0183\n$\5$\u0185\n$\3%\3"+
		"%\3%\7%\u018a\n%\f%\16%\u018d\13%\3&\3&\3&\5&\u0192\n&\3&\3&\3\'\3\'\3"+
		"\'\5\'\u0199\n\'\3\'\3\'\3(\3(\5(\u019f\n(\3(\3(\5(\u01a3\n(\5(\u01a5"+
		"\n(\3)\3)\3)\7)\u01aa\n)\f)\16)\u01ad\13)\3*\3*\3+\3+\3+\3+\3+\5+\u01b6"+
		"\n+\5+\u01b8\n+\3+\3+\3+\3+\3+\3+\7+\u01c0\n+\f+\16+\u01c3\13+\3,\5,\u01c6"+
		"\n,\3,\3,\3,\3,\3,\3,\5,\u01ce\n,\3,\3,\3,\3,\3,\7,\u01d5\n,\f,\16,\u01d8"+
		"\13,\3,\3,\3,\5,\u01dd\n,\3,\3,\3,\5,\u01e2\n,\3,\3,\3,\3,\3,\3,\3,\3"+
		",\7,\u01ec\n,\f,\16,\u01ef\13,\3,\3,\5,\u01f3\n,\3,\5,\u01f6\n,\3,\3,"+
		"\3,\3,\5,\u01fc\n,\3,\3,\5,\u0200\n,\3,\3,\3,\5,\u0205\n,\3,\5,\u0208"+
		"\n,\3-\3-\3-\3-\5-\u020e\n-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-"+
		"\3-\3-\3-\3-\3-\7-\u0223\n-\f-\16-\u0226\13-\3.\3.\3.\6.\u022b\n.\r.\16"+
		".\u022c\3.\3.\5.\u0231\n.\3.\3.\3.\3.\3.\6.\u0238\n.\r.\16.\u0239\3.\3"+
		".\5.\u023e\n.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\7.\u024e\n.\f"+
		".\16.\u0251\13.\5.\u0253\n.\3.\3.\3.\3.\3.\3.\5.\u025b\n.\3.\3.\3.\3."+
		"\3.\3.\3.\5.\u0264\n.\3.\3.\3.\3.\3.\3.\3.\5.\u026d\n.\3.\3.\3.\3.\3."+
		"\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\6.\u0282\n.\r.\16.\u0283\3"+
		".\3.\3.\3.\3.\3.\3.\7.\u028d\n.\f.\16.\u0290\13.\5.\u0292\n.\3.\3.\3."+
		"\3.\3.\3.\3.\5.\u029b\n.\3.\3.\3.\3.\3.\3.\3.\3.\6.\u02a5\n.\r.\16.\u02a6"+
		"\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3.\3."+
		"\5.\u02c0\n.\3.\3.\3.\3.\3.\5.\u02c7\n.\3.\5.\u02ca\n.\3.\3.\3.\3.\5."+
		"\u02d0\n.\3.\3.\3.\3.\3.\3.\3.\3.\7.\u02da\n.\f.\16.\u02dd\13.\3/\3/\3"+
		"/\3/\3/\6/\u02e4\n/\r/\16/\u02e5\5/\u02e8\n/\3\60\3\60\3\61\3\61\3\62"+
		"\3\62\3\62\5\62\u02f1\n\62\3\63\3\63\5\63\u02f5\n\63\3\64\3\64\3\64\6"+
		"\64\u02fa\n\64\r\64\16\64\u02fb\3\65\3\65\3\65\5\65\u0301\n\65\3\66\3"+
		"\66\3\66\3\66\3\66\3\67\5\67\u0309\n\67\3\67\3\67\5\67\u030d\n\67\38\3"+
		"8\38\38\38\38\38\38\38\38\38\38\38\38\38\58\u031e\n8\38\38\58\u0322\n"+
		"8\38\38\38\38\38\78\u0329\n8\f8\168\u032c\138\38\58\u032f\n8\58\u0331"+
		"\n8\39\39\39\79\u0336\n9\f9\169\u0339\139\3:\3:\3:\3:\5:\u033f\n:\3:\5"+
		":\u0342\n:\3;\3;\3;\7;\u0347\n;\f;\16;\u034a\13;\3<\3<\3<\3<\3<\5<\u0351"+
		"\n<\3<\5<\u0354\n<\3=\3=\3=\3=\3=\3>\3>\3>\3>\5>\u035f\n>\3?\3?\3?\7?"+
		"\u0364\n?\f?\16?\u0367\13?\3@\3@\3@\3A\3A\6A\u036e\nA\rA\16A\u036f\3A"+
		"\5A\u0373\nA\3B\3B\3B\5B\u0378\nB\3C\3C\3C\3C\3C\3C\5C\u0380\nC\3D\3D"+
		"\3E\3E\5E\u0386\nE\3E\3E\3E\5E\u038b\nE\3E\3E\3E\5E\u0390\nE\3E\3E\5E"+
		"\u0394\nE\3E\3E\5E\u0398\nE\3E\3E\5E\u039c\nE\3E\3E\5E\u03a0\nE\3E\3E"+
		"\5E\u03a4\nE\3E\3E\5E\u03a8\nE\3E\3E\5E\u03ac\nE\3E\5E\u03af\nE\3F\3F"+
		"\3G\3G\3H\3H\3H\2\6\36TXZI\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$"+
		"&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084"+
		"\u0086\u0088\u008a\u008c\u008e\2\25\4\2\30\30EE\4\2__\177\177\5\2\16\16"+
		"\23\23\u00d6\u00d6\5\2ZZ\u00ef\u00ef\u00f7\u00f7\4\2\u010e\u010f\u0113"+
		"\u0113\4\2LL\u0110\u0112\4\2\u010e\u010f\u0116\u0116\3\2\u00de\u00df\4"+
		"\2\4\4ee\4\2\4\4aa\5\2\34\34\u0082\u0082\u00ea\u00ea\3\2\u0106\u010d\4"+
		"\2ZZ\u00ef\u00ef\3\2\u010e\u010f\4\2\u011c\u011c\u011e\u011e\3\2\u011d"+
		"\u011e\62\2\f\r\17\20\22\22\25\26\30\31\33\33\35!$$&)++-\63\65\6589>I"+
		"KMQQSY\\\\^`cdgillnprsuwyy||~\u0081\u0084\u0094\u0096\u0096\u0099\u009a"+
		"\u009d\u009e\u00a1\u00a1\u00a3\u00a4\u00a6\u00af\u00b1\u00b9\u00bb\u00c1"+
		"\u00c3\u00ca\u00cc\u00cf\u00d1\u00d5\u00d7\u00df\u00e1\u00e5\u00e9\u00e9"+
		"\u00eb\u00f4\u00f8\u00fb\u00fe\u0100\u0103\u0103\u0105\u0105\21\2\22\22"+
		"\67\67RRffttxx}}\u0083\u0083\u0095\u0095\u009b\u009b\u00c2\u00c2\u00cc"+
		"\u00cc\u00d2\u00d2\u00f5\u00f5\u00fd\u00fd\23\2\f\21\23\23\25\668QSeg"+
		"suwy|~\u0082\u0084\u0094\u0096\u009a\u009c\u00c1\u00c3\u00cb\u00cd\u00d1"+
		"\u00d3\u00f4\u00f6\u00fc\u00fe\u0105\2\u0416\2\u0090\3\2\2\2\4\u0099\3"+
		"\2\2\2\6\u009c\3\2\2\2\b\u009f\3\2\2\2\n\u00a2\3\2\2\2\f\u00a5\3\2\2\2"+
		"\16\u00a8\3\2\2\2\20\u00ab\3\2\2\2\22\u00ae\3\2\2\2\24\u00b1\3\2\2\2\26"+
		"\u00b4\3\2\2\2\30\u00b6\3\2\2\2\32\u00b9\3\2\2\2\34\u00bf\3\2\2\2\36\u00c5"+
		"\3\2\2\2 \u00d7\3\2\2\2\"\u00d9\3\2\2\2$\u00e1\3\2\2\2&\u00e9\3\2\2\2"+
		"(\u00f1\3\2\2\2*\u0104\3\2\2\2,\u0107\3\2\2\2.\u010a\3\2\2\2\60\u010d"+
		"\3\2\2\2\62\u0110\3\2\2\2\64\u0130\3\2\2\2\66\u0137\3\2\2\28\u014c\3\2"+
		"\2\2:\u014e\3\2\2\2<\u015a\3\2\2\2>\u015c\3\2\2\2@\u0167\3\2\2\2B\u0171"+
		"\3\2\2\2D\u0175\3\2\2\2F\u0184\3\2\2\2H\u0186\3\2\2\2J\u0191\3\2\2\2L"+
		"\u0198\3\2\2\2N\u019c\3\2\2\2P\u01a6\3\2\2\2R\u01ae\3\2\2\2T\u01b7\3\2"+
		"\2\2V\u0207\3\2\2\2X\u020d\3\2\2\2Z\u02cf\3\2\2\2\\\u02e7\3\2\2\2^\u02e9"+
		"\3\2\2\2`\u02eb\3\2\2\2b\u02ed\3\2\2\2d\u02f2\3\2\2\2f\u02f9\3\2\2\2h"+
		"\u02fd\3\2\2\2j\u0302\3\2\2\2l\u030c\3\2\2\2n\u0330\3\2\2\2p\u0332\3\2"+
		"\2\2r\u033a\3\2\2\2t\u0343\3\2\2\2v\u034b\3\2\2\2x\u0355\3\2\2\2z\u035e"+
		"\3\2\2\2|\u0360\3\2\2\2~\u0368\3\2\2\2\u0080\u0372\3\2\2\2\u0082\u0377"+
		"\3\2\2\2\u0084\u037f\3\2\2\2\u0086\u0381\3\2\2\2\u0088\u03ae\3\2\2\2\u008a"+
		"\u03b0\3\2\2\2\u008c\u03b2\3\2\2\2\u008e\u03b4\3\2\2\2\u0090\u0094\5\26"+
		"\f\2\u0091\u0093\7\3\2\2\u0092\u0091\3\2\2\2\u0093\u0096\3\2\2\2\u0094"+
		"\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0097\3\2\2\2\u0096\u0094\3\2"+
		"\2\2\u0097\u0098\7\2\2\3\u0098\3\3\2\2\2\u0099\u009a\5N(\2\u009a\u009b"+
		"\7\2\2\3\u009b\5\3\2\2\2\u009c\u009d\5P)\2\u009d\u009e\7\2\2\3\u009e\7"+
		"\3\2\2\2\u009f\u00a0\5$\23\2\u00a0\u00a1\7\2\2\3\u00a1\t\3\2\2\2\u00a2"+
		"\u00a3\5\"\22\2\u00a3\u00a4\7\2\2\3\u00a4\13\3\2\2\2\u00a5\u00a6\5J&\2"+
		"\u00a6\u00a7\7\2\2\3\u00a7\r\3\2\2\2\u00a8\u00a9\5H%\2\u00a9\u00aa\7\2"+
		"\2\3\u00aa\17\3\2\2\2\u00ab\u00ac\5L\'\2\u00ac\u00ad\7\2\2\3\u00ad\21"+
		"\3\2\2\2\u00ae\u00af\5n8\2\u00af\u00b0\7\2\2\3\u00b0\23\3\2\2\2\u00b1"+
		"\u00b2\5p9\2\u00b2\u00b3\7\2\2\3\u00b3\25\3\2\2\2\u00b4\u00b5\5\32\16"+
		"\2\u00b5\27\3\2\2\2\u00b6\u00b7\7.\2\2\u00b7\u00b8\7\u0118\2\2\u00b8\31"+
		"\3\2\2\2\u00b9\u00ba\5\36\20\2\u00ba\u00bb\5\34\17\2\u00bb\33\3\2\2\2"+
		"\u00bc\u00bd\7\u00a0\2\2\u00bd\u00be\7\37\2\2\u00be\u00c0\5$\23\2\u00bf"+
		"\u00bc\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00c2\7\u0085"+
		"\2\2\u00c2\u00c4\5R*\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\35"+
		"\3\2\2\2\u00c5\u00c6\b\20\1\2\u00c6\u00c7\5 \21\2\u00c7\u00d0\3\2\2\2"+
		"\u00c8\u00c9\f\3\2\2\u00c9\u00cb\7\u00f5\2\2\u00ca\u00cc\7\16\2\2\u00cb"+
		"\u00ca\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00cf\5\36"+
		"\20\4\u00ce\u00c8\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0"+
		"\u00d1\3\2\2\2\u00d1\37\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3\u00d8\5(\25"+
		"\2\u00d4\u00d5\7\u00e0\2\2\u00d5\u00d8\5H%\2\u00d6\u00d8\5> \2\u00d7\u00d3"+
		"\3\2\2\2\u00d7\u00d4\3\2\2\2\u00d7\u00d6\3\2\2\2\u00d8!\3\2\2\2\u00d9"+
		"\u00de\5R*\2\u00da\u00db\7\4\2\2\u00db\u00dd\5R*\2\u00dc\u00da\3\2\2\2"+
		"\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df#\3"+
		"\2\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e6\5&\24\2\u00e2\u00e3\7\4\2\2\u00e3"+
		"\u00e5\5&\24\2\u00e4\u00e2\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2"+
		"\2\2\u00e6\u00e7\3\2\2\2\u00e7%\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00eb"+
		"\5R*\2\u00ea\u00ec\t\2\2\2\u00eb\u00ea\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec"+
		"\u00ef\3\2\2\2\u00ed\u00ee\7\u0099\2\2\u00ee\u00f0\t\3\2\2\u00ef\u00ed"+
		"\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\'\3\2\2\2\u00f1\u00f3\5*\26\2\u00f2"+
		"\u00f4\5\60\31\2\u00f3\u00f2\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f8\3"+
		"\2\2\2\u00f5\u00f7\5\62\32\2\u00f6\u00f5\3\2\2\2\u00f7\u00fa\3\2\2\2\u00f8"+
		"\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fc\3\2\2\2\u00fa\u00f8\3\2"+
		"\2\2\u00fb\u00fd\5,\27\2\u00fc\u00fb\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd"+
		"\u00ff\3\2\2\2\u00fe\u0100\5@!\2\u00ff\u00fe\3\2\2\2\u00ff\u0100\3\2\2"+
		"\2\u0100\u0102\3\2\2\2\u0101\u0103\5.\30\2\u0102\u0101\3\2\2\2\u0102\u0103"+
		"\3\2\2\2\u0103)\3\2\2\2\u0104\u0105\7\u00cb\2\2\u0105\u0106\5P)\2\u0106"+
		"+\3\2\2\2\u0107\u0108\7\u0102\2\2\u0108\u0109\5T+\2\u0109-\3\2\2\2\u010a"+
		"\u010b\7m\2\2\u010b\u010c\5T+\2\u010c/\3\2\2\2\u010d\u010e\7e\2\2\u010e"+
		"\u010f\5\64\33\2\u010f\61\3\2\2\2\u0110\u0111\7\u0080\2\2\u0111\u0113"+
		"\7\u00ff\2\2\u0112\u0114\7\u00a2\2\2\u0113\u0112\3\2\2\2\u0113\u0114\3"+
		"\2\2\2\u0114\u0115\3\2\2\2\u0115\u0116\5|?\2\u0116\u011f\7\5\2\2\u0117"+
		"\u011c\5R*\2\u0118\u0119\7\4\2\2\u0119\u011b\5R*\2\u011a\u0118\3\2\2\2"+
		"\u011b\u011e\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u0120"+
		"\3\2\2\2\u011e\u011c\3\2\2\2\u011f\u0117\3\2\2\2\u011f\u0120\3\2\2\2\u0120"+
		"\u0121\3\2\2\2\u0121\u0122\7\6\2\2\u0122\u012e\5\u0082B\2\u0123\u0125"+
		"\7\27\2\2\u0124\u0123\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0126\3\2\2\2"+
		"\u0126\u012b\5\u0082B\2\u0127\u0128\7\4\2\2\u0128\u012a\5\u0082B\2\u0129"+
		"\u0127\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2\2\2\u012b\u012c\3\2"+
		"\2\2\u012c\u012f\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u0124\3\2\2\2\u012e"+
		"\u012f\3\2\2\2\u012f\63\3\2\2\2\u0130\u0134\5<\37\2\u0131\u0133\5\66\34"+
		"\2\u0132\u0131\3\2\2\2\u0133\u0136\3\2\2\2\u0134\u0132\3\2\2\2\u0134\u0135"+
		"\3\2\2\2\u0135\65\3\2\2\2\u0136\u0134\3\2\2\2\u0137\u0138\58\35\2\u0138"+
		"\u0139\7}\2\2\u0139\u013b\5<\37\2\u013a\u013c\5:\36\2\u013b\u013a\3\2"+
		"\2\2\u013b\u013c\3\2\2\2\u013c\67\3\2\2\2\u013d\u013f\7t\2\2\u013e\u013d"+
		"\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u014d\3\2\2\2\u0140\u0142\7\u0083\2"+
		"\2\u0141\u0143\7\u00a2\2\2\u0142\u0141\3\2\2\2\u0142\u0143\3\2\2\2\u0143"+
		"\u014d\3\2\2\2\u0144\u0146\7\u00c2\2\2\u0145\u0147\7\u00a2\2\2\u0146\u0145"+
		"\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u014d\3\2\2\2\u0148\u014a\7f\2\2\u0149"+
		"\u014b\7\u00a2\2\2\u014a\u0149\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014d"+
		"\3\2\2\2\u014c\u013e\3\2\2\2\u014c\u0140\3\2\2\2\u014c\u0144\3\2\2\2\u014c"+
		"\u0148\3\2\2\2\u014d9\3\2\2\2\u014e\u014f\7\u009b\2\2\u014f\u0150\5T+"+
		"\2\u0150;\3\2\2\2\u0151\u0152\5H%\2\u0152\u0153\5F$\2\u0153\u015b\3\2"+
		"\2\2\u0154\u0155\7\5\2\2\u0155\u0156\5\32\16\2\u0156\u0157\7\6\2\2\u0157"+
		"\u0158\5F$\2\u0158\u015b\3\2\2\2\u0159\u015b\5> \2\u015a\u0151\3\2\2\2"+
		"\u015a\u0154\3\2\2\2\u015a\u0159\3\2\2\2\u015b=\3\2\2\2\u015c\u015d\7"+
		"\u00fe\2\2\u015d\u0162\5R*\2\u015e\u015f\7\4\2\2\u015f\u0161\5R*\2\u0160"+
		"\u015e\3\2\2\2\u0161\u0164\3\2\2\2\u0162\u0160\3\2\2\2\u0162\u0163\3\2"+
		"\2\2\u0163\u0165\3\2\2\2\u0164\u0162\3\2\2\2\u0165\u0166\5F$\2\u0166?"+
		"\3\2\2\2\u0167\u0168\7k\2\2\u0168\u0169\7\37\2\2\u0169\u016e\5R*\2\u016a"+
		"\u016b\7\4\2\2\u016b\u016d\5R*\2\u016c\u016a\3\2\2\2\u016d\u0170\3\2\2"+
		"\2\u016e\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016fA\3\2\2\2\u0170\u016e"+
		"\3\2\2\2\u0171\u0172\7\5\2\2\u0172\u0173\5D#\2\u0173\u0174\7\6\2\2\u0174"+
		"C\3\2\2\2\u0175\u017a\5~@\2\u0176\u0177\7\4\2\2\u0177\u0179\5~@\2\u0178"+
		"\u0176\3\2\2\2\u0179\u017c\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u017b\3\2"+
		"\2\2\u017bE\3\2\2\2\u017c\u017a\3\2\2\2\u017d\u017f\7\27\2\2\u017e\u017d"+
		"\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0182\5\u0084C"+
		"\2\u0181\u0183\5B\"\2\u0182\u0181\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0185"+
		"\3\2\2\2\u0184\u017e\3\2\2\2\u0184\u0185\3\2\2\2\u0185G\3\2\2\2\u0186"+
		"\u018b\5~@\2\u0187\u0188\7\7\2\2\u0188\u018a\5~@\2\u0189\u0187\3\2\2\2"+
		"\u018a\u018d\3\2\2\2\u018b\u0189\3\2\2\2\u018b\u018c\3\2\2\2\u018cI\3"+
		"\2\2\2\u018d\u018b\3\2\2\2\u018e\u018f\5~@\2\u018f\u0190\7\7\2\2\u0190"+
		"\u0192\3\2\2\2\u0191\u018e\3\2\2\2\u0191\u0192\3\2\2\2\u0192\u0193\3\2"+
		"\2\2\u0193\u0194\5~@\2\u0194K\3\2\2\2\u0195\u0196\5~@\2\u0196\u0197\7"+
		"\7\2\2\u0197\u0199\3\2\2\2\u0198\u0195\3\2\2\2\u0198\u0199\3\2\2\2\u0199"+
		"\u019a\3\2\2\2\u019a\u019b\5~@\2\u019bM\3\2\2\2\u019c\u01a4\5R*\2\u019d"+
		"\u019f\7\27\2\2\u019e\u019d\3\2\2\2\u019e\u019f\3\2\2\2\u019f\u01a2\3"+
		"\2\2\2\u01a0\u01a3\5~@\2\u01a1\u01a3\5B\"\2\u01a2\u01a0\3\2\2\2\u01a2"+
		"\u01a1\3\2\2\2\u01a3\u01a5\3\2\2\2\u01a4\u019e\3\2\2\2\u01a4\u01a5\3\2"+
		"\2\2\u01a5O\3\2\2\2\u01a6\u01ab\5N(\2\u01a7\u01a8\7\4\2\2\u01a8\u01aa"+
		"\5N(\2\u01a9\u01a7\3\2\2\2\u01aa\u01ad\3\2\2\2\u01ab\u01a9\3\2\2\2\u01ab"+
		"\u01ac\3\2\2\2\u01acQ\3\2\2\2\u01ad\u01ab\3\2\2\2\u01ae\u01af\5T+\2\u01af"+
		"S\3\2\2\2\u01b0\u01b1\b+\1\2\u01b1\u01b2\7\u0097\2\2\u01b2\u01b8\5T+\6"+
		"\u01b3\u01b5\5X-\2\u01b4\u01b6\5V,\2\u01b5\u01b4\3\2\2\2\u01b5\u01b6\3"+
		"\2\2\2\u01b6\u01b8\3\2\2\2\u01b7\u01b0\3\2\2\2\u01b7\u01b3\3\2\2\2\u01b8"+
		"\u01c1\3\2\2\2\u01b9\u01ba\f\4\2\2\u01ba\u01bb\7\21\2\2\u01bb\u01c0\5"+
		"T+\5\u01bc\u01bd\f\3\2\2\u01bd\u01be\7\u009f\2\2\u01be\u01c0\5T+\4\u01bf"+
		"\u01b9\3\2\2\2\u01bf\u01bc\3\2\2\2\u01c0\u01c3\3\2\2\2\u01c1\u01bf\3\2"+
		"\2\2\u01c1\u01c2\3\2\2\2\u01c2U\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c4\u01c6"+
		"\7\u0097\2\2\u01c5\u01c4\3\2\2\2\u01c5\u01c6\3\2\2\2\u01c6\u01c7\3\2\2"+
		"\2\u01c7\u01c8\7\33\2\2\u01c8\u01c9\5X-\2\u01c9\u01ca\7\21\2\2\u01ca\u01cb"+
		"\5X-\2\u01cb\u0208\3\2\2\2\u01cc\u01ce\7\u0097\2\2\u01cd\u01cc\3\2\2\2"+
		"\u01cd\u01ce\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf\u01d0\7q\2\2\u01d0\u01d1"+
		"\7\5\2\2\u01d1\u01d6\5R*\2\u01d2\u01d3\7\4\2\2\u01d3\u01d5\5R*\2\u01d4"+
		"\u01d2\3\2\2\2\u01d5\u01d8\3\2\2\2\u01d6\u01d4\3\2\2\2\u01d6\u01d7\3\2"+
		"\2\2\u01d7\u01d9\3\2\2\2\u01d8\u01d6\3\2\2\2\u01d9\u01da\7\6\2\2\u01da"+
		"\u0208\3\2\2\2\u01db\u01dd\7\u0097\2\2\u01dc\u01db\3\2\2\2\u01dc\u01dd"+
		"\3\2\2\2\u01dd\u01de\3\2\2\2\u01de\u01df\7\u00c3\2\2\u01df\u0208\5X-\2"+
		"\u01e0\u01e2\7\u0097\2\2\u01e1\u01e0\3\2\2\2\u01e1\u01e2\3\2\2\2\u01e2"+
		"\u01e3\3\2\2\2\u01e3\u01e4\7\u0084\2\2\u01e4\u01f2\t\4\2\2\u01e5\u01e6"+
		"\7\5\2\2\u01e6\u01f3\7\6\2\2\u01e7\u01e8\7\5\2\2\u01e8\u01ed\5R*\2\u01e9"+
		"\u01ea\7\4\2\2\u01ea\u01ec\5R*\2\u01eb\u01e9\3\2\2\2\u01ec\u01ef\3\2\2"+
		"\2\u01ed\u01eb\3\2\2\2\u01ed\u01ee\3\2\2\2\u01ee\u01f0\3\2\2\2\u01ef\u01ed"+
		"\3\2\2\2\u01f0\u01f1\7\6\2\2\u01f1\u01f3\3\2\2\2\u01f2\u01e5\3\2\2\2\u01f2"+
		"\u01e7\3\2\2\2\u01f3\u0208\3\2\2\2\u01f4\u01f6\7\u0097\2\2\u01f5\u01f4"+
		"\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7\u01f8\7\u0084\2"+
		"\2\u01f8\u01fb\5X-\2\u01f9\u01fa\7P\2\2\u01fa\u01fc\7\u0118\2\2\u01fb"+
		"\u01f9\3\2\2\2\u01fb\u01fc\3\2\2\2\u01fc\u0208\3\2\2\2\u01fd\u01ff\7{"+
		"\2\2\u01fe\u0200\7\u0097\2\2\u01ff\u01fe\3\2\2\2\u01ff\u0200\3\2\2\2\u0200"+
		"\u0201\3\2\2\2\u0201\u0208\7\u0098\2\2\u0202\u0204\7{\2\2\u0203\u0205"+
		"\7\u0097\2\2\u0204\u0203\3\2\2\2\u0204\u0205\3\2\2\2\u0205\u0206\3\2\2"+
		"\2\u0206\u0208\t\5\2\2\u0207\u01c5\3\2\2\2\u0207\u01cd\3\2\2\2\u0207\u01dc"+
		"\3\2\2\2\u0207\u01e1\3\2\2\2\u0207\u01f5\3\2\2\2\u0207\u01fd\3\2\2\2\u0207"+
		"\u0202\3\2\2\2\u0208W\3\2\2\2\u0209\u020a\b-\1\2\u020a\u020e\5Z.\2\u020b"+
		"\u020c\t\6\2\2\u020c\u020e\5X-\t\u020d\u0209\3\2\2\2\u020d\u020b\3\2\2"+
		"\2\u020e\u0224\3\2\2\2\u020f\u0210\f\b\2\2\u0210\u0211\t\7\2\2\u0211\u0223"+
		"\5X-\t\u0212\u0213\f\7\2\2\u0213\u0214\t\b\2\2\u0214\u0223\5X-\b\u0215"+
		"\u0216\f\6\2\2\u0216\u0217\7\u0114\2\2\u0217\u0223\5X-\7\u0218\u0219\f"+
		"\5\2\2\u0219\u021a\7\u0117\2\2\u021a\u0223\5X-\6\u021b\u021c\f\4\2\2\u021c"+
		"\u021d\7\u0115\2\2\u021d\u0223\5X-\5\u021e\u021f\f\3\2\2\u021f\u0220\5"+
		"^\60\2\u0220\u0221\5X-\4\u0221\u0223\3\2\2\2\u0222\u020f\3\2\2\2\u0222"+
		"\u0212\3\2\2\2\u0222\u0215\3\2\2\2\u0222\u0218\3\2\2\2\u0222\u021b\3\2"+
		"\2\2\u0222\u021e\3\2\2\2\u0223\u0226\3\2\2\2\u0224\u0222\3\2\2\2\u0224"+
		"\u0225\3\2\2\2\u0225Y\3\2\2\2\u0226\u0224\3\2\2\2\u0227\u0228\b.\1\2\u0228"+
		"\u022a\7\"\2\2\u0229\u022b\5x=\2\u022a\u0229\3\2\2\2\u022b\u022c\3\2\2"+
		"\2\u022c\u022a\3\2\2\2\u022c\u022d\3\2\2\2\u022d\u0230\3\2\2\2\u022e\u022f"+
		"\7N\2\2\u022f\u0231\5R*\2\u0230\u022e\3\2\2\2\u0230\u0231\3\2\2\2\u0231"+
		"\u0232\3\2\2\2\u0232\u0233\7O\2\2\u0233\u02d0\3\2\2\2\u0234\u0235\7\""+
		"\2\2\u0235\u0237\5R*\2\u0236\u0238\5x=\2\u0237\u0236\3\2\2\2\u0238\u0239"+
		"\3\2\2\2\u0239\u0237\3\2\2\2\u0239\u023a\3\2\2\2\u023a\u023d\3\2\2\2\u023b"+
		"\u023c\7N\2\2\u023c\u023e\5R*\2\u023d\u023b\3\2\2\2\u023d\u023e\3\2\2"+
		"\2\u023e\u023f\3\2\2\2\u023f\u0240\7O\2\2\u0240\u02d0\3\2\2\2\u0241\u0242"+
		"\7#\2\2\u0242\u0243\7\5\2\2\u0243\u0244\5R*\2\u0244\u0245\7\27\2\2\u0245"+
		"\u0246\5n8\2\u0246\u0247\7\6\2\2\u0247\u02d0\3\2\2\2\u0248\u0249\7\u00dd"+
		"\2\2\u0249\u0252\7\5\2\2\u024a\u024f\5N(\2\u024b\u024c\7\4\2\2\u024c\u024e"+
		"\5N(\2\u024d\u024b\3\2\2\2\u024e\u0251\3\2\2\2\u024f\u024d\3\2\2\2\u024f"+
		"\u0250\3\2\2\2\u0250\u0253\3\2\2\2\u0251\u024f\3\2\2\2\u0252\u024a\3\2"+
		"\2\2\u0252\u0253\3\2\2\2\u0253\u0254\3\2\2\2\u0254\u02d0\7\6\2\2\u0255"+
		"\u0256\7_\2\2\u0256\u0257\7\5\2\2\u0257\u025a\5R*\2\u0258\u0259\7o\2\2"+
		"\u0259\u025b\7\u0099\2\2\u025a\u0258\3\2\2\2\u025a\u025b\3\2\2\2\u025b"+
		"\u025c\3\2\2\2\u025c\u025d\7\6\2\2\u025d\u02d0\3\2\2\2\u025e\u025f\7\24"+
		"\2\2\u025f\u0260\7\5\2\2\u0260\u0263\5R*\2\u0261\u0262\7o\2\2\u0262\u0264"+
		"\7\u0099\2\2\u0263\u0261\3\2\2\2\u0263\u0264\3\2\2\2\u0264\u0265\3\2\2"+
		"\2\u0265\u0266\7\6\2\2\u0266\u02d0\3\2\2\2\u0267\u0268\7\177\2\2\u0268"+
		"\u0269\7\5\2\2\u0269\u026c\5R*\2\u026a\u026b\7o\2\2\u026b\u026d\7\u0099"+
		"\2\2\u026c\u026a\3\2\2\2\u026c\u026d\3\2\2\2\u026d\u026e\3\2\2\2\u026e"+
		"\u026f\7\6\2\2\u026f\u02d0\3\2\2\2\u0270\u0271\7\u00ae\2\2\u0271\u0272"+
		"\7\5\2\2\u0272\u0273\5X-\2\u0273\u0274\7q\2\2\u0274\u0275\5X-\2\u0275"+
		"\u0276\7\6\2\2\u0276\u02d0\3\2\2\2\u0277\u02d0\5\\/\2\u0278\u02d0\7\u0110"+
		"\2\2\u0279\u027a\5|?\2\u027a\u027b\7\7\2\2\u027b\u027c\7\u0110\2\2\u027c"+
		"\u02d0\3\2\2\2\u027d\u027e\7\5\2\2\u027e\u0281\5N(\2\u027f\u0280\7\4\2"+
		"\2\u0280\u0282\5N(\2\u0281\u027f\3\2\2\2\u0282\u0283\3\2\2\2\u0283\u0281"+
		"\3\2\2\2\u0283\u0284\3\2\2\2\u0284\u0285\3\2\2\2\u0285\u0286\7\6\2\2\u0286"+
		"\u02d0\3\2\2\2\u0287\u0288\5z>\2\u0288\u0291\7\5\2\2\u0289\u028e\5R*\2"+
		"\u028a\u028b\7\4\2\2\u028b\u028d\5R*\2\u028c\u028a\3\2\2\2\u028d\u0290"+
		"\3\2\2\2\u028e\u028c\3\2\2\2\u028e\u028f\3\2\2\2\u028f\u0292\3\2\2\2\u0290"+
		"\u028e\3\2\2\2\u0291\u0289\3\2\2\2\u0291\u0292\3\2\2\2\u0292\u0293\3\2"+
		"\2\2\u0293\u029a\7\6\2\2\u0294\u0295\7]\2\2\u0295\u0296\7\5\2\2\u0296"+
		"\u0297\7\u0102\2\2\u0297\u0298\5T+\2\u0298\u0299\7\6\2\2\u0299\u029b\3"+
		"\2\2\2\u029a\u0294\3\2\2\2\u029a\u029b\3\2\2\2\u029b\u02d0\3\2\2\2\u029c"+
		"\u029d\5\u0082B\2\u029d\u029e\7\b\2\2\u029e\u029f\5R*\2\u029f\u02d0\3"+
		"\2\2\2\u02a0\u02a1\7\5\2\2\u02a1\u02a4\5\u0082B\2\u02a2\u02a3\7\4\2\2"+
		"\u02a3\u02a5\5\u0082B\2\u02a4\u02a2\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6"+
		"\u02a4\3\2\2\2\u02a6\u02a7\3\2\2\2\u02a7\u02a8\3\2\2\2\u02a8\u02a9\7\6"+
		"\2\2\u02a9\u02aa\7\b\2\2\u02aa\u02ab\5R*\2\u02ab\u02d0\3\2\2\2\u02ac\u02d0"+
		"\5\u0082B\2\u02ad\u02ae\7\5\2\2\u02ae\u02af\5R*\2\u02af\u02b0\7\6\2\2"+
		"\u02b0\u02d0\3\2\2\2\u02b1\u02b2\7Y\2\2\u02b2\u02b3\7\5\2\2\u02b3\u02b4"+
		"\5\u0082B\2\u02b4\u02b5\7e\2\2\u02b5\u02b6\5X-\2\u02b6\u02b7\7\6\2\2\u02b7"+
		"\u02d0\3\2\2\2\u02b8\u02b9\t\t\2\2\u02b9\u02ba\7\5\2\2\u02ba\u02bb\5X"+
		"-\2\u02bb\u02bc\t\n\2\2\u02bc\u02bf\5X-\2\u02bd\u02be\t\13\2\2\u02be\u02c0"+
		"\5X-\2\u02bf\u02bd\3\2\2\2\u02bf\u02c0\3\2\2\2\u02c0\u02c1\3\2\2\2\u02c1"+
		"\u02c2\7\6\2\2\u02c2\u02d0\3\2\2\2\u02c3\u02c4\7\u00ee\2\2\u02c4\u02c6"+
		"\7\5\2\2\u02c5\u02c7\t\f\2\2\u02c6\u02c5\3\2\2\2\u02c6\u02c7\3\2\2\2\u02c7"+
		"\u02c9\3\2\2\2\u02c8\u02ca\5X-\2\u02c9\u02c8\3\2\2\2\u02c9\u02ca\3\2\2"+
		"\2\u02ca\u02cb\3\2\2\2\u02cb\u02cc\7e\2\2\u02cc\u02cd\5X-\2\u02cd\u02ce"+
		"\7\6\2\2\u02ce\u02d0\3\2\2\2\u02cf\u0227\3\2\2\2\u02cf\u0234\3\2\2\2\u02cf"+
		"\u0241\3\2\2\2\u02cf\u0248\3\2\2\2\u02cf\u0255\3\2\2\2\u02cf\u025e\3\2"+
		"\2\2\u02cf\u0267\3\2\2\2\u02cf\u0270\3\2\2\2\u02cf\u0277\3\2\2\2\u02cf"+
		"\u0278\3\2\2\2\u02cf\u0279\3\2\2\2\u02cf\u027d\3\2\2\2\u02cf\u0287\3\2"+
		"\2\2\u02cf\u029c\3\2\2\2\u02cf\u02a0\3\2\2\2\u02cf\u02ac\3\2\2\2\u02cf"+
		"\u02ad\3\2\2\2\u02cf\u02b1\3\2\2\2\u02cf\u02b8\3\2\2\2\u02cf\u02c3\3\2"+
		"\2\2\u02d0\u02db\3\2\2\2\u02d1\u02d2\f\t\2\2\u02d2\u02d3\7\t\2\2\u02d3"+
		"\u02d4\5X-\2\u02d4\u02d5\7\n\2\2\u02d5\u02da\3\2\2\2\u02d6\u02d7\f\7\2"+
		"\2\u02d7\u02d8\7\7\2\2\u02d8\u02da\5\u0082B\2\u02d9\u02d1\3\2\2\2\u02d9"+
		"\u02d6\3\2\2\2\u02da\u02dd\3\2\2\2\u02db\u02d9\3\2\2\2\u02db\u02dc\3\2"+
		"\2\2\u02dc[\3\2\2\2\u02dd\u02db\3\2\2\2\u02de\u02e8\7\u0098\2\2\u02df"+
		"\u02e8\5b\62\2\u02e0\u02e8\5\u0088E\2\u02e1\u02e8\5`\61\2\u02e2\u02e4"+
		"\7\u0118\2\2\u02e3\u02e2\3\2\2\2\u02e4\u02e5\3\2\2\2\u02e5\u02e3\3\2\2"+
		"\2\u02e5\u02e6\3\2\2\2\u02e6\u02e8\3\2\2\2\u02e7\u02de\3\2\2\2\u02e7\u02df"+
		"\3\2\2\2\u02e7\u02e0\3\2\2\2\u02e7\u02e1\3\2\2\2\u02e7\u02e3\3\2\2\2\u02e8"+
		"]\3\2\2\2\u02e9\u02ea\t\r\2\2\u02ea_\3\2\2\2\u02eb\u02ec\t\16\2\2\u02ec"+
		"a\3\2\2\2\u02ed\u02f0\7y\2\2\u02ee\u02f1\5d\63\2\u02ef\u02f1\5h\65\2\u02f0"+
		"\u02ee\3\2\2\2\u02f0\u02ef\3\2\2\2\u02f0\u02f1\3\2\2\2\u02f1c\3\2\2\2"+
		"\u02f2\u02f4\5f\64\2\u02f3\u02f5\5j\66\2\u02f4\u02f3\3\2\2\2\u02f4\u02f5"+
		"\3\2\2\2\u02f5e\3\2\2\2\u02f6\u02f7\5l\67\2\u02f7\u02f8\5\u0082B\2\u02f8"+
		"\u02fa\3\2\2\2\u02f9\u02f6\3\2\2\2\u02fa\u02fb\3\2\2\2\u02fb\u02f9\3\2"+
		"\2\2\u02fb\u02fc\3\2\2\2\u02fcg\3\2\2\2\u02fd\u0300\5j\66\2\u02fe\u0301"+
		"\5f\64\2\u02ff\u0301\5j\66\2\u0300\u02fe\3\2\2\2\u0300\u02ff\3\2\2\2\u0300"+
		"\u0301\3\2\2\2\u0301i\3\2\2\2\u0302\u0303\5l\67\2\u0303\u0304\5\u0082"+
		"B\2\u0304\u0305\7\u00e8\2\2\u0305\u0306\5\u0082B\2\u0306k\3\2\2\2\u0307"+
		"\u0309\t\17\2\2\u0308\u0307\3\2\2\2\u0308\u0309\3\2\2\2\u0309\u030a\3"+
		"\2\2\2\u030a\u030d\t\20\2\2\u030b\u030d\7\u0118\2\2\u030c\u0308\3\2\2"+
		"\2\u030c\u030b\3\2\2\2\u030dm\3\2\2\2\u030e\u030f\7\26\2\2\u030f\u0310"+
		"\7\u010a\2\2\u0310\u0311\5n8\2\u0311\u0312\7\u010c\2\2\u0312\u0331\3\2"+
		"\2\2\u0313\u0314\7\u008f\2\2\u0314\u0315\7\u010a\2\2\u0315\u0316\5n8\2"+
		"\u0316\u0317\7\4\2\2\u0317\u0318\5n8\2\u0318\u0319\7\u010c\2\2\u0319\u0331"+
		"\3\2\2\2\u031a\u0321\7\u00dd\2\2\u031b\u031d\7\u010a\2\2\u031c\u031e\5"+
		"t;\2\u031d\u031c\3\2\2\2\u031d\u031e\3\2\2\2\u031e\u031f\3\2\2\2\u031f"+
		"\u0322\7\u010c\2\2\u0320\u0322\7\u0108\2\2\u0321\u031b\3\2\2\2\u0321\u0320"+
		"\3\2\2\2\u0322\u0331\3\2\2\2\u0323\u032e\5\u0082B\2\u0324\u0325\7\5\2"+
		"\2\u0325\u032a\7\u011c\2\2\u0326\u0327\7\4\2\2\u0327\u0329\7\u011c\2\2"+
		"\u0328\u0326\3\2\2\2\u0329\u032c\3\2\2\2\u032a\u0328\3\2\2\2\u032a\u032b"+
		"\3\2\2\2\u032b\u032d\3\2\2\2\u032c\u032a\3\2\2\2\u032d\u032f\7\6\2\2\u032e"+
		"\u0324\3\2\2\2\u032e\u032f\3\2\2\2\u032f\u0331\3\2\2\2\u0330\u030e\3\2"+
		"\2\2\u0330\u0313\3\2\2\2\u0330\u031a\3\2\2\2\u0330\u0323\3\2\2\2\u0331"+
		"o\3\2\2\2\u0332\u0337\5r:\2\u0333\u0334\7\4\2\2\u0334\u0336\5r:\2\u0335"+
		"\u0333\3\2\2\2\u0336\u0339\3\2\2\2\u0337\u0335\3\2\2\2\u0337\u0338\3\2"+
		"\2\2\u0338q\3\2\2\2\u0339\u0337\3\2\2\2\u033a\u033b\5~@\2\u033b\u033e"+
		"\5n8\2\u033c\u033d\7\u0097\2\2\u033d\u033f\7\u0098\2\2\u033e\u033c\3\2"+
		"\2\2\u033e\u033f\3\2\2\2\u033f\u0341\3\2\2\2\u0340\u0342\5\30\r\2\u0341"+
		"\u0340\3\2\2\2\u0341\u0342\3\2\2\2\u0342s\3\2\2\2\u0343\u0348\5v<\2\u0344"+
		"\u0345\7\4\2\2\u0345\u0347\5v<\2\u0346\u0344\3\2\2\2\u0347\u034a\3\2\2"+
		"\2\u0348\u0346\3\2\2\2\u0348\u0349\3\2\2\2\u0349u\3\2\2\2\u034a\u0348"+
		"\3\2\2\2\u034b\u034c\5\u0082B\2\u034c\u034d\7\13\2\2\u034d\u0350\5n8\2"+
		"\u034e\u034f\7\u0097\2\2\u034f\u0351\7\u0098\2\2\u0350\u034e\3\2\2\2\u0350"+
		"\u0351\3\2\2\2\u0351\u0353\3\2\2\2\u0352\u0354\5\30\r\2\u0353\u0352\3"+
		"\2\2\2\u0353\u0354\3\2\2\2\u0354w\3\2\2\2\u0355\u0356\7\u0101\2\2\u0356"+
		"\u0357\5R*\2\u0357\u0358\7\u00e6\2\2\u0358\u0359\5R*\2\u0359y\3\2\2\2"+
		"\u035a\u035f\5|?\2\u035b\u035f\7]\2\2\u035c\u035f\7\u0083\2\2\u035d\u035f"+
		"\7\u00c2\2\2\u035e\u035a\3\2\2\2\u035e\u035b\3\2\2\2\u035e\u035c\3\2\2"+
		"\2\u035e\u035d\3\2\2\2\u035f{\3\2\2\2\u0360\u0365\5\u0082B\2\u0361\u0362"+
		"\7\7\2\2\u0362\u0364\5\u0082B\2\u0363\u0361\3\2\2\2\u0364\u0367\3\2\2"+
		"\2\u0365\u0363\3\2\2\2\u0365\u0366\3\2\2\2\u0366}\3\2\2\2\u0367\u0365"+
		"\3\2\2\2\u0368\u0369\5\u0082B\2\u0369\u036a\5\u0080A\2\u036a\177\3\2\2"+
		"\2\u036b\u036c\7\u010f\2\2\u036c\u036e\5\u0082B\2\u036d\u036b\3\2\2\2"+
		"\u036e\u036f\3\2\2\2\u036f\u036d\3\2\2\2\u036f\u0370\3\2\2\2\u0370\u0373"+
		"\3\2\2\2\u0371\u0373\3\2\2\2\u0372\u036d\3\2\2\2\u0372\u0371\3\2\2\2\u0373"+
		"\u0081\3\2\2\2\u0374\u0378\5\u0084C\2\u0375\u0376\6B\r\2\u0376\u0378\5"+
		"\u008cG\2\u0377\u0374\3\2\2\2\u0377\u0375\3\2\2\2\u0378\u0083\3\2\2\2"+
		"\u0379\u0380\7\u0122\2\2\u037a\u0380\5\u0086D\2\u037b\u037c\6C\16\2\u037c"+
		"\u0380\5\u008aF\2\u037d\u037e\6C\17\2\u037e\u0380\5\u008eH\2\u037f\u0379"+
		"\3\2\2\2\u037f\u037a\3\2\2\2\u037f\u037b\3\2\2\2\u037f\u037d\3\2\2\2\u0380"+
		"\u0085\3\2\2\2\u0381\u0382\7\u0123\2\2\u0382\u0087\3\2\2\2\u0383\u0385"+
		"\6E\20\2\u0384\u0386\7\u010f\2\2\u0385\u0384\3\2\2\2\u0385\u0386\3\2\2"+
		"\2\u0386\u0387\3\2\2\2\u0387\u03af\7\u011d\2\2\u0388\u038a\6E\21\2\u0389"+
		"\u038b\7\u010f\2\2\u038a\u0389\3\2\2\2\u038a\u038b\3\2\2\2\u038b\u038c"+
		"\3\2\2\2\u038c\u03af\7\u011e\2\2\u038d\u038f\6E\22\2\u038e\u0390\7\u010f"+
		"\2\2\u038f\u038e\3\2\2\2\u038f\u0390\3\2\2\2\u0390\u0391\3\2\2\2\u0391"+
		"\u03af\t\21\2\2\u0392\u0394\7\u010f\2\2\u0393\u0392\3\2\2\2\u0393\u0394"+
		"\3\2\2\2\u0394\u0395\3\2\2\2\u0395\u03af\7\u011c\2\2\u0396\u0398\7\u010f"+
		"\2\2\u0397\u0396\3\2\2\2\u0397\u0398\3\2\2\2\u0398\u0399\3\2\2\2\u0399"+
		"\u03af\7\u0119\2\2\u039a\u039c\7\u010f\2\2\u039b\u039a\3\2\2\2\u039b\u039c"+
		"\3\2\2\2\u039c\u039d\3\2\2\2\u039d\u03af\7\u011a\2\2\u039e\u03a0\7\u010f"+
		"\2\2\u039f\u039e\3\2\2\2\u039f\u03a0\3\2\2\2\u03a0\u03a1\3\2\2\2\u03a1"+
		"\u03af\7\u011b\2\2\u03a2\u03a4\7\u010f\2\2\u03a3\u03a2\3\2\2\2\u03a3\u03a4"+
		"\3\2\2\2\u03a4\u03a5\3\2\2\2\u03a5\u03af\7\u0120\2\2\u03a6\u03a8\7\u010f"+
		"\2\2\u03a7\u03a6\3\2\2\2\u03a7\u03a8\3\2\2\2\u03a8\u03a9\3\2\2\2\u03a9"+
		"\u03af\7\u011f\2\2\u03aa\u03ac\7\u010f\2\2\u03ab\u03aa\3\2\2\2\u03ab\u03ac"+
		"\3\2\2\2\u03ac\u03ad\3\2\2\2\u03ad\u03af\7\u0121\2\2\u03ae\u0383\3\2\2"+
		"\2\u03ae\u0388\3\2\2\2\u03ae\u038d\3\2\2\2\u03ae\u0393\3\2\2\2\u03ae\u0397"+
		"\3\2\2\2\u03ae\u039b\3\2\2\2\u03ae\u039f\3\2\2\2\u03ae\u03a3\3\2\2\2\u03ae"+
		"\u03a7\3\2\2\2\u03ae\u03ab\3\2\2\2\u03af\u0089\3\2\2\2\u03b0\u03b1\t\22"+
		"\2\2\u03b1\u008b\3\2\2\2\u03b2\u03b3\t\23\2\2\u03b3\u008d\3\2\2\2\u03b4"+
		"\u03b5\t\24\2\2\u03b5\u008f\3\2\2\2w\u0094\u00bf\u00c3\u00cb\u00d0\u00d7"+
		"\u00de\u00e6\u00eb\u00ef\u00f3\u00f8\u00fc\u00ff\u0102\u0113\u011c\u011f"+
		"\u0124\u012b\u012e\u0134\u013b\u013e\u0142\u0146\u014a\u014c\u015a\u0162"+
		"\u016e\u017a\u017e\u0182\u0184\u018b\u0191\u0198\u019e\u01a2\u01a4\u01ab"+
		"\u01b5\u01b7\u01bf\u01c1\u01c5\u01cd\u01d6\u01dc\u01e1\u01ed\u01f2\u01f5"+
		"\u01fb\u01ff\u0204\u0207\u020d\u0222\u0224\u022c\u0230\u0239\u023d\u024f"+
		"\u0252\u025a\u0263\u026c\u0283\u028e\u0291\u029a\u02a6\u02bf\u02c6\u02c9"+
		"\u02cf\u02d9\u02db\u02e5\u02e7\u02f0\u02f4\u02fb\u0300\u0308\u030c\u031d"+
		"\u0321\u032a\u032e\u0330\u0337\u033e\u0341\u0348\u0350\u0353\u035e\u0365"+
		"\u036f\u0372\u0377\u037f\u0385\u038a\u038f\u0393\u0397\u039b\u039f\u03a3"+
		"\u03a7\u03ab\u03ae";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}