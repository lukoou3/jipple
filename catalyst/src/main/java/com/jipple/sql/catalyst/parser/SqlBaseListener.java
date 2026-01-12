// Generated from D:\IdeaWorkspace\jipple\catalyst\src\main\antlr4\com\jipple\sql\catalyst\parser\SqlBase.g4 by ANTLR 4.7
package com.jipple.sql.catalyst.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SqlBaseParser}.
 */
public interface SqlBaseListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void enterSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void exitSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSingleExpression(SqlBaseParser.SingleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSingleExpression(SqlBaseParser.SingleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleExpressions}.
	 * @param ctx the parse tree
	 */
	void enterSingleExpressions(SqlBaseParser.SingleExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleExpressions}.
	 * @param ctx the parse tree
	 */
	void exitSingleExpressions(SqlBaseParser.SingleExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleOrderBy}.
	 * @param ctx the parse tree
	 */
	void enterSingleOrderBy(SqlBaseParser.SingleOrderByContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleOrderBy}.
	 * @param ctx the parse tree
	 */
	void exitSingleOrderBy(SqlBaseParser.SingleOrderByContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singlePartitionBy}.
	 * @param ctx the parse tree
	 */
	void enterSinglePartitionBy(SqlBaseParser.SinglePartitionByContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singlePartitionBy}.
	 * @param ctx the parse tree
	 */
	void exitSinglePartitionBy(SqlBaseParser.SinglePartitionByContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleTableIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterSingleTableIdentifier(SqlBaseParser.SingleTableIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleTableIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitSingleTableIdentifier(SqlBaseParser.SingleTableIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleMultipartIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterSingleMultipartIdentifier(SqlBaseParser.SingleMultipartIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleMultipartIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitSingleMultipartIdentifier(SqlBaseParser.SingleMultipartIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleFunctionIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterSingleFunctionIdentifier(SqlBaseParser.SingleFunctionIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleFunctionIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitSingleFunctionIdentifier(SqlBaseParser.SingleFunctionIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleDataType}.
	 * @param ctx the parse tree
	 */
	void enterSingleDataType(SqlBaseParser.SingleDataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleDataType}.
	 * @param ctx the parse tree
	 */
	void exitSingleDataType(SqlBaseParser.SingleDataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleTableSchema}.
	 * @param ctx the parse tree
	 */
	void enterSingleTableSchema(SqlBaseParser.SingleTableSchemaContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleTableSchema}.
	 * @param ctx the parse tree
	 */
	void exitSingleTableSchema(SqlBaseParser.SingleTableSchemaContext ctx);
	/**
	 * Enter a parse tree produced by the {@code statementDefault}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatementDefault(SqlBaseParser.StatementDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code statementDefault}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatementDefault(SqlBaseParser.StatementDefaultContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#commentSpec}.
	 * @param ctx the parse tree
	 */
	void enterCommentSpec(SqlBaseParser.CommentSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#commentSpec}.
	 * @param ctx the parse tree
	 */
	void exitCommentSpec(SqlBaseParser.CommentSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQuery(SqlBaseParser.QueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQuery(SqlBaseParser.QueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#queryOrganization}.
	 * @param ctx the parse tree
	 */
	void enterQueryOrganization(SqlBaseParser.QueryOrganizationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#queryOrganization}.
	 * @param ctx the parse tree
	 */
	void exitQueryOrganization(SqlBaseParser.QueryOrganizationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code queryTermDefault}
	 * labeled alternative in {@link SqlBaseParser#queryTerm}.
	 * @param ctx the parse tree
	 */
	void enterQueryTermDefault(SqlBaseParser.QueryTermDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code queryTermDefault}
	 * labeled alternative in {@link SqlBaseParser#queryTerm}.
	 * @param ctx the parse tree
	 */
	void exitQueryTermDefault(SqlBaseParser.QueryTermDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setOperation}
	 * labeled alternative in {@link SqlBaseParser#queryTerm}.
	 * @param ctx the parse tree
	 */
	void enterSetOperation(SqlBaseParser.SetOperationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setOperation}
	 * labeled alternative in {@link SqlBaseParser#queryTerm}.
	 * @param ctx the parse tree
	 */
	void exitSetOperation(SqlBaseParser.SetOperationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code queryPrimaryDefault}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 */
	void enterQueryPrimaryDefault(SqlBaseParser.QueryPrimaryDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code queryPrimaryDefault}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 */
	void exitQueryPrimaryDefault(SqlBaseParser.QueryPrimaryDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code table}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 */
	void enterTable(SqlBaseParser.TableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code table}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 */
	void exitTable(SqlBaseParser.TableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inlineTableDefault1}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 */
	void enterInlineTableDefault1(SqlBaseParser.InlineTableDefault1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code inlineTableDefault1}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 */
	void exitInlineTableDefault1(SqlBaseParser.InlineTableDefault1Context ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#partitionItems}.
	 * @param ctx the parse tree
	 */
	void enterPartitionItems(SqlBaseParser.PartitionItemsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#partitionItems}.
	 * @param ctx the parse tree
	 */
	void exitPartitionItems(SqlBaseParser.PartitionItemsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#sortItems}.
	 * @param ctx the parse tree
	 */
	void enterSortItems(SqlBaseParser.SortItemsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#sortItems}.
	 * @param ctx the parse tree
	 */
	void exitSortItems(SqlBaseParser.SortItemsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#sortItem}.
	 * @param ctx the parse tree
	 */
	void enterSortItem(SqlBaseParser.SortItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#sortItem}.
	 * @param ctx the parse tree
	 */
	void exitSortItem(SqlBaseParser.SortItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code regularQuerySpecification}
	 * labeled alternative in {@link SqlBaseParser#querySpecification}.
	 * @param ctx the parse tree
	 */
	void enterRegularQuerySpecification(SqlBaseParser.RegularQuerySpecificationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code regularQuerySpecification}
	 * labeled alternative in {@link SqlBaseParser#querySpecification}.
	 * @param ctx the parse tree
	 */
	void exitRegularQuerySpecification(SqlBaseParser.RegularQuerySpecificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#selectClause}.
	 * @param ctx the parse tree
	 */
	void enterSelectClause(SqlBaseParser.SelectClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#selectClause}.
	 * @param ctx the parse tree
	 */
	void exitSelectClause(SqlBaseParser.SelectClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void enterWhereClause(SqlBaseParser.WhereClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void exitWhereClause(SqlBaseParser.WhereClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#havingClause}.
	 * @param ctx the parse tree
	 */
	void enterHavingClause(SqlBaseParser.HavingClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#havingClause}.
	 * @param ctx the parse tree
	 */
	void exitHavingClause(SqlBaseParser.HavingClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void enterFromClause(SqlBaseParser.FromClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void exitFromClause(SqlBaseParser.FromClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#lateralView}.
	 * @param ctx the parse tree
	 */
	void enterLateralView(SqlBaseParser.LateralViewContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#lateralView}.
	 * @param ctx the parse tree
	 */
	void exitLateralView(SqlBaseParser.LateralViewContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterRelation(SqlBaseParser.RelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitRelation(SqlBaseParser.RelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#joinRelation}.
	 * @param ctx the parse tree
	 */
	void enterJoinRelation(SqlBaseParser.JoinRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#joinRelation}.
	 * @param ctx the parse tree
	 */
	void exitJoinRelation(SqlBaseParser.JoinRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#joinType}.
	 * @param ctx the parse tree
	 */
	void enterJoinType(SqlBaseParser.JoinTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#joinType}.
	 * @param ctx the parse tree
	 */
	void exitJoinType(SqlBaseParser.JoinTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#joinCriteria}.
	 * @param ctx the parse tree
	 */
	void enterJoinCriteria(SqlBaseParser.JoinCriteriaContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#joinCriteria}.
	 * @param ctx the parse tree
	 */
	void exitJoinCriteria(SqlBaseParser.JoinCriteriaContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tableName}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void enterTableName(SqlBaseParser.TableNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tableName}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void exitTableName(SqlBaseParser.TableNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aliasedQuery}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void enterAliasedQuery(SqlBaseParser.AliasedQueryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aliasedQuery}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void exitAliasedQuery(SqlBaseParser.AliasedQueryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inlineTableDefault2}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void enterInlineTableDefault2(SqlBaseParser.InlineTableDefault2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code inlineTableDefault2}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void exitInlineTableDefault2(SqlBaseParser.InlineTableDefault2Context ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#inlineTable}.
	 * @param ctx the parse tree
	 */
	void enterInlineTable(SqlBaseParser.InlineTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#inlineTable}.
	 * @param ctx the parse tree
	 */
	void exitInlineTable(SqlBaseParser.InlineTableContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#aggregationClause}.
	 * @param ctx the parse tree
	 */
	void enterAggregationClause(SqlBaseParser.AggregationClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#aggregationClause}.
	 * @param ctx the parse tree
	 */
	void exitAggregationClause(SqlBaseParser.AggregationClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierList(SqlBaseParser.IdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierList(SqlBaseParser.IdentifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#identifierSeq}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierSeq(SqlBaseParser.IdentifierSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#identifierSeq}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierSeq(SqlBaseParser.IdentifierSeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#tableAlias}.
	 * @param ctx the parse tree
	 */
	void enterTableAlias(SqlBaseParser.TableAliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#tableAlias}.
	 * @param ctx the parse tree
	 */
	void exitTableAlias(SqlBaseParser.TableAliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#multipartIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterMultipartIdentifier(SqlBaseParser.MultipartIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#multipartIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitMultipartIdentifier(SqlBaseParser.MultipartIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#tableIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterTableIdentifier(SqlBaseParser.TableIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#tableIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitTableIdentifier(SqlBaseParser.TableIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#functionIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterFunctionIdentifier(SqlBaseParser.FunctionIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#functionIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitFunctionIdentifier(SqlBaseParser.FunctionIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#namedExpression}.
	 * @param ctx the parse tree
	 */
	void enterNamedExpression(SqlBaseParser.NamedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#namedExpression}.
	 * @param ctx the parse tree
	 */
	void exitNamedExpression(SqlBaseParser.NamedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#namedExpressionSeq}.
	 * @param ctx the parse tree
	 */
	void enterNamedExpressionSeq(SqlBaseParser.NamedExpressionSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#namedExpressionSeq}.
	 * @param ctx the parse tree
	 */
	void exitNamedExpressionSeq(SqlBaseParser.NamedExpressionSeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(SqlBaseParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(SqlBaseParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalNot}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalNot(SqlBaseParser.LogicalNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalNot}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalNot(SqlBaseParser.LogicalNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code predicated}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void enterPredicated(SqlBaseParser.PredicatedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code predicated}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void exitPredicated(SqlBaseParser.PredicatedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalBinary}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalBinary(SqlBaseParser.LogicalBinaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalBinary}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalBinary(SqlBaseParser.LogicalBinaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(SqlBaseParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(SqlBaseParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueExpressionDefault}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void enterValueExpressionDefault(SqlBaseParser.ValueExpressionDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueExpressionDefault}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void exitValueExpressionDefault(SqlBaseParser.ValueExpressionDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void enterComparison(SqlBaseParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void exitComparison(SqlBaseParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithmeticBinary}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticBinary(SqlBaseParser.ArithmeticBinaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithmeticBinary}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticBinary(SqlBaseParser.ArithmeticBinaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithmeticUnary}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticUnary(SqlBaseParser.ArithmeticUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithmeticUnary}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticUnary(SqlBaseParser.ArithmeticUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code struct}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterStruct(SqlBaseParser.StructContext ctx);
	/**
	 * Exit a parse tree produced by the {@code struct}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitStruct(SqlBaseParser.StructContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dereference}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterDereference(SqlBaseParser.DereferenceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dereference}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitDereference(SqlBaseParser.DereferenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleCase}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterSimpleCase(SqlBaseParser.SimpleCaseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleCase}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitSimpleCase(SqlBaseParser.SimpleCaseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code columnReference}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterColumnReference(SqlBaseParser.ColumnReferenceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code columnReference}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitColumnReference(SqlBaseParser.ColumnReferenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rowConstructor}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterRowConstructor(SqlBaseParser.RowConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rowConstructor}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitRowConstructor(SqlBaseParser.RowConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code last}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterLast(SqlBaseParser.LastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code last}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitLast(SqlBaseParser.LastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code star}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterStar(SqlBaseParser.StarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code star}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitStar(SqlBaseParser.StarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code subscript}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterSubscript(SqlBaseParser.SubscriptContext ctx);
	/**
	 * Exit a parse tree produced by the {@code subscript}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitSubscript(SqlBaseParser.SubscriptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code substring}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterSubstring(SqlBaseParser.SubstringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code substring}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitSubstring(SqlBaseParser.SubstringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code cast}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterCast(SqlBaseParser.CastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code cast}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitCast(SqlBaseParser.CastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constantDefault}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantDefault(SqlBaseParser.ConstantDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constantDefault}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantDefault(SqlBaseParser.ConstantDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lambda}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterLambda(SqlBaseParser.LambdaContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lambda}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitLambda(SqlBaseParser.LambdaContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenthesizedExpression}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedExpression(SqlBaseParser.ParenthesizedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenthesizedExpression}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedExpression(SqlBaseParser.ParenthesizedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code extract}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterExtract(SqlBaseParser.ExtractContext ctx);
	/**
	 * Exit a parse tree produced by the {@code extract}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitExtract(SqlBaseParser.ExtractContext ctx);
	/**
	 * Enter a parse tree produced by the {@code any_value}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterAny_value(SqlBaseParser.Any_valueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code any_value}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitAny_value(SqlBaseParser.Any_valueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code trim}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterTrim(SqlBaseParser.TrimContext ctx);
	/**
	 * Exit a parse tree produced by the {@code trim}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitTrim(SqlBaseParser.TrimContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(SqlBaseParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(SqlBaseParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code searchedCase}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterSearchedCase(SqlBaseParser.SearchedCaseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code searchedCase}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitSearchedCase(SqlBaseParser.SearchedCaseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code position}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPosition(SqlBaseParser.PositionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code position}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPosition(SqlBaseParser.PositionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code first}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterFirst(SqlBaseParser.FirstContext ctx);
	/**
	 * Exit a parse tree produced by the {@code first}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitFirst(SqlBaseParser.FirstContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterNullLiteral(SqlBaseParser.NullLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitNullLiteral(SqlBaseParser.NullLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intervalLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterIntervalLiteral(SqlBaseParser.IntervalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intervalLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitIntervalLiteral(SqlBaseParser.IntervalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numericLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterNumericLiteral(SqlBaseParser.NumericLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numericLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitNumericLiteral(SqlBaseParser.NumericLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(SqlBaseParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(SqlBaseParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(SqlBaseParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(SqlBaseParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(SqlBaseParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(SqlBaseParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#booleanValue}.
	 * @param ctx the parse tree
	 */
	void enterBooleanValue(SqlBaseParser.BooleanValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#booleanValue}.
	 * @param ctx the parse tree
	 */
	void exitBooleanValue(SqlBaseParser.BooleanValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#interval}.
	 * @param ctx the parse tree
	 */
	void enterInterval(SqlBaseParser.IntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#interval}.
	 * @param ctx the parse tree
	 */
	void exitInterval(SqlBaseParser.IntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#errorCapturingMultiUnitsInterval}.
	 * @param ctx the parse tree
	 */
	void enterErrorCapturingMultiUnitsInterval(SqlBaseParser.ErrorCapturingMultiUnitsIntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#errorCapturingMultiUnitsInterval}.
	 * @param ctx the parse tree
	 */
	void exitErrorCapturingMultiUnitsInterval(SqlBaseParser.ErrorCapturingMultiUnitsIntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#multiUnitsInterval}.
	 * @param ctx the parse tree
	 */
	void enterMultiUnitsInterval(SqlBaseParser.MultiUnitsIntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#multiUnitsInterval}.
	 * @param ctx the parse tree
	 */
	void exitMultiUnitsInterval(SqlBaseParser.MultiUnitsIntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#errorCapturingUnitToUnitInterval}.
	 * @param ctx the parse tree
	 */
	void enterErrorCapturingUnitToUnitInterval(SqlBaseParser.ErrorCapturingUnitToUnitIntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#errorCapturingUnitToUnitInterval}.
	 * @param ctx the parse tree
	 */
	void exitErrorCapturingUnitToUnitInterval(SqlBaseParser.ErrorCapturingUnitToUnitIntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#unitToUnitInterval}.
	 * @param ctx the parse tree
	 */
	void enterUnitToUnitInterval(SqlBaseParser.UnitToUnitIntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#unitToUnitInterval}.
	 * @param ctx the parse tree
	 */
	void exitUnitToUnitInterval(SqlBaseParser.UnitToUnitIntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#intervalValue}.
	 * @param ctx the parse tree
	 */
	void enterIntervalValue(SqlBaseParser.IntervalValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#intervalValue}.
	 * @param ctx the parse tree
	 */
	void exitIntervalValue(SqlBaseParser.IntervalValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code complexDataType}
	 * labeled alternative in {@link SqlBaseParser#dataType}.
	 * @param ctx the parse tree
	 */
	void enterComplexDataType(SqlBaseParser.ComplexDataTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code complexDataType}
	 * labeled alternative in {@link SqlBaseParser#dataType}.
	 * @param ctx the parse tree
	 */
	void exitComplexDataType(SqlBaseParser.ComplexDataTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code primitiveDataType}
	 * labeled alternative in {@link SqlBaseParser#dataType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveDataType(SqlBaseParser.PrimitiveDataTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code primitiveDataType}
	 * labeled alternative in {@link SqlBaseParser#dataType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveDataType(SqlBaseParser.PrimitiveDataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#colTypeList}.
	 * @param ctx the parse tree
	 */
	void enterColTypeList(SqlBaseParser.ColTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#colTypeList}.
	 * @param ctx the parse tree
	 */
	void exitColTypeList(SqlBaseParser.ColTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#colType}.
	 * @param ctx the parse tree
	 */
	void enterColType(SqlBaseParser.ColTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#colType}.
	 * @param ctx the parse tree
	 */
	void exitColType(SqlBaseParser.ColTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#complexColTypeList}.
	 * @param ctx the parse tree
	 */
	void enterComplexColTypeList(SqlBaseParser.ComplexColTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#complexColTypeList}.
	 * @param ctx the parse tree
	 */
	void exitComplexColTypeList(SqlBaseParser.ComplexColTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#complexColType}.
	 * @param ctx the parse tree
	 */
	void enterComplexColType(SqlBaseParser.ComplexColTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#complexColType}.
	 * @param ctx the parse tree
	 */
	void exitComplexColType(SqlBaseParser.ComplexColTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#whenClause}.
	 * @param ctx the parse tree
	 */
	void enterWhenClause(SqlBaseParser.WhenClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#whenClause}.
	 * @param ctx the parse tree
	 */
	void exitWhenClause(SqlBaseParser.WhenClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(SqlBaseParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(SqlBaseParser.FunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(SqlBaseParser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(SqlBaseParser.QualifiedNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#errorCapturingIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterErrorCapturingIdentifier(SqlBaseParser.ErrorCapturingIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#errorCapturingIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitErrorCapturingIdentifier(SqlBaseParser.ErrorCapturingIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code errorIdent}
	 * labeled alternative in {@link SqlBaseParser#errorCapturingIdentifierExtra}.
	 * @param ctx the parse tree
	 */
	void enterErrorIdent(SqlBaseParser.ErrorIdentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code errorIdent}
	 * labeled alternative in {@link SqlBaseParser#errorCapturingIdentifierExtra}.
	 * @param ctx the parse tree
	 */
	void exitErrorIdent(SqlBaseParser.ErrorIdentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code realIdent}
	 * labeled alternative in {@link SqlBaseParser#errorCapturingIdentifierExtra}.
	 * @param ctx the parse tree
	 */
	void enterRealIdent(SqlBaseParser.RealIdentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code realIdent}
	 * labeled alternative in {@link SqlBaseParser#errorCapturingIdentifierExtra}.
	 * @param ctx the parse tree
	 */
	void exitRealIdent(SqlBaseParser.RealIdentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(SqlBaseParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(SqlBaseParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unquotedIdentifier}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterUnquotedIdentifier(SqlBaseParser.UnquotedIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unquotedIdentifier}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitUnquotedIdentifier(SqlBaseParser.UnquotedIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code quotedIdentifierAlternative}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterQuotedIdentifierAlternative(SqlBaseParser.QuotedIdentifierAlternativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code quotedIdentifierAlternative}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitQuotedIdentifierAlternative(SqlBaseParser.QuotedIdentifierAlternativeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#quotedIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterQuotedIdentifier(SqlBaseParser.QuotedIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#quotedIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitQuotedIdentifier(SqlBaseParser.QuotedIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exponentLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterExponentLiteral(SqlBaseParser.ExponentLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exponentLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitExponentLiteral(SqlBaseParser.ExponentLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterDecimalLiteral(SqlBaseParser.DecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitDecimalLiteral(SqlBaseParser.DecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code legacyDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterLegacyDecimalLiteral(SqlBaseParser.LegacyDecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code legacyDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitLegacyDecimalLiteral(SqlBaseParser.LegacyDecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterIntegerLiteral(SqlBaseParser.IntegerLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitIntegerLiteral(SqlBaseParser.IntegerLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bigIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterBigIntLiteral(SqlBaseParser.BigIntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bigIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitBigIntLiteral(SqlBaseParser.BigIntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code smallIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterSmallIntLiteral(SqlBaseParser.SmallIntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code smallIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitSmallIntLiteral(SqlBaseParser.SmallIntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tinyIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterTinyIntLiteral(SqlBaseParser.TinyIntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tinyIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitTinyIntLiteral(SqlBaseParser.TinyIntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code doubleLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterDoubleLiteral(SqlBaseParser.DoubleLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code doubleLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitDoubleLiteral(SqlBaseParser.DoubleLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(SqlBaseParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(SqlBaseParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bigDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterBigDecimalLiteral(SqlBaseParser.BigDecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bigDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitBigDecimalLiteral(SqlBaseParser.BigDecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#ansiNonReserved}.
	 * @param ctx the parse tree
	 */
	void enterAnsiNonReserved(SqlBaseParser.AnsiNonReservedContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#ansiNonReserved}.
	 * @param ctx the parse tree
	 */
	void exitAnsiNonReserved(SqlBaseParser.AnsiNonReservedContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#strictNonReserved}.
	 * @param ctx the parse tree
	 */
	void enterStrictNonReserved(SqlBaseParser.StrictNonReservedContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#strictNonReserved}.
	 * @param ctx the parse tree
	 */
	void exitStrictNonReserved(SqlBaseParser.StrictNonReservedContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#nonReserved}.
	 * @param ctx the parse tree
	 */
	void enterNonReserved(SqlBaseParser.NonReservedContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#nonReserved}.
	 * @param ctx the parse tree
	 */
	void exitNonReserved(SqlBaseParser.NonReservedContext ctx);
}