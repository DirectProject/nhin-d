package org.nhindirect.stagent;

import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.trust.TrustEnforcementStatus;

import java.security.cert.X509Certificate;

/**
 * NHIN-Direct agent specific logic for an {@link InternetAddress}. 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NHINDAddress extends InternetAddress
{
	static final long serialVersionUID = -5804460458173783482L;	
	
    private X509Certificate m_certificate;
    private TrustEnforcementStatus m_trustStatus;
    private Collection<X509Certificate> m_trustAnchors;
        
    /**
     * Constructs an address from a string representation.  The address must be parsable into an {@link InternetAddress}.
     * @param address String representation of an address.
     */
    public NHINDAddress(String address)
    {
    	super();
    	setAddress(address);
    }

    /**
     * Constructs an address from an {@link InternetAddress}.
     * @param address The internet address.
     */    
    public NHINDAddress(InternetAddress address)
    {
    	super();
    	setAddress(address.getAddress());
    }

    /**
     * Constructs an address from a string representation and associates an X509Certificate with the address.
     * The address must be parsable into an {@link InternetAddress}.
     * @param address String representation of an address.
     * @param certificate The certificate to be associated with the address.
     */      
    public NHINDAddress(String address, X509Certificate certificate)
    {            
    	super();
        setAddress(address);
    	this.m_certificate = certificate;
    }
    
    /**
     * Gets the host associated with the address.
     * @return The host associated with the address.
     */
    public String getHost()
    {
    	String retVal = "";
    	
    	int index = this.getAddress().indexOf("@");
    	if (index >= 0)
    		retVal = this.getAddress().substring(index + 1);
    	
    	return retVal;
    }
    
    /**
     * Gets the X509 certificate associated with the address.
     * @return The X509 certificate associated with the address.  Returns null if a certificate is not associated.
     */
    public X509Certificate getCertificate()
    {
        return this.m_certificate;
    }
 
    /**
     * Associates an X509 certificate with the address.
     * @param value The certificate to associates with the address.
     */
    public void setCertificate(X509Certificate value)
    {
        this.m_certificate = value;
    }

    
    /**
     * Indicates if the address is associated with a certificate.
     * @return True is a certificate is associated.  False otherwise.
     */
    public boolean hasCertificate()
    {
        return (this.m_certificate != null);
    }
    
    /**
     * Gets all certificate anchors that this address trusts.  The returned collection is unmodifiable.
     * @return A collection of certificate anchors that are trusted by this address.
     */
    public Collection<X509Certificate> getTrustAnchors()
    {
    	return Collections.unmodifiableCollection(this.m_trustAnchors);
    }            
    
    /**
     * Gets all certificate anchors that this address trusts.  The returned collection is unmodifiable.
     * @return A collection of certificate anchors that are trusted by this address.
     */    
    public void setTrustAnchors(Collection<X509Certificate> value)
    {
        this.m_trustAnchors = value;
    }
    
    /**
     * Indicates if the address has certificate trust anchors associated with it.
     * @return True if the address has certificate trust anchors associate with it.  False otherwise.
     */
    public boolean hasTrustAnchors()
    {
        return (this.m_trustAnchors != null && this.m_trustAnchors.size() > 0);
    }
    
    /**
     * Gets the trust status of the address.
     * @return The trust status of the address.
     */
    public TrustEnforcementStatus getStatus()
    {
        return this.m_trustStatus;
    }
        
    /**
     * Sets the trust status of the address.
     * @param value The trust status of the address.
     */
    public void setStatus(TrustEnforcementStatus value)
    {
        this.m_trustStatus = value;
    }
           
    /**
     * Indicates if the provided trust status is trusted by this address.  The minimum trust status is considered to be trusted if its Enum ordinal value is greater
     * than or equal to this address' trust status ordinal value.
     * @param minTrustStatus The trust status to compare with the address' trust status.
     * @return True if the status trusted.  False otherwise.
     */
    public boolean isTrusted(TrustEnforcementStatus minTrustStatus)
    {
        return (this.m_trustStatus.compareTo(minTrustStatus) >= 0);
    }   
    
}
