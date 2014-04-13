package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;


import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoImpl_getPolicyGroupsTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testGetPolicies_emptyPolicyGroupStore_assertNoPolicGroupsRetrieved()
	{
		final Collection<CertPolicyGroup> groups = polDao.getPolicyGroups();
		
		assertTrue(groups.isEmpty());
	}
	
	@Test
	public void testGetPolicies_singleEntryInPolicyGroupStore_assertPolicyGroupRetrieved()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		final Collection<CertPolicyGroup> groups = polDao.getPolicyGroups();
		
		assertEquals(1, groups.size());
		
		CertPolicyGroup addedGroup = groups.iterator().next();
		
		assertEquals(group.getPolicyGroupName(), addedGroup.getPolicyGroupName());	
		assertTrue(now.getTimeInMillis() <= addedGroup.getCreateTime().getTimeInMillis());
	}	
	
	@Test
	public void testGetPolicies_multipeEntriesInPolicyGroupStore_assertPolicyGroupsRetrieved()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		// add policy group 1
		final CertPolicyGroup group1 = new CertPolicyGroup();
		group1.setPolicyGroupName("Test Group1");
		
		polDao.addPolicyGroup(group1);
		
		// add policy 2
		final CertPolicyGroup group2 = new CertPolicyGroup();
		group2.setPolicyGroupName("Test Group2");
		
		polDao.addPolicyGroup(group2);
		
		final Collection<CertPolicyGroup> groups = polDao.getPolicyGroups();
		
		assertEquals(2, groups.size());
		
		Iterator<CertPolicyGroup> iter = groups.iterator();
		
		CertPolicyGroup addedGroup = iter.next();
		
		assertEquals(group1.getPolicyGroupName(), addedGroup.getPolicyGroupName());	
		assertTrue(now.getTimeInMillis() <= addedGroup.getCreateTime().getTimeInMillis());
		
		addedGroup = iter.next();
		
		assertEquals(group2.getPolicyGroupName(), addedGroup.getPolicyGroupName());	
		assertTrue(now.getTimeInMillis() <= addedGroup.getCreateTime().getTimeInMillis());
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
			dao.getPolicyGroups();
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
			dao.getPolicyGroups();
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
