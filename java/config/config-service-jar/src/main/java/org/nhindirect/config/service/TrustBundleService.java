package org.nhindirect.config.service;

import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collection;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.nhindirect.config.store.BundleRefreshError;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;

public interface TrustBundleService 
{
    @WebMethod(operationName = "getTrustBundles", action = "urn:GetTrustBundles")
    Collection<TrustBundle> getTrustBundles(@WebParam(name = "fetchAnchors") boolean fetchAnchors) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "getTrustBundleByName", action = "urn:GetTrustBundleByName")
    public TrustBundle getTrustBundleByName(@WebParam(name = "bundleName")  String bundleName) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "getTrustBundleById", action = "urn:GetTrustBundleById")
    public TrustBundle getTrustBundleById(@WebParam(name = "id")  long id) throws ConfigurationServiceException;  
    
    @WebMethod(operationName = "addTrustBundle", action = "urn:AddTrustBundle")
    public void addTrustBundle(@WebParam(name = "bundle") TrustBundle bundle) throws ConfigurationServiceException;   
    
    @WebMethod(operationName = "updateTrustBundleAnchors", action = "urn:UpdateTrustBundleAnchors")
    public void updateTrustBundleAnchors(@WebParam(name = "trustBundleId") long trustBundleId, 
    		@WebParam(name = "attemptTime") Calendar attemptTime, @WebParam(name = "newAnchorSet") Collection<TrustBundleAnchor> newAnchorSet) 
    				throws ConfigurationServiceException;        
    
    @WebMethod(operationName = "updateLastUpdateError", action = "urn:UpdateLastUpdateError")
    public void updateLastUpdateError(@WebParam(name = "trustBundleId") long trustBundleId, 
    		@WebParam(name = "attemptTime") Calendar attemptTime, @WebParam(name = "error") BundleRefreshError error)  throws ConfigurationServiceException;   
    
    @WebMethod(operationName = "deleteTrustBundles", action = "urn:DeleteTrustBundles")
    public void deleteTrustBundles(@WebParam(name = "trustBundleIds") long[] trustBundleIds) throws ConfigurationServiceException;  
    
    @WebMethod(operationName = "updateTrustBundleSigningCertificate", action = "urn:UpdateTrustBundleSigningCertificate")
    public void updateTrustBundleSigningCertificate(@WebParam(name = "trustBundleIds") long trustBundleId, 
    		@WebParam(name = "signingCert") X509Certificate signingCert) throws ConfigurationServiceException;      
}
