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

package org.nhindirect.config.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.TrustBundleService;
import org.nhindirect.config.store.BundleRefreshError;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.CertificateException;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.config.store.TrustBundleDomainReltn;
import org.nhindirect.config.store.dao.TrustBundleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Implementation of the TrustBundleService
 * @author Greg Meyer
 * @since 1.3
 */
@WebService(endpointInterface = "org.nhindirect.config.service.TrustBundleService")
public class TrustBundleServiceImpl implements TrustBundleService
{
    private static final Log log = LogFactory.getLog(TrustBundleServiceImpl.class);

	protected ProducerTemplate template;
    
    private TrustBundleDao dao;

    /**
	 * Initialization method.
	 */
    ///CLOVER:OFF
    public void init() 
    {
        log.info("TrustBundleServiceImpl initialized");
    }
    ///CLOVER:ON
    
    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<TrustBundle> getTrustBundles(boolean fetchAnchors)
			throws ConfigurationServiceException 
	{
		final Collection<TrustBundle> bundles = dao.getTrustBundles();
		
		if (!fetchAnchors)
		{
			for (TrustBundle bundle : bundles)
				bundle.setTrustBundleAnchors(new ArrayList<TrustBundleAnchor>());
		}
		
		return bundles;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public TrustBundle getTrustBundleByName(String bundleName)
			throws ConfigurationServiceException 
	{
		return dao.getTrustBundleByName(bundleName);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public TrustBundle getTrustBundleById(long id)
			throws ConfigurationServiceException 
	{
		return dao.getTrustBundleById(id);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void addTrustBundle(TrustBundle bundle)
			throws ConfigurationServiceException 
	{
		dao.addTrustBundle(bundle);
		
		// the trust bundle does not contain any of the anchors
		// they must be fetched from the URL... use the
		// refresh route to force downloading the anchors
		template.sendBody(bundle);
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public void refreshTrustBundle(@WebParam(name = "id") long id) throws ConfigurationServiceException
    {
		final TrustBundle bundle = dao.getTrustBundleById(id);
		
		if (bundle != null)
			template.sendBody(bundle);
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public void updateLastUpdateError(long trustBundleId, Calendar attemptTime,
			BundleRefreshError error) throws ConfigurationServiceException 
	{
		dao.updateLastUpdateError(trustBundleId, attemptTime, error);		
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void deleteTrustBundles(long[] trustBundleIds)
			throws ConfigurationServiceException 
	{
		dao.deleteTrustBundles(trustBundleIds);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void updateTrustBundleSigningCertificate(long trustBundleId,
			Certificate signingCert) throws ConfigurationServiceException 
	{
		try
		{
			dao.updateTrustBundleSigningCertificate(trustBundleId, signingCert.toCredential().getCert());	
		}
		catch (CertificateException e)
		{
			throw new ConfigurationServiceException(e);
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public void associateTrustBundleToDomain(long domainId, long trustBundleId,  boolean incoming,
    		boolean outgoing)
			throws ConfigurationServiceException 
	{
    	dao.associateTrustBundleToDomain(domainId, trustBundleId, incoming, outgoing);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void disassociateTrustBundleFromDomain(long domainId,
			long trustBundleId) throws ConfigurationServiceException 
	{
		dao.disassociateTrustBundleFromDomain(domainId, trustBundleId);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void disassociateTrustBundlesFromDomain(long domainId)
			throws ConfigurationServiceException 
	{
		dao.disassociateTrustBundlesFromDomain(domainId);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void disassociateTrustBundleFromDomains(long trustBundleId)
			throws ConfigurationServiceException 
	{
		dao.disassociateTrustBundleFromDomains(trustBundleId);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<TrustBundleDomainReltn> getTrustBundlesByDomain(long domainId, boolean fetchAnchors)
			throws ConfigurationServiceException 
	{
		final Collection<TrustBundleDomainReltn> bundles = dao.getTrustBundlesByDomain(domainId);
		
		if (!fetchAnchors)
		{
			for (TrustBundleDomainReltn bundle : bundles)
				bundle.getTrustBundle().setTrustBundleAnchors(new ArrayList<TrustBundleAnchor>());
		}
		
		return bundles;
		
	}
            
	/**
     * Set the value of the DNSDao object.
     * 
     * @param dao
     *            the value of the DNSDao object.
     */
    @Autowired
    public void setDao(TrustBundleDao dao) 
    {
        this.dao = dao;
    }

    /**
     * Return the value of the DNSDao object.
     * 
     * @return the value of the DNSDao object.
     */
    ///CLOVER:OFF
    public TrustBundleDao getDao() 
    {
        return dao;
    }
    ///CLOVER:ON
    
    /**
     * Sets the camel {@link ProducerTemplate} object for bundle refresh operations.
     * @param template
     */
    @Autowired
    @Qualifier("bundleRefresh")
    public void setTemplate(ProducerTemplate template) 
    {
        this.template = template;
    }
    
}
