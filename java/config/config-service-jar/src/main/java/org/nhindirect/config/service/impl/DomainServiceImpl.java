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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.FaultAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.DomainService;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.DomainDao;
import org.springframework.beans.factory.annotation.Autowired;


@WebService(endpointInterface = "org.nhindirect.config.service.DomainService")
public class DomainServiceImpl implements DomainService {

	private static final Log log = LogFactory.getLog(DomainServiceImpl.class);

	private DomainDao dao;
	
	public void init() {
		log.info("DomainService initialized");
	}
	
	public void addDomain(Domain domain) throws ConfigurationServiceException {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		dao.add(domain);
		log.info("Added Domain: " + domain.getDomainName());
		if (log.isDebugEnabled()) log.debug("Exit");
	}

	public void updateDomain(Domain domain) throws ConfigurationServiceException {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		if (domain != null) {
			dao.update(domain);
			log.info("Modified Domain: " + domain.getDomainName());
		}
		
		if (log.isDebugEnabled()) log.debug("Exit");
	}

	public int getDomainCount() throws ConfigurationServiceException {
		return dao.count();
	}

	public Collection<Domain> getDomains(Collection<String> domainNames, EntityStatus status) throws ConfigurationServiceException {
		ArrayList<String> domains = new ArrayList<String>(domainNames);
		return dao.getDomains(domains, status);
	}

	public void removeDomain(String domainName) throws ConfigurationServiceException {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		dao.delete(domainName);
		log.info("Modified Domain: " + domainName);
		
		if (log.isDebugEnabled()) log.debug("Exit");
	}

	public Collection<Domain> listDomains(String lastDomainName, int maxResults) throws ConfigurationServiceException {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		List<Domain> result = dao.listDomains(lastDomainName, maxResults);
		
		if (log.isDebugEnabled()) {
			if (result == null) {
				log.debug("Exit: NULL");
			}
			else {
				log.debug("Exit: " + result.toString());
			}
		}
		
		return result;	
	}
	
	public Collection<Domain> searchDomain(String domain, EntityStatus status) {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		List<Domain> result = dao.searchDomain(domain, status);
		
		if (log.isDebugEnabled()) {
			if (result == null) {
				log.debug("Exit: NULL");
			}
			else {
				log.debug("Exit: " + result.toString());
			}
		}
		
		return result;		
	}
	
	public Domain getDomain(Long id) {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		Domain result = dao.getDomain(id);
		
		if (log.isDebugEnabled()) {
			if (result == null) {
				log.debug("Exit: NULL");
			}
			else {
				log.debug("Exit: " + result.toString());
			}
		}
		
		return result;
	}
	
	@Autowired
	public void setDao(DomainDao aDao)
	{
		dao = aDao;
	}


}

