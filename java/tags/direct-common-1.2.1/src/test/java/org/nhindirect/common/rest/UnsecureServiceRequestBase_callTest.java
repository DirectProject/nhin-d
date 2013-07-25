package org.nhindirect.common.rest;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.nhindirect.common.rest.exceptions.ServiceException;

public class UnsecureServiceRequestBase_callTest 
{

	@Test
	public void testCall_callWithNoReturnValue() throws Exception
	{
		

		HttpClient mockClient = mock(HttpClient.class);
		
		StatusLine statLine = mock(StatusLine.class);
		when(statLine.getStatusCode()).thenReturn(204);
		HttpResponse resp = mock(HttpResponse.class);
		when(resp.getStatusLine()).thenReturn(statLine);
		
		
		when(mockClient.execute((HttpUriRequest)any())).thenReturn(resp);
		
		MockServiceRequest req = new MockServiceRequest(mockClient, "http://service/svc", "Test");
		
		assertNull(req.call());
	}
	
	@Test
	public void testCall_nullRequest_assertServiceException() throws Exception
	{
		
		HttpClient mockClient = mock(HttpClient.class);
		
		StatusLine statLine = mock(StatusLine.class);
		when(statLine.getStatusCode()).thenReturn(204);
		HttpResponse resp = mock(HttpResponse.class);
		when(resp.getStatusLine()).thenReturn(statLine);
		
		
		when(mockClient.execute((HttpUriRequest)any())).thenReturn(resp);
		
		MockServiceRequest req = new MockServiceRequest(mockClient, "http://service/svc", "");
		
		boolean exceptionOccured = false;
		try
		{
			req.call();
		}
		catch (ServiceException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	

}
