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

package org.nhindirect.stagent.cert;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.impl.CRLRevocationManager;

/**
 * Abstract base class for a certificate store implementation.  It does not implement any specific certificate storage functions
 * against a certificate repository implementation.  Storage specific implementation should over ride this class to communicate
 * with the underlying storage medium.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public abstract class CertificateStore implements X509Store, CertificateResolver
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

        Collection<X509Certificate> certs = getAllCertificates();

        if (certs == null)
            return retVal;

        for (X509Certificate cert : certs) 
        {
            if (CryptoExtensions.certSubjectContainsName(cert, subjectName)) 
            {
                retVal.add(cert);
            } 
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
    public abstract Collection<X509Certificate> getAllCertificates();    
    
	
	/**
	 * {@inheritDoc}
	 */	
	public Collection<X509Certificate> getCertificates(InternetAddress address)
    {
        return getUsableCerts(address);
    }
	
    private Collection<X509Certificate> getUsableCerts(InternetAddress address)
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
        
        // search for "+" extension on the email address
        if (theAddress.indexOf("+") > -1 && theAddress.indexOf("@") > -1)
        {
        	int startIndex = theAddress.indexOf("+");
        	int endIndex = theAddress.indexOf("@");
        	
        	theAddress = theAddress.substring(0, startIndex) + theAddress.substring(endIndex);
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

        return filterUsable(certs);
    }
        
    /*
     * Removed certs that are not valid due to date expiration, CLR lists, or other revocation criteria
     */
    private Collection<X509Certificate> filterUsable(Collection<X509Certificate> certs)
    {
    	Collection<X509Certificate> filteredCerts = new ArrayList<X509Certificate>();
    	
        for (X509Certificate cert : certs)
        {
        	try
        	{
                /*
                 * flow control based on exception handling is generally bad
                 * practice, but this is how the X509Certificate checks validity
                 * based on date (instead of returning a boolean)
                 */
        		cert.checkValidity(new GregorianCalendar().getTime());
        		
        		// Search CRLs to determine if this certificate has been revoked
        		RevocationManager revocationManager = CRLRevocationManager.getInstance();
        		if (!revocationManager.isRevoked(cert))
                    filteredCerts.add(cert);
        	} 
            catch (Exception e) {/* no op.... the cert is not valid for the given time */}
        }
        
        return filteredCerts.size() == 0 ? null : filteredCerts;
    }
    

}
