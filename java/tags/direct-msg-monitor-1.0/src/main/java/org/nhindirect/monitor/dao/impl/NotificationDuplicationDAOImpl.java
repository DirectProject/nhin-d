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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.monitor.dao.NotificationDAOException;
import org.nhindirect.monitor.dao.NotificationDuplicationDAO;
import org.nhindirect.monitor.dao.entity.ReceivedNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate based implementation of the NotificationDuplicationDAO.
 * @author Greg Meyer
 * @since 1.0
 */
@Repository
public class NotificationDuplicationDAOImpl implements NotificationDuplicationDAO
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NotificationDuplicationDAOImpl.class);
	
    @PersistenceContext
    @Autowired
    protected EntityManager entityManager;
    
    /**
     * Default constructor
     */
    public NotificationDuplicationDAOImpl()
    {
    	
    }

	/*
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
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = false)	
	public void addNotification(String messageId, String address) throws NotificationDAOException 
	{
		validateState();	
		
		try
		{
			final Collection<String> notification = this.getReceivedAddresses(messageId, Arrays.asList(address));
			if (!notification.isEmpty())
			{
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Notification for message id " + messageId + " and address " + address +
						" already received.  Not adding to received notification store.");
				return;
			}
			
			final ReceivedNotification notif = new ReceivedNotification();
			notif.setMessageid(messageId);
			notif.setAddress(address);
			notif.setReceivedTime(Calendar.getInstance(Locale.getDefault()));
			
			entityManager.persist(notif);
			entityManager.flush();
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			throw new NotificationDAOException("Failed to add notification to the store.", e);
		}
		///CLOVER:ON
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
	public Set<String> getReceivedAddresses(String messageId, Collection<String> addresses) throws NotificationDAOException 
	{
		validateState();

        if (addresses == null || addresses.size() == 0)
        	return Collections.emptySet();
 
        if (messageId == null || messageId.isEmpty())
        	return Collections.emptySet();
        
        Collection<String> rs;
        Set<String> retVal;
        
        final StringBuffer ids = new StringBuffer("(");
        for (String tp : addresses) 
        {
            if (ids.length() > 1) 
            {
            	ids.append(", ");
            }
            ids.append("'").append(tp.toLowerCase(Locale.getDefault())).append("'");
        }
        ids.append(")");
        
    	try
    	{        
	        final String query = "SELECT address from ReceivedNotification rn WHERE rn.messageid = ?1 and rn.address IN " + ids.toString();
	 
	        final Query select = entityManager.createQuery(query);
	        select.setParameter(1, messageId.toLowerCase(Locale.getDefault()));
	        rs = (Collection<String>)select.getResultList();
	        if (rs == null || rs.size() == 0)
	        	return Collections.emptySet();
	        
	        retVal = new HashSet<String>(rs);
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new NotificationDAOException("Failed to execute received notification DAO query.", e);
    	}
    	///CLOVER:ON
        return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Transactional(readOnly = false)
	public void purgeNotifications(Calendar purgeTime) throws NotificationDAOException 
	{
    	validateState();

		try
		{
	        final Query delete = entityManager.createQuery("DELETE FROM ReceivedNotification rn where rn.receivedTime < ?1");
	        delete.setParameter(1, purgeTime);	        
	        delete.executeUpdate();	

	        entityManager.flush();
		}
		///CLOVER:OFF
    	catch (Exception rt)
    	{
    		throw new NotificationDAOException("Failed to execute delete.", rt);
    	}  
    	///CLOVER:ON
		
		
		
	}
	
}
