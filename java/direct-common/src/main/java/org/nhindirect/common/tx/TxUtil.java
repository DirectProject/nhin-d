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

package org.nhindirect.common.tx;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.mail.SMIMEStandard;
import org.nhindirect.common.mail.dsn.DSNStandard;
import org.nhindirect.common.tx.model.TxMessageType;


/**
 * Utility class for message monitoring
 * @author Greg Meyer
 * @since 1.1
 */
public class TxUtil 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TxUtil.class);
	

	/**
	 * Gets the message type based on the content type headers.
	 * @param msg The message to extract the type from.
	 * @return MDN if the message is an MDN message (<a href="http://tools.ietf.org/html/rfc3798">RFC 3798</a>)<br>
	 * DSN if the message is a DNS message (<a href="http://tools.ietf.org/html/rfc3464">RFC 3464</a>)<br>
	 * Normal for all other message type.<br>
	 * Return Unknown if an error occurs.
	 * 
	 */
	public static TxMessageType getMessageType(MimeMessage msg)
	{
		try
		{
	        ContentType contentType = new ContentType(msg.getContentType());
	
	        if (contentType.match(MDNStandard.MediaType.ReportMessage) && 
	        		contentType.getParameter(MDNStandard.MediaType.ReportType) != null)
	        {	
	        	 
	        	if (contentType.getParameter(MDNStandard.MediaType.ReportType).equalsIgnoreCase(MDNStandard.MediaType.ReportTypeValueNotification))	        
	        		return TxMessageType.MDN;
	        	else if (contentType.getParameter(DSNStandard.MediaType.ReportType).equalsIgnoreCase(DSNStandard.MediaType.ReportTypeValueDelivery))	        
	        		return TxMessageType.DSN;	        	
	        }
	        else if (contentType.match(SMIMEStandard.EncryptedContentMediaType) || 
	        		contentType.match(SMIMEStandard.EncryptedContentMediaTypeAlternative))
	        {
	        	return TxMessageType.SMIME;	 
	        }
	        
	        return TxMessageType.IMF;
		}
		///CLOVER:OFF
		catch (ParseException e)
		{
			LOGGER.warn("Failed to discern message type.", e);
		}
		catch (MessagingException e)
		{
			LOGGER.warn("Failed to discern message type.", e);
		}	
		return TxMessageType.UNKNOWN;
		///CLOVER:ON
	}	
		
}
