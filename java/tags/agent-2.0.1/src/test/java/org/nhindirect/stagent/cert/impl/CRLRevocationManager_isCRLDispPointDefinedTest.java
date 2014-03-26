package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;

import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class CRLRevocationManager_isCRLDispPointDefinedTest extends TestCase
{
	public void testIsCRLDispPointDefined_assertCRLDispDefined() throws Exception
	{
		X509Certificate cert = TestUtils.loadCertificate("uhin.cer");
		assertTrue(CRLRevocationManager.isCRLDispPointDefined(cert));
	}
	
	public void testIsCRLDispPointDefined_assertCRLDispNotDefined() throws Exception
	{
		X509Certificate cert = TestUtils.loadCertificate("gm2552.der");
		assertFalse(CRLRevocationManager.isCRLDispPointDefined(cert));
	}
}
