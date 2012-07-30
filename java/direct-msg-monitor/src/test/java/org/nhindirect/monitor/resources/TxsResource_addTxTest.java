package org.nhindirect.monitor.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.any;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;

public class TxsResource_addTxTest 
{
	@Test
	public void testAddTx_nullTemplate_assertExcecption()
	{
		Tx tx = mock(Tx.class);
		
		TxsResource resource = new TxsResource(null, null);
		
		boolean exceptionOccured = false;
		
		try
		{
			resource.addTx(tx);
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testAddTx_exceptionInSubmission_assertErrorCode()
	{
		ProducerTemplate template = mock(ProducerTemplate.class);
		doThrow(new RuntimeException("")).when(template).sendBody(any());
		
		Tx tx = mock(Tx.class);
		
		TxsResource resource = new TxsResource(template, null);

		Response res = resource.addTx(tx);
		
		assertEquals(500, res.getStatus());
	}
	
	@Test
	public void testAddTx_exceptionInSubmission_assertCreatedCode()
	{
		ProducerTemplate template = mock(ProducerTemplate.class);
		
		Tx tx = mock(Tx.class);
		
		TxsResource resource = new TxsResource(template, null);

		Response res = resource.addTx(tx);
		
		assertEquals(201, res.getStatus());
	}
}
