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
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.nhindirect.stagent.CryptoExtensions;

public class MessageEncEnvInfoInspector 
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
        	
        	final SMIMEEnveloped env = new SMIMEEnveloped(new MimeMessage(null, inStream)); 
        	
        	String OID = env.getEncryptionAlgOID();
        	
        	System.out.println("Encryption OID: " + OID);
        	
           	
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
}
