package org.nhindirect.config.manager;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.EntityStatus;
import org.nhindirect.config.manager.printers.CertRecordPrinter;
import org.nhindirect.config.manager.printers.CertUtils;
import org.nhindirect.config.manager.printers.RecordPrinter;
import org.nhindirect.dns.tools.utils.Command;
import org.nhindirect.dns.tools.utils.StringArrayUtil;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.X509CertificateEx;


public class CertCommands 
{
    private static final String LIST_CERTIFICATES_USAGE = "Lists certificates in the system";

    private static final String LIST_EMAIL_CERTIFICATES_USAGE = "Lists certificates by a given email address or domain" +
            "\r\n address" +
    		"\r\n\t address: The email address or domain to search for.  Certificates are searched on the subject alternative name field of legacy email address of the certificate";

    private static final String EXPORT_EMAIL_CERTIFICATES_USAGE = "Exports certificates by a given email address or domain" +
            "\r\n address" +
    		"\r\n\t address: The email address or domain to search for.  Certificates are searched on the subject alternative name field of legacy email address of the certificate";
    
    private static final String IMPORT_PUBLIC_CERT_USAGE = "Imports a certificate that does not contain private key information" +
            "\r\n  certfile" +
            "\r\n\t certfile: Fully qualified path and file name of the X509 certificate file.  Place the file name in quotes (\"\") if there are spaces in the path or name.";
    
    private static final String IMPORT_PRIVATE_CERT_USAGE = "Imports a certificate with a private key an optional passphrase. \r\n" +
            "Files should be in pkcs12 format." +
    		"\r\n  certfile [passphrase]" +
            "\r\n\t certfile: Fully qualified path and file name of the pkcs12 certificate file.  Place the file name in quotes (\"\") if there are spaces in the path or name." +
            "\r\n\t [passphrase]: Optional passphrase to decrypt the pkcs12 file.";   
    
    private static final String ADD_IPKIX_CERT_USAGE = "Add an IPKIX record with a subject and URL. \r\n" +
    		"\r\n  subject URL" +
    		"\r\n  subject: email address or domain name" +
            "\r\n\t URL: Fully qualified URL to certificate";  
    
    private static final String REMOVED_CERTIFICATE_USAGE = "Removes a certifacte from the system by owner." +
            "\r\n  owner" +
            "\r\n\t owner: owner or URL of the certificate to be removed";    
     
    
	protected ConfigurationServiceProxy proxy;
    
	protected RecordPrinter<org.nhind.config.Certificate> certPrinter;
    
	public CertCommands(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy;
		
		this.certPrinter = new CertRecordPrinter();
	}    
	
