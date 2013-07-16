package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;


import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;
import org.nhindirect.policy.PolicyLexicon;

public class CertPolicyDaoImpl_removePolicyUseFromGroupTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testRemovePolicyFromGroup_addedPolicyToGroup_assertAssociationRemoved()
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");
		polDao.addPolicyGroup(group);
		
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		polDao.addPolicyUseToGroup(group.getId(), policy.getId(), CertPolicyUse.PUBLIC_RESOLVER, true, false);
			
		CertPolicyGroup assocGroup = polDao.getPolicyGroupById(group.getId());
		assertEquals(1, assocGroup.getCertPolicyGroupReltn().size());
		
		polDao.removePolicyUseFromGroup(group.getCertPolicyGroupReltn().iterator().next().getId());
		
	}

	
	@Test
	public void testRemovePolicyFromGroup_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl certDao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			certDao.removePolicyUseFromGroup(1234);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
	
	@Test
	public void testRemovePolicyFromGroup_errorInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery("SELECT cpr from CertPolicyGroupReltn cpr WHERE cpr.id = ?1");
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.removePolicyUseFromGroup(1234);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);				
	}	
}
