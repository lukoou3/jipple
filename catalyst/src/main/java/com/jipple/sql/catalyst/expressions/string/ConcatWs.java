package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.TrueLiteral;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.errors.QueryCompilationErrors;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.ArrayType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StringType;
import com.jipple.sql.types.TypeCollection;
import com.jipple.tuple.Tuple2;
import com.jipple.unsafe.types.UTF8String;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConcatWs extends Expression {
    private final List<Expression> children;

    public ConcatWs(List<Expression> children) {
        this.children = children;
    }

    @Override
    public Object[] args() {
        return new Object[]{children};
    }

    @Override
    public List<Expression> children() {
        return children;
    }

    @Override
    public String prettyName() {
        return "concat_ws";
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        AbstractDataType arrayOrStr = TypeCollection.of(new ArrayType(StringType.INSTANCE), StringType.INSTANCE);
        List<AbstractDataType> types = new ArrayList<>(children.size());
        types.add(StringType.INSTANCE);
        for (int i = 1; i < children.size(); i++) {
            types.add(arrayOrStr);
        }
        return Option.some(types);
    }

    @Override
    public DataType dataType() {
        return StringType.INSTANCE;
    }

    @Override
    public boolean nullable() {
        return children.get(0).nullable();
    }

    @Override
    public boolean foldable() {
        return children.stream().allMatch(Expression::foldable);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (children.isEmpty()) {
            throw QueryCompilationErrors.wrongNumArgsError(prettyName(), List.of(1), children.size());
        }
        return super.checkInputDataTypes();
    }

    @Override
    public Object eval(InternalRow input) {
        List<UTF8String> flatInputs = new ArrayList<>();
        for (Expression child : children) {
            Object value = child.eval(input);
            if (value == null) {
                flatInputs.add(null);
            } else if (value instanceof UTF8String str) {
                flatInputs.add(str);
            } else if (value instanceof ArrayData arr) {
                UTF8String[] values = arr.toArray(StringType.INSTANCE, UTF8String[]::new);
                for (UTF8String s : values) {
                    flatInputs.add(s);
                }
            }
        }
        UTF8String separator = flatInputs.get(0);
        UTF8String[] inputs = flatInputs.subList(1, flatInputs.size()).toArray(new UTF8String[0]);
        return UTF8String.concatWs(separator, inputs);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        boolean allStrings = children.stream().allMatch(child -> child.dataType() instanceof StringType);
        if (allStrings) {
            List<ExprCode> evals = children.stream().map(child -> child.genCode(ctx)).toList();
            ExprCode separator = evals.get(0);
            List<ExprCode> strings = evals.subList(1, evals.size());
            int numArgs = strings.size();
            String args = ctx.freshName("args");
            List<String> inputs = new ArrayList<>(strings.size());
            for (int i = 0; i < strings.size(); i++) {
                ExprCode eval = strings.get(i);
                if (eval.isNull instanceof TrueLiteral) {
                    continue;
                }
                inputs.add(CodeGeneratorUtils.template(
                        """
                                ${evalCode}
                                if (!${isNull}) {
                                  ${args}[${index}] = ${value};
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("evalCode", eval.code),
                                Map.entry("isNull", eval.isNull),
                                Map.entry("args", args),
                                Map.entry("index", i),
                                Map.entry("value", eval.value)
                        )
                ));
            }
            String codes = ctx.splitExpressionsWithCurrentInputs(
                    inputs,
                    "valueConcatWs",
                    List.of(Tuple2.of("UTF8String[]", args))
            );
            return ev.copy(Block.block(
                    """
                            UTF8String[] ${args} = new UTF8String[${numArgs}];
                            ${separatorCode}
                            ${codes}
                            UTF8String ${value} = UTF8String.concatWs(${separatorValue}, ${args});
                            boolean ${isNull} = ${value} == null;
                            """,
                    Map.ofEntries(
                            Map.entry("args", args),
                            Map.entry("numArgs", numArgs),
                            Map.entry("separatorCode", separator.code),
                            Map.entry("codes", codes),
                            Map.entry("value", ev.value),
                            Map.entry("separatorValue", separator.value),
                            Map.entry("isNull", ev.isNull)
                    )
            ));
        }

        String isNullArgs = ctx.freshName("isNullArgs");
        String valueArgs = ctx.freshName("valueArgs");
        String array = ctx.freshName("array");
        String varargNum = ctx.freshName("varargNum");
        String idxVararg = ctx.freshName("idxInVararg");

        List<ExprCode> evals = children.stream().map(child -> child.genCode(ctx)).toList();
        List<String> argBuild = new ArrayList<>(children.size() - 1);
        List<String> varargCount = new ArrayList<>(children.size() - 1);
        List<String> varargBuild = new ArrayList<>(children.size() - 1);

        for (int idx = 0; idx < children.size() - 1; idx++) {
            Expression child = children.get(idx + 1);
            ExprCode eval = evals.get(idx + 1);
            String reprForIsNull = isNullArgs + "[" + idx + "]";
            String reprForValue = valueArgs + "[" + idx + "]";

            argBuild.add(CodeGeneratorUtils.template(
                    """
                            ${evalCode}
                            ${reprForIsNull} = ${evalIsNull};
                            ${reprForValue} = ${evalValue};
                            """,
                    Map.ofEntries(
                            Map.entry("evalCode", eval.code),
                            Map.entry("reprForIsNull", reprForIsNull),
                            Map.entry("evalIsNull", eval.isNull),
                            Map.entry("reprForValue", reprForValue),
                            Map.entry("evalValue", eval.value)
                    )
            ));

            if (child.dataType() instanceof StringType) {
                if (!(eval.isNull instanceof TrueLiteral)) {
                    String reprForValueCast = "((UTF8String) " + reprForValue + ")";
                    varargBuild.add(CodeGeneratorUtils.template(
                            "${array}[${idxVararg}++] = ${reprForIsNull} ? (UTF8String) null : ${reprForValueCast};",
                            Map.ofEntries(
                                    Map.entry("array", array),
                                    Map.entry("idxVararg", idxVararg),
                                    Map.entry("reprForIsNull", reprForIsNull),
                                    Map.entry("reprForValueCast", reprForValueCast)
                            )
                    ));
                }
                varargCount.add("");
            } else if (child.dataType() instanceof ArrayType) {
                if (!(eval.isNull instanceof TrueLiteral)) {
                    String reprForValueCast = "((ArrayData) " + reprForValue + ")";
                    String size = ctx.freshName("n");
                    varargCount.add(CodeGeneratorUtils.template(
                            """
                                    if (!${reprForIsNull}) {
                                      ${varargNum} += ${reprForValueCast}.numElements();
                                    }
                                    """,
                            Map.ofEntries(
                                    Map.entry("reprForIsNull", reprForIsNull),
                                    Map.entry("varargNum", varargNum),
                                    Map.entry("reprForValueCast", reprForValueCast)
                            )
                    ));
                    varargBuild.add(CodeGeneratorUtils.template(
                            """
                                    if (!${reprForIsNull}) {
                                      final int ${size} = ${reprForValueCast}.numElements();
                                      for (int j = 0; j < ${size}; j ++) {
                                        ${array}[${idxVararg}++] = ${getValue};
                                      }
                                    }
                                    """,
                            Map.ofEntries(
                                    Map.entry("reprForIsNull", reprForIsNull),
                                    Map.entry("size", size),
                                    Map.entry("reprForValueCast", reprForValueCast),
                                    Map.entry("array", array),
                                    Map.entry("idxVararg", idxVararg),
                                    Map.entry("getValue", CodeGeneratorUtils.getValue(reprForValueCast, StringType.INSTANCE, "j"))
                            )
                    ));
                } else {
                    varargCount.add("");
                    varargBuild.add("");
                }
            } else {
                varargCount.add("");
                varargBuild.add("");
            }
        }

        String argBuilds = ctx.splitExpressionsWithCurrentInputs(
                argBuild,
                "initializeArgsArrays",
                List.of(
                        Tuple2.of("boolean []", isNullArgs),
                        Tuple2.of("Object []", valueArgs)
                )
        );

        String varargCounts = ctx.splitExpressionsWithCurrentInputs(
                varargCount,
                "varargCountsConcatWs",
                List.of(
                        Tuple2.of("boolean []", isNullArgs),
                        Tuple2.of("Object []", valueArgs)
                ),
                "int",
                body -> CodeGeneratorUtils.template(
                        """
                                int ${varargNum} = 0;
                                ${body}
                                return ${varargNum};
                                """,
                        Map.of(
                                "varargNum", varargNum,
                                "body", body
                        )
                ),
                funcs -> funcs.stream()
                        .map(funcCall -> varargNum + " += " + funcCall + ";")
                        .collect(java.util.stream.Collectors.joining("\n"))
        );

        String varargBuilds = ctx.splitExpressionsWithCurrentInputs(
                varargBuild,
                "varargBuildsConcatWs",
                List.of(
                        Tuple2.of("UTF8String []", array),
                        Tuple2.of("int", idxVararg),
                        Tuple2.of("boolean []", isNullArgs),
                        Tuple2.of("Object []", valueArgs)
                ),
                "int",
                body -> CodeGeneratorUtils.template(
                        """
                                ${body}
                                return ${idxVararg};
                                """,
                        Map.of(
                                "body", body,
                                "idxVararg", idxVararg
                        )
                ),
                funcs -> funcs.stream()
                        .map(funcCall -> idxVararg + " = " + funcCall + ";")
                        .collect(java.util.stream.Collectors.joining("\n"))
        );

        int stringArgs = (int) children.stream().filter(child -> child.dataType() instanceof StringType).count();
        int initialVarargNum = stringArgs - 1;

        return ev.copy(Block.block(
                """
                        boolean[] ${isNullArgs} = new boolean[${len}];
                        Object[] ${valueArgs} = new Object[${len}];
                        ${argBuilds}
                        int ${varargNum} = ${initialVarargNum};
                        int ${idxVararg} = 0;
                        ${varargCounts}
                        UTF8String[] ${array} = new UTF8String[${varargNum}];
                        ${varargBuilds}
                        ${separatorCode}
                        UTF8String ${value} = UTF8String.concatWs(${separatorValue}, ${array});
                        boolean ${isNull} = ${value} == null;
                        """,
                Map.ofEntries(
                        Map.entry("isNullArgs", isNullArgs),
                        Map.entry("valueArgs", valueArgs),
                        Map.entry("len", children.size() - 1),
                        Map.entry("argBuilds", argBuilds),
                        Map.entry("varargNum", varargNum),
                        Map.entry("initialVarargNum", initialVarargNum),
                        Map.entry("idxVararg", idxVararg),
                        Map.entry("varargCounts", varargCounts),
                        Map.entry("array", array),
                        Map.entry("varargBuilds", varargBuilds),
                        Map.entry("separatorCode", evals.get(0).code),
                        Map.entry("value", ev.value),
                        Map.entry("separatorValue", evals.get(0).value),
                        Map.entry("isNull", ev.isNull)
                )
        ));
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new ConcatWs(newChildren);
    }
}
