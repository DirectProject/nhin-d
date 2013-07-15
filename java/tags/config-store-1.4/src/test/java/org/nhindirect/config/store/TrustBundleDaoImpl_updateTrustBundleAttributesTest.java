package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_updateTrustBundleAttributesTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testTrustBundleAttributes_updateCert_assertCertUpdated() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		TrustBundleAnchor anchor = new TrustBundleAnchor();
		anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), bundle.getBundleName(), bundle.getBundleURL(), anchor.toCertificate(),
				bundle.getRefreshInterval());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(bundle.getBundleName(), updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
		assertEquals(anchor.toCertificate(), updatedBundle.toSigningCertificate());
		
	}
	
	@Test
	public void testTrustBundleAttributes_updateCert_setNull_assertCertNull() throws Exception
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
		
		assertEquals(bundle.getBundleName(), updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
		assertNull(updatedBundle.getSigningCertificateData());
		
	}
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateBundleName_assertNameUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), "New Test Bundle Name", bundle.getBundleURL(), 
		null, bundle.getRefreshInterval());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals("New Test Bundle Name", updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}	
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateBundleName_nullName_assertNameNotUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), null, bundle.getBundleURL(), 
		null, bundle.getRefreshInterval());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(bundle.getBundleName(), updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateBundleName_emptyName_assertNameNotUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), "", bundle.getBundleURL(), 
		null, bundle.getRefreshInterval());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(bundle.getBundleName(), updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}
	
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateBundleURL_assertURLUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), bundle.getBundleName(), "http://testBundle/bundle.p7b333", 
		null, bundle.getRefreshInterval());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(bundle.getBundleName(), bundle.getBundleName());
		assertEquals("http://testBundle/bundle.p7b333", updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}	
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateBundleURL_nullURL_assertUrlNotUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), bundle.getBundleName(), null, 
		null, bundle.getRefreshInterval());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(bundle.getBundleName(), updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}
	
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateBundleURL_emptyURL_assertUrlNotUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), bundle.getBundleName(), "", 
		null, bundle.getRefreshInterval());
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(bundle.getBundleName(), updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(bundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}
	
	@Test
	public void testUpdateTrustBundleSigningCertificate_updateBundleRefreshInterval_assertIntervalUpdate() throws Exception
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateTrustBundleAttributes(bundle.getId(), bundle.getBundleName(), bundle.getBundleURL(), 
		null, 7);
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals(bundle.getBundleName(), updatedBundle.getBundleName());
		assertEquals(bundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(7, updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(bundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}
	
	
	@Test
	public void testUpdateTrustBundleAttributes_bundleDoesntExist_assertException() throws Exception
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
	public void testUpdateTrustBundleAttributes_exceptionInQuery_assertException() throws Exception
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
	public void testUpdateTrustBundleAttributes_noEntityManager_assertException() throws Exception
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
