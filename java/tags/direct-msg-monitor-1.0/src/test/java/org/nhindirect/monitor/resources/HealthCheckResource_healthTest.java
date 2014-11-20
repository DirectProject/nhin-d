package org.nhindirect.monitor.resources;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.util.Collection;
import java.util.Collections;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.BaseTestPlan;
import org.nhindirect.monitor.TxsServiceRunner;
import org.nhindirect.monitor.util.TestUtils;

import com.sun.jersey.api.client.WebResource;

public class HealthCheckResource_healthTest 
{
	static WebResource resource;
	
	protected MockEndpoint mockEndpoint;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				TxsServiceRunner.startTxsService();
								
				resource = 	TestUtils.getResource(TxsServiceRunner.getTxsServiceURL());		

			}
			catch (Throwable t)
			{
				throw new RuntimeException(t);
			}
		}
		
		@Override
		protected void tearDownMocks()
		{
		}

		
		protected Collection<Tx> getTxsToSubmit()
		{
			return Collections.emptyList();
		}
									
		
		@Override
		protected void performInner() throws Exception
		{
			
			String html = resource.path("/health").get(String.class);
			doAssertions(html);
		}
		
		
		protected void doAssertions(String html) throws Exception
		{
			
		}
	}
	
	@Test
	public void testHealth_assertNoException() throws Exception
	{
		new TestPlan()
		{
		
			protected void doAssertions(String html) throws Exception
			{
				assertNotNull(html);
				assertFalse(html.isEmpty());
				assertTrue(html.startsWith("<"));
				
			}
		}.perform();		
	}
}
