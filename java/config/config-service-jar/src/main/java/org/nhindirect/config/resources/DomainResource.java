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
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.dao.AddressDao;
import org.nhindirect.config.store.dao.DomainDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

@Component
@Path("domain/")
@Singleton
public class DomainResource 
{
	protected static final CacheControl noCache;
	
    private static final Log log = LogFactory.getLog(DomainResource.class);
	
    static
	{
		noCache = new CacheControl();
		noCache.setNoCache(true);
	}
    
    protected AddressDao addressDao;
    
    protected DomainDao domainDao;
    
    /**
     * Constructor
     */
    public DomainResource()
    {
		
	}
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) 
    {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setDomainDao(DomainDao domainDao) 
    {
        this.domainDao = domainDao;
    }
    
    @Path("{domain}")
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response getDomain(@PathParam("domain") String domain)
    {   	
    	try
    	{
    		org.nhindirect.config.store.Domain retDomain = domainDao.getDomainByName(domain);
    		if (retDomain == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    		
    		return Response.ok(EntityModelConversion.toModelDomain(retDomain)).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response searchDomains(@QueryParam("domainName") @DefaultValue("") String domainName,
    		@QueryParam("entityStatus") @DefaultValue("") String entityStatus)
    {
    	
    	org.nhindirect.config.store.EntityStatus status = null;
    	// get the entity status requested
    	if (!entityStatus.isEmpty())
    	{
    		try
    		{
    			status = org.nhindirect.config.store.EntityStatus.valueOf(entityStatus);
    		}
    		catch (IllegalArgumentException e)
    		{
    			log.warn("EntityStatus enum value of " + entityStatus + " encountered.  Defaulting EntityStatus to null");
    		}
    	}
    	
    	// do the search
    	try
    	{
    		Collection<org.nhindirect.config.store.Domain> domains = domainDao.searchDomain(domainName.isEmpty() ? null : domainName, status);

    		if (domains.isEmpty())
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    		
    		final Collection<Domain> retDomains = new ArrayList<Domain>();
    		for (org.nhindirect.config.store.Domain domain : domains)
    			retDomains.add(EntityModelConversion.toModelDomain(domain));
    			
    		final GenericEntity<Collection<Domain>> entity = new GenericEntity<Collection<Domain>>(retDomains) {};
    		
    		return Response.ok(entity).cacheControl(noCache).build();    		
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up domains.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)      
    public Response addDomain(@Context UriInfo uriInfo, Domain domain) 
    {
    	
    	// check to see if it already exists
    	try
    	{
    		if (domainDao.getDomainByName(domain.getDomainName()) != null)
    			return Response.status(Status.CONFLICT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final org.nhindirect.config.store.Domain toDomain = EntityModelConversion.toEntityDomain(domain);
    	
    	try
    	{
    		domainDao.add(toDomain);
    		
    		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
    		final URI newLoc = newLocBuilder.path("domain/" + domain.getDomainName()).build();
    		
    		return Response.created(newLoc).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    }   
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)      
    public Response updateDomain(Domain domain) 
    {
    	// make sure the domain exists
    	org.nhindirect.config.store.Domain existingDomain;
    	try
    	{
    		existingDomain = domainDao.getDomainByName(domain.getDomainName());
	    	if (existingDomain == null)
	    		return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final org.nhindirect.config.store.Domain toDomain = EntityModelConversion.toEntityDomain(domain);
    	toDomain.setId(existingDomain.getId());
    	
    	try
    	{
    		domainDao.update(toDomain);
    		
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error updating domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    }     
    
    @DELETE
    @Path("{domain}")   
    public Response removedDomain(@PathParam("domain") String domain)   
    {
    	// make sure it exists
    	try
    	{
    		if (domainDao.getDomainByName(domain) == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{
    		domainDao.delete(domain);
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error deleting domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}    	
    }    
}
