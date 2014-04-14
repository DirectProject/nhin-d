package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_addTrustBundleTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testAddTrustBundle_addBundle_noAnchors_assertAdded()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		final Collection<TrustBundle> bundles = tbDao.getTrustBundles();
		
		assertEquals(1, bundles.size());
		
		TrustBundle addedBundle = bundles.iterator().next();
		
		assertEquals("Test Bundle", addedBundle.getBundleName());
		assertEquals("http://testBundle/bundle.p7b", addedBundle.getBundleURL());
		assertEquals("12345", addedBundle.getCheckSum());
		assertEquals(5, addedBundle.getRefreshInterval());
		assertTrue(now.getTimeInMillis() <= addedBundle.getCreateTime().getTimeInMillis());
		assertNull(addedBundle.getLastRefreshAttempt());
		assertNull(addedBundle.getLastSuccessfulRefresh());
		assertNull(addedBundle.getLastRefreshError());
		assertNull(addedBundle.getSigningCertificateData());
		assertTrue(addedBundle.getTrustBundleAnchors().isEmpty());
	}
	
	@Test
	public void testAddTrustBundle_addBundle_withAnchors_assertAdded() throws Exception
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
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
		
		final Collection<TrustBundle> bundles = tbDao.getTrustBundles();
		
		assertEquals(1, bundles.size());
		
		TrustBundle addedBundle = bundles.iterator().next();
		
		assertEquals("Test Bundle", addedBundle.getBundleName());
		assertEquals("http://testBundle/bundle.p7b", addedBundle.getBundleURL());
		assertEquals("12345", addedBundle.getCheckSum());
		assertEquals(5, addedBundle.getRefreshInterval());
		assertTrue(now.getTimeInMillis() <= addedBundle.getCreateTime().getTimeInMillis());
		assertNull(addedBundle.getLastRefreshAttempt());
		assertNull(addedBundle.getLastSuccessfulRefresh());
		assertNull(addedBundle.getLastRefreshError());
		assertNull(addedBundle.getSigningCertificateData());
		assertEquals(1, addedBundle.getTrustBundleAnchors().size());
		

		TrustBundleAnchor addedAnchor = addedBundle.getTrustBundleAnchors().iterator().next();

		assertNotNull(addedAnchor.toCertificate());
		assertEquals(anchor.toCertificate(), addedAnchor.toCertificate());
		assertEquals(Thumbprint.toThumbprint(anchor.toCertificate()).toString(), addedAnchor.getThumbprint());
		assertEquals(anchor.toCertificate().getNotAfter(), addedAnchor.getValidEndDate().getTime());
		assertEquals(anchor.toCertificate().getNotBefore(), addedAnchor.getValidStartDate().getTime());	
		assertEquals(anchor.getTrustBundle().getBundleName(), bundle.getBundleName());
		assertEquals(anchor.getTrustBundle().getId(), addedBundle.getId());
	}	
	
	@Test
	public void testAddTrustBundle_addExistingBundle_assertException()
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		Collection<TrustBundle> bundles = tbDao.getTrustBundles();
		
		assertEquals(1, bundles.size());
		
		boolean exceptionOccured = false;
		
		try
		{
			tbDao.addTrustBundle(bundle);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		bundles = tbDao.getTrustBundles();
		
		assertEquals(1, bundles.size());		
	}	
	
	@Test
	public void testAddTrustBundle_noEntityManager_assertException()
	{

		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.addTrustBundle(new TrustBundle());
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
}
