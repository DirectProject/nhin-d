package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;


public class CertPolicyDaoImpl_addPolicyGroupTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testAddPolicyGroup_addPolicyGroup_assertAdded()
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
	public void testAddPolicyGroup_addExistingPolicy_assertException()
	{
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");
		
		polDao.addPolicyGroup(group);
		
		Collection<CertPolicyGroup> groups = polDao.getPolicyGroups();
		
		assertEquals(1, groups.size());
		
		boolean exceptionOccured = false;
		
		try
		{
			polDao.addPolicyGroup(group);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);

		groups = polDao.getPolicyGroups();
		
		assertEquals(1, groups.size());		
	}	
	
	@Test
	public void testAddPolicyGroup_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.addPolicyGroup(new CertPolicyGroup());
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
