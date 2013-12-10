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
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoImpl_disassociatePolicyGroupsFromDomainTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testDisassociatePolicyGroupsFromDomain_associateDomainAndPolicy_assertAssociationRemoved()
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
		
		polDao.disassociatePolicyGroupsFromDomain(domain.getId());
		
		reltn = polDao.getPolicyGroupsByDomain(domain.getId());
		assertEquals(0, reltn.size());
	}
	
	@Test
	public void testDisassociatePolicyGroupsFromDomain_unknownDomain_assertException()
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
			spyDao.disassociatePolicyGroupsFromDomain(1234);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));	
	}	
	
	
	@Test
	public void testDisassociatePolicyGroupsFromDomain_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.disassociatePolicyGroupsFromDomain(1234);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
	
	@Test
	public void testDisassociatePolicyGroupsFromDomain_unknownErrorInRemove_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		
		final Query deleteQuery = mock(Query.class);
		doThrow(new RuntimeException("Just Passing Through")).when(deleteQuery).executeUpdate();
		when(mgr.createQuery("DELETE from CertPolicyGroupDomainReltn cpr where cpr.domain  = ?1")).thenReturn(deleteQuery);
		
		
		final CertPolicyDaoImpl dao  = new CertPolicyDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		
		final CertPolicyDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.disassociatePolicyGroupsFromDomain(1234);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(domainDao, times(1)).getDomain(new Long(1234));
		verify(deleteQuery, times(1)).executeUpdate();
	}
	
}
