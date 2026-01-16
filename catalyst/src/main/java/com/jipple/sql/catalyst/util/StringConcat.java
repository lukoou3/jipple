package com.jipple.sql.catalyst.util;

import com.jipple.unsafe.array.ByteArrayUtils;

import java.util.ArrayList;

/**
 * Concatenation of sequence of strings to final string with cheap append method
 * and one memory allocation for the final string.  Can also bound the final size of
 * the string.
 */
public class StringConcat {
    protected final ArrayList<String> strings;
    protected int length;
    protected final int maxLength;

    public StringConcat() {
        this(ByteArrayUtils.MAX_ROUNDED_ARRAY_LENGTH);
    }

    public StringConcat(int maxLength) {
        this.maxLength = maxLength;
        this.strings = new ArrayList<String>();
        this.length = 0;
    }

    public boolean atLimit() {
        return length >= maxLength;
    }

    /**
     * Appends a string and accumulates its length to allocate a string buffer for all
     * appended strings once in the toString method.  Returns true if the string still
     * has room for further appends before it hits its max limit.
     */
    public void append(String s) {
        if (s != null) {
            int sLen = s.length();
            if (!atLimit()) {
                int available = maxLength - length;
                String stringToAppend = (available >= sLen) ? s : s.substring(0, available);
                strings.add(stringToAppend);
            }

            // Keeps the total length of appended strings. Note that we need to cap the length at
            // `ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH`; otherwise, we will overflow
            // length causing StringIndexOutOfBoundsException in the substring call above.
            length = (int) Math.min((long) length + sLen, ByteArrayUtils.MAX_ROUNDED_ARRAY_LENGTH);
        }
    }

    /**
     * The method allocates memory for all appended strings, writes them to the memory and
     * returns concatenated string.
     */
    @Override
    public String toString() {
        int finalLength = atLimit() ? maxLength : length;
        StringBuilder result = new StringBuilder(finalLength);
        strings.forEach(s -> result.append(s));
        return result.toString();
    }
}
