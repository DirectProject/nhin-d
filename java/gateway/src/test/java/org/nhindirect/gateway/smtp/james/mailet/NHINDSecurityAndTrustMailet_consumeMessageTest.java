package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.MailetConfig;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_consumeMessageTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		NHINDSecurityAndTrustMailet theMailet;
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.CONFIG_URL_PARAM, "file://" + configfile);
			params.put(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, getConsumeMDNSetting());
			
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
			Tx tx = getTx();
			
			doAssertions(theMailet.consumeMessage(tx, isOutgoing()));
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected String getConsumeMDNSetting()
		{
			return "true";
		}
		
		protected Tx getTx() throws Exception
		{
			return null;
		}
		
		protected boolean isOutgoing()
		{
			return true;
		}
		
		protected void doAssertions(boolean consumeMessage) throws Exception
		{
			
		}			
	}
	
	public void testconsumeMessage_nullTx_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testconsumeMessage_nonMDNMessage_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected Tx getTx() throws Exception
			{
				final MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("PlainOutgoingMessage.txt")));
				final Map<String, TxDetail> details = theMailet.txParser.getMessageDetails(msg);
				final Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				return tx;
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testconsumeMessage_MDNMessage_falseMDNConsumeSetting_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getConsumeMDNSetting()
			{
				return "false";
			}
			
			@Override
			protected Tx getTx() throws Exception
			{
				final MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
				final Map<String, TxDetail> details = theMailet.txParser.getMessageDetails(msg);
				final Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				return tx;
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testconsumeMessage_MDNMessage_outgoingMessage_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected Tx getTx() throws Exception
			{
				final MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
				final Map<String, TxDetail> details = theMailet.txParser.getMessageDetails(msg);
				final Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				return tx;
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testconsumeMessage_incomingMDNMessage_nullDisposition_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			protected boolean isOutgoing()
			{
				return false;
			}
			
			@Override
			protected Tx getTx() throws Exception
			{
				final MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
				final Map<String, TxDetail> details = theMailet.txParser.getMessageDetails(msg);
				details.remove(TxDetailType.DISPOSITION.getType());
				final Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				return tx;
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testconsumeMessage_incomingMDNMessage_dispatchedDisposition_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			protected boolean isOutgoing()
			{
				return false;
			}
			
			@Override
			protected Tx getTx() throws Exception
			{
				final MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
				final Map<String, TxDetail> details = theMailet.txParser.getMessageDetails(msg);
				details.put(TxDetailType.DISPOSITION.getType(), new TxDetail(TxDetailType.DISPOSITION, "dispatched"));
				final Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				return tx;
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertFalse(consumeMessage);
			}						
		}.perform();
	}
	
	public void testconsumeMessage_incomingMDNMessage_assertTrue() throws Exception 
	{
		new TestPlan() 
		{
			protected boolean isOutgoing()
			{
				return false;
			}
			
			@Override
			protected Tx getTx() throws Exception
			{
				final MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
				final Map<String, TxDetail> details = theMailet.txParser.getMessageDetails(msg);
				final Tx tx = new Tx(TxUtil.getMessageType(msg), details);
				return tx;
			}
			
			@Override
			protected void doAssertions(boolean consumeMessage) throws Exception
			{
				assertTrue(consumeMessage);
			}						
		}.perform();
	}
}
