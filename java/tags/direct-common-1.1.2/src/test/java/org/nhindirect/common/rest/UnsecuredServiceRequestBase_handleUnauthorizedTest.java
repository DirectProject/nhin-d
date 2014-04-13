package org.nhindirect.common.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.apache.http.HttpResponse;
import org.junit.Test;
import org.nhindirect.common.rest.exceptions.AuthorizationException;

public class UnsecuredServiceRequestBase_handleUnauthorizedTest 
{
	
	@Test
	public void testHandleUnauthorized_noHeaders_assertAuthorizationException() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		HttpResponse resp = mock(HttpResponse.class);
		
		
		AuthorizationException ex = req.handleUnauthorized(resp);
		assertEquals("Action not authorized",ex.getMessage());
	}
	

}
