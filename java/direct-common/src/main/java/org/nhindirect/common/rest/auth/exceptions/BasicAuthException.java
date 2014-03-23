package org.nhindirect.common.rest.auth.exceptions;

///CLOVER:OFF
public class BasicAuthException extends Exception
{

	private static final long serialVersionUID = -2790878395247454716L;

	/**
	 * {@inheritDoc}
	 */
    public BasicAuthException() 
    {
    }

	/**
	 * {@inheritDoc}
	 */
    public BasicAuthException(String msg) 
    {
        super(msg);
    }

	/**
	 * {@inheritDoc}
	 */
    public BasicAuthException(String msg, Throwable t) 
    {
        super(msg, t);
    }

	/**
	 * {@inheritDoc}
	 */
    public BasicAuthException(Throwable t) 
    {
        super(t);
    }
}
///CLOVER:ON
