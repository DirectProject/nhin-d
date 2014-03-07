package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.Security;
import java.util.Collection;


import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.store.dao.CertificateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/configStore-keyProtMgr-test.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class CertificateDao_stripP12ProtectionWithManagerTest 
{
	private static final String derbyHomeLoc = "/target/data";	
	
	private static final String certBasePath = "src/test/resources/certs/"; 
	
	@Autowired
	private CertificateDao certificateDao;
	
	
	static
	{
		try
		{
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			File baseLocation = new File("dummy.txt");
			String fullDerbyHome = baseLocation.getAbsolutePath().substring(0, baseLocation.getAbsolutePath().lastIndexOf(File.separator)) + derbyHomeLoc;
			System.setProperty("derby.system.home", fullDerbyHome);

		}
		catch (Exception e)
		{
			
		}
	}	    	
	
	private static byte[] loadCertificateData(String certFileName) throws Exception
	{
		final File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	
	protected void cleanDatabase() throws Exception 
	{
		Collection<Certificate> certificates = certificateDao.list((String)null);		
		
		if (certificates != null && certificates.size() > 0)
		{
			for (Certificate cert : certificates)
				certificateDao.delete(cert.getOwner());
			
		}
		certificates = certificateDao.list((String)null);	
		
		assertEquals(0, certificates.size());
	}
	
	protected Certificate populateCert(String certFile, String keyFile) throws Exception
	{
		cleanDatabase();
		
		final byte[] certData = (keyFile != null && !keyFile.isEmpty()) ? 
				CertificateDaoTest.loadPkcs12FromCertAndKey(certFile, keyFile) :
					loadCertificateData(certFile);
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
		
		return cert;
	}
	
	@Test
	public void testStripP12ProtectionTest_p12ProtectionWithManager_assertP12Returned() throws Exception
	{
		populateCert("gm2552.der", "gm2552Key.der");
		
    	
		Collection<Certificate> certificates = certificateDao.list((String)null);
		assertEquals(1, certificates.size());
		
		Certificate cert = certificates.iterator().next();
		
		assertTrue(cert.isPrivateKey());
		final byte[] certData = CertificateDaoTest.loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");

		CertUtils.CertContainer container = CertUtils.toCertContainer(certData);
		
		assertEquals(container.getCert(), CertUtils.toCertContainer(cert.getData()).getCert());
		
	}
	
	@Test
	public void testStripP12ProtectionTest_X509CertAndManager_assertX509Returned() throws Exception
	{
		populateCert("gm2552.der", null);
				
		Collection<Certificate> certificates = certificateDao.list((String)null);
		assertEquals(1, certificates.size());
		
		Certificate cert = certificates.iterator().next();
		
		assertFalse(cert.isPrivateKey());
		final byte[] certData = loadCertificateData("gm2552.der");

		CertUtils.CertContainer container = CertUtils.toCertContainer(certData);
		
		assertEquals(container.getCert(), CertUtils.toCertContainer(cert.getData()).getCert());	
	}
}
