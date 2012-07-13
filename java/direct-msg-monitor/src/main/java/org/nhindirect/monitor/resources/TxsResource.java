package org.nhindirect.monitor.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.enunciate.jaxrs.TypeHint;
import org.nhindirect.common.tx.model.Tx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

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
	
    static
	{
		noCache = new CacheControl();
		noCache.setNoCache(true);
	}
    
    
	public TxsResource()
	{
		
	}
	
	public TxsResource(ProducerTemplate template)
	{
		this.template = template;
	}
	
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
}
