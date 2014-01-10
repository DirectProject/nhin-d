/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
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

package org.nhindirect.config.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.Thumbprint;
import org.nhindirect.config.store.dao.AnchorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

@Component
@Path("anchor/")
@Singleton
public class AnchorResource 
{
	protected static final CacheControl noCache;
	
    private static final Log log = LogFactory.getLog(AnchorResource.class);
	
    static
	{
		noCache = new CacheControl();
		noCache.setNoCache(true);
	}
    
    protected AnchorDao anchorDao;
    
    /**
     * Constructor
     */
    public AnchorResource()
    {
		
	}
    
    @Autowired
    public void setAnchorDao(AnchorDao anchorDao) 
    {
        this.anchorDao = anchorDao;
    }
    
    
    @Path("{owner}")
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response getAnchorForOwner(@QueryParam("incoming") @DefaultValue("false") boolean incoming, 
    		@QueryParam("outgoing") @DefaultValue("false") boolean outgoing, 
    		@QueryParam("thumbprint") @DefaultValue("") String thumbprint, 
    		@PathParam("owner") String owner)
    {
    	List<org.nhindirect.config.store.Anchor> retAnchors;
    	
    	try
    	{
    		retAnchors = anchorDao.list(Arrays.asList(owner));
    		if (retAnchors.isEmpty())
    			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up anchors.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<Anchor> modelAnchors = new ArrayList<Anchor>();

    	for (org.nhindirect.config.store.Anchor anchor : retAnchors)
    	{
    		if (!((incoming && !anchor.isIncoming()) || (outgoing && !anchor.isOutgoing()) ||
    				(!thumbprint.isEmpty() && !thumbprint.equalsIgnoreCase(anchor.getThumbprint()))))
    		{
    			modelAnchors.add(EntityModelConversion.toModelAnchor(anchor));
    		}
    	}
    	
		if (modelAnchors.isEmpty())
			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();

		final GenericEntity<Collection<Anchor>> entity = new GenericEntity<Collection<Anchor>>(modelAnchors) {};
		
		return Response.ok(entity).cacheControl(noCache).build();    	
    }
    
    @Produces(MediaType.APPLICATION_JSON)       
    @GET 
    public Response getAnchors()
    {
    	List<org.nhindirect.config.store.Anchor> retAnchors;
    	
    	try
    	{
    		retAnchors = anchorDao.listAll();
    		if (retAnchors.isEmpty())
    			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up anchors.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<Anchor> modelAnchors = new ArrayList<Anchor>();
    	for (org.nhindirect.config.store.Anchor anchor: retAnchors)
    	{
    		modelAnchors.add(EntityModelConversion.toModelAnchor(anchor));
    	}
    	
		final GenericEntity<Collection<Anchor>> entity = new GenericEntity<Collection<Anchor>>(modelAnchors) {};
		
		return Response.ok(entity).cacheControl(noCache).build();  
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)      
    public Response addAnchor(@Context UriInfo uriInfo, Anchor anchor) 
    {
    	// check to see if it already exists
    	try
    	{
    		final String thumbprint = (anchor.getThumbprint() == null || anchor.getThumbprint().isEmpty()) ?
    				Thumbprint.toThumbprint(anchor.getAnchorAsX509Certificate()).toString() : anchor.getThumbprint();
    				
    		final Collection<org.nhindirect.config.store.Anchor> existingAnchors = anchorDao.list(Arrays.asList(anchor.getOwner()));
    		for (org.nhindirect.config.store.Anchor existingAnchor : existingAnchors)
    		{
    			if (existingAnchor.getThumbprint().equalsIgnoreCase(thumbprint))
    				return Response.status(Status.CONFLICT).cacheControl(noCache).build();
    		}
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing anchor.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{
    		anchorDao.add(EntityModelConversion.toEntityAnchor(anchor));
    		
    		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
    		final URI newLoc = newLocBuilder.path("anchor/" + anchor.getOwner()).build();
    		
    		return Response.created(newLoc).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding anchor.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    }
   
    @Path("ids/{ids}")
    @DELETE
    public Response removeAnchorsByIds(@PathParam("ids") String ids)
    {
    	final String[] idArray = ids.split(",");
    	final List<Long> idList = new ArrayList<Long>();
    	
    	
    	try
    	{
    		for (String id : idArray)
    			idList.add(Long.parseLong(id));
    		
    		anchorDao.delete(idList);
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error removing anchors by ids.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @DELETE
    @Path("{owner}")   
    public Response removeAnchorsByOwner(@PathParam("owner") String owner)
    {
    	try
    	{
    		anchorDao.delete(owner);
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error removing anchors by owner.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
}
