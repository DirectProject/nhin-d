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

import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;

import org.nhindirect.stagent.parser.EntitySerializer;

/**
 * Utility class for wrapping a message according to the NHIN Direct agent specification.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class WrappedMessage 
{
	@SuppressWarnings("unchecked")
	/*
	 * Copy the requested headers from the original message into a new header collection.
	 */
	private static InternetHeaders copyHeaders(Message message, String[] headersToCopy) throws MessagingException
	{
		InternetHeaders retVal = new InternetHeaders();
			
		// InternetHeaders allow work on the raw message header
		Enumeration<String> hdEnum = message.getMatchingHeaderLines(headersToCopy);	
		
		while (hdEnum.hasMoreElements())
			retVal.addHeaderLine(hdEnum.nextElement());			
		
		return retVal;
	}
	
	/**
	 * Wraps a messaging into a new message by creating a message wrapper, copying only desired headers into the wrapper, and pushing the entire
	 * original message (including headers) into the body of the wrapper.  
	 * @param message The message to wrap.
	 * @param headersToCopy The headers that should be copied from the original message into the wrapper.
	 * @return A message object that wraps the entire original entity (including headers) in its body. 
	 * @throws MessagingException
	 */
    public static Message create(Message message, String[] headersToCopy) throws MessagingException
    {
    	if (message == null)
    		throw new IllegalArgumentException("Message cannot be null");
    	
    	InternetHeaders copiedHeaders = copyHeaders(message, headersToCopy);
    	copiedHeaders.setHeader(MailStandard.Headers.ContentType, MailStandard.MediaType.WrappedMessage);
    	
    	return new Message(copiedHeaders, EntitySerializer.Default.serializeToBytes(message));    	    	
    }
    
	/**
	 * Wraps a messaging (represented in a raw string) into a new message by creating a message wrapper, copying only desired headers into the wrapper, 
	 * and pushing the entire original message (including headers) into the body of the wrapper.  
	 * @param message The message to wrap.
	 * @param headersToCopy The headers that should be copied from the original message into the wrapper.
	 * @return A message object that wraps the entire original entity (including headers) in its body. 
	 * @throws MessagingException
	 */
    public static Message create(String message, String[] headersToCopy) throws MessagingException
    {
    	if (message == null || message.length() == 0)
    		throw new IllegalArgumentException("Message cannot be null or empty");
    	
    	Message msg = new Message(EntitySerializer.Default.deserialize(message));
    	
    	return create(msg, headersToCopy);
    }    
    
    /**
     * Determines if a message is wrapped.
     * @param message The message to check.
     * @return Returns true if the message is determined to be a wrapped message.  The wrapped message can be objected from the wrapper using
     * the {@link #extract(Message)}} operation.
     */
    public static boolean isWrapped(Message message)
    {
    	if (message == null)
    		return false; 	
    	
    	try
    	{    		
    		return message.getContentType().equalsIgnoreCase(MailStandard.MediaType.WrappedMessage);
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return false;
    }
    
    /**
     * Extracts the wrapped message from the wrapper.
     * @param message The message wrapper.
     * @return The message that is contained inside the wrapper's body.
     * @throws MessagingException
     */
    public static Message extract(Message message) throws MessagingException
    {
    	if (!isWrapped(message))
    		 throw new MimeException(MimeError.ContentTypeMismatch);

    	byte[] body = message.getContentAsBytes();
    	
    	if (body == null || body.length == 0)
    		throw new MimeException(MimeError.MissingBody);
    	
    	return new Message(EntitySerializer.Default.deserialize(body));
    }
}
