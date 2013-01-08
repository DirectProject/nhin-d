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

	@Override
	public TrustBundle getTrustBundleByName(String bundleName)
			throws ConfigurationServiceException 
	{
		return dao.getTrustBundleByName(bundleName);
	}

	@Override
	public TrustBundle getTrustBundleById(long id)
			throws ConfigurationServiceException 
	{
		return dao.getTrustBundleById(id);
	}

	@Override
	public void addTrustBundle(TrustBundle bundle)
			throws ConfigurationServiceException 
	{
		dao.addTrustBundle(bundle);
		template.sendBody(bundle);
	}

	@Override
    public void refreshTrustBundle(@WebParam(name = "id") long id) throws ConfigurationServiceException
    {
		final TrustBundle bundle = dao.getTrustBundleById(id);
		
		if (bundle != null)
			template.sendBody(bundle);
    }

	@Override
	public void updateLastUpdateError(long trustBundleId, Calendar attemptTime,
			BundleRefreshError error) throws ConfigurationServiceException 
	{
		dao.updateLastUpdateError(trustBundleId, attemptTime, error);		
	}

	@Override
	public void deleteTrustBundles(long[] trustBundleIds)
			throws ConfigurationServiceException 
	{
		dao.deleteTrustBundles(trustBundleIds);
	}

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

	
	
    @Override
	public void associateTrustBundleToDomain(long domainId, long trustBundleId,  boolean incoming,
    		boolean outgoing)
			throws ConfigurationServiceException 
	{
    	dao.associateTrustBundleToDomain(domainId, trustBundleId, incoming, outgoing);
	}

	@Override
	public void disassociateTrustBundleFromDomain(long domainId,
			long trustBundleId) throws ConfigurationServiceException 
	{
		dao.disassociateTrustBundleFromDomain(domainId, trustBundleId);
	}

	@Override
	public void disassociateTrustBundlesFromDomain(long domainId)
			throws ConfigurationServiceException 
	{
		dao.disassociateTrustBundlesFromDomain(domainId);
	}

	@Override
	public void disassociateTrustBundleFromDomains(long trustBundleId)
			throws ConfigurationServiceException 
	{
		dao.disassociateTrustBundleFromDomains(trustBundleId);
	}

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
    
    @Autowired
    @Qualifier("bundleRefresh")
    public void setTemplate(ProducerTemplate template) 
    {
        this.template = template;
    }
    
}
