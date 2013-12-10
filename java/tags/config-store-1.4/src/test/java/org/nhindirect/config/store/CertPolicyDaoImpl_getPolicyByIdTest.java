package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;
import org.nhindirect.policy.PolicyLexicon;

public class CertPolicyDaoImpl_getPolicyByIdTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testGetPolicyById_emptyStore_assertNoPolicyReturned()
	{
		assertNull(polDao.getPolicyById(1234));
	}
	
	@Test
	public void testGetPolicyById_singlePolicyInStore_idNotInStore_assertNoPolicyReturned()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test Policy");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		
		assertNull(polDao.getPolicyById(1234));
	}
	
	@Test
	public void testGetPolicyById_singlePolicyInStore_assertPolicyReturned()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), addedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), addedPolicy.getLexicon());
		assertTrue(now.getTimeInMillis() <= addedPolicy.getCreateTime().getTimeInMillis());
		assertTrue(Arrays.equals(policy.getPolicyData(), addedPolicy.getPolicyData()));
	}	
	
	@Test
	public void testGetPolicyById_exceptionInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyById(1234);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testGetPolicyById_noEntityManager_assertException()
	{
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getPolicyById(1234);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
}
