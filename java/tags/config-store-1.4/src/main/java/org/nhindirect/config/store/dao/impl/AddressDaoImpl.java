/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
 in the documentation and/or other materials provided with the distribution.  
 3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
 BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.store.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.store.Address;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.AddressDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementing class for Address DAO methods.
 * 
 * @author ppyette
 */
@Repository
public class AddressDaoImpl implements AddressDao {

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    private static final Log log = LogFactory.getLog(AddressDaoImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#count()
     */
    @Transactional(readOnly = true)
    public int count() {
        if (log.isDebugEnabled())
            log.debug("Enter");
        Long result = (Long) entityManager.createQuery("select count(d) from Address a").getSingleResult();
        if (log.isDebugEnabled())
            log.debug("Exit: " + result.intValue());
        return result.intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#add(org.nhindirect.config.store.Address)
     */
    @Transactional(readOnly = false)
    public void add(Address item) {
        if (log.isDebugEnabled())
            log.debug("Enter");

        if (item != null) {
            item.setCreateTime(Calendar.getInstance());
            item.setUpdateTime(item.getCreateTime());
            entityManager.persist(item);
        }

        if (log.isDebugEnabled())
            log.debug("Exit");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#update(org.nhindirect.config.store.Address)
     */
    @Transactional(readOnly = false)
    public void update(Address item) {
        if (log.isDebugEnabled())
            log.debug("Enter");

        if (item != null) {
            Address inDb = entityManager.find(Address.class, item.getId());
            inDb.setDisplayName(item.getDisplayName());
            inDb.setEndpoint(item.getEndpoint());
            inDb.setDomain(item.getDomain());
            inDb.setEmailAddress(item.getEmailAddress());
            inDb.setType(item.getType());
            inDb.setStatus(item.getStatus());
            inDb.setUpdateTime(Calendar.getInstance());
            entityManager.merge(inDb);
        }

        if (log.isDebugEnabled())
            log.debug("Exit");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#save(org.nhindirect.config.store.Address)
     */
    @Transactional(readOnly = false)
    public void save(Address item) {
        update(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#delete(java.lang.String)
     */
    // TODO Check to see if this address is a postmaster Address and remove it
    // from Domain prior to deletion
    @Transactional(readOnly = false)
    public void delete(String name) {
        if (log.isDebugEnabled())
            log.debug("Enter");
        
        int count = 0;
        if (name != null) {
            Query delete = entityManager.createQuery("DELETE FROM Address a WHERE UPPER(a.emailAddress) = ?1");
            delete.setParameter(1, name.toUpperCase(Locale.getDefault()));
            count = delete.executeUpdate();
        }

        if (log.isDebugEnabled())
            log.debug("Exit: " + count + " records deleted");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#listAddresses(java.lang.String, int)
     */
    @Transactional(readOnly = true)
    public List<Address> listAddresses(String name, int count) {
        // TODO Auto-generated method stub
        return null;
    }

    /* 
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#get(java.lang.String)
     */
    @Transactional(readOnly = true)
    public Address get(String name) {
        if (log.isDebugEnabled())
            log.debug("Enter");

        Address result = null;

        if (name != null) {
            Query select = entityManager.createQuery("SELECT DISTINCT a from Address a d WHERE UPPER(a.emailAddress) = ?1");
            result = (Address) select.setParameter(1, name.toUpperCase(Locale.getDefault())).getSingleResult();
        }

        if (log.isDebugEnabled())
            log.debug("Exit");
        return result;
    }

    /* 
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#listAddresses(java.util.List, org.nhindirect.config.store.EntityStatus)
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
    public List<Address> listAddresses(List<String> names, EntityStatus status) {
        if (log.isDebugEnabled())
            log.debug("Enter");

        List<Address> result = null;
        Query select = null;
        if (names != null) {
            StringBuffer nameList = new StringBuffer("(");
            for (String aName : names) {
                if (nameList.length() > 1) {
                    nameList.append(", ");
                }
                nameList.append("'").append(aName.toUpperCase(Locale.getDefault())).append("'");
            }
            nameList.append(")");
            String query = "SELECT a from Address a WHERE UPPER(a.emailAddress) IN " + nameList.toString();

            if (status != null) {
                select = entityManager.createQuery(query + " AND a.status = ?1");
                select.setParameter(1, status);
            } else {
                select = entityManager.createQuery(query);
            }
        } else {
            if (status != null) {
                select = entityManager.createQuery("SELECT a from Address a WHERE a.status = ?1");
                select.setParameter(1, status);
            } else {
                select = entityManager.createQuery("SELECT a from Address a");
            }

        }

        @SuppressWarnings("rawtypes")
        List rs = select.getResultList();
        if ((rs.size() != 0) && (rs.get(0) instanceof Address)) {
            result = (List<Address>) rs;
        } else {
            result = new ArrayList<Address>();
        }

        if (log.isDebugEnabled())
            log.debug("Exit");
        return result;
    }

    /* 
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AddressDao#getByDomain(org.nhindirect.config.store.Domain, org.nhindirect.config.store.EntityStatus)
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
    public List<Address> getByDomain(Domain domain, EntityStatus status) {
        if (log.isDebugEnabled())
            log.debug("Enter");

        List<Address> result = null;
        Query select = null;
        if (domain != null) {
            String query = "SELECT a from Address a WHERE a.domain = ?1";

            if (status != null) {
                select = entityManager.createQuery(query + " AND a.status = ?2");
                select.setParameter(1, domain);
                select.setParameter(2, status);
            } else {
                select = entityManager.createQuery(query);
                select.setParameter(1, domain);
            }
        } else {
            if (status != null) {
                select = entityManager.createQuery("SELECT a from Address a WHERE a.status = ?1");
                select.setParameter(1, status);
            } else {
                select = entityManager.createQuery("SELECT a from Address a");
            }
        }

        @SuppressWarnings("rawtypes")
        List rs = select.getResultList();
        if ((rs.size() != 0) && (rs.get(0) instanceof Address)) {
            result = (List<Address>) rs;
        } else {
            result = new ArrayList<Address>();
        }

        if (log.isDebugEnabled())
            log.debug("Exit");
        return result;
    }

    /**
     * Get the value of entityManager.
     * 
     * @return the value of entityManager.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Set the value of entityManager.
     * 
     * @param entityManager
     *            The value of entityManager.
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
