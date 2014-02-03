package org.nhind.config.rest;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;

public interface CertPolicyService 
{
	public Collection<CertPolicy> getPolicies() throws ServiceException;
	
	public CertPolicy getPolicyByName(String policyName) throws ServiceException;
	
	public void addPolicy(CertPolicy policy) throws ServiceException;
	
	public void deletePolicy(String policyName) throws ServiceException;
	
	public void updatePolicy(String policyName, CertPolicy policyAttributes) throws ServiceException;
	
	public Collection<CertPolicyGroup> getPolicyGroups() throws ServiceException;
	
	public CertPolicyGroup getPolicyGroup(String groupName) throws ServiceException;
	
	public void addPolicyGroup(CertPolicyGroup group) throws ServiceException;
	
	public void deletePolicyGroup(String groupName) throws ServiceException;
	
	public void updatePolicyGroup(String groupName, String newGroupName) throws ServiceException;
	
	public void addPolicyUseToGroup(String groupName, CertPolicyGroupUse use) throws ServiceException;
	
	public void removePolicyUseFromGroup(String groupName, CertPolicyGroupUse use) throws ServiceException;
	
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ServiceException;
	
	public Collection<CertPolicyGroup> getPolicyGroupsByDomain(String domainName) throws ServiceException;
	
	public void associatePolicyGroupToDomain(String groupName, String domainName) throws ServiceException;	
	
	public void disassociatePolicyGroupFromDomain(String groupName, String domainName) throws ServiceException;	
	
	public void disassociatePolicyGroupsFromDomain(String domainName) throws ServiceException;	
	
	public void disassociatePolicyGroupFromDomains(String groupName) throws ServiceException;		
}
