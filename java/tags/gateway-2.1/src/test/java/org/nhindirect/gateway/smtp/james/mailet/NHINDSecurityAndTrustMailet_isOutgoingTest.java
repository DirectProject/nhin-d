package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.NHINDAddress;

public class NHINDSecurityAndTrustMailet_isOutgoingTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		protected NHINDSecurityAndTrustMailet theMailet;
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		protected void setupMocks() 
		{
			theMailet = new NHINDSecurityAndTrustMailet();

			try
			{
				MailetConfig config = getMailetConfig();
				
				theMailet.init(config);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		@Override
		protected void performInner() throws Exception
		{

			MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource(getMessageToSend())));
			NHINDAddress sender = new NHINDAddress((InternetAddress)msg.getFrom()[0]);
			
			doAssertions(theMailet.isOutgoing(msg, sender));
		}
		
		protected String getMessageToSend()
		{
			return "PlainOutgoingMessage.txt";
		}
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected void doAssertions(boolean b) throws Exception
		{
		}			
	}
	
	public void testIsOutgoingTest_senderInDomain_assertTrue() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(boolean b) throws Exception
			{
				assertTrue(b);
			}			
		}.perform();
	}
	
	public void testIsOutgoingTest_senderNotInDomain_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageToSend()
			{
				return "MultipleRecipientsIncomingMessage.txt";
			}
			@Override
			protected void doAssertions(boolean b) throws Exception
			{
				assertFalse(b);
			}			
		}.perform();
	}
	
	public void testIsOutgoingTest_encryptedMessageFromInternalDomain_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageToSend()
			{
				return "EncryptedMessage.txt";
			}
			@Override
			protected void doAssertions(boolean b) throws Exception
			{
				assertFalse(b);
			}			
		}.perform();
	}
}
