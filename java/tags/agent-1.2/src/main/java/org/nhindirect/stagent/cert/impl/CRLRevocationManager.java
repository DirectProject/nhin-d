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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.RevocationManager;

/**
 * Utility class for handling the storage and lookup of certificate revocation
 * lists. The class utilizes a JCS object to periodically refresh the stored
 * CRLs.
 * 
 * TODO: Add a JCS object to cache CRLs..
 * 
 * @author beau
 */
public class CRLRevocationManager implements RevocationManager {

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultNHINDAgent.class);

    private static final int CRL_FETCH_TIMEOUT = 3000;
    
    private Set<CRL> crlCollection;
    
    private static CertificateFactory certificateFactory;
   
    // TODO: convert to JCS cache
    private static Map<String, X509CRL> cache;
    
    static 
    {
        cache = new HashMap<String, X509CRL>();
        
        try 
        {
            certificateFactory = CertificateFactory.getInstance("X.509");
        } 
        catch (CertificateException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Default constructor.
     */
    public CRLRevocationManager() 
    { 
        this.crlCollection = new HashSet<CRL>();
    }
    
    /**
     * Return a read-only set of CRL objects.
     * 
     * @return a read-only set of CRL objects.
     */
    private Set<CRL> getCRLCollection() 
    {
        return Collections.unmodifiableSet(crlCollection);
    }

    /**
     * Extract and fetch all CRLs stored within a given certificate. Cache is
     * updated per policy or if the cached CRL has passed planned update date.
     * This method is thread safe.
     * 
     * @param certificate
     *            The certificate from which to extract and fetch CRLs.
     * @throws CRLException
     */
    private void loadCRLs(X509Certificate certificate)
    {
        if (certificate == null)
            return;
              
        try {
        	
        	CRLDistPoint distPoints = CRLDistPoint.getInstance(getExtensionValue(certificate,
                    		X509Extensions.CRLDistributionPoints.getId()));
            
        	// Add CRL distribution point(s)
            if (distPoints != null) 
            {
            	 
                for (DistributionPoint distPoint : distPoints.getDistributionPoints())
            	{
                	String distPointURL = distPoint.getDistributionPoint().getName().toString();

                    if (distPointURL.startsWith("General")) 
                    {
                           distPointURL = getNameString(distPointURL);
                    }     

                	X509CRL crlImpl = getCrlFromUri(distPointURL);
                            if (crlImpl != null)
                                crlCollection.add(crlImpl);
                }
            } 
        }
        catch (Exception e) 
        {
            if (LOGGER.isWarnEnabled()) 
                LOGGER.warn("Unable to handle CDP CRL(s): " + e.getMessage());
        }
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public boolean isRevoked(X509Certificate certificate)
    {
        loadCRLs(certificate);

        for (CRL crl : getCRLCollection()) 
        {
            if (crl.isRevoked(certificate))
                return true;
        }
        
        return false;
    }
          
    /**
     * Create an X509CRLImpl object from a URL pointing to a valid CRL.
     * 
     * @param crlUrlString
     *            The URL of a valid CRL.
     * @return an X509CRLImpl object representing the CRL.
     * @throws Exception
     */
    private X509CRL getCrlFromUri(String crlUrlString)
    {
        if (crlUrlString == null || crlUrlString.trim().length() == 0)
            return null;
            
        synchronized(cache) 
        { 
        	
            X509CRL crlImpl = cache.get(crlUrlString);
            
            if (crlImpl != null && crlImpl.getNextUpdate().before(new Date())) 
            {
                cache.remove(crlUrlString);
                crlImpl = null;
            }
            
            if (crlImpl == null)
            {
                try 
                {
                    URLConnection urlConnection = new URL(crlUrlString).openConnection();
                    urlConnection.setConnectTimeout(CRL_FETCH_TIMEOUT);
                    
                    InputStream crlInputStream = urlConnection.getInputStream();
                    
                    try 
                    {
                        crlImpl = (X509CRL)certificateFactory.generateCRL(crlInputStream);
                    } 
                    finally 
                    {
                        crlInputStream.close();
                    }
                    
                    cache.put(crlUrlString, crlImpl);
                }
                catch (Exception e)
                {
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Unable to retrieve or parse CRL " + crlUrlString);
                }
            }
            
            return crlImpl;
        }
    }
       
    /**
     * Get the URI from the standardized generalNameString.
     * 
     * @param generalNameString
     *            the general name string.
     * @return a URI.
     */
    protected String getNameString(String generalNameString) 
    {
    	generalNameString = generalNameString.trim();
    	int index = generalNameString.indexOf("http");
    	if (index > -1)
    		generalNameString = generalNameString.substring(index);
    	
    	return generalNameString;
    }
    
    protected static DERObject getExtensionValue(
            java.security.cert.X509Extension    ext,
            String                              oid)
            throws AnnotatedException
        {
            byte[]  bytes = ext.getExtensionValue(oid);
            if (bytes == null)
            {
                return null;
            }

            return getObject(oid, bytes);
        }
        
    private static DERObject getObject(
            String oid,
            byte[] ext)
            throws AnnotatedException
    {
        try
        {
            ASN1InputStream aIn = new ASN1InputStream(ext);
            ASN1OctetString octs = (ASN1OctetString)aIn.readObject();

            aIn = new ASN1InputStream(octs.getOctets());
            return aIn.readObject();
        }
        catch (Exception e)
        {
            throw new NHINDException("exception processing extension " + oid, e);
        }
    }
    
    public static boolean isCRLDispPointDefined(X509Certificate cert)
    {
    	boolean retVal = false;
    	try
    	{
    		CRLDistPoint distPoints = CRLDistPoint.getInstance(getExtensionValue(cert,
        		X509Extensions.CRLDistributionPoints.getId()));
    		
    		if (distPoints != null && distPoints.getDistributionPoints() != null && distPoints.getDistributionPoints().length > 0)
    			retVal = true;
    	}
    	catch (Exception e){/*no-op */ }
    	
    	return retVal;
    }	
    
}
