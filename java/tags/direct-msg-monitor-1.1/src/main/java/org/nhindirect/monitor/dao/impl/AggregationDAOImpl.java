/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.monitor.dao.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;

import javax.persistence.PersistenceContext;

import org.nhindirect.monitor.dao.AggregationDAO;
import org.nhindirect.monitor.dao.AggregationDAOException;
import org.nhindirect.monitor.dao.AggregationVersionException;
import org.nhindirect.monitor.dao.entity.Aggregation;
import org.nhindirect.monitor.dao.entity.AggregationCompleted;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA implementation of the {@link AggregationDAO} interface.
 * @author Greg Meyer
 * @since 1.1
 */
@Repository
public class AggregationDAOImpl implements AggregationDAO
{
	protected static final int DEFAULT_RECOVERY_LOCK_INTERVAL = 120;
	
    @PersistenceContext(unitName="direct-msg-monitor-store")
    protected EntityManager entityManager;
 
    protected int recoveredEntityLockInterval = DEFAULT_RECOVERY_LOCK_INTERVAL;
	    
    /**
     * Default constructor
     */
    public AggregationDAOImpl()
    {
    	
    }

	/**
	 * Validate that we have a connection to the DAO
	 */
	protected void validateState()
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

	/**
	 * Sets the interval (in seconds) that recovered aggregations will be locked.
	 * @param recoveredEntityLockInterval The interval (in seconds) that recovered aggregations will be locked.
	 */
	public void setRecoveryLockInterval(int recoveredEntityLockInterval)
	{
		this.recoveredEntityLockInterval = recoveredEntityLockInterval;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = true)	
	public Aggregation getAggregation(String id) throws AggregationDAOException
	{
		validateState();	
		
		try
		{
			// get the aggregation by correlation id
			return entityManager.find(Aggregation.class, id);
		}
		catch (Exception e)
		{
			throw new AggregationDAOException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = false, rollbackFor={AggregationDAOException.class})	
	public AggregationCompleted getAggregationCompleted(String id, boolean lock) throws AggregationDAOException
	{
		validateState();	
		
		try
		{
			if (!lock)
			{
				// it's not locked, so just find the completed aggregation and return it
				return entityManager.find(AggregationCompleted.class, id);
			}
			
			else
			{
				/* this is essentially a recovery of a failed completed exchange
				* camel runs this on start up and incremental intervals... it is
				* essential that two application or route instances do not try to play
				* back the same exchange at the same time, or else duplicate exchanges may be
				* routed.... the recovery needs to be locked
				*/
				
				// first check to see if the recovery is available for locking
				final AggregationCompleted entity = entityManager.find(AggregationCompleted.class, id);
				if (entity == null)
					return null;
				
				// availability is determined by the lockedUntil date on the entity
				// if the recovery lock time has not passed, then we can't recover
				if (entity.getRecoveryLockedUntilDtTm() != null && entity.getRecoveryLockedUntilDtTm().after(Calendar.getInstance(Locale.getDefault())))
					return null; 
				
				
				// now lock the row and update lock time and the count
				entityManager.lock(entity, LockModeType.WRITE);
				final Calendar newRecoveryLockTime = Calendar.getInstance(Locale.getDefault());
				newRecoveryLockTime.add(Calendar.SECOND, recoveredEntityLockInterval);
				entity.setRecoveryLockedUntilDtTm(newRecoveryLockTime);
				
				// persist the time new lock time and increment the update count
				entityManager.persist(entity);
				
				entityManager.flush();
				
				return entity;
			}
		}
		catch (OptimisticLockException ol)
		{
			// someone else locked the row... return null
			return null;
		}
		catch (Exception e)
		{
			throw new AggregationDAOException(e);
		}	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = false, rollbackFor={AggregationDAOException.class})	
	public void addUpdateAggregation(Aggregation aggr) throws AggregationDAOException
	{
		try
		{
			// find the aggregation
			final Aggregation existingAggr = this.getAggregation(aggr.getId());
			
			// if its not there by the requested aggregation has a version > 1, then somethine is wrong
			if (existingAggr == null && aggr.getVersion() > 0)
				throw new AggregationVersionException("Aggregation not found but expected to exist due to non 0 version number");
			
			if (existingAggr != null)
			{
				// make sure the version on the existing aggregator matches ours
				if (existingAggr.getVersion() != aggr.getVersion())
					throw new AggregationVersionException("Version number of aggreation does not match what is in the store.");
				
				// lock the aggregation for update
				entityManager.lock(existingAggr, LockModeType.WRITE);
				existingAggr.setExchangeBlob(aggr.getExchangeBlob());
				entityManager.persist(existingAggr);
			}
			else
			{
				// initial add... set the version number to 1
				aggr.setVersion(aggr.getVersion() + 1);
				entityManager.persist(aggr);
			}
			
			// commit
			entityManager.flush();
		}
		catch (AggregationDAOException ae)
		{
			throw ae;
		}
		catch (OptimisticLockException ol)
		{
			throw new AggregationVersionException("Aggregation was updated by another thread or process before it could be committed.");
		}
		catch (Exception e)
		{
			throw new AggregationDAOException("Failed to add or update aggregation.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = false, rollbackFor={AggregationDAOException.class})
	public void removeAggregation(Aggregation agg, String exchangeId) throws AggregationDAOException
	{
		try
		{
			// find the aggregation
			final Aggregation existingAgg = this.getAggregation(agg.getId());
	
			if (existingAgg != null)
			{
				// check the version number for consistency
				if (existingAgg.getVersion() != agg.getVersion())
					throw new AggregationVersionException("Version number of aggreation does not match what is in the store.");		
				
				// lock for removal
				entityManager.lock(existingAgg, LockModeType.WRITE);
				entityManager.remove(existingAgg);
			}
			else
				throw new AggregationDAOException("Aggregation does not exist is store.");	
			
			// add to the completed repository
			final AggregationCompleted completed = new AggregationCompleted();
			
			completed.setExchangeBlob(existingAgg.getExchangeBlob());
			completed.setId(exchangeId);
			completed.setVersion(1);
			
			entityManager.persist(completed);
			
			// commit
			entityManager.flush();
		}
		catch (AggregationDAOException ae)
		{
			throw ae;
		}
		///CLOVER:OFF
		catch (OptimisticLockException ol)
		{
			throw new AggregationVersionException("Aggregation was removed by another thread or process before it could be committed.");
		}		
		catch (Exception e)
		{
			throw new AggregationDAOException("Failed to remove aggregation.", e);
		}
		///CLOVER:ON
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = false)
	public void confirmAggregation(String id) throws AggregationDAOException
	{
		try
		{
			// find the aggregation
			final AggregationCompleted completed = entityManager.find(AggregationCompleted.class, id);
			if (completed != null)
			{
				// remove it
				entityManager.remove(completed);
				entityManager.flush();
			}
		}
		catch (Exception e)
		{
			throw new AggregationDAOException("Failed to confirm aggregation.", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
    @Transactional(readOnly = true)
	public List<String> getAggregationKeys() throws AggregationDAOException
	{
        final String query = "SELECT id from Aggregation agg";
        
        Collection<String> rs;
        List<String> retVal = null;;
        
        try
        {
        	// get the list using a simple select
	        final Query select = entityManager.createQuery(query);
	        rs = (Collection<String>)select.getResultList();
	        
	        // no keys... return an empty set
	        if (rs == null || rs.size() == 0)
	        	return Collections.emptyList();
	        
	        // put the list into a linked list
	        retVal = new LinkedList<String>(rs);
        }
		catch (Exception e)
		{
			throw new AggregationDAOException("Failed to get aggregation keys", e);
		}
        
        return retVal;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
    @Transactional(readOnly = true)
	public List<String> getAggregationCompletedKeys() throws AggregationDAOException
	{
        final String query = "SELECT id from AggregationCompleted agg";
        
        Collection<String> rs;
        List<String> retVal = null;;
        
        try
        {
        	// get the list using a simple select
	        final Query select = entityManager.createQuery(query);
	        rs = (Collection<String>)select.getResultList();
	        
	        // no keys... return an empty set
	        if (rs == null || rs.size() == 0)
	        	return Collections.emptyList();
	        
	        // put the list into a linked list
	        retVal = new LinkedList<String>(rs);
        }
    	catch (Exception e)
    	{
    		throw new AggregationDAOException("Failed to get aggregation completed keys", e);
    	}
        
        return retVal;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = false)
	public void purgeAll() throws AggregationDAOException
	{
		try
		{
			// purge using simple delete statements
			Query delete = entityManager.createQuery("DELETE FROM Aggregation agg where agg.version > -1");
		        
	        delete.executeUpdate();	
	        
			delete = entityManager.createQuery("DELETE FROM AggregationCompleted agg where agg.version > -1");
	        
	        delete.executeUpdate();	
	        
	        entityManager.flush();
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			throw new AggregationDAOException("Failed to purge all aggregation information.", e);
		}
		///CLOVER:ON
	}
}
