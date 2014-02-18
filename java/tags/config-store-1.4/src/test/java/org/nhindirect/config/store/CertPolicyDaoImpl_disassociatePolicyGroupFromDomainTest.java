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
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoImpl_disassociatePolicyGroupFromDomainTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testDisassociatePolicyGroupFromDomain_associateDomainAndBundle_assertAssociationRemoved()
	{
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");
		
		polDao.addPolicyGroup(group);
		
		polDao.associatePolicyGroupToDomain(domain.getId(), group.getId());
		
		Collection<CertPolicyGroupDomainReltn> reltn = polDao.getPolicyGroupsByDomain(domain.getId());
		assertEquals(1, reltn.size());
		
		polDao.disassociatePolicyGroupFromDomain(domain.getId(), group.getId());
		
		reltn = polDao.getPolicyGroupsByDomain(domain.getId());
		assertEquals(0, reltn.size());
	}

	@Test
	public void testDisassociatePolicyGroupFromDomain_unknownDomain_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
	
		
		final DomainDao domainDao = mock(DomainDao.class);
		
		final CertPolicyDaoImpl dao  = new CertPolicyDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		CertPolicyDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociatePolicyGroupFromDomain(1234, 5678);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));
		verify(spyDao, never()).getPolicyGroupById(5678);		
	}


	@Test
	public void testDisassociatePolicyGroupFromDomain_unknownPolicy_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		final CertPolicyDaoImpl dao  = new CertPolicyDaoImpl();
		final Query query = mock(Query.class);
		doThrow(new NoResultException()).when(query).getSingleResult();
		when(mgr.createQuery("SELECT cpg from CertPolicyGroup cpg WHERE cpg.id = ?1")).thenReturn(query);
		
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		final CertPolicyDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociatePolicyGroupFromDomain(1234, 5678);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));
		verify(spyDao, times(1)).getPolicyGroupById(5678);		
		verify(mgr, never()).remove((CertPolicyGroupDomainReltn)any());				
	}		


	@Test
	public void testDisassociatePolicyGroupFromDomain_associationNotMade_assertException()
	{
		boolean exceptionOccured = false;
		
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");
		
		polDao.addPolicyGroup(group);
		
		
		Collection<CertPolicyGroupDomainReltn> reltn = polDao.getPolicyGroupsByDomain(domain.getId());
		assertEquals(0, reltn.size());
		
		try
		{
			polDao.disassociatePolicyGroupFromDomain(domain.getId(), group.getId());
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);	
	}	

	@Test
	public void testDisassociatePolicyGroupFromDomain_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.disassociatePolicyGroupFromDomain(1234, 5678);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	

	@Test
	public void testDisassociatePolicyGroupFromDomain_unknownErrorInRemove_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(mgr).remove((CertPolicyGroupDomainReltn)any());
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		final CertPolicyGroup group = mock(CertPolicyGroup.class);
		final Query query = mock(Query.class);
		when(query.getSingleResult()).thenReturn(group);
		when(mgr.createQuery("SELECT cpg from CertPolicyGroup cpg WHERE cpg.id = ?1")).thenReturn(query);
		
		final CertPolicyGroupDomainReltn reltn = mock(CertPolicyGroupDomainReltn.class);
		final Query findReltnQeury = mock(Query.class);
		when(findReltnQeury.getSingleResult()).thenReturn(reltn);
		when(mgr.createQuery("SELECT cpr from CertPolicyGroupDomainReltn cpr where cpr.domain  = ?1 " +
        		" and cpr.certPolicyGroup = ?2 ")).thenReturn(findReltnQeury);
		
		final CertPolicyDaoImpl dao  = new CertPolicyDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		
		final CertPolicyDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociatePolicyGroupFromDomain(1234, 5678);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(1234L);
		verify(spyDao, times(1)).getPolicyGroupById(5678);	
		verify(mgr, times(1)).remove((CertPolicyGroupDomainReltn)any());				
	}		

}
