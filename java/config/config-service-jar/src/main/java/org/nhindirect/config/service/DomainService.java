package org.nhindirect.config.service;
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
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;

public interface DomainService {
	
    @WebMethod(operationName = "addDomain", action = "urn:AddDomain")
	void addDomain(@WebParam(name = "domain") Domain domain) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "updateDomain", action = "urn:UpdateDomain")
	void updateDomain(@WebParam(name = "domain") Domain domain) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "getDomainCount", action = "urn:GetDomainCount")
	int  getDomainCount() throws ConfigurationServiceException;
    
    @WebMethod(operationName = "getDomains", action = "urn:GetDomains")
	Collection<Domain> getDomains(@WebParam(name = "names")Collection<String> domainNames, 
			                @WebParam(name = "status")     EntityStatus status) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "removeDomain", action = "urn:RemoveDomain")
	void removeDomain(@WebParam(name = "name") String domainName) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "listDomains", action = "urn:listDomains")
	Collection<Domain> listDomains(@WebParam(name = "names")    String lastDomainName, 
			                       @WebParam(name = "maxResults") int maxResults) throws ConfigurationServiceException;

	@WebMethod(operationName = "searchDomain", action = "urn:SearchDomain")
	Collection<Domain> searchDomain(@WebParam(name = "name")  String domain, 
			                        @WebParam(name = "status")EntityStatus status);
	
	@WebMethod(operationName = "getDomain", action = "urn:GetDomain")
	Domain getDomain(@WebParam(name = "id") Long id);
}

