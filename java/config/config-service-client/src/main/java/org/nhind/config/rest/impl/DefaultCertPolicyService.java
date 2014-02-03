package org.nhind.config.rest.impl;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.rest.impl.requests.AddPolicyGroupRequest;
import org.nhind.config.rest.impl.requests.AddPolicyRequest;
import org.nhind.config.rest.impl.requests.AddPolicyUseToGroupRequest;
import org.nhind.config.rest.impl.requests.AssociatePolicyGroupToDomainRequest;
import org.nhind.config.rest.impl.requests.DeletePolicyGroupRequest;
import org.nhind.config.rest.impl.requests.DeletePolicyRequest;
import org.nhind.config.rest.impl.requests.DisassociatePolicyGroupFromDomainRequest;
import org.nhind.config.rest.impl.requests.DisassociatePolicyGroupFromDomainsRequest;
import org.nhind.config.rest.impl.requests.DisassociatePolicyGroupsFromDomainRequest;
import org.nhind.config.rest.impl.requests.GetPoliciesRequest;
import org.nhind.config.rest.impl.requests.GetPolicyByNameRequest;
import org.nhind.config.rest.impl.requests.GetPolicyGroupDomainReltnsRequest;
import org.nhind.config.rest.impl.requests.GetPolicyGroupRequest;
import org.nhind.config.rest.impl.requests.GetPolicyGroupsByDomainRequest;
import org.nhind.config.rest.impl.requests.GetPolicyGroupsRequest;
import org.nhind.config.rest.impl.requests.RemovePolicyUseFromGroupRequest;
import org.nhind.config.rest.impl.requests.UpdatePolicyGroupRequest;
import org.nhind.config.rest.impl.requests.UpdatePolicyRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;

public class DefaultCertPolicyService extends AbstractSecuredService implements CertPolicyService
{
    public DefaultCertPolicyService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Collection<CertPolicy> getPolicies() throws ServiceException 
	{
		return callWithRetry(new GetPoliciesRequest(httpClient, serviceURL, jsonMapper, securityManager));			
	}

	@Override
	public CertPolicy getPolicyByName(String policyName) throws ServiceException 
	{
		final Collection<CertPolicy> policies =  callWithRetry(new GetPolicyByNameRequest(httpClient, serviceURL, jsonMapper, securityManager, policyName));	
		
		return (policies.isEmpty()) ? null : policies.iterator().next();
	}

	@Override
	public void addPolicy(CertPolicy policy) throws ServiceException 
	{
		callWithRetry(new AddPolicyRequest(httpClient, serviceURL, jsonMapper, securityManager, policy));		
	}

	@Override
	public void deletePolicy(String policyName) throws ServiceException 
	{
		callWithRetry(new DeletePolicyRequest(httpClient, serviceURL, jsonMapper, securityManager, policyName));	
	}

	@Override
	public void updatePolicy(String policyName, CertPolicy policyAttributes) throws ServiceException 
	{
		callWithRetry(new UpdatePolicyRequest(httpClient, serviceURL, jsonMapper, securityManager, policyName, policyAttributes));	
	}

	@Override
	public Collection<CertPolicyGroup> getPolicyGroups() throws ServiceException 
	{
		return callWithRetry(new GetPolicyGroupsRequest(httpClient, serviceURL, jsonMapper, securityManager));	
	}

	@Override
	public CertPolicyGroup getPolicyGroup(String groupName) throws ServiceException 
	{
		final Collection<CertPolicyGroup> groups =  callWithRetry(new GetPolicyGroupRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName));	
		
		return (groups.isEmpty()) ? null : groups.iterator().next();		
	}

	@Override
	public void addPolicyGroup(CertPolicyGroup group) throws ServiceException 
	{
		callWithRetry(new AddPolicyGroupRequest(httpClient, serviceURL, jsonMapper, securityManager, group));
	}

	@Override
	public void deletePolicyGroup(String groupName) throws ServiceException 
	{
		callWithRetry(new DeletePolicyGroupRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName));
	}

	@Override
	public void updatePolicyGroup(String groupName, String newGroupName) throws ServiceException  
	{
		callWithRetry(new UpdatePolicyGroupRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName, newGroupName));	
	}

	@Override
	public void addPolicyUseToGroup(String groupName, CertPolicyGroupUse use) throws ServiceException 
	{
		callWithRetry(new AddPolicyUseToGroupRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName, use));	
	}

	@Override
	public void removePolicyUseFromGroup(String groupName, CertPolicyGroupUse use) throws ServiceException 
	{
		callWithRetry(new RemovePolicyUseFromGroupRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName, use));	
	}

	@Override
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ServiceException 
	{
		return callWithRetry(new GetPolicyGroupDomainReltnsRequest(httpClient, serviceURL, jsonMapper, securityManager));	
		
	}

	@Override
	public Collection<CertPolicyGroup> getPolicyGroupsByDomain(String domainName) throws ServiceException 
	{
		return callWithRetry(new GetPolicyGroupsByDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, domainName));	
	}

	@Override
	public void associatePolicyGroupToDomain(String groupName, String domainName) throws ServiceException 
	{
		callWithRetry(new AssociatePolicyGroupToDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName, domainName));	
	}

	@Override
	public void disassociatePolicyGroupFromDomain(String groupName, String domainName) throws ServiceException 
	{
		callWithRetry(new DisassociatePolicyGroupFromDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName, domainName));	
	}

	@Override
	public void disassociatePolicyGroupsFromDomain(String domainName) throws ServiceException 
	{
		callWithRetry(new DisassociatePolicyGroupsFromDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, domainName));	
	}

	@Override
	public void disassociatePolicyGroupFromDomains(String groupName) throws ServiceException 
	{
		callWithRetry(new DisassociatePolicyGroupFromDomainsRequest(httpClient, serviceURL, jsonMapper, securityManager, groupName));	
	}
}
