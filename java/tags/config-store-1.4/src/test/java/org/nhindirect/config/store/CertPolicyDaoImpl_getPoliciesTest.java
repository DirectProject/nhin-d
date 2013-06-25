package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;
import org.nhindirect.policy.PolicyLexicon;

public class CertPolicyDaoImpl_getPoliciesTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testGetPolicies_emptyPolicyStore_assertNoPoliciesRetrieved()
	{
		final Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertTrue(policies.isEmpty());
	}
	
	@Test
	public void testGetPolicies_singleEntryInPolicyStore_assertPoliciesRetrieved()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test Policy");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		final Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertEquals(1, policies.size());
		
		CertPolicy addedPolicy = policies.iterator().next();
		
		assertEquals("Test Policy", addedPolicy.getPolicyName());	
		assertEquals(PolicyLexicon.XML, addedPolicy.getLexicon());
		assertTrue(now.getTimeInMillis() <= addedPolicy.getCreateTime().getTimeInMillis());
		assertTrue(Arrays.equals(new byte[] {1,2,3}, addedPolicy.getPolicyData()));
	}	
	
	@Test
	public void testGetPolicies_multipeEntriesInPolicyStore_assertPoliciesRetrieved()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
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
		
		final Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertEquals(2, policies.size());
		
		Iterator<CertPolicy> iter = policies.iterator();
		
		CertPolicy addedPolicy = iter.next();
		
		assertEquals(policy1.getPolicyName(), addedPolicy.getPolicyName());	
		assertEquals(policy1.getLexicon(), addedPolicy.getLexicon());
		assertTrue(now.getTimeInMillis() <= addedPolicy.getCreateTime().getTimeInMillis());
		assertTrue(Arrays.equals(policy1.getPolicyData(), addedPolicy.getPolicyData()));
		
		addedPolicy = iter.next();
		
		assertEquals(policy2.getPolicyName(), addedPolicy.getPolicyName());	
		assertEquals(policy2.getLexicon(), addedPolicy.getLexicon());
		assertTrue(now.getTimeInMillis() <= addedPolicy.getCreateTime().getTimeInMillis());
		assertTrue(Arrays.equals(policy2.getPolicyData(), addedPolicy.getPolicyData()));
	}
	
	@Test
	public void testGetPolices_exceptionInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicies();
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testGetPolices_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicies();
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
