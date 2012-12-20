package org.nhindirect.config.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.camel.Handler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.store.BundleRefreshError;
import org.nhindirect.config.store.CertificateException;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.config.store.dao.TrustBundleDao;

public class BundleRefreshProcessor 
{
	protected static final int DEFAULT_URL_CONNECTION_TIMEOUT = 10000; // 10 seconds	
	protected static final int DEFAULT_URL_READ_TIMEOUT = 10000; // 10 hour seconds	
	
    private static final Log log = LogFactory.getLog(BundleRefreshProcessor.class);
	
	protected TrustBundleDao dao;
	
	public BundleRefreshProcessor()
	{
		try
		{
	        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() 
	        {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() 
	            {
	                return null;
	            }
	            
	            public void checkClientTrusted(X509Certificate[] certs, String authType) 
	            {
	            }
	            
	            public void checkServerTrusted(X509Certificate[] certs, String authType) 
	            {
	            }
	        }};
	        
	        // Install the all-trusting trust manager
	        final SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        
	        // Create all-trusting host name verifier
	        
	        HostnameVerifier allHostsValid = new HostnameVerifier() 
	        {
	            public boolean verify(String hostname, SSLSession session) 
	            {
	                return true;
	            }
	        };
	        
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		}
		catch (Exception e)
		{
			
		}
	}
	
	public void setDao(TrustBundleDao dao)
	{
		this.dao = dao;
	}
	
	@Handler
	public void refreshBundle(TrustBundle bundle)
	{
		// track when the process started
		final Calendar processAttempStart = Calendar.getInstance(Locale.getDefault());

		
		// get the bundle from the URL
		final byte[] rawBundle = downloadBundleToByteArray(bundle, processAttempStart);
		Collection<X509Certificate> bundleCerts = null;
		if (rawBundle != null)
			bundleCerts = convertRawBundleToAnchorCollection(rawBundle);
		
		if (bundleCerts == null)
		{
			dao.updateLastUpdateError(bundle.getId(), processAttempStart, BundleRefreshError.INVALID_BUNDLE_FORMAT);
			log.warn("Failed to download bundle from URL " + bundle.getBundleURL());
		}
		
		// check to see if there is a difference in the anchor sets
		final HashSet<X509Certificate> existingSet = new HashSet<X509Certificate>();
		for (TrustBundleAnchor anchor : bundle.getTrustBundleAnchors())
		{
			try
			{
				existingSet.add(anchor.toCertificate());
			}
			catch (CertificateException e) { /*no-op */}
		}
		final HashSet<X509Certificate> downloadedSet = new HashSet<X509Certificate>();	
		downloadedSet.addAll((Collection<X509Certificate>)bundleCerts);
		
		// now lets see if there are any differences
		boolean update = false;
		if (existingSet.size() != downloadedSet.size())
			update = true;
		else
		{
			final Set<X509Certificate> intersection = new HashSet<X509Certificate>(existingSet);
			intersection.retainAll(downloadedSet);
			
			if (intersection.size() != existingSet.size())
				update = true;
		}
		
		if (update)
		{
			try
			{
				final Collection<TrustBundleAnchor> newAnchors = new ArrayList<TrustBundleAnchor>();
				for (X509Certificate downloadedAnchor : downloadedSet)
				{
					try
					{
						final TrustBundleAnchor anchorToAdd = new TrustBundleAnchor();
						anchorToAdd.setData(downloadedAnchor.getEncoded());
						newAnchors.add(anchorToAdd);
					}
					catch (Exception e) { /*no-op */}
				}

				bundle.setTrustBundleAnchors(newAnchors);
				dao.updateTrustBundleAnchors(bundle.getId(), processAttempStart, newAnchors);
			}
			catch (ConfigurationStoreException e) 
			{ 
				dao.updateLastUpdateError(bundle.getId(), processAttempStart, BundleRefreshError.INVALID_BUNDLE_FORMAT);
				log.warn("Failed to write updated bundle anchors to data store ", e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Collection<X509Certificate> convertRawBundleToAnchorCollection(byte[] rawBundle)
	{
		Collection<? extends Certificate> bundleCerts = null;
		
		// check to see if its an unsigned PKCS7 container
		try
		{
			bundleCerts = CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(rawBundle));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// didnt work... try again as a CMS signed message
		if (bundleCerts == null)
		{
			
		}
		
		return (Collection<X509Certificate>)bundleCerts;
	}
	
	protected byte[] downloadBundleToByteArray(TrustBundle bundle, Calendar processAttempStart)
	{
		InputStream inputStream = null;

		byte[] retVal = null;
		final ByteArrayOutputStream ouStream = new ByteArrayOutputStream();
		
		try
		{
			// in this case the cert is a binary representation
			// of the CERT URL... transform to a string
			final URL certURL = new URL(bundle.getBundleURL());
			
			final URLConnection connection = certURL.openConnection();
			
			// the connection is not actually made until the input stream
			// is open, so set the timeouts before getting the stream
			connection.setConnectTimeout(DEFAULT_URL_CONNECTION_TIMEOUT);
			connection.setReadTimeout(DEFAULT_URL_READ_TIMEOUT);
			
			// open the URL as in input stream
			inputStream = connection.getInputStream();
			
			int BUF_SIZE = 2048;		
			int count = 0;

			final byte buf[] = new byte[BUF_SIZE];
			
			while ((count = inputStream.read(buf)) > -1)
			{
				ouStream.write(buf, 0, count);
			}
			
			retVal = ouStream.toByteArray();
		}
		catch (SocketTimeoutException e)
		{
			dao.updateLastUpdateError(bundle.getId(), processAttempStart, BundleRefreshError.DOWNLOAD_TIMEOUT);
			log.warn("Failed to download bundle from URL " + bundle.getBundleURL(), e);
		}
		catch (Exception e)
		{
			dao.updateLastUpdateError(bundle.getId(), processAttempStart, BundleRefreshError.NOT_FOUND);
			log.warn("Failed to download bundle from URL " + bundle.getBundleURL(), e);
		}
		finally
		{
			IOUtils.closeQuietly(ouStream);
		}
		
		return retVal;
	}
}
