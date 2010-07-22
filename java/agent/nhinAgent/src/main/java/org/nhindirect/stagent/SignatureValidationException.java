package org.nhindirect.stagent;

import java.security.cert.CertificateException;

/**
 * Exception thrown when a message's signature can not be validated.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class SignatureValidationException extends CertificateException
{
	static final long serialVersionUID = 3791037981173852503L;	
	
	/**
	 * Constructs an exception with a message.
	 * @param msg The exception message.
	 */
	public SignatureValidationException(String msg)
	{
		super(msg);
	}
	
	/**
	 * Constructs an exception with an exception that caused the error.
	 * @param innerException The exception that caused the error.
	 */
	public SignatureValidationException(Exception innerException)
	{
		super(innerException);
	}	
	
	/**
	 * Constructs an exception with a message and the exception that caused the error.
	 * @param msg The exception message.
	 * @param innerException The exception that caused the error.
	 */	
	public SignatureValidationException(String msg, Exception innerException)
	{
		super(msg, innerException);
	}
}
