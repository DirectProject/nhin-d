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

import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.config.model.exceptions.CertificateConversionException;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.CertificateException;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.CertificateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementing class for Certificate DAO methods.
 * 
 * @author ppyette
 */
@Repository
public class CertificateDaoImpl implements CertificateDao 
{
    static
    {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
	
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Autowired(required = false)
    private KeyStoreProtectionManager kspMgr;
    
    private static final Log log = LogFactory.getLog(CertificateDaoImpl.class);
    
    public void setKeyStoreProtectionManager(KeyStoreProtectionManager mgr)
    {
    	this.kspMgr = mgr;
    }
    
    public void setEntityManager(EntityManager entityManager)
    {
    	this.entityManager = entityManager;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#load(java.lang.String, java.lang.String)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public Certificate load(String owner, String thumbprint) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");

        List<Certificate> result = null;
        Query select = null;
        if (owner == null && thumbprint == null) 
        {
        	select = entityManager.createQuery("SELECT c from Certificate c");            
        } 
        else if (owner != null && thumbprint == null)
        {
        	select = entityManager.createQuery("SELECT c from Certificate c WHERE UPPER(c.owner) = ?1");
        	select.setParameter(1, owner.toUpperCase(Locale.getDefault()));	
        }
        else if (owner == null && thumbprint != null)
        {
        	select = entityManager.createQuery("SELECT c from Certificate c WHERE c.thumbprint = ?1");
        	select.setParameter(1, thumbprint);	
        }
        else
        {
        	select = entityManager.createQuery("SELECT c from Certificate c WHERE c.thumbprint = ?1 and UPPER(c.owner) = ?2");
        	select.setParameter(1, thumbprint);
        	select.setParameter(2, owner.toUpperCase(Locale.getDefault()));	        	
        }

        List rs = select.getResultList();
        if ((rs.size() != 0) && (rs.get(0) instanceof Certificate)) {
            result = (List<Certificate>) rs;
        }
        else 
        	return null;
        
        for (Certificate cert : result)
        	stripP12Protection(cert);
        
        if (log.isDebugEnabled())
            log.debug("Exit");
        
        return result.iterator().next();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#list(java.util.List)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public List<Certificate> list(List<Long> idList) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");
        
        if (idList == null || idList.size() == 0)
        	return Collections.emptyList();
 
        List<Certificate> result = Collections.emptyList();
        
        Query select = null;
        StringBuffer ids = new StringBuffer("(");
        for (Long id : idList) 
        {
            if (ids.length() > 1) 
            {
            	ids.append(", ");
            }
            ids.append(id);
        }
        ids.append(")");
        String query = "SELECT c from Certificate c WHERE c.id IN " + ids.toString();
 
        select = entityManager.createQuery(query);

		List rs = select.getResultList();
        if (rs != null && (rs.size() != 0) && (rs.get(0) instanceof Certificate)) 
        {
            result = (List<Certificate>) rs;
        }

        for (Certificate cert : result)
        	stripP12Protection(cert);
        
        if (log.isDebugEnabled())
            log.debug("Exit");
       
        return result;    	

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#list(java.lang.String)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public List<Certificate> list(String owner) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");

        List<Certificate> result = Collections.emptyList();
        Query select = null;
        if (owner == null) 
        {
        	select = entityManager.createQuery("SELECT c from Certificate c");            
        } 
        else if (owner != null)
        {
        	select = entityManager.createQuery("SELECT c from Certificate c WHERE UPPER(c.owner) = ?1");
        	select.setParameter(1, owner.toUpperCase(Locale.getDefault()));	
        }

        List rs = select.getResultList();
        if ((rs.size() != 0) && (rs.get(0) instanceof Certificate)) {
            result = (List<Certificate>) rs;
        }
        
        for (Certificate cert : result)
        	stripP12Protection(cert);
        
        if (log.isDebugEnabled())
            log.debug("Exit");
         
        return result;
    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#save(org.nhindirect.config.store.Certificate)
     */
    @Transactional(readOnly = false)
    public void save(Certificate cert) 
    {
    	save(Arrays.asList(cert));
    }

    private Certificate stripP12Protection(Certificate cert)
    {
    	
    	if (cert.isPrivateKey() && kspMgr != null)
    	{
    		final char[] emptyProtection = "".toCharArray();
    		try
    		{
    			if (CertUtils.toCertContainer(cert.getData()) != null)
    				return cert;
    		}
    		catch (CertificateConversionException e)
    		{
    			// no-op.. this just means that the certificate is probably protected by pass phrases
    			// need to convert
    		}
    		
			try
			{
	    		final String oldKeystorePassPhrase = new String(kspMgr.getKeyStoreProtectionKey().getEncoded());
				final String oldPrivateKeyPassPhrase = new String(kspMgr.getKeyStoreProtectionKey().getEncoded());
	    		
	    		// decrypt the key store and private key
				final byte[] data = CertUtils.changePkcs12Protection(cert.getData(), oldKeystorePassPhrase.toCharArray(), 
								oldPrivateKeyPassPhrase.toCharArray(), emptyProtection, emptyProtection);
				
				cert.setData(data);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Error stripping P12 protection data", e);
			}
    	}

    	return cert;  // this is just a plain jane X509 Certificate
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#save(java.util.List)
     */
    @Transactional(readOnly = false)
    public void save(List<Certificate> certList) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");

        if (certList != null && certList.size() > 0)
        {
        	for (Certificate cert : certList)
        	{
        		cert.setCreateTime(Calendar.getInstance());
        	
	        	try
	        	{
	        		CertUtils.CertContainer container = null;
	        		X509Certificate xcert = null;
	        		try
	        		{
	        			// this might be an X509Certificate or a P12 key store.. assume there is no protection for P12 key stores... 
	        			container = CertUtils.toCertContainer(cert.getData());
	        			xcert = container.getCert();
	        		}
	        		catch (Exception e)
	        		{
	        			// probably not a certificate but an IPKIX URL
	        		}
	        		
	        		if (cert.getValidStartDate() == null && xcert != null)
	        		{
	        			Calendar startDate = Calendar.getInstance();
	        			startDate.setTime(xcert.getNotBefore());
	        			cert.setValidStartDate(startDate);
	        		}
	        		if (cert.getValidEndDate() == null && xcert != null)
	        		{
	        			Calendar endDate = Calendar.getInstance();
	        			endDate.setTime(xcert.getNotAfter());
	        			cert.setValidEndDate(endDate);
	        		}
	
	        		if (cert.getStatus() == null)
	        			cert.setStatus(EntityStatus.NEW);
	        		
	        		cert.setPrivateKey(container != null && container.getKey() != null);
	        		
	        		// if the key store protection manager is set and this is a P12 file, convert the cert data into a protected P12 file
	        		if (cert.isPrivateKey() && kspMgr != null)
	        		{
						try
						{
							final String newKeystorePassPhrase = new String(kspMgr.getKeyStoreProtectionKey().getEncoded());
							final String newPrivateKeyPassPhrase = new String(kspMgr.getPrivateKeyProtectionKey().getEncoded());
							
							cert.setRawData(CertUtils.changePkcs12Protection(cert.getData(), "".toCharArray(), "".toCharArray(), 
									newKeystorePassPhrase.toCharArray(), newPrivateKeyPassPhrase.toCharArray()));
						}
						catch (Exception e)
						{
							throw new RuntimeException("Error converting P12 to encrypted/protected format", e);
						}
	        		}
	        	}
	        	catch (CertificateException e)
	        	{
	        		
	        	}
        	

	        	if (log.isDebugEnabled())
	        		log.debug("Calling JPA to persist the Certificate");

	        	entityManager.persist(cert);
	            if (log.isDebugEnabled())
	                log.debug("Returned from JPA: Certificate ID=" + cert.getId());
	        	
        	}
            entityManager.flush();
        }

        if (log.isDebugEnabled())
            log.debug("Exit");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#setStatus(java.util.List, org.nhindirect.config.store.EntityStatus)
     */
    @Transactional(readOnly = false)
    public void setStatus(List<Long> certificateIDs, EntityStatus status) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");
        
        List<Certificate> certs = this.list(certificateIDs);
        if (certs == null || certs.size() == 0)
        	return;
        
        for (Certificate cert : certs)
        {
        	cert.setStatus(status);
        		entityManager.merge(cert);
        }
        	
        if (log.isDebugEnabled())
            log.debug("Exit");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#setStatus(java.lang.String, org.nhindirect.config.store.EntityStatus)
     */
    @Transactional(readOnly = false)        
    public void setStatus(String owner, EntityStatus status) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");
        
        List<Certificate> certs = list(owner);
        if (certs == null || certs.size() == 0)
        	return;
        
        for (Certificate cert : certs)
        {
        	cert.setStatus(status);
        		entityManager.merge(cert);
        }
        	
        if (log.isDebugEnabled())
            log.debug("Exit");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#delete(java.util.List)
     */
    @Transactional(readOnly = false)        
    public void delete(List<Long> idList) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");

        if (idList != null && idList.size() > 0)
        {
	        StringBuffer ids = new StringBuffer("(");
	        for (Long id : idList) 
	        {
	            if (ids.length() > 1) 
	            {
	            	ids.append(", ");
	            }
	            ids.append(id);
	        }
	        ids.append(")");
	        String query = "DELETE FROM Certificate c WHERE c.id IN " + ids.toString();
	        
	        int count = 0;
	        Query delete = entityManager.createQuery(query);
	        count = delete.executeUpdate();
	
	        if (log.isDebugEnabled())
	            log.debug("Exit: " + count + " certificate records deleted");
        }
        
        if (log.isDebugEnabled())
            log.debug("Exit");

    }
    
	/*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.CertificateDao#delete(java.lang.String)
     */
    @Transactional(readOnly = false)     
    public void delete(String owner) 
    {
        if (log.isDebugEnabled())
            log.debug("Enter");

        if (owner == null)
        	return;
        
        int count = 0;
        if (owner != null) 
        {
            Query delete = entityManager.createQuery("DELETE FROM Certificate c WHERE UPPER(c.owner) = ?1");
            delete.setParameter(1, owner.toUpperCase(Locale.getDefault()));
            count = delete.executeUpdate();
        }

        if (log.isDebugEnabled())
            log.debug("Exit: " + count + " certificate records deleted");
    }

    
    
}
