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
import java.security.cert.X509Certificate;
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

import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.exceptions.CertificateConversionException;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.config.store.TrustBundleDomainReltn;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.TrustBundleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

@Component
@Path("trustbundle/")
@Singleton
public class TrustBundleResource 
{
	protected static final CacheControl noCache;
	
    private static final Log log = LogFactory.getLog(TrustBundleResource.class);
	
    static
	{
		noCache = new CacheControl();
		noCache.setNoCache(true);
	}
    
    protected TrustBundleDao bundleDao;
  
    protected DomainDao domainDao;
    
    protected ProducerTemplate template;
    
    /**
     * Constructor
     */
    public TrustBundleResource()
    {
		
	}
    
    @Autowired
    public void setTrustBundleDao(TrustBundleDao bundleDao) 
    {
        this.bundleDao = bundleDao;
    }
    
    @Autowired
    public void setDomainDao(DomainDao domainDao) 
    {
        this.domainDao = domainDao;
    }
    
    @Autowired
    @Qualifier("bundleRefresh")
    public void setTemplate(ProducerTemplate template) 
    {
        this.template = template;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrustBundles(@QueryParam("fetchAnchors") @DefaultValue("true") boolean fetchAnchors)
    {
    	
    	Collection<org.nhindirect.config.store.TrustBundle> retBundles = null;
    	
    	try
    	{
    		retBundles = bundleDao.getTrustBundles();
    		
    		if (retBundles.isEmpty())
    			return Response.noContent().cacheControl(noCache).build();

    	}
    	catch (Throwable e)
    	{
    		log.error("Error looking up trust bundles", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<TrustBundle> modelBundles = new ArrayList<TrustBundle>();
    	for (org.nhindirect.config.store.TrustBundle bundle: retBundles)
    	{
    		if (!fetchAnchors)
    			bundle.setTrustBundleAnchors(new ArrayList<TrustBundleAnchor>());
    		
    		modelBundles.add(EntityModelConversion.toModelTrustBundle(bundle));
    	}
    	
    	final GenericEntity<Collection<TrustBundle>> entity = new GenericEntity<Collection<TrustBundle>>(modelBundles) {};
    	
    	return Response.ok(entity).cacheControl(noCache).build();
    }
    
    @GET  
    @Produces(MediaType.APPLICATION_JSON)
    @Path("domains/{domainName}")
    public Response getTrustBundlesByDomain(@PathParam("domainName") String domainName, @QueryParam("fetchAnchors") @DefaultValue("true") boolean fetchAnchors)
    {
    	
    	// make sure the domain exists
    	org.nhindirect.config.store.Domain entityDomain;
    	try
    	{
    		entityDomain = domainDao.getDomainByName(domainName);
    		if (entityDomain == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    		
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	Collection<TrustBundleDomainReltn> retBundles = null;
    	
    	try
    	{
    		retBundles = bundleDao.getTrustBundlesByDomain(entityDomain.getId());
  
    		if (retBundles.isEmpty())
    			return Response.noContent().cacheControl(noCache).build();
    		
    	}
    	catch (Throwable e)
    	{
    		log.error("Error looking up trust bundles", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<TrustBundle> modelBundles = new ArrayList<TrustBundle>();
    	for (org.nhindirect.config.store.TrustBundleDomainReltn bundleReltn: retBundles)
    	{
    		if (!fetchAnchors)
    			bundleReltn.getTrustBundle().setTrustBundleAnchors(new ArrayList<TrustBundleAnchor>());
    		
    		modelBundles.add(EntityModelConversion.toModelTrustBundle(bundleReltn.getTrustBundle()));
    	}
    	
    	final GenericEntity<Collection<TrustBundle>> entity = new GenericEntity<Collection<TrustBundle>>(modelBundles) {};
    	
    	return Response.ok(entity).cacheControl(noCache).build();
    }
    
    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{bundleName}")
    public Response getTrustBundleByName(@PathParam("bundleName") String bundleName)
    {
    	try
    	{
    		final org.nhindirect.config.store.TrustBundle retBundle = bundleDao.getTrustBundleByName(bundleName);
    		
    		if (retBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();

    		final TrustBundle modelBundle = EntityModelConversion.toModelTrustBundle(retBundle);
    		
    		return Response.ok(modelBundle).cacheControl(noCache).build(); 
    		
    	}
    	catch (Throwable e)
    	{
    		log.error("Error looking up trust bundles", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }  
    
    @PUT 
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response addTrustBundle(@Context UriInfo uriInfo, TrustBundle bundle)
    {
    	// make sure it doesn't exist
    	try
    	{
    		if (bundleDao.getTrustBundleByName(bundle.getBundleName()) != null)
    			return Response.status(Status.CONFLICT).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{    		
    		final org.nhindirect.config.store.TrustBundle entityBundle = EntityModelConversion.toEntityTrustBundle(bundle);
    		
    		bundleDao.addTrustBundle(entityBundle);
    		
    		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
    		final URI newLoc = newLocBuilder.path("trustbundle/" + bundle.getBundleName()).build();
    		
    		// the trust bundle does not contain any of the anchors
    		// they must be fetched from the URL... use the
    		// refresh route to force downloading the anchors
    		template.sendBody(entityBundle);
    		
    		return Response.created(newLoc).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding trust bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }   
    
    @Path("{bundle}/refreshBundle")
    @POST  
    public Response refreshTrustBundle(@PathParam("bundle") String bundleName)    
    {
    	// make sure it exists and refresh it
    	try
    	{
    		final org.nhindirect.config.store.TrustBundle entityBundle = bundleDao.getTrustBundleByName(bundleName);
    		
    		if (entityBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    		
    		template.sendBody(entityBundle);
    		
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error refreshing bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @DELETE 
    @Path("{bundle}")
    public Response deleteBundle(@PathParam("bundle") String bundleName)
    {
    	// make sure it exists
    	org.nhindirect.config.store.TrustBundle entityBundle;
    	try
    	{
    		entityBundle = bundleDao.getTrustBundleByName(bundleName);
    		if (entityBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{
    		bundleDao.deleteTrustBundles(new long[] {entityBundle.getId()});
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error deleting trust bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @POST 
    @Path("{bundle}/signingCert")  
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response updateSigningCert(@PathParam("bundle") String bundleName, byte[] certData)
    {   
    	X509Certificate signingCert = null;
    	if (certData.length > 0)
    	{
	    	try
	    	{
	    		signingCert = CertUtils.toX509Certificate(certData);		
	    	}
	    	catch (CertificateConversionException ex)
	    	{
	    		log.error("Signing certificate is not in a valid format " + bundleName, ex);
	    		return Response.status(Status.BAD_REQUEST).cacheControl(noCache).build();
	    	}
    	}
    	
    	// make sure the bundle exists
    	org.nhindirect.config.store.TrustBundle entityBundle;
    	try
    	{
    		entityBundle = bundleDao.getTrustBundleByName(bundleName);
    		if (entityBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// now update
    	try
    	{
    		bundleDao.updateTrustBundleSigningCertificate(entityBundle.getId(), signingCert);
    		
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error updating trust bundle signing certificate.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	
    }
    
    @POST 
    @Path("{bundle}/bundleAttributes")  
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response updateBundleAttributes(@PathParam("bundle") String bundleName, TrustBundle bundleData)
    {  
    	// make sure the bundle exists
    	org.nhindirect.config.store.TrustBundle entityBundle;
    	try
    	{
    		entityBundle = bundleDao.getTrustBundleByName(bundleName);
    		if (entityBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final String oldBundleURL = entityBundle.getBundleURL();
    	
    	// if there is a signing certificate in the request, make sure it's valid
    	X509Certificate newSigningCert = null;
    	if (bundleData.getSigningCertificateData() != null)
    	{
        	
        	try
        	{
        		newSigningCert = CertUtils.toX509Certificate(bundleData.getSigningCertificateData());		
        	}
        	catch (CertificateConversionException ex)
        	{
        		log.error("Signing certificate is not in a valid format " + bundleName, ex);
        		return Response.status(Status.BAD_REQUEST).cacheControl(noCache).build();
        	}
    	}

    	// update the bundle
    	try
    	{
    		bundleDao.updateTrustBundleAttributes(entityBundle.getId(), bundleData.getBundleName(), bundleData.getBundleURL(), newSigningCert, bundleData.getRefreshInterval());
    		
			// if the URL changed, the bundle needs to be refreshed
			if (bundleData.getBundleURL() != null && !bundleData.getBundleURL().isEmpty() && !oldBundleURL.equals(bundleData.getBundleURL()))
			{
				entityBundle = bundleDao.getTrustBundleById(entityBundle.getId());

				template.sendBody(entityBundle);
			}
    		
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error updating trust bundle attributes.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @POST 
    @Path("{bundle}/{domain}")  
    public Response associateTrustBundleToDomain(@PathParam("bundle") String bundleName, @PathParam("domain") String domainName,
    		@QueryParam("incoming") @DefaultValue("true") boolean incoming, @QueryParam("outgoing") @DefaultValue("true") boolean outgoing)
    {
    	// make sure the bundle exists
    	org.nhindirect.config.store.TrustBundle entityBundle;
    	try
    	{
    		entityBundle = bundleDao.getTrustBundleByName(bundleName);
    		if (entityBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// make sure the domain exists
    	org.nhindirect.config.store.Domain entityDomain;
    	try
    	{
    		entityDomain = domainDao.getDomainByName(domainName);
    		if (entityDomain == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    		
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// now make the association
    	try
    	{
    		bundleDao.associateTrustBundleToDomain(entityDomain.getId(), entityBundle.getId(), incoming, outgoing);
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error associating trust bundle to domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}    	
    }
    
    @DELETE 
    @Path("{bundle}/{domain}")  
    public Response disassociateTrustBundleFromDomain(@PathParam("bundle") String bundleName, @PathParam("domain") String domainName)
    {    
    	// make sure the bundle exists
    	org.nhindirect.config.store.TrustBundle entityBundle;
    	try
    	{
    		entityBundle = bundleDao.getTrustBundleByName(bundleName);
    		if (entityBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// make sure the domain exists
    	org.nhindirect.config.store.Domain entityDomain;
    	try
    	{
    		entityDomain = domainDao.getDomainByName(domainName);
    		if (entityDomain == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    		
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// now make the disassociation
    	try
    	{
    		bundleDao.disassociateTrustBundleFromDomain(entityDomain.getId(), entityBundle.getId());
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error disassociating trust bundle from domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}       	
    }
    
    @DELETE 
    @Path("{domain}/deleteFromDomain")  
    public Response disassociateTrustBundlesFromDomain(@PathParam("domain") String domainName)
    {   
    	// make sure the domain exists
    	org.nhindirect.config.store.Domain entityDomain;
    	try
    	{
    		entityDomain = domainDao.getDomainByName(domainName);
    		if (entityDomain == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    		
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// now make the disassociation
    	try
    	{
    		bundleDao.disassociateTrustBundlesFromDomain(entityDomain.getId());
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error disassociating trust bundles from domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}  
    }
    
    @DELETE 
    @Path("{bundle}/deleteFromBundle")  
    public Response disassociateTrustBundleFromDomains(@PathParam("bundle") String bundleName)
    {   
    	// make sure the bundle exists
    	org.nhindirect.config.store.TrustBundle entityBundle;
    	try
    	{
    		entityBundle = bundleDao.getTrustBundleByName(bundleName);
    		if (entityBundle == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up bundle.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// now make the disassociation
    	try
    	{
    		bundleDao.disassociateTrustBundleFromDomains(entityBundle.getId());
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error disassociating trust bundle from domains.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}  
    }    
}
