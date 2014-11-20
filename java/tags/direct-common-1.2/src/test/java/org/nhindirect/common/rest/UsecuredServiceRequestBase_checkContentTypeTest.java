package org.nhindirect.common.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.junit.Test;
import org.nhindirect.common.rest.exceptions.ServiceException;

public class UsecuredServiceRequestBase_checkContentTypeTest 
{
	@Test
	public void testCheckContentType_validContentType() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		Header hdr = mock(Header.class);
		when(hdr.getName()).thenReturn("Content-Type");
		when(hdr.getValue()).thenReturn("text/plain");
		
		HttpEntity entity = mock(HttpEntity.class);
		when(entity.getContentType()).thenReturn(hdr);
		
		req.checkContentType("text/plain", entity);

	}
	
	@Test
	public void testCheckContentType_incompatibleType_assertServiceException() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		Header hdr = mock(Header.class);
		when(hdr.getName()).thenReturn("Content-Type");
		when(hdr.getValue()).thenReturn("text/xml");
		
		HttpEntity entity = mock(HttpEntity.class);
		when(entity.getContentType()).thenReturn(hdr);
		
		boolean exceptionOccured = false;
		try
		{
			req.checkContentType("text/plain", entity);
		}
		catch (ServiceException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testCheckContentType_nullEnitity_assertServiceException() throws Exception
	{
		MockServiceRequest req = new MockServiceRequest(null, "http://service/svc", "Test");
		
		boolean exceptionOccured = false;
		try
		{
			req.checkContentType("text/plain", null);
		}
		catch (ServiceException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
