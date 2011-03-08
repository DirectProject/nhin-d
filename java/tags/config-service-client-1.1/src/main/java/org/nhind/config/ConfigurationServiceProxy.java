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
  
  public org.nhind.config.Domain[] getDomains(java.lang.String[] names, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDomains(names, status);
  }
  
  public org.nhind.config.Domain getDomain(java.lang.Long id) throws java.rmi.RemoteException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDomain(id);
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
  
  public org.nhind.config.DnsRecord[] getDNSByName(java.lang.String name) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSByName(name);
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
  
  public void setCertificateStatus(long[] certificateIds, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.setCertificateStatus(certificateIds, status);
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
  
  public org.nhind.config.Address[] listAddresss(java.lang.String lastEmailAddress, int maxResults) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.listAddresss(lastEmailAddress, maxResults);
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
  
  public org.nhind.config.DnsRecord getDNSByRecordId(long recordId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getDNSByRecordId(recordId);
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
  
  public org.nhind.config.Anchor[] getIncomingAnchors(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getIncomingAnchors(owner, options);
  }
  
  public org.nhind.config.Domain[] searchDomain(java.lang.String name, org.nhind.config.EntityStatus status) throws java.rmi.RemoteException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.searchDomain(name, status);
  }
  
  public void removeDNSByRecordId(long recordId) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeDNSByRecordId(recordId);
  }
  
  public org.nhind.config.Certificate[] getCertificatesForOwner(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getCertificatesForOwner(owner, options);
  }
  
  public void removeCertificates(long[] certificateIds) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeCertificates(certificateIds);
  }
  
  public void removeAnchorsForOwner(java.lang.String owner) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    configurationService.removeAnchorsForOwner(owner);
  }
  
  public org.nhind.config.Anchor[] getAnchorsForOwner(java.lang.String owner, org.nhind.config.CertificateGetOptions options) throws java.rmi.RemoteException, org.nhind.config.ConfigurationServiceException{
    if (configurationService == null)
      _initConfigurationServiceProxy();
    return configurationService.getAnchorsForOwner(owner, options);
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
  
  
}