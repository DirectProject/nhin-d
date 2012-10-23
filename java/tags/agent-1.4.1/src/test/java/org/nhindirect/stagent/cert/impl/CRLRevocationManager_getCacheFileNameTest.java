package org.nhindirect.stagent.cert.impl;

import junit.framework.TestCase;

public class CRLRevocationManager_getCacheFileNameTest extends TestCase
{
	@Override
	public void setUp()
	{
		CRLRevocationManager.initCRLCacheLocation();
		CRLRevocationManager.getInstance().flush();
		CRLRevocationManager.crlCacheLocation = null;
	}
	
	@Override
	public void tearDown()
	{
		CRLRevocationManager.getInstance().flush();
		CRLRevocationManager.initCRLCacheLocation();
	}
	
	public void testGetCacheName_uniqueNames() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();
		String uriName1 = CRLRevocationManager.getCacheFileName("http://localhost:8080/master.crl");
		assertNotNull(uriName1);
		assertTrue(uriName1.contains("CrlCache"));
		
		String uriName2 = CRLRevocationManager.getCacheFileName("http://localhost/master.crl");
		assertNotNull(uriName2);
		assertTrue(uriName2.contains("CrlCache"));
		
		assertFalse(uriName1.equals(uriName2));
	}
	
	public void testGetCacheName_sameNames() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();
		String uriName1 = CRLRevocationManager.getCacheFileName("http://localhost:8080/master.crl");
		assertNotNull(uriName1);
		assertTrue(uriName1.contains("CrlCache"));
		
		String uriName2 = CRLRevocationManager.getCacheFileName("http://localhost:8080/master.crl");
		assertNotNull(uriName2);
		assertTrue(uriName2.contains("CrlCache"));
		
		assertEquals(uriName1, uriName2);
	}
	
	public void testGetCacheName_nullCacheLocation_assertEmptyName() throws Exception
	{
		String uriName1 = CRLRevocationManager.getCacheFileName("http://localhost:8080/master.crl");
		assertNotNull(uriName1);
		assertEquals("", uriName1);

	}
	
}
