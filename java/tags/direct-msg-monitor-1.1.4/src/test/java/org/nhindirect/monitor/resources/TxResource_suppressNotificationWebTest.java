package org.nhindirect.monitor.resources;

import static org.junit.Assert.assertFalse;


import javax.ws.rs.core.MediaType;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.TxsServiceRunner;
import org.nhindirect.monitor.util.TestUtils;

import com.sun.jersey.api.client.WebResource;

public class TxResource_suppressNotificationWebTest 
{
	static WebResource resource;
	
	protected MockEndpoint mockEndpoint;
	

	@Test
	public void suppressNotificationWebTest() throws Exception
	{

		Tx tx = TestUtils.makeMessage(TxMessageType.DSN, "12345", "", "", "", "");
		
		TxsServiceRunner.startTxsService();
						
		CamelContext context = (CamelContext)TxsServiceRunner.getSpringApplicationContext().getBean("web-camel-context");
		
		mockEndpoint = context.getEndpoint("mock:result", MockEndpoint.class);
		
		resource = 	TestUtils.getResource(TxsServiceRunner.getTxsServiceURL());		

		Boolean b = resource.path("/txs/suppressNotification").entity(tx, MediaType.APPLICATION_JSON).post(Boolean.class);

		assertFalse(b);

	}	
}
