package com.jipple.sql.errors;

import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;

import java.util.List;
import java.util.Map;

public class QueryCompilationErrors {
    public static AnalysisException unresolvedRoutineError(FunctionIdentifier name, List<String> searchPath) {
        return new AnalysisException("UNRESOLVED_ROUTINE",
                Map.of(
                        "routineName", name.funcName,
                        "searchPath", String.join(", ", searchPath)
                )
        );
    }

    public static AnalysisException funcBuildError(String name, Exception error) {
        String message = "Failed to build function: " + name + ". " + error.getMessage();
        return new AnalysisException(message, null, null, error, null, null, null);
    }

    public static AnalysisException wrongNumArgsError(String name, List<Integer> validParametersCount, int actual) {
        String message = "Wrong number of arguments for function " + name
            + ". Expected one of " + validParametersCount + ", but got " + actual + ".";
        return new AnalysisException(message, null, null, null, null, null, null);
    }

}
