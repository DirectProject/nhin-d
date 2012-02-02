package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.nhindirect.stagent.utils.TestUtils;

public class CRLRevocationManager_performanceTest extends TestCase
{
	
	public void testDummy()
	{
		
	}
/*
	public void testGetCRL_multipleDispPoints() throws Exception
	{
		X509Certificate cert = TestUtils.loadCertificate("uhin.cer");
		
		CRLRevocationManager mgr = CRLRevocationManager.getInstance();
		mgr.flush();
		
		mgr.isRevoked(cert);
	}
*/
}
