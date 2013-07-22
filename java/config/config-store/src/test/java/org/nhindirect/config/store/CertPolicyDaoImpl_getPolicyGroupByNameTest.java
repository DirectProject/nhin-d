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

public class CertPolicyDaoImpl_getPolicyGroupByNameTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testGetPolicyGroupByName_emptyStore_assertNoPolicyReturned()
	{
		assertNull(polDao.getPolicyGroupByName("Test Group"));
	}
	
	@Test
	public void testGetPolicyGroupByName_singlePolicyGroupInStore_nameNotInStore_assertNoPolicyGroupReturned()
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		
		assertNull(polDao.getPolicyGroupByName("Test Group X"));
	}
	
	@Test
	public void testGetPolicyGroupByName_singlePolicyGroupInStore_assertPolicyGroupReturned()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		CertPolicyGroup addedGroup = polDao.getPolicyGroupByName("Test GrouP");
		
		assertEquals(group.getPolicyGroupName(), addedGroup.getPolicyGroupName());	
		assertTrue(now.getTimeInMillis() <= addedGroup.getCreateTime().getTimeInMillis());
	}	
	
	@Test
	public void testGetPolicyGroupByName_exceptionInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyGroupByName("any");
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testGetPolicyGroupByName_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyGroupByName("any");
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
