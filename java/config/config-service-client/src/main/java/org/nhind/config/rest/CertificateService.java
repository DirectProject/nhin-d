package org.nhind.config.rest;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;

public interface CertificateService 
{
	public Collection<Certificate> getAllCertificates() throws ServiceException;
	
	public Collection<Certificate> getCertificatesByOwner(String owner) throws ServiceException;
	
	public Certificate getCertificatesByOwnerAndThumbprint(String owner, String thumbprint) throws ServiceException;	
	
	public void addCertificate(Certificate cert) throws ServiceException;
	
	public void deleteCertificatesByIds(Collection<Long> ids) throws ServiceException;
	
	public void deleteCertificateByOwner(String owner) throws ServiceException;
	
}
