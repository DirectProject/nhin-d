package org.nhindirect.gateway.smtp.james.mailet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.TxService;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;

import com.google.inject.Module;

public class NotificationSupressor_suppressMessageTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		NotificationSuppressor theMailet;
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, getConsumeMDNSetting());
			params.put("MessageMonitoringServiceURL", getMessageMonitoringServiceURL());
			
			return new MockMailetConfig(params, "NotificationSupressor");	
		}
		
		@Override
		protected void setupMocks() 
		{
			theMailet = new NotificationSuppressor()
			{
				@Override
				protected Collection<Module> getInitModules()
				{
					return getTestInitModules();
				}
			};

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
			MimeMessage msg = getMessageToSuppress();
			
			MockMail mail = new MockMail(msg);
			theMailet.service(mail);
			
			doAssertions(mail.getState().equals(Mail.GHOST));
		}
		
		protected String getConsumeMDNSetting()
		{
			return "true";
		}
		
		protected String getMessageMonitoringServiceURL()
		{
			return "http://localhost/msg-monitor";
		}
		
		protected Collection<Module> getTestInitModules()
		{
			return null;
		}
		
		protected abstract MimeMessage getMessageToSuppress() throws Exception;
		
		protected boolean isOutgoing()
		{
			return true;
		}
		
		protected void doAssertions(boolean consumeMessage) throws Exception
		{
			
		}			
	}
	
	public void testConsumeMessage_nonNotificationMessage_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("PlainOutgoingMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testConsumeMessage_noTxService_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				this.theMailet.txService = null;
			}
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("PlainOutgoingMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testConsumeMessage_consumeMDNFlagSet_MDNProccessedMessage_assertTrue() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertTrue(consumeMessage);
			}						
		}.perform();
		
	}
	
	public void testConsumeMessage_consumeMDNFlagNotSet_MDNProccessedMessage_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConsumeMDNSetting()
			{
				return "false";
			}
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testConsumeMessage_consumeMDNFlagSet_dispositionEmpty_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("PlainOutgoingMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	
	public void testConsumeMessage_consumeMDNFlagSet_dispositionProcessed_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNDispatchedMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testConsumeMessage_txServiceReturnsTrue_assertTrue() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				final TxService service = mock(TxService.class);
				try
				{
					when(service.suppressNotification((Tx)any())).thenReturn(true);
				}
				catch (ServiceException e){
				
				}
				theMailet.txService = service;
			}
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNDispatchedMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertTrue(consumeMessage);
			}						
		}.perform();
	}
	
	public void testConsumeMessage_exceptionInService_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				final TxService service = mock(TxService.class);
				try
				{
					when(service.suppressNotification((Tx)any())).thenThrow(new ServiceException());
				}
				catch (ServiceException e){
				
				}
				theMailet.txService = service;
			}
			
			@Override
			protected MimeMessage getMessageToSuppress() throws Exception
			{
				return new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNDispatchedMessage.txt")));
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
}
