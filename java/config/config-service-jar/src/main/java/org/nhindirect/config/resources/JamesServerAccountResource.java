package org.nhindirect.config.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.model.Address;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

@Component
@Path("jamesaccounts/")
@Singleton
public class JamesServerAccountResource extends ProtectedResource
{
    private static final Log log = LogFactory.getLog(JamesServerAccountResource.class);
    
    protected String directHome;
    
    public JamesServerAccountResource()
    {
    	boolean directHomeFound = false;
    	final Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) 
        {
        	if (envName.compareToIgnoreCase("DIRECT_HOME") == 0)
        	{
        		directHomeFound = true;
        		directHome = env.get(envName);
                break;         
        	}
        }

        if (!directHomeFound)
        	directHome = null;
    }
    
    @Path("2X")
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response get2XAccounts()
    {
    	return null;
    }
    
    @Path("3X")
    @Produces(MediaType.APPLICATION_JSON)       
    @GET
    public Response get3XAccounts()
    {
    	final List<Address> addrs = new ArrayList<Address>();
    	
    	final String jamesClExec = directHome + "/apache-james-3.0-beta4/bin/james-cli.sh -h localhost listusers";
    	
    	try
    	{
    		String line;
    		final Process process = Runtime.getRuntime ().exec(jamesClExec);
    		
    		OutputStream stdin = process.getOutputStream ();
    		InputStream stdout = process.getInputStream ();
    		
    		BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
    		
    		while ((line = reader.readLine ()) != null) 
    		{
    			final Address addr = new Address();
    			addr.setEmailAddress(line);
    			
    			addrs.add(addr);
    		}
    		
    		IOUtils.closeQuietly(reader);
    		IOUtils.closeQuietly(writer);
    		
    		final GenericEntity<Collection<Address>> entity = new GenericEntity<Collection<Address>>(addrs) {};
    		
    		return Response.ok(entity).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up addresses.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @Path("3X/{address}/{password}")
    @Produces(MediaType.APPLICATION_JSON)       
    @PUT
    public Response addAccount(@Context UriInfo uriInfo, @QueryParam("address") String address, 
    		@QueryParam("password") String password)
    {
    	final String jamesClExec = directHome + "/apache-james-3.0-beta4/bin/james-cli.sh -h localhost adduser " + address + " " + password;
    	
    	try
    	{
    		String line;
    		final Process process = Runtime.getRuntime ().exec(jamesClExec);
    		
    		OutputStream stdin = process.getOutputStream ();
    		InputStream stdout = process.getInputStream ();
    		
    		BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
    		
    		
    		boolean userAdded = false;
    		while ((line = reader.readLine ()) != null) 
    		{
    			if (line.contains("adduser command executed sucessfully"))
    				userAdded = true;
    			else if (line.contains("already exists"))
    			{
    				return Response.status(Status.CONFLICT).cacheControl(noCache).build();
    			}
    		}
    		
    		IOUtils.closeQuietly(reader);
    		IOUtils.closeQuietly(writer);
    		
    		if (userAdded)
    		{
        		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
        		final URI newLoc = newLocBuilder.path("jamesaccounts/3.0/" + address).build();
        		
        		return Response.created(newLoc).cacheControl(noCache).build();
    		}
    		
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding address.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
}
