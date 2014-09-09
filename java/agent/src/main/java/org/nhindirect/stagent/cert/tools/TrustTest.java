package org.nhindirect.stagent.cert.tools;

import java.io.File;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.DNSCertificateStore;
import org.nhindirect.stagent.trust.TrustChainValidator;

public class TrustTest 
{
	public static void main(String[] args)
	{
		CryptoExtensions.registerJCEProviders();
		
		
		if (args.length == 0)
		{
            printUsage();
            System.exit(-1);			
		}
		
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
        
        if (anchorFiles == null || anchorFiles.length == 0)
        {
        	System.err.println("You must provide the name of the anchor files.");
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
