package org.nhindirect.config.service;

import java.util.Collection;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.nhindirect.config.store.CertPolicy;
import org.nhindirect.config.store.CertPolicyGroup;
import org.nhindirect.config.store.CertPolicyGroupDomainReltn;
import org.nhindirect.config.store.CertPolicyUse;
import org.nhindirect.policy.PolicyLexicon;


public interface CertificatePolicyService 
{
    @WebMethod(operationName = "getPolicies", action = "urn:GetPolicies")
	public Collection<CertPolicy> getPolicies() throws ConfigurationServiceException;
	
    @WebMethod(operationName = "getPolicyByName", action = "urn:GetPolicyByName")
	public CertPolicy getPolicyByName(@WebParam(name = "policyName") String policyName) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "getPolicyById", action = "urn:GetPolicyById")
	public CertPolicy getPolicyById(@WebParam(name = "policyId") long id) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "addPolicy", action = "urn:AddPolicy")
	public void addPolicy(@WebParam(name = "policy") CertPolicy policy) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "deletePolicies", action = "urn:DeletePolicies")
	public void deletePolicies(@WebParam(name = "policyIds") long[] policyIds) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "updatePolicyAttributes", action = "urn:UpdatePolicyAttributes")
	public void updatePolicyAttributes(@WebParam(name = "policyId") long id, @WebParam(name = "policyName") String policyName, 
			@WebParam(name = "policyLexicon") PolicyLexicon lexicon,  @WebParam(name = "policyData") byte[] policyData) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "getPolicyGroups", action = "urn:GetPolicyGroups")
	public Collection<CertPolicyGroup> getPolicyGroups() throws ConfigurationServiceException;
	
    @WebMethod(operationName = "getPolicyGroupByName", action = "urn:GetPolicyGroupByName")
	public CertPolicyGroup getPolicyGroupByName(@WebParam(name = "policyGroupName")  String policyGroupName) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "getPolicyGroupById", action = "urn:GetPolicyGroupById")
	public CertPolicyGroup getPolicyGroupById(@WebParam(name = "policyGroupId") long id) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "addPolicyGroup", action = "urn:AddPolicyGroup")
	public void addPolicyGroup(@WebParam(name = "policyGroup") CertPolicyGroup group) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "deletePolicyGroups", action = "urn:DeletePolicyGroups")
	public void deletePolicyGroups(@WebParam(name = "policyGroupIds") long[] groupIds) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "updateGroupAttributes", action = "urn:UpdateGroupAttributes")
	public void updateGroupAttributes(@WebParam(name = "policyGroupId") long id, 
			@WebParam(name = "policyGroupName")String groupName) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "addPolicyUseToGroup", action = "urn:AddPolicyUseToGroup")
	public void addPolicyUseToGroup(@WebParam(name = "policyGroupId") long groupId, @WebParam(name = "policyId") long policyId, 
			@WebParam(name = "policyUse") CertPolicyUse policyUse, 
			@WebParam(name = "incoming") boolean incoming, @WebParam(name = "outgoing") boolean outgoing) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "removePolicyUseFromGroup", action = "urn:RemovePolicyUseFromGroup")
	public void removePolicyUseFromGroup(@WebParam(name = "policyGroupReltnId") long policyGroupReltnId) throws ConfigurationServiceException;
	
    @WebMethod(operationName = "associatePolicyGroupToDomain", action = "urn:AssociatePolicyGroupToDomain")
	public void associatePolicyGroupToDomain(@WebParam(name = "domainId") long domainId, 
			@WebParam(name = "policyGroupId") long policyGroupId) throws ConfigurationServiceException;

    @WebMethod(operationName = "disassociatePolicyGroupFromDomain", action = "urn:DisassociatePolicyGroupFromDomain")
	public void disassociatePolicyGroupFromDomain(@WebParam(name = "domainId") long domainId, 
			@WebParam(name = "policyGroupId") long policyGroupId) throws ConfigurationServiceException;	

    @WebMethod(operationName = "disassociatePolicyGroupsFromDomain", action = "urn:DisassociatePolicyGroupsFromDomain")
	public void disassociatePolicyGroupsFromDomain(@WebParam(name = "domainId") long domainId) throws ConfigurationServiceException;	

    @WebMethod(operationName = "disassociatePolicyGroupFromDomains", action = "urn:DisassociatePolicyGroupFromDomains")
	public void disassociatePolicyGroupFromDomains(@WebParam(name = "policyGroupId") long policyGroupId) throws ConfigurationServiceException;		

    @WebMethod(operationName = "getPolicyGroupDomainReltns", action = "urn:GetPolicyGroupDomainReltns")
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ConfigurationServiceException;	
    
    @WebMethod(operationName = "getPolicyGroupsByDomain", action = "urn:GetPolicyGroupsByDomain")
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupsByDomain(
			@WebParam(name = "domainId") long domainId) throws ConfigurationServiceException;	
}
