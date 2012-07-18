package org.nhindirect.gateway.smtp.james.mailet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.nhindirect.common.tx.module.DefaultTxDetailParserModule;
import org.nhindirect.common.tx.module.ProviderTxServiceModule;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.parser.EntitySerializer;

import com.google.inject.Module;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_ghostConsumedMDNProcessedMessageTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		NHINDSecurityAndTrustMailet outgoingMailet;
		NHINDSecurityAndTrustMailet incomingMailet;
		
		protected MailetConfig getMailetConfig(String configFileName, String consumeMDN) throws Exception
		{
			String configFile = TestUtils.getTestConfigFile(configFileName);
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.CONFIG_URL_PARAM, "file://" + configFile);
			params.put(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, consumeMDN);
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		@Override
		protected void setupMocks() 
		{
			outgoingMailet = new NHINDSecurityAndTrustMailet()
			{
				@Override
				protected Collection<Module> getInitModules()
				{
					final Collection<Module> mods = new ArrayList<Module>();
					final ProviderTxServiceModule module = ProviderTxServiceModule.create(new MockTxServiceProvider());
					mods.add(module);
					mods.add(DefaultTxDetailParserModule.create());
					
					return mods;
				}
			};
			
			incomingMailet = new NHINDSecurityAndTrustMailet()
			{
				@Override
				protected Collection<Module> getInitModules()
				{
					final Collection<Module> mods = new ArrayList<Module>();
					final ProviderTxServiceModule module = ProviderTxServiceModule.create(new MockTxServiceProvider());
					mods.add(module);
					mods.add(DefaultTxDetailParserModule.create());
					
					return mods;
				}
			};

			try
			{
				MailetConfig config = getMailetConfig(getOutgoingConfigFileName(), "true");
				outgoingMailet.init(config);
				
				config = getMailetConfig(getIncomingConfigFileName(), getConsumeMDNSetting());
				incomingMailet.init(config);
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
			outgoingMailet.service(theMessage);
			
			// now send the encrypted message to the incoming mailet
			MockMail theIncomingMessage = new MockMail(theMessage.getMessage());
			incomingMailet.service(theIncomingMessage);
			
			doAssertions(theMessage, theIncomingMessage);
		}
		
		protected String getMessageToSend()
		{
			return "MDNMessage.txt";
		}
		
		protected String getIncomingConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected String getOutgoingConfigFileName()
		{
			return "ValidConfigStateLine.txt";
		}
		
		protected String getConsumeMDNSetting()
		{
			return "true";
		}
		
		protected void doAssertions(MockMail outgoingMail, MockMail incomingMail) throws Exception
		{
		}			
	}
	
	public void testGhostConsumedMDNProcessedMessage_MDNMessage_assertMessageGhosted() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void doAssertions(MockMail outgoingMail, MockMail incomingMail) throws Exception
			{
				assertEquals(Mail.TRANSPORT, outgoingMail.getState());
				assertEquals(Mail.GHOST, incomingMail.getState());
			}			
		}.perform();
	}
	
	public void testGhostConsumedMDNProcessedMessage_MDNMessage_consumeMDNSettingFalse_assertMessageNotGhosted() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getConsumeMDNSetting()
			{
				return "false";
			}
			
			@Override
			protected void doAssertions(MockMail outgoingMail, MockMail incomingMail) throws Exception
			{
				assertEquals(Mail.TRANSPORT, outgoingMail.getState());
				assertEquals(Mail.TRANSPORT, incomingMail.getState());
			}			
		}.perform();
	}
	
	
	
	public void testGhostConsumedMDNProcessedMessage_nonMDNMessage_assertMessageNotGhosted() throws Exception 
	{		
		new TestPlan() 
		{
			@Override
			protected String getMessageToSend()
			{
				return "PlainOutgoingMessage.txt";
			}
			
			@Override
			protected String getIncomingConfigFileName()
			{
				return "ValidConfigStateLine.txt";
			}
			
			@Override
			protected String getOutgoingConfigFileName()
			{
				return "ValidConfig.xml";

			}
			
			@Override
			protected void doAssertions(MockMail outgoingMail, MockMail incomingMail) throws Exception
			{
				assertEquals(Mail.TRANSPORT, outgoingMail.getState());
				assertEquals(Mail.TRANSPORT, incomingMail.getState());
			}		
		}.perform();
	}
}
