package org.nhindirect.common.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.apache.http.client.HttpClient;
import org.junit.Test;

public class UsecuredServiceRequestBase_constructTest 
{
	@Test
	public void testConstruct_validConstruction() throws Exception
	{
		HttpClient client = mock(HttpClient.class);		
		
		MockServiceRequest req = new MockServiceRequest(client, "http://service/svc", "Test");
		assertNotNull(req);
		assertEquals(client, req.httpClient);
		assertEquals("Test", req.msg);
		assertEquals("http://service/svc", req.serviceUrl);
	}
}
