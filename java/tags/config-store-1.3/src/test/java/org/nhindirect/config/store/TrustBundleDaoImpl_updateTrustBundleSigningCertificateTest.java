package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;


import org.junit.Test;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_updateTrustBundleSigningCertificateTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateSigningCert_assertCertUpdated() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		TrustBundleAnchor anchor = new TrustBundleAnchor();
		anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
		
		tbDao.updateTrustBundleSigningCertificate(bundle.getId(), anchor.toCertificate());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(anchor.toCertificate(), updatedBundle.toSigningCertificate());
		
	}
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateCert_setNull_assertBundleUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		bundle.setSigningCertificateData(loadCertificateData("secureHealthEmailCACert.der"));
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleSigningCertificate(bundle.getId(), null);
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertNull(updatedBundle.getSigningCertificateData());
		
	}
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_bundleDoesntExist_assertException() throws Exception
	{
		
		boolean exceptionOccured = false;
		
		try
		{
			TrustBundleAnchor anchor = new TrustBundleAnchor();
			anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
			
			tbDao.updateTrustBundleSigningCertificate(1234, anchor.toCertificate());
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_exceptionInQuery_assertException() throws Exception
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			TrustBundleAnchor anchor = new TrustBundleAnchor();
			anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
			
			tbDao.updateTrustBundleSigningCertificate(1234, anchor.toCertificate());
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_noEntityManager_assertException() throws Exception
	{
		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			TrustBundleAnchor anchor = new TrustBundleAnchor();
			anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
			
			dao.updateTrustBundleSigningCertificate(1234, anchor.toCertificate());
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
