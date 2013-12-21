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
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.parser.EntitySerializer;

import com.sun.mail.dsn.DeliveryStatus;

public class NHINDSecurityAndTrustMailet_autoGeneratorDSNTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		NHINDSecurityAndTrustMailet theMailet;
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.CONFIG_URL_PARAM, "file://" + configfile);
			params.put(SecurityAndTrustMailetOptions.AUTO_DSN_FAILURE_CREATION_PARAM, getAutoDSNSetting());
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		@Override
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
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected String getAutoDSNSetting()
		{
			return NHINDSecurityAndTrustMailet.GENERAL_DSN_OPTION + "," + NHINDSecurityAndTrustMailet.RELIABLE_DSN_OPTION;
		}
		
		protected void doAssertions(MockMailetContext context) throws Exception
		{
		}			
	}
	
	public void testAutoGeneratorDSN_generateDNSForGeneralUntrustedRecips_assertDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainUntrustedOutgoingMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(1, context.getSentMessages().size());
				MimeMessage dsnMessage = context.getSentMessages().iterator().next().getMessage();
				
				String originalMessageString = TestUtils.readMessageResource(getMessageToSend());
				
				MimeMessage originalMsg = EntitySerializer.Default.deserialize(originalMessageString);
				
				assertEquals(MailStandard.getHeader(originalMsg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
						MailStandard.getHeader(dsnMessage, MailStandard.Headers.To).toLowerCase(Locale.getDefault()));
				
				NHINDAddress originalSenderAddress = new NHINDAddress(MailStandard.getHeader(originalMsg, MailStandard.Headers.From));
				NHINDAddress dsnFromAddress = new NHINDAddress(MailStandard.getHeader(dsnMessage, MailStandard.Headers.From));
				
				assertTrue(dsnFromAddress.getHost().toLowerCase(Locale.getDefault()).contains(originalSenderAddress.getHost().toLowerCase(Locale.getDefault())));

				
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_generateDNSForGeneralMultiRecipUntrustedRecips_assertDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainOutgoingMessageWithRejectedRecips.txt";
			}
			
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
				assertEquals("someotherrecip@nontrustedomain.org", rejectRecip);
				
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_noDSNSetting_multiRecipUntrustedRecips_assertNoDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getAutoDSNSetting()
			{
				return "";
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainOutgoingMessageWithRejectedRecips.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(0, context.getSentMessages().size());
				
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_noGeneralSetting_multiRecipUntrustedRecips_assertNoDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getAutoDSNSetting()
			{
				return NHINDSecurityAndTrustMailet.RELIABLE_DSN_OPTION;
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainOutgoingMessageWithRejectedRecips.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(0, context.getSentMessages().size());
				
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_noDSNSetting_untrustedGeneralMessage_assertDSNNotSent() throws Exception 
	{
		new TestPlan() 
		{

			protected String getAutoDSNSetting()
			{
				return "";
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainUntrustedOutgoingMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(0, context.getSentMessages().size());
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_noGeneralSetting_untrustedGeneralMessage_assertDSNNotSent() throws Exception 
	{
		new TestPlan() 
		{

			protected String getAutoDSNSetting()
			{
				return NHINDSecurityAndTrustMailet.RELIABLE_DSN_OPTION;
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainUntrustedOutgoingMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(0, context.getSentMessages().size());
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_generateDNSForReliableUntrustedRecips_assertDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainUntrustedReliableOutgoingMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(1, context.getSentMessages().size());
				
				MimeMessage dsnMessage = context.getSentMessages().iterator().next().getMessage();
				
				String originalMessageString = TestUtils.readMessageResource(getMessageToSend());
				
				MimeMessage originalMsg = EntitySerializer.Default.deserialize(originalMessageString);
				
				assertEquals(MailStandard.getHeader(originalMsg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
						MailStandard.getHeader(dsnMessage, MailStandard.Headers.To).toLowerCase(Locale.getDefault()));
				
				NHINDAddress originalSenderAddress = new NHINDAddress(MailStandard.getHeader(originalMsg, MailStandard.Headers.From));
				NHINDAddress dsnFromAddress = new NHINDAddress(MailStandard.getHeader(dsnMessage, MailStandard.Headers.From));
				
				assertTrue(dsnFromAddress.getHost().toLowerCase(Locale.getDefault()).contains(originalSenderAddress.getHost().toLowerCase(Locale.getDefault())));
			}			
		}.perform();
	}
	
	
	
	public void testAutoGeneratorDSN_noDSNSetting_reliableUntrustedRecips_assertNoDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getAutoDSNSetting()
			{
				return "";
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainUntrustedReliableOutgoingMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(0, context.getSentMessages().size());
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_noReliableSetting_reliableUntrustedRecips_assertNoDSNSent() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getAutoDSNSetting()
			{
				return NHINDSecurityAndTrustMailet.GENERAL_DSN_OPTION;
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "PlainUntrustedReliableOutgoingMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockMailetContext context) throws Exception
			{
				assertEquals(0, context.getSentMessages().size());
			}			
		}.perform();
	}
	
	public void testAutoGeneratorDSN_untrustedMDNMessage_assertNoDSNSent() throws Exception 
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
