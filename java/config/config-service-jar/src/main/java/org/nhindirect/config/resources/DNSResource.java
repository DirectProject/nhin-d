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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
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
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.dao.DNSDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

@Component
@Path("dns/")
@Singleton
public class DNSResource 
{
	protected static final CacheControl noCache;
	
    private static final Log log = LogFactory.getLog(DNSResource.class);
	
    static
	{
		noCache = new CacheControl();
		noCache.setNoCache(true);
	}
    
    protected DNSDao dnsDao;
    
    /**
     * Constructor
     */
    public DNSResource()
    {
		
	}
    
    @Autowired
    public void setDNSDao(DNSDao dnsDao) 
    {
        this.dnsDao = dnsDao;
    }
     
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response getDNSRecords(@QueryParam("type") @DefaultValue("-1") int type, @QueryParam("name") @DefaultValue("") String name)
    {
    	Collection<org.nhindirect.config.store.DNSRecord> retRecords;
    	
    	try
    	{
	    	if (type > -1 && !name.isEmpty())
	    		retRecords = dnsDao.get(name.endsWith(".") ? name : (name + "."), type);
	    	else if (type > -1)
	    		retRecords = dnsDao.get(type);
	    	else if (!name.isEmpty())
	    		retRecords = dnsDao.get(name.endsWith(".") ? name : (name + "."));
	    	else
	    	{
        		log.error("Either a DNS query name or type (or both) must be specified.");
        		return Response.status(Status.BAD_REQUEST).cacheControl(noCache).build();	    		
	    	}
	    		
	    	
	    	if (retRecords.isEmpty())
	    		return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();	
	    		
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up DNS records.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<DNSRecord> modelRecords = new ArrayList<DNSRecord>();
    	for (org.nhindirect.config.store.DNSRecord record : retRecords)
    	{
    		modelRecords.add(EntityModelConversion.toModelDNSRecord(record));
    	}
    	
		final GenericEntity<Collection<DNSRecord>> entity = new GenericEntity<Collection<DNSRecord>>(modelRecords) {};
		
		return Response.ok(entity).cacheControl(noCache).build();      	
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)      
    public Response addDNSRecord(@Context UriInfo uriInfo, DNSRecord record)
    {
    	if (!record.getName().endsWith("."))
    		record.setName(record.getName() + ".");
    	
    	// check to see if it already exists
    	try
    	{
    		final Collection<org.nhindirect.config.store.DNSRecord> records = dnsDao.get(record.getName(), record.getType());

    			for (org.nhindirect.config.store.DNSRecord compareRecord : records)
    				// do a binary compare of the data
    				if (Arrays.equals(record.getData(), compareRecord.getData()))
    					return Response.status(Status.CONFLICT).cacheControl(noCache).build();
    			
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up DNS records.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{
    		dnsDao.add(Arrays.asList(EntityModelConversion.toEntityDNSRecord(record)));
    		
    		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
    		final URI newLoc = newLocBuilder.path("dns?type=" + record.getType() + "&name=" + record.getName()).build();
    		
    		return Response.created(newLoc).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding DNS record.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)      
    public Response updateDNSRecord(DNSRecord updateRecord)
    {       	
    	// ensure it exists 
    	try
    	{
    		if (dnsDao.get(updateRecord.getId()) == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up DNS records.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	if (!updateRecord.getName().endsWith("."))
    		updateRecord.setName(updateRecord.getName() + ".");
    	
    	try
    	{
    		dnsDao.update(updateRecord.getId(), EntityModelConversion.toEntityDNSRecord(updateRecord));
    		
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error updating DNS record.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @Path("{ids}")
    @DELETE
    public Response removeDNSRecordsByIds(@PathParam("ids") String ids)
    {
    	final String[] idArray = ids.split(",");
    	final long[] idList = new long[idArray.length];
    	
    	try
    	{
    		for (int i = 0; i < idArray.length; ++i)
    			idList[i] = (Long.parseLong(idArray[i]));
    		
    		dnsDao.remove(idList);
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error removing DNS records by ids.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
}
