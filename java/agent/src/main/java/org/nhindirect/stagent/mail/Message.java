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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.parser.EntitySerializer;

/**
 * Extension to Java MimeMessage with utility methods. 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class Message extends MimeMessage 
{    
    /**
     * Constructs an empty message.
     */
    Message()
    {
    	super((Session)null);
    }
    
    /**
     * Constructs a message with the provided headers and message body.
     * @param headers Collection of headers to apply to the message.
     * @param content The message's body.
     * @throws MessagingException
     */
    public Message(InternetHeaders headers, byte[] content) throws MessagingException 
    {
    	super(null, toInputStream(headers, content));
    }    
    
    private static InputStream toInputStream(InternetHeaders headers, byte[] content) throws MessagingException 
    {
    	MimeBodyPart prt = new MimeBodyPart(headers, content);
    	return new ByteArrayInputStream(EntitySerializer.Default.serializeToBytes(prt));
    }
    
    /**
     * Constructs a message from an existing MimeMessage.
     * @param message The message that will make up the header and body of this message.
     */
    public Message(MimeMessage message) throws MessagingException 
    {
    	super(message);    	    	
    }
    
    /**
     * Constructs a message from input stream containing the entire message.
     * @param inStream An input stream containing the entire content (headers and content) of the message.
     */
    public Message(InputStream inStream) throws MessagingException 
    {
    	super(null, inStream);   	    	
    }            

    /*
     * Utility function for retrieving raw headers.... raw headers information will be vital for proper message wrapping
     */
    @SuppressWarnings("unchecked")
    private String getRawHeaderLine(String headerName)
    {
    	try
    	{    	
	    	Enumeration<String> headers = this.getMatchingHeaderLines(new String[] {headerName});
	    	
	    	if (headers != null && headers.hasMoreElements())
	    		return (String)headers.nextElement();
    	}
    	catch (MessagingException e)
    	{
    		/* no-op */
    	}

    	return null;
    }
    
    /**
     * Gets the raw to header.
     * @return The raw to header of the message.
     */
    public String getToHeader()
    {    	
    	return getRawHeaderLine(MailStandard.Headers.To);
    }
    
    /**
     * Gets the raw cc header.
     * @return The raw cc header of the message.
     */    
    public String getCCHeader()
    {
    	return getRawHeaderLine(MailStandard.Headers.CC);
    }     
    
    /**
     * Gets the raw bcc header.
     * @return The raw bcc header of the message.
     */    
    public String getBCCHeader()
    {
    	return getRawHeaderLine(MailStandard.Headers.BCC);
    }               
    
    /**
     * Gets the raw from header.
     * @return The raw from header of the message.
     */    
    public String getFromHeader()
    {
    	return getRawHeaderLine(MailStandard.Headers.From);
    }         
    
    /**
     * Gets the raw subject header.
     * @return The raw subject header of the message.
     */    
    public String getSubjectHeader()
    {
    	return getRawHeaderLine(MailStandard.Headers.Subject);
    }       
    
    /**
     * Gets the raw message id header.
     * @return The raw message id header of the message.
     */    
    public String getIDHeader()
    {
    	return getRawHeaderLine(MailStandard.Headers.MessageID);
    }        
    
    /**
     * Gets the raw date header.
     * @return The raw date header of the message.
     */    
    public String getDateHeader()
    {
    	return getRawHeaderLine(MailStandard.Headers.Date);
    }      
    
	/**
	 * Gets a copy of this message without any non-mime headers.
	 * @returns A copy of this message without any non-mime headers.
	 */
    @SuppressWarnings("unchecked")
    public MimeEntity extractMimeEntity()
    {
    	MimeEntity retVal = null;
    	
    	try
    	{
    		InternetHeaders headers = new InternetHeaders();
    		
	        if (this.headers.getAllHeaders().hasMoreElements())
	        {
	        	Enumeration<javax.mail.Header> hEnum = this.headers.getAllHeaders();
	        	while (hEnum.hasMoreElements())
	        	{
	        		javax.mail.Header hdr = hEnum.nextElement();
	        		if (MimeStandard.startsWith(hdr.getName(), MimeStandard.HeaderPrefix))
	        			headers.addHeader(hdr.getName(), hdr.getValue());
	        	}
	
	            if (!headers.getAllHeaders().hasMoreElements())
	            {                        	
	                throw new MimeException(MimeError.InvalidMimeEntity);
	            }
	            
	            retVal = new MimeEntity(headers, getContentAsBytes());
	            
	        }
    	}
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity, e);
    	}
    	
        return retVal;
    }
    
    /**
     * Creates a MimeEntity object from this message that will be used for creating a message signature.  
     * @param includeEpilogue Indicates if the message's epilogue part (if one exists) should be used in generating the message signature digest. 
     * @return MimeEntity object that will be used for creating a message signature. 
     */
    public MimeEntity extractEntityForSignature(boolean includeEpilogue)
    {
        if (includeEpilogue || !isMultiPart())
        {
            return this.extractMimeEntity();
        }
        
        /*
        MimeEntity signableEntity = new MimeEntity();
        signableEntity.setHeaders(this.getHeaders().selectMimeHeaders());
        
        StringSegment content = StringSegment.createNull();
        for (EntityPart part : this.getAllParts())
        {
            if (part.getType() == EntityPartType.Epilogue)
            {
                content = new StringSegment(content.getSource(), content.getStartIndex(), part.getSourceText().getStartIndex() - 1);
            }
            else
            {                
                content.union(part.getSourceText());
            }
        }            
        signableEntity.setBody(new Body(content));
        
        return signableEntity;
        */
        return null;
    }
    
    @Override
    public String toString()
    {
        return EntitySerializer.Default.serialize(this);
    }    
    
    /**
     * Gets the content (body) of the message as a byte array.
     * @return The content (body) of the message as a byte array.
     */
    public byte[] getContentAsBytes()
    {
    	byte retVal[] = null;
    	
    	try
    	{
	    	InputStream inStream = getRawInputStream();
	    	ByteArrayOutputStream oStream = new ByteArrayOutputStream();
	    	byte buffer[] = new byte[2048];
	    	int read;
	    	while ((read = inStream.read(buffer)) > -1)
	    		oStream.write(buffer, 0, read);
	    	
	    	retVal = oStream.toByteArray();
	    	IOUtils.closeQuietly(oStream);	
    	}
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.InvalidBody, e);
    	}
    	
    	return retVal; 
    }    
    
    private boolean isMultiPart()
    {
    	String contentType;
    	try
    	{
    		contentType = this.getContentType();
    	}
    	catch (MessagingException e)
    	{
    		return false;
    	}
        if (contentType == null || contentType.length() == 0)
        {
            return false;
        }
        return MimeStandard.contains(contentType, MimeStandard.MediaType.Multipart);
    }         
}
