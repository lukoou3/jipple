package com.jipple.sql.catalyst.expressions.codegen;

import java.io.Serializable;
import java.util.Map;

/**
 * A wrapper for the source code to be compiled by {@link CodeGenerator}.
 */
public class CodeAndComment implements Serializable {
    public final String body;
    public final Map<String, String> comment;

    public CodeAndComment(String body, Map<String, String> comment) {
        this.body = body;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CodeAndComment that = (CodeAndComment) obj;
        return body != null ? body.equals(that.body) : that.body == null;
    }

    @Override
    public int hashCode() {
        return body != null ? body.hashCode() : 0;
    }
}
