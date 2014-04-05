package org.nhindirect.common.crypto.exceptions;

///CLOVER:OFF
/**
 * Generic cryptography exception thrown when perform crypto operations.
 * @author Greg Meyer
 * @since 1.3
 *
 */
public class CryptoException extends Exception
{
	
	private static final long serialVersionUID = -213341487580684180L;

	/**
	 * {@inheritDoc}
	 */
    public CryptoException() 
    {
    }

	/**
	 * {@inheritDoc}
	 */
    public CryptoException(String msg) 
    {
        super(msg);
    }

	/**
	 * {@inheritDoc}
	 */
    public CryptoException(String msg, Throwable t) 
    {
        super(msg, t);
    }

	/**
	 * {@inheritDoc}
	 */
    public CryptoException(Throwable t) 
    {
        super(t);
    }
}
///CLOVER:OFF
