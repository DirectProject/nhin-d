package org.nhindirect.gateway.smtp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.impl.NoOpAuditor;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.DefaultMessageEnvelope;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.mail.notifications.MDNStandard;
import org.nhindirect.stagent.module.AuditorModule;
import org.nhindirect.stagent.provider.InstanceAuditorProvider;
import org.nhindirect.stagent.provider.MockNHINDAgentProvider;

import com.google.inject.Module;
import com.google.inject.Provider;

import junit.framework.TestCase;

public class DefaultSmtpAgent_AuditMessage_Test extends TestCase 
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected MockAuditor auditor = new MockAuditor();
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
				Provider<Auditor> auditProvider = new InstanceAuditorProvider(auditor);
				Module auditModule = AuditorModule.create(auditProvider);
				
				agent = SmtpAgentFactory.createAgent(new URL("file://" + configfile), null, agentProvider, Arrays.asList(auditModule));
				
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
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
		
		protected class MockAuditor extends NoOpAuditor
		{
			private final Map<AuditEvent, Collection<? extends AuditContext>> events = new HashMap<AuditEvent, Collection<? extends AuditContext>>();
			
			@Override
			public void writeEvent(UUID eventId, Calendar eventTimeStamp, String principal, AuditEvent event, Collection<? extends AuditContext> contexts)
			{
				events.put(event, contexts);
			}
			
			public Map<AuditEvent, Collection<? extends AuditContext>> getEvents()
			{
				return events;
			}
		}
	}
	
	public void testAuditIncomingMessage_AssertEventsAudited() throws Exception 
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
				DefaultSmtpAgent smtpAgent = (DefaultSmtpAgent)agent;
				assertNotNull(smtpAgent.getAuditor());
				assertTrue(smtpAgent.getAuditor() instanceof MockAuditor);
				
				assertTrue(auditor.getEvents().size() > 0);
				
				boolean foundIncomingType = false;
				boolean foundMDNType = false;
				for (Entry<AuditEvent, Collection<? extends AuditContext>> entry : auditor.getEvents().entrySet())
				{
					AuditEvent event = entry.getKey();
					assertEquals(event.getType(), "SMTP Direct Message Processing");
					
					if (event.getName().equals(AuditEvents.INCOMING_MESSAGE_NAME))
						foundIncomingType = true;
					else if (event.getName().equals(AuditEvents.PRODUCE_MDN_NAME))
					{
						boolean foundFinalRecip = false;
						boolean foundOrigMsgId = false;		
						boolean foundDisp = false;
						
						for (AuditContext ctx : entry.getValue())
						{
							if (ctx.getContextName().equals(MDNStandard.Headers.FinalRecipient))
							{
								foundFinalRecip = true;
							}
							else if (ctx.getContextName().equals(MDNStandard.Headers.OriginalMessageID))
							{
								foundOrigMsgId = true;
							}
							else if (ctx.getContextName().equals(MDNStandard.Headers.Disposition))
							{
								assertEquals("automatic-action/MDN-sent-automatically;processed", ctx.getContextValue());
								foundDisp = true;
							}							
							
						}
						
						assertTrue(foundFinalRecip);
						assertTrue(foundOrigMsgId);
						assertTrue(foundDisp);
						
						foundMDNType = true;
						// assert attributes of the event
						
					}
				}
				
				assertTrue(foundIncomingType);
				assertTrue(foundMDNType);
			}
			
		}.perform();
	}	
	
	public void testAuditOutgoingMessage_AssertEventsAudited() throws Exception 
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
				DefaultSmtpAgent smtpAgent = (DefaultSmtpAgent)agent;
				assertNotNull(smtpAgent.getAuditor());
				assertTrue(smtpAgent.getAuditor() instanceof MockAuditor);
				
				assertTrue(auditor.getEvents().size() > 0);
				
				boolean foundOutgoingType = false;
				for (Entry<AuditEvent, Collection<? extends AuditContext>> entry : auditor.getEvents().entrySet())
				{
					AuditEvent event = entry.getKey();
					assertEquals(event.getType(), "SMTP Direct Message Processing");
					
					if (event.getName().equals("Outgoing Direct Message"))
						foundOutgoingType = true;
				}
				
				assertTrue(foundOutgoingType);
			}
			
		}.perform();
	}		
}
