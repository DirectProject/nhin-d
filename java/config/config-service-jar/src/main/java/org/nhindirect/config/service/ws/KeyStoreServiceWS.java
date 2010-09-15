package org.nhindirect.config.service.ws;
/* 

Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Patrick Pyette	ppyette@inpriva.com
 
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
import java.util.Collection;

import javax.jws.WebService;
import javax.xml.ws.FaultAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.KeyStoreService;
import org.nhindirect.config.store.dao.CertificateDao;
import org.nhindirect.config.store.dao.DomainDao;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ppyette
 *
 */

@WebService(endpointInterface="org.nhindirect.config.service.KeyStoreService")
public class KeyStoreServiceWS implements KeyStoreService {
	
	private static final Log log = LogFactory.getLog(KeyStoreServiceWS.class);
	
	private CertificateDao dao;
	
	public void init() {
		log.info("KeyStoreService initialized");
	}
	
	@Autowired
	public void setDao(CertificateDao aDao) {
		dao = aDao;
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#getCertificates(java.lang.String)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public Collection<X509Certificate> getCertificates(String subjectName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#contains(java.security.cert.X509Certificate)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public boolean contains(X509Certificate cert) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#add(java.security.cert.X509Certificate)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public void add(X509Certificate cert) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#add(java.util.Collection)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public void add(Collection<X509Certificate> certs) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#remove(java.security.cert.X509Certificate)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public void remove(X509Certificate cert) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#remove(java.util.Collection)
	 */
	public void remove(Collection<X509Certificate> certs) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#remove(java.lang.String)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public void remove(String subjectName) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#update(java.security.cert.X509Certificate)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public void update(X509Certificate cert) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#update(java.util.Collection)
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public void update(Collection<X509Certificate> certs) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nhindirect.config.service.KeyStoreService#getAllCertificates()
	 */
	@FaultAction(className=ConfigurationServiceException.class)
	public Collection<X509Certificate> getAllCertificates() {
		// TODO Auto-generated method stub
		return null;
	}

}
