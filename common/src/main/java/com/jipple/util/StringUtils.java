package com.jipple.util;

import java.util.Base64;

public class StringUtils {
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    /**
     * Given a hex string this will return the byte array corresponding to the string .
     *
     * @param hex the hex String array
     * @return a byte array that is a hex string representation of the given string. The size of the
     *     byte array is therefore hex.length/2
     */
    public static byte[] hexStringToByte(final String hex) {
        final byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bts;
    }

    /**
     * Convert an input byte array into a string using the {@link Base64} encoding scheme
     *
     * @param input The byte array to convert to base64
     *
     * @return the base64 of the input in string form
     */
    public static String encodeBase64String(byte[] input) {
        return BASE64_ENCODER.encodeToString(input);
    }

    /**
     * Decode an input byte array using the {@link Base64} encoding scheme and return a newly-allocated byte array
     *
     * @param input The byte array to decode from base64
     *
     * @return a newly-allocated byte array
     */
    public static byte[] decodeBase64(byte[] input) {
        return BASE64_DECODER.decode(input);
    }

}
