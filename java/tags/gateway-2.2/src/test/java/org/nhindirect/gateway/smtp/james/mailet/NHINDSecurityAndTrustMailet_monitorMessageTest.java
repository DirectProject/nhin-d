package org.nhindirect.gateway.smtp.james.mailet;

import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.MailetConfig;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.common.tx.module.DefaultTxDetailParserModule;
import org.nhindirect.common.tx.module.ProviderTxServiceModule;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.parser.EntitySerializer;

import com.google.inject.Module;

public class NHINDSecurityAndTrustMailet_monitorMessageTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		NHINDSecurityAndTrustMailet theMailet;
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.CONFIG_URL_PARAM, "file://" + configfile);
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		@Override
		protected void setupMocks() 
		{
			NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet()
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
				MailetConfig config = getMailetConfig();
				
				theMailet = spy(mailet);
				
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
			
			theMailet.service(theMessage);
			
			doAssertions((MockTxService)theMailet.txService);
		}
		
		protected String getMessageToSend()
		{
			return "PlainOutgoingMessage.txt";
		}
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected void doAssertions(MockTxService service) throws Exception
		{
		}			
	}
	
	public void testMonitorMessage_trackTrustedOutgoingMessage_assertMessageTracked() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void doAssertions(MockTxService service) throws Exception
			{
				
				assertEquals(1, service.txs.size());
				Tx tx = service.txs.iterator().next();
				assertEquals(TxMessageType.IMF, tx.getMsgType());
				
				MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource(getMessageToSend())));
				assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
						tx.getDetail(TxDetailType.FROM).getDetailValue());
			}			
		}.perform();
	}
	
	public void testMonitorMessage_trackMDNMessage_assertMessageNotTracked() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getConfigFileName()
			{
				return "ValidConfigStateLine.txt";
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "MDNMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockTxService service) throws Exception
			{
				assertEquals(0, service.txs.size());
			}			
		}.perform();
	}
	
	public void testMonitorMessage_trackDSNMessage_assertMessageNotTracked() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getConfigFileName()
			{
				return "ValidConfigStateLine.txt";
			}
			
			@Override
			protected String getMessageToSend()
			{
				return "DSNMessage.txt";
			}
			
			@Override
			protected void doAssertions(MockTxService service) throws Exception
			{
				assertEquals(0, service.txs.size());
			}			
		}.perform();
	}
}
