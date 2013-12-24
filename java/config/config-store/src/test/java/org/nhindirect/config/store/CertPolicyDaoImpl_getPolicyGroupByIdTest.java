package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoImpl_getPolicyGroupByIdTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testGetPolicyGroupById_emptyStore_assertNoPolicyGroupReturned()
	{
		assertNull(polDao.getPolicyGroupById(1234));
	}
	
	@Test
	public void testGetPolicyGroupById_singlePolicyGroupInStore_idNotInStore_assertNoPolicyGroupReturned()
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);		
		
		assertNull(polDao.getPolicyGroupById(1234));
	}
	
	@Test
	public void testGetPolicyGroupById_singlePolicyGroupInStore_assertPolicyGroupReturned()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		CertPolicyGroup addedGroup = polDao.getPolicyGroupById(group.getId());
		
		assertEquals(group.getPolicyGroupName(), addedGroup.getPolicyGroupName());	
		assertTrue(now.getTimeInMillis() <= addedGroup.getCreateTime().getTimeInMillis());
	}	
	
	@Test
	public void testGetPolicyGroupById_exceptionInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyGroupById(1234);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testGetPolicyGroupById_noEntityManager_assertException()
	{
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyGroupById(1234);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
