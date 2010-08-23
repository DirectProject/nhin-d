package org.nhindirect.gateway.smtp;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.module.SmtpAgentConfigModule;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.stagent.provider.MockNHINDAgentProvider;
import org.nhindirect.stagent.utils.InjectionUtils;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.AgentException;
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
	
	public void testOutgoingMessageNoTrustedRecips_AssertOutgoingBounce() throws Exception 
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
						bind(AgentException.class).
						annotatedWith(Names.named("MockAgentOutgoingException")).toInstance(new AgentException(AgentError.NoTrustedRecipients));						
					}
				});
				super.setupMocks();
			}
			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainOutgoingMessage.txt");
			}	
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNull(result.getProcessedMessage());
				assertNull(result.getIncomingBounceMessage());
				assertNotNull(result.getOutgoingBounceMessage());
			}
			
		}.perform();
	}		
	
	public void testIncomingMessageNoTrustedRecips_AssertOutgoingBounce() throws Exception 
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
						
						bind(AgentException.class).
						annotatedWith(Names.named("MockAgentIncomingException")).toInstance(new AgentException(AgentError.NoTrustedRecipients));						
					}
				});
				super.setupMocks();
			}
			
			protected String getMessageToProcess() throws Exception
			{
				return TestUtils.readMessageResource("PlainIncomingMessage.txt");
			}	
			
			@Override
			protected void doAssertions(MessageProcessResult result) throws Exception
			{
				assertNotNull(result);
				assertNull(result.getProcessedMessage());
				assertNotNull(result.getIncomingBounceMessage());
				assertNull(result.getOutgoingBounceMessage());
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
				assertNull(result.getIncomingBounceMessage());
				assertNotNull(result.getOutgoingBounceMessage());
			}
			
		}.perform();
	}		
	
	
}
