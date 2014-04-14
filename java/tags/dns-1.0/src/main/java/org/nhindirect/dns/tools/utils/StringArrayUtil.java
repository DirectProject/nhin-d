/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/


package org.nhindirect.dns.tools.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class for parsing and working with string array paremeters.
 * @author Greg Meyer
 *
 * @since 1.0
 */
public class StringArrayUtil 
{
    private static final char[] Whitespace = { ' ', '\t', '\r', '\n' };
    private static final char[] Quotes = { '"' };
	
    static
    {
    	Arrays.sort(Whitespace);    	
    }
    
    public static String getValueOrNull(String[] args, int indexAt)
    {
        if (indexAt < 0 || indexAt >= args.length)
        {
            return null;
        }
        
        return args[indexAt];
    }
    
    public static boolean isNullOrEmpty(String[] args)
    {
        return (args == null || args.length == 0);
    }
    
    public static boolean isNullOrEmpty(String str)
    {
        return (str == null || str.isEmpty());
    }
    
    public static String getRequiredValue(String[] args, int index)
    {
        String value = getValueOrNull(args, index);
        if (isNullOrEmpty(value))
        {
            throw new IllegalArgumentException("Missing argument at position " + index);
        }

        return value;
    }

    public static String getOptionalValue(String[] args, int index, String defaultValue)
    {
        String value = getValueOrNull(args,index);
        if (isNullOrEmpty(value))
        {
            return defaultValue;
        }

        return value;
    }

    static int skipOver(String source, int startAt, char[] chars)
    {
        for (int i = startAt; i < source.length(); ++i)
        {
            if (Arrays.binarySearch(chars, source.charAt(i)) < 0)
            {
                return i;
            }
        }

        return -1;
    }
    
    static String[] parseAsCommandLine(String input)
    {
    	if (isNullOrEmpty(input))
    		return new String[0];
    
    	ArrayList<String> retVal = new ArrayList<String>();
    	
        int index = 0;
        while (index < input.length())
        {
            int tokenStartAt = skipOver(input, index, Whitespace);
            if (tokenStartAt < 0)
            {
                break;
            }

            char[] delimiter;
            if (Arrays.binarySearch(Quotes, input.charAt(tokenStartAt)) >= 0)
            {
                tokenStartAt++;
                delimiter = Quotes;
            }
            else
            {
                delimiter = Whitespace;
            }

            int tokenEndAt  = skipTo(input, tokenStartAt, delimiter);
            if (tokenEndAt < 0)
            {
                tokenEndAt = input.length();
            }
            
            int length = tokenEndAt - tokenStartAt;
            if (length > 0)
            {
            	retVal.add(input.substring(tokenStartAt, tokenEndAt));
            }
            
            index = tokenEndAt + 1;
        }    	
        
        return retVal.toArray(new String[retVal.size()]);
    }
    
    static int skipTo(String source, int startAt, char[] chars)
    {
    	int firstIndex = 0x8FFF;
    	int foundIndex = -1;
    	
    	for (char ch : chars)
    	{
    		if ((foundIndex = source.indexOf(ch, startAt)) > -1)
    			if (foundIndex < firstIndex)
    				firstIndex = foundIndex;
    	}
    	
       return (firstIndex < 0x8FFF) ? firstIndex : -1;
    	   
    }    
}
