package org.nhindirect.config.service.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.FaultAction;

import org.nhindirect.config.service.AnchorService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.EntityStatus;


@WebService(endpointInterface = "org.nhindirect.config.service.AnchorService")
public class AnchorServiceWS implements AnchorService {

	@FaultAction(className=ConfigurationServiceException.class)
	public void addAnchors(List<Anchor> anchors)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	@FaultAction(className=ConfigurationServiceException.class)
	public Anchor getAnchor(String owner, String thumbprint,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public List<Anchor> getAnchors(List<Long> anchorIds,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public List<Anchor> getAnchorsForOwner(String owner,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public List<Anchor> getIncomingAnchors(String owner,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public List<Anchor> getOutgoingAnchors(String owner,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public void setAnchorStatusForOwner(String owner, EntityStatus status)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}
	
	@FaultAction(className=ConfigurationServiceException.class)
	public List<Anchor> ListAnchors(Long lastAnchorID, int maxResults,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@FaultAction(className=ConfigurationServiceException.class)
	public void removeAnchors(List<Long> anchorIds)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	@FaultAction(className=ConfigurationServiceException.class)
	public void removeAnchorsForOwner(String owner)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

}
