package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.smtp.dsn.impl.FailedDeliveryDSNCreator;

import junit.framework.TestCase;

public class TimelyAndReliableLocalDelivery_initTest extends TestCase
{
	protected MailetConfig getMailetConfig() throws Exception
	{
		Map<String,String> params = new HashMap<String, String>();
		
		return new MockMailetConfig(params, "TimelyAndReliableLocalDelivery");	
	}
	
	public void testInit_classExists_initSuccessful() throws Exception
	{
		TimelyAndReliableLocalDelivery mailet = new TimelyAndReliableLocalDelivery();
		mailet.init(getMailetConfig());
		
		assertNotNull(mailet.localDeliveryMailet);
		assertNotNull(mailet.txParser);
		assertNotNull(mailet.dsnCreator);
		assertTrue(mailet.dsnCreator instanceof FailedDeliveryDSNCreator);
	}
	
	public void testInit_exceptionInLocalDeliverMailetCreation_initSuccessful() throws Exception
	{
		TimelyAndReliableLocalDelivery mailet = new TimelyAndReliableLocalDelivery()
		{
			@Override
			protected Object createLocalDeliveryClass() throws Exception
			{
				throw new Exception();
			}
		};

		
		boolean expceptionOccured = false;
		try
		{
			mailet.init(getMailetConfig());
				
		}
		catch (Exception e)
		{
			expceptionOccured = true;
		}
		
		assertNull(mailet.localDeliveryMailet);
		assertTrue(expceptionOccured);

	}
}
