package org.nhindirect.config.service.ws;
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
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.FaultAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.DomainService;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.springframework.beans.factory.annotation.Autowired;


@WebService(endpointInterface = "org.nhindirect.config.service.DomainService")
public class DomainServiceWS implements DomainService {

	private static final Log log = LogFactory.getLog(DomainServiceWS.class);
	
	private DomainDao dao;
	
	public void init() {
		log.info("DomainService initialized");
	}
	
	@FaultAction(className=ConfigurationServiceException.class)
	public void addDomain(Domain domain) throws ConfigurationServiceException {
		if (domain != null) {
			dao.add(domain);
		}
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public void updateDomain(Domain domain) throws ConfigurationServiceException {
		if (domain != null) {
			dao.update(domain);
		}
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public int getDomainCount() throws ConfigurationServiceException {
		return dao.count();
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public List<Domain> getDomains(List<String> domainNames, EntityStatus status) throws ConfigurationServiceException {
		return dao.getDomains(domainNames, status);
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public void removeDomain(String domainName) throws ConfigurationServiceException {
		dao.delete(domainName);
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public List<Domain> listDomains(String lastDomainName, int maxResults) throws ConfigurationServiceException {
		return dao.listDomains(lastDomainName, maxResults);
	}
	
	@Autowired
	public void setDao(DomainDao aDao)
	{
		dao = aDao;
	}
}

