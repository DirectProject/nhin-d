package org.nhind.config.rest.impl;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.TrustBundleService;
import org.nhind.config.rest.impl.requests.AddTrustBundleRequest;
import org.nhind.config.rest.impl.requests.AssociateTrustBundleToDomainRequest;
import org.nhind.config.rest.impl.requests.DeleteTrustBundleRequest;
import org.nhind.config.rest.impl.requests.DisassociateTrustBundleFromDomainRequest;
import org.nhind.config.rest.impl.requests.DisassociateTrustBundleFromDomainsRequest;
import org.nhind.config.rest.impl.requests.DisassociateTrustBundlesFromDomainRequest;
import org.nhind.config.rest.impl.requests.GetTrustBundleRequest;
import org.nhind.config.rest.impl.requests.GetTrustBundlesByDomainRequest;
import org.nhind.config.rest.impl.requests.GetTrustBundlesRequest;
import org.nhind.config.rest.impl.requests.RefreshTrustBundleRequest;
import org.nhind.config.rest.impl.requests.UpdateSigningCertRequest;
import org.nhind.config.rest.impl.requests.UpdateTrustBundleAttributesRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class DefaultTrustBundleService extends AbstractSecuredService implements TrustBundleService
{
    public DefaultTrustBundleService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Collection<TrustBundle> getTrustBundles(boolean fetchAnchors) throws ServiceException 
	{
		return callWithRetry(new GetTrustBundlesRequest(httpClient, serviceURL, jsonMapper, securityManager, fetchAnchors));		
	}

	@Override
	public Collection<TrustBundle> getTrustBundlesByDomain(String domainName, boolean fetchAnchors) throws ServiceException 
	{
		return callWithRetry(new GetTrustBundlesByDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, domainName, fetchAnchors));	
	}

	@Override
	public TrustBundle getTrustBundle(String bundleName) throws ServiceException 
	{
		final Collection<TrustBundle> bundles =  callWithRetry(new GetTrustBundleRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName));	
		
		return (bundles.isEmpty()) ? null : bundles.iterator().next();	
	}

	@Override
	public void addTrustBundle(TrustBundle bundle) throws ServiceException 
	{
		callWithRetry(new AddTrustBundleRequest(httpClient, serviceURL, jsonMapper, securityManager, bundle));	
	}

	@Override
	public void refreshTrustBundle(String bundleName) throws ServiceException 
	{
		callWithRetry(new RefreshTrustBundleRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName));	
	}

	@Override
	public void deleteTrustBundle(String bundleName) throws ServiceException 
	{
		callWithRetry(new DeleteTrustBundleRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName));	
	}

	@Override
	public void updateSigningCert(String bundleName, X509Certificate cert) throws ServiceException 
	{
		callWithRetry(new UpdateSigningCertRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName, cert));	
	}

	@Override
	public void updateTrustBundleAttributes(String bundleName, TrustBundle bundleData) throws ServiceException 
	{
		callWithRetry(new UpdateTrustBundleAttributesRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName, bundleData));		
	}

	@Override
	public void associateTrustBundleToDomain(String bundleName, String domainName, boolean incoming, boolean outgoing) throws ServiceException 
	{
		callWithRetry(new AssociateTrustBundleToDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName, domainName,
				incoming, outgoing));
	}

	@Override
	public void disassociateTrustBundleFromDomain(String bundleName, String domainName) throws ServiceException 
	{
		callWithRetry(new DisassociateTrustBundleFromDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName, domainName));		
	}

	@Override
	public void disassociateTrustBundlesFromDomain(String domainName) throws ServiceException 
	{
		callWithRetry(new DisassociateTrustBundlesFromDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, domainName));
	}

	@Override
	public void disassociateTrustBundleFromDomains(String bundleName) throws ServiceException 
	{
		callWithRetry(new DisassociateTrustBundleFromDomainsRequest(httpClient, serviceURL, jsonMapper, securityManager, bundleName));
	}
    
    

}
