package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;
import org.nhindirect.policy.PolicyLexicon;

public class CertPolicyDaoImpl_updatePolicyAttributesTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testUpdatePolicyAttributes_updateName_assertUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), "Test Pol", policy.getLexicon(), policy.getPolicyData());
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals("Test Pol", updatedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(policy.getPolicyData(), updatedPolicy.getPolicyData()));
	}
	
	@Test
	public void testUpdatePolicyAttributes_updateName_nullName_assertNotUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), null, policy.getLexicon(), policy.getPolicyData());
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), updatedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(policy.getPolicyData(), updatedPolicy.getPolicyData()));
	}	
	
	@Test
	public void testUpdatePolicyAttributes_updateName_emptyName_assertNotUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), "", policy.getLexicon(), policy.getPolicyData());
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), updatedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(policy.getPolicyData(), updatedPolicy.getPolicyData()));
	}		
	
	@Test
	public void testUpdatePolicyAttributes_updateData_assertUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), policy.getPolicyName(), policy.getLexicon(), new byte[] {4,5,6});
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), updatedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(new byte[] {4,5,6}, updatedPolicy.getPolicyData()));
	}
	
	@Test
	public void testUpdatePolicyAttributes_updateData_nullData_assertNotUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), policy.getPolicyName(), policy.getLexicon(), null);
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), updatedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(policy.getPolicyData(), updatedPolicy.getPolicyData()));
	}	
	
	@Test
	public void testUpdatePolicyAttributes_updateData_emptyData_assertNotUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), policy.getPolicyName(), policy.getLexicon(), new byte[] {});
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), updatedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(policy.getPolicyData(), updatedPolicy.getPolicyData()));
	}		
	
	@Test
	public void testUpdatePolicyAttributes_updateLexicon_assertUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), policy.getPolicyName(), PolicyLexicon.JAVA_SER, policy.getPolicyData());
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), updatedPolicy.getPolicyName());	
		assertEquals(PolicyLexicon.JAVA_SER, updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(policy.getPolicyData(), updatedPolicy.getPolicyData()));
	}
	
	@Test
	public void testUpdatePolicyAttributes_updateLexicon_nullLexicon_assertNotUpdated()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		CertPolicy addedPolicy = polDao.getPolicyById(policy.getId());
		assertNotNull(addedPolicy);
		
		polDao.updatePolicyAttributes(policy.getId(), policy.getPolicyName(), null, policy.getPolicyData());
		
		CertPolicy updatedPolicy =  polDao.getPolicyById(policy.getId());
		
		assertEquals(policy.getPolicyName(), updatedPolicy.getPolicyName());	
		assertEquals(policy.getLexicon(), updatedPolicy.getLexicon());
		assertTrue(Arrays.equals(policy.getPolicyData(), updatedPolicy.getPolicyData()));
	}		
	
	@Test
	public void testUpdatePolicyAttributes_policyDoesntExist_assertException() throws Exception
	{
		
		boolean exceptionOccured = false;
		
		try
		{
			polDao.updatePolicyAttributes(12345, "", null, null);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdatePolicyAttributes_exceptionInQuery_assertException() throws Exception
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.updatePolicyAttributes(12345, "", null, null);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void ttestUpdatePolicyAttributes_noEntityManager_assertException() throws Exception
	{
		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.updatePolicyAttributes(12345, "", null, null);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
