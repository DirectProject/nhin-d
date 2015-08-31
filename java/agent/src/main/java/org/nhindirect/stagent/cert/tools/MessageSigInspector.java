package org.nhindirect.stagent.cert.tools;

import java.io.File;
import java.io.InputStream;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.stagent.CryptoExtensions;

public class MessageSigInspector 
{
	static
	{
		CryptoExtensions.registerJCEProviders();
	}
	
	public static void main(String args[])
	{
		if (args.length == 0)
		{
            //printUsage();
            System.exit(-1);			
		}	
		
		String messgefile = null;
		
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
        
            // Options
            if (!arg.startsWith("-"))
            {
                System.err.println("Error: Unexpected argument [" + arg + "]\n");
                //printUsage();
                System.exit(-1);
            }
            
            else if (arg.equalsIgnoreCase("-msgFile"))
            {
                if (i == args.length - 1 || args[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing message file");
                    System.exit(-1);
                }
         
                messgefile = args[++i];
                
            }
            else if (arg.equals("-help"))
            {
                //printUsage();
                System.exit(-1);
            }            
            else
            {
                System.err.println("Error: Unknown argument " + arg + "\n");
                //printUsage();
                System.exit(-1);
            }
            
        }
        
        if (messgefile == null)
        {
        	System.err.println("Error: missing message file\n");
        }
        
        InputStream inStream = null;
        try
        {
        	inStream = FileUtils.openInputStream(new File(messgefile));
        	
        	MimeMessage message = new MimeMessage(null, inStream);
        	
        	MimeMultipart mm = (MimeMultipart)message.getContent();
        	
    		//byte[] messageBytes = EntitySerializer.Default.serializeToBytes(mm.getBodyPart(0).getContent());
            //MimeBodyPart signedContent = null;
            
           	//signedContent = new MimeBodyPart(new ByteArrayInputStream(messageBytes));
        	
           	final CMSSignedData signed = new CMSSignedData(null, mm.getBodyPart(1).getInputStream());
           	
	        CertStore certs = signed.getCertificatesAndCRLs("Collection", CryptoExtensions.getJCEProviderName());
	        SignerInformationStore  signers = signed.getSignerInfos();
	        @SuppressWarnings("unchecked")
			Collection<SignerInformation> c = signers.getSigners();
	        
	        System.out.println("Found " + c.size() + " signers");
	        
	        int cnt = 1;
	        for (SignerInformation signer : c)
	        {
	            Collection<? extends Certificate> certCollection = certs.getCertificates(signer.getSID());
	            if (certCollection != null && certCollection.size() > 0)
	            {
	            
	            	X509Certificate cert = (X509Certificate)certCollection.iterator().next();
	            	System.out.println("\r\nInfo for certificate " + cnt++);
	            	System.out.println("\tSubject " + cert.getSubjectDN());
	            	
	            	byte[]  bytes = cert.getExtensionValue("2.5.29.15");
	            	
	            	DERObject obj = getObject(bytes);
	            	
	            	final KeyUsage keyUsage = new KeyUsage((DERBitString)obj);
	            	
	        		final byte[] data = keyUsage.getBytes();
	        		
	        		final int intValue = (data.length == 1) ? data[0] & 0xff : (data[1] & 0xff) << 8 | (data[0] & 0xff);
	        		
	        		System.out.println("\tKey Usage: " + intValue);
	            }
	        }
           	
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	IOUtils.closeQuietly(inStream);
        }
	}
	
    protected static DERObject getObject(byte[] ext)
            throws PolicyProcessException
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
            throw new PolicyProcessException("Exception processing data ", e);
        }
        finally
        {
        	IOUtils.closeQuietly(aIn);
        }
    }	
}
