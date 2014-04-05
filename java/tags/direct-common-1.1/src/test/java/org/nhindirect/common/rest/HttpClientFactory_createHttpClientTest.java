package org.nhindirect.common.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.junit.Test;

public class HttpClientFactory_createHttpClientTest 
{
	@Test
	public void testCreateHttpClient_createWithDefaultSettings()
	{
		HttpClient client = HttpClientFactory.createHttpClient();		
		assertNotNull(client);
		assertEquals(HttpClientFactory.DEFAULT_CON_TIMEOUT, HttpConnectionParams.getConnectionTimeout(client.getParams()));
		assertEquals(HttpClientFactory.DEFAULT_SO_TIMEOUT, HttpConnectionParams.getSoTimeout(client.getParams()));
	}
	
	@Test
	public void testCreateHttpClient_createWithCustomSettings()
	{
		HttpClient client = HttpClientFactory.createHttpClient(15000, 10000);		
		assertNotNull(client);
		assertEquals(15000, HttpConnectionParams.getConnectionTimeout(client.getParams()));
		assertEquals(10000, HttpConnectionParams.getSoTimeout(client.getParams()));
	}
}
