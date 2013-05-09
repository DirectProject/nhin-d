package org.nhindirect.config.store.dao.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.store.CertPolicy;
import org.nhindirect.config.store.CertPolicyGroup;
import org.nhindirect.config.store.CertPolicyGroupReltn;
import org.nhindirect.config.store.CertPolicyUse;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.CertPolicyGroupDomainReltn;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.policy.PolicyLexicon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CertPolicyDaoImpl implements CertPolicyDao
{
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    protected DomainDao domainDao;	
    
    private static final Log log = LogFactory.getLog(CertPolicyDaoImpl.class);
    
    public CertPolicyDaoImpl()
    {
    	
    }
    
    @Autowired
	public void setDomainDao(DomainDao domainDao)
	{
		this.domainDao = domainDao;
	}
	
	/**
	 * Validate that we have a connection to the DAO
	 */
	protected void validateState() throws ConfigurationStoreException
	{	
    	if (entityManager == null)
    		throw new IllegalStateException("entityManger has not been initialized");
	}
	
	/**
	 * Sets the entity manager for access to the underlying data store medium.
	 * @param entityManager The entity manager.
	 */
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	@Override
    @Transactional(readOnly = true)
	public Collection<CertPolicy> getPolicies() throws ConfigurationStoreException 
	{
		validateState();
		
        try
        {
	        final Query select = entityManager.createQuery("SELECT cp from CertPolicy cp");
	        
	        final Collection<CertPolicy> rs = select.getResultList();
	        if (rs.size() == 0)
	        	return Collections.emptyList();
	        
	        return rs;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute certificate policy DAO query.", e);
    	}
	}

	@Override
    @Transactional(readOnly = true)	
	public CertPolicy getPolicyByName(String policyName) throws ConfigurationStoreException 
	{
		validateState();
        
        try
        {
            final Query select = entityManager.createQuery("SELECT cp from CertPolicy cp WHERE UPPER(cp.policyName) = ?1");
            select.setParameter(1, policyName.toUpperCase(Locale.getDefault()));
            
            final CertPolicy rs = (CertPolicy)select.getSingleResult();
            
	        return rs;
        }
        catch (NoResultException e)
        {
        	return null;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute certificate policy DAO query.", e);
    	}
	}

	@Override
	@Transactional(readOnly = true)	
	public CertPolicy getPolicyById(long id) throws ConfigurationStoreException 
	{
		validateState();
        
        try
        {
            final Query select = entityManager.createQuery("SELECT cp from CertPolicy cp WHERE cp.id = ?1");
            select.setParameter(1, id);
            
            final CertPolicy rs = (CertPolicy)select.getSingleResult();
            
	        return rs;
        }
        catch (NoResultException e)
        {
        	return null;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute certificate policy DAO query.", e);
    	}	
	}

	@Override
	@Transactional(readOnly = false)	
	public void addPolicy(CertPolicy policy) throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final CertPolicy existingPolicy = this.getPolicyByName(policy.getPolicyName());
			if (existingPolicy != null)
				throw new ConfigurationStoreException("Certificate policy " + policy.getPolicyName() + " already exists");
			
			policy.setCreateTime(Calendar.getInstance(Locale.getDefault()));
			
			entityManager.persist(policy);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to add certificate policy " + policy.getPolicyName(), e);
    	}
    	///CLOVER:ON
	}

	@Override
	@Transactional(readOnly = false)	
	public void deletePolicies(long[] policyIds) throws ConfigurationStoreException 
	{
		validateState();

        if (policyIds == null || policyIds.length == 0)
        	return;

        for (long id : policyIds) 
        {
        	try
        	{
        		final CertPolicy bundle = this.getPolicyById(id);

        		this.removePolicyUseFromGroups(id);
        		
        		entityManager.remove(bundle);
    	        entityManager.flush();
        	}
        	catch (ConfigurationStoreException e)
        	{
        		log.warn(e.getMessage(), e);
        	}
        	
        }
		
	}

	/**
	 * {@inheritDoc}
	 */
    @Transactional(readOnly = false)	
	public void removePolicyUseFromGroups(long policyId) throws ConfigurationStoreException
	{
		validateState();
		
		// make sure the trust bundle exists
		final CertPolicy policy = this.getPolicyById(policyId);
		if (policy == null)
			throw new ConfigurationStoreException("Certificate policy with id " + policyId + " does not exist");
		
		try
		{
			final Query delete = entityManager.createQuery("DELETE from CertPolicyGroupReltn cpr where cpr.certPolicy  = ?1");
	        
	        delete.setParameter(1, policy);
	        delete.executeUpdate();
	        
	        entityManager.flush();
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to remove policy id " + policyId + " from all groups" , e);
		}
	}
	
	@Override
	@Transactional(readOnly = false)	
	public void updatePolicyAttributes(long id, String policyName,
			PolicyLexicon lexicon, byte[] policyData) throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final CertPolicy existingPolicy = this.getPolicyById(id);
			if (existingPolicy == null)
				throw new ConfigurationStoreException("Policy does not exist");
			
			if (policyData != null && policyData.length > 0)
				existingPolicy.setPolicyData(policyData);
					
			
			if (policyName != null && !policyName.isEmpty())
				existingPolicy.setPolicyName(policyName);
	
			if (lexicon != null)
				existingPolicy.setLexicon(lexicon);			
			
			entityManager.persist(existingPolicy);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to update certificate policy attributes.", e);
    	}	
    	///CLOVER:ON
		
	}

	@Override
	@Transactional(readOnly = true)	
	public Collection<CertPolicyGroup> getPolicyGroups() throws ConfigurationStoreException 
	{
		validateState();
		
        try
        {
	        final Query select = entityManager.createQuery("SELECT cpg from CertPolicyGroup cpg");
	        
	        @SuppressWarnings("unchecked")
			final Collection<CertPolicyGroup> rs = select.getResultList();
	        if (rs.size() == 0)
	        	return Collections.emptyList();
	        
            // load relationships now as they were deferred by lazy loading 
            for (CertPolicyGroup group : rs)
            	group.getCertPolicyGroupReltn().size();
	        
	        return rs;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute certificate policy group DAO query.", e);
    	}
	}

	@Override
	@Transactional(readOnly = true)	
	public CertPolicyGroup getPolicyGroupByName(String policyGroupName) throws ConfigurationStoreException 
	{
		validateState();
        
        try
        {
            final Query select = entityManager.createQuery("SELECT cpg from CertPolicyGroup cpg WHERE UPPER(cpg.policyGroupName) = ?1");
            select.setParameter(1, policyGroupName.toUpperCase(Locale.getDefault()));
            
            final CertPolicyGroup rs = (CertPolicyGroup)select.getSingleResult();
            
            // load relationships now as they were deferred by lazy loading 
            rs.getCertPolicyGroupReltn().size();
            
	        return rs;
        }
        catch (NoResultException e)
        {
        	return null;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute certificate policy group DAO query.", e);
    	}
	}

	@Override
	@Transactional(readOnly = true)	
	public CertPolicyGroup getPolicyGroupById(long id) throws ConfigurationStoreException 
	{
		validateState();
        
        try
        {
            final Query select = entityManager.createQuery("SELECT cpg from CertPolicyGroup cpg WHERE cpg.id = ?1");
            select.setParameter(1, id);
            
            final CertPolicyGroup rs = (CertPolicyGroup)select.getSingleResult();
            
            // load relationships now as they were deferred by lazy loading 
            rs.getCertPolicyGroupReltn().size();
            
	        return rs;
        }
        catch (NoResultException e)
        {
        	return null;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute certificate policy group DAO query.", e);
    	}
	}

	@Override
	@Transactional(readOnly = false)	
	public void addPolicyGroup(CertPolicyGroup group) throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final CertPolicyGroup existingPolicyGroup = this.getPolicyGroupByName(group.getPolicyGroupName());
			if (existingPolicyGroup != null)
				throw new ConfigurationStoreException("Certificate policy group " + group.getPolicyGroupName() + " already exists");
			
			group.setCreateTime(Calendar.getInstance(Locale.getDefault()));
			
			entityManager.persist(group);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to add certificate policy group " + group.getPolicyGroupName(), e);
    	}
    	///CLOVER:ON
		
	}

	@Override
	@Transactional(readOnly = false)	
	public void deletePolicyGroups(long[] groupIds) throws ConfigurationStoreException 
	{
		validateState();

        if (groupIds == null || groupIds.length == 0)
        	return;

        for (long id : groupIds) 
        {
        	try
        	{
        		final CertPolicyGroup bundle = this.getPolicyGroupById(id);

        		entityManager.remove(bundle);
    	        entityManager.flush();
        	}
        	catch (ConfigurationStoreException e)
        	{
        		log.warn(e.getMessage(), e);
        	}
        }
		
	}

	@Override
	@Transactional(readOnly = false)	
	public void updateGroupAttributes(long id, String groupName) throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final CertPolicyGroup existingPolicyGroup = this.getPolicyGroupById(id);
			if (existingPolicyGroup == null)
				throw new ConfigurationStoreException("Policy group does not exist");
					
			
			if (groupName != null && !groupName.isEmpty())
				existingPolicyGroup.setPolicyGroupName(groupName);		
			
			entityManager.persist(existingPolicyGroup);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to update certificate policy attributes.", e);
    	}	
    	///CLOVER:ON
		
	}

	@Override
	@Transactional(readOnly = false)	
	public void addPolicyUseToGroup(long groupId, long policyId, 
			CertPolicyUse policyUse, boolean incoming, boolean outgoing) throws ConfigurationStoreException 
	{
		validateState();
		
		
		// make sure the policy exists
		final CertPolicyGroup policyGroup = this.getPolicyGroupById(groupId);
		if (policyGroup == null)
			throw new ConfigurationStoreException("Policy group with id " + groupId + " does not exist");
		
		// make sure the policy exists
		final CertPolicy policy = this.getPolicyById(policyId);
		if (policy == null)
			throw new ConfigurationStoreException("Policy with id " + policyId + " does not exist");
		
		try
		{
			final CertPolicyGroupReltn reltn = new CertPolicyGroupReltn();
			reltn.setCertPolicy(policy);
			reltn.setCertPolicyGroup(policyGroup);
			reltn.setPolicyUse(policyUse);
			reltn.setIncoming(incoming);
			reltn.setOutgoing(outgoing);
			
			policyGroup.getCertPolicyGroupReltn().add(reltn);
			entityManager.persist(policyGroup);
			
			entityManager.flush();
			
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to add policy use to policy group.", e);
		}
	}

	@Override
	@Transactional(readOnly = false)	
	public void removePolicyUseFromGroup(long policyGroupReltnId) throws ConfigurationStoreException
	{
		validateState();
        
        try
        {
            final Query select = entityManager.createQuery("DELETE from CertPolicyGroupReltn cpr WHERE cpr.id = ?1");
            select.setParameter(1, policyGroupReltnId);

            select.executeUpdate();
			entityManager.flush();
        }
        catch (NoResultException e)
        {
        	throw new ConfigurationStoreException("Policy group reltn with id " + policyGroupReltnId + " does not exist");
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to remove policy use from policy group.", e);
    	}	
	}

	@Override
    @Transactional(readOnly = false)
	public void associatePolicyGroupToDomain(long domainId, long policyGroupId)
			throws ConfigurationStoreException 
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		// make sure the policy group exists
		final CertPolicyGroup policyGroup = this.getPolicyGroupById(policyGroupId);
		if (policyGroup == null)
			throw new ConfigurationStoreException("Policy group with id " + policyGroup + " does not exist");
		
		try
		{
			final CertPolicyGroupDomainReltn policyGroupDomainAssoc = new CertPolicyGroupDomainReltn();
			policyGroupDomainAssoc.setDomain(domain);
			policyGroupDomainAssoc.setCertPolicyGroup(policyGroup);
			
			entityManager.persist(policyGroupDomainAssoc);
			entityManager.flush();
			
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to associate policy group to domain.", e);
		}
	}

	@Override
    @Transactional(readOnly = false)	
	public void disassociatePolicyGroupFromDomain(long domainId, long policyGroupId) throws ConfigurationStoreException 
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		// make sure the policy group exists
		final CertPolicyGroup policyGroup = this.getPolicyGroupById(policyGroupId);
		if (policyGroup == null)
			throw new ConfigurationStoreException("Policy group with id " + policyGroup + " does not exist");
		
		try
		{
			final Query select = entityManager.createQuery("SELECT cpr from CertPolicyGroupDomainReltn cpr where cpr.domain  = ?1 " +
	        		" and cpr.certPolicyGroup = ?2 ");
	        
	        select.setParameter(1, domain);
	        select.setParameter(2, policyGroup);
            
	        final CertPolicyGroupDomainReltn reltn = (CertPolicyGroupDomainReltn)select.getSingleResult();
	        
	        entityManager.remove(reltn);
	        entityManager.flush();
		}
		catch (NoResultException e)
		{
			throw new ConfigurationStoreException("Association between domain id " + domainId + " and policy group id " 
					 + policyGroupId + " does not exist", e);
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to delete policy group from domain relation.", e);
		}	
	}

	@Override
    @Transactional(readOnly = false)	
	public void disassociatePolicyGroupsFromDomain(long domainId) throws ConfigurationStoreException 
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		try
		{
			final Query delete = entityManager.createQuery("DELETE from CertPolicyGroupDomainReltn cpr where cpr.domain  = ?1");
	        
	        delete.setParameter(1, domain);
	        delete.executeUpdate();
	        
	        entityManager.flush();
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to dissaccociate group policies from domain id ." + domainId, e);
		}
		
	}

	@Override
    @Transactional(readOnly = false)	
	public void disassociatePolicyGroupFromDomains(long policyGroupId) throws ConfigurationStoreException 
	{
		validateState();
		
		// make sure the policy group exists
		final CertPolicyGroup policyGroup = this.getPolicyGroupById(policyGroupId);
		if (policyGroup == null)
			throw new ConfigurationStoreException("Policy group with id " + policyGroupId + " does not exist");
		
		try
		{
			final Query delete = entityManager.createQuery("DELETE from CertPolicyGroupDomainReltn cpr where cpr.certPolicyGroup  = ?1");
	        
	        delete.setParameter(1, policyGroup);
	        delete.executeUpdate();
	        
	        entityManager.flush();
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to dissaccociate domains from policy group id ." + policyGroupId, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
    @Transactional(readOnly = true)	
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ConfigurationStoreException
	{
		validateState();
		
        try
        {
	        final Query select = entityManager.createQuery("SELECT cpdr from CertPolicyGroupDomainReltn cpdr");
	        
	        final Collection<CertPolicyGroupDomainReltn> rs = select.getResultList();
	        if (rs.size() == 0)
	        	return Collections.emptyList();
	        
	        for (CertPolicyGroupDomainReltn reltn : rs)
	        {
                if (!reltn.getCertPolicyGroup().getCertPolicyGroupReltn().isEmpty())
                	for (CertPolicyGroupReltn groupReltn : reltn.getCertPolicyGroup().getCertPolicyGroupReltn())
                		groupReltn.getCertPolicy().getPolicyData();
	        }
	        
	        return rs;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute certificate policy DAO query.", e);
    	}
	}
	
	@SuppressWarnings("unchecked")
	@Override
    @Transactional(readOnly = true)	
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupsByDomain(long domainId) throws ConfigurationStoreException 
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		Collection<CertPolicyGroupDomainReltn> retVal = null;
        try
        {
	        final Query select = entityManager.createQuery("SELECT cpr from CertPolicyGroupDomainReltn cpr where cpr.domain = ?1");
	        select.setParameter(1, domain);
	        
	        retVal = (Collection<CertPolicyGroupDomainReltn>)select.getResultList();
	        if (retVal.size() == 0)
	        	return Collections.emptyList();
	        
	        for (CertPolicyGroupDomainReltn reltn : retVal)
	        {
                if (!reltn.getCertPolicyGroup().getCertPolicyGroupReltn().isEmpty())
                	for (CertPolicyGroupReltn groupReltn : reltn.getCertPolicyGroup().getCertPolicyGroupReltn())
                		groupReltn.getCertPolicy().getPolicyData();
	        }
	       
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute policy group to domain relation DAO query.", e);
    	}
        
		return retVal;
	}	
}
