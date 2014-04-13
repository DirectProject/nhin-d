/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.stagent;

import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeException;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;

import java.io.UnsupportedEncodingException;
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
	
	private AddressSource source;
    private Collection<X509Certificate> certificates;
    private TrustEnforcementStatus m_trustStatus;
    private Collection<X509Certificate> m_trustAnchors;
        
    /**
     * Constructs an address from a string representation.  The address must be parsable into an {@link InternetAddress}.
     * @param address String representation of an address.
     */
    public NHINDAddress(String address)
    {
    	this(address, AddressSource.Unknown);
    }

    /**
     * Constructs an address from an {@link InternetAddress}.
     * @param address The internet address.
     */    
    public NHINDAddress(InternetAddress address)
    {
    	this(address, AddressSource.Unknown);
    }

    /**
     * Constructs an address from a string representation.  The address must be parsable into an {@link InternetAddress}.
     * @param address String representation of an address.
     * @param source Indicates the type of address respective to the message.  
     */    
    public NHINDAddress(String address, AddressSource source)
    {
    	super();
    	try
    	{
    		InternetAddress a[] = parse(address, true);
    		if (a.length > 0)
    		{
    			this.setAddress(a[0].getAddress());
    			this.setPersonal(a[0].getPersonal());
    		}
    		else
    			this.setAddress(address);
    	}
    	catch (AddressException e)
    	{
    		this.setAddress(address);
    	}
    	catch (UnsupportedEncodingException e)
    	{
    		throw new MimeException(MimeError.InvalidHeader, e);
    	}    	
    	this.source = source;    
    	this.m_trustStatus = TrustEnforcementStatus.Failed;
    }    
    
    /**
     * Constructs an address from an {@link InternetAddress}.
     * @param address The internet address.
     * @param source Indicates the type of address respective to the message.
     */     
    public NHINDAddress(InternetAddress address, AddressSource source)
    {
    	super();
    	try
    	{
   			this.setAddress(address.getAddress());
   			this.setPersonal(address.getPersonal());
    	}
    	catch (UnsupportedEncodingException e)
    	{
    		throw new MimeException(MimeError.InvalidHeader, e);
    	}    
    	
    	this.source = source;
    	setAddress(address.getAddress());
    }

    
    /**
     * Constructs an address from a string representation and associates a collection X509Certificates with the address.
     * The address must be parsable into an {@link InternetAddress}.
     * @param address String representation of an address.
     * @param certificates The certificates to be associated with the address.
     */      
    public NHINDAddress(String address, Collection<X509Certificate> certificates)
    {            
    	super();
    	try
    	{
    		InternetAddress a[] = parse(address, true);
    		if (a.length > 0)
    		{
    			this.setAddress(a[0].getAddress());
    			this.setPersonal(a[0].getPersonal());
    		}
    		else
    			this.setAddress(address);
    	}
    	catch (AddressException e)
    	{
    		this.setAddress(address);
    	}
    	catch (UnsupportedEncodingException e)
    	{
    		throw new MimeException(MimeError.InvalidHeader, e);
    	}
    	this.certificates = certificates;
    }
    
    /**
     * Gets the domain host associated with the address.
     * @return The host associated with the address.
     */
    public String getHost()
    {
    	String retVal = "";
    	
    	// remove any extra information such as < and >
    	String address = this.getAddress();
    	int index;
    	if ((index = address.indexOf('<')) > -1)
    		address = address.substring(index + 1);
    	
    	if ((index = address.indexOf('>')) > -1)
    		address = address.substring(0, index); 
    	
    	index = address.indexOf("@");
    	if (index >= 0)
    		retVal = address.substring(index + 1);
    	
    	return retVal;
    }
    
    /**
     * Gets the X509 certificates associated with the address.
     * @return The X509 certificates associated with the address.  Returns null if no certificates is not associated.
     */
    public Collection<X509Certificate> getCertificates()
    {
        return certificates;
    }
 
    /**
     * Associates an X509 certificate with the address.
     * @param value The certificate to associates with the address.
     */
    public void setCertificates(Collection<X509Certificate> certs)
    {
        this.certificates = certs;
    }

    
    /**
     * Indicates if the address is associated with a certificate.
     * @return True is a certificate is associated.  False otherwise.
     */
    public boolean hasCertificates()
    {
        return (certificates != null && certificates.size() > 0);
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
     * Sets all certificate anchors that this address trusts.
     * @param certs A collection of certificate anchors that are trusted by this address.
     */    
    public void setTrustAnchors(Collection<X509Certificate> certs)
    {
        this.m_trustAnchors = certs;
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
    
    /**
     * Gets the source type of the address such as TO, CC, and BCC.
     * @return The source type of the address
     */
    public AddressSource getSource() 
    {
		return source;
	}

    /**
     * Sets the source type of the address.
     * @param source The source type of the address.
     */
	public void setSource(AddressSource source) 
	{
		this.source = source;
	}

	/**
	 * Indicates if the address's domain matches the provided domain.  The domain check is case insensitive.
	 * @param domain The domain to match.
	 * @return True if the address's domain matches the provided domain. False otherwise.
	 */
	public boolean domainEquals(String domain)
    {
    	return getHost().equalsIgnoreCase(domain);
    }
	
	/**
	 * Indicates if the the address's domain is in the list of domains.  The domain check is case insensitive.
	 * @param domains The domain to check.
	 * @return True if the address's domain is in the list of provided domains.  False otherwise.
	 */
	public boolean isInDomain(Collection<String> domains)
	{
		for (String domain : domains)
			if (domainEquals(domain))
				return true;
		
		return false;
	}
	
    /**
     * Gets the domain host associated with the address.
     * @param theAddress The address to get the host from.
     * @return The host associated with the address.
     */
	public static String getHost(InternetAddress theAddress)
	{
    	String retVal = "";
    	
    	// remove any extra information such as < and >
    	String address = theAddress.getAddress();
    	int index;
    	if ((index = address.indexOf('<')) > -1)
    		address = address.substring(index + 1);
    	
    	if ((index = address.indexOf('>')) > -1)
    		address = address.substring(0, index); 
    	
    	index = address.indexOf("@");
    	if (index >= 0)
    		retVal = address.substring(index + 1);
    	
    	return retVal;
	}
}
