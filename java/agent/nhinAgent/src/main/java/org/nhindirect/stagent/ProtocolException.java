package org.nhindirect.stagent;

/**
 * Exception thrown when an invalid message in encountered.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class ProtocolException extends NHINDException 
{
	static final long serialVersionUID = 3303346719726194880L;	
	
	/**
	 * 
	 * Enumerated cause of the exception
	 *
	 */
    public enum ProtocolError
    {
        Unexpected,
        InvalidCRLF,
        InvalidMimeEntity,
        InvalidHeader,
        InvalidBody,
        InvalidBodySubpart,
        MissingTo,
        MissingFrom,
        MissingNameValueSeparator,
        MissingHeaderValue,
        ContentTypeMismatch,
        TransferEncodingMismatch,
        Base64EncodingRequired,
        InvalidSignatureMimeParts
    }
    
	/**
	 * Constructs an exception with the protocol error.
	 * @param error The protocol error.
	 */    
    public ProtocolException(ProtocolError error)
    {
    	super(error);
    }
    
	/**
	 * Constructs an exception with a message and the protocol error.
	 * @param error The protocol error
	 * @param msg The exception message.
	 */    
    public ProtocolException(ProtocolError error, String message)
    {
    	super(error, message);
    }
       
	/**
	 * Constructs an exception with the protocol error and the exception that caused the error.
	 * @param error The protocol error.
	 * @param innerException The exception that caused the error.
	 */     
    public ProtocolException(ProtocolError error, Exception innerException)
    {
    	super(error, innerException);
    }
    
	/**
	 * Constructs an exception with the protocol error, a message, and the exception that caused the error.
	 * @param error The protocol error.
	 * @param msg The exception message.
	 * @param innerException The exception that caused the error.
	 */      
    public ProtocolException(ProtocolError error, String message, Exception innerException)
    {
    	super(error, message, innerException);
    }
    
}
