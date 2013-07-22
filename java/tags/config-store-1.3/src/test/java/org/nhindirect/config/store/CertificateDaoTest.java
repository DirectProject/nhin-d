package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.config.store.dao.CertificateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/configStore-test.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class CertificateDaoTest 
{
	private static final String derbyHomeLoc = "/target/data";	
	
	private static final String certBasePath = "src/test/resources/certs/"; 
	
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
		File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	private static byte[] loadPkcs12FromCertAndKey(String certFileName, String keyFileName) throws Exception
	{
		byte[] retVal = null;
		try
		{
			KeyStore localKeyStore = KeyStore.getInstance("PKCS12", Certificate.getJCEProviderName());
			
			localKeyStore.load(null, null);
			
			byte[] certData = loadCertificateData(certFileName);
			byte[] keyData = loadCertificateData(keyFileName);
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream inStr = new ByteArrayInputStream(certData);
			java.security.cert.Certificate cert = cf.generateCertificate(inStr);
			inStr.close();
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( keyData );
			Key privKey = kf.generatePrivate (keysp);
			
			char[] array = "".toCharArray();
			
			localKeyStore.setKeyEntry("privCert", privKey, array,  new java.security.cert.Certificate[] {cert});
			
			ByteArrayOutputStream outStr = new ByteArrayOutputStream();
			localKeyStore.store(outStr, array);
			
			retVal = outStr.toByteArray();
			
			outStr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return retVal;
	}
	
	@Autowired
	private CertificateDao certificateDao;
	
	@Test
	public void testCleanDatabase() throws Exception 
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
	public void testAddPublicCert() throws Exception
	{
		testCleanDatabase();
	
		byte[] certData = loadCertificateData("gm2552.der");
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		Collection<Certificate> certificates = certificateDao.list((String)null);
		assertEquals(1, certificates.size());
	}
	
	@Test 
	public void testAddPKIXURL() throws Exception
	{
		testCleanDatabase();
		
		Certificate cert = new Certificate();
		cert.setData("http://localhost/test.der".getBytes());
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		Collection<Certificate> certificates = certificateDao.list((String)null);
		assertEquals(1, certificates.size());
		
		Certificate addedCert = certificates.iterator().next();
		
		assertEquals("", addedCert.getThumbprint());
		assertEquals("http://localhost/test.der", new String(addedCert.getData()));
		
	}
	
	@Test 
	public void testAddPrivateCert() throws Exception
	{
		testCleanDatabase();
	
		byte[] certData = loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		Collection<Certificate> certificates = certificateDao.list((String)null);
		assertEquals(1, certificates.size());
	}	
	
	@Test 
	public void testGetByOwner() throws Exception
	{
		testCleanDatabase();
	
		byte[] certData = loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		Collection<Certificate> certificates = certificateDao.list("gm2552@cerner.com");
		assertEquals(1, certificates.size());
		cert = certificates.iterator().next();
		
		assertEquals("gm2552@cerner.com", cert.getOwner());
		
		
		testCleanDatabase();
		
		certData = loadCertificateData("gm2552.der");
		
		cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		certificates = certificateDao.list("gm2552@cerner.com");
		assertEquals(1, certificates.size());
		cert = certificates.iterator().next();
		
		assertEquals("gm2552@cerner.com", cert.getOwner());
	}	
	
	@Test 
	public void testGetById() throws Exception
	{
		testCleanDatabase();
	
		byte[] certData = loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		Collection<Certificate> certificates = certificateDao.list("gm2552@cerner.com");
		assertEquals(1, certificates.size());
		cert = certificates.iterator().next();
		certificates = certificateDao.list(Arrays.asList(cert.getId()));
		
		assertEquals(1, certificates.size());
		cert = certificates.iterator().next();
		
		assertEquals("gm2552@cerner.com", cert.getOwner());
		
	}		
	
	@Test 
	public void testDeleteByOwner() throws Exception
	{
		testCleanDatabase();
	
		byte[] certData = loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		Collection<Certificate> certificates = certificateDao.list("gm2552@cerner.com");
		assertEquals(1, certificates.size());
		cert = certificates.iterator().next();		
		assertEquals("gm2552@cerner.com", cert.getOwner());
		
		certificateDao.delete("gm2552@cerner.com");
		certificates = certificateDao.list("gm2552@cerner.com");
		assertEquals(0, certificates.size());
	}		
	
	@Test 
	public void testDeleteById() throws Exception
	{
		testCleanDatabase();
	
		byte[] certData = loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		certificateDao.save(cert);
				
		Collection<Certificate> certificates = certificateDao.list("gm2552@cerner.com");
		assertEquals(1, certificates.size());
		cert = certificates.iterator().next();		
		assertEquals("gm2552@cerner.com", cert.getOwner());
		
		certificateDao.delete(Arrays.asList(cert.getId()));
		certificates = certificateDao.list("gm2552@cerner.com");
		assertEquals(0, certificates.size());
	}		
}
