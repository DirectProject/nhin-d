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

import java.security.cert.X509Certificate;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;


import org.nhindirect.config.service.ws.CertificateGetOptions;
import org.nhindirect.config.store.EntityStatus;

@WebService(name = "CertificateService", targetNamespace = "http://nhind.org/config")
public interface CertificateService {
	
	//TODO Should X509Certificate actually be X509CertificateEx? 
	
	@WebMethod(operationName = "addCertificates", action = "urn:AddCertificates")
	void addCertificates(List<X509Certificate> certs) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getCertificate", action = "urn:GetCertificate")
	X509Certificate getCertificate(String owner, String thumbprint, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getCertificates", action = "urn:GetCertificates")
	List<X509Certificate> getCertificates(List<Long> certificateIds, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getCertificatesForOwner", action = "urn:GetCertificatesForOwner")
	List<X509Certificate> getCertificatesForOwner(String owner, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "setCertificateStatus", action = "urn:SetCertificateStatus")
	void setCertificateStatus(List<Long> certificateIds, EntityStatus status) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "setCertificateStatusForOwner", action = "urn:SetCertificateStatusForOwner")
	void setCertificateStatusForOwner(String owner, EntityStatus status) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "removeCertificates", action = "urn:RemoveCertificates")
	void removeCertificates(List<Long> certificateIds) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "removeCertificatesForOwner", action = "urn:RemoveCertificatesForOwner")
	void removeCertificatesForOwner(String owner) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "ListCertificates", action = "urn:ListCertificates")
	List<X509Certificate> ListCertificates(long lastCertificateId, int maxResults, CertificateGetOptions options) throws ConfigurationServiceException;

}
