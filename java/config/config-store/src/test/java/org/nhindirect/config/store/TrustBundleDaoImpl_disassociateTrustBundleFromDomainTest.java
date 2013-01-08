package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.junit.Test;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_disassociateTrustBundleFromDomainTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testDisassociateTrustBundleFromDomain_associateDomainAndBundle_assertAssociationRemoved()
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
		
		tbDao.disassociateTrustBundleFromDomain(domain.getId(), bundle.getId());
		
		bundles = tbDao.getTrustBundlesByDomain(domain.getId());
		assertEquals(0, bundles.size());
	}
	
	@Test
	public void testDisassociateTrustBundleFromDomain_unknownDomain_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
	
		
		final DomainDao domainDao = mock(DomainDao.class);
		
		final TrustBundleDaoImpl dao  = new TrustBundleDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		TrustBundleDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociateTrustBundleFromDomain(1234, 5678);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));
		verify(spyDao, never()).getTrustBundleById(5678);		
	}
	
	@Test
	public void testDisassociateTrustBundleFromDomain_unknownTrustBundle_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		final TrustBundleDaoImpl dao  = new TrustBundleDaoImpl();
		final Query query = mock(Query.class);
		doThrow(new NoResultException()).when(query).getSingleResult();
		when(mgr.createQuery("SELECT tb from TrustBundle tb WHERE tb.id = ?1")).thenReturn(query);
		
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		final TrustBundleDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociateTrustBundleFromDomain(1234, 5678);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));
		verify(spyDao, times(1)).getTrustBundleById(5678);		
		verify(mgr, never()).remove((TrustBundleDomainReltn)any());				
	}		
	
	@Test
	public void testDisassociateTrustBundleFromDomain_associationNotMade_assertException()
	{
		boolean exceptionOccured = false;
		
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://test/url/bundle");
		bundle.setCheckSum("1234");
		tbDao.addTrustBundle(bundle);
		
		
		Collection<TrustBundleDomainReltn> bundles = tbDao.getTrustBundlesByDomain(domain.getId());
		assertEquals(0, bundles.size());
		
		try
		{
			tbDao.disassociateTrustBundleFromDomain(domain.getId(), bundle.getId());
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);	
	}	
	
	@Test
	public void testDisassociateTrustBundleFromDomain_noEntityManager_assertException()
	{

		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.disassociateTrustBundleFromDomain(1234, 5678);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
	
	@Test
	public void testDisassociateTrustBundleFromDomain_unknownErrorInRemove_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(mgr).remove((TrustBundleDomainReltn)any());
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		final TrustBundle bundle = mock(TrustBundle.class);
		final Query query = mock(Query.class);
		when(query.getSingleResult()).thenReturn(bundle);
		when(mgr.createQuery("SELECT tb from TrustBundle tb WHERE tb.id = ?1")).thenReturn(query);
		
		final TrustBundleDomainReltn reltn = mock(TrustBundleDomainReltn.class);
		final Query findReltnQeury = mock(Query.class);
		when(findReltnQeury.getSingleResult()).thenReturn(reltn);
		when(mgr.createQuery("SELECT tbd from TrustBundleDomainReltn tbd where tbd.domain  = ?1 " +
	        		" and tbd.trustBundle = ?2 ")).thenReturn(findReltnQeury);
		
		final TrustBundleDaoImpl dao  = new TrustBundleDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		
		final TrustBundleDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociateTrustBundleFromDomain(1234, 5678);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));
		verify(spyDao, times(1)).getTrustBundleById(5678);	
		verify(mgr, times(1)).remove((TrustBundleDomainReltn)any());				
	}		
}
