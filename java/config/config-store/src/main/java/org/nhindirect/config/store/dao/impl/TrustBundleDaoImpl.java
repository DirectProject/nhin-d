package org.nhindirect.config.store.dao.impl;

import java.security.cert.X509Certificate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.nhindirect.config.store.BundleRefreshError;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.config.store.dao.TrustBundleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TrustBundleDaoImpl implements TrustBundleDao
{
	/*
	 * Provided by Spring application context.
	 */
	@PersistenceContext
    @Autowired
    protected EntityManager entityManager;
	
	/**
	 * Empty constructor
	 */
	public TrustBundleDaoImpl()
	{
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
	public void updateTrustBundleAnchors(long trustBundleId, Calendar attemptTime, Collection<TrustBundleAnchor> newAnchorSet)
			throws ConfigurationStoreException 
	{
    	validateState();
    	
    	try
    	{
			final TrustBundle existingBundle = this.getTrustBundleById(trustBundleId);
			if (existingBundle == null)
				throw new ConfigurationStoreException("Trust bundle does not exist");
			
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
        
        //deleteAnchorCircleAssociations(thumbprints);
        
        Query delete = null;
        final StringBuffer ids = new StringBuffer("(");
        for (long id : trustBundleIds) 
        {
            if (ids.length() > 1) 
            {
            	ids.append(", ");
            }
            ids.append(id);
        }
        ids.append(")");
        
    	try
    	{        
	        final String deleteString = "DELETE from TrustBundle ta where ta.id IN " + ids.toString();
	 
	        delete = entityManager.createQuery(deleteString);

	        delete.executeUpdate();	
	        entityManager.flush();
    	}
    	catch (Exception e)
    	{
    		throw new ConfigurationStoreException("Failed to trust bundles from store.", e);
    	}
	}

	@Override
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


}
