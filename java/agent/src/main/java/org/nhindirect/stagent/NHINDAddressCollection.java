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

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;

import java.security.cert.X509Certificate;

/**
 * A collection of NHINDAddresses.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NHINDAddressCollection extends ArrayList<NHINDAddress> 
{
	static final long serialVersionUID = -2750152014905400257L;	
	
    public static final TrustEnforcementStatus DefaultMinTrustStatus = TrustEnforcementStatus.Success_Offline;
    
    /**
     * Constructs an empty collection.
     */
    public NHINDAddressCollection()
    {
    }
    
    /**
     * Gets a collection of all certificates associated with all of the addresses in the collection.
     * @return A collection of all certificates associated with all of the addresses in the collection.
     */
    public Collection<X509Certificate> getCertificates()
    {
    	Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
        
    	for (NHINDAddress add : this)
    		if (add.hasCertificates())
    			certs.addAll(add.getCertificates());
    	
    	return certs;
    }
    
    /**
     * Gets the first available certificate the certificate collection.  This is generally used to choose a certificate for validating a message signature.
     * @return The first available certificate the certificate collection.
     */
    public X509Certificate getFirstCertificate()
    {
    	for (NHINDAddress add : this)
    		if (add.hasCertificates())
    			return add.getCertificates().iterator().next();
    	
    	return null;
    }
            
    /**
     * Gets all addresses in the collection that are trusted.
     * @return All addresses in the collection that are trusted.
     */
    public Collection<NHINDAddress> getTrusted()
    {
        return this.getTrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }

    /**
     * Gets all addresses in the collection that meet the minimum trust status.
     * @param minTrustStatus The minimum trust status.
     * @return All addresses in the collection that are trusted.
     */
    public Collection<NHINDAddress> getTrusted(TrustEnforcementStatus minTrustStatus)
    {
    	Collection<NHINDAddress> adds = new ArrayList<NHINDAddress>();
    	
    	for (NHINDAddress add : this)
    		if (add.isTrusted(minTrustStatus))
    			adds.add(add);
    			
    	return adds;
    }
    
    /**
     * Gets all addresses in the collection that are not trusted.
     * @return All addresses in the collection that are not trusted.
     */
    public Collection<NHINDAddress> getUntrusted()
    {
        return this.getUntrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }

    /**
     * Gets all addresses in the collection that do not meet the minimum trust status.
     * @param minTrustStatus The minimum trust status.
     * @return All addresses in the collection that are not trusted.
     */    
    public Collection<NHINDAddress> getUntrusted(TrustEnforcementStatus minTrustStatus)
    {
    	Collection<NHINDAddress> adds = new ArrayList<NHINDAddress>();
    	
    	for (NHINDAddress add : this)
    		if (!add.isTrusted(minTrustStatus))
    			adds.add(add);
    			
    	return adds;    	
    }
    
    /**
     * Indicates if the collection has any addresses that are trusted.
     * @return True if the collection contains any addresses that are trusted.  False otherwise.
     */
    public boolean isTrusted()
    {
        return this.isTrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }

    /**
     * Indicates if the collection has any addresses that meet the minimum trust status.
     * @param minTrustStatus The minimum trust status.
     * @return True if the collection contains any addresses that eet the minimum trust status.  False otherwise.
     */
    public boolean isTrusted(TrustEnforcementStatus minTrustStatus)
    {
    	for (NHINDAddress add : this)
    		if (!add.isTrusted(minTrustStatus))
    			return false;
    	
    	return true;    	
    }
    
    /**
     * Removes all addresses from the collection that are note trusted.
     */
    public void removeUntrusted()
    {
        this.removeUntrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }
    
    /**
     * Removes all addresses from the collection that do not meet the minimum trust status.
     */    
    public void removeUntrusted(TrustEnforcementStatus minTrustStatus)
    {
        // Remove anybody who is not trusted
    	for (int i = this.size() - 1; i >=0; --i)
    		if (!this.get(i).isTrusted(minTrustStatus))
    			this.remove(i);
    }
    
    /**
     * Converts the collection an instance of a Collection<InternetAddress> object.
     * @return
     */
    public Collection<InternetAddress> toInternetAddressCollection()
    {
    	Collection<InternetAddress> retVal = new ArrayList<InternetAddress>();
    	
    	retVal.addAll(this);
    	
    	return retVal;
    	
    }
    
    /**
     * Sets the address source type of all addresses in the collection.
     * @param source The address source type to apply to all addresses in the collection. 
     */
    public void setSource(AddressSource source)
    {
    	for (NHINDAddress addr : this)
    		addr.setSource(source);
    		
    }
    
    @Override
    /**
     * Converts the collection to a list of addresses compatible with an message routing header message (including the standard delimiter).
     * @return The collection as an RFC compliant message routing header. 
     */
    public String toString()
    {
    	return InternetAddress.toString(this.toArray(new InternetAddress[this.size()]));
    }
    
    /**
     * Generates an instance of an NHINDAddressCollection from a collection of NHINDAddress addresses.
     * @param source A collection of NHINDAddress addresses to seed this object with.
     * @return n instance of an NHINDAddressCollection object containing all of the source addresses.
     */
    public static NHINDAddressCollection create(Collection<NHINDAddress> source)
    {
        NHINDAddressCollection addresses = new NHINDAddressCollection();
        addresses.addAll(source);
        
        return addresses;
    }   
    
    /**
     * Parses an message router header to a collection of address.  The addressline may or may not include the header name.
     * @param addressesLine The raw message header.  The header name does not need to be included, but should use the proper header delimiter
     * if it is included.
     * @param source The address source type of the address line.
     * @return A collection of addresses parsed from the address line.
     */
    public static NHINDAddressCollection parse(String addressesLine, AddressSource source)
    {
    	
    	NHINDAddressCollection retVal = new NHINDAddressCollection();
    
    	if (addressesLine != null)
    	{
	    	
	    	// strip the header separator if it exists
	    	int index = addressesLine.indexOf(':');
	    	String addressString = index > -1 ? addressesLine.substring(index + 1) : addressesLine;
	    	
	    	// split out the address using the standard delimiter
	    	String[] addresses = addressString.split(String.valueOf(MailStandard.MailAddressSeparator));
	    	
	    	for (String addr : addresses)
	    		retVal.add(new NHINDAddress(addr.trim(), source));
    	}    	
    	return retVal;
    }
    
    
}
