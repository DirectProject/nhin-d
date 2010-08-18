package org.nhindirect.gateway.smtp;

import java.util.Arrays;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.module.SmtpAgentConfigModule;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.stagent.provider.MockNHINDAgentProvider;
import org.nhindirect.stagent.DefaultMessageEnvelope;

import com.google.inject.Guice;
import com.google.inject.Injector;

import junit.framework.TestCase;

public class DefaultSmtpAgent_ProcessMessage_Test extends TestCase 
{
	
	abstract class TestPlan extends BaseTestPlan 
	{

		private SmtpAgent agent;
		
		@Override
		public void setupMocks()
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Injector injector = Guice.createInjector(SmtpAgentConfigModule.create(configfile, null,
					new MockNHINDAgentProvider(Arrays.asList(new String[] {"cerner.com, securehealthemail.com"}))));
			SmtpAgentConfig config = injector.getInstance(SmtpAgentConfig.class);
			agent = config.getAgentInjector().getInstance(SmtpAgent.class);
		}
		
		@Override
		protected void performInner() throws Exception 
		{
			DefaultMessageEnvelope env = new DefaultMessageEnvelope(getMessageToProcess());
			
			MessageProcessResult result = agent.processMessage(env.getMessage(), env.getRecipients(), env.getSender());			
			doAssertions(result);
		}	
	
		protected void doAssertions(MessageProcessResult result) throws Exception
		{
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
				
		protected abstract String getMessageToProcess() throws Exception;	
	}
	
	public void testProcessValidIncomingMessage_AssertSuccessfulResultWithNoBounces() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainIncomingMessage.txt");
			}	
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNotNull(result.getProcessedMessage());
				assertNull(result.getIncomingBounceMessage());
				assertNull(result.getOutgoingBounceMessage());
			}
			
		}.perform();
	}
	
	public void testProcessValidOutgoingMessage_AssertSuccessfulResultWithNoBounces() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNotNull(result.getProcessedMessage());
				assertNull(result.getIncomingBounceMessage());
				assertNull(result.getOutgoingBounceMessage());
			}
			
		}.perform();
	}	
}
