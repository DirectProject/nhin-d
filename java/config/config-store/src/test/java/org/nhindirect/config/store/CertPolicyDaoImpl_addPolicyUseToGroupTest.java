package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.CertPolicyDaoImpl;
import org.nhindirect.policy.PolicyLexicon;

public class CertPolicyDaoImpl_addPolicyUseToGroupTest extends CertPolicyDaoBaseTest
{
	@Test
	public void testAddPolicyToGroup_associatePolicyAndGroup_assertAssociationAdded()
	{
		final CertPolicy policy = new CertPolicy();
		policy.setPolicyName("Test PolicY");
		policy.setLexicon(PolicyLexicon.XML);
		policy.setPolicyData(new byte[] {1,2,3});
		
		polDao.addPolicy(policy);
		
		final CertPolicyGroup group = new CertPolicyGroup();
		group.setPolicyGroupName("Test Group");

		polDao.addPolicyGroup(group);
		
		polDao.addPolicyUseToGroup(group.getId(), policy.getId(), CertPolicyUse.PUBLIC_RESOLVER, true, false);
		
		final CertPolicyGroup assocGroup = polDao.getPolicyGroupById(group.getId());
		assertEquals(1, assocGroup.getCertPolicyGroupReltn().size());
		CertPolicyGroupReltn reltn = assocGroup.getCertPolicyGroupReltn().iterator().next();
		
		assertEquals(policy.getId(), reltn.getCertPolicy().getId());
		assertEquals(group.getId(), reltn.getCertPolicyGroup().getId());
		assertEquals(CertPolicyUse.PUBLIC_RESOLVER, reltn.getPolicyUse());
		assertTrue(reltn.isIncoming());
		assertFalse(reltn.isOutgoing());
	}
	
	@Test
	public void testAddPolicyToGroup_unknownGroup_assertException()
	{
		boolean exceptionOccured = false;

		
		try
		{
			polDao.addPolicyUseToGroup(1234, 5678, CertPolicyUse.PRIVATE_RESOLVER, true, true);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	
	}
	
	@Test
	public void testAddPolicyToGroup_unknownTrustBundle_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		
		final CertPolicyDaoImpl certDao = new CertPolicyDaoImpl()
		{
			@Override
			public CertPolicyGroup getPolicyGroupById(long id) throws ConfigurationStoreException 
			{
				return new CertPolicyGroup();
			}
		};
		
		
		final Query query = mock(Query.class);
		doThrow(new NoResultException()).when(query).getSingleResult();
		when(mgr.createQuery("SELECT cp from CertPolicy cp WHERE cp.id = ?1")).thenReturn(query);
		
		certDao.setEntityManager(mgr);
		
		final CertPolicyDaoImpl spyDao = spy(certDao);
		
		try
		{
			spyDao.addPolicyUseToGroup(1234L, 5678L, CertPolicyUse.PRIVATE_RESOLVER, true, true);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(spyDao, times(1)).getPolicyGroupById(1234L);
		verify(spyDao, times(1)).getPolicyById(5678L);		
		verify(mgr, never()).persist((CertPolicyGroupReltn)any());				
	}	
	
	@Test
	public void testAddPolicyToGroup_errorInAdd_assertException()
	{
		boolean exceptionOccured = false;
		final EntityManager mgr = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(mgr).persist((CertPolicyGroupReltn)any());
		
		final CertPolicyDaoImpl certDao = new CertPolicyDaoImpl()
		{
			@Override
			public CertPolicy getPolicyById(long id) throws ConfigurationStoreException 
			{
				return new CertPolicy();
			}
			
			@Override
			public CertPolicyGroup getPolicyGroupById(long id) throws ConfigurationStoreException 
			{
				return new CertPolicyGroup();
			}
		};
		
		certDao.setEntityManager(mgr);
		
		final CertPolicyDaoImpl spyDao = spy(certDao);
		
		try
		{
			spyDao.addPolicyUseToGroup(1234L, 5678L, CertPolicyUse.PRIVATE_RESOLVER, true, true);
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		verify(spyDao, times(1)).getPolicyGroupById(1234);
		verify(spyDao, times(1)).getPolicyById(5678);	
		verify(mgr, times(1)).persist((CertPolicyGroupReltn)any());			
	}	
	
	@Test
	public void testAddPolicyToGroup_noEntityManager_assertException()
	{

		final CertPolicyDaoImpl dao = new CertPolicyDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.addPolicyUseToGroup(1234L, 5678L, CertPolicyUse.PRIVATE_RESOLVER, true, true);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
}
