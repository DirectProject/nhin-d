package org.nhindirect.config.store.dao;

import java.util.Collection;

import org.nhindirect.config.store.CertPolicy;
import org.nhindirect.config.store.CertPolicyGroup;
import org.nhindirect.config.store.CertPolicyUse;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.CertPolicyGroupDomainReltn;
import org.nhindirect.policy.PolicyLexicon;

public interface CertPolicyDao 
{
	public Collection<CertPolicy> getPolicies() throws ConfigurationStoreException;
	
	public CertPolicy getPolicyByName(String policyName) throws ConfigurationStoreException;
	
	public CertPolicy getPolicyById(long id) throws ConfigurationStoreException;
	
	public void addPolicy(CertPolicy policy) throws ConfigurationStoreException;
	
	public void deletePolicies(long[] policyIds) throws ConfigurationStoreException;
	
	public void updatePolicyAttributes(long id, String policyName, PolicyLexicon lexicon, 
			byte[] policyData) throws ConfigurationStoreException;
	
	public Collection<CertPolicyGroup> getPolicyGroups() throws ConfigurationStoreException;
	
	public CertPolicyGroup getPolicyGroupByName(String policyGroupName) throws ConfigurationStoreException;
	
	public CertPolicyGroup getPolicyGroupById(long id) throws ConfigurationStoreException;
	
	public void addPolicyGroup(CertPolicyGroup group) throws ConfigurationStoreException;
	
	public void deletePolicyGroups(long[] groupIds) throws ConfigurationStoreException;
	
	public void updateGroupAttributes(long id, String groupName) throws ConfigurationStoreException;
	
	public void addPolicyUseToGroup(long groupId, long policyId, CertPolicyUse policyUse, 
			boolean incoming, boolean outgoing) throws ConfigurationStoreException;
	
	public void removePolicyUseFromGroup(long policyGroupReltnId) throws ConfigurationStoreException;
	
	public void associatePolicyGroupToDomain(long domainId, long policyGroupId) throws ConfigurationStoreException;

	public void disassociatePolicyGroupFromDomain(long domainId, long policyGroupId) throws ConfigurationStoreException;	

	public void disassociatePolicyGroupsFromDomain(long domainId) throws ConfigurationStoreException;	

	public void disassociatePolicyGroupFromDomains(long policyGroupId) throws ConfigurationStoreException;		

	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ConfigurationStoreException;	
	
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupsByDomain(long domainId) throws ConfigurationStoreException;	
}
