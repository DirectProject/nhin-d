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
import org.nhindirect.policy.PolicyLexicon;

public class CertPolicyDaoImpl_deletePoliciesTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testDeletePolicies_singlePolicy_assertPolicyDeleted()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test Policy");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertEquals(1, policies.size());
		
		polDao.deletePolicies(new long[] {policies.iterator().next().getId()});
		
		policies = polDao.getPolicies();
		
		assertEquals(0, policies.size());
	}
	
	@Test
	public void testDeletePolicies_multiplePolicies_assertSinglePolicyDeleted()
	{
		// add policy 1
		final CertPolicy policy1 = new CertPolicy();
		policy1.setPolicyName("Test Policy1");
		policy1.setLexicon(PolicyLexicon.XML);
		policy1.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy1);
		
		// add policy 2
		final CertPolicy policy2 = new CertPolicy();
		policy2.setPolicyName("Test Policy2");
		policy2.setLexicon(PolicyLexicon.JAVA_SER);
		policy2.setPolicyData(new byte[] {4,5,6});
		
		polDao.addPolicy(policy2);
		
		Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertEquals(2, policies.size());
		
		polDao.deletePolicies(new long[] {policies.iterator().next().getId()});
		
		policies = polDao.getPolicies();
		
		assertEquals(1, policies.size());
	}
	
	@Test
	public void testDeletePolicies_multiplePolicyes_assertAllPoliciesDeleted()
	{
		// add policy 1
		final CertPolicy policy1 = new CertPolicy();
		policy1.setPolicyName("Test Policy1");
		policy1.setLexicon(PolicyLexicon.XML);
		policy1.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy1);
		
		// add policy 2
		final CertPolicy policy2 = new CertPolicy();
		policy2.setPolicyName("Test Policy2");
		policy2.setLexicon(PolicyLexicon.JAVA_SER);
		policy2.setPolicyData(new byte[] {4,5,6});
		
		polDao.addPolicy(policy2);
		
		Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertEquals(2, policies.size());
		
		Iterator<CertPolicy> iter = policies.iterator();
		
		polDao.deletePolicies(new long[] {iter.next().getId(), iter.next().getId()});
		
		policies = polDao.getPolicies();
		
		assertEquals(0, policies.size());
	}	
	
	@Test
	public void testDeletePolicies_nullArray_assertNoError()
	{
		polDao.deletePolicies(null);
	}
	
	@Test
	public void testDeletePolicies_emptyArray_assertNoError()
	{
		polDao.deletePolicies(new long[]{});
	}	
	
	@Test
	public void testDeletePolicies_exceptionInQuery_assertNoException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).remove((TrustBundle)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.deletePolicies(new long[] {1234});
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertFalse(exceptionOccured);
		verify(manager, never()).remove((CertPolicy)any());
		verify(manager, never()).flush();
	}
	
	@Test
	public void testDeletePolicies_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.deletePolicies(new long[] {1234});
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
