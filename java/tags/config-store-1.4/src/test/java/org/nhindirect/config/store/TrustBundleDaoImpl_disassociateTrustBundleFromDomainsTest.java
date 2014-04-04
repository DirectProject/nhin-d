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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_disassociateTrustBundleFromDomainsTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testDisassociateTrustBundleFromDomains_associateDomainAndBundle_assertAssociationRemoved()
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
		
		Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain.getId());
		assertEquals(1, bundles.size());
		
		tbDao.disassociateTrustBundleFromDomains(bundle.getId());
		
		bundles = tbDao.getTrustBundlesByDomain(domain.getId());
		assertEquals(0, bundles.size());
	}
	
	@Test
	public void testDisassociateTrustBundlesFromDomain_unknownBundle_assertException()
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
		
		Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain.getId());
		assertEquals(1, bundles.size());
		
		boolean exceptionOccured = false;
		try
		{
			tbDao.disassociateTrustBundleFromDomains(746263);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	

	@Test
	public void testDisassociateTrustBundleFromDomains_noEntityManager_assertException()
	{

		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.disassociateTrustBundleFromDomains(1234);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
	
	@Test
	public void testDisassociateTrustBundleFromDomains_unknownErrorInRemove_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		
		final Query deleteQuery = mock(Query.class);
		doThrow(new RuntimeException("Just Passing Through")).when(deleteQuery).executeUpdate();
		when(mgr.createQuery("DELETE from TrustBundleDomainReltn tbd where tbd.trustBundle  = ?1")).thenReturn(deleteQuery);
		
		

		final TrustBundle bundle = mock(TrustBundle.class);
		final Query query = mock(Query.class);
		when(query.getSingleResult()).thenReturn(bundle);
		when(mgr.createQuery("SELECT tb from TrustBundle tb WHERE tb.id = ?1")).thenReturn(query);
		
		final TrustBundleDaoImpl dao  = new TrustBundleDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		
		final TrustBundleDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociateTrustBundleFromDomains(1234);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(query, times(1)).getSingleResult();
		verify(deleteQuery, times(1)).executeUpdate();
	}
}
