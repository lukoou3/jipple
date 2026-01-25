package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.InternalRowWriter;
import com.jipple.tuple.Tuple2;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An interpreted version of a safe projection.
 */
public class InterpretedSafeProjection extends Projection {
    private final List<Expression> exprs;
    private final SpecificInternalRow mutableRow;
    private final List<Tuple2<Expression, InternalRowWriter>> exprsWithWriters;
    /**
     * @param expressions that produces the resulting fields. These expressions must be bound
     *                    to a schema.
     */
    public InterpretedSafeProjection(List<Expression> expressions) {
        this.exprs = expressions;
        this.mutableRow = new SpecificInternalRow(this.exprs.stream().map(Expression::dataType).collect(Collectors.toList()));
        this.exprsWithWriters = IntStream.range(0, expressions.size()).filter(i -> !(exprs.get(i) instanceof NoOp)).mapToObj(i -> {
            Expression e = exprs.get(i);
            InternalRowWriter writer = InternalRow.getWriterFast(i, e.dataType());
            InternalRowWriter f;
            if (!e.nullable()) {
                f = writer;
            } else {
                f = (input, v) -> {
                    if (v == null) {
                        input.setNullAt(i);
                    } else {
                        writer.write(input, v);
                    }
                };
            }
            return Tuple2.of(e, f);
        }).collect(Collectors.toList());
    }

    @Override
    public void open(int partitions, int partitionIndex) throws Exception {
        openExprs(exprs, partitions, partitionIndex);
    }

    @Override
    public Object apply(InternalRow row) {
        Tuple2<Expression, InternalRowWriter> exprWithWriter;
        Object v;
        for (int i = 0; i < exprsWithWriters.size(); i++) {
            exprWithWriter = exprsWithWriters.get(i);
            v = exprWithWriter._1.eval(row);
            exprWithWriter._2.write(mutableRow, v);
        }
        return mutableRow;
    }

    @Override
    public void close() throws Exception {
        closeExprs(exprs);
    }
}
