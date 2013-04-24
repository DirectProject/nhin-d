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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.ParseException;

import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.parser.EntitySerializer;

/**
 * Container for a complete MIME entity.  May consist of a complete MIME message or a MIME part of a message.
 * @author Greg Meyer
 * @author Umesh Madan
 */
public class MimeEntity extends MimeBodyPart
{
    /**
     * Constructs an empty MIME entity.
     */
    public MimeEntity()
    {
    	super();
    }
    
    /**
     * Constructs a MimeEntity with the provided headers and message body.
     * @param headers Collection of headers to apply to the message.
     * @param content The message's body.
     * @throws MessagingException
     */    
    public MimeEntity(InternetHeaders headers, byte[] content) throws MessagingException
    {
    	super(headers, content);
    }
    
    /**
     * Constructs a message from input stream containing the entire message.
     * @param inStream An input stream containing the entire content (headers and content) of the message.
     */    
    public MimeEntity(InputStream inStream) throws MessagingException
    {
    	super(inStream);
    }

    /**
     * Indicates if the entity is MIME multipart entity.
     * @return True if the entity is MIME multipart entity.  False otherwise.
     */
    public boolean isMultiPart()
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

            
    /**
     * Verifies if the content type of the entity is what is expected.
     * @param expectedType The content type of the entity that is expected.
     * @throws ProtocolException Thrown if the entity's content type does not match the expected type.
     */
    public void verifyContentType(String expectedType) throws MimeException
    {
    	try
    	{
    		verifyContentType(new ContentType(expectedType));
    	}
    	catch (ParseException e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity, e);
    	}
    	
    }

    /**
     * Verifies if the content type of the entity is what is expected.
     * @param expectedType The content type of the entity that is expected.
     * @throws ProtocolException Thrown if the entity's content type does not match the expected type.
     */    
    public void verifyContentType(ContentType expectedType) throws MimeException
    {
    	try
    	{
    		if (!expectedType.match(this.getContentType()))
    		{
    			throw new MimeException(MimeError.ContentTypeMismatch);
    		}
    	}
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.ContentTypeMismatch);
    	}
    }
    
    /**
     * Verifies if the transfer encoding of the entity is what is expected.
     * @param expectedEncoding The transfer encoding  of the entity that is expected.
     * @throws ProtocolException Thrown if the entity's transfer encoding  does not match the expected encoding.
     */       
    public void verifyTransferEncoding(String expectedEncoding) throws MimeException
    {
    	try
    	{
	        String transferEncodingHeader = this.getEncoding();
	        if (transferEncodingHeader == null || transferEncodingHeader.compareToIgnoreCase((expectedEncoding)) != 0)
	        {
	            throw new MimeException(MimeError.TransferEncodingMismatch);
	        }
    	}
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.TransferEncodingMismatch);
    	}      	
    	
    }

    @Override
    public String toString()
    {
        return EntitySerializer.Default.serialize(this);
    }
    
    /**
     * Gets the content (body) of the entity as a byte array.
     * @return The content (body) of the entity as a byte array.
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
    
}
