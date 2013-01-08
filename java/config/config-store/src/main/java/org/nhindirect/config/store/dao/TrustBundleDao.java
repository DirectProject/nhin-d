package org.nhindirect.config.store.dao;

import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collection;

import javax.jws.WebParam;

import org.nhindirect.config.store.BundleRefreshError;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.config.store.TrustBundleDomainReltn;

public interface TrustBundleDao 
{
	public Collection<TrustBundle> getTrustBundles() throws ConfigurationStoreException;
	
	public TrustBundle getTrustBundleByName(String bundleName) throws ConfigurationStoreException;	

	public TrustBundle getTrustBundleById(long id) throws ConfigurationStoreException;		
	
	public void addTrustBundle(TrustBundle bundle) throws ConfigurationStoreException;
	
	public void updateTrustBundleAnchors(long trustBundleId, Calendar attemptTime, Collection<TrustBundleAnchor> newAnchorSet,
			String bundleCheckSum) throws ConfigurationStoreException;
	
	public void updateLastUpdateError(long trustBundleId, Calendar attemptTime, BundleRefreshError error) throws ConfigurationStoreException;
	
	public void deleteTrustBundles(long[] trustBundleIds) throws ConfigurationStoreException;
	
	public void updateTrustBundleSigningCertificate(long trustBundleId, X509Certificate signingCert) throws ConfigurationStoreException;
	
	public void associateTrustBundleToDomain(long domainId, long trustBundleId, boolean incoming,
    		boolean outgoing) throws ConfigurationStoreException;
	
	public void disassociateTrustBundleFromDomain(long domainId, long trustBundleId) throws ConfigurationStoreException;	
	
	public void disassociateTrustBundlesFromDomain(long domainId) throws ConfigurationStoreException;	
	
	public void disassociateTrustBundleFromDomains(long trustBundleId) throws ConfigurationStoreException;		
	
	public Collection<TrustBundleDomainReltn> getTrustBundlesByDomain(long domainId) throws ConfigurationStoreException;		
}
