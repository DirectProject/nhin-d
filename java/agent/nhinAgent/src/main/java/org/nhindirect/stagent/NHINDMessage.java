package org.nhindirect.stagent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.ProtocolException.ProtocolError;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.parser.Protocol;

/**
 * A complete MIME message with NHIN direct trust and certificate logic.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
@SuppressWarnings("unchecked")
public class NHINDMessage extends MimeMessage 
{
    NHINDAgent agent;
    NHINDAddress sender;
    
    /**
     * Constructs an empty message.
     */
    NHINDMessage()
    {
    	super((Session)null);
    }
 
    
    /**
     * Constructs a message with the provided headers and message body.
     * @param headers Collection of headers to apply to the message.
     * @param content The message's body.
     * @throws MessagingException
     */
    public NHINDMessage(InternetHeaders headers, byte[] content) throws MessagingException 
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
    public NHINDMessage(MimeMessage message) throws MessagingException 
    {
    	super(message);    	    	
    }
    
    /**
     * Constructs a message from input stream containing the entire message.
     * @param inStream An input stream containing the entire content (headers and content) of the message.
     */
    public NHINDMessage(InputStream inStream) throws MessagingException 
    {
    	super(null, inStream);   	    	
    }
    
    /**
     * Gets the NHINDAgent instance that will be applied to this message. 
     * @return The NHINDAgent instance that will be applied to this message. 
     */
    public NHINDAgent getAgent()
    {
    	return agent;
    }

    /**
     * Sets the NHINDAgent instance that will be applied to this message. 
     * @param value The NHINDAgent instance that will be applied to this message. 
     */
    public void setAgent(NHINDAgent value)
    {
        agent = value;
    }
            

    /**
     * Gets the sender of this message.
     * @return The sender of this message.
     */
    public NHINDAddress getSender()
    {
    	try
    	{
		    if (sender == null)
		    {
		    	if (getFrom().length > 0)
		        sender = new NHINDAddress((InternetAddress)getFrom()[0]);
		    }
    	}
    	catch (MessagingException e)
    	{
    		return null;
    	}
	    return sender;
    }
    
    /**
     * Validates if this message contains the required fields to be a complete NHINDMessage.
     */
    public void validate()
    {
    	try
    	{
	        if (this.getAllRecipients().length == 0)
	        {
	            throw new ProtocolException(ProtocolError.MissingTo);
	        }
	
	        if (this.getFrom().length == 0)
	        {
	            throw new ProtocolException(ProtocolError.MissingFrom);
	        }
    	}
    	catch (MessagingException e)
    	{
    		throw new ProtocolException(ProtocolError.InvalidHeader);
    	}
    }    
    
    /// <summary>
    /// The source message has non-MIME headers
    /// Takes the source and creates new Message that contains only items relevant to Mime
    /// </summary>
    /// <returns></returns>
	/**
	 * Gets a copy of this message without any non-mime headers.
	 * @returns A copy of this message without any non-mime headers.
	 */
    public MimeEntity extractMimeEntity()
    {
    	MimeEntity retVal = null;
    	
    	try
    	{
    		InternetHeaders headers = new InternetHeaders();
    		
	        if (this.headers.getAllHeaders().hasMoreElements())
	        {
	        	Enumeration hEnum = this.headers.getAllHeaders();
	        	while (hEnum.hasMoreElements())
	        	{
	        		javax.mail.Header hdr = (javax.mail.Header)hEnum.nextElement();
	        		if (Protocol.startsWith(hdr.getName(), Protocol.MimeHeaderPrefix))
	        			headers.addHeader(hdr.getName(), hdr.getValue());
	        	}
	
	            if (!headers.getAllHeaders().hasMoreElements())
	            {                        	
	                throw new ProtocolException(ProtocolError.InvalidMimeEntity);
	            }
	            
	            retVal = new MimeEntity(headers, getContentAsBytes());
	            
	        }
    	}
    	catch (MessagingException e)
    	{
    		throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
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
	    	oStream.close();
    	}
    	catch (Exception e)
    	{
    		throw new ProtocolException(ProtocolError.InvalidBody, e);
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
        return Protocol.contains(contentType, Protocol.MediaType_Multipart);
    }         
}
