package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;
import java.util.stream.Collectors;

import static com.jipple.sql.catalyst.expressions.BindReferences.bindReferences;

/**
 * A [[Projection]] that is calculated by calling the `eval` of each of the specified expressions.
 */
public class InterpretedProjection extends Projection {
    private final List<Expression> exprs;

    public InterpretedProjection(List<Expression> expressions, List<Attribute> inputSchema) {
        this(bindReferences(expressions, new AttributeSeq(inputSchema)));
    }

    /**
     * A [[Projection]] that is calculated by calling the `eval` of each of the specified expressions.
     * @param expressions a sequence of expressions that determine the value of each column of the
     *                    output row.
     */
    public InterpretedProjection(List<Expression> expressions) {
        this.exprs = expressions;
    }

    @Override
    public void open(int partitions, int partitionIndex) throws Exception {
        openExprs(exprs, partitions, partitionIndex);
    }

    @Override
    public Object apply(InternalRow input) {
        Object[] outputArray = new Object[exprs.size()];
        for (int i = 0; i < outputArray.length; i++) {
            outputArray[i] = exprs.get(i).eval(input);
        }
        return new GenericInternalRow(outputArray);
    }

    @Override
    public void close() throws Exception {
        closeExprs(exprs);
    }

    @Override
    public String toString() {
        return String.format("Row => [%s]", exprs.stream().map(Expression::toString).collect(Collectors.joining(",")));
    }
}
