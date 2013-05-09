package org.nhindirect.config.service.impl;

import java.util.Collection;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.CertificatePolicyService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.store.CertPolicy;
import org.nhindirect.config.store.CertPolicyGroup;
import org.nhindirect.config.store.CertPolicyGroupDomainReltn;
import org.nhindirect.config.store.CertPolicyUse;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.policy.PolicyLexicon;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.nhindirect.config.service.CertificatePolicyServiceImpl")
public class CertificatePolicyServiceImpl implements CertificatePolicyService
{
    private static final Log log = LogFactory.getLog(CertificatePolicyServiceImpl.class);
    
    private CertPolicyDao dao;
    
    /**
	 * Initialization method.
	 */
    ///CLOVER:OFF
    public void init() 
    {
        log.info("CertificatePolicyServiceImpl initialized");
    }
    ///CLOVER:ON
    
	/**
     * Set the value of the CertPolicyDao object.
     * 
     * @param dao
     *            the value of the CertPolicyDao object.
     */
    @Autowired
    public void setDao(CertPolicyDao dao) 
    {
        this.dao = dao;
    }

    /**
     * Return the value of the CertPolicyDao object.
     * 
     * @return the value of the CertPolicyDao object.
     */
    ///CLOVER:OFF
    public CertPolicyDao getDao() 
    {
        return dao;
    }
    ///CLOVER:ON

	@Override
	public Collection<CertPolicy> getPolicies() throws ConfigurationServiceException 
	{
		return dao.getPolicies();
	}

	@Override
	public CertPolicy getPolicyByName(String policyName) throws ConfigurationServiceException 
	{
		return dao.getPolicyByName(policyName);
	}

	@Override
	public CertPolicy getPolicyById(long id) throws ConfigurationServiceException 
	{
		return dao.getPolicyById(id);
	}

	@Override
	public void addPolicy(CertPolicy policy) throws ConfigurationServiceException 
	{
		dao.addPolicy(policy);
	}

	@Override
	public void deletePolicies(long[] policyIds) throws ConfigurationServiceException 
	{
		dao.deletePolicies(policyIds);
	}

	@Override
	public void updatePolicyAttributes(long id, String policyName,
			PolicyLexicon lexicon, byte[] policyData) throws ConfigurationServiceException 
	{
		dao.updatePolicyAttributes(id, policyName, lexicon, policyData);
	}

	@Override
	public Collection<CertPolicyGroup> getPolicyGroups() throws ConfigurationServiceException 
	{
		return dao.getPolicyGroups();
	}

	@Override
	public CertPolicyGroup getPolicyGroupByName(String policyGroupName) throws ConfigurationServiceException 
	{
		return dao.getPolicyGroupByName(policyGroupName);
	}

	@Override
	public CertPolicyGroup getPolicyGroupById(long id) throws ConfigurationServiceException 
	{
		return dao.getPolicyGroupById(id);
	}

	@Override
	public void addPolicyGroup(CertPolicyGroup group) throws ConfigurationServiceException 
	{
		dao.addPolicyGroup(group);
	}

	@Override
	public void deletePolicyGroups(long[] groupIds) throws ConfigurationServiceException 
	{
		dao.deletePolicyGroups(groupIds);
	}

	@Override
	public void updateGroupAttributes(long id, String groupName) throws ConfigurationServiceException 
	{	
		dao.updateGroupAttributes(id, groupName);
	}

	@Override
	public void addPolicyUseToGroup(long groupId, long policyId, CertPolicyUse policyUse,
			boolean incoming, boolean outgoing) throws ConfigurationServiceException 
	{	
		dao.addPolicyUseToGroup(groupId, policyId, policyUse, incoming, outgoing);
	}

	@Override
	public void removePolicyUseFromGroup(long policyGroupReltnId) throws ConfigurationServiceException 
	{
		dao.removePolicyUseFromGroup(policyGroupReltnId);
	}

	@Override
	public void associatePolicyGroupToDomain(long domainId,long policyGroupId) throws ConfigurationServiceException 
	{	
		dao.associatePolicyGroupToDomain(domainId, policyGroupId);
	}

	@Override
	public void disassociatePolicyGroupFromDomain(long domainId, long policyGroupId) throws ConfigurationServiceException 
	{
		dao.disassociatePolicyGroupFromDomain(domainId, policyGroupId);
	}

	@Override
	public void disassociatePolicyGroupsFromDomain(long domainId) throws ConfigurationServiceException 
	{	
		dao.disassociatePolicyGroupsFromDomain(domainId);
	}

	@Override
	public void disassociatePolicyGroupFromDomains(long policyGroupId) throws ConfigurationServiceException 
	{
		dao.disassociatePolicyGroupFromDomains(policyGroupId);
	}

	@Override
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ConfigurationServiceException
	{
		return dao.getPolicyGroupDomainReltns();
	}
	
	@Override
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupsByDomain(long domainId) throws ConfigurationServiceException 
	{
		return dao.getPolicyGroupsByDomain(domainId);
	}   
}
