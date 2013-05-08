/**
 * ConfigurationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public interface ConfigurationService extends java.rmi.Remote {
    public org.nhind.config.Anchor getAnchor(java.lang.String owner, java.lang.String thumbprint, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Certificate[] listCertificates(long lastCertificateId, int maxResutls, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.DnsRecord[] getDNSByType(int type) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Anchor[] getAnchors(long[] anchorId, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Domain[] getDomains(java.lang.String[] names, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Domain getDomain(java.lang.Long id) throws java.rmi.RemoteException;
    public void addAnchor(org.nhind.config.Anchor[] anchor) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.TrustBundle getTrustBundleByName(java.lang.String bundleName) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void addSetting(java.lang.String name, java.lang.String value) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.TrustBundle getTrustBundleById(long id) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.DnsRecord[] getDNSByNameAndType(java.lang.String name, int type) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void setCertificateStatusForOwner(java.lang.String owner, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeDNS(org.nhind.config.DnsRecord[] records) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeDomainById(java.lang.Long id) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.TrustBundle[] getTrustBundles(boolean fetchAnchors) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Anchor[] getOutgoingAnchors(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeDNSByRecordIds(java.lang.Long[] recordIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.DnsRecord[] getDNSByName(java.lang.String name) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void updateDomain(org.nhind.config.Domain domain) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Domain[] listDomains(java.lang.String names, int maxResults) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Anchor[] listAnchors(java.lang.Long lastAnchorId, int maxResults, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void updateAddress(org.nhind.config.Address address) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Setting getSettingByName(java.lang.String name) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void setCertificateStatus(long[] certificateIds, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void addDomain(org.nhind.config.Domain domain) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void deleteSetting(java.lang.String[] names) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void updateDNS(long recordId, org.nhind.config.DnsRecord record) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void setAnchorStatusForOwner(java.lang.String owner, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void addDNS(org.nhind.config.DnsRecord[] records) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void associateTrustBundleToDomain(long domainId, long trustBundleId, boolean incoming, boolean outgoing) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public boolean contains(org.nhind.config.Certificate cert) throws java.rmi.RemoteException;
    public void removeCertificatesForOwner(java.lang.String owner) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Address[] listAddresss(java.lang.String lastEmailAddress, int maxResults) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void refreshTrustBundle(long id) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.TrustBundleDomainReltn[] getTrustBundlesByDomain(long domainId, boolean fetchAnchors) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void addAddress(org.nhind.config.Address[] address) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Setting[] getAllSettings() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.DnsRecord getDNSByRecordId(long recordId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public int getAddressCount() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void updateTrustBundleSigningCertificate(long trustBundleIds, org.nhind.config.Certificate signingCert) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Setting[] getSettingsByNames(java.lang.String[] names) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeAnchors(long[] anchorId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeAddress(java.lang.String emailAddress) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void updateSetting(java.lang.String name, java.lang.String value) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeDomain(java.lang.String name) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Certificate[] getCertificates(long[] certificateIds, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Address[] getAddresss(java.lang.String[] emailAddress, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Anchor[] getIncomingAnchors(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeDNSByRecordId(long recordId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Domain[] searchDomain(java.lang.String name, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException;
    public void addTrustBundle(org.nhind.config.TrustBundle bundle) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Certificate[] getCertificatesForOwner(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeCertificates(long[] certificateIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void disassociateTrustBundlesFromDomain(long domainId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void disassociateTrustBundleFromDomain(long domainId, long trustBundleId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void removeAnchorsForOwner(java.lang.String owner) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Anchor[] getAnchorsForOwner(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public int getDomainCount() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public int getDNSCount() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void disassociateTrustBundleFromDomains(long trustBundleId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void addCertificates(org.nhind.config.Certificate[] certs) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.DnsRecord[] getDNSByRecordIds(java.lang.Long[] recordIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public org.nhind.config.Certificate getCertificate(java.lang.String owner, java.lang.String thumbprint, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void updateLastUpdateError(long trustBundleId, java.util.Calendar attemptTime, org.nhind.config.BundleRefreshError error) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void updateTrustBundleAttributes(long trustBundleId, java.lang.String trustBundleName, java.lang.String trustBundleURL, org.nhind.config.Certificate signingCert, int trustBundleRefreshInterval) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
    public void deleteTrustBundles(java.lang.Long[] trustBundleIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException;
}
