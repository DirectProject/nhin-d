package org.nhindirect.common.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnsecuredServiceRequestBase_escapeURITest 
{
	@Test
	public void testEscapeURI_replaceSpace() throws Exception
	{
		String uri = UnsecuredServiceRequestBase.uriEscape("Test Space");
		assertEquals("Test%20Space", uri);
	}
	
	@Test
	public void testEscapeURI_replacePlus() throws Exception
	{
		String uri = UnsecuredServiceRequestBase.uriEscape("Test+Space");
		assertEquals("Test%2BSpace", uri);
	}
}
