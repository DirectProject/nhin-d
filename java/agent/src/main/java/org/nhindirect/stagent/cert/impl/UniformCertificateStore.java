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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.X509Store;
import org.nhindirect.stagent.cert.impl.annotation.UniformCertStoreCerts;

import com.google.inject.Inject;

/**
 * Certificate store when an entire organization is represented by a single certificate.  This should not be used if a single agent instance is
 * used to manage multiple organizations/domains.
 * @author Greg Meyer
 *
 */
public class UniformCertificateStore implements CertificateResolver
{
	private Collection<X509Certificate> certs;
	
    public UniformCertificateStore(X509Certificate cert)
    {
    	certs = new ArrayList<X509Certificate>();
    	certs.add(cert);
    }
    
    @Inject
    public UniformCertificateStore(@UniformCertStoreCerts Collection<X509Certificate> certs)
    {
    	setCertificates(certs);
    }    
    
    public UniformCertificateStore(X509Store certs)
    {
        if (certs == null)
        {
            throw new IllegalArgumentException();
        }
        this.setCertificates(certs.getAllCertificates());
    }    

    public void setCertificates(Collection<X509Certificate> certs)
	{
        if (certs == null || certs.size() == 0)
        {
            throw new IllegalArgumentException("Empty or null certificates are not allowed");
        }
        
        this.certs = new ArrayList<X509Certificate>(certs);
	}

    public Collection<X509Certificate> getCertificates(InternetAddress address)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException();
	    }
	    
	    return Collections.unmodifiableCollection(certs);
	}
}
