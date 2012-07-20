package org.nhindirect.gateway.smtp.james.mailet;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.mailet.MailetConfig;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.mail.MailUtil;
import org.nhindirect.common.mail.dsn.DSNStandard;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.parser.EntitySerializer;

import com.sun.mail.dsn.DeliveryStatus;

public class DirectBounce_sendDSNTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		DirectBounce theMailet;
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			Map<String,String> params = new HashMap<String, String>();
			
			return new MockMailetConfig(params, "DirectBounce");	
		}
		
		@Override
		protected void setupMocks() 
		{
			theMailet = new DirectBounce();

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
			// encrypt
			String originalMessage = TestUtils.readMessageResource(getMessageToSend());
			
			MimeMessage msg = EntitySerializer.Default.deserialize(originalMessage);
			
			MockMail theMessage = new MockMail(msg);
			
			try
			{
				theMailet.service(theMessage);
			}
			catch (Exception e)
			{
				/* no-op */
			}
			doAssertions((MockMailetContext)theMailet.getMailetContext());
		}
		
		protected String getMessageToSend()
		{
			return "PlainOutgoingMessage.txt";
		}
		
		protected void doAssertions(MockMailetContext context) throws Exception
		{
		}			
	}
	
	public void testSendDSN_IMFMessage_assertDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(1, context.getSentMessages().size());
				MimeMessage dsnMessage = context.getSentMessages().iterator().next().getMessage();
				
				String originalMessageString = TestUtils.readMessageResource(getMessageToSend());
				
				MimeMessage originalMsg = EntitySerializer.Default.deserialize(originalMessageString);
				
				assertEquals(MailStandard.getHeader(originalMsg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
						MailStandard.getHeader(dsnMessage, MailStandard.Headers.To).toLowerCase(Locale.getDefault()));
				
				final DeliveryStatus status = new DeliveryStatus(new ByteArrayInputStream(MailUtil.serializeToBytes(dsnMessage)));
				
				final String rejectRecip = DSNStandard.getFinalRecipients(status);
				assertEquals(MailStandard.getHeader(originalMsg, MailStandard.Headers.To), rejectRecip);
				
			}			
		}.perform();
	}	
	
	public void testSendDSN_MDNMessage_assertNotDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageToSend()
			{
				return "MDNMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(0, context.getSentMessages().size());
			}			
		}.perform();
	}
}
