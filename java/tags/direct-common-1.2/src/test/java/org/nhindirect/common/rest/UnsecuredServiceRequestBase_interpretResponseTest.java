package org.nhindirect.common.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.apache.http.HttpResponse;
import org.junit.Test;
import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

public class UnsecuredServiceRequestBase_interpretResponseTest 
{
	@Test
	public void testInterpretResponseTest_200Status() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		assertNull(req.interpretResponse(200, resp));
	}
	
	@Test
	public void testInterpretResponseTest_201Status() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		assertNull(req.interpretResponse(201, resp));
	}
	
	@Test
	public void testInterpretResponseTest_204Status() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		assertNull(req.interpretResponse(204, resp));
	}
	
	@Test
	public void testInterpretResponseTest_401Status_assertAuthorizationException() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		boolean exceptionOccured = false;
		try
		{
			req.interpretResponse(401, resp);
		}
		catch (AuthorizationException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testInterpretResponseTest_404Status_assertServiceMethodException() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		boolean exceptionOccured = false;
		try
		{
			req.interpretResponse(404, resp);
		}
		catch (ServiceMethodException e)
		{
			assertEquals(404, e.getResponseCode());
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testInterpretResponseTest_400Status_assertServiceMethodException() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		boolean exceptionOccured = false;
		try
		{
			req.interpretResponse(400, resp);
		}
		catch (ServiceMethodException e)
		{
			assertEquals(400, e.getResponseCode());
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testInterpretResponseTest_500Status_assertServiceMethodException() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		boolean exceptionOccured = false;
		try
		{
			req.interpretResponse(500, resp);
		}
		catch (ServiceMethodException e)
		{
			assertEquals(500, e.getResponseCode());
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
