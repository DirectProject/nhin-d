package org.nhind.config.rest;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public interface TrustBundleService 
{
	public Collection<TrustBundle> getTrustBundles(boolean fetchAnchors) throws ServiceException;
	
	public Collection<TrustBundle> getTrustBundlesByDomain(String domainName, boolean fetchAnchors) throws ServiceException;	
	
	public TrustBundle getTrustBundle(String bundleName) throws ServiceException;
	
	public void addTrustBundle(TrustBundle bundle) throws ServiceException;
	
	public void refreshTrustBundle(String bundleName) throws ServiceException;
	
	public void deleteTrustBundle(String bundleName) throws ServiceException;
	
	public void updateSigningCert(String bundleName, X509Certificate cert) throws ServiceException;
	
	public void updateTrustBundleAttributes(String bundleName, TrustBundle bundleData) throws ServiceException;
	
	public void associateTrustBundleToDomain(String bundleName, String domainName, boolean incoming, boolean outgoing) throws ServiceException;
	
	public void disassociateTrustBundleFromDomain(String bundleName, String domainName) throws ServiceException;
	
	public void disassociateTrustBundlesFromDomain(String domainName) throws ServiceException;
	
	public void disassociateTrustBundleFromDomains(String bundleName) throws ServiceException;
}
