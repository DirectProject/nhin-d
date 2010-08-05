package org.nhindirect.stagent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;

import org.nhindirect.stagent.ProtocolException.ProtocolError;
import org.nhindirect.stagent.parser.Protocol;

/**
 * A specific type of MultipartEntity that has been signed.
 * @author Greg Meyer
 * @author Umesh Madan
 */
public class SignedEntity
{
	static final long serialVersionUID = -519795569247486544L;	
	
    private transient MimeEntity content;
    private transient MimeEntity signature;
    private transient MimeMultipart originalMMPart;
    
    /**
     * Constructs a signed entity from a JavaMail MimeMultipart.  The MimeMultipart object contains a part consisting of the message signature.
     * @param mmContentType The content type of the entity.
     * @param mm The MimeMultipart that contains the parts that were used to sign the message and the message signature part.
     */
    public SignedEntity(ContentType mmContentType, MimeMultipart mm) throws ProtocolException
    {
    	originalMMPart = mm;        
        
        try
        {        	        	
	        for (int i = 0; i < mm.getCount(); ++i)
	        {
	        	
	        	String contentType = mm.getBodyPart(i).getContentType();
	        	
        		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	    mm.getBodyPart(i).writeTo(bos);
        	    bos.flush(); 	    

                InputStream stream = new ByteArrayInputStream(bos.toByteArray());

                MimeEntity ent = new MimeEntity(stream);
        	    
                bos.close();	        	
	        	
	        	if (contentType.contains("application/pkcs7-signature") 
	        			|| contentType.contains("application/x-pkcs7-signature"))
	        	{	        		
	                signature = ent;	        	    
	        	}
	        	else
	        	{
	        		content = ent;	        	    	        	   
	        	}
	        }
        }
	
        catch (IOException e) 
        {
        	throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
        }
        catch (MessagingException e)
        {
        	throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
        }
    }
    
    /**
     * Constructs a signed entity from a JavaMail MimeMultipart.  The MimeMultipart object contains a part consisting of the message signature.
     * @param algorithm The digest algorithm used to create message signature.
     * @param mm The MimeMultipart that contains the parts that were used to sign the message and the message signature part.
     */
    public SignedEntity(DigestAlgorithm algorithm, MimeMultipart mm)
    {
    	this(createContentType(algorithm), mm);
    	
    }
    
    /**
     * Gets The content entity that was signed.
     * @return The content that was signed.
     */
    public MimeEntity getContent()
    {
            return content;
    }

    /**
     * Sets the content entity that will be used to generate the signature.
     * @param value The content entity that will be used to generate the signature.
     */
    public void setContent(MimeEntity value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException();
        }
        
        content = value;
    }
    
    /**
     * Gets the entity part that contains the message signature.
     * @return The entity part that contains the message signature.
     */
    public MimeEntity getSignature()
    {
        return signature;
    }
    
    /**
     * Sets the entity part that contains the message signature.
     * @param value The entity part that contains the message signature.
     */
    public void setSignature(MimeEntity value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException();
        }

        //value.verifyContentType(Protocol.SignatureContentType);
        //value.verifyTransferEncoding(Protocol.TransferEncodingBase64);
        signature = value;
    }
       
    /**
     * Gets the original MimeMultipart object of the signed entity.
     * @return The original MimeMultipart object of the signed entity.
     */
    public MimeMultipart getMimeMultipart()
    {
    	return originalMMPart;
    }
    
    /**
     * Gets the content (body) of the signed entity as a byte array.  This includes both the content part and the 
     * signature part.
     * @return The content (body) of the message as a byte array.
     */
    public byte[] getEntityBodyAsBytes()
    {
    	byte[] retVal = null;
    	ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    	try
    	{
    		originalMMPart.writeTo(oStream);
    		retVal = oStream.toByteArray();
    		oStream.close();
    	}
    	catch (Exception e)
    	{
    		throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
    	}
    	
    	return retVal;
    	
    }
    
	/**
	 * Generates a signed entity from a MimeMultipart entity.  The MIME entity should contain an entity part consisting of the message signature.
	 * @param source The MimeMultipart that will be parsed into a signed entity.
	 * @return A signed entity containing the content that was signed and the message signature.
	 */
    public static SignedEntity load(MimeMultipart source)
    {
        if (source == null)
        {
            throw new IllegalArgumentException();
        }
        
        
        SignedEntity retVal = null;
        try
        {
        	retVal = new SignedEntity(new ContentType(source.getContentType()), source);
        }
    	catch (ParseException e)
    	{
        	throw new ProtocolException(ProtocolError.InvalidHeader, e);
    	}

        return retVal;
    }
       
    /**
     * Creates a MIME content type based on the digest algorithm.
     * @param digestAlgorithm The digest algorithm used to generate the message signature.
     * @return a MIME content type based on the digest algorithm.
     */
    static ContentType createContentType(DigestAlgorithm digestAlgorithm)
    {
    	ContentType contentType = null;
    	try
    	{
    		contentType = new ContentType(Protocol.MultiPartType_Signed);
    	
    		contentType.setParameter("micalg", Protocol.toString(digestAlgorithm));
    	}
    	catch (ParseException e){}
    	
        return contentType;
    }
}
