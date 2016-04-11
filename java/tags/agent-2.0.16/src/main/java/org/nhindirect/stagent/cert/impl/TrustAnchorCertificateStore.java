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

package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.cert.CertificateResolver;

/**
 * Trust anchor certificate store that maps domains to a set of certificates (anchors).
 * @author Greg Meyer
 *
 */
public class TrustAnchorCertificateStore implements CertificateResolver
{
	private Map<String, Collection<X509Certificate>> certs;
	
	/**
	 * Constructor
	 * @param certs A map of domains that are associated to a collection of certificates (anchors).
	 */
    public TrustAnchorCertificateStore(Map<String, Collection<X509Certificate>> certs)
    {
    	setCertificates(certs);
    }


    /**
     * Sets the mapping of domains to its certificates (anchors).
     * @param certs
     */
	public void setCertificates(Map<String, Collection<X509Certificate>> certs) 
	{
		if (certs == null)
			throw new IllegalArgumentException();
	
		this.certs = new HashMap<String, Collection<X509Certificate>>();
		
		// copy this map, but make all the domains upper case for lookups
		//Set<Entry<String, Collection<X509Certificate>>> entrySet = ;
		for (Entry<String, Collection<X509Certificate>> entry : certs.entrySet())
		{
			this.certs.put(entry.getKey().toUpperCase(Locale.getDefault()), entry.getValue());
		}
	}  
    
	/**
	 * Gets the certificates (anchors) for the address's domain.
	 * @param address The address used to search for trust anchors.  This method uses the address's domain to search
	 * for trust anchors.  Domain search is case insensitive.
	 * @return A collection of certificates (anchors) for the address's domain.
	 */
    public Collection<X509Certificate> getCertificates(InternetAddress address)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException();
	    }
	    
	    // get the certificates for this address's domain
	    String domain = NHINDAddress.getHost(address);
	    
	    // convert to upper case for lookup
	    domain = domain.toUpperCase(Locale.getDefault());
	    
	    Collection<X509Certificate> retCerts = certs.get(domain);
	    
	    if (retCerts == null)
	    	retCerts = Collections.emptyList(); // return an empty list of no certs are found
	    
	    return retCerts;
	}
}
