package org.nhindirect.install;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.nhindirect.stagent.cert.tools.CreatePKCS12;
import org.nhindirect.stagent.cert.tools.certgen.CertCreateFields;
import org.nhindirect.stagent.cert.tools.certgen.CertGenerator;
import org.nhindirect.stagent.cert.tools.certgen.CertLoader;

public class SubscriberCertCreator extends AbstractCertCreator
{
	protected String caCNField;
	protected String caPass;	
	protected String emailField;
	
	private CertCreateFields signer;
	private X509Certificate signerCert;
	private PrivateKey signerKey;
	
	
	public SubscriberCertCreator()
	{
		
	}
	
	public static void main(String[] args) 
	{
		final SubscriberCertCreator creator = new SubscriberCertCreator();
		
		creator.createCert(args);
		
	}
	
	@Override
	public void createCert(final String[] args)
	{
		expField = args[0]; 
		keyStr = args[1];
		pass = args[2];	
		emailField = args[3];
		caCNField = args[4];
		caPass = args[5];
		
		
		try
		{
			signer = CertLoader.loadCertificate(createNewFileName(caCNField, false), createNewFileName(caCNField, true), caPass.toCharArray());
			signerCert = signer.getSignerCert();
			signerKey = (PrivateKey)signer.getSignerKey();	
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}	
		
		Map<String, Object> attributes = new HashMap<String, Object>(); 
		attributes.put("CN", emailField);
		attributes.put("EMAILADDRESS",emailField);	
		
		attributes.put("ALLOWTOSIGN", false);	
		
		int exp = Integer.parseInt(expField);
		int keyStre =  Integer.parseInt(keyStr);
		
		CertCreateFields createFields = new CertCreateFields(attributes, createNewFileName(emailField, false), createNewFileName(emailField, true),
				pass.toCharArray(), exp, 
				keyStre, signerCert, signerKey);
		
		// create the cert
		try
		{
			CertCreateFields retCert = CertGenerator.createCertificate(createFields, true);
			CreatePKCS12.create(retCert.getNewCertFile(), retCert.getNewKeyFile(), new String(pass.toCharArray()), null);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
