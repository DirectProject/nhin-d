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

import org.nhindirect.config.store.Address;
import org.nhindirect.config.store.EntityStatus;

public interface AddressService {

    @WebMethod(operationName = "addAddress", action = "urn:AddAddress")
	void addAddress(@WebParam(name = "address") Collection<Address> address) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "updateAddress", action = "urn:UpdateAddress")
	void updateAddress(@WebParam(name = "address") Address address) throws ConfigurationServiceException;;
    
    @WebMethod(operationName = "getAddressCount", action = "urn:GetAddressCount")
	int  getAddressCount() throws ConfigurationServiceException;;
    
    @WebMethod(operationName = "getAddresss", action = "urn:GetAddresss")
	Collection<Address> getAddress(@WebParam(name = "emailAddress") Collection<String> addressNames, 
			                       @WebParam(name = "status") EntityStatus status) throws ConfigurationServiceException;;
    
    @WebMethod(operationName = "removeAddress", action = "urn:RemoveAddress")
	void removeAddress(@WebParam(name = "emailAddress") String addressName) throws ConfigurationServiceException;;
    
    @WebMethod(operationName = "listAddresss", action = "urn:ListAddresss")
	Collection<Address> listAddresss(@WebParam(name = "lastEmailAddress") String lastAddressName, 
			                         @WebParam(name = "maxResults") int maxResults) throws ConfigurationServiceException;;
}

