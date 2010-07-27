package org.nhindirect.stagent.cert;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

/**
 * Extended X509 certificate that contains private key information.
 * @author Greg Meyer
 *
 */
public class X509CertificateEx extends X509Certificate
{
	private final PrivateKey privKey;
	private final X509Certificate internalCert;
	
	/**
	 * Creates X509CertificateEx object from an existing certificate and its private key.
	 * @param cert The original certificate.
	 * @param privKey The certificates private key.
	 * @return A certificate wrapper that contains the original certificates and its private key.
	 */
	public static X509CertificateEx fromX509Certificate(X509Certificate cert, PrivateKey privKey)
	{
		if (cert == null || privKey == null)
			throw new IllegalArgumentException();
		
		return new X509CertificateEx(cert, privKey);
	}
	
	
	private X509CertificateEx(X509Certificate cert, PrivateKey _privKey)
	{
		internalCert = cert;
		privKey = _privKey;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException
	{
		internalCert.checkValidity();
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException
	{
		internalCert.checkValidity(date);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getBasicConstraints()
	{
		return internalCert.getBasicConstraints();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */	
	public  List<String> getExtendedKeyUsage() throws CertificateParsingException
	{
		return internalCert.getExtendedKeyUsage();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */	
	public Collection<List<?>> getIssuerAlternativeNames()  throws CertificateParsingException
	{
		return internalCert.getIssuerAlternativeNames();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public Principal getIssuerDN() 	 
	{
		return internalCert.getIssuerDN();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean[] getIssuerUniqueID() 	 
	{
		return internalCert.getIssuerUniqueID();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public X500Principal getIssuerX500Principal() 
	{
		return internalCert.getIssuerX500Principal();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean[] getKeyUsage() 
	{
		return internalCert.getKeyUsage();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public Date getNotAfter()  
	{
		return internalCert.getNotAfter();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public Date getNotBefore()  
	{
		return internalCert.getNotBefore();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public BigInteger getSerialNumber()   
	{
		return internalCert.getSerialNumber();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public String getSigAlgName() 
	{
		return internalCert.getSigAlgName();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public String getSigAlgOID()  
	{
		return internalCert.getSigAlgOID();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public byte[] getSigAlgParams()  
	{
		return internalCert.getSigAlgParams();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public byte[] getSignature()
	{
		return internalCert.getSignature();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException
	{
		return internalCert.getSubjectAlternativeNames();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public Principal getSubjectDN()  
	{
		return internalCert.getSubjectDN();
	}		
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean[] getSubjectUniqueID()   
	{
		return internalCert.getSubjectUniqueID();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public X500Principal getSubjectX500Principal() 
	{
		return internalCert.getSubjectX500Principal();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public byte[] getTBSCertificate() throws CertificateEncodingException
	{
		return internalCert.getTBSCertificate();
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getVersion()
	{
		return internalCert.getVersion();
	}		
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object other)
	{
		return internalCert.equals(other);
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public byte[] getEncoded() throws CertificateEncodingException
	{
		return internalCert.getEncoded();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public PublicKey getPublicKey()
	{
		return internalCert.getPublicKey();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() 
	{
		return internalCert.hashCode() ;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		return internalCert.toString() ;
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public void verify(PublicKey key) throws CertificateException,
    NoSuchAlgorithmException,
    InvalidKeyException,
    NoSuchProviderException,
    SignatureException
	{
		internalCert.verify(key);
	}	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public void verify(PublicKey key, String sigProvider) throws CertificateException,
    NoSuchAlgorithmException,
    InvalidKeyException,
    NoSuchProviderException,
    SignatureException
	{
		internalCert.verify(key, sigProvider);
	}	

	/**
	 * {@inheritDoc}
	 */
	public boolean hasUnsupportedCriticalExtension()
	{
		return internalCert.hasUnsupportedCriticalExtension();
	}
	
	/**
	 * {@inheritDoc}
	 */
    public Set<String> getCriticalExtensionOIDs()
    {
    	return internalCert.getCriticalExtensionOIDs();
    }

	/**
	 * {@inheritDoc}
	 */
    public Set<String> getNonCriticalExtensionOIDs()
    {
    	return internalCert.getNonCriticalExtensionOIDs();
    }

	/**
	 * {@inheritDoc}
	 */
    public byte[] getExtensionValue(String oid)
    {
    	return internalCert.getExtensionValue(oid);
    }
    
    /**
     * Indicates if the certificate contains its private key.
     * @return True if the certificate has access to its private key.  False otherwise.
     */
    public boolean hasPrivateKey()
    {
    	return privKey != null;
    }
    
    /**
     * Gets the certificates private key.
     * @return Gets the certificates private key.
     */
    public PrivateKey getPrivateKey()
    {
    	return privKey;
    }    
}
