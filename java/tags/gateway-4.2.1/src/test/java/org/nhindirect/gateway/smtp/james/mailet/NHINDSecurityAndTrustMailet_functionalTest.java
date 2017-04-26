package org.nhindirect.gateway.smtp.james.mailet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.Mailet;
import org.apache.mailet.MailetConfig;
import org.nhind.config.Anchor;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.Setting;
import org.nhindirect.gateway.smtp.GatewayState;
import org.nhindirect.gateway.smtp.config.ConfigServiceRunner;
import org.nhindirect.gateway.smtp.james.mailet.NHINDSecurityAndTrustMailet;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.notifications.MDNStandard;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsParameter;
import org.nhindirect.stagent.parser.EntitySerializer;


import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_functionalTest extends TestCase 
{
	private static final String certBasePath = "src/test/resources/certs/";
	
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}	
	
	abstract class TestPlan extends BaseTestPlan 
	{		
		protected ConfigurationServiceProxy proxy;
		
		protected void setupMocks() 
		{
			// create the web service and proxy.... not really mocks
			try
			{
				OptionsManager.destroyInstance();
				
				ConfigServiceRunner.startConfigService();
				proxy = new ConfigurationServiceProxy();
				proxy.setEndpoint(ConfigServiceRunner.getConfigServiceURL());
				
				cleanConfig();
				
				addConfiguration();
				
				// setup the GatewayState to run every 1 second to test for concurrency
				GatewayState.getInstance().setSettingsUpdateInterval(1);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
        protected void removeFile(String filename){
            File delete = new File(filename);
            delete.delete();
        }
		
    	protected void tearDownMocks() 
    	{
    		// reset the gateway state
			// setup the GatewayState to run every 1 second to test for concurrency
			GatewayState.getInstance().setSettingsUpdateInterval(300);
			if (GatewayState.getInstance().isAgentSettingManagerRunning())
			{
				GatewayState.getInstance().stopAgentSettingsManager();
			}
			
			OptionsManager.destroyInstance();
    	}
        

		
		protected Mailet getMailet(String configurationFileName)  throws Exception
		{
			Mailet retVal = null;
			String configfile = TestUtils.getTestConfigFile(configurationFileName);
			Map<String,String> params = new HashMap<String, String>();
			
			if (configurationFileName.startsWith("http"))
				params.put("ConfigURL", ConfigServiceRunner.getConfigServiceURL());
			else
				params.put("ConfigURL", "file://" + configfile);
			
			retVal = new NHINDSecurityAndTrustMailet();
			
			MailetConfig mailetConfig = new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");
			
			retVal.init(mailetConfig);
			
			return retVal;
		}
		
		
		protected byte[] loadCertificateData(String certFileName) throws Exception
		{
			File fl = new File(certBasePath + certFileName);
			
			return FileUtils.readFileToByteArray(fl);
		}
			
        protected void addConfiguration() throws Exception
        {
        	addDomains();
        	
        	addTrustAnchors();
        	
        	addPublicCertificates();
        	
        	addPrivateCertificates();  
        	
        	addSettings();
        }
        
        protected void cleanConfig() throws Exception
        {
     	        	
        	// clean domains
        	int domainCount = proxy.getDomainCount();
        	Domain[] doms = proxy.listDomains(null, domainCount);
        	if (doms != null)
        		for (Domain dom : doms)
        		{
                	// clean anchors
                	proxy.removeAnchorsForOwner(dom.getDomainName());
 
        			proxy.removeDomain(dom.getDomainName());
        		}        
        	
        	// clean certificates
        	Certificate[] certs = proxy.listCertificates(0, 0x8FFFF, null);
        	if (certs != null && certs.length > 0)
        	{
        		long[] ids = new long[certs.length];
        		for (int i = 0; i < certs.length; ++i)
        			ids[i] = certs[i].getId();
        		
        		proxy.removeCertificates(ids) ;
        	}
        	
        	// clean settings
        	Setting[] settings = proxy.getAllSettings();
        	if (settings != null)
        		for (Setting setting : settings)
        			proxy.deleteSetting(new String[] {setting.getName()});
        }        
        
        protected void addTrustAnchors() throws Exception
        {
        	
        }
        
        protected void addPublicCertificates() throws Exception
        {
        	// default uses DNS
        }
        
        protected void addPrivateCertificates() throws Exception
        {
        	
        }
        
        protected void addSettings() throws Exception
        {
        	// just use default settings
        }
		
        protected void addDomains() throws Exception
        {

        }        
        
        protected void removeTestFiles()
        {
            removeFile("LDAPPrivateCertStore");
            removeFile("LDAPTrustAnchorStore");
            removeFile("LdapCacheStore");
            removeFile("DNSCacheStore");
            removeFile("WSPrivCacheStore");
            removeFile("PublicStoreKeyFile");
            removeFile("WSPublicCacheStore");
        }
        
	    protected void addCertificatesToConfig(String certFilename, String keyFileName, String email) throws Exception
	    {
	    	byte[] dataToAdd = null;
	    	if (keyFileName == null)
	    	{
	    		// just load the cert
	    		dataToAdd = loadCertificateData(certFilename);
	    	}
	    	else
	    	{
	    		dataToAdd = loadPkcs12FromCertAndKey(certFilename, keyFileName);
	    	}
	    	
	    	Certificate cert = new Certificate();
	    	cert.setData(dataToAdd);
	    	cert.setOwner(email);
	    	
	    	proxy.addCertificates(new Certificate[] {cert});
	    }
	    
	    protected byte[] loadPkcs12FromCertAndKey(String certFileName, String keyFileName) throws Exception
		{
			byte[] retVal = null;
			try
			{
				KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
				
				localKeyStore.load(null, null);
				
				byte[] certData = loadCertificateData(certFileName);
				byte[] keyData = loadCertificateData(keyFileName);
				
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				InputStream inStr = new ByteArrayInputStream(certData);
				java.security.cert.Certificate cert = cf.generateCertificate(inStr);
				inStr.close();
				
				KeyFactory kf = KeyFactory.getInstance("RSA");
				PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( keyData );
				Key privKey = kf.generatePrivate (keysp);
				
				char[] array = "".toCharArray();
				
				localKeyStore.setKeyEntry("privCert", privKey, array,  new java.security.cert.Certificate[] {cert});
				
				ByteArrayOutputStream outStr = new ByteArrayOutputStream();
				localKeyStore.store(outStr, array);
				
				retVal = outStr.toByteArray();
				
				outStr.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return retVal;
		}   		
		
		@Override
		protected abstract void performInner() throws Exception;
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected abstract String getMessageToProcess() throws Exception;
	}
	
	public void testProcessOutgoingMessageEndToEnd() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	

			
			protected void performInner() throws Exception
			{

				// encrypt
				String originalMessage = getMessageToProcess();
				
				MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
				
				// add an MDN request
				msg.setHeader(MDNStandard.Headers.DispositionNotificationTo, msg.getHeader(MailStandard.Headers.From, ","));
				
				MockMail theMessage = new MockMail(msg);
				
				Mailet theMailet = getMailet("ValidConfig.xml");
				
				theMailet.service(theMessage);
				
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				msg = theMessage.getMessage();
				
				assertTrue(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);
				
				
				// decrypt
				theMailet = getMailet("ValidConfigStateLine.txt");				
				
				theMessage = new MockMail(msg);
				
				theMailet.service(theMessage);
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				
				msg = theMessage.getMessage();
				assertFalse(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);

				Message compareMessage = new Message(theMessage.getMessage());
				
				// remove the MDN before comparison				
				compareMessage.removeHeader(MDNStandard.Headers.DispositionNotificationTo);
				
				assertEquals(originalMessage, compareMessage.toString());
				

				
			}				
					
		}.perform();
	}
	
	public void testProcessOutgoingMessageEndToEnd_tamperedRoutingHeaders_rejectPolicyOn_assertRejected() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	

			@Override
			protected Mailet getMailet(String configurationFileName)  throws Exception
			{
				Mailet retVal = null;
				String configfile = TestUtils.getTestConfigFile(configurationFileName);
				Map<String,String> params = new HashMap<String, String>();
				
				if (configurationFileName.startsWith("http"))
					params.put("ConfigURL", ConfigServiceRunner.getConfigServiceURL());
				else
					params.put("ConfigURL", "file://" + configfile);
				
				params.put(SecurityAndTrustMailetOptions.REJECT_ON_ROUTING_TAMPER, "true");
				
				retVal = new NHINDSecurityAndTrustMailet();
				
				MailetConfig mailetConfig = new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");
				
				retVal.init(mailetConfig);
				
				return retVal;
			}
			
			
			protected void performInner() throws Exception
			{
								
				// encrypt
				String originalMessage = getMessageToProcess();
				
				MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
				
				// add an MDN request
				msg.setHeader(MDNStandard.Headers.DispositionNotificationTo, msg.getHeader(MailStandard.Headers.From, ","));
				
				MockMail theMessage = new MockMail(msg);
				
				Mailet theMailet = getMailet("ValidConfig.xml");
				
				
				theMailet.service(theMessage);
				
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				msg = theMessage.getMessage();
				
				assertTrue(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);
				
				
				// decrypt
				theMailet = getMailet("ValidConfigStateLine.txt");				
				
				theMessage = new MockMail(msg);
				final MailAddress validAddress = new MailAddress(msg.getRecipients(RecipientType.TO)[0].toString());
				final MailAddress injectedAttackAddress = new MailAddress("externUser2@starugh-stateline.com");
				theMessage.setRecipients(Arrays.asList(validAddress, injectedAttackAddress));
				

				theMailet.service(theMessage);

				// rejected and ghosted
				assertEquals(Mail.GHOST, theMessage.getState());
			}				
					
		}.perform();
	}
	
	public void testProcessOutgoingMessageEndToEnd_tamperedRoutingHeaders_rejectPolicyOff_assertNotRejected() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	

			
			protected void performInner() throws Exception
			{
								
				// encrypt
				String originalMessage = getMessageToProcess();
				
				MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
				
				// add an MDN request
				msg.setHeader(MDNStandard.Headers.DispositionNotificationTo, msg.getHeader(MailStandard.Headers.From, ","));
				
				MockMail theMessage = new MockMail(msg);
				
				Mailet theMailet = getMailet("ValidConfig.xml");
				
				
				theMailet.service(theMessage);
				
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				msg = theMessage.getMessage();
				
				assertTrue(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);
				
				
				// decrypt
				theMailet = getMailet("ValidConfigStateLine.txt");				
				
				theMessage = new MockMail(msg);
				final MailAddress validAddress = new MailAddress(msg.getRecipients(RecipientType.TO)[0].toString());
				final MailAddress injectedAttackAddress = new MailAddress("externUser2@starugh-stateline.com");
				theMessage.setRecipients(Arrays.asList(validAddress, injectedAttackAddress));		

				theMailet.service(theMessage);
						
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				
				msg = theMessage.getMessage();
				assertFalse(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);
			}				
					
		}.perform();
	}
	
	public void testProcessOutgoingMessageEndToEnd_multipleProcessThreads() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	

			
			final class MessageEncrypter implements Callable<MimeMessage>
			{
				private final Mailet theMailet;
				public MessageEncrypter(Mailet theMailet)
				{
					this.theMailet = theMailet;
				}
				
				public MimeMessage call() throws Exception
				{
					// encrypt
					String originalMessage = getMessageToProcess();
					
					MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
					
					// add an MDN request
					msg.setHeader(MDNStandard.Headers.DispositionNotificationTo, msg.getHeader(MailStandard.Headers.From, ","));
					
					MockMail theMessage = new MockMail(msg);
					
	
					theMailet.service(theMessage);
					
					
					assertNotNull(theMessage);
					assertNotNull(theMessage.getMessage());
					
					msg = theMessage.getMessage();
					
					return msg;
				}
			}
			
			protected void performInner() throws Exception
			{

				ExecutorService execService = Executors.newFixedThreadPool(10);
				String originalMessage = getMessageToProcess();
				
				// execute 100 times
				GatewayState.getInstance().setSettingsUpdateInterval(1);
				Mailet theMailet = getMailet("ValidConfig.xml");
				List<Future<MimeMessage>> futures = new ArrayList<Future<MimeMessage>>();
				for (int i = 0; i < 100; ++i)
				{
					futures.add(execService.submit(new MessageEncrypter(theMailet)));
				}
				
				assertEquals(100, futures.size());
				
				GatewayState.getInstance().setSettingsUpdateInterval(300);
				for (Future<MimeMessage> future : futures)
				{
									
					// decrypt
					theMailet = getMailet("ValidConfigStateLine.txt");				
					
					MockMail theMessage = new MockMail(future.get());
					
					theMailet.service(theMessage);
					
					assertNotNull(theMessage);
					assertNotNull(theMessage.getMessage());
					
					
					MimeMessage msg = theMessage.getMessage();
					assertFalse(SMIMEStandard.isEncrypted(msg));
					assertEquals(theMessage.getState(), Mail.TRANSPORT);
	
					Message compareMessage = new Message(theMessage.getMessage());
					
					// remove the MDN before comparison				
					compareMessage.removeHeader(MDNStandard.Headers.DispositionNotificationTo);
					
					assertEquals(originalMessage, compareMessage.toString());
					
				}
				
			}				
					
		}.perform();
	}
	
	public void testProcessMDNMessageEndToEnd() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("MDNMessage.txt");
			}	

			
			protected void performInner() throws Exception
			{

				// encrypt
				String originalMessage = getMessageToProcess();
				
				MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
				
				MockMail theMessage = new MockMail(msg);
				
				Mailet theMailet = getMailet("ValidConfigStateLine.txt");	
				
				
				theMailet.service(theMessage);
				
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				msg = theMessage.getMessage();
				
				assertTrue(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);
				
				
				// decrypt
				theMailet = getMailet("ValidConfig.xml");			
				
				theMessage = new MockMail(msg);
				
				theMailet.service(theMessage);
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				
				msg = theMessage.getMessage();
				assertFalse(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);
				
			}				
					
		}.perform();
	}
		
	
	public void testProcessOutgoingMessage_NoTrustedRecipients() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainUntrustedOutgoingMessage.txt");
			}	

			
			protected void performInner() throws Exception
			{

				// encrypt
				String originalMessage = getMessageToProcess();
				
				MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
				
				// add an MDN request
				msg.setHeader(MDNStandard.Headers.DispositionNotificationTo, msg.getHeader(MailStandard.Headers.From, ","));
				
				MockMail theMessage = new MockMail(msg);
				
				Mailet theMailet = getMailet("ValidConfig.xml");
				
				theMailet.service(theMessage);
				
				assertEquals(Mail.GHOST, theMessage.getState());
			}				
						
			
		}.perform();
		
		
	}	
	
	public void testProcessOutgoingMessageWSEndToEnd() throws Exception 
	{
		new TestPlan() 
		{			
	        
	        protected void addTrustAnchors() throws Exception
	        {
	        	Vector<Anchor> vec = new Vector<Anchor>();
	        	
	        	Anchor anchor = new Anchor();
	        	anchor.setData(loadCertificateData("securehealthemail.com.der"));
	        	anchor.setOwner("securehealthemail.com");
	        	anchor.setIncoming(true);
	        	anchor.setOutgoing(true);
	        	vec.add(anchor);
	        	
	        	proxy.addAnchor(vec.toArray(new Anchor[vec.size()]));
	        }
	        
	        protected void addDomains() throws Exception
	        {
	        	Domain dom = new Domain();
	        	dom.setDomainName("securehealthemail.com");
	        	dom.setPostMasterEmail("postmaster@securehealthemail.com");
	        	proxy.addDomain(dom);
	        }
	        
	        	
	        protected void addSettings() throws Exception
	        {
	        	proxy.addSetting("PublicStoreType", "WS");
	        }
	        
	        protected void addPublicCertificates() throws Exception
	        {
	        	// will use the same cert for public and private... don't need to add twice for
	        	// this test
	        }
	        
	        protected void addPrivateCertificates() throws Exception
	        {
	        	addCertificatesToConfig("gm2552New.pkcs12", null, "gm2552@securehealthemail.com");
	        	addCertificatesToConfig("highlandclinic.pkcs12", null, "highlandclinic@securehealthemail.com");
	        	
	        }
			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessageInterDomain.txt");
			}	

			
			protected void performInner() throws Exception
			{

				// encrypt
				String originalMessage = getMessageToProcess();
				
				MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
				
				// add an MDN request
				msg.setHeader(MDNStandard.Headers.DispositionNotificationTo, msg.getHeader(MailStandard.Headers.From, ","));
				
				MockMail theMessage = new MockMail(msg);
				
				Mailet theMailet = getMailet("http");
				
				theMailet.service(theMessage);
				
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				msg = theMessage.getMessage();
				
				assertTrue(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);
				
				
				// decrypt
				theMailet = getMailet("http");				
				
				theMessage = new MockMail(msg);
				
				theMailet.service(theMessage);
				
				assertNotNull(theMessage);
				assertNotNull(theMessage.getMessage());
				
				
				msg = theMessage.getMessage();
				assertFalse(SMIMEStandard.isEncrypted(msg));
				assertEquals(theMessage.getState(), Mail.TRANSPORT);

				Message compareMessage = new Message(theMessage.getMessage());
				
				// remove the MDN before comparison
				compareMessage.removeHeader(MDNStandard.Headers.DispositionNotificationTo);
				
				assertEquals(originalMessage, compareMessage.toString());
				
			}				
					
		}.perform();
	}	
}
