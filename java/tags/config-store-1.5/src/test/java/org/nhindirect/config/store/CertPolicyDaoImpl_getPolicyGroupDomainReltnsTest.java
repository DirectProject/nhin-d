package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoImpl_getPolicyGroupDomainReltnsTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testGetPolicyGroupsDomainReltns_emptyReltnStore_assertNoReltnsRetrieved()
	{
		final Collection<CertPolicyGroupDomainReltn> groups = polDao.getPolicyGroupDomainReltns();
		
		assertTrue(groups.isEmpty());
	}
	
	@Test
	public void testGetPolicyGroupsDomainReltns_singleEntryInReltns_assertReltnRetrieved()
	{
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");
		
		polDao.addPolicyGroup(group);
		
		polDao.associatePolicyGroupToDomain(domain.getId(), group.getId());
		
		final Collection<CertPolicyGroupDomainReltn> reltns = polDao.getPolicyGroupDomainReltns();
		assertEquals(1, reltns.size());
		
		CertPolicyGroupDomainReltn reltn = reltns.iterator().next();
		assertEquals(group, reltn.getCertPolicyGroup());
		assertEquals(domain, reltn.getDomain());
		
	}	
	
	@Test
	public void testGetPolicyGroupsDomainReltns_multipeEntriesReltns_assertReltnsRetrieved()
	{
		final Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		dmDao.add(domain);
		
		final CertPolicyGroup group1 = new CertPolicyGroup();
		group1.setPolicyGroupName("Test Group 1");
		
		final CertPolicyGroup group2 = new CertPolicyGroup();
		group2.setPolicyGroupName("Test Group 2");
		
		polDao.addPolicyGroup(group1);
		polDao.addPolicyGroup(group2);
		
		polDao.associatePolicyGroupToDomain(domain.getId(), group1.getId());
		polDao.associatePolicyGroupToDomain(domain.getId(), group2.getId());
		
		final Collection<CertPolicyGroupDomainReltn> reltns = polDao.getPolicyGroupDomainReltns();
		assertEquals(2, reltns.size());
		
		Iterator<CertPolicyGroupDomainReltn> iter = reltns.iterator();
		
		CertPolicyGroupDomainReltn reltn = iter.next();
		assertEquals(group1, reltn.getCertPolicyGroup());
		assertEquals(domain, reltn.getDomain());
		
		reltn = iter.next();
		assertEquals(group2, reltn.getCertPolicyGroup());
		assertEquals(domain, reltn.getDomain());
	}
	
	@Test
	public void testGetPolicyGroups_exceptionInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyGroupDomainReltns();
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testGetPolicyGroups_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyGroupDomainReltns();
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
