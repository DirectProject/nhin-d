package org.nhindirect.stagent.trust;

import org.nhindirect.stagent.NHINDException;

/**
 * Exception thrown when during trust enforcement operations.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class TrustException extends NHINDException 
{    
	static final long serialVersionUID = -2194790485513875172L;	
	
	/**
	 * Constructs an exception with the trust error.
	 * @param error The trust error.
	 */    
    public TrustException(TrustError error)
    {
    	super(error);
    }
 
	/**
	 * Constructs an exception with the trust error and the exception that caused the error.
	 * @param error The trust error.
	 * @param innerException The exception that caused the error.
	 */       
    public TrustException(TrustError error, Exception innerException)
    {
    	super(error, innerException);
    }
}
