package org.nhindirect.stagent;

/**
 * Generic exceptions thrown by the {@link NHINDAgent}
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NHINDException extends RuntimeException 
{
	static final long serialVersionUID = 6723803791479967054L;	
	
	Object m_error;
	
	/**
	 * Constructs an empty exception.
	 */
    public NHINDException()
    {
    	m_error = null;
    }
	
    /**
     * Constructs an exception with a generic error.
     * @param error The generic exception error.
     */
    public NHINDException(Object error)
    {
    	m_error = error;
    }
    
	/**
	 * Constructs an exception with a message.
	 * @param message The exception message.
	 */  
    public NHINDException(String message)
    {
    	super(message);
    }
    
	/**
	 * Constructs an exception with a message and a generic error.
     * @param error The generic exception error.
	 * @param msg The exception message.
	 */       
    public NHINDException(Object error, String message)
    {
    	super(message);
    	m_error = error;
    }
    
	/**
	 * Constructs an exception with a message and and the exception that caused the error.
	 * @param message The exception message.
	 * @param innerException The exception that caused the error.
	 */      
    public NHINDException(String message, Exception innerException)
    {
    	super(message, innerException);
    }
    
	/**
	 * Constructs an exception with a generic error and the exception that caused the error.
     * @param error The generic exception error.
	 * @param innerException The exception that caused the error.
	 */      
    public NHINDException(Object error, Exception innerException)
    {
    	super(innerException);
    	m_error = error;
    }
    
	/**
	 * Constructs an exception with a generic error, a message, and the exception that caused the error.
     * @param error The generic exception error.
	 * @param message The exception message.
	 * @param innerException The exception that caused the error.
	 */     
    public NHINDException(Object error, String message, Exception innerException)
    {
    	super(message, innerException);
    	m_error = error;
    }
    
    /**
     * Gets the generic exception error.
     * @return The generic exception error.
     */
    public Object getError()
    {
    	return m_error;
    }
    
    @Override
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
    	return "ERROR=" + m_error + "\r\n";
    }
}
