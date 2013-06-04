package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;
import org.nhindirect.policy.PolicyLexicon;

public class CertPolicyDaoImpl_addPolicyTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testAddPolicy_addPolicy_assertAdded()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		final Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertEquals(1, policies.size());
		
		CertPolicy addedPolicy = policies.iterator().next();
		
		assertEquals(policy.getPolicyName(), addedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), addedPolicy.getLexicon());
		assertTrue(now.getTimeInMillis() <= addedPolicy.getCreateTime().getTimeInMillis());
		assertTrue(Arrays.equals(policy.getPolicyData(), addedPolicy.getPolicyData()));
	}
	
	
	@Test
	public void testAddPolicy_addExistingPolicy_assertException()
	{
		
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		Collection<CertPolicy> policies = polDao.getPolicies();
		
		assertEquals(1, policies.size());
		
		boolean exceptionOccured = false;
		
		try
		{
			polDao.addPolicy(policy);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		policies = polDao.getPolicies();
		
		assertEquals(1, policies.size());		
	}	
	
	@Test
	public void testAddPolicy_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.addPolicy(new CertPolicy());
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
