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

package org.nhindirect.common.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common RFC822/5322 headers and common header collections. 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class MailStandard 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(MailStandard.class);
	
	public static class Headers
	{
	    public static final String To = "to";
	    public static final String CC = "cc";
	    public static final String BCC = "bcc";
	    public static final String From = "from";
	    public static final String Sender = "sender";
	    public static final String MessageID = "message-id";
	    public static final String Subject = "subject";
	    public static final String Date = "date";
	    public static final String OrigDate = "orig-date";
	    public static final String InReplyTo = "in-reply-to";
	    public static final String References = "references";
	    public static final String ContentType = "content-type";
	}

	
	/**
	 * Gets a specific header from the message
	 * @param msg The message to extract the message from.
	 * @param headerName The header name.
	 * @return The value of the header.  Return an empty string if the header is not found.
	 */
	public static String getHeader(MimeMessage msg, String headerName)
	{
		String retVal = "";
		
		try
		{
			retVal = msg.getHeader(headerName, ",");
			if (retVal == null)
				retVal = "";
		}
		catch (MessagingException e)
		{
			///CLOVER:OFF			
			LOGGER.warn("Failed to retrieve header \"" + headerName + "\" from message.", e);
			///CLOVER:ON
		}
		
		return retVal;
	}
}
