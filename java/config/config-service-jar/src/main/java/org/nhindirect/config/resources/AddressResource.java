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
import java.util.List;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.model.Address;
import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.dao.AddressDao;
import org.nhindirect.config.store.dao.DomainDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

/**
 * JAX-RS resource for managing address resources in the configuration service.
 * <p>
 * Although not required, this class is instantiated using the Jersey SpringServlet and dependencies are defined in the Sprint context XML file.
 * @author Greg Meyer
 * @since 2.0
 */
@Component
@Path("address/")
@Singleton
public class AddressResource extends ProtectedResource
{
    private static final Log log = LogFactory.getLog(AddressResource.class);
    
    /**
     * Address DAO is defined in the context XML file an injected by Spring
     */
    protected AddressDao dao;
  
    /**
     * Domain DAO is defined in the context XML file an injected by Spring
     */
    protected DomainDao domainDao;
    
    /**
     * Constructor
     */
    public AddressResource()
    {
		
	}
    
    /**
     * Sets the address Dao.  Auto populated by Spring
     * @param dao Address Dao
     */
    @Autowired
    public void setAddressDao(AddressDao dao) 
    {
        this.dao = dao;
    }
    
    /**
     * Sets the domain Dao.  Auto populate by Spring
     * @param domainDao
     */
    @Autowired
    public void setDomainDao(DomainDao domainDao) 
    {
        this.domainDao = domainDao;
    }
    
    /**
     * Gets an address by name.
     * @return A JSON representation of an Address.  Returns 404 if the address doesn't exists.
     * @param address The address to retrieve.
     */
    @Path("{address}")
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response getAddress(@PathParam("address") String address)
    {   	
    	try
    	{
    		org.nhindirect.config.store.Address retAddress = dao.get(address);
    		if (retAddress == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    		
    		return Response.ok(EntityModelConversion.toModelAddress(retAddress)).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    /**
     * Gets all addresses configured for a given domain.
     * @param domainName The domain name to retrieve addresses for.
     * @return  A JSON representation of a list of addresses.  Returns a 404 status if the domain does not exists
     * or a 204 status if no addresses are configured for the domain.
     */
    @Path("domain/{domain}")
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response getAddressesByDomain(@PathParam("domain") String domainName)
    {   	
    	// get the domain
    	org.nhindirect.config.store.Domain domain = null;
    	try
    	{
    		domain = domainDao.getDomainByName(domainName);
    		if (domain == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}    	
    	
    	try
    	{
    		final List<org.nhindirect.config.store.Address> addresses = dao.getByDomain(domain, null);
    		if (addresses == null || addresses.isEmpty())
    			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();
    		
    		final Collection<Address> retAddresses = new ArrayList<Address>();
    		for (org.nhindirect.config.store.Address address : addresses)
    			retAddresses.add(EntityModelConversion.toModelAddress(address));
    			
    		final GenericEntity<Collection<Address>> entity = new GenericEntity<Collection<Address>>(retAddresses) {};
    		
    		return Response.ok(entity).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up addresses.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    /**
     * Adds an address to the system and associates it with a domain.
     * @param uriInfo Injected URI context used for building the location URI.
     * @param address The address to add.
     * @return Returns status 201 if added successfully, 404 if the domain does not exist, or 409 if
     * the address already exists.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)      
    public Response addAddress(@Context UriInfo uriInfo, Address address) 
    {
    	// make sure the domain exists
    	if (address.getDomainName() == null || address.getDomainName().isEmpty())
    		return Response.status(Status.BAD_REQUEST).cacheControl(noCache).build();
    	
    	org.nhindirect.config.store.Domain domain;
    	try
    	{
    		domain = domainDao.getDomainByName(address.getDomainName());
	    	if (domain == null)
	    		return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// check to see if it already exists
    	try
    	{
    		if (dao.get(address.getEmailAddress()) != null)
    			return Response.status(Status.CONFLICT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final org.nhindirect.config.store.Address toAdd = EntityModelConversion.toEntityAddress(address);
    	toAdd.setDomain(domain);
    	
    	try
    	{
    		dao.add(toAdd);
    		
    		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
    		final URI newLoc = newLocBuilder.path("address/" + address.getEmailAddress()).build();
    		
    		return Response.created(newLoc).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    }
    
    /**
     * Updates the attributes of an existing address.
     * @param address The address to update along with new attributes.
     * @return Returns 204 if the address is updated successfully, 400 if the domain name is empty, 404 if the
     * domain or address does not exist.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)      
    public Response updateAddress(Address address) 
    {
    	// make sure the domain exists
    	if (address.getDomainName() == null || address.getDomainName().isEmpty())
    		return Response.status(Status.BAD_REQUEST).cacheControl(noCache).build();
    	
    	org.nhindirect.config.store.Domain domain;
    	try
    	{
    		domain = domainDao.getDomainByName(address.getDomainName());
	    	if (domain == null)
	    		return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// make sure the address exists
    	org.nhindirect.config.store.Address existingAdd = null;
    	try
    	{
    		existingAdd = dao.get(address.getEmailAddress());
    		if (existingAdd == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final org.nhindirect.config.store.Address toAdd = EntityModelConversion.toEntityAddress(address);
    	toAdd.setId(existingAdd.getId());
    	toAdd.setDomain(domain);
    	
    	try
    	{
    		dao.update(toAdd);
    		
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error updating address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    }    
    
    /**
     * Removes an address from the system.
     * @param address The address to removed.
     * @return Returns a status of 200 if the address was removed or 404 if the address does not exists.
     */
    @DELETE
    @Path("{address}")     
    public Response removedAddress(@PathParam("address") String address)   
    {
    	// make sure it exists
    	try
    	{
    		if (dao.get(address) == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{
    		dao.delete(address);
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error deleting address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}    	
    }
}
