package org.nhind.config;

public class ConfigurationServiceProxy implements org.nhind.config.ConfigurationService {
  private String _endpoint = null;
  private org.nhind.config.ConfigurationService configurationService = null;
  
  public ConfigurationServiceProxy() {
    _initConfigurationServiceProxy();
  }
  
  public ConfigurationServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initConfigurationServiceProxy();
  }
  
  private void _initConfigurationServiceProxy() {
    try {
      configurationService = (new org.nhindirect.config.service.impl.ConfigurationServiceImplServiceLocator()).getConfigurationServiceImplPort();
      if (configurationService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)configurationService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)configurationService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (configurationService != null)
      ((javax.xml.rpc.Stub)configurationService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.nhind.config.ConfigurationService getConfigurationService() {
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService;
  }
  
  public void updateGroupAttributes(long policyGroupId, java.lang.String policyGroupName) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateGroupAttributes(policyGroupId, policyGroupName);
  }
  
  public org.nhind.config.Anchor getAnchor(java.lang.String owner, java.lang.String thumbprint, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getAnchor(owner, thumbprint, options);
  }
  
  public org.nhind.config.Certificate[] listCertificates(long lastCertificateId, int maxResutls, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.listCertificates(lastCertificateId, maxResutls, options);
  }
  
  public org.nhind.config.DnsRecord[] getDNSByType(int type) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSByType(type);
  }
  
  public org.nhind.config.Anchor[] getAnchors(long[] anchorId, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getAnchors(anchorId, options);
  }
  
  public org.nhind.config.CertPolicyGroup getPolicyGroupById(long policyGroupId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicyGroupById(policyGroupId);
  }
  
  public org.nhind.config.Domain[] getDomains(java.lang.String[] names, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDomains(names, status);
  }
  
  public org.nhind.config.CertPolicy[] getPolicies() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicies();
  }
  
  public void addAnchor(org.nhind.config.Anchor[] anchor) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addAnchor(anchor);
  }
  
  public void addSetting(java.lang.String name, java.lang.String value) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addSetting(name, value);
  }
  
  public void addPolicy(org.nhind.config.CertPolicy policy) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addPolicy(policy);
  }
  
  public org.nhind.config.Domain getDomain(java.lang.Long id) throws java.rmi.RemoteException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDomain(id);
  }
  
  public void updatePolicyAttributes(long policyId, java.lang.String policyName, org.nhind.config.PolicyLexicon policyLexicon, byte[] policyData) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updatePolicyAttributes(policyId, policyName, policyLexicon, policyData);
  }
  
  public org.nhind.config.TrustBundle getTrustBundleByName(java.lang.String bundleName) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getTrustBundleByName(bundleName);
  }
  
  public org.nhind.config.TrustBundle getTrustBundleById(long id) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getTrustBundleById(id);
  }
  
  public org.nhind.config.DnsRecord[] getDNSByNameAndType(java.lang.String name, int type) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSByNameAndType(name, type);
  }
  
  public void setCertificateStatusForOwner(java.lang.String owner, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.setCertificateStatusForOwner(owner, status);
  }
  
  public void removeDNS(org.nhind.config.DnsRecord[] records) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeDNS(records);
  }
  
  public void removeDomainById(java.lang.Long id) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeDomainById(id);
  }
  
  public org.nhind.config.TrustBundle[] getTrustBundles(boolean fetchAnchors) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getTrustBundles(fetchAnchors);
  }
  
  public org.nhind.config.Anchor[] getOutgoingAnchors(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getOutgoingAnchors(owner, options);
  }
  
  public void removeDNSByRecordIds(java.lang.Long[] recordIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeDNSByRecordIds(recordIds);
  }
  
  public org.nhind.config.DnsRecord[] getDNSByName(java.lang.String name) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSByName(name);
  }
  
  public void removePolicyUseFromGroup(long policyGroupReltnId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removePolicyUseFromGroup(policyGroupReltnId);
  }
  
  public void updateDomain(org.nhind.config.Domain domain) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateDomain(domain);
  }
  
  public org.nhind.config.Domain[] listDomains(java.lang.String names, int maxResults) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.listDomains(names, maxResults);
  }
  
  public org.nhind.config.Anchor[] listAnchors(java.lang.Long lastAnchorId, int maxResults, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.listAnchors(lastAnchorId, maxResults, options);
  }
  
  public void updateAddress(org.nhind.config.Address address) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateAddress(address);
  }
  
  public org.nhind.config.Setting getSettingByName(java.lang.String name) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getSettingByName(name);
  }
  
  public void addPolicyGroup(org.nhind.config.CertPolicyGroup policyGroup) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addPolicyGroup(policyGroup);
  }
  
  public void setCertificateStatus(long[] certificateIds, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.setCertificateStatus(certificateIds, status);
  }
  
  public org.nhind.config.CertPolicyGroup getPolicyGroupByName(java.lang.String policyGroupName) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicyGroupByName(policyGroupName);
  }
  
  public org.nhind.config.CertPolicyGroupDomainReltn[] getPolicyGroupDomainReltns() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicyGroupDomainReltns();
  }
  
  public void addDomain(org.nhind.config.Domain domain) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addDomain(domain);
  }
  
  public void deleteSetting(java.lang.String[] names) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.deleteSetting(names);
  }
  
  public void disassociatePolicyGroupFromDomains(long policyGroupId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.disassociatePolicyGroupFromDomains(policyGroupId);
  }
  
  public void associateTrustBundleToDomain(long domainId, long trustBundleId, boolean incoming, boolean outgoing) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.associateTrustBundleToDomain(domainId, trustBundleId, incoming, outgoing);
  }
  
  public org.nhind.config.Address[] listAddresss(java.lang.String lastEmailAddress, int maxResults) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.listAddresss(lastEmailAddress, maxResults);
  }
  
  public void updateDNS(long recordId, org.nhind.config.DnsRecord record) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateDNS(recordId, record);
  }
  
  public void setAnchorStatusForOwner(java.lang.String owner, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.setAnchorStatusForOwner(owner, status);
  }
  
  public void addDNS(org.nhind.config.DnsRecord[] records) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addDNS(records);
  }
  
  public void associatePolicyGroupToDomain(long domainId, long policyGroupId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.associatePolicyGroupToDomain(domainId, policyGroupId);
  }
  
  public boolean contains(org.nhind.config.Certificate cert) throws java.rmi.RemoteException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.contains(cert);
  }
  
  public void removeCertificatesForOwner(java.lang.String owner) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeCertificatesForOwner(owner);
  }
  
  public void refreshTrustBundle(long id) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.refreshTrustBundle(id);
  }
  
  public org.nhind.config.TrustBundleDomainReltn[] getTrustBundlesByDomain(long domainId, boolean fetchAnchors) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getTrustBundlesByDomain(domainId, fetchAnchors);
  }
  
  public void addAddress(org.nhind.config.Address[] address) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addAddress(address);
  }
  
  public org.nhind.config.Setting[] getAllSettings() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getAllSettings();
  }
  
  public void disassociatePolicyGroupsFromDomain(long domainId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.disassociatePolicyGroupsFromDomain(domainId);
  }
  
  public org.nhind.config.DnsRecord getDNSByRecordId(long recordId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSByRecordId(recordId);
  }
  
  public void updateTrustBundleSigningCertificate(long trustBundleIds, org.nhind.config.Certificate signingCert) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateTrustBundleSigningCertificate(trustBundleIds, signingCert);
  }
  
  public int getAddressCount() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getAddressCount();
  }
  
  public org.nhind.config.Setting[] getSettingsByNames(java.lang.String[] names) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getSettingsByNames(names);
  }
  
  public void removeAnchors(long[] anchorId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeAnchors(anchorId);
  }
  
  public void removeAddress(java.lang.String emailAddress) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeAddress(emailAddress);
  }
  
  public void deletePolicyGroups(java.lang.Long[] policyGroupIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.deletePolicyGroups(policyGroupIds);
  }
  
  public org.nhind.config.CertPolicyGroup[] getPolicyGroups() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicyGroups();
  }
  
  public org.nhind.config.Certificate[] getCertificates(long[] certificateIds, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getCertificates(certificateIds, options);
  }
  
  public org.nhind.config.Address[] getAddresss(java.lang.String[] emailAddress, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getAddresss(emailAddress, status);
  }
  
  public void disassociatePolicyGroupFromDomain(long domainId, long policyGroupId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.disassociatePolicyGroupFromDomain(domainId, policyGroupId);
  }
  
  public void updateSetting(java.lang.String name, java.lang.String value) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateSetting(name, value);
  }
  
  public void removeDomain(java.lang.String name) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeDomain(name);
  }
  
  public org.nhind.config.Anchor[] getIncomingAnchors(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getIncomingAnchors(owner, options);
  }
  
  public org.nhind.config.CertPolicy getPolicyById(long policyId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicyById(policyId);
  }
  
  public void addPolicyUseToGroup(long policyGroupId, long policyId, org.nhind.config.CertPolicyUse policyUse, boolean incoming, boolean outgoing) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addPolicyUseToGroup(policyGroupId, policyId, policyUse, incoming, outgoing);
  }
  
  public void removeDNSByRecordId(long recordId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeDNSByRecordId(recordId);
  }
  
  public org.nhind.config.Domain[] searchDomain(java.lang.String name, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.searchDomain(name, status);
  }
  
  public void addTrustBundle(org.nhind.config.TrustBundle bundle) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addTrustBundle(bundle);
  }
  
  public org.nhind.config.Certificate[] getCertificatesForOwner(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getCertificatesForOwner(owner, options);
  }
  
  public void disassociateTrustBundlesFromDomain(long domainId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.disassociateTrustBundlesFromDomain(domainId);
  }
  
  public org.nhind.config.CertPolicy getPolicyByName(java.lang.String policyName) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicyByName(policyName);
  }
  
  public void removeAnchorsForOwner(java.lang.String owner) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeAnchorsForOwner(owner);
  }
  
  public org.nhind.config.CertPolicyGroupDomainReltn[] getPolicyGroupsByDomain(long domainId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getPolicyGroupsByDomain(domainId);
  }
  
  public org.nhind.config.Anchor[] getAnchorsForOwner(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getAnchorsForOwner(owner, options);
  }
  
  public void deletePolicies(java.lang.Long[] policyIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.deletePolicies(policyIds);
  }
  
  public void removeCertificates(long[] certificateIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeCertificates(certificateIds);
  }
  
  public void disassociateTrustBundleFromDomain(long domainId, long trustBundleId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.disassociateTrustBundleFromDomain(domainId, trustBundleId);
  }
  
  public int getDomainCount() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDomainCount();
  }
  
  public int getDNSCount() throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSCount();
  }
  
  public void disassociateTrustBundleFromDomains(long trustBundleId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.disassociateTrustBundleFromDomains(trustBundleId);
  }
  
  public void addCertificates(org.nhind.config.Certificate[] certs) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.addCertificates(certs);
  }
  
  public org.nhind.config.DnsRecord[] getDNSByRecordIds(java.lang.Long[] recordIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSByRecordIds(recordIds);
  }
  
  public org.nhind.config.Certificate getCertificate(java.lang.String owner, java.lang.String thumbprint, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getCertificate(owner, thumbprint, options);
  }
  
  public void updateLastUpdateError(long trustBundleId, java.util.Calendar attemptTime, org.nhind.config.BundleRefreshError error) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateLastUpdateError(trustBundleId, attemptTime, error);
  }
  
  public void updateTrustBundleAttributes(long trustBundleId, java.lang.String trustBundleName, java.lang.String trustBundleURL, org.nhind.config.Certificate signingCert, int trustBundleRefreshInterval) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.updateTrustBundleAttributes(trustBundleId, trustBundleName, trustBundleURL, signingCert, trustBundleRefreshInterval);
  }
  
  public void deleteTrustBundles(java.lang.Long[] trustBundleIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.deleteTrustBundles(trustBundleIds);
  }
  
  
}