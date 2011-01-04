package org.nhindirect.gateway.smtp;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.mail.Message.RecipientType;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.module.SmtpAgentConfigModule;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.notifications.NotificationHelper;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;
import org.nhindirect.stagent.provider.MockNHINDAgentProvider;
import org.nhindirect.stagent.utils.InjectionUtils;
import org.nhindirect.stagent.DefaultMessageEnvelope;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import junit.framework.TestCase;

public class DefaultSmtpAgent_ProcessMessage_Test extends TestCase 
{
	
	abstract class TestPlan extends BaseTestPlan 
	{
		protected SmtpAgent agent;
		protected Collection<Module> modules = new ArrayList<Module>();
		protected Provider<NHINDAgent> agentProvider;
		
		@Override
		public void setupMocks()
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			
			if (agentProvider == null)
				agentProvider = new MockNHINDAgentProvider(Arrays.asList(new String[] {"cerner.com", "securehealthemail.com"}));
			
			try
			{
				modules.add(SmtpAgentConfigModule.create(new URL("file://" + configfile), null, agentProvider));	
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			
			Injector injector = Guice.createInjector(modules);
			SmtpAgentConfig config = injector.getInstance(SmtpAgentConfig.class);
			agent = config.getAgentInjector().getInstance(SmtpAgent.class);
			
			injector.injectMembers(agent.getAgent());
			injector.injectMembers(agent);
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
	
	public void testProcessValidIncomingMessage_AutoResponseTrue_NOMDNRequest_AssertSuccessfulResultWithAnMDNMessage() throws Exception 
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
				
				assertNotNull(result);
				assertNotNull(result.getProcessedMessage());
				assertNotNull(result.getProcessedMessage().getMessage());
				assertNotNull(result.getNotificationMessages());
				assertTrue(result.getNotificationMessages().size() > 0);
				
				// get the first message
				NotificationMessage notiMsg = result.getNotificationMessages().iterator().next();
				
				assertEquals(1, notiMsg.getRecipients(RecipientType.TO).length);
				
				Message processedMessage = result.getProcessedMessage().getMessage();
				String processedSender = processedMessage.getFrom()[0].toString();
				String notiRecip = notiMsg.getRecipients(RecipientType.TO)[0].toString();
				// make sure the to and from are the same
				assertEquals(processedSender, notiRecip);				
			}
			
		}.perform();
	}
	
	public void testProcessValidIncomingMessageWithMDNRequest_AssertSuccessfulResultWithNoBounces() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainIncomingMessage.txt");
			}	
			
			@Override
			protected void performInner() throws Exception 
			{
				DefaultMessageEnvelope env = new DefaultMessageEnvelope(getMessageToProcess());
				
				// add the notification request
				NotificationHelper.requestNotification(env.getMessage());
				
				MessageProcessResult result = agent.processMessage(env.getMessage(), env.getRecipients(), env.getSender());			
				doAssertions(result);
			}
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNotNull(result.getProcessedMessage());
				assertNotNull(result.getProcessedMessage().getMessage());
				assertNotNull(result.getNotificationMessages());
				assertTrue(result.getNotificationMessages().size() > 0);
				
				// get the first message
				NotificationMessage notiMsg = result.getNotificationMessages().iterator().next();
				
				assertEquals(1, notiMsg.getRecipients(RecipientType.TO).length);
				
				Message processedMessage = result.getProcessedMessage().getMessage();
				String processedSender = processedMessage.getFrom()[0].toString();
				String notiRecip = notiMsg.getRecipients(RecipientType.TO)[0].toString();
				// make sure the to and from are the same
				assertEquals(processedSender, notiRecip);
				
			}
			
		}.perform();
	}
	
	public void testProcessValidIncomingMessageWithMDNRequest_AutoResponseFalse_AssertSuccessfulResultWithNoMDN() throws Exception 
	{
		new TestPlan() 
		{			
			@Override
			protected String getConfigFileName()
			{
				return "ValidConfigNoMDNAutoResponse.xml";
			}
			
			@Override
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainIncomingMessage.txt");
			}	
			
			@Override
			protected void performInner() throws Exception 
			{
				DefaultMessageEnvelope env = new DefaultMessageEnvelope(getMessageToProcess());
				
				// add the notification request
				NotificationHelper.requestNotification(env.getMessage());
				
				MessageProcessResult result = agent.processMessage(env.getMessage(), env.getRecipients(), env.getSender());			
				doAssertions(result);
			}
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNotNull(result.getProcessedMessage());
				assertNotNull(result.getProcessedMessage().getMessage());
				assertNotNull(result.getNotificationMessages());
				
				// make sure there is no MDN message
				assertEquals(0, result.getNotificationMessages().size());										
				
			}
			
		}.perform();
	}	
	
	public void testProcessValidOutgoingMessageWithMDNRequest_AssertSuccessfulResult() throws Exception 
	{
		new TestPlan() 
		{			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	
			
			@Override
			protected void performInner() throws Exception 
			{
				DefaultMessageEnvelope env = new DefaultMessageEnvelope(getMessageToProcess());
				
				// add the notification request
				NotificationHelper.requestNotification(env.getMessage());
				
				MessageProcessResult result = agent.processMessage(env.getMessage(), env.getRecipients(), env.getSender());			
				doAssertions(result);
			}			
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNotNull(result.getProcessedMessage());
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
			}
			
		}.perform();
	}	
	
	public void testUniitializedNHINDAgent_AssertUninitializedException() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			public void setupMocks()
			{
				super.setupMocks();
				// a little bad magic to set the private agent to null
				try
				{
					Field field = agent.getClass().getDeclaredField("agent");
					field.setAccessible(true);
					field.set(agent, null);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainIncomingMessage.txt");
			}	
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(SmtpAgentError.Uninitialized, ex.getError());
			}

			
		}.perform();
	}	
	
	public void testProcessValidOutgoingMessage_AssertSuccessfulResultWithOutgoingBounces() throws Exception 
	{
		new TestPlan() 
		{			
			
			
			@Override
			public void setupMocks()
			{
				modules.add(new AbstractModule()
				{
					@Override
					protected void configure()
					{
						bind(InjectionUtils.collectionOf(String.class)).
						annotatedWith(Names.named("MockAgentAnchorDomains")).
							toInstance(Arrays.asList(new String[] {"starugh-stateline.com"}));						
					}
				});
				super.setupMocks();
			}			
			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessageWithRejectedRecips.txt");
			}	
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNotNull(result.getProcessedMessage());
			}
			
		}.perform();
	}		
	
	
}
