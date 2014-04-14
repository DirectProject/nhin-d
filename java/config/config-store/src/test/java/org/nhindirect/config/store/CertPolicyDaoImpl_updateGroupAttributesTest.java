package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;

public class CertPolicyDaoImpl_updateGroupAttributesTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testUpdateGroupAttributes_updateName_assertUpdated()
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		CertPolicyGroup addedGroup = polDao.getPolicyGroupById(group.getId());
		assertNotNull(addedGroup);
		
		polDao.updateGroupAttributes(group.getId(), "Test Group 2");
		
		CertPolicyGroup updatedGroup =  polDao.getPolicyGroupById(group.getId());
		
		assertEquals("Test Group 2", updatedGroup.getPolicyGroupName());	
	}
	
	@Test
	public void testUpdateGroupAttributes_updateName_nullName_assertNotUpdated()
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		CertPolicyGroup addedGroup = polDao.getPolicyGroupById(group.getId());
		assertNotNull(addedGroup);
		
		polDao.updateGroupAttributes(group.getId(), null);
		
		CertPolicyGroup updatedGroup =  polDao.getPolicyGroupById(group.getId());
		
		assertEquals(group.getPolicyGroupName(), updatedGroup.getPolicyGroupName());	
	}	
	
	@Test
	public void testUpdateGroupAttributes_updateName_emptyName_assertNotUpdated()
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		CertPolicyGroup addedGroup = polDao.getPolicyGroupById(group.getId());
		assertNotNull(addedGroup);
		
		polDao.updateGroupAttributes(group.getId(), "");
		
		CertPolicyGroup updatedGroup =  polDao.getPolicyGroupById(group.getId());
		
		assertEquals(group.getPolicyGroupName(), updatedGroup.getPolicyGroupName());	
	}		
	
	@Test
	public void testUpdateGroupAttributes_policyGroupDoesntExist_assertException() throws Exception
	{
		
		boolean exceptionOccured = false;
		
		try
		{
			polDao.updateGroupAttributes(12345, "");
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdateGroupAttributes_exceptionInQuery_assertException() throws Exception
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.updateGroupAttributes(12345, "");
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void ttestUpdateGroupAttributes_noEntityManager_assertException() throws Exception
	{
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.updateGroupAttributes(12345, "");
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
