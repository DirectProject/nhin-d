package org.nhindirect.config.service;
/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Patrick Pyette  ppyette@inpriva.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Collection;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.Certificate;

public interface CertificateService {
	
	//TODO Should X509Certificate actually be X509CertificateEx? 
	
	@WebMethod(operationName = "addCertificates", action = "urn:AddCertificates")
	void addCertificates(@WebParam(name = "certs") Collection<Certificate> certs) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getCertificate", action = "urn:GetCertificate")
	Certificate getCertificate(@WebParam(name = "owner") String owner, 
			                       @WebParam(name = "thumbprint") String thumbprint, 
			                       @WebParam(name = "options") CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getCertificates", action = "urn:GetCertificates")
	Collection<Certificate> getCertificates(@WebParam(name = "certificateIds") Collection<Long> certificateIds, 
			                                    @WebParam(name = "options") CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getCertificatesForOwner", action = "urn:GetCertificatesForOwner")
	Collection<Certificate> getCertificatesForOwner(@WebParam(name = "owner") String owner, 
			                                            @WebParam(name = "options") CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "setCertificateStatus", action = "urn:SetCertificateStatus")
	void setCertificateStatus(@WebParam(name = "certificateIds") Collection<Long> certificateIds, 
			                  @WebParam(name = "status") EntityStatus status) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "setCertificateStatusForOwner", action = "urn:SetCertificateStatusForOwner")
	void setCertificateStatusForOwner(@WebParam(name = "owner") String owner, 
			                          @WebParam(name = "status") EntityStatus status) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "removeCertificates", action = "urn:RemoveCertificates")
	void removeCertificates(@WebParam(name = "certificateIds") Collection<Long> certificateIds) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "removeCertificatesForOwner", action = "urn:RemoveCertificatesForOwner")
	void removeCertificatesForOwner(@WebParam(name = "owner") String owner) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "listCertificates", action = "urn:ListCertificates")
	Collection<Certificate> listCertificates(@WebParam(name = "lastCertificateId") long lastCertificateId, 
			                                     @WebParam(name = "maxResutls") int maxResults, 
			                                     @WebParam(name = "options") CertificateGetOptions options) throws ConfigurationServiceException;
    
    /**
     * Determines if a certificate exists in the certificate store.  Although not specific in the interface
     * definition, certificate thumbprinting is recommended for certificate searching.
     * @param cert The certificate to search for.
     * @return True if the certificate exist in the store.  False otherwise.
     */
    @WebMethod(operationName = "contains", action = "urn:Contains")
	public boolean contains(@WebParam(name = "cert") Certificate cert);        
        
}
