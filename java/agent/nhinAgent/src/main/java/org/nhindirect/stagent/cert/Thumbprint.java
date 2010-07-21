package org.nhindirect.stagent.cert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.nhindirect.stagent.NHINDException;

/**
 * An X509Certificate thumb print.  Thumb print is essentially a SHA-1 digest of the certificates der encoding.
 * @author Greg Meyer
 */
public class Thumbprint 
{	
	private final byte[] digest;
	private final String digestString;
	
	/**
	 * Creates a thumbprint of an X509Certificate.
	 * @param cert The certificate to convert.
	 * @return A thumbprint of the certificate.
	 */
	public static Thumbprint toThumbprint(X509Certificate cert)
	{
		Thumbprint retVal = null;
		
		if (cert == null)
			throw new IllegalArgumentException();
		
		try
		{
			retVal =  new Thumbprint(cert);
		}
		catch (Throwable e)
		{
			throw new NHINDException(e);
		}
		
		return retVal;
	}
	
	private Thumbprint (X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] der = cert.getEncoded();

		md.update(der);
        digest = md.digest();
        
        digestString = createStringRep();
	}
	
	/**
	 * Gets the raw byte digest of the certificate's der encoding. 
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
		
        StringBuffer buf = new StringBuffer(digest.length * 2);

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
		
		Thumbprint compareTo = (Thumbprint)obj;
		
		// deep compare
		return Arrays.equals(compareTo.digest, digest);
	}
}
