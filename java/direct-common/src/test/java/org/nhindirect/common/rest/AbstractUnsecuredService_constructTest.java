package org.nhindirect.common.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.apache.http.client.HttpClient;

import org.junit.Test;

public class AbstractUnsecuredService_constructTest 
{
	@Test
	public void testConstruct_validObjectCreated() throws Exception
	{
		HttpClient client = mock(HttpClient.class);
		MockService impl = new MockService("http://localhost/mock",  client);
		
		assertNotNull(impl);
		assertEquals(client, impl.httpClient);
		assertEquals("http://localhost/mock/", impl.serviceURL);
	}
	
	
	@Test
	public void testConstruct_nullClient1_assertIllegalArgumentException() throws Exception
	{		
		
		boolean exceptionOccured = false;
		try
		{
			new MockService("http://localhost/mock",  null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	
	@Test
	public void testConstruct_nullServiceUrl_assertIllegalArgumentException() throws Exception
	{		
		HttpClient client = mock(HttpClient.class);
		boolean exceptionOccured = false;
		try
		{
			new MockService(null,  client);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testConstruct_emptyServiceUr1_assertIllegalArgumentException() throws Exception
	{		
		HttpClient client = mock(HttpClient.class);
		boolean exceptionOccured = false;
		try
		{
			new MockService("",  client);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}

}
