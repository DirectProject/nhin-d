/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

package org.nhindirect.stagent.mail;

import java.util.Locale;

/**
 * Message and MIME constants
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class MimeStandard 
{
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final String CRLF = "\r\n";
    public static final char Escape = '\\';
    public static final char NameValueSeparator = ':';
    public static final char BoundaryChar = '-';
    public static final String BoundarySeparator = "--";

    //
    // Headers
    //
    public static final String HeaderPrefix = "Content-";
    public static final String VersionHeader = "MIME-Version";
    
    public static final String ContentTypeHeader = "Content-Type";
    public static final String ContentIDHeader = "Content-ID";
    public static final String ContentDispositionHeader = "Content-Disposition";
    public static final String ContentDescriptionHeader = "Content-Description";
    public static final String ContentTransferEncodingHeader = "Content-Transfer-Encoding";

    public static final String TransferEncodingBase64 = "base64";
    public static final String TransferEncoding7Bit = "7bit";
    public static final String TransferEncodingQuoted = "quoted-printable";

    //
    // Content-Type
    //
    public static class MediaType
    {
    	public static final String TextPlain = "text/plain";
    	public static final String Default = TextPlain;
    	public static final String Multipart = "multipart";
    	public static final String MultipartMixed = "multipart/mixed;";
    }
    
    //
    // MAIL
    //
    public static final String MailAddressSeparator = ",";
    //
    // Other..
    //    
    
    public static boolean isWhitespace(char ch)
    {
        //
        // CR/LF are reserved characters
        //
        return (ch == ' ' || ch == '\t');
    }
    
    public static String combine(String name, String value)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("name");
        }
        if (value == null || value.length() == 0)
        {
            throw new IllegalArgumentException("value");
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(MimeStandard.NameValueSeparator).append(" ");
        builder.append(value);
        return builder.toString();
    }
    
    //
    // RFC: 
    //  - All string comparisions are case-insensitive        
    //  - locale independant - i.e. ordinal
    //
    public static boolean equals(String x, String y)
    {
        return String.CASE_INSENSITIVE_ORDER.compare(x, y) == 0;
    }
    
    public static boolean startsWith(String x, String y)
    {
    	if (y.length() > x.length())
    		return false;
    	
    	return String.CASE_INSENSITIVE_ORDER.compare(x.substring(0, y.length()), y) == 0;
    }

    public static boolean contains(String x, String y)
    {
        return x.toLowerCase(Locale.getDefault()).indexOf(y.toLowerCase(Locale.getDefault())) >= 0;
    }    
    
}
