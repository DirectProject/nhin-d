package org.nhindirect.stagent;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

import org.nhindirect.stagent.ProtocolException.ProtocolError;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.parser.Protocol;

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
        return Protocol.contains(contentType, Protocol.MediaType_Multipart);
    }

            
    /**
     * Verifies if the content type of the entity is what is expected.
     * @param expectedType The content type of the entity that is expected.
     * @throws ProtocolException Thrown if the entity's content type does not match the expected type.
     */
    public void verifyContentType(String expectedType) throws ProtocolException
    {
    	String parsedType;
    	try
    	{
    		parsedType = this.getContentType();
    		if (parsedType == null || !Protocol.equals(parsedType, expectedType))
    		{
    			throw new ProtocolException(ProtocolError.ContentTypeMismatch);
    		}
    	}
    	catch (MessagingException e)
    	{
    		throw new ProtocolException(ProtocolError.ContentTypeMismatch);
    	}
    }

    /**
     * Verifies if the content type of the entity is what is expected.
     * @param expectedType The content type of the entity that is expected.
     * @throws ProtocolException Thrown if the entity's content type does not match the expected type.
     */    
    public void verifyContentType(ContentType expectedType) throws ProtocolException
    {
    	String parsedType;
    	try
    	{
    		parsedType = this.getContentType();
    		if (parsedType == null || !Protocol.equals(parsedType, expectedType.toString()))
    		{
    			throw new ProtocolException(ProtocolError.ContentTypeMismatch);
    		}
    	}
    	catch (MessagingException e)
    	{
    		throw new ProtocolException(ProtocolError.ContentTypeMismatch);
    	}
    }
    
    /**
     * Verifies if the transfer encoding of the entity is what is expected.
     * @param expectedEncoding The transfer encoding  of the entity that is expected.
     * @throws ProtocolException Thrown if the entity's transfer encoding  does not match the expected encoding.
     */       
    public void verifyTransferEncoding(String expectedEncoding) throws ProtocolException
    {
    	try
    	{
	        String transferEncodingHeader = this.getEncoding();
	        if (transferEncodingHeader == null || transferEncodingHeader.compareToIgnoreCase((expectedEncoding)) != 0)
	        {
	            throw new ProtocolException(ProtocolError.TransferEncodingMismatch);
	        }
    	}
    	catch (MessagingException e)
    	{
    		throw new ProtocolException(ProtocolError.TransferEncodingMismatch);
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
	    	oStream.close();
    	}
    	catch (Exception e)
    	{
    		throw new ProtocolException(ProtocolError.InvalidBody, e);
    	}
    	
    	return retVal; 
    }
    
}
