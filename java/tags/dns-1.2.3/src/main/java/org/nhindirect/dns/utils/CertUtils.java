package org.nhindirect.dns.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.dns.DNSException;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.X509CertificateEx;


public class CertUtils 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(CertUtils.class);
    static
    {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    /**
     * Takes a PKCS12 byte stream and returns a PKCS12 byte stream with the pass phrase protection and encryption removed.  
     * @param bytes The PKCS12 byte stream that will be stripped.
     * @param passphrase The pass phrase of the PKCS12 byte stream.  This is used to decrypt the PKCS12 stream.
     * @return A PKCS12 byte stream representation of the original PKCS12 stream with the pass phrase protection and encryption removed.
     */
	public static byte[] pkcs12ToStrippedPkcs12(byte[] bytes, String passphrase) throws DNSException
	{
		if (bytes == null || bytes.length == 0)
			throw new IllegalArgumentException("Pkcs byte stream cannot be null or empty.");
		
		if (passphrase == null)
			throw new IllegalArgumentException("Passphrase cannot be null.");
		
		
		byte[] retVal = null;
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    	final ByteArrayOutputStream outStr = new ByteArrayOutputStream();
        // lets try this a as a PKCS12 data stream first
        try
        {
        	final KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
        	
        	localKeyStore.load(bais, passphrase.toCharArray());
        	final Enumeration<String> aliases = localKeyStore.aliases();



    		// we are really expecting only one alias 
    		if (aliases.hasMoreElements())        			
    		{
    			final String alias = aliases.nextElement();
    			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
    			
				// check if there is private key
				final Key key = localKeyStore.getKey(alias, "".toCharArray());
				if (key != null && key instanceof PrivateKey) 
				{
					// now convert to a pcks12 format without the passphrase
					final char[] emptyPass = "".toCharArray();
					
					localKeyStore.setKeyEntry("privCert", key, emptyPass,  new java.security.cert.Certificate[] {cert});

					localKeyStore.store(outStr, emptyPass);	
					
					retVal = outStr.toByteArray();
					
				}
    		}
        }
        catch (Exception e)
        {
        	throw new DNSException("Failed to strip encryption for PKCS stream.");
        }
        finally
        {
        	try {bais.close(); }
        	catch (Exception e) {/* no-op */}
        	
        	try {outStr.close(); }
        	catch (Exception e) {/* no-op */}
        }

        return retVal;
	}
	
	/**
	 * Converts an X509Certificate to a byte stream representation.  If the certificate contains a private key, the returned representation
	 * is a PKCS12 byte stream with no pass phrase protection or encryption.
	 * @param cert The certificate to convert.
	 * @return A byte stream representation of the certificate.
	 */
	public static byte[] x509CertificateToBytes(X509Certificate cert) throws DNSException
	{
		if (cert instanceof X509CertificateEx)
		{
	    	final ByteArrayOutputStream outStr = new ByteArrayOutputStream();
			try
			{
				// return as a pkcs12 file with no encryption
				final KeyStore convertKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
				convertKeyStore.load(null, null);
				final char[] emptyPass = "".toCharArray();
				
				convertKeyStore.setKeyEntry("privCert", ((X509CertificateEx) cert).getPrivateKey(), emptyPass,  new java.security.cert.Certificate[] {cert});
				convertKeyStore.store(outStr, emptyPass);	
				
				return outStr.toByteArray();
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				throw new DNSException("Failed to convert certificate to a byte stream.");
			}
			///CLOVER:ON
	        finally
	        {	        	
	        	try {outStr.close(); }
	        	catch (Exception e) {/* no-op */}
	        }
		}
		else
		{
			try
			{
				return cert.getEncoded();
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				throw new DNSException("Failed to convert certificate to a byte stream.");
			}
			///CLOVER:ON
		}
	}
	
	/**
	 * Converts a byte stream to an X509Certificate.  The byte stream can either be an encoded X509Certificate or a PKCS12 byte stream.  
	 * <p>
	 * If the stream is a PKCS12 representation, then an empty ("") pass phrase is used to decrypt the stream.  In addition the resulting X509Certificate
	 * implementation will contain the private key.
	 * @param data  The byte stream representation to convert.
	 * @return An X509Certificate representation of the byte stream.
	 */
	public static X509Certificate toX509Certificate(byte[] data) throws DNSException
	{
		return toX509Certificate(data, "");
	}
	
	/**
	 * Converts a byte stream to an X509Certificate.  The byte stream can either be an encoded X509Certificate or a PKCS12 byte stream.  
	 * <p>
	 * If the stream is a PKCS12 representation, then the pass phrase is used to decrypt the stream.  In addition the resulting X509Certificate
	 * implementation will contain the private key.
	 * @param data The byte stream representation to convert.
	 * @param passPhrase  If the byte stream is a PKCS12 representation, then the then the pass phrase is used to decrypt the stream.  Can be
	 * null if the stream is an encoded X509Certificate and not a PKCS12 byte stream.
	 * @return  An X509Certificate representation of the byte stream.
	 */
    public static X509Certificate toX509Certificate(byte[] data, String passPhrase) throws DNSException
    {
		if (data == null || data.length == 0)
			throw new IllegalArgumentException("Byte stream cannot be null or empty.");
    	
    	// do not use a null pass phrase
    	if (passPhrase == null)
    		passPhrase = "";
    	
    	X509Certificate retVal = null;
    	ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try 
        {
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
            	
            	localKeyStore.load(bais, passPhrase.toCharArray());
            	Enumeration<String> aliases = localKeyStore.aliases();


        		// we are really expecting only one alias 
        		if (aliases.hasMoreElements())        			
        		{
        			String alias = aliases.nextElement();
        			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
        			
    				// check if there is private key
    				Key key = localKeyStore.getKey(alias, passPhrase.toCharArray());
    				if (key != null && key instanceof PrivateKey) 
    				{
    					retVal = X509CertificateEx.fromX509Certificate(cert, (PrivateKey)key);
    				}
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, try next step
            }
   
            if (retVal == null)            	
            {
            	//try X509 certificate factory next       
                bais.reset();
                bais = new ByteArrayInputStream(data);

            	retVal = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            }
        } 
        catch (Exception e) 
        {
        	throw new DNSException("Failed to convert byte stream to a certificate.");
        }
        finally
        {
        	try {bais.close();} catch (IOException ex) {}
        }
        
        return retVal;
    }
    
    /**
     * Creates an X509Certificate object from an existing file.  The file should be a DER encoded representation of the certificate.
     * @param certFile The file to load into a certificate object.
     * @return An X509Certificate loaded from the file.
     */
    public static X509Certificate certFromFile(String certFile)
    {
    	final File theCertFile = new File(certFile);
    	try
    	{
    		LOGGER.trace("Full path of cert file to load: " + theCertFile.getAbsolutePath());
    		
    		return toX509Certificate(FileUtils.readFileToByteArray(theCertFile));
    	}
    	catch (Exception e) 
    	{
    		// this is used as a factory method, so just return null if the certificate could not be loaded
    		// instead of throwing an exception, but make sure the error is logged
    		LOGGER.error("Failed to load certificate from file " + theCertFile.getAbsolutePath(), e);
    		return null;
    	}
    }
}

