package com.jipple.sql.catalyst.trees;

import com.jipple.QueryContext;

/**
 * The class represents error context of a SQL query.
 */
public class SQLQueryContext implements QueryContext {
    
    private final Integer line;
    private final Integer startPosition;
    private final Integer originStartIndex;
    private final Integer originStopIndex;
    private final String sqlText;
    private final String originObjectType;
    private final String originObjectName;
    
    private String summary;
    private String fragment;
    
    public SQLQueryContext(Integer line, Integer startPosition, Integer originStartIndex,
                          Integer originStopIndex, String sqlText, String originObjectType, 
                          String originObjectName) {
        this.line = line;
        this.startPosition = startPosition;
        this.originStartIndex = originStartIndex;
        this.originStopIndex = originStopIndex;
        this.sqlText = sqlText;
        this.originObjectType = originObjectType;
        this.originObjectName = originObjectName;
    }
    
    @Override
    public String objectType() {
        return originObjectType != null ? originObjectType : "";
    }
    
    @Override
    public String objectName() {
        return originObjectName != null ? originObjectName : "";
    }
    
    @Override
    public int startIndex() {
        return originStartIndex != null ? originStartIndex : -1;
    }
    
    @Override
    public int stopIndex() {
        return originStopIndex != null ? originStopIndex : -1;
    }
    
    /**
     * The SQL query context of current node. For example:
     * == SQL of VIEW v1(line 1, position 25) ==
     * SELECT '' AS five, i.f1, i.f1 - int('2') AS x FROM INT4_TBL i
     *                          ^^^^^^^^^^^^^^^
     */
    public String getSummary() {
        if (summary == null) {
            // If the query context is missing or incorrect, simply return an empty string.
            if (!isValid()) {
                summary = "";
            } else {
                String positionContext = "";
                if (line != null && startPosition != null) {
                    // Note that the line number starts from 1, while the start position starts from 0.
                    // Here we increase the start position by 1 for consistency.
                    positionContext = "(line " + line + ", position " + (startPosition + 1) + ")";
                }
                String objectContext = "";
                if (originObjectType != null && originObjectName != null) {
                    objectContext = " of " + originObjectType + " " + originObjectName;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("== SQL").append(objectContext).append(positionContext).append(" ==\n");
                
                String text = sqlText;
                int start = Math.max(originStartIndex, 0);
                int stop = Math.min(originStopIndex != null ? originStopIndex : text.length() - 1, 
                                   text.length() - 1);
                // Ideally we should show all the lines which contains the SQL text context of the current
                // node:
                // [additional text] [current tree node] [additional text]
                // However, we need to truncate the additional text in case it is too long. The following
                // variable is to define the max length of additional text.
                int maxExtraContextLength = 32;
                String truncatedText = "...";
                int lineStartIndex = start;
                // Collect the SQL text within the starting line of current Node.
                // The text is truncated if it is too long.
                while (lineStartIndex >= 0 &&
                    start - lineStartIndex <= maxExtraContextLength &&
                    text.charAt(lineStartIndex) != '\n') {
                    lineStartIndex--;
                }
                boolean startTruncated = start - lineStartIndex > maxExtraContextLength;
                int currentIndex = lineStartIndex;
                if (startTruncated) {
                    currentIndex -= truncatedText.length();
                }
                
                int lineStopIndex = stop;
                // Collect the SQL text within the ending line of current Node.
                // The text is truncated if it is too long.
                while (lineStopIndex < text.length() &&
                    lineStopIndex - stop <= maxExtraContextLength &&
                    text.charAt(lineStopIndex) != '\n') {
                    lineStopIndex++;
                }
                boolean stopTruncated = lineStopIndex - stop > maxExtraContextLength;
                
                String truncatedSubText = (startTruncated ? truncatedText : "") +
                    text.substring(lineStartIndex + 1, lineStopIndex) +
                    (stopTruncated ? truncatedText : "");
                String[] lines = truncatedSubText.split("\n");
                for (String lineText : lines) {
                    builder.append(lineText).append("\n");
                    currentIndex++;
                    for (int i = 0; i < lineText.length(); i++) {
                        if (currentIndex < start) {
                            builder.append(" ");
                        } else if (currentIndex >= start && currentIndex <= stop) {
                            builder.append("^");
                        }
                        currentIndex++;
                    }
                    builder.append("\n");
                }
                summary = builder.toString();
            }
        }
        return summary;
    }
    
    /**
     * Gets the textual fragment of a SQL query.
     */
    @Override
    public String fragment() {
        if (fragment == null) {
            if (!isValid()) {
                fragment = "";
            } else {
                fragment = sqlText.substring(originStartIndex, originStopIndex + 1);
            }
        }
        return fragment;
    }
    
    /**
     * Checks if this context is valid.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return sqlText != null && originStartIndex != null && originStopIndex != null &&
            originStartIndex >= 0 && originStopIndex < sqlText.length() &&
            originStartIndex <= originStopIndex;
    }
}

