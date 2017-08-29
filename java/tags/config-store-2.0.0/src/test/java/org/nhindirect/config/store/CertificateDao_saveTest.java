package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
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
public class CertificateDao_saveTest 
{
	private static final String derbyHomeLoc = "/target/data";	
	
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
	
	@Test
	public void saveWithCertAndPrivKeyData() throws Exception
	{
		cleanDatabase();
		
		final byte[] certData = FileUtils.readFileToByteArray(new File("./src/test/resources/certs/gm2552.der"));
		final byte[] keyData = FileUtils.readFileToByteArray(new File("./src/test/resources/certs/gm2552Key.der"));
		
		Certificate addCert = new Certificate();
		addCert.setData(CertUtils.certAndWrappedKeyToRawByteFormat(keyData, CertUtils.toX509Certificate(certData)));
		addCert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(addCert);

		
		final Collection<Certificate> certificates = certificateDao.list((String)null);
		assertEquals(1, certificates.size());
		
		final Certificate cert = certificates.iterator().next();
		
		assertTrue(cert.isPrivateKey());

		CertUtils.CertContainer container = CertUtils.toCertContainer(certData);
		
		assertEquals(container.getCert(), CertUtils.toCertContainer(cert.getData()).getCert());
	}
	
}
