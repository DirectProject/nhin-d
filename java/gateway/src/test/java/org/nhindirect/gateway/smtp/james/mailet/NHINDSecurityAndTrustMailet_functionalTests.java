package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.smtp.james.mailet.NHINDSecurityAndTrustMailet;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.parser.EntitySerializer;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_functionalTests extends TestCase 
{
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void performInner() throws Exception 
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet();
			MailetConfig mailetConfig = new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");
			
			theMailet.init(mailetConfig);

			MimeMessage msg = EntitySerializer.Default.deserialize(getMessageToProcess());
			
			MockMail theMessage = new MockMail(msg);
			
			theMailet.service(theMessage);
			
			doAssertions(theMessage);
		}	
		
		protected void doAssertions(Mail processedMsg) throws Exception
		{
		}
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected abstract String getMessageToProcess() throws Exception;
	}
	
	public void testProcessOutgoingMessageWithValidConfig() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	
			
			@Override
			protected void doAssertions(Mail processedMsg) throws Exception
			{
				assertNotNull(processedMsg);
				assertNotNull(processedMsg.getMessage());
				
				MimeMessage msg = processedMsg.getMessage();
				
				assertTrue(SMIMEStandard.isEncrypted(msg));
			}
			
		}.perform();
	}
}
