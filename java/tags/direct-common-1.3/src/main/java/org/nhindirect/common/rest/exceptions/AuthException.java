package org.nhindirect.common.rest.exceptions;

///CLOVER:OFF
public class AuthException extends ServiceException
{
	private static final long serialVersionUID = -7896523286952218332L;

	/**
	 * {@inheritDoc}
	 */
    public AuthException() 
    {
    }

	/**
	 * {@inheritDoc}
	 */
    public AuthException(String msg) 
    {
        super(msg);
    }

	/**
	 * {@inheritDoc}
	 */
    public AuthException(String msg, Throwable t) 
    {
        super(msg, t);
    }

	/**
	 * {@inheritDoc}
	 */
    public AuthException(Throwable t) 
    {
        super(t);
    }
}
///CLOVER:ON
