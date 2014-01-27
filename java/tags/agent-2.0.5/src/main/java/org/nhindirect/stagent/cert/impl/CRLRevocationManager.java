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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.RevocationManager;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsParameter;

/**
 * Utility class for handling the storage and lookup of certificate revocation
 * lists.  CRLs are cached in memory for short term lookup an cached to file 
 * in a location defined by the {@link OptionsParameter#CRL_CACHE_LOCATION} option (default location is directory name CrlCache
 * in the application's working directory).
 * <br>
 * The manager is implementation as a singleton object.  Instances should be obtained using {@link #getInstance()} 
 * @author beau
 * @author Greg Meyer
 */
public class CRLRevocationManager implements RevocationManager 
{

    private static final Log LOGGER = LogFactory.getFactory().getInstance(CRLRevocationManager.class);

    private static final int CRL_FETCH_TIMEOUT = 3000;
    
    private static final String DEFAULT_CRL_CACHE_LOCATION = "CrlCache";
    
    private static CertificateFactory certificateFactory;
   
    protected static final CRLRevocationManager INSTANCE;
    
    protected final static Map<String, SoftReference<X509CRL>> cache;
    
    protected static File crlCacheLocation;
   
    static 
    {
    	CryptoExtensions.registerJCEProviders();
    	
        cache = new HashMap<String, SoftReference<X509CRL>>();
        
        try 
        {
            certificateFactory = CertificateFactory.getInstance("X.509", CryptoExtensions.getJCEProviderName());
        } 
        catch (CertificateException e) 
        {
        	LOGGER.error("Failed to create certificate factory for CRL management ", e);
        } 
        catch (NoSuchProviderException e) 
        {
        	LOGGER.error("Failed to create certificate factory for CRL management ", e);
		}
        
        // initialize the cache location
        initCRLCacheLocation();
 
        INSTANCE = new CRLRevocationManager();
    }
    
    
    /**
     * Gets the instance of the revocation manager.
     * @return The revocation manager.
     */
    public static CRLRevocationManager getInstance()
    {
    	return INSTANCE;
    }
    
    /**
     * Default constructor.
     */
    public CRLRevocationManager() 
    { 
    }
    
    /**
     * Return all the CRLs currently loaded in the manager.  It does not include file cached CRLs.
     * 
     * @return A set off all the CRLs currently loaded in the manager.
     */
    public Set<CRL> getCRLCollection() 
    {
    	synchronized (cache)
    	{
    		final Set<CRL> retVal = new HashSet<CRL>();
    		for (SoftReference<X509CRL> ref : cache.values())
    		{
    			final CRL crl = ref.get();
    			if (crl != null)
    				retVal.add(crl);
    		}
    		return Collections.unmodifiableSet(retVal);
    	}
    }

