package org.nhindirect.common.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.nhindirect.common.rest.exceptions.ServiceException;

public class AbstractUnsecuredService_callWithRetryTest 
{
	@Test
	@SuppressWarnings("unchecked")
	public void testCallWithRetry_successfulCall() throws Exception
	{		
		HttpClient client = mock(HttpClient.class);
		MockService svc = new MockService("http://localhost/mock", client);
		
		ServiceRequest<Object, Exception> req = mock(ServiceRequest.class);
		
		svc.mockCallNoReturn(req);
	}
	
	
	@Test
	@SuppressWarnings("unchecked")
	public void testCallWithRetry_ioException_assertServiceException() throws Exception
	{

		HttpClient client = mock(HttpClient.class);
		MockService svc = new MockService("http://localhost/mock", client);
		
		ServiceRequest<Object, Exception> req = mock(ServiceRequest.class);
		doThrow(new IOException("Pass through")).when(req).call();
		
		boolean exceptionOccured = false;
		try
		{
			svc.mockCallNoReturn(req);
		}
		catch (ServiceException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}

}
