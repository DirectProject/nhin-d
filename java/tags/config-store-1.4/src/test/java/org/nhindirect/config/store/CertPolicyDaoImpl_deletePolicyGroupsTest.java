package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoImpl_deletePolicyGroupsTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testDeletePolicyGroups_singlePolicyGroup_assertPolicyGroupDeleted()
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		Collection<CertPolicyGroup> groups = polDao.getPolicyGroups();
		
		assertEquals(1, groups.size());
		
		polDao.deletePolicyGroups(new long[] {groups.iterator().next().getId()});
		
		groups = polDao.getPolicyGroups();
		
		assertEquals(0, groups.size());
	}
	
	@Test
	public void testDeletePolicyGroups_multiplePolicyGroups_assertSinglePolicyGroupDeleted()
	{
		// add policy group 1
		final CertPolicyGroup group1 = new CertPolicyGroup();
		group1.setPolicyGroupName("Test Group1");
		
		polDao.addPolicyGroup(group1);
		
		// add policy 2
		final CertPolicyGroup group2 = new CertPolicyGroup();
		group2.setPolicyGroupName("Test Group2");
		
		polDao.addPolicyGroup(group2);
		
		Collection<CertPolicyGroup> groups = polDao.getPolicyGroups();
		
		assertEquals(2, groups.size());
		
		polDao.deletePolicyGroups(new long[] {groups.iterator().next().getId()});
		
		groups = polDao.getPolicyGroups();
		
		assertEquals(1, groups.size());
	}
	
	@Test
	public void testDeletePolicyGroups_multiplePolicyGroups_assertAllPolicyGroupsDeleted()
	{
		// add policy group 1
		final CertPolicyGroup group1 = new CertPolicyGroup();
		group1.setPolicyGroupName("Test Group1");
		
		polDao.addPolicyGroup(group1);
		
		// add policy 2
		final CertPolicyGroup group2 = new CertPolicyGroup();
		group2.setPolicyGroupName("Test Group2");
		
		polDao.addPolicyGroup(group2);
		
		Collection<CertPolicyGroup> groups = polDao.getPolicyGroups();
		
		assertEquals(2, groups.size());
		
		Iterator<CertPolicyGroup> iter = groups.iterator();
		
		polDao.deletePolicyGroups(new long[] {iter.next().getId(), iter.next().getId()});
		
		groups = polDao.getPolicyGroups();
		
		assertEquals(0, groups.size());
	}	
	
	@Test
	public void testDeletePolicyGroups_nullArray_assertNoError()
	{
		polDao.deletePolicyGroups(null);
	}
	
	@Test
	public void testDeletePolicyGroups_emptyArray_assertNoError()
	{
		polDao.deletePolicyGroups(new long[]{});
	}	
	
	@Test
	public void testDeletePolicyGroups_exceptionInQuery_assertNoException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).remove((TrustBundle)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.deletePolicyGroups(new long[] {1234});
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertFalse(exceptionOccured);
		verify(manager, never()).remove((CertPolicyGroup)any());
		verify(manager, never()).flush();
	}
	
	@Test
	public void testDeletePolicyGroups_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.deletePolicyGroups(new long[] {1234});
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
