package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.MailetConfig;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;

import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.AddressSource;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;


public class NHINDSecurityAndTrustMailet_getMessageToTrackTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		protected NHINDSecurityAndTrustMailet theMailet;
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		protected void setupMocks() 
		{
			theMailet = new NHINDSecurityAndTrustMailet()
			{
				@Override
				protected boolean isOutgoing(MimeMessage msg, NHINDAddress sender)
				{
					return isMessageOutgoing();
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

			MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource(getMessageToSend())));
			NHINDAddress sender = new NHINDAddress((InternetAddress)msg.getFrom()[0]);
			
			
			final NHINDAddressCollection recipients = new NHINDAddressCollection();		


			final Address[] recipsAddr = msg.getAllRecipients();
			for (Address addr : recipsAddr)
			{
				
				recipients.add(new NHINDAddress(addr.toString(), (AddressSource)null));
			}
			
			doAssertions(theMailet.getTxToTrack(msg, sender, recipients));
		}
		
		protected String getMessageToSend()
		{
			return "PlainOutgoingMessage.txt";
		}
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected boolean isMessageOutgoing()
		{
			return true;
		}
		
		protected void doAssertions(Tx tx) throws Exception
		{
		}			
	}
	
	public void testMessageToTrackTest_nullParser_assertNullTx() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				theMailet.txParser = null;
			}
			
			@Override
			protected void doAssertions(Tx tx) throws Exception
			{
				assertNull(tx);
			}				
		}.perform();
	}
	
	public void testMessageToTrackTest_nonIMFMessage_assertMDNTx() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageToSend()
			{
				return "MDNMessage.txt";
			}
			
			@Override
			protected void doAssertions(Tx tx) throws Exception
			{
				assertNotNull(tx);
				assertEquals(TxMessageType.MDN, tx.getMsgType());
				
				MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource(getMessageToSend())));
				assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
						tx.getDetail(TxDetailType.FROM).getDetailValue());
			}				
		}.perform();
	}
	
	
	public void testMessageToTrackTest_regularOutgoingMessage_assertTx() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(Tx tx) throws Exception
			{
				assertNotNull(tx);
				assertEquals(TxMessageType.IMF, tx.getMsgType());
				
				MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource(getMessageToSend())));
				assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
						tx.getDetail(TxDetailType.FROM).getDetailValue());
			}				
		}.perform();
	}
}
