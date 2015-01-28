package org.nhindirect.stagent.cert.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.DNSCertificateStore;
import org.nhindirect.stagent.trust.TrustChainValidator;

public class TrustTest 
{
	protected static final int DEFAULT_URL_CONNECTION_TIMEOUT = 10000; // 10 seconds	
	protected static final int DEFAULT_URL_READ_TIMEOUT = 10000; // 10 hour seconds	
	
	public static void main(String[] args)
	{
		CryptoExtensions.registerJCEProviders();
		
		
		if (args.length == 0)
		{
            printUsage();
            System.exit(-1);			
		}
		
		String configServiceURL = "";
		String bundleURL = "";
		String certFileName = "";
		String[] anchorFiles = null;	
		
		// Check parameters
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];

            // Options
            if (!arg.startsWith("-"))
            {
                System.err.println("Error: Unexpected argument [" + arg + "]\n");
                printUsage();
                System.exit(-1);
            }
            else if (arg.equalsIgnoreCase("-cert"))
            {
                if (i == args.length - 1 || args[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing certificate file name");
                    System.exit(-1);
                }
                
                certFileName = args[++i];
                
            }
            else if (arg.equalsIgnoreCase("-bundleURL"))
            {
                if (i == args.length - 1 || args[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing bundle URL");
                    System.exit(-1);
                }
                
                bundleURL = args[++i];
                
            }    
            else if (arg.equalsIgnoreCase("-configServiceURL"))
            {
                if (i == args.length - 1 || args[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing config service URL");
                    System.exit(-1);
                }
                
                configServiceURL = args[++i];
                
            }                
            else if (arg.equals("-anchors"))
            {
                if (i == args.length - 1 || args[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing anchor file names");
                    System.exit(-1);
                }
                anchorFiles = args[++i].split(",");
            }
            else if (arg.equals("-help"))
            {
                printUsage();
                System.exit(-1);
            }            
            else
            {
                System.err.println("Error: Unknown argument " + arg + "\n");
                printUsage();
                System.exit(-1);
            }
        }	
        
        if (certFileName == null || certFileName.isEmpty())
        {
        	System.err.println("You must provide the name of the certificate file to test.");
        	printUsage();
        }
        
        if ((anchorFiles == null || anchorFiles.length == 0) && bundleURL.isEmpty() && configServiceURL.isEmpty())
        {
        	System.err.println("You must provide the name of the anchor files, a bundle URL, or config service URL.");
        	printUsage();
        }
        
        // load the certificates
        final File certFileToTest = new File(certFileName);
        if (!certFileToTest.exists())
        {
        	System.out.println("Certificate file " + certFileName + " does not exist.");
        	System.exit(-1);
        	return;
        }
        
        try
        {
        	final Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
        	
        	if (anchorFiles != null && anchorFiles.length > 0)
        	{
	        	for (String anchorToLoad : anchorFiles)
	        	{
	                final File anchorFile = new File(anchorToLoad);
	                if (!anchorFile.exists())
	                {
	                	System.out.println("Anchor file " + certFileName + " does not exist.");
	                	System.exit(-1);
	                	return;
	                }
	                
	                anchors.add((X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(FileUtils.openInputStream(anchorFile)));
	        	}
        	}
        	
            if (!bundleURL.isEmpty())
            {
            	final byte[] bundleBytes = downloadBundleToByteArray(bundleURL);
            	if (bundleBytes == null)
            	{
                	System.out.println("Could not get bundle at URL " + bundleURL);
                	System.exit(-1);
            	}
            		
            	final Collection<X509Certificate> bundleAnchors = convertRawBundleToAnchorCollection(bundleBytes);
            	
            	anchors.addAll(bundleAnchors);
            	
            }
        
            final X509Certificate certToTest = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(FileUtils.openInputStream(certFileToTest));
            
            final TrustChainValidator chainValidator = new TrustChainValidator();
            
            final Collection<CertificateResolver> intermediateResolvers = Arrays.asList((CertificateResolver)new DNSCertificateStore());
            
            chainValidator.setCertificateResolver(intermediateResolvers);
            
            boolean isTrusted = chainValidator.isTrusted(certToTest, anchors);
            
            if (isTrusted)
            	System.out.println("Certificate is trusted");
            else
            	System.out.println("Certificate is NOT trusted");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        System.exit(0);
	}
	
	protected static byte[] downloadBundleToByteArray(String url)
	{
		InputStream inputStream = null;

		byte[] retVal = null;
		final ByteArrayOutputStream ouStream = new ByteArrayOutputStream();
		
		try
		{
			// in this case the cert is a binary representation
			// of the CERT URL... transform to a string
			final URL certURL = new URL(url);
			
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
		///CLOVER:OFF
		catch (Exception e)
		{
			e.printStackTrace();
		}
		///CLOVER:ON
		finally
		{
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(ouStream);
		}
		
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	protected static Collection<X509Certificate> convertRawBundleToAnchorCollection(byte[] rawBundle)
	{
		Collection<? extends Certificate> bundleCerts = null;
		InputStream inStream = null;
		// check to see if its an unsigned PKCS7 container
		try
		{
			inStream = new ByteArrayInputStream(rawBundle);
			bundleCerts = CertificateFactory.getInstance("X.509").generateCertificates(inStream);
			
			// in Java 7, an invalid bundle may be returned as a null instead of throw an exception
			// if its null and has no anchors, then try again as a signed bundle
			if (bundleCerts != null && bundleCerts.size() == 0)
				bundleCerts = null;
			
		}
		catch (Exception e)
		{
			/* no-op for now.... this may not be a p7b, so try it as a signed message*/
			e.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(inStream);
		}

		return (Collection<X509Certificate>)bundleCerts;
	}
	
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java TrustTest (options)...\n\n");
        use.append("options:\n");
        use.append("-cert		The certificate file name that will be tested for trust.\n");
        use.append("\n");
        use.append("-anchors    Comma delimited list of anchors files used for trust.\n");
        

        System.err.println(use);        
    }	
}
