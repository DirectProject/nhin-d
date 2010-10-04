/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.util;

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
        if (in == null || token == null) {
            throw new IllegalArgumentException("Input and token must both be non-null");
        }

        int count = 0;
        String ret = in;
        StringTokenizer list = new StringTokenizer(in, token);

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
