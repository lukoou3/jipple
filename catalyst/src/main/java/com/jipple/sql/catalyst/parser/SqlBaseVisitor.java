// Generated from D:\IdeaWorkspace\jipple\catalyst\src\main\antlr4\com\jipple\sql\catalyst\parser\SqlBase.g4 by ANTLR 4.7
package com.jipple.sql.catalyst.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SqlBaseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SqlBaseVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleExpression(SqlBaseParser.SingleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleExpressions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleExpressions(SqlBaseParser.SingleExpressionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleOrderBy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleOrderBy(SqlBaseParser.SingleOrderByContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singlePartitionBy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSinglePartitionBy(SqlBaseParser.SinglePartitionByContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleTableIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleTableIdentifier(SqlBaseParser.SingleTableIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleMultipartIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleMultipartIdentifier(SqlBaseParser.SingleMultipartIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleFunctionIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleFunctionIdentifier(SqlBaseParser.SingleFunctionIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleDataType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleDataType(SqlBaseParser.SingleDataTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleTableSchema}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleTableSchema(SqlBaseParser.SingleTableSchemaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statementDefault}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementDefault(SqlBaseParser.StatementDefaultContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#commentSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommentSpec(SqlBaseParser.CommentSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery(SqlBaseParser.QueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#queryOrganization}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueryOrganization(SqlBaseParser.QueryOrganizationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code queryTermDefault}
	 * labeled alternative in {@link SqlBaseParser#queryTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueryTermDefault(SqlBaseParser.QueryTermDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setOperation}
	 * labeled alternative in {@link SqlBaseParser#queryTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetOperation(SqlBaseParser.SetOperationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code queryPrimaryDefault}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueryPrimaryDefault(SqlBaseParser.QueryPrimaryDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code table}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable(SqlBaseParser.TableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inlineTableDefault1}
	 * labeled alternative in {@link SqlBaseParser#queryPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInlineTableDefault1(SqlBaseParser.InlineTableDefault1Context ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#partitionItems}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPartitionItems(SqlBaseParser.PartitionItemsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#sortItems}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSortItems(SqlBaseParser.SortItemsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#sortItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSortItem(SqlBaseParser.SortItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code regularQuerySpecification}
	 * labeled alternative in {@link SqlBaseParser#querySpecification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegularQuerySpecification(SqlBaseParser.RegularQuerySpecificationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#selectClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectClause(SqlBaseParser.SelectClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#whereClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereClause(SqlBaseParser.WhereClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#havingClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHavingClause(SqlBaseParser.HavingClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#fromClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFromClause(SqlBaseParser.FromClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#lateralView}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLateralView(SqlBaseParser.LateralViewContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelation(SqlBaseParser.RelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#joinRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinRelation(SqlBaseParser.JoinRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#joinType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinType(SqlBaseParser.JoinTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#joinCriteria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinCriteria(SqlBaseParser.JoinCriteriaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tableName}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableName(SqlBaseParser.TableNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aliasedQuery}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAliasedQuery(SqlBaseParser.AliasedQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inlineTableDefault2}
	 * labeled alternative in {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInlineTableDefault2(SqlBaseParser.InlineTableDefault2Context ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#inlineTable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInlineTable(SqlBaseParser.InlineTableContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#aggregationClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregationClause(SqlBaseParser.AggregationClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#identifierList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierList(SqlBaseParser.IdentifierListContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#identifierSeq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierSeq(SqlBaseParser.IdentifierSeqContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#tableAlias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableAlias(SqlBaseParser.TableAliasContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#multipartIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultipartIdentifier(SqlBaseParser.MultipartIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#tableIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableIdentifier(SqlBaseParser.TableIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#functionIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionIdentifier(SqlBaseParser.FunctionIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#namedExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedExpression(SqlBaseParser.NamedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#namedExpressionSeq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedExpressionSeq(SqlBaseParser.NamedExpressionSeqContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(SqlBaseParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalNot}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalNot(SqlBaseParser.LogicalNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predicated}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicated(SqlBaseParser.PredicatedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalBinary}
	 * labeled alternative in {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalBinary(SqlBaseParser.LogicalBinaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicate(SqlBaseParser.PredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueExpressionDefault}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueExpressionDefault(SqlBaseParser.ValueExpressionDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(SqlBaseParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithmeticBinary}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticBinary(SqlBaseParser.ArithmeticBinaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithmeticUnary}
	 * labeled alternative in {@link SqlBaseParser#valueExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticUnary(SqlBaseParser.ArithmeticUnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code struct}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct(SqlBaseParser.StructContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dereference}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDereference(SqlBaseParser.DereferenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleCase}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleCase(SqlBaseParser.SimpleCaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code columnReference}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnReference(SqlBaseParser.ColumnReferenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rowConstructor}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRowConstructor(SqlBaseParser.RowConstructorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code last}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLast(SqlBaseParser.LastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code star}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStar(SqlBaseParser.StarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code subscript}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscript(SqlBaseParser.SubscriptContext ctx);
	/**
	 * Visit a parse tree produced by the {@code substring}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstring(SqlBaseParser.SubstringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cast}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCast(SqlBaseParser.CastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constantDefault}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantDefault(SqlBaseParser.ConstantDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lambda}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambda(SqlBaseParser.LambdaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesizedExpression}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedExpression(SqlBaseParser.ParenthesizedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code extract}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtract(SqlBaseParser.ExtractContext ctx);
	/**
	 * Visit a parse tree produced by the {@code any_value}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_value(SqlBaseParser.Any_valueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code trim}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrim(SqlBaseParser.TrimContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(SqlBaseParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code searchedCase}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearchedCase(SqlBaseParser.SearchedCaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code position}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPosition(SqlBaseParser.PositionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code first}
	 * labeled alternative in {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFirst(SqlBaseParser.FirstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullLiteral(SqlBaseParser.NullLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intervalLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntervalLiteral(SqlBaseParser.IntervalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericLiteral(SqlBaseParser.NumericLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(SqlBaseParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(SqlBaseParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(SqlBaseParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#booleanValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanValue(SqlBaseParser.BooleanValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#interval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterval(SqlBaseParser.IntervalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#errorCapturingMultiUnitsInterval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitErrorCapturingMultiUnitsInterval(SqlBaseParser.ErrorCapturingMultiUnitsIntervalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#multiUnitsInterval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiUnitsInterval(SqlBaseParser.MultiUnitsIntervalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#errorCapturingUnitToUnitInterval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitErrorCapturingUnitToUnitInterval(SqlBaseParser.ErrorCapturingUnitToUnitIntervalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#unitToUnitInterval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnitToUnitInterval(SqlBaseParser.UnitToUnitIntervalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#intervalValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntervalValue(SqlBaseParser.IntervalValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code complexDataType}
	 * labeled alternative in {@link SqlBaseParser#dataType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexDataType(SqlBaseParser.ComplexDataTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code primitiveDataType}
	 * labeled alternative in {@link SqlBaseParser#dataType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveDataType(SqlBaseParser.PrimitiveDataTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#colTypeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColTypeList(SqlBaseParser.ColTypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#colType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColType(SqlBaseParser.ColTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#complexColTypeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexColTypeList(SqlBaseParser.ComplexColTypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#complexColType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexColType(SqlBaseParser.ComplexColTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#whenClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenClause(SqlBaseParser.WhenClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#functionName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionName(SqlBaseParser.FunctionNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#qualifiedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedName(SqlBaseParser.QualifiedNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#errorCapturingIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitErrorCapturingIdentifier(SqlBaseParser.ErrorCapturingIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code errorIdent}
	 * labeled alternative in {@link SqlBaseParser#errorCapturingIdentifierExtra}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitErrorIdent(SqlBaseParser.ErrorIdentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code realIdent}
	 * labeled alternative in {@link SqlBaseParser#errorCapturingIdentifierExtra}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealIdent(SqlBaseParser.RealIdentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(SqlBaseParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unquotedIdentifier}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnquotedIdentifier(SqlBaseParser.UnquotedIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code quotedIdentifierAlternative}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuotedIdentifierAlternative(SqlBaseParser.QuotedIdentifierAlternativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#quotedIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuotedIdentifier(SqlBaseParser.QuotedIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exponentLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExponentLiteral(SqlBaseParser.ExponentLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalLiteral(SqlBaseParser.DecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code legacyDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLegacyDecimalLiteral(SqlBaseParser.LegacyDecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteral(SqlBaseParser.IntegerLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bigIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBigIntLiteral(SqlBaseParser.BigIntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code smallIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSmallIntLiteral(SqlBaseParser.SmallIntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tinyIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTinyIntLiteral(SqlBaseParser.TinyIntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code doubleLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoubleLiteral(SqlBaseParser.DoubleLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(SqlBaseParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bigDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBigDecimalLiteral(SqlBaseParser.BigDecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#ansiNonReserved}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnsiNonReserved(SqlBaseParser.AnsiNonReservedContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#strictNonReserved}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrictNonReserved(SqlBaseParser.StrictNonReservedContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#nonReserved}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonReserved(SqlBaseParser.NonReservedContext ctx);
}