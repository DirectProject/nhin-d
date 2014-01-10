package org.nhindirect.config.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.nhindirect.config.service.impl.CertificatePolicyServiceImpl;
import org.nhindirect.config.store.CertPolicy;
import org.nhindirect.config.store.CertPolicyGroup;
import org.nhindirect.config.store.CertPolicyUse;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.policy.PolicyLexicon;

public class CertificatePolicyServiceTest extends TestCase 
{
	private CertificatePolicyServiceImpl impl;
	private CertPolicyDao dao;
	
	@Override
	public void setUp()
	{
		dao = mock(CertPolicyDao.class);
		
		impl = new CertificatePolicyServiceImpl();
		impl.setDao(dao);
	}
	
	public void testGetPolicies() throws Exception
	{
		impl.getPolicies();
		
		verify(dao, times(1)).getPolicies();
	}
	
	public void testGetPolicyByName() throws Exception
	{
		impl.getPolicyByName("1234");
		
		verify(dao, times(1)).getPolicyByName("1234");
	}
	
	public void testGetPolicyById() throws Exception
	{
		impl.getPolicyById(1234);
		
		verify(dao, times(1)).getPolicyById(1234);	
	}
	
	public void testAddPolicy() throws Exception
	{
		final CertPolicy policy = new CertPolicy();
		
		impl.addPolicy(policy);
		
		verify(dao, times(1)).addPolicy(policy);	
	}
	
	public void testDeletePolicies() throws Exception
	{
		final long[] policyIds = new long[] {1,2,3};
		
		impl.deletePolicies(policyIds);
		
		verify(dao, times(1)).deletePolicies(policyIds);	
	}	
	
	public void testUpdatePolicyAttributes() throws Exception
	{
		final byte[] data = new byte[] {1,2,3};
		
		impl.updatePolicyAttributes(1234, "Test Policy", PolicyLexicon.JAVA_SER, data);
		
		verify(dao, times(1)).updatePolicyAttributes(1234, "Test Policy", PolicyLexicon.JAVA_SER, data);
	}	
	
	public void testGetPolicyGroups() throws Exception
	{
		impl.getPolicyGroups();
		
		verify(dao, times(1)).getPolicyGroups();
	}
	
	public void testGetPolicyGroupByName() throws Exception
	{
		impl.getPolicyGroupByName("1234");
		
		verify(dao, times(1)).getPolicyGroupByName("1234");
	}
	
	public void testGetPolicyGroupById() throws Exception
	{
		impl.getPolicyGroupById(1234);
		
		verify(dao, times(1)).getPolicyGroupById(1234);
	}
	
	public void testAddPolicyGruop() throws Exception
	{
		final CertPolicyGroup group = new CertPolicyGroup();
		
		impl.addPolicyGroup(group);
		
		verify(dao, times(1)).addPolicyGroup(group);
	}	
	
	public void testDeletePolicGroups() throws Exception
	{
		final long[] policyGroupIds = new long[] {1,2,3};
		
		impl.deletePolicyGroups(policyGroupIds);
		
		verify(dao, times(1)).deletePolicyGroups(policyGroupIds);	
	}	
	
	public void testUpdateGroupAttributes() throws Exception
	{	
		impl.updateGroupAttributes(1234, "Group");
		
		verify(dao, times(1)).updateGroupAttributes(1234, "Group");	
	}	
	
	public void testAddPolicyUseToGroup() throws Exception
	{
		impl.addPolicyUseToGroup(1234, 5678, CertPolicyUse.PRIVATE_RESOLVER, true, false);
		
		verify(dao, times(1)).addPolicyUseToGroup(1234, 5678, CertPolicyUse.PRIVATE_RESOLVER, true, false);	
	}
	
	public void testRemovePolicyUseFromGroup() throws Exception
	{
		impl.removePolicyUseFromGroup(1234);
		
		verify(dao, times(1)).removePolicyUseFromGroup(1234);
	}	
	
	public void testAssociatePolicyGroupToDomain() throws Exception
	{
		impl.associatePolicyGroupToDomain(1234, 5678);
		
		verify(dao, times(1)).associatePolicyGroupToDomain(1234, 5678);
	}
	
	public void testDisassociatePolicyGroupFromDomain() throws Exception
	{
		impl.disassociatePolicyGroupFromDomain(1234, 5678);
		
		verify(dao, times(1)).disassociatePolicyGroupFromDomain(1234, 5678);
	}
	
	public void testDisassociatePolicyGroupsFromDomain() throws Exception
	{
		impl.disassociatePolicyGroupsFromDomain(1234);
		
		verify(dao, times(1)).disassociatePolicyGroupsFromDomain(1234);
	}
	
	public void testDisassociatePolicyGroupFromDomains() throws Exception
	{
		impl.disassociatePolicyGroupFromDomains(1234);
		
		verify(dao, times(1)).disassociatePolicyGroupFromDomains(1234);
	}
	
	public void testGetPolicyGroupDomainReltns() throws Exception
	{
		impl.getPolicyGroupDomainReltns();
		
		verify(dao, times(1)).getPolicyGroupDomainReltns();
	}
	
	public void testGetPolicyGroupsByDomain() throws Exception
	{
		impl.getPolicyGroupsByDomain(1234);
		
		verify(dao, times(1)).getPolicyGroupsByDomain(1234);
	}
}
