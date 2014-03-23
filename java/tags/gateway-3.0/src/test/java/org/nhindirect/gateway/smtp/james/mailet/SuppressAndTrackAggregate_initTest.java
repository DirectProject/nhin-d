package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.nhindirect.common.tx.impl.DefaultTxDetailParser;
import org.nhindirect.gateway.testutils.BaseTestPlan;

import junit.framework.TestCase;

public class SuppressAndTrackAggregate_initTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.MONITORING_SERVICE_URL_PARAM, getMonitoringServiceURL());
			
			
			return new MockMailetConfig(params, "TrackIncomingNotification");	
		}
		
		@Override
		protected void performInner() throws Exception
		{
			SuppressAndTrackAggregate theMailet = new SuppressAndTrackAggregate();

			MailetConfig config = getMailetConfig();
			
			theMailet.init(config);
			doAssertions(theMailet);
		}
		
		
		protected String getMonitoringServiceURL()
		{
			return "";
		}

		protected void doAssertions(SuppressAndTrackAggregate notif) throws Exception
		{

		}		
		
	}
	
	public void testInitialization_emptyMonitorURL() throws Exception 
	{
		new TestPlan() 
		{
			protected void doAssertions(SuppressAndTrackAggregate notif) throws Exception
			{
				assertNotNull(notif.suppessor.txParser);
				assertNotNull(notif.suppessor.txService);
				assertTrue(notif.suppessor.txParser instanceof DefaultTxDetailParser);
				
				assertNotNull(notif.tracker.txParser);
				assertNotNull(notif.tracker.txService);
				assertTrue(notif.tracker.txParser instanceof DefaultTxDetailParser);
			}	
		}.perform();
	}	
	
	public void testInitialization_nullMonitorURL() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMonitoringServiceURL()
			{
				return null;
			}
			
			protected void doAssertions(SuppressAndTrackAggregate notif) throws Exception
			{
				assertNotNull(notif.suppessor.txParser);
				assertNotNull(notif.suppessor.txService);
				assertTrue(notif.suppessor.txParser instanceof DefaultTxDetailParser);
				
				assertNotNull(notif.tracker.txParser);
				assertNotNull(notif.tracker.txService);
				assertTrue(notif.tracker.txParser instanceof DefaultTxDetailParser);
			}	
		}.perform();
	}	
	
	public void testInitialization_valueMonitorURL() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMonitoringServiceURL()
			{
				return "http://localhost/msg-monitor";
			}
			
			protected void doAssertions(SuppressAndTrackAggregate notif) throws Exception
			{
				assertNotNull(notif.suppessor.txParser);
				assertNotNull(notif.suppessor.txService);
				assertTrue(notif.suppessor.txParser instanceof DefaultTxDetailParser);
				
				assertNotNull(notif.tracker.txParser);
				assertNotNull(notif.tracker.txService);
				assertTrue(notif.tracker.txParser instanceof DefaultTxDetailParser);
			}	
		}.perform();
	}
	
}
