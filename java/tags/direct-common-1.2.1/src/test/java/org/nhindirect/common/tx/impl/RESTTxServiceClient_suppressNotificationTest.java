package org.nhindirect.common.tx.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.nhindirect.common.BaseTestPlan;
import org.nhindirect.common.ServiceRunner;
import org.nhindirect.common.rest.HttpClientFactory;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.mock.MockTxsResource;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.util.TestUtils;

public class RESTTxServiceClient_suppressNotificationTest 
{
	
	protected RESTTxServiceClient client;
	protected MockTxsResource resource;
	
	abstract class TestPlan<T> extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				ServiceRunner.startServices();
				client = new RESTTxServiceClient(ServiceRunner.getTxsServiceURL(), HttpClientFactory.createHttpClient(),
						new DefaultTxDetailParser());			
				
				resource = (MockTxsResource)ServiceRunner.getSpringApplicationContext().getBean("mockTxsResource");
				
				
				resource.clearTxState();
			}
			catch (Throwable t)
			{
				throw new RuntimeException(t);
			}
		}
		
		@Override
		protected void tearDownMocks()
		{
			if (resource != null)
				resource.clearTxState();
		}

		
		protected T getNotficationSubmit() throws Exception
		{
			return null;
		}
									
		
		@Override
		protected void performInner() throws Exception
		{
			boolean b = false;
			
			T tx = getNotficationSubmit();
				b = suppressNotification(tx);
				
			doAssertions(b);
		}
		
		
		protected void doAssertions(boolean b) throws Exception
		{
			
		}
		
		protected abstract boolean suppressNotification(T tx) throws Exception;
	}	
	
	@Test
	public void testNonNotificationMessage_assertFalse() throws Exception
	{
		new TestPlan<MimeMessage>()
		{
			
			@Override
			protected boolean suppressNotification(MimeMessage tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected MimeMessage getNotficationSubmit() throws Exception
			{
				return TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
			}
			
			
			protected void doAssertions(boolean b) throws Exception
			{				
				assertFalse(b);
			}
		}.perform();		
	}
	
	@Test
	public void testMDNNotificationMessage_assertTrue() throws Exception
	{
		new TestPlan<MimeMessage>()
		{
			
			@Override
			protected boolean suppressNotification(MimeMessage tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected MimeMessage getNotficationSubmit() throws Exception
			{
				return TestUtils.readMimeMessageFromFile("MDNMessage.txt");
			}
			
			
			protected void doAssertions(boolean b) throws Exception
			{				
				assertTrue(b);
			}
		}.perform();		
	}
	
	@Test
	public void testDSNNotificationMessage_assertTrue() throws Exception
	{
		new TestPlan<MimeMessage>()
		{
			
			@Override
			protected boolean suppressNotification(MimeMessage tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected MimeMessage getNotficationSubmit() throws Exception
			{
				return TestUtils.readMimeMessageFromFile("DSNMessage.txt");
			}
			
			
			protected void doAssertions(boolean b) throws Exception
			{				
				assertTrue(b);
			}
		}.perform();		
	}
	
	@Test
	public void testNullNotificationMessage_assertException() throws Exception
	{
		new TestPlan<MimeMessage>()
		{
			
			@Override
			protected boolean suppressNotification(MimeMessage tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected MimeMessage getNotficationSubmit() throws Exception
			{
				return null;
			}
			
			
			protected void assertException(Exception exception) throws Exception 
			{
				// default case should not throw an exception
				assertTrue(exception != null) ;
				assertTrue(exception instanceof IllegalArgumentException);
			}
		}.perform();		
	}
	
	@Test
	public void testEmptyOriginalMessageId_assertFalse() throws Exception
	{
		new TestPlan<Tx>()
		{
			
			@Override
			protected boolean suppressNotification(Tx tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected Tx getNotficationSubmit() throws Exception
			{
				MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessage.txt");
				Map<String, TxDetail> details = new DefaultTxDetailParser().getMessageDetails(msg);
				details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, ""));
				Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				
				return tx;
			}
			
			
			protected void doAssertions(boolean b) throws Exception
			{				
				assertFalse(b);
			}
		}.perform();		
	}
	
	@Test
	public void testNullOriginalMessageId_assertFalse() throws Exception
	{
		new TestPlan<Tx>()
		{
			
			@Override
			protected boolean suppressNotification(Tx tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected Tx getNotficationSubmit() throws Exception
			{
				MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessage.txt");
				Map<String, TxDetail> details = new DefaultTxDetailParser().getMessageDetails(msg);
				details.remove(TxDetailType.PARENT_MSG_ID.getType());
				Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				
				return tx;
			}
			
			
			protected void doAssertions(boolean b) throws Exception
			{				
				assertFalse(b);
			}
		}.perform();		
	}
	
	@Test
	public void testNullTx_assertException() throws Exception
	{
		new TestPlan<Tx>()
		{
			
			@Override
			protected boolean suppressNotification(Tx tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected Tx getNotficationSubmit() throws Exception
			{
				return null;
			}
			
			
			protected void assertException(Exception exception) throws Exception 
			{
				// default case should not throw an exception
				assertTrue(exception != null) ;
				assertTrue(exception instanceof IllegalArgumentException);
			}
		}.perform();		
	}
	
	
	@Test
	public void testNotNotificationString_assertFalse() throws Exception
	{
		new TestPlan<Tx>()
		{
			
			@Override
			protected boolean suppressNotification(Tx tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected Tx getNotficationSubmit() throws Exception
			{
				MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessage.txt");
				Map<String, TxDetail> details = new DefaultTxDetailParser().getMessageDetails(msg);
				details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, "NotNotification"));
				Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				
				return tx;
			}
			
			
			protected void doAssertions(boolean b) throws Exception
			{				
				assertFalse(b);
			}
		}.perform();		
	}
	
	@Test
	public void testNotificationMessageIdString_assertTrue() throws Exception
	{
		new TestPlan<Tx>()
		{
			
			@Override
			protected boolean suppressNotification(Tx tx) throws Exception
			{
				return client.suppressNotification(tx);
			}
			
			@Override
			protected Tx getNotficationSubmit() throws Exception
			{
				MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessage.txt");
				Map<String, TxDetail> details = new DefaultTxDetailParser().getMessageDetails(msg);
				Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				
				return tx;
			}
			
			
			protected void doAssertions(boolean b) throws Exception
			{				
				assertTrue(b);
			}
		}.perform();		
	}
	
}
