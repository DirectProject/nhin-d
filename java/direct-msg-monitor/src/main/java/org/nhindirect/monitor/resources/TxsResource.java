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

package org.nhindirect.monitor.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.enunciate.jaxrs.TypeHint;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.processor.DuplicateNotificationStateManager;
import org.nhindirect.monitor.processor.DuplicateNotificationStateManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

/**
 * Jax-RS resource that acts as a RESTful entry point into a Camel route that handles message monitoring and tracking.  The route
 * starting point is contained in a Camel producer template that injected at creation time.
 * <p>
 * The resource returns a status of 201 (created) if the message is successfully added to the route.  A status of 500 is returned other wise.
 * @author Greg Meyer
 * @since 1.0
 */
@Component
@Path("txs/")
@Singleton
public class TxsResource 
{
	/**
	 * Cache definition for no caching of responses.
	 */
	protected static final CacheControl noCache;
	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TxsResource.class);

	
    @Autowired
	protected ProducerTemplate template;
	
    @Autowired 
    protected DuplicateNotificationStateManager dupStateManager;
    
    static
	{
		noCache = new CacheControl();
		noCache.setNoCache(true);
	}
    
    /**
     * Constructor
     */
	public TxsResource()
	{
		
	}
	
	/**
	 * Constructor 
	 * @param template Production template used for placing message into the Camel route.
	 */
	public TxsResource(ProducerTemplate template, DuplicateNotificationStateManager dupMgr)
	{
		this.template = template;
		this.dupStateManager = dupMgr;
	}
	
	/**
	 * Adds a message into the system
	 * @param tx The message to add.
	 * @return Jax-RS response object containing the http status code.  If the message is successfully added to the route, then status 201 (created)
	 * is returned.  500 is returned if an error occurs.
	 */
    @TypeHint(Tx.class)  
    @POST
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response addTx(Tx tx)
    {
    	///CLOVER:OFF
    	if (LOGGER.isTraceEnabled())
    		LOGGER.trace("Attempting to add Tx");
    	///CLOVER:ON
    	
    	if (template == null)
    		throw new IllegalStateException("Template producer cannot be null.  Please examine the txs resource configuration");
    	
    	try
    	{
    		template.sendBody(tx);
    	}
    	catch (Throwable t)
    	{
    		LOGGER.error("Failed to add Tx message", t);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	///CLOVER:OFF
    	if (LOGGER.isTraceEnabled())
    		LOGGER.trace("Tx added");
    	///CLOVER:ON
    	
		return Response.status(Status.CREATED).cacheControl(noCache).build();
    }
    
    /**
	 * Indicates if a notification message should be suppressed from being delivered to the original message edge client
	 * based on existing notifications being received, if the original message is subject to the timely and reliable guidance,
	 * and other policies based on a specific HISP implementation.
	 * <p>
	 * This resource implements an overloaded POST paradigm using an RPC type pattern.  Typically, an operation such as this would use a GET verb with query parameters.  
	 * However, due to the large number of possible recipients in the message (potentially resulting in a URI size that may be too large from some
	 * servers), this resource uses a POST verb with the notification message as the payload.
     * @param notificationMessage The notification message in question.
     * @return If the resource execution is successful, the resource returns status 200 (OK) with the content being a JSON representation of a boolean value indicating if
     * the notification message should be suppressed.  500 is returned if an error occurs.
     */
    @Path("suppressNotification")
    @TypeHint(Boolean.class)  
    @POST
    @Produces(MediaType.APPLICATION_JSON) 
    @Consumes(MediaType.APPLICATION_JSON) 
    public Response supressNotification(Tx notificationMessage) 
    {
    	if (dupStateManager == null)
    		throw new IllegalStateException("Duplicatoin state manager cannot be null.  Please examine the txs resource configuration");
    	
    	try
    	{
    		Boolean retEntity = dupStateManager.suppressNotification(notificationMessage);
    		return Response.ok(retEntity).cacheControl(noCache).build();
    	}
    	catch (DuplicateNotificationStateManagerException e)
    	{
    		return Response.serverError().cacheControl(noCache).build();
    	}
		
    }
}
