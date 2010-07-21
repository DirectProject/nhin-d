package org.nhindirect.stagent.cert;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.mail.internet.InternetAddress;

/**
 * Abstract base class for a certificate store implementation.  It does not implement any specific certificate storage functions
 * against a certificate repository implementation.  Storage specific implementation should over ride this class to communicate
 * with the underlying storage medium.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public abstract class CertificateStore implements IX509Store, ICertificateService
{	
	/**
	 * {@inheritDoc}
	 */
    public abstract boolean contains(X509Certificate cert);

	/**
	 * {@inheritDoc}
	 */    
    public abstract void add(X509Certificate cert);

	/**
	 * {@inheritDoc}
	 */    
    public abstract void remove(X509Certificate cert);

	/**
	 * {@inheritDoc}
	 */    
    public Collection<X509Certificate> getCertificates(String subjectName)
    {
    	Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
    	
    	Collection<X509Certificate> certs = getCertificates();
    	
    	if (certs == null)
    		return retVal;
    	
    	for (X509Certificate cert : certs)
    	{    		
    		
    		if (cert.getSubjectDN().getName().toLowerCase(Locale.getDefault()).contains(subjectName.toLowerCase(Locale.getDefault())))
    			retVal.add(cert);
    	}
    	
    	return retVal;
    } 
    
	/**
	 * {@inheritDoc}
	 */    
    public void add(Collection<X509Certificate> certs)
    {
        if (certs == null)
        {
            throw new IllegalArgumentException();
        }

        for (X509Certificate cert : certs)
        {
            add(cert);
        }
    }
    
	/**
	 * {@inheritDoc}
	 */    
    public void remove(Collection<X509Certificate> certs)
    {
        if (certs == null)
        {
            throw new IllegalArgumentException();
        }
        
        for (X509Certificate cert : certs)
        {
            remove(cert);
        }
    }
    
	/**
	 * {@inheritDoc}
	 */    
    public void remove(String subjectName)
    {
    	Collection<X509Certificate> certs = getCertificates(subjectName);
        if (certs != null && certs.size() > 0)
        {
            remove(certs);
        }
    }
    
	/**
	 * {@inheritDoc}
	 */    
    public void update(X509Certificate cert)
    {

        if (contains(cert))
        {
            remove(cert);
        }
        add(cert);
    }
    
	/**
	 * {@inheritDoc}
	 */    
    public void update(Collection<X509Certificate> certs)
    {
        if (certs == null)
        {
            throw new IllegalArgumentException();
        }
        
        for (X509Certificate cert : certs)
        {
            update(cert);
        }
    }    
    
	/**
	 * {@inheritDoc}
	 */    
    public abstract Collection<X509Certificate> getCertificates();    
    
	/**
	 * {@inheritDoc}
	 */    
	public X509CertificateEx getPrivateCertificate(InternetAddress address)
    {
		X509Certificate retVal = getFirstUsableCert(address);
		
		if (retVal == null || !(retVal instanceof X509CertificateEx))
			return null;
		
        return (X509CertificateEx)retVal;
    }    
	
	/**
	 * {@inheritDoc}
	 */	
	public X509Certificate getCertificate(InternetAddress address)
    {
        return getFirstUsableCert(address);
    }
	
    private X509Certificate getFirstUsableCert(InternetAddress address)
    {
        if (address == null)
        {
            throw new IllegalArgumentException();
        }

        // may need to do some parsing of the address because the some email clients may send real name information along with the address
        int index = 0;
        String theAddress = address.getAddress();
        if ((index = theAddress.indexOf("<")) > -1 && theAddress.endsWith(">"))
        {
        	theAddress = theAddress.substring(index + 1);
       		theAddress = theAddress.substring(0, theAddress.length() - 1);
        }
        
        Collection<X509Certificate> certs = getCertificates("EMAILADDRESS=" + theAddress);

        if (certs == null || certs.size() == 0)
        {
        	// find by host
        	
        	if ((index = theAddress.indexOf("@")) > -1)
        	{
        		theAddress = theAddress.substring(index + 1);
        		certs = getCertificates("EMAILADDRESS=" + theAddress);
        	}
        	else
        		return null;
        }

        return findUsable(certs);
    }
        
    private X509Certificate findUsable(Collection<X509Certificate> certs)
    {
        for (X509Certificate cert : certs)
        {
        	try
        	{
        		// flow control based on exception handling is generally bad practice, but this is how the X509Certificate
        		// checks validity based on date (instead of returning a boolean)
        		cert.checkValidity(new GregorianCalendar().getTime());
        		return cert;        		
        	}
            catch (Exception e) {/* no op.... the cert is not valid for the given time */}
        }
        
        return null;
    }
}
