package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_getTrustBundlesByDomainTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testGetTrustBundlesByDomain_associationsExist_assertBundlesRetrieved()
	{
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://test/url/bundle");
		bundle.setCheckSum("1234");
		tbDao.addTrustBundle(bundle);
		
		tbDao.associateTrustBundleToDomain(domain.getId(), bundle.getId(), true, true);
		
		final Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain.getId());
		assertEquals(1, bundles.size());
	}
	
	@Test
	public void testGetTrustBundlesByDomain_multipleAssociationsExist_assertBundlesRetrieved()
	{
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final TrustBundle bundle1 = new TrustBundle();
		bundle1.setBundleName("Test Bundle1");
		bundle1.setBundleURL("http://test/url/bundle1");
		bundle1.setCheckSum("1234");
		tbDao.addTrustBundle(bundle1);
		
		final TrustBundle bundle2 = new TrustBundle();
		bundle2.setBundleName("Test Bundle2");
		bundle2.setBundleURL("http://test/url/bundle2");
		bundle2.setCheckSum("1234");
		tbDao.addTrustBundle(bundle2);
		
		tbDao.associateTrustBundleToDomain(domain.getId(), bundle1.getId(), true, true);
		tbDao.associateTrustBundleToDomain(domain.getId(), bundle2.getId(), true, true);
		
		final Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain.getId());
		assertEquals(2, bundles.size());
		
		Iterator<TrustBundleDomainReltn> bundleIter = bundles.iterator();
		assertEquals(bundle1.getBundleName(), bundleIter.next().getTrustBundle().getBundleName());
		assertEquals(bundle2.getBundleName(), bundleIter.next().getTrustBundle().getBundleName());
	}	
	
	@Test
	public void testGetTrustBundlesByDomain_multipleAssociationsExist_oneToEachDomain_assertBundlesRetrieved()
	{
		final Domain domain1 = new Domain();
		domain1.setDomainName("Test Domain 1");
		dmDao.add(domain1);
		
		final Domain domain2 = new Domain();
		domain2.setDomainName("Test Domain 2");
		dmDao.add(domain2);
		
		final TrustBundle bundle1 = new TrustBundle();
		bundle1.setBundleName("Test Bundle1");
		bundle1.setBundleURL("http://test/url/bundle1");
		bundle1.setCheckSum("1234");
		tbDao.addTrustBundle(bundle1);
		
		final TrustBundle bundle2 = new TrustBundle();
		bundle2.setBundleName("Test Bundle2");
		bundle2.setBundleURL("http://test/url/bundle2");
		bundle2.setCheckSum("1234");
		tbDao.addTrustBundle(bundle2);
		
		tbDao.associateTrustBundleToDomain(domain1.getId(), bundle1.getId(), true, true);
		tbDao.associateTrustBundleToDomain(domain2.getId(), bundle2.getId(), true, true);
		
		Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain1.getId());
		assertEquals(1, bundles.size());
		
		Iterator<TrustBundleDomainReltn> bundleIter = bundles.iterator();
		assertEquals(bundle1.getBundleName(), bundleIter.next().getTrustBundle().getBundleName());
		
		bundles = tbDao.getTrustBundlesByDomain(domain2.getId());
		assertEquals(1, bundles.size());
		
		bundleIter = bundles.iterator();
		assertEquals(bundle2.getBundleName(), bundleIter.next().getTrustBundle().getBundleName());

	}	
	
	@Test
	public void testGetTrustBundlesByDomain_multipleAssociationsExist_bundleToMultipeDomains_assertBundlesRetrieved()
	{
		final Domain domain1 = new Domain();
		domain1.setDomainName("Test Domain 1");
		dmDao.add(domain1);
		
		final Domain domain2 = new Domain();
		domain2.setDomainName("Test Domain 2");
		dmDao.add(domain2);
		
		final TrustBundle bundle1 = new TrustBundle();
		bundle1.setBundleName("Test Bundle1");
		bundle1.setBundleURL("http://test/url/bundle1");
		bundle1.setCheckSum("1234");
		tbDao.addTrustBundle(bundle1);
		
		
		tbDao.associateTrustBundleToDomain(domain1.getId(), bundle1.getId(), true, true);
		tbDao.associateTrustBundleToDomain(domain2.getId(), bundle1.getId(), true, true);
		
		Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain1.getId());
		assertEquals(1, bundles.size());
		
		Iterator<TrustBundleDomainReltn> bundleIter = bundles.iterator();
		assertEquals(bundle1.getBundleName(), bundleIter.next().getTrustBundle().getBundleName());
		
		bundles = tbDao.getTrustBundlesByDomain(domain2.getId());
		assertEquals(1, bundles.size());
		
		bundleIter = bundles.iterator();
		assertEquals(bundle1.getBundleName(), bundleIter.next().getTrustBundle().getBundleName());

	}
	
	@Test
	public void testGetTrustBundlesByDomain_noBundlesInDomain_assertBundlesNotRetrieved()
	{
		final Domain domain1 = new Domain();
		domain1.setDomainName("Test Domain 1");
		dmDao.add(domain1);
		
		Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain1.getId());
		assertEquals(0, bundles.size());
	}	
	
	@Test
	public void testGetTrustBundlesByDomain_unknownDomain_assertException()
	{
		boolean exceptionOccured = false;
		try
		{
			tbDao.getTrustBundlesByDomain(1234);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);	
	}	
	
	@Test
	public void testGetTrustBundlesByDomain_noEntityManager_assertException()
	{

		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getTrustBundlesByDomain(1234);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
	
	@Test
	public void testGetTrustBundlesByDomain_errorInGet_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		final TrustBundleDomainReltn reltn = mock(TrustBundleDomainReltn.class);
		final Query findReltnQeury = mock(Query.class);
		when(findReltnQeury.getSingleResult()).thenReturn(reltn);
		doThrow(new RuntimeException("Just Passing Through")).when(findReltnQeury).getResultList();
		when(mgr.createQuery("SELECT tbd from TrustBundleDomainReltn tbd where tbd.domain = ?1")).thenReturn(findReltnQeury);
		
		final TrustBundleDaoImpl dao  = new TrustBundleDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		
		final TrustBundleDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.getTrustBundlesByDomain(1234);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));
		verify(findReltnQeury, times(1)).getResultList();	
	}		
}