    /**
     * Extract and fetch all CRLs stored within a given certificate. Cache is
     * updated per policy or if the cached CRL has passed planned update date.
     * This method is thread safe.
     * 
     * @param certificate
     *            The certificate from which to extract and fetch CRLs.
     * @return The first CRL loaded from the certificate CRL distribution points
     * @throws CRLException
     */
    protected X509CRL loadCRLs(X509Certificate certificate)
    {
        if (certificate == null)
            return null;
          
        X509CRL retVal = null;
        
        try 
        {	
        	// get the distribution points extension
        	CRLDistPoint distPoints = CRLDistPoint.getInstance(getExtensionValue(certificate,
                    		X509Extensions.CRLDistributionPoints.getId()));
            
        	// Add CRL distribution point(s)
            if (distPoints != null) 
            {
            	
            	// iterate through the distribution points and get the first CRL that can be obtained
                for (DistributionPoint distPoint : distPoints.getDistributionPoints())
            	{
                	String distPointURL = distPoint.getDistributionPoint().getName().toString();

                    if (distPointURL.startsWith("General")) 
                    {
                    	// get the actual URL associated with the name
                    	distPointURL = getNameString(distPointURL);
                    }     

                    // get the CRL from the distribution point CRL
                    retVal = getCrlFromUri(distPointURL);
                    if (retVal != null) 
                    	return retVal;  // do we need to retrieve the list from each CRL, or is each dist point identical?
                }
            } 
        }
        catch (Exception e) 
        {
            if (LOGGER.isWarnEnabled()) 
                LOGGER.warn("Unable to handle CDP CRL(s): " + e.getMessage());
        }
        
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public boolean isRevoked(X509Certificate certificate)
    {
    	if (certificate == null)
    		return false;
    	
    	final CRL crl = loadCRLs(certificate);
        if(crl == null)
        {
    		final StringBuilder builder = new StringBuilder("Cannot find a CRL for certificate.").append("\r\n\tDN: ").append(certificate.getSubjectDN());
    		builder.append("\r\n\tSerial Number: ").append(certificate.getSerialNumber().toString(16));        
        	LOGGER.warn(builder.toString());
            return false;
        }
        
        if(crl.isRevoked(certificate))
        {
        	final StringBuilder builder = new StringBuilder("Certificate is revoked by CRL ").append("\r\n\tDN: ").append(certificate.getSubjectDN());
     		builder.append("\r\n\tSerial Number: ").append(certificate.getSerialNumber().toString(16));  
     		LOGGER.warn(builder.toString());
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
    protected X509CRL getCrlFromUri(String crlUrlString)
    {
        if (crlUrlString == null || crlUrlString.trim().length() == 0)
            return null;
        
        X509CRL crlImpl = null;
        
        // check to see if the CRL is in the CRL cache
        // cached CRL objects are held as soft references, so they may be deleted from the cache
        // if memory resources are low
        synchronized(cache) 
        { 
        	final SoftReference<X509CRL> crlRef = cache.get(crlUrlString);
        	if (crlRef != null)
        	{
        		// make sure the reference is still valid
        		crlImpl = crlRef.get();
	            if ((crlImpl != null && crlImpl.getNextUpdate().before(new Date()))  || (crlImpl == null)) 
	            {
	            	// the CRL either is no longer valid, or the SoftReference has been removed
	            	// either way, remove the SoftReference object from the in memory cache
	                cache.remove(crlUrlString);
	                
	                // only removed the file from the cache if the CRL has expired
	                // don't removed if the only the SoftReference was removed
	                if (crlImpl != null)
	                {
	                	// the CRL is expired
	                	removeCrlCacheFile(crlUrlString);
	                	crlImpl = null;
	                }
	            }
        	}
        }
        
        // try to load the CRL from a cache file.... file names are a SHA-1 hash of the 
        // CRLs distribution point URI
        if (crlImpl == null)
        {
        	// get the file name
    		final String uriFileName = getCacheFileName(crlUrlString);
    		if (!uriFileName.isEmpty())
    		{
    			// create a file to load from
    			final File cacheFile = new File(uriFileName);
    			InputStream fileInStream = null;
    			try
    			{
    				// make sure the file exists before attempting to load
    				if (cacheFile.exists())
    				{
    			        synchronized(cache) 
    			        { 
        					// load the CRL from an input stream
        					fileInStream = FileUtils.openInputStream(cacheFile);

        					crlImpl = (X509CRL)certificateFactory.generateCRL(fileInStream);
        					
        					if (crlImpl == null)
        					{
        						throw new CRLException("CRL load from cache resulted in null CLR implementation instance.");
        					}
        					
        					// close the stream now because we can't delete it on windows
        					// if the stream is open
        					IOUtils.closeQuietly(fileInStream);
        					
        					fileInStream = null;
        					
	    					// make sure the CRL isn't expired
	    		            if (crlImpl != null && crlImpl.getNextUpdate().before(new Date())) 
	    		            {
	    		            	// the CRL has expired, so removed it from the cache and 
	    		            	// delete the file
	    		                cache.remove(crlUrlString);
	    		                removeCrlCacheFile(crlUrlString);
	    		                crlImpl = null;
	    		            }
	    		            else
	    		            {
	    		            	// file load successful... add it the cache
	    	                	cache.put(crlUrlString, new SoftReference<X509CRL>(crlImpl));
	    		            }
    			        }
    				}
    			}
    			catch (CRLException e)
    			{
			        synchronized(cache) 
			        { 
			        	LOGGER.warn("CRL cache file " + uriFileName + " appears to be corrupt.  Deleting file.", e);
	    				// have to close the file stream or else we can't delete file on windows
	    				IOUtils.closeQuietly(fileInStream);
			        	
			        	removeCrlCacheFile(crlUrlString);
			        }
    			}
    			catch (Throwable t)
    			{
    				LOGGER.warn("Failed to load CRL from cache file " + uriFileName, t);
    			}
    			finally
    			{
    				if (fileInStream != null)
    				{
    					IOUtils.closeQuietly(fileInStream);
    				}
    			}
    		}
        }
        
        // could not get file from memory or file cache... load from URL
        if (crlImpl == null)
        {
            try 
            {
            	// create a URL connection object from the distribution point
                URLConnection urlConnection = new URL(crlUrlString).openConnection();
                urlConnection.setConnectTimeout(CRL_FETCH_TIMEOUT);
                
                // get the input stream
                InputStream crlInputStream = urlConnection.getInputStream();
                
                try 
                {
                	// load from URI
                   crlImpl = (X509CRL)certificateFactory.generateCRL(crlInputStream);
                } 
                catch (Throwable t)
                {
                	LOGGER.warn("Failed to load CRL from URL " + crlUrlString, t);
                }
                finally 
                {
                	IOUtils.closeQuietly(crlInputStream);
                }
                
                if (crlImpl != null)
                {
                	// the CRL load was successful.... add it to the cache
                	// and write it a file
                	synchronized(cache)
                	{
                		cache.put(crlUrlString, new SoftReference<X509CRL>(crlImpl));
                		writeCRLCacheFile(crlUrlString, crlImpl);
                	}
                }
            }
            catch (Exception e)
            {
                LOGGER.warn("Unable to retrieve or parse CRL from URI " + crlUrlString);
            }
        }
        
        return crlImpl;
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
    	
    	// try http
    	int index = generalNameString.indexOf("http");
    	if (index > -1)
    	{
    		generalNameString = generalNameString.substring(index);
    	}
    	else
    	{    	
    		// try ldap
        	index = generalNameString.indexOf("ldap");
        	if (index > -1)
        	{
        		generalNameString = generalNameString.substring(index);
        	}
        	// try from file
        	else
        	{
        		index = generalNameString.indexOf("file");
            	if (index > -1)
            	{
            		generalNameString = generalNameString.substring(index);
            	}
        	}
        	
    	}
    	
    	return generalNameString;
    }
    
    /**
     * Write a CRL to a cache file based on the distribution point URI
     * @param cacheURI The URI of the CRL distribution point
     * @param crl The CRL to write to a file
     */
    protected void writeCRLCacheFile(String cacheURI, X509CRL crl)
    {
    	// get the file name based in the distribution point URI
		final String uriFileName = getCacheFileName(cacheURI);
		if (!uriFileName.isEmpty())
		{
			// build a file descriptor
			final File cacheFile = new File(uriFileName);
			OutputStream outStream = null;
			try
			{
				// if the file already exists, try to delete it
				if (cacheFile.exists())
					if (!cacheFile.delete())
					{
						LOGGER.warn("Could not delete old CRL cache file for URI " + cacheURI + "  File may become stale");
						return;
					}

				// write the CRL to a file by using the encoded bytes of the CRL
				//outStream = FileUtils.openOutputStream(cacheFile);
				//outStream.write(crl.getEncoded());
				FileUtils.writeByteArrayToFile(cacheFile, crl.getEncoded());
			}
			catch (Throwable t)
			{
				LOGGER.warn("Failed to write CRL to cache file " + uriFileName, t);
			}
			finally
			{
				IOUtils.closeQuietly(outStream);
			}
		}

    }
    
    /**
     * Deletes a CRL cache file based on the distribution point URI
     * @param cacheURI The CRL distribution URL
     */
    protected void removeCrlCacheFile(String cacheURI)
    {

    	// get the cache file name
		final String uriFileName = getCacheFileName(cacheURI);
		if (!uriFileName.isEmpty())
		{
			// build a file descriptor
			final File cacheFile = new File(uriFileName);
			try
			{
				// make sure the file exists, then try to delete it
    			if (cacheFile.exists())
    				if (!cacheFile.delete())
    					LOGGER.warn("Could not delete CRL cache file " + cacheFile.getAbsolutePath());
			}
			catch (Throwable t)
			{
				LOGGER.warn("Could not delete CRL cache file " + cacheFile.getAbsolutePath(), t);
			}
		}

    }
    
    /**
     * Builds a cache file name based on the CRL distribution point URL.  The file name is built by
     * taking a SHA-1 hash of the URI, prepending it with the configuration cache location, and adding the suffix .cache.
     * @param cacheURI The CRL distribution point URI.
     * @return A full path file name of the cache file.  If the CRL cache location is not available or a SHA-1 can't be created, null is returned.
     */
    protected static String getCacheFileName(String cacheURI)
    {
    	if (crlCacheLocation == null)
    		return "";
    	
    	String retVal = "";
    	try
    	{
			final MessageDigest md = MessageDigest.getInstance("SHA-1");
			final byte[] bURI = cacheURI.getBytes("UTF-8");
	
			md.update(bURI);
	        final byte[] digest = md.digest();
	        
	        final String digestString = createDigestStringRep(digest);
	        retVal = crlCacheLocation.getAbsolutePath() + File.separator + digestString + ".cache";
    	}
    	catch (Throwable t)
    	{
    		LOGGER.warn("Failed to create cacheURI digest for URI " + cacheURI, t);
    	}
    	
    	return retVal;
    }
    
    /**
     * Creates a string representation from a digest byte array.
     * @param digest The digest as bytes
     * @return A human readable String representation of the digest.
     */
	protected static String createDigestStringRep(byte[] digest)
	{
	    final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};		
		
        StringBuffer buf = new StringBuffer(digest.length * 2);

        for (byte bt : digest) 
        {
            buf.append(hexDigits[(bt & 0xf0) >> 4]);
            buf.append(hexDigits[bt & 0x0f]);
        }

        return buf.toString();
	}
    
    /**
     * Flushes the contents of the in memory cache and deletes
     * all cache files in the CRL cache location.
     */
    public void flush()
    {
    	synchronized(cache)
    	{
    		// clean the in memory cache
    		cache.clear();
    		
    		// clean out the file cache
    		// make sure the location is defined first
    		if (crlCacheLocation != null)
    		{
    			try
    			{
    				// blow away every file in the cache location
    				FileUtils.cleanDirectory(crlCacheLocation);
    			}
    			catch (IOException e)
    			{
    				LOGGER.warn("Failed to clean CRL cache directory " + crlCacheLocation.getAbsolutePath() 
    						+ " during flush operation.", e);
    			}
    		}
    	}
    }
    
    /**
     * Initializes the CRL cache location option
     */
    protected static void initCRLCacheLocation()
    {
        // get the location from the OptionsManager.... if it doesn't exist, then set a default location
        // of CrlCache off the working directory
        final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.CRL_CACHE_LOCATION);
        final String cacheLoc = (param == null || param.getParamValue() == null || param.getParamValue().isEmpty()) ?
        		DEFAULT_CRL_CACHE_LOCATION : param.getParamValue();
        
        // initialize the CRL cache location
        try
        {
        	crlCacheLocation = new File(cacheLoc); 
        	if (crlCacheLocation.exists())
        	{
        		// if the file location already exists and is not a directory
        		// then log a warning and disable caching
        		if (!crlCacheLocation.isDirectory())
        		{
        			LOGGER.warn("Configured CRL cache location " + cacheLoc + " already exists and is not a directory. " +
        				"CRL file caching will be disable");
        		
        			crlCacheLocation = null;
        		}
        	}
        	else
        	{
        		// force the directory to be created
        		FileUtils.forceMkdir(crlCacheLocation);
        	}
        }
        catch (Throwable t)
        {
			LOGGER.warn("Failed to initialize CRL cache location " + cacheLoc + " CRL file caching will be disable" , t);
			crlCacheLocation = null;
        }
        
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
    	ASN1InputStream aIn = null;
        try
        {
            aIn = new ASN1InputStream(ext);
            ASN1OctetString octs = (ASN1OctetString)aIn.readObject();
        	IOUtils.closeQuietly(aIn);
            
            aIn = new ASN1InputStream(octs.getOctets());
            return aIn.readObject();
        }
        catch (Exception e)
        {
            throw new NHINDException("exception processing extension " + oid, e);
        }
        finally
        {
        	IOUtils.closeQuietly(aIn);
        }
    }
    
    /**
     * Determines if a certificate has CRL distribution point extension.
     * @param cert The certificate to check.
     * @return True is the certificate contains a CRL distribution point extension.  False otherwise.
     */
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
