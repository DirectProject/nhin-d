package org.nhind.config.rest.impl;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.impl.requests.AddCertificateRequest;
import org.nhind.config.rest.impl.requests.DeleteCertificateByOwnerRequest;
import org.nhind.config.rest.impl.requests.DeleteCertificatesByIdsRequest;
import org.nhind.config.rest.impl.requests.GetAllCertificatesRequest;
import org.nhind.config.rest.impl.requests.GetCertificatesByOwnerAndThumbprintRequest;
import org.nhind.config.rest.impl.requests.GetCertificatesByOwnerRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;

public class DefaultCertificateService  extends AbstractSecuredService implements CertificateService
{
    public DefaultCertificateService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Collection<Certificate> getAllCertificates() throws ServiceException 
	{
		return callWithRetry(new GetAllCertificatesRequest(httpClient, serviceURL, jsonMapper, securityManager));	
		
	}

	@Override
	public Collection<Certificate> getCertificatesByOwner(String owner) throws ServiceException 
	{
		
		return callWithRetry(new GetCertificatesByOwnerRequest(httpClient, serviceURL, jsonMapper, securityManager, owner));	
	}

	@Override
	public Certificate getCertificatesByOwnerAndThumbprint(
			String owner, String thumbprint) throws ServiceException 
	{
		final Collection<Certificate> certs =  callWithRetry(new GetCertificatesByOwnerAndThumbprintRequest(httpClient, serviceURL, jsonMapper, securityManager, 
				owner, thumbprint));	
		
		return (certs.isEmpty()) ? null : certs.iterator().next();
	}

	@Override
	public void addCertificate(Certificate cert) throws ServiceException 
	{
		 callWithRetry(new AddCertificateRequest(httpClient, serviceURL, jsonMapper, securityManager, cert));	
	}

	@Override
	public void deleteCertificatesByIds(Collection<Long> ids) throws ServiceException 
	{
		 callWithRetry(new DeleteCertificatesByIdsRequest(httpClient, serviceURL, jsonMapper, securityManager, ids));			
	}

	@Override
	public void deleteCertificateByOwner(String owner) throws ServiceException 
	{
		 callWithRetry(new DeleteCertificateByOwnerRequest(httpClient, serviceURL, jsonMapper, securityManager, owner));	
	}
}
