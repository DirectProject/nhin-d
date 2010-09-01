package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.Mailet;
import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.smtp.james.mailet.NHINDSecurityAndTrustMailet;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.parser.EntitySerializer;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_functionalTests extends TestCase 
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		protected Mailet getMailet(String configurationFileName)  throws Exception
		{
			Mailet retVal = null;
			String configfile = TestUtils.getTestConfigFile(configurationFileName);
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			retVal = new NHINDSecurityAndTrustMailet();
			MailetConfig mailetConfig = new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");
			
			retVal.init(mailetConfig);
		
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
				
				assertEquals(originalMessage, compareMessage.toString());
				
			}				
					
		}.perform();
	}
}
