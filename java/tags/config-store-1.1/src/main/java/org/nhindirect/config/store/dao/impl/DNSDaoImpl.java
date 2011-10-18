/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Greg Meyer     gm2552@cerner.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

package org.nhindirect.config.store.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.dao.DNSDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.xbill.DNS.Type;

/**
 * JPA implementation of the DNSDao interface.
 * @author Greg Meyer
 * @since 1.1
 */
@Repository
public class DNSDaoImpl implements DNSDao 
{

	@PersistenceContext
    @Autowired
    private EntityManager entityManager;
    
    private static final Log log = LogFactory.getLog(DNSDaoImpl.class);	
	
    /**
     * {@inheritDoc}}
     */
    @Override
    @Transactional(readOnly = false)
	public void add(Collection<DNSRecord> records) 
    {
        if (log.isDebugEnabled())
            log.debug("add() Enter");
        
        try
        {
	        if (records != null && records.size() > 0)
	        {	
		        for (DNSRecord record : records)
		        {
		            // ensure record doesn't already exist or we are not adding a record with type ANY
		        	
		        	if(record.getType() == Type.ANY)
		        		throw new ConfigurationStoreException("Cannot add records with type ANY.");        	
		        	else
		        	{
		        		Collection<DNSRecord> checkRecs = get(record.getName(), record.getType());
		        		if (checkRecs.contains(record))
		        			throw new ConfigurationStoreException("Record name " + record.getName() + " and type " + record.getType() + 
		        					" already exists with same rdata.");
		        	}
		        		
		        	        	
		        	record.setCreateTime(Calendar.getInstance());
		            
		        	if (log.isDebugEnabled())
		                log.debug("Persisting DNS record\r\n\tName: "  + record.getName() + "\r\n\tType: " + record.getType());
		        	entityManager.persist(record);
		        }
		
		    	if (log.isDebugEnabled())
		    		log.debug("Flushing " + records.size() + " added records.");
		    	
		        entityManager.flush();
	        }

        }
        finally
        {
        	if (log.isDebugEnabled())
        		log.debug("add() Exit");
        }
	}

    /**
     * {@inheritDoc}}
     */
	@Override
	public int count() 
	{
        if (log.isDebugEnabled())
        	log.debug("count() Enter");
        
        Long result = (Long) entityManager.createQuery("select count(d) from DNSRecord d").getSingleResult();
        
        if (log.isDebugEnabled())
            log.debug("count() Exit: " + result.intValue());
        
        return result.intValue();
	}

    /**
     * {@inheritDoc}}
     */
	@Override
    @SuppressWarnings("unchecked")
	public Collection<DNSRecord> get(int type) 
	{
        if (log.isDebugEnabled())
            log.debug("get(int) Enter");

        
        List<DNSRecord> result = Collections.emptyList();

        Query select = null;
        if (type != Type.ANY)
        {
        	select = entityManager.createQuery("SELECT d from DNSRecord d WHERE d.type = ?1");
            select.setParameter(1, type);
        }
        else
        	select = entityManager.createQuery("SELECT d from DNSRecord d");
        
        List rs = select.getResultList();
        if (rs != null && (rs.size() != 0) && (rs.get(0) instanceof DNSRecord)) 
        {
            result = (List<DNSRecord>) rs;
        }

        if (log.isDebugEnabled())
            log.debug("get(int) Exit");
        return result;
	}

    /**
     * {@inheritDoc}}
     */	
	@Override
	public DNSRecord get(long recordId) 
	{
        if (log.isDebugEnabled())
            log.debug("get(long) Enter");
        
        Collection<DNSRecord> recs =  get(new long[] {recordId});
        if (recs == null || recs.isEmpty())
        	return null;
        
        if (log.isDebugEnabled())
            log.debug("get(long) Exit");
        
        return recs.iterator().next();
	}

    /**
     * {@inheritDoc}}
     */	
	@Override
    @SuppressWarnings("unchecked")	
	public Collection<DNSRecord> get(long[] recordIds) 
	{
        if (log.isDebugEnabled())
            log.debug("get(long[]) Enter");
        
        List<DNSRecord> result = Collections.emptyList();
        
        if (recordIds != null && recordIds.length > 0)
        {
	        Query select = null;
	        StringBuffer idList = new StringBuffer("(");
	        for (long id : recordIds) 
	        {
	            if (idList.length() > 1) 
	            {
	            	idList.append(", ");
	            }
	            idList.append(id);
	        }
	        idList.append(")");
	        String query = "SELECT d from DNSRecord d WHERE d.id IN " + idList.toString();
	 
	        select = entityManager.createQuery(query);
	        List rs = select.getResultList();
	        if (rs != null && (rs.size() != 0) && (rs.get(0) instanceof DNSRecord)) 
	        {
	            result = (List<DNSRecord>) rs;
	        }
        }
        
        if (log.isDebugEnabled())
            log.debug("get(long[]) Exit");
        
        return result;       
	}

