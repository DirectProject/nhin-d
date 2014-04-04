package org.nhindirect.common.rest.auth.exceptions;

///CLOVER:OFF
public class NoSuchUserException extends BasicAuthException
{
	private static final long serialVersionUID = 5907656286777902656L;

	/**
	 * {@inheritDoc}
	 */
    public NoSuchUserException() 
    {
    }

	/**
	 * {@inheritDoc}
	 */
    public NoSuchUserException(String msg) 
    {
        super(msg);
    }

	/**
	 * {@inheritDoc}
	 */
    public NoSuchUserException(String msg, Throwable t) 
    {
        super(msg, t);
    }

	/**
	 * {@inheritDoc}
	 */
    public NoSuchUserException(Throwable t) 
    {
        super(t);
    }
}
///CLOVER:ON
