package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_updateTrustBundleAnchorsTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testUpdateTrustBundleAnchors_addNewAnchors_assertNewAnchors() throws Exception
	{
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		final TrustBundleAnchor anchor = new TrustBundleAnchor();
		anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
		anchor.setTrustBundle(bundle);
		
		tbDao.updateTrustBundleAnchors(bundle.getId(), 
				Calendar.getInstance(Locale.getDefault()), Arrays.asList(anchor), "6789");
		
		final TrustBundle addedBundle = tbDao.getTrustBundleById(bundle.getId());
		assertEquals(1, addedBundle.getTrustBundleAnchors().size());
		
		final TrustBundleAnchor addedAnchor = addedBundle.getTrustBundleAnchors().iterator().next();
		assertEquals(anchor.toCertificate(), addedAnchor.toCertificate());
	}
	
	@Test
	public void testUpdateTrustBundleAnchors_addAdditionalAnchors_assertNewAnchors() throws Exception
	{
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		TrustBundleAnchor anchor = new TrustBundleAnchor();
		anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
		anchor.setTrustBundle(bundle);
		
		bundle.setTrustBundleAnchors(Arrays.asList(anchor));
		
		tbDao.addTrustBundle(bundle);
		
		final TrustBundleAnchor additionalAnchor = new TrustBundleAnchor();
		additionalAnchor.setData(loadCertificateData("umesh.der"));
		additionalAnchor.setTrustBundle(bundle);
		
		tbDao.updateTrustBundleAnchors(bundle.getId(), 
				Calendar.getInstance(Locale.getDefault()), Arrays.asList(anchor, additionalAnchor), "6789");
		
		final TrustBundle addedBundle = tbDao.getTrustBundleById(bundle.getId());
		assertEquals("6789", addedBundle.getCheckSum());
		assertEquals(2, addedBundle.getTrustBundleAnchors().size());
		
		Iterator<TrustBundleAnchor> iter = addedBundle.getTrustBundleAnchors().iterator();
		
		TrustBundleAnchor addedAnchor = iter.next();
		assertEquals(anchor.toCertificate(), addedAnchor.toCertificate());
		
		addedAnchor = iter.next();
		assertEquals(additionalAnchor.toCertificate(), addedAnchor.toCertificate());
	}	
	
	@Test
	public void testUpdateTrustBundleAnchors_addSwapAnchors_assertNewAnchors() throws Exception
	{
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		TrustBundleAnchor anchor = new TrustBundleAnchor();
		anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
		anchor.setTrustBundle(bundle);
		
		bundle.setTrustBundleAnchors(Arrays.asList(anchor));
		
		tbDao.addTrustBundle(bundle);
		
		TrustBundle addedBundle = tbDao.getTrustBundleById(bundle.getId());
		assertEquals(1, addedBundle.getTrustBundleAnchors().size());
		
		final TrustBundleAnchor newAnchor = new TrustBundleAnchor();
		newAnchor.setData(loadCertificateData("umesh.der"));
		newAnchor.setTrustBundle(bundle);
		
		tbDao.updateTrustBundleAnchors(bundle.getId(), 
				Calendar.getInstance(Locale.getDefault()), Arrays.asList(newAnchor), "6789");
		
		addedBundle = tbDao.getTrustBundleById(bundle.getId());
		assertEquals(1, addedBundle.getTrustBundleAnchors().size());
		
		TrustBundleAnchor addedAnchor = addedBundle.getTrustBundleAnchors().iterator().next();
		assertEquals(newAnchor.toCertificate(), addedAnchor.toCertificate());
		
	}
	
	@Test
	public void testUpdateTrustBundleAnchors_bundleDoenstExist_assertException() throws Exception
	{

		boolean exceptionOccured = false;
		
		try
		{
			tbDao.updateTrustBundleAnchors(1234, Calendar.getInstance(Locale.getDefault()), new ArrayList<TrustBundleAnchor>(), "6789");
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdateTrustBundleAnchors_exceptionInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.updateTrustBundleAnchors(1234, Calendar.getInstance(Locale.getDefault()), new ArrayList<TrustBundleAnchor>(), "6789");
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdateTrustBundleAnchors_noEntityManager_assertException()
	{

		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.updateTrustBundleAnchors(1234, Calendar.getInstance(Locale.getDefault()), new ArrayList<TrustBundleAnchor>(), "6789");
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
}
