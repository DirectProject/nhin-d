package org.nhindirect.gateway.smtp.james.mailet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.apache.mailet.MailetConfig;
import org.nhind.config.Anchor;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.Setting;
import org.nhindirect.gateway.smtp.config.ConfigServiceRunner;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_initialization_Test extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		protected ConfigurationServiceProxy proxy;
		
        protected void cleanConfig() throws Exception
        {
        	
        	proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
     	        	
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
        	if (certs != null)
        		for (Certificate cert : certs)
        			proxy.removeCertificatesForOwner(cert.getOwner());
        	
        	// clean settings
        	Setting[] settings = proxy.getAllSettings();
        	if (settings != null)
        		for (Setting setting : settings)
        			proxy.deleteSetting(new String[] {setting.getName()});
        }
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		@Override
		protected void performInner() throws Exception
		{
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet();

			MailetConfig config = getMailetConfig();
			
			theMailet.init(config);
			doAssertions(theMailet);
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
		{
		}		
		
	}
	
	public void testValidMailetConfiguration_AssertProperXMLFileInitialization() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				assertNotNull(agent);
				assertNotNull(agent.getInitParameter("ConfigURL"));
				assertEquals("file://" + TestUtils.getTestConfigFile(getConfigFileName()), agent.getInitParameter("ConfigURL"));
				
			}				
		}.perform();
	}
	
	public void testValidMailetConfiguration_AssertProperWSInitialization() throws Exception 
	{
		new TestPlan() 
		{
				
			
			@Override
			protected MailetConfig getMailetConfig() throws Exception
			{
				ConfigServiceRunner.startConfigService();
				cleanConfig();
				addDomains();	
				addTrustAnchors();
				
				Map<String,String> params = new HashMap<String, String>();
				
				params.put("ConfigURL", ConfigServiceRunner.getConfigServiceURL());						
				
				
				return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
			}
			
	        protected void addDomains() throws Exception
	        {
	        	Domain dom = new Domain();
	        	dom.setDomainName("cerner.com");
	        	dom.setPostMasterEmail("postmaster@cerner.com");
	        	proxy.addDomain(dom);
	        	
	        	dom = new Domain();
	        	dom.setDomainName("securehealthemail.com");
	        	dom.setPostMasterEmail("postmaster@securehealthemail.com");
	        	proxy.addDomain(dom);
	        }
			
	        protected void addTrustAnchors() throws Exception
	        {
	        	Vector<Anchor> vec = new Vector<Anchor>();
	        	
	        	Anchor anchor = new Anchor();
	        	anchor.setData(getCertificateFileData("cacert.der"));
	        	anchor.setOwner("cerner.com");
	        	anchor.setIncoming(true);
	        	anchor.setOutgoing(true);
	        	vec.add(anchor);
	        	
	        	anchor = new Anchor();
	        	anchor.setData(getCertificateFileData("cacert.der"));
	        	anchor.setOwner("securehealthemail.com");
	        	anchor.setIncoming(true);
	        	anchor.setOutgoing(true);
	        	vec.add(anchor);
	        	
	        	proxy.addAnchor(vec.toArray(new Anchor[vec.size()]));
	        }	        
	        
			
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				assertNotNull(agent);
				assertNotNull(agent.getInitParameter("ConfigURL"));
				assertEquals(ConfigServiceRunner.getConfigServiceURL(), agent.getInitParameter("ConfigURL"));
				
			}				
		}.perform();
	}	
	
	public void testValidMailetConfiguration_AssertProperWSRESTInitialization() throws Exception 
	{
		new TestPlan() 
		{
				
			
			@Override
			protected MailetConfig getMailetConfig() throws Exception
			{
				ConfigServiceRunner.startConfigService();
				cleanConfig();
				addDomains();	
				addTrustAnchors();
				
				Map<String,String> params = new HashMap<String, String>();
				
				params.put("ConfigURL", ConfigServiceRunner.getRestAPIBaseURL());						
				params.put("SmptAgentConfigProvider", "org.nhindirect.gateway.smtp.provider.RESTSmtpAgentConfigProvider");
				
				return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
			}
			
	        protected void addDomains() throws Exception
	        {
	        	Domain dom = new Domain();
	        	dom.setDomainName("cerner.com");
	        	dom.setPostMasterEmail("postmaster@cerner.com");
	        	proxy.addDomain(dom);
	        	
	        	dom = new Domain();
	        	dom.setDomainName("securehealthemail.com");
	        	dom.setPostMasterEmail("postmaster@securehealthemail.com");
	        	proxy.addDomain(dom);
	        }
			
	        protected void addTrustAnchors() throws Exception
	        {
	        	Vector<Anchor> vec = new Vector<Anchor>();
	        	
	        	Anchor anchor = new Anchor();
	        	anchor.setData(getCertificateFileData("cacert.der"));
	        	anchor.setOwner("cerner.com");
	        	anchor.setIncoming(true);
	        	anchor.setOutgoing(true);
	        	vec.add(anchor);
	        	
	        	anchor = new Anchor();
	        	anchor.setData(getCertificateFileData("cacert.der"));
	        	anchor.setOwner("securehealthemail.com");
	        	anchor.setIncoming(true);
	        	anchor.setOutgoing(true);
	        	vec.add(anchor);
	        	
	        	proxy.addAnchor(vec.toArray(new Anchor[vec.size()]));
	        }	        
	        
			
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				assertNotNull(agent);
				assertNotNull(agent.getInitParameter("ConfigURL"));
				assertEquals(ConfigServiceRunner.getRestAPIBaseURL(), agent.getInitParameter("ConfigURL"));
				
			}				
		}.perform();
	}	
	
	public void testNullConfigURL_AssertMessagingException() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected MailetConfig getMailetConfig()
			{
				Map<String,String> params = new HashMap<String, String>();
				
				
				
				return new MockMailetConfig(params, "MyTest");
			
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				throw new RuntimeException();  // should not get here
			}	
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof MessagingException);
			}		
		}.perform();
	}	
	
	public void testEmptyConfigURL_AssertMessagingException() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected MailetConfig getMailetConfig()
			{
				Map<String,String> params = new HashMap<String, String>();
				params.put("ConfigURL", "");
				
				return new MockMailetConfig(params, "MyTest");
			
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				throw new RuntimeException();  // should not get here
			}	
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof MessagingException);
			}		
		}.perform();
	}
	
	public void testMalformedURL_AssertMessagingException() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected MailetConfig getMailetConfig()
			{
				Map<String,String> params = new HashMap<String, String>();
				params.put("ConfigURL", "mal/F0rmed\\UR!");
				
				return new MockMailetConfig(params, "MyTest");
			
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				throw new RuntimeException();  // should not get here
			}	
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof MessagingException);
			}		
		}.perform();
	}
	
	public void testBadConfig_AssertMessagingException() throws Exception 
	{
		new TestPlan() 
		{
			protected String getConfigFileName()
			{
				return "InvalidXMLInstance.xml";
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				throw new RuntimeException();  // should not get here
			}	
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof MessagingException);
			}		
		}.perform();
	}		
	
	protected byte[] getCertificateFileData(String file) throws Exception
	{
		File fl = new File("src/test/resources/certs/" + file);
		
		return FileUtils.readFileToByteArray(fl);
	}	
}