    /**
     * {@inheritDoc}}
     */	
	@Override
    @SuppressWarnings("unchecked")	
	public Collection<DNSRecord> get(String name, int type) 
	{
        if (log.isDebugEnabled())
            log.debug("get(String, int) Enter");
        
        List<DNSRecord> result = Collections.emptyList();
        
        Query select = null;
        if (type == Type.ANY)
        {
        	select = entityManager.createQuery("SELECT d from DNSRecord d WHERE UPPER(d.name) = ?1");
            select.setParameter(1, name.toUpperCase(Locale.getDefault()));
        }
        else
        {
        	select = entityManager.createQuery("SELECT d from DNSRecord d WHERE UPPER(d.name) = ?1 and d.type = ?2");
            select.setParameter(1, name.toUpperCase(Locale.getDefault()));
            select.setParameter(2, type);
        }
        
        List rs = select.getResultList();
        if (rs != null && (rs.size() != 0) && (rs.get(0) instanceof DNSRecord)) 
        {
            result = (List<DNSRecord>) rs;
        }        
        
        if (log.isDebugEnabled())
            log.debug("get(String, int) Exit");
        
        return result;
	}
	
    /**
     * {@inheritDoc}}
     */	
	@Override
	public Collection<DNSRecord> get(String name) 	
	{
        if (log.isDebugEnabled())
            log.debug("get(String) Enter");
        
        Collection<DNSRecord> retVal = get(name, Type.ANY);
        
        if (log.isDebugEnabled())
            log.debug("get(String) Exit");
        
        return  retVal;
	}

    /**
     * {@inheritDoc}}
     */
    @Override
    @Transactional(readOnly = false)
	public void remove(Collection<DNSRecord> records) 
	{
        if (log.isDebugEnabled())
            log.debug("remove(Collection<DNSRecord>) Enter");
		
        
        if (records != null && records.size() > 0)
        {
            Collection<DNSRecord> toDelete = new ArrayList<DNSRecord>();
            
            // get the records out of the DAO
            for (DNSRecord record : records)
            	toDelete.addAll(get(record.getName(), record.getType()));

            // delete all qualifying records
            if (toDelete.size() > 0)
            	for (DNSRecord record : toDelete)
                    entityManager.remove(record);
            
            if (log.isDebugEnabled())
            {
                if (toDelete.size() == 0)
                	log.debug("No DNS records qualified for deletion.");
                else
                	log.debug(toDelete.size() + " DNS records deleted");
            }
            entityManager.flush();
        }
        
        if (log.isDebugEnabled())
            log.debug("remove(Collection<DNSRecord>) Exit");
	}

    /**
     * {@inheritDoc}}
     */
    @Override
    @Transactional(readOnly = false)
	public void remove(long recordId) 
	{
        if (log.isDebugEnabled())
            log.debug("remove(long) Enter");
        
        remove(new long[] {recordId});
        
        if (log.isDebugEnabled())
            log.debug("remove(long) Exit");        
	}

    /**
     * {@inheritDoc}}
     */
    @Override
    @Transactional(readOnly = false)
	public void remove(long[] recordIds) 
	{
        if (log.isDebugEnabled())
            log.debug("remove(long[]) Enter");
     
        if (recordIds != null && recordIds.length > 0)
        {
		
	        StringBuffer ids = new StringBuffer("(");
	        for (long id : recordIds) 
	        {
	            if (ids.length() > 1) 
	            {
	            	ids.append(", ");
	            }
	            ids.append(id);
	        }
	        ids.append(")");
	        String query = "DELETE FROM DNSRecord d WHERE d.id IN " + ids.toString();
	        
	        int count = 0;
	        Query delete = entityManager.createQuery(query);
	        count = delete.executeUpdate();
	
	        if (log.isDebugEnabled())
	        {
	        	if (count == 0)
	        		log.debug("No DNS records qualified for deletion.");
	        	else
	        		log.debug(count + " DNS records deleted");
	        }
	        
	        entityManager.flush();
        }        
        
        if (log.isDebugEnabled())
            log.debug("remove(long[]) Exit");      
		
	}

    /**
     * {@inheritDoc}}
     */
    @Override
    @Transactional(readOnly = false)
	public void update(long id, DNSRecord record) 
	{
        if (log.isDebugEnabled())
            log.debug("remove(long id, DNSRecord record) Enter");
        
        try
        {
        	// get the record
        	if (record.getType() == Type.ANY)
        		throw new ConfigurationStoreException("Record type for update cannot be ANY");
		
        	DNSRecord toUpdate = get(id);
        	if (toUpdate == null)
        		throw new ConfigurationStoreException("Record with id " + id + " does not exist.");
        	
        	toUpdate.setType(record.getType());
        	toUpdate.setName(record.getName());
        	toUpdate.setTtl(record.getTtl());
        	toUpdate.setDclass(record.getDclass());
        	toUpdate.setData(record.getData());
        	
        	entityManager.merge(toUpdate);
        	entityManager.flush();
        	
            if (log.isDebugEnabled())
                log.debug("1 DNS record updated.");
        }
        finally
        {	       
	        if (log.isDebugEnabled())
	            log.debug("remove(long id, DNSRecord record) Exit");
        }
	}    
}

