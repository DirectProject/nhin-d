package org.nhind.config.rest;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Anchor;

public interface AnchorService 
{
	public Collection<Anchor> getAnchors() throws ServiceException;
	
	public Collection<Anchor> getAnchorsForOwner(String owner, boolean incoming, boolean outgoing, String thumbprint) throws ServiceException;
	
	public void addAnchor(Anchor anchor) throws ServiceException;
	
	public void deleteAnchorsByIds(Collection<Long> ids) throws ServiceException;
	
	public void deleteAnchorsByOwner(String owner) throws ServiceException;
}
