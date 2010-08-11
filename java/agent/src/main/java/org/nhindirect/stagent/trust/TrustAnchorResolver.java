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

package org.nhindirect.stagent.trust;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.stagent.cert.ICertificateResolver;
import org.nhindirect.stagent.cert.IX509Store;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cert.impl.UniformCertificateStore;

public class TrustAnchorResolver implements ITrustAnchorResolver
{
    private ICertificateResolver outgoingAnchors;
    private ICertificateResolver incomingAnchors;
    
    public TrustAnchorResolver(Collection<X509Certificate> anchors)
	{
    	this(anchors, anchors);
	}    
    
    public TrustAnchorResolver(Collection<X509Certificate> outgoingAnchors, Collection<X509Certificate> incomingAnchors)
	{
    	this (new UniformCertificateStore(outgoingAnchors), new UniformCertificateStore(incomingAnchors));
	}    
    
    public TrustAnchorResolver(IX509Store anchors)
	{
    	this(anchors, anchors);
	}  
    
    public TrustAnchorResolver(IX509Store outgoingAnchors, IX509Store incomingAnchors)
	{
    	this(new UniformCertificateStore(outgoingAnchors), new UniformCertificateStore(incomingAnchors));
	}  
    
    public TrustAnchorResolver(ICertificateResolver anchors)
	{
    	this(anchors, anchors);
	}
    
    public TrustAnchorResolver(ICertificateResolver outgoingAnchors, ICertificateResolver incomingAnchors)
    {
        if (outgoingAnchors == null || incomingAnchors == null)
        {
            throw new IllegalArgumentException();
        }
        
        this.outgoingAnchors = outgoingAnchors;
        this.incomingAnchors = incomingAnchors;
    }    
    
    public ICertificateResolver getOutgoingAnchors()
    {
    	return outgoingAnchors;
    }

    public ICertificateResolver getIncomingAnchors()
    {
    	return incomingAnchors;
    }    
    
    public static TrustAnchorResolver createDefault()
    {
    	/*
    	 * TODO: add declarative methods to create a default... trust anchor distribution is an out of band process, so
    	 * just harding to a file for now
    	 */
    	KeyStoreCertificateStore store = new KeyStoreCertificateStore(new File("UniformTrustArchorStore"));
    	
        UniformCertificateStore anchors = new UniformCertificateStore(store.getAllCertificates());
        return new TrustAnchorResolver(anchors);
    }    
}
