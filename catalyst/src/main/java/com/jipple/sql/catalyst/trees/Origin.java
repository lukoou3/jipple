package com.jipple.sql.catalyst.trees;

import com.jipple.QueryContext;

/**
 * Contexts of TreeNodes, including location, SQL text, object type and object name.
 * The only supported object type is "VIEW" now. In the future, we may support SQL UDF or other
 * objects which contain SQL text.
 */
public class Origin {
    
    public final Integer line;
    public final Integer startPosition;
    public final Integer startIndex;
    public final Integer stopIndex;
    public final String sqlText;
    public final String objectType;
    public final String objectName;

    private SQLQueryContext _context;
    
    public Origin() {
        this(null, null, null, null, null, null, null);
    }

    public Origin(Integer line, Integer startPosition) {
        this(line, startPosition, null, null, null, null, null);
    }
    
    public Origin(Integer line, Integer startPosition, Integer startIndex,
                  Integer stopIndex, String sqlText, String objectType, String objectName) {
        this.line = line;
        this.startPosition = startPosition;
        this.startIndex = startIndex;
        this.stopIndex = stopIndex;
        this.sqlText = sqlText;
        this.objectType = objectType;
        this.objectName = objectName;
    }
    
    /**
     * Gets the lazy-initialized SQL query context.
     * 
     * @return the SQL query context
     */
    public SQLQueryContext context() {
        if (_context == null) {
            _context = new SQLQueryContext(line, startPosition, startIndex, stopIndex,
                                         sqlText, objectType, objectName);
        }
        return _context;
    }
    
    /**
     * Gets the query context array.
     * 
     * @return an array containing the context if valid, empty array otherwise
     */
    public QueryContext[] getQueryContext() {
        SQLQueryContext ctx = context();
        if (ctx.isValid()) {
            return new QueryContext[]{ctx};
        } else {
            return new QueryContext[0];
        }
    }
    
    /**
     * Creates a copy of this Origin with updated fields.
     * 
     * @param line the line number
     * @param startPosition the start position
     * @return a new Origin with updated fields
     */
    public Origin copy(Integer line, Integer startPosition) {
        return new Origin(line, startPosition, this.startIndex, this.stopIndex,
                         this.sqlText, this.objectType, this.objectName);
    }

}
