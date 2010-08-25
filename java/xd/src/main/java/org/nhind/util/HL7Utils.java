package org.nhind.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Utility methods for working with HL7 strings.
 * 
 * @author beau
 */
public class HL7Utils {

    /**
     * Break up a string using the specified token and return the value in the
     * specified field.
     * 
     * @param in
     *            The string to break up using the specified token.
     * @param token
     *            The token with which to break up the input string.
     * @param field
     *            The numeric field number to extract and return.
     * @return the specified field of the string, broken by the given token.
     */
    public static String returnField(String in, String token, int field) {
        StringTokenizer list = new StringTokenizer(in, token);
        String ret = in;
        int count = 0;
        while (list.hasMoreElements()) {
            String temp = list.nextToken();
            if (++count == field) {
                ret = temp;
            }
        }
        return ret;
    }

    /**
     * Split up a string using the given delimiter and return as a list of
     * tokens
     * 
     * @param input
     *            The string to split
     * @param delimiter
     *            The delimiter used for splitting
     * @return a list of split tokens
     */
    public static List<String> split(String input, String delimiter) {
        if (StringUtils.isEmpty(input)) {
            return Collections.emptyList();
        }

        if (StringUtils.isEmpty(delimiter)) {
            throw new IllegalArgumentException("A delimiter must be provided.");
        }

        String quotedDelimiter = Pattern.quote(delimiter);
        return Arrays.asList(input.split(quotedDelimiter, -1));
    }
}
