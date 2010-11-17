package org.nhindirect.dns;


public class DNSException extends Exception 
{
	
	private DNSError<?> error;
	
	/**
	 * Construct an exception with an error message.
	 * @param error The error message.
	 */
    public DNSException(String error)
    {
    	super(error);
    }	
	
	/**
	 * Construct an exception with a given DNS error.
	 * @param error The DNS error.
	 */
    public DNSException(DNSError<?> error)
    {
    	this(error, "");
    }
    
	/**
	 * Constructs an exception with a message and the DNS error.
	 * @param error The DNS error
	 * @param msg The exception message.
	 */    
    public DNSException(DNSError<?> error, String message)
    {
    	this(error,"",null);
    }
       
	/**
	 * Constructs an exception with the DNS error and the exception that caused the error.
	 * @param error The DNS error.
	 * @param innerException The exception that caused the error.
	 */     
    public DNSException(DNSError<?> error, Exception innerException)
    {
    	this(error, "", innerException);
    }
    
	/**
	 * Constructs an exception with the DNS error, a message, and the exception that caused the error.
	 * @param error The DNS error.
	 * @param msg The exception message.
	 * @param innerException The exception that caused the error.
	 */      
    public DNSException(DNSError<?> error, String message, Exception innerException)
    {
    	super(message, innerException);
    	this.error = error;
    }
    
    public DNSError<?> getError()
    {
    	return this.error;
    }
}
