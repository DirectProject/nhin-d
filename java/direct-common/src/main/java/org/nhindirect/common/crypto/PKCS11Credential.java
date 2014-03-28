package org.nhindirect.common.crypto;

/**
 * Interface to access credentials for "logging into" a PKCS11 token.  Credentials may be stored in or on a variety of media including 
 * protected files, databases, or secure sockets.
 * @author Greg Meyer
 * @since 1.3
 */
public interface PKCS11Credential 
{
	public char[] getPIN();
}
