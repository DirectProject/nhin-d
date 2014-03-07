package org.nhindirect.config.store;

import org.junit.Test;
import org.nhindirect.config.model.utils.CertUtils;

public class Certfificate_setDataTest 
{
	@Test
	public void testSetData_setWithProtectedData() throws Exception 
	{
		final byte[] certData = CertificateDaoTest.loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");
		
		final byte[] protectedCertData =  CertUtils.changePkcs12Protection(certData, "".toCharArray(), "".toCharArray(), 
				"12345".toCharArray(), "67890".toCharArray());
		
		Certificate cert = new Certificate();
		cert.setData(protectedCertData);
		// just make sure an exception didn't happen here
	}
}
