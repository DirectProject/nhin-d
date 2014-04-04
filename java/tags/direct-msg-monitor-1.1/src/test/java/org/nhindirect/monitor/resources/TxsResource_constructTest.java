package org.nhindirect.monitor.resources;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.nhindirect.monitor.processor.DuplicateNotificationStateManager;

public class TxsResource_constructTest 
{
	@Test
	public void testConstruct_defaultConstructor()
	{
		TxsResource resource = new TxsResource();
		
		assertNull(resource.template);
		assertNull(resource.dupStateManager);
	}
	
	@Test
	public void testConstruct_nullProducer()
	{
		TxsResource resource = new TxsResource(null, null);
		
		assertNull(resource.template);
		assertNull(resource.dupStateManager);
	}
	
	@Test
	public void testConstruct_nonNullProducer()
	{
		ProducerTemplate template = mock(ProducerTemplate.class);
		DuplicateNotificationStateManager dupMgr = mock(DuplicateNotificationStateManager.class);
		
		TxsResource resource = new TxsResource(template, dupMgr);
		
		assertEquals(template, resource.template);
		assertEquals(dupMgr, resource.dupStateManager);
	}

}
