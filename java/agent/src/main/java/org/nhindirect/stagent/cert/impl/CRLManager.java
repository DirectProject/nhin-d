package org.nhindirect.stagent.cert.impl;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.stagent.DefaultNHINDAgent;

import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.GeneralName;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

/**
 * Utility class for handling the storage and lookup of certificate revocation
 * lists. The class utilizes a JCS object to periodically refresh the stored
 * CRLs.
 * 
 * TODO: Add a JCS object to cache CRLs..
 * 
 * @author beau
 */
public class CRLManager {

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultNHINDAgent.class);
    
    private String defaultCrlUri;
    private Set<CRL> crlCollection;
    
    private static CertificateFactory certificateFactory;
    
    static 
    {
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
    public CRLManager() 
    { 
        this(null);
    }

    /**
     * Construct a CRLManager using a default CRL.
     */
    public CRLManager(String defaultCrlUri) 
    { 
        this.defaultCrlUri = defaultCrlUri;
        this.crlCollection = new HashSet<CRL>();
    }
    
    /**
     * Return a list of CRL objects for certificates passed to the loadCRL method.
     * 
     * @return a list of CRL objects.
     */
    public Set<CRL> getCRLCollection() 
    {
        return crlCollection;
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
    @SuppressWarnings("unchecked")
    private void loadCRLs(X509Certificate certificate)
    {
        if (certificate == null)
            return;
        
        try 
        {
            // Add default CRL
            X509CRLImpl defaultCrl = getCrlFromUri(defaultCrlUri);
            if (defaultCrl != null)
                crlCollection.add(defaultCrl);
        }
        catch (Exception e)
        {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Unable to handle default CRL: " + e.getMessage());
        }
        
        try {
            // Add CRL distribution point(s)
            X509CertImpl certificateImpl = new X509CertImpl(certificate.getEncoded());
            CRLDistributionPointsExtension crlDistributionPointsExtension = certificateImpl.getCRLDistributionPointsExtension();
    
            if (crlDistributionPointsExtension != null) 
            {
                for (DistributionPoint distributionPoint : (List<DistributionPoint>) crlDistributionPointsExtension.get(CRLDistributionPointsExtension.POINTS)) 
                {
                    for (GeneralName generalName : distributionPoint.getFullName().names()) 
                    {
                        String generalNameString = generalName.toString();

                        if (generalNameString.startsWith("URIName: ")) 
                        {
                            String crlURLString = generalNameString.substring(9);
                         
                            X509CRLImpl crlImpl = getCrlFromUri(crlURLString);
                            if (crlImpl != null)
                                crlCollection.add(crlImpl);
                        }
                    }
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
     * Determine whether or not a certificate has been revoked.
     * 
     * @param certificate
     *            The certificate to inspect.
     * @return true if the certificate has been revoked, false otherwise.
     * @throws CRLException
     */
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
    private X509CRLImpl getCrlFromUri(String crlUrlString)
    {
        if (crlUrlString == null || crlUrlString.trim().length() == 0)
            return null;
        
        X509CRLImpl crlImpl = null;
        
        try {
            URLConnection urlConnection = new URL(crlUrlString).openConnection();
            urlConnection.setConnectTimeout(3000);
            
            InputStream crlInputStream = urlConnection.getInputStream();
            
            try 
            {
                crlImpl = (X509CRLImpl) certificateFactory.generateCRL(crlInputStream);
            } 
            finally 
            {
                crlInputStream.close();
            }
        }
        catch (Exception e)
        {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Unable to retrieve or parse CRL " + crlUrlString);
        }
        
        return crlImpl;
    }
}
