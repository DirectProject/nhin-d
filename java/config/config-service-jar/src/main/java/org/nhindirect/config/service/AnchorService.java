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
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.FaultAction;

import org.nhindirect.config.service.ws.CertificateGetOptions;
import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.EntityStatus;

@WebService(name = "AnchorsService",targetNamespace = "http://nhind.org/config")
public interface AnchorService {

	@WebMethod(operationName = "addAnchor", action = "urn:AddAnchor")
	public void addAnchors(List<Anchor> anchors) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getAnchor", action = "urn:GetAnchor")
	public Anchor getAnchor(String owner, String thumbprint, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getAnchors", action = "urn:GetAnchors")
	public List<Anchor> getAnchors(List<Long> anchorIds, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getAnchorsForOwner", action = "urn:GetAnchorsForOwner")
	public List<Anchor> getAnchorsForOwner(String owner, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getIncomingAnchors", action = "urn:GetIncomingAnchors")
	public List<Anchor> getIncomingAnchors(String owner, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "getOutgoingAnchors", action = "urn:GetOutgoingAnchors")
	public List<Anchor> getOutgoingAnchors(String owner, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "setAnchorStatusForOwner", action = "urn:SetAnchorStatusForOwner")
	public void setAnchorStatusForOwner(String owner, EntityStatus status) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "ListAnchors", action = "urn:ListAnchors")
	public List<Anchor> ListAnchors(Long lastAnchorID, int maxResults, CertificateGetOptions options) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "removeAnchors", action = "urn:RemoveAnchors")
	public void removeAnchors(List<Long> anchorIds) throws ConfigurationServiceException;
	
	@WebMethod(operationName = "removeAnchorsForOwner", action = "urn:RemoveAnchorsForOwner")
	public void removeAnchorsForOwner(String owner) throws ConfigurationServiceException;

}



