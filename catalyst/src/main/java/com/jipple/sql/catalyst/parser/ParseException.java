package com.jipple.sql.catalyst.parser;

import com.jipple.QueryContext;
import com.jipple.error.JippleThrowableHelper;
import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.trees.CurrentOrigin;
import com.jipple.sql.catalyst.trees.Origin;
import com.jipple.sql.catalyst.util.JippleParserUtils;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link ParseException} is an {@link AnalysisException} that is thrown during the parse process. It
 * contains fields and an extended error message that make reporting and diagnosing errors easier.
 */
public class ParseException extends AnalysisException {
    
    public final String command;
    public final Origin start;
    public final Origin stop;
    private final QueryContext[] queryContext;
    
    // Main constructor
    public ParseException(String command, String message, Origin start, Origin stop,
                          String errorClass, Map<String, String> messageParameters,
                          QueryContext[] queryContext) {
        super(message, start.line, start.startPosition, null, errorClass, messageParameters, queryContext);
        this.command = command;
        this.start = start;
        this.stop = stop;
        this.queryContext = queryContext;
    }

    public ParseException(String command, String message, Origin start, Origin stop) {
        this(command, message, start, stop, null, null, null);
    }

    public ParseException(String errorClass, Map<String, String> messageParameters,
                          ParserRuleContext ctx) {
        this(
            JippleParserUtils.command(ctx),
            JippleThrowableHelper.getMessage(errorClass, messageParameters),
            JippleParserUtils.position(ctx.getStart()),
            JippleParserUtils.position(ctx.getStop()),
            errorClass,
            messageParameters,
            getQueryContextStatic()
        );
    }
    
    public ParseException(String errorClass, ParserRuleContext ctx) {
        this(errorClass, new HashMap<>(), ctx);
    }
    
    /**
     * Compose the message through JippleThrowableHelper given errorClass and messageParameters.
     */
    public ParseException(String command, Origin start, Origin stop,
                          String errorClass, Map<String, String> messageParameters) {
        this(
            command,
            JippleThrowableHelper.getMessage(errorClass, messageParameters),
            start,
            stop,
            errorClass,
            messageParameters,
            getQueryContextStatic()
        );
    }
    
    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append(super.getMessage());
        
        if (start.line != null && start.startPosition != null) {
            int l = start.line;
            int p = start.startPosition;
            builder.append("(line ").append(l).append(", pos ").append(p).append(")\n");
            if (command != null) {
                String[] lines = command.split("\n");
                String[] above = Arrays.copyOf(lines, l);
                String[] below = Arrays.copyOfRange(lines, l, lines.length);
                builder.append("\n== SQL ==\n");
                for (String line : above) {
                    builder.append(line).append("\n");
                }
                for (int i = 0; i < p; i++) {
                    builder.append("-");
                }
                builder.append("^^^\n");
                for (String line : below) {
                    builder.append(line).append("\n");
                }
            }
        } else {
            if (command != null) {
                builder.append("\n== SQL ==\n").append(command);
            }
        }
        return builder.toString();
    }
    
    /**
     * Creates a copy with updated command.
     * 
     * @param cmd the command
     * @return a new ParseException2 with updated command
     */
    public ParseException withCommand(String cmd) {
        String cls;
        Map<String, String> params;
        if ("PARSE_SYNTAX_ERROR".equals(getErrorClass()) && (cmd == null || cmd.trim().isEmpty())) {
            // PARSE_EMPTY_STATEMENT error class overrides the PARSE_SYNTAX_ERROR when cmd is empty
            cls = "PARSE_EMPTY_STATEMENT";
            params = new HashMap<>();
        } else {
            cls = getErrorClass();
            params = getMessageParameters();
        }
        return new ParseException(cmd, super.getMessage(), start, stop, cls, params, queryContext);
    }
    
    @Override
    public QueryContext[] getQueryContext() {
        return queryContext;
    }
    
    /**
     * Gets the query context from CurrentOrigin.
     * 
     * @return the query context array
     */
    private static QueryContext[] getQueryContextStatic() {
        com.jipple.sql.catalyst.trees.SQLQueryContext context = CurrentOrigin.get().context();
        if (context.isValid()) {
            return new QueryContext[]{context};
        } else {
            return new QueryContext[0];
        }
    }
}

