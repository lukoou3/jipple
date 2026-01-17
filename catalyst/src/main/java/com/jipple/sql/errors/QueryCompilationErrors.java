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


}