	@Command(name = "ListCerts", usage = LIST_CERTIFICATES_USAGE)
    public void listCerts(String[] args)
	{
		try
		{
			final org.nhind.config.Certificate[] certs = proxy.listCertificates(1, 1000, null);
			if (certs == null || certs.length == 0)
				System.out.println("No certificates found");
			else
			{
				certPrinter.printRecords(Arrays.asList(certs));
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup certificates: " + e.getMessage());
		}

	}	
	
	@Command(name = "ListCertsByAddress", usage = LIST_EMAIL_CERTIFICATES_USAGE)
    public void listCertsByAddress(String[] args)
	{
		String owner = StringArrayUtil.getRequiredValue(args, 0);
		
		try
		{		
			final org.nhind.config.Certificate[] certs = proxy.getCertificatesForOwner(owner, null);
			
			if (certs == null || certs.length == 0)
				System.out.println("No certificates found");
			else
			{
				certPrinter.printRecords(Arrays.asList(certs));
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup certificates: " + e.getMessage());
		}
	}	
	
	
	@Command(name = "ExportCertByAddress", usage = EXPORT_EMAIL_CERTIFICATES_USAGE)
    public void exportCertByAddress(String[] args)
	{
		String owner = StringArrayUtil.getRequiredValue(args, 0);
		
		try
		{		
			final org.nhind.config.Certificate[] certs = proxy.getCertificatesForOwner(owner, null);
			
			if (certs == null || certs.length == 0)
				System.out.println("No certificates found");
			else
			{
				certPrinter.printRecords(Arrays.asList(certs));
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup certificates: " + e.getMessage());
		}
	}
	
	@Command(name = "AddPublicCert", usage = IMPORT_PUBLIC_CERT_USAGE)
    public void importPublicCert(String[] args)
	{
		final String fileLoc = StringArrayUtil.getRequiredValue(args, 0);
		try
		{
			final X509Certificate cert = CertUtils.certFromFile(fileLoc);
			

				final org.nhind.config.Certificate addCert = new org.nhind.config.Certificate();
				addCert.setData(cert.getEncoded());
				addCert.setOwner(CryptoExtensions.getSubjectAddress(cert));
				addCert.setPrivateKey(false);
				addCert.setStatus(EntityStatus.ENABLED);

				proxy.addCertificates(new org.nhind.config.Certificate[] {addCert});
				System.out.println("Successfully imported public certificate.");
			
		}
		catch (IOException e)
		{
			System.out.println("Error reading file " + fileLoc + " : " + e.getMessage());
			return;
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			System.out.println("Error importing certificate " + fileLoc + " : " + e.getMessage());
		}
		///CLOVER:ON
		
	}	
	
	@Command(name = "AddPrivateCert", usage = IMPORT_PRIVATE_CERT_USAGE)
    public void importPrivateCert(String[] args)
	{
		final String fileLoc = StringArrayUtil.getRequiredValue(args, 0);
		final String passPhrase = StringArrayUtil.getOptionalValue(args, 1, "");
		try
		{
			
			final byte[] certBytes = FileUtils.readFileToByteArray(new File(fileLoc));
			
			final byte[] insertBytes = (passPhrase == null || passPhrase.isEmpty()) ?
					certBytes : CertUtils.pkcs12ToStrippedPkcs12(certBytes, passPhrase);
			
			final X509Certificate cert = CertUtils.toX509Certificate(insertBytes);
			
			org.nhind.config.Certificate addCert = new org.nhind.config.Certificate();
			addCert.setData(certBytes);
			addCert.setOwner(CryptoExtensions.getSubjectAddress(cert));
			addCert.setPrivateKey(cert instanceof X509CertificateEx);
			addCert.setStatus(EntityStatus.ENABLED);

			proxy.addCertificates(new org.nhind.config.Certificate[] {addCert});
			System.out.println("Successfully imported private certificate.");
			
		}
		catch (IOException e)
		{
			System.out.println("Error reading file " + fileLoc + " : " + e.getMessage());
			return;
		}
		catch (Exception e)
		{
			System.out.println("Error importing certificate " + fileLoc + " : " + e.getMessage());
		}	
	}	
	
	@Command(name = "AddIPKIXCert", usage = ADD_IPKIX_CERT_USAGE)
    public void addIPKIXCert(String[] args)
	{
		final String owner = StringArrayUtil.getRequiredValue(args, 0);
		final String URL = StringArrayUtil.getRequiredValue(args, 1);
		
		try
		{

				org.nhind.config.Certificate addCert = new org.nhind.config.Certificate();
				addCert.setData(URL.getBytes());
				addCert.setOwner(owner);
				addCert.setPrivateKey(false);
				addCert.setStatus(EntityStatus.ENABLED);

				proxy.addCertificates(new org.nhind.config.Certificate[] {addCert});
				System.out.println("Successfully added IPKIX certificate URL.");

			
		}
		catch (Exception e)
		{
			System.out.println("Error add IPKIX URL: " + e.getMessage());
		}	
	}	
	
	@Command(name = "RemoveCert", usage = REMOVED_CERTIFICATE_USAGE)
    public void removeCert(String[] args)
	{
		final String owner = StringArrayUtil.getRequiredValue(args, 0);

		try
		{
			proxy.removeCertificatesForOwner(owner);
			System.out.println("Successfully removed certificate for owner." + owner);
		}
		catch (Exception e)
		{
			System.out.println("Error removing certificate for owner " + owner + " : " + e.getMessage());
		}	
	}
	
	public void setRecordPrinter(RecordPrinter<org.nhind.config.Certificate> printer)
	{
		this.certPrinter = printer; 
	}	
	
	public void setConfigurationProxy(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy; 
	}	
	
	protected void writeCertsToFiles(org.nhind.config.Certificate[] certs) throws IOException
	{
		int idx = 1;
		for (org.nhind.config.Certificate cert : certs)
		{
			X509Certificate transCert = this.certFromData(cert.getData());
			
			String certFileName= "";
			String extension = (transCert instanceof X509CertificateEx ) ? ".p12" : ".der";
			String certFileHold = CryptoExtensions.getSubjectAddress(transCert) + extension;
			if (certs.length > 1)
			{
				int index = certFileHold.lastIndexOf(".");
				if (index < 0)
					certFileHold += "(" + idx + ")";
				else
				{
					certFileName = certFileHold.substring(0, index - 1) + "(" + idx + ")" + certFileHold.substring(index);
				}
						
			}
			else
				certFileName = certFileHold;
			
			File certFile = new File(certFileName);
			if (certFile.exists())
				certFile.delete();
			
			
			System.out.println("Writing cert file: " + certFile.getAbsolutePath());
			FileUtils.writeByteArrayToFile(certFile, x509CertificateToBytes(transCert));						
			
			++idx;
		}
	}
	
    private X509Certificate certFromData(byte[] data)
    {
    	X509Certificate retVal = null;
        try 
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
            	
            	localKeyStore.load(bais, "".toCharArray());
            	Enumeration<String> aliases = localKeyStore.aliases();


        		// we are really expecting only one alias 
        		if (aliases.hasMoreElements())        			
        		{
        			String alias = aliases.nextElement();
        			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
        			
    				// check if there is private key
    				Key key = localKeyStore.getKey(alias, "".toCharArray());
    				if (key != null && key instanceof PrivateKey) 
    				{
    					retVal = X509CertificateEx.fromX509Certificate(cert, (PrivateKey)key);
    				}
    				else
    					retVal = cert;
    					
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, go on to next step
            }
   
            if (retVal == null)            	
            {
            	//try X509 certificate factory next       
                bais.reset();
                bais = new ByteArrayInputStream(data);

                retVal = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);            	
            }
            bais.close();
        } 
        catch (Exception e) 
        {
            throw new NHINDException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return retVal;
    }	
    
	public static byte[] x509CertificateToBytes(X509Certificate cert)
	{
		if (cert instanceof X509CertificateEx)
		{
	    	final ByteArrayOutputStream outStr = new ByteArrayOutputStream();
			try
			{
				// return as a pkcs12 file with no encryption
				final KeyStore convertKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
				convertKeyStore.load(null, null);
				final char[] emptyPass = "".toCharArray();
				
				convertKeyStore.setKeyEntry("privCert", ((X509CertificateEx) cert).getPrivateKey(), emptyPass,  new java.security.cert.Certificate[] {cert});
				convertKeyStore.store(outStr, emptyPass);	
				
				return outStr.toByteArray();
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				throw new NHINDException("Failed to convert certificate to a byte stream.", e);
			}
			///CLOVER:ON
	        finally
	        {	        	
	        	try {outStr.close(); }
	        	catch (Exception e) {/* no-op */}
	        }
		}
		else
		{
			try
			{
				return cert.getEncoded();
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				throw new NHINDException("Failed to convert certificate to a byte stream.", e);
			}
			///CLOVER:ON
		}
	}    
}
