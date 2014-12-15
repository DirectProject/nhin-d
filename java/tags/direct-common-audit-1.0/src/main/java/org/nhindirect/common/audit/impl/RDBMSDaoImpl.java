package org.nhindirect.common.audit.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DOA implementation for audit events.
 * @author Greg Meyer
 * @since 1.0
 */
@Repository
public class RDBMSDaoImpl implements RDBMSDao
{
	private final Log LOGGER = LogFactory.getFactory().getInstance(RDBMSDaoImpl.class);
	
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;
    
    public RDBMSDaoImpl()
    {	
    	
    }
    
    ///CLOVER:OFF
    public RDBMSDaoImpl(EntityManager entityManager)
    {
    	this();
    	
    	setEntityManager(entityManager);
    }
    ///CLOVER:ON
    
	/**
	 * Sets the entity manager for access to the underlying data store medium.
	 * @param entityManager The entity manager.
	 */
    ///CLOVER:OFF
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
    ///CLOVER:ON
	
	/**
	 * Validate that we have a connection to the DAO
	 */
	protected void validateState()
	{	
    	if (entityManager == null)
    		throw new IllegalStateException("entityManger has not been initialized");
	}

	@Override
	@Transactional(readOnly = false)
	public void writeRDBMSEvent(UUID eventId, Calendar eventTimeStamp,
			String principal, AuditEvent event,
			Collection<? extends AuditContext> contexts) 
    {
		try
		{
	    	validateState();
	    	
			final org.nhindirect.common.audit.impl.entity.AuditEvent newEvent =
					new org.nhindirect.common.audit.impl.entity.AuditEvent();
	
			newEvent.setEventName(event.getName());
			newEvent.setEventType(event.getType());
	
			newEvent.setEventTime(Calendar.getInstance(Locale.getDefault()));
			newEvent.setPrincipal(principal);
			newEvent.setUUID(eventId.toString());
	
			if (contexts != null)
			{
				final Collection<org.nhindirect.common.audit.impl.entity.AuditContext> entityContexts = newEvent.getAuditContexts();
				for (AuditContext context : contexts)
				{
					final org.nhindirect.common.audit.impl.entity.AuditContext newContext = 
							new org.nhindirect.common.audit.impl.entity.AuditContext();
					
					newContext.setContextName(context.getContextName());
					newContext.setContextValue(context.getContextValue());
					newContext.setAuditEvent(newEvent);
					entityContexts.add(newContext);
				}
			}
			entityManager.persist(newEvent);
			entityManager.flush();
		}
		catch (Throwable e)
		{
			LOGGER.error("Failed to write audit event to RDBMS store: " + e.getMessage(), e);	
		}
		
	}

	@Override
	@Transactional(readOnly = true)	
	public Integer getRDBMSEventCount() 
	{
		try
		{
	    	validateState();    	
	
	    	final Query select = entityManager.createQuery("SELECT count(ae) from AuditEvent ae");
	
	    	return new Integer(((Long)select.getSingleResult()).intValue());
		}
		catch (Throwable e)
		{
			LOGGER.error("Failed to write audit event count: " + e.getMessage(), e);	
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)	
	public Collection<org.nhindirect.common.audit.impl.entity.AuditEvent> getRDBMSEvents(Integer eventCount) 
	{
		try
		{
	    	final Query select = 
	    			entityManager.createQuery("SELECT ae from AuditEvent ae ORDER BY ae.eventTime desc").setMaxResults(eventCount);
			
	        @SuppressWarnings("unchecked")
			final Collection<org.nhindirect.common.audit.impl.entity.AuditEvent> rs = select.getResultList();
	        
	        // lazy fetching
	        for (org.nhindirect.common.audit.impl.entity.AuditEvent event : rs)
	        	event.getAuditContexts().size();
	        
	        return rs;
		}
		catch (Throwable e)
		{
			LOGGER.error("Failed get audit events: " + e.getMessage(), e);	
			throw new RuntimeException(e);
		}		
	}

	@Override
	@Transactional(readOnly = false)
	public void rDBMSclear() 
	{
		// truncating the tables
		
		try
		{
	    	Query delete = 
	    			entityManager.createQuery("delete from AuditContext");
	    	
	    	delete.executeUpdate();
	    	
	    	delete = 
	    			entityManager.createQuery("delete from AuditEvent");
	    	
	    	delete.executeUpdate();
	    	
	    	entityManager.flush();
		}
		catch (Throwable e)
		{
			LOGGER.error("Failed clear audit events: " + e.getMessage(), e);	
			throw new RuntimeException(e);
		}	
	}
	
	
}
