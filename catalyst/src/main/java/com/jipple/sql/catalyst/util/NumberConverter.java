package com.jipple.sql.catalyst.util;

/**
 * Utility class for converting numeric types to byte arrays.
 * All conversions use big-endian byte order.
 */
public class NumberConverter {

    /**
     * Converts a long value to an 8-byte array in big-endian order.
     * 
     * @param l the long value to convert
     * @return an 8-byte array representing the long value
     */
    public static byte[] toBinary(long l) {
        byte[] result = new byte[8];
        result[0] = (byte) ((l >>> 56) & 0xFF);
        result[1] = (byte) ((l >>> 48) & 0xFF);
        result[2] = (byte) ((l >>> 40) & 0xFF);
        result[3] = (byte) ((l >>> 32) & 0xFF);
        result[4] = (byte) ((l >>> 24) & 0xFF);
        result[5] = (byte) ((l >>> 16) & 0xFF);
        result[6] = (byte) ((l >>> 8) & 0xFF);
        result[7] = (byte) (l & 0xFF);
        return result;
    }

    /**
     * Converts an int value to a 4-byte array in big-endian order.
     * 
     * @param i the int value to convert
     * @return a 4-byte array representing the int value
     */
    public static byte[] toBinary(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >>> 24) & 0xFF);
        result[1] = (byte) ((i >>> 16) & 0xFF);
        result[2] = (byte) ((i >>> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * Converts a short value to a 2-byte array in big-endian order.
     * 
     * @param s the short value to convert
     * @return a 2-byte array representing the short value
     */
    public static byte[] toBinary(short s) {
        byte[] result = new byte[2];
        result[0] = (byte) ((s >>> 8) & 0xFF);
        result[1] = (byte) (s & 0xFF);
        return result;
    }

    /**
     * Converts a byte value to a 1-byte array.
     * 
     * @param b the byte value to convert
     * @return a 1-byte array containing the byte value
     */
    public static byte[] toBinary(byte b) {
        byte[] result = new byte[1];
        result[0] = b;
        return result;
    }
}
