package org.nhindirect.config.service.impl;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.AnchorService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.AnchorDao;
import org.springframework.beans.factory.annotation.Autowired;

public class AnchorServiceImpl implements AnchorService {

	private static final Log log = LogFactory.getLog(AnchorServiceImpl.class);
	
	private AnchorDao dao;
	
	public void addAnchors(Collection<Anchor> anchors)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	public Anchor getAnchor(String owner, String thumbprint,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Anchor> getAnchors(Collection<Long> anchorIds,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Anchor> getAnchorsForOwner(String owner,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Anchor> getIncomingAnchors(String owner,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Anchor> getOutgoingAnchors(String owner,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAnchorStatusForOwner(String owner, EntityStatus status)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}
	
	public Collection<Anchor> listAnchors(Long lastAnchorID, int maxResults,
			CertificateGetOptions options) throws ConfigurationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeAnchors(Collection<Long> anchorIds)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	public void removeAnchorsForOwner(String owner)
			throws ConfigurationServiceException {
		// TODO Auto-generated method stub

	}

	@Autowired
	public void setDao(AnchorDao dao) {
		this.dao = dao;
	}

	public AnchorDao getDao() {
		return dao;
	}

}
