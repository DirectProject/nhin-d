package org.nhindirect.config.manager;


import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.EntityStatus;
import org.nhindirect.config.manager.printers.CertRecordPrinter;
import org.nhindirect.config.manager.printers.RecordPrinter;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.dns.tools.utils.Command;
import org.nhindirect.dns.tools.utils.StringArrayUtil;
import org.nhindirect.stagent.CryptoExtensions;
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
    
    private static final String IMPORT_PRIVATE_CERT_USAGE = "Imports a certificate with a private key and an optional passphrase. \r\n" +
            "Files should be in pkcs12 format." +
    		"\r\n  certfile [passphrase]" +
            "\r\n\t certfile: Fully qualified path and file name of the pkcs12 certificate file.  Place the file name in quotes (\"\") if there are spaces in the path or name." +
            "\r\n\t [passphrase]: Optional passphrase to decrypt the pkcs12 file.";   

    private static final String IMPORT_PRIVATE_CERT_W_WRAPPEDKEY_USAGE = "Imports a certificate with a wrapped private key. \r\n" +
            "The wrapped private key is assumed to have been wrapped by an external module and will on be unwrapped by the same module.  No password is needed" +
    		"\r\n  certfile privateKeyfile" +
            "\r\n\t certfile: Fully qualified path and file name of the certificate file in DER format.  Place the file name in quotes (\"\") if there are spaces in the path or name." +
            "\r\n\t privateKeyfile: Fully qualified path and file name of the wrapped private key file.  Place the file name in quotes (\"\") if there are spaces in the path or name.";
    
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
				writeCertsToFiles(certs);
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
			final X509Certificate cert = new CertUtils().certFromFile(fileLoc);
			

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
	
	
	@Command(name = "AddPrivateCertWithWrappedKey", usage = IMPORT_PRIVATE_CERT_W_WRAPPEDKEY_USAGE)
    public void importPrivateCertWithWrappedKey(String[] args)
	{
		final String certFileLoc = StringArrayUtil.getRequiredValue(args, 0);
		final String keyFileLoc = StringArrayUtil.getRequiredValue(args, 1);

		try
		{
			final byte[] certFileBytes = FileUtils.readFileToByteArray(new File(certFileLoc));
			final byte[] keyFileBytes = FileUtils.readFileToByteArray(new File(keyFileLoc));
			
			final X509Certificate cert = CertUtils.toX509Certificate(certFileBytes);
			
			byte[] certBytes = org.nhindirect.config.model.utils.CertUtils.certAndWrappedKeyToRawByteFormat(keyFileBytes, cert);
			
			org.nhind.config.Certificate addCert = new org.nhind.config.Certificate();
			addCert.setData(certBytes);
			addCert.setOwner(CryptoExtensions.getSubjectAddress(cert));
			addCert.setPrivateKey(cert instanceof X509CertificateEx);
			addCert.setStatus(EntityStatus.ENABLED);

			proxy.addCertificates(new org.nhind.config.Certificate[] {addCert});
			System.out.println("Successfully imported certificate.");
			
		}
		catch (IOException e)
		{
			System.out.println("Error reading file: " + e.getMessage());
			return;
		}
		catch (Exception e)
		{
			System.out.println("Error importing certificate " + e.getMessage());
		}	
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
			CertUtils.CertContainer cont = CertUtils.toCertContainer(cert.getData());
			X509Certificate transCert = cont.getCert();
			
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
			
			try
			{
				FileUtils.writeByteArrayToFile(certFile, transCert.getEncoded());						
			}
			catch (Exception e)
			{
				System.out.println("Failed to write cert file: " + certFile.getAbsolutePath()  + " :" +  e.getMessage());
			}
			++idx;
		}
	}
}
