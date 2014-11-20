package org.nhindirect.stagent.cert.impl;

import junit.framework.TestCase;

/*
 * This class may not fully run as part of a general build as it is intended to be a performance checker and requires
 * heap memory to be increased (-Xmx1024m).  There are no assertions handled, so exceptions in the revocation manager
 * should not affect general builds.
 */
public class CRLRevocationManager_performanceTest extends TestCase
{
	
	public void testDummy()
	{
		
	}

	/*
	public void testGetCRL_performanceTest() throws Exception
	{
		X509Certificate cert = TestUtils.loadCertificate("uhin.cer");
		
		CRLRevocationManager mgr = CRLRevocationManager.getInstance();
		mgr.flush();
		
		// time how long it takes to check revocation... first pass will have to download the CRL
		
		long startTime = System.currentTimeMillis();
		mgr.isRevoked(cert);
		long endTime = System.currentTimeMillis();
		
		System.out.println("Revocation checking time for CRL download from URI: " + (endTime - startTime) + "ms");
		
		// time how long it takes to check revocation again, this should be done with the CRL in the cache
		startTime = System.currentTimeMillis();
		mgr.isRevoked(cert);
		endTime = System.currentTimeMillis();
		
		System.out.println("Revocation checking time for CRL in memory cache: " + (endTime - startTime) + "ms");
		
		// delete memory cache and load from file
		CRLRevocationManager.cache.clear();
		
		// let the system rest and catch up with the cache delete and file write
		Thread.currentThread().sleep(2000);
		
		startTime = System.currentTimeMillis();
		mgr.isRevoked(cert);
		endTime = System.currentTimeMillis();
		
		System.out.println("Revocation checking time for CRL from file cache: " + (endTime - startTime) + "ms");
		
		// time how long it takes to check revocation again, this should be done with the CRL in the cache
		startTime = System.currentTimeMillis();
		mgr.isRevoked(cert);
		endTime = System.currentTimeMillis();
		
		System.out.println("Revocation checking time for CRL in memory cache: " + (endTime - startTime) + "ms");
	}
*/
}
