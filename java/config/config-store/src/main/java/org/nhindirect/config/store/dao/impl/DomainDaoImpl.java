package org.nhindirect.config.store.dao.impl;
/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Patrick Pyette	ppyette@inpriva.com
 
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.DomainDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default Spring/JPA implemenation
 * @author ppyette
 *
 */
@Repository
public class DomainDaoImpl implements DomainDao {
	
    @PersistenceContext
    private EntityManager entityManager;

    private static final Log log = LogFactory.getLog(DomainDaoImpl.class);
       

	/* (non-Javadoc)
	 * @see org.nhindirect.config.store.dao.DomainDao#count()
	 */
    // @Transactional(readOnly = true)
	public int count() {
		if (log.isDebugEnabled()) log.debug("Enter");
		Long result = (Long) entityManager.createQuery("select count(d) from Domain d").getSingleResult();
		if (log.isDebugEnabled()) log.debug("Exit: " + result.intValue());
		return result.intValue();
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.store.dao.DomainDao#add(org.nhindirect.config.store.Domain)
	 */
    @Transactional(readOnly = false)
	public void add(Domain item) {
    	if (log.isDebugEnabled()) log.debug("Enter");
	    
    	if (item != null) {
    		item.setCreateTime(Calendar.getInstance());
    		item.setUpdateTime(item.getCreateTime());
    		
    		// Avoid a Constraint Violation if we have 0L as an id
    		if (((item.getPostmasterEmailAddressId()) != null) &&
    			(item.getPostmasterEmailAddressId().longValue() == 0)) {
    			item.setPostmasterEmailAddressId((Long)null);
    		}
    	
	    	entityManager.persist(item);
	    }
		
    	if (log.isDebugEnabled()) log.debug("Exit");
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.store.dao.DomainDao#update(org.nhindirect.config.store.Domain)
	 */
    @Transactional(readOnly = false)
	public void update(Domain item) {
    	if (log.isDebugEnabled()) log.debug("Enter");
	    
    	if (item != null) {
    		Domain inDb = entityManager.find(Domain.class, item.getId());
    		inDb.setDomainName(item.getDomainName());
    		inDb.setPostmasterEmailAddressId(item.getPostmasterEmailAddressId());
    		inDb.setStatus(item.getStatus());
    		inDb.setUpdateTime(Calendar.getInstance());
    		entityManager.merge(inDb);
    	}
    	
	    if (log.isDebugEnabled()) log.debug("Exit");
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.store.dao.DomainDao#save(org.nhindirect.config.store.Domain)
	 */
    @Transactional(readOnly = false)
	public void save(Domain item) {		
    	update(item);
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.store.dao.DomainDao#delete(java.lang.String)
	 */
    @Transactional(readOnly = false)
	public void delete(String name) {
    	if (log.isDebugEnabled()) log.debug("Enter");
    	
	    int count = 0;
    	if (name != null) {
    		Query delete = entityManager.createQuery("DELETE FROM Domain d WHERE d.domainName = ?1");
    		delete.setParameter(1, name);
    		count = delete.executeUpdate();
    	}

	    if (log.isDebugEnabled()) log.debug("Exit: " + count + " records deleted");
	}
    
    @Transactional(readOnly = true)
    public Domain getDomainByName(String name) {
    	if (log.isDebugEnabled()) log.debug("Enter");
    	
    	Domain result = null;
    	
    	if (name != null) {
    		Query select = entityManager.createQuery("SELECT DISTINCT d from Domain d WHERE d.domainName = ?1");
			result = (Domain) select.setParameter(1, name).getSingleResult();
    	}
    	
	    if (log.isDebugEnabled()) log.debug("Exit");
	    return result;
    }


	/* (non-Javadoc)
	 * @see org.nhindirect.config.store.dao.DomainDao#getDomains(java.lang.String, org.nhindirect.config.store.EntityStatus)
	 * 
	 * Convert the list of names into a String to be used in an IN clause (i.e. {"One", "Two", "Three"} --> ('One', 'Two', 'Three'))
	 */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Domain> getDomains(List<String> names, EntityStatus status) {
    	if (log.isDebugEnabled()) log.debug("Enter");
    	
    	List<Domain> result = null;
    	Query select = null;
    	if (names != null) {
    		StringBuffer nameList = new StringBuffer("(");
    		for (String aName : names ) {
    			if (nameList.length() > 1) {
    				nameList.append(", ");
    			}
    			nameList.append("'")
    			        .append(aName)
    			        .append("'");
    		}
    		nameList.append(")");
    		String query = "SELECT d from Domain d WHERE d.domainName IN " + nameList.toString();
    		
    		if (status != null) {
    			select = entityManager.createQuery(query + " AND d.status = ?1");
    	    	select.setParameter(1, status);
    		}
    		else {
    			select = entityManager.createQuery(query);
    		}
    	}
    	else {
    		if (status != null)
    		{
    			select = entityManager.createQuery("SELECT d from Domain d WHERE d.status = ?1");
    	    	select.setParameter(1, status);
    		}
    		else {
    			select = entityManager.createQuery("SELECT d from Domain d");
    		}
    		
    	}
    	
    	@SuppressWarnings("rawtypes")
		List rs = select.getResultList();
    	if ((rs.size() != 0) &&
    	    (rs.get(0) instanceof Domain)) {
    		result = (List<Domain>)rs;
    	}
    	else {
    		result = new ArrayList<Domain>();
    	}
    	
	    if (log.isDebugEnabled()) log.debug("Exit");
		return result;
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.store.dao.DomainDao#listDomains(java.lang.String, int)
	 * 
	 */
    //TODO  I'm not sure if this is doing the right thing.  I suspect that the real intent is to do some kind of db paging
    @Transactional(readOnly = true)
	public List<Domain> listDomains(String name, int count) {
    	if (log.isDebugEnabled()) log.debug("Enter");
    	
    	List<Domain> result = null;
    	Query select = null;
    	if (name != null) {
    		select = entityManager.createQuery("SELECT d from Domain d WHERE d.domainName = ?1");
    	    select.setParameter(1, name);
    	}
    	else {
    		select = entityManager.createQuery("SELECT d from Domain d");
    	}
    	
    	// assuming that a count of zero really means no limit
    	if (count > 0) {
    		select.setMaxResults(count);
    	}
    	
    	@SuppressWarnings("rawtypes")
		List rs = select.getResultList();
    	if ((rs.size() != 0) &&
    	    (rs.get(0) instanceof Domain)) {
    		result = (List<Domain>)rs;
    	}
    	
	    if (log.isDebugEnabled()) log.debug("Exit");
		return result;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
