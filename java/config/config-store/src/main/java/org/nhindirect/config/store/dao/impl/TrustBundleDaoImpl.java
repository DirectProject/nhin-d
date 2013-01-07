package org.nhindirect.config.store.dao.impl;

import java.security.cert.X509Certificate;

import java.util.ArrayList;
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
import org.nhindirect.config.store.BundleRefreshError;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.config.store.TrustBundleDomainReltn;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.TrustBundleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TrustBundleDaoImpl implements TrustBundleDao
{
    private static final Log log = LogFactory.getLog(TrustBundleDaoImpl.class);
	
	/*
	 * Provided by Spring application context.
	 */
	@PersistenceContext
    @Autowired
    protected EntityManager entityManager;
	
    protected DomainDao domainDao;	
	
	/**
	 * Empty constructor
	 */
	public TrustBundleDaoImpl()
	{
	}
	
    @Autowired
	public void setDomainDao(DomainDao domainDao)
	{
		this.domainDao = domainDao;
	}
	
	/*
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
	public Collection<TrustBundle> getTrustBundles() throws ConfigurationStoreException 
	{
		validateState();
		
		Collection<TrustBundle> rs;
        try
        {
	        Query select = entityManager.createQuery("SELECT tb from TrustBundle tb");
	        
	        rs = select.getResultList();
	        if (rs.size() == 0)
	        	return Collections.emptyList();
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute trust bundle DAO query.", e);
    	}
        
        // make sure the anchors are loaded
        for (TrustBundle bundle : rs)
        {
            if (!bundle.getTrustBundleAnchors().isEmpty())
            	for (TrustBundleAnchor anchor : bundle.getTrustBundleAnchors())
            		anchor.getData();
        }
        
		return rs;
	}

	@Override
    @Transactional(readOnly = true)
	public TrustBundle getTrustBundleByName(String bundleName) throws ConfigurationStoreException
	{
		validateState();
        
        try
        {
            Query select = entityManager.createQuery("SELECT tb from TrustBundle tb WHERE UPPER(tb.bundleName) = ?1");
            select.setParameter(1, bundleName.toUpperCase(Locale.getDefault()));
            
            TrustBundle rs = (TrustBundle)select.getSingleResult();

            // make sure the anchors are loaded
            if (!rs.getTrustBundleAnchors().isEmpty())
            	for (TrustBundleAnchor anchor : rs.getTrustBundleAnchors())
            		anchor.getData();
            
	        return rs;
        }
        catch (NoResultException e)
        {
        	return null;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute trust bundle DAO query.", e);
    	}
	}

	@Override
    @Transactional(readOnly = true)
	public TrustBundle getTrustBundleById(long id) throws ConfigurationStoreException
	{
		validateState();
        
        try
        {
            Query select = entityManager.createQuery("SELECT tb from TrustBundle tb WHERE tb.id = ?1");
            select.setParameter(1, id);
            
            TrustBundle rs = (TrustBundle)select.getSingleResult();

            // make sure the anchors are loaded
            if (!rs.getTrustBundleAnchors().isEmpty())
            	for (TrustBundleAnchor anchor : rs.getTrustBundleAnchors())
            		anchor.getData();
            
	        return rs;
        }
        catch (NoResultException e)
        {
        	return null;
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute trust bundle DAO query.", e);
    	}		
	}
	
	@Override
    @Transactional(readOnly = false)
	public void addTrustBundle(TrustBundle bundle) throws ConfigurationStoreException 
	{
		
    	validateState();
    	
    	try
    	{
			final TrustBundle existingBundle = this.getTrustBundleByName(bundle.getBundleName());
			if (existingBundle != null)
				throw new ConfigurationStoreException("Trust bundle " + bundle.getBundleName() + " already exists");
			
			bundle.setCreateTime(Calendar.getInstance(Locale.getDefault()));
			
			entityManager.persist(bundle);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to add trust bundle " + bundle.getBundleName(), e);
    	}
    	///CLOVER:ON
		
	}

	@Override
    @Transactional(readOnly = false)
	public void updateTrustBundleAnchors(long trustBundleId, Calendar attemptTime, Collection<TrustBundleAnchor> newAnchorSet,
			String bundleCheckSum)
			throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final TrustBundle existingBundle = this.getTrustBundleById(trustBundleId);

			if (existingBundle == null)
				throw new ConfigurationStoreException("Trust bundle does not exist");
			
			existingBundle.setCheckSum(bundleCheckSum);
			existingBundle.setTrustBundleAnchors(newAnchorSet);
			existingBundle.setLastRefreshAttempt(attemptTime);
			existingBundle.setLastSuccessfulRefresh(Calendar.getInstance(Locale.getDefault()));
			
			entityManager.persist(existingBundle);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to update anchors in trust bundle.", e);
    	}	
    	///CLOVER:ON
	}

	@Override
    @Transactional(readOnly = false)
	public void updateLastUpdateError(long trustBundleId, Calendar attemptTime, BundleRefreshError error) 
			throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final TrustBundle existingBundle = this.getTrustBundleById(trustBundleId);
			if (existingBundle == null)
				throw new ConfigurationStoreException("Trust bundle does not exist");
			
			existingBundle.setLastRefreshAttempt(attemptTime);
			existingBundle.setLastRefreshError(error);
			
			entityManager.persist(existingBundle);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to update bundle last refresh error.", e);
    	}	
    	///CLOVER:ON
	}

    @Transactional(readOnly = false)
	@Override
	public void deleteTrustBundles(long[] trustBundleIds) throws ConfigurationStoreException 
	{
		validateState();

        if (trustBundleIds == null || trustBundleIds.length == 0)
        	return;

        for (long id : trustBundleIds) 
        {
        	try
        	{
        		final TrustBundle bundle = this.getTrustBundleById(id);
        		
        		this.disassociateTrustBundleFromDomains(id);
        		
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
	public void updateTrustBundleSigningCertificate(long trustBundleId, X509Certificate signingCert) 
			throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final TrustBundle existingBundle = this.getTrustBundleById(trustBundleId);
			if (existingBundle == null)
				throw new ConfigurationStoreException("Trust bundle does not exist");
			
			existingBundle.setSigningCertificateData(signingCert.getEncoded());
			
			entityManager.persist(existingBundle);
			entityManager.flush();
    	}
    	catch (ConfigurationStoreException cse)
    	{
    		throw cse;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to update bundle last refresh error.", e);
    	}	
    	///CLOVER:ON
		
	}

	@Override
    @Transactional(readOnly = false)
	public void associateTrustBundleToDomain(long domainId, long trustBundleId) throws ConfigurationStoreException
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		// make sure the trust bundle exists
		final TrustBundle trustBundle = this.getTrustBundleById(trustBundleId);
		if (trustBundle == null)
			throw new ConfigurationStoreException("Trust budnel with id " + trustBundle + " does not exist");
		
		try
		{
			final TrustBundleDomainReltn domainTrustBundleAssoc = new TrustBundleDomainReltn();
			domainTrustBundleAssoc.setDomain(domain);
			domainTrustBundleAssoc.setTrustBundle(trustBundle);
			
			entityManager.persist(domainTrustBundleAssoc);
			entityManager.flush();
			
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to associate trust bundle to domain.", e);
		}
	}
	
	@Override
    @Transactional(readOnly = false)
	public void disassociateTrustBundleFromDomain(long domainId, long trustBundleId) throws ConfigurationStoreException
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		// make sure the trust bundle exists
		final TrustBundle trustBundle = this.getTrustBundleById(trustBundleId);
		if (trustBundle == null)
			throw new ConfigurationStoreException("Trust budnel with id " + trustBundle + " does not exist");
		
		try
		{
			final Query select = entityManager.createQuery("SELECT tbd from TrustBundleDomainReltn tbd where tbd.domain  = ?1 " +
	        		" and tbd.trustBundle = ?2 ");
	        
	        select.setParameter(1, domain);
	        select.setParameter(2, trustBundle);
            
	        final TrustBundleDomainReltn reltn = (TrustBundleDomainReltn)select.getSingleResult();
	        
	        entityManager.remove(reltn);
	        entityManager.flush();
		}
		catch (NoResultException e)
		{
			throw new ConfigurationStoreException("Association between domain id " + domainId + " and trust bundle id" 
					 + trustBundleId + " does not exist", e);
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to delete trust bundle to domain relation.", e);
		}
        
	}
	
	@Override
    @Transactional(readOnly = false)
	public void disassociateTrustBundlesFromDomain(long domainId) throws ConfigurationStoreException
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		try
		{
			final Query delete = entityManager.createQuery("DELETE from TrustBundleDomainReltn tbd where tbd.domain  = ?1");
	        
	        delete.setParameter(1, domain);
	        delete.executeUpdate();
	        
	        entityManager.flush();
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to dissaccociate trust bundle from domain id ." + domainId, e);
		}
	}
	
	@Override
    @Transactional(readOnly = false)	
	public void disassociateTrustBundleFromDomains(long trustBundleId) throws ConfigurationStoreException
	{
		validateState();
		
		// make sure the trust bundle exists
		final TrustBundle trustBundle = this.getTrustBundleById(trustBundleId);
		if (trustBundle == null)
			throw new ConfigurationStoreException("Trust budnel with id " + trustBundle + " does not exist");
		
		try
		{
			final Query delete = entityManager.createQuery("DELETE from TrustBundleDomainReltn tbd where tbd.trustBundle  = ?1");
	        
	        delete.setParameter(1, trustBundle);
	        delete.executeUpdate();
	        
	        entityManager.flush();
		}
		catch (Exception e)
		{
			throw new ConfigurationStoreException("Failed to dissaccociate domains from trust bundle id ." + trustBundleId, e);
		}
	}
	
	@Override
    @Transactional(readOnly = true)	
	public Collection<TrustBundle> getTrustBundlesByDomain(long domainId) throws ConfigurationStoreException
	{
		validateState();
		
		// make sure the domain exists
		final Domain domain = domainDao.getDomain(domainId);
		if (domain == null)
			throw new ConfigurationStoreException("Domain with id " + domainId + " does not exist");
		
		Collection<TrustBundle> retVal;
        try
        {
	        final Query select = entityManager.createQuery("SELECT tbd from TrustBundleDomainReltn tbd where tbd.domain = ?1");
	        select.setParameter(1, domain);
	        
	        @SuppressWarnings("unchecked")
			final Collection<TrustBundleDomainReltn> rs = select.getResultList();
	        if (rs.size() == 0)
	        	return Collections.emptyList();
	        
	        retVal = new ArrayList<TrustBundle>();
	        for (TrustBundleDomainReltn reltn : rs)
	        	retVal.add(reltn.getTrustBundle());
	       
        }
      	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to execute trust bundle relation DAO query.", e);
    	}
        
		return retVal;
	}
}
