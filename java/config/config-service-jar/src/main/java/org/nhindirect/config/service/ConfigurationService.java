package org.nhindirect.config.service;

import javax.jws.WebService;

@WebService(name = "ConfigurationService", targetNamespace = "http://nhind.org/config")
public interface ConfigurationService extends AddressService, 
                                              DomainService,
		                                      CertificateService,  
		                                      AnchorService {

}
