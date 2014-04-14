package org.nhindirect.config.store;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Matchers.any;

import java.io.File;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nhindirect.common.crypto.impl.BootstrappedKeyStoreProtectionManager;
import org.nhindirect.config.model.exceptions.CertificateConversionException;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.store.dao.impl.CertificateDaoImpl;

public class CertificateDaoImp_saveWithProtectionMgr 
{
	private Certificate persistedCert;
	
	private static final String certBasePath = "src/test/resources/certs/"; 
	
	private static byte[] loadCertificateData(String certFileName) throws Exception
	{
		final File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	protected Certificate populateCert(String certFile, String keyFile) throws Exception
	{
		
		final byte[] certData = (keyFile != null && !keyFile.isEmpty()) ? 
				CertificateDaoTest.loadPkcs12FromCertAndKey(certFile, keyFile) :
					loadCertificateData(certFile);
		
		Certificate cert = new Certificate();
		cert.setData(certData);
		cert.setOwner("gm2552@cerner.com");
		
		return cert;
	}
	
	@Test
	public void testStripP12ProtectionTest_NoP12ProtectionWithManager_assertP12Returned() throws Exception
	{				
		final EntityManager manager = mock(EntityManager.class);
 
		doAnswer(new Answer<Object>()
				{
					public Object answer(InvocationOnMock invocation)
					{
						persistedCert = (Certificate)invocation.getArguments()[0];
						
						return "";
					}
				}).when(manager).persist(any());
		
		final BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager();
		mgr.setKeyStoreProtectionKey("12345");
		mgr.setPrivateKeyProtectionKey("67890");
		
		CertificateDaoImpl daoImpl = new CertificateDaoImpl();
		daoImpl.setKeyStoreProtectionManager(mgr);
		daoImpl.setEntityManager(manager);
		
		daoImpl.save(populateCert("gm2552.der", "gm2552Key.der"));

		assert(persistedCert.getData() != null);
		
		// make sure we can't access the P12 without a passphrase
		boolean exceptionOccured = false;
		try
		{
			CertUtils.toCertContainer(persistedCert.getData());
		}
		catch (CertificateConversionException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
	}
	
}
