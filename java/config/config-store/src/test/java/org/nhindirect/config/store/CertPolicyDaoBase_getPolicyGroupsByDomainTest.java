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
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoBase_getPolicyGroupsByDomainTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testGetPolicyGroupsByDomain_associationsExist_assertPoliciesRetrieved()
	{
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");
		
		polDao.addPolicyGroup(group);
		
		polDao.associatePolicyGroupToDomain(domain.getId(), group.getId());
		
		final Collection<CertPolicyGroupDomainReltn> policies = polDao.getPolicyGroupsByDomain(domain.getId());
		assertEquals(1, policies.size());
	}
	
	@Test
	public void testGetPolicyGroupsByDomain_multipleAssociationsExist_assertPoliciesRetrieved()
	{
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final CertPolicyGroup group1 = new CertPolicyGroup();
		group1.setPolicyGroupName("Test Group1");
		
		polDao.addPolicyGroup(group1);
		

		final CertPolicyGroup group2 = new CertPolicyGroup();
		group2.setPolicyGroupName("Test Group2");
		
		polDao.addPolicyGroup(group2);
		
		polDao.associatePolicyGroupToDomain(domain.getId(), group1.getId());
		polDao.associatePolicyGroupToDomain(domain.getId(), group2.getId());
		
		final Collection<CertPolicyGroupDomainReltn> policies = polDao.getPolicyGroupsByDomain(domain.getId());
		assertEquals(2, policies.size());
		
		Iterator<CertPolicyGroupDomainReltn> polIter = policies.iterator();
		assertEquals(group1.getPolicyGroupName(), polIter.next().getCertPolicyGroup().getPolicyGroupName());
		assertEquals(group2.getPolicyGroupName(), polIter.next().getCertPolicyGroup().getPolicyGroupName());
	}	
	
	@Test
	public void testGetPolicyGroupsByDomain_multipleAssociationsExist_oneToEachDomain_assertPoliciesRetrieved()
	{
		final Domain domain1 = new Domain();
		domain1.setDomainName("Test Domain 1");
		dmDao.add(domain1);
		
		final Domain domain2 = new Domain();
		domain2.setDomainName("Test Domain 2");
		dmDao.add(domain2);
		
		final CertPolicyGroup group1 = new CertPolicyGroup();
		group1.setPolicyGroupName("Test Group1");
		
		polDao.addPolicyGroup(group1);
		
		final CertPolicyGroup group2 = new CertPolicyGroup();
		group2.setPolicyGroupName("Test Group2");
		
		polDao.addPolicyGroup(group2);
		
		polDao.associatePolicyGroupToDomain(domain1.getId(), group1.getId());
		polDao.associatePolicyGroupToDomain(domain2.getId(), group2.getId());
		
		Collection<CertPolicyGroupDomainReltn> policies = polDao.getPolicyGroupsByDomain(domain1.getId());
		assertEquals(1, policies.size());
		
		Iterator<CertPolicyGroupDomainReltn> polIter = policies.iterator();
		assertEquals(group1.getPolicyGroupName(), polIter.next().getCertPolicyGroup().getPolicyGroupName());
		
		policies = polDao.getPolicyGroupsByDomain(domain2.getId());
		assertEquals(1, policies.size());
		
		polIter = policies.iterator();
		assertEquals(group2.getPolicyGroupName(), polIter.next().getCertPolicyGroup().getPolicyGroupName());

	}	

	@Test
	public void testGetPolicyGroupsByDomain_multipleAssociationsExist_policyToMultipeDomains_assertPoliciesRetrieved()
	{
		final Domain domain1 = new Domain();
		domain1.setDomainName("Test Domain 1");
		dmDao.add(domain1);
		
		final Domain domain2 = new Domain();
		domain2.setDomainName("Test Domain 2");
		dmDao.add(domain2);
		
		final CertPolicyGroup group1 = new CertPolicyGroup();
		group1.setPolicyGroupName("Test Group1");
		
		polDao.addPolicyGroup(group1);
		
		
		polDao.associatePolicyGroupToDomain(domain1.getId(), group1.getId());
		polDao.associatePolicyGroupToDomain(domain2.getId(), group1.getId());
		
		Collection<CertPolicyGroupDomainReltn> policies = polDao.getPolicyGroupsByDomain(domain1.getId());
		assertEquals(1, policies.size());
		
		Iterator<CertPolicyGroupDomainReltn> polIter = policies.iterator();
		assertEquals(group1.getPolicyGroupName(), polIter.next().getCertPolicyGroup().getPolicyGroupName());
		
		policies = polDao.getPolicyGroupsByDomain(domain2.getId());
		assertEquals(1, policies.size());
		
		polIter = policies.iterator();
		assertEquals(group1.getPolicyGroupName(), polIter.next().getCertPolicyGroup().getPolicyGroupName());

	}
	

	@Test
	public void testGetPolicyGroupsByDomain_noPoliciesInDomain_assertPoliciesNotRetrieved()
	{
		final Domain domain1 = new Domain();
		domain1.setDomainName("Test Domain 1");
		dmDao.add(domain1);
		
		Collection<CertPolicyGroupDomainReltn> policies = polDao.getPolicyGroupsByDomain(domain1.getId());
		assertEquals(0, policies.size());
	}	
	

	@Test
	public void testGetPolicyGroupsByDomain_unknownDomain_assertException()
	{
		boolean exceptionOccured = false;
		try
		{
			polDao.getPolicyGroupsByDomain(1234);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);	
	}	

	@Test
	public void testGetPolicyGroupsByDomain_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyGroupsByDomain(1234);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	

	@Test
	public void testGetPolicyGroupsByDomain_errorInGet_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		final Domain domain = mock(Domain.class);
		
		final DomainDao domainDao = mock(DomainDao.class);
		when(domainDao.getDomain(new Long(1234))).thenReturn(domain);
		
		final CertPolicyGroupDomainReltn reltn = mock(CertPolicyGroupDomainReltn.class);
		final Query findReltnQeury = mock(Query.class);
		when(findReltnQeury.getSingleResult()).thenReturn(reltn);
		doThrow(new RuntimeException("Just Passing Through")).when(findReltnQeury).getResultList();
		when(mgr.createQuery("SELECT cpr from CertPolicyGroupDomainReltn cpr where cpr.domain = ?1")).thenReturn(findReltnQeury);
		
		final CertPolicyDaoImpl dao  = new CertPolicyDaoImpl();
		dao.setDomainDao(domainDao);
		dao.setEntityManager(mgr);
		
		
		final CertPolicyDaoImpl spyDao = spy(dao);
		
		try
		{
			spyDao.getPolicyGroupsByDomain(1234);
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
