package org.nhindirect.config.service.impl;
/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Collection;

import javax.jws.WebService;
import javax.xml.ws.FaultAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.CertificateService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.CertificateDao;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.nhindirect.config.service.CertificateService")
public class CertificateServiceImpl implements CertificateService {
	
	//TODO Should Certificate be replaced with X509CertificateEx?
	
	private static final Log log = LogFactory.getLog(CertificateServiceImpl.class);
	
	private CertificateDao dao;
	
	@Autowired
	public void setDao(CertificateDao aDao) {
		dao = aDao;
	}
	
	public void init() {
		log.info("CertificateService initialized");
	}

	public void addCertificates(Collection<Certificate> certs) throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	public Certificate getCertificate(String owner, String thumbprint,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Certificate> getCertificates(Collection<Long> certIds, CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Certificate> getCertificatesForOwner(String owner,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCertificateStatus(Collection<Long> certificateIDs,
			EntityStatus status) throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	public void setCertificateStatusForOwner(String owner, EntityStatus status)  throws ConfigurationServiceException{
		// TODO Auto-generated method stub

	}

	public void removeCertificates(Collection<Long> certificateIds) throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	public void removeCertificatesForOwner(String owner) throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	public Collection<Certificate> listCertificates(long lastCertificateID,
			int maxResults, CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean contains(Certificate cert) {
		// TODO Auto-generated method stub
		return false;
	}

}
