package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_initialization_Test extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig()
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
	
	public void testValidMailetConfiguration_AssertProperInitialization() throws Exception 
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
}
