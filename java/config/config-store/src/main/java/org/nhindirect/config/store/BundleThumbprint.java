package org.nhindirect.config.store;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BundleThumbprint 
{
	private final byte[] digest;
	private final String digestString;
	
    /**
     * Creates a thumbprint of a byte array.
     * 
     * @param bytes
     *            The byte array to convert to a thumbprint.
     * @return A thumbprint of the byte array.
     * @throws CertificateException
     */
	public static BundleThumbprint toThumbprint(byte[] bytes) throws NoSuchAlgorithmException
	{	
		if (bytes == null)
			throw new IllegalArgumentException();
		
		final BundleThumbprint retVal = new BundleThumbprint(bytes);
		return retVal;

	}
		
	private BundleThumbprint (byte[] bytes) throws NoSuchAlgorithmException
	{
		final MessageDigest md = MessageDigest.getInstance("SHA-1");

		md.update(bytes);
        digest = md.digest();
        
        digestString = createStringRep();
	}
	
    /**
     * Gets the raw byte digest of the certificate's der encoding.
     * 
     * @return The certificates digest.
     */
	public byte[] getDigest()
	{
		return digest.clone();
	}
	
	private String createStringRep()
	{
	    final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};		
		
        final StringBuffer buf = new StringBuffer(digest.length * 2);

        for (byte bt : digest) 
        {
            buf.append(hexDigits[(bt & 0xf0) >> 4]);
            buf.append(hexDigits[bt & 0x0f]);
        }

        return buf.toString();
	}
	
	@Override
    /**
     * {@inheritDoc}
     */
	public String toString()
	{
		return digestString;
	}
	
	@Override
    /**
     * {@inheritDoc}
     */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Thumbprint))
			return false;
		
		final BundleThumbprint compareTo = (BundleThumbprint)obj;
		
		// deep compare
		return Arrays.equals(compareTo.digest, digest);
	}
}
