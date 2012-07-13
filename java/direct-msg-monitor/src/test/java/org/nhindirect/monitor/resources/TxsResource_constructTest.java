package org.nhindirect.monitor.resources;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;

public class TxsResource_constructTest 
{
	@Test
	public void testConstruct_defaultConstructor()
	{
		TxsResource resource = new TxsResource();
		
		assertNull(resource.template);
	}
	
	@Test
	public void testConstruct_nullProducer()
	{
		TxsResource resource = new TxsResource(null);
		
		assertNull(resource.template);
	}
	
	@Test
	public void testConstruct_nonNullProducer()
	{
		ProducerTemplate template = mock(ProducerTemplate.class);
		
		TxsResource resource = new TxsResource(template);
		
		assertEquals(template, resource.template);
	}

}
