package org.nhindirect.config.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.config.store.dao.DomainDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.Singleton;

@Component
@Path("certpolicy/")
@Singleton
public class CertPolicyResource 
{
	protected static final CacheControl noCache;
	
    private static final Log log = LogFactory.getLog(CertPolicyResource.class);
	
    static
	{
		noCache = new CacheControl();
		noCache.setNoCache(true);
	}
    
    protected CertPolicyDao policyDao;
  
    protected DomainDao domainDao;
    
    /**
     * Constructor
     */
    public CertPolicyResource()
    {
		
	}
    
    @Autowired
    public void setCertPolicyDao(CertPolicyDao policyDao) 
    {
        this.policyDao = policyDao;
    }
    
    @Autowired
    public void setDomainDao(DomainDao domainDao) 
    {
        this.domainDao = domainDao;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPolicies()
    {
    	Collection<org.nhindirect.config.store.CertPolicy> retPolicies;
    	
    	try
    	{
    		retPolicies = policyDao.getPolicies();
    		if (retPolicies.isEmpty())
    			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up cert policies.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<CertPolicy> modelPolicies = new ArrayList<CertPolicy>();
    	
    	for (org.nhindirect.config.store.CertPolicy policy : retPolicies)	
    		modelPolicies.add(EntityModelConversion.toModelCertPolicy(policy));
    	
		final GenericEntity<Collection<CertPolicy>> entity = new GenericEntity<Collection<CertPolicy>>(modelPolicies) {};
		
		return Response.ok(entity).cacheControl(noCache).build();      	
    }
    
    @GET
    @Path("{policyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPolicyByName(@PathParam("policyName") String policyName)
    {
    	try
    	{
    		final org.nhindirect.config.store.CertPolicy retPolicy = policyDao.getPolicyByName(policyName);
    		
    		if (retPolicy == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();

    		final CertPolicy modelPolicy = EntityModelConversion.toModelCertPolicy(retPolicy);
    		
    		return Response.ok(modelPolicy).cacheControl(noCache).build(); 
    		
    	}
    	catch (Throwable e)
    	{
    		log.error("Error looking up cert policy", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}    	
    }  
    
    @PUT 
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response addPolicy(@Context UriInfo uriInfo, CertPolicy policy)
    {
    	// make sure it doesn't exist
    	try
    	{
    		if (policyDao.getPolicyByName(policy.getPolicyName()) != null)
    			return Response.status(Status.CONFLICT).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up cert policy.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{    		
    		final org.nhindirect.config.store.CertPolicy entityPolicy = EntityModelConversion.toEntityCertPolicy(policy);
    		
    		policyDao.addPolicy(entityPolicy);
    		
    		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
    		final URI newLoc = newLocBuilder.path("certpolicy/" + policy.getPolicyName()).build();
    		    		
    		return Response.created(newLoc).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding trust cert policy.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }  
    
    @Path("{policyName}")
    @DELETE
    public Response removePolicyByName(@PathParam("policyName") String policyName)
    {
    	// make sure it exists
    	org.nhindirect.config.store.CertPolicy enitityPolicy = null;
    	try
    	{
    		enitityPolicy = policyDao.getPolicyByName(policyName); 
    		if (enitityPolicy == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing cert policy.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{
    		policyDao.deletePolicies(new long[] {enitityPolicy.getId()});
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error deleting cert policy.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}  
    }    
    
    @POST 
    @Path("{policyName}/policyAttributes")  
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response updatePolicyAttributes(@PathParam("policyName") String policyName, CertPolicy policyData)
    { 
       	// make sure the policy exists
    	org.nhindirect.config.store.CertPolicy entityPolicy;
    	try
    	{
    		entityPolicy = policyDao.getPolicyByName(policyName);
    		if (entityPolicy == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up cert policy.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}

    	// update the policy
    	try
    	{
    		policyDao.updatePolicyAttributes(entityPolicy.getId(), policyData.getPolicyName(), policyData.getLexicon(), policyData.getPolicyData());
    		
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error updating cert policy attributes.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @Path("groups")  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPolicyGroups()
    {
    	Collection<org.nhindirect.config.store.CertPolicyGroup> retGroups;
    	
    	try
    	{
    		retGroups = policyDao.getPolicyGroups();
    		if (retGroups.isEmpty())
    			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up cert policy groups.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<CertPolicyGroup> modelGroups = new ArrayList<CertPolicyGroup>();
    	
    	for (org.nhindirect.config.store.CertPolicyGroup group : retGroups)	
    		modelGroups.add(EntityModelConversion.toModelCertPolicyGroup(group));
    	
		final GenericEntity<Collection<CertPolicyGroup>> entity = new GenericEntity<Collection<CertPolicyGroup>>(modelGroups) {};
		
		return Response.ok(entity).cacheControl(noCache).build();      	
    }  
    
    @Path("groups/{groupName}")  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPolicyGroupByName(@PathParam("groupName") String groupName)
    {
    	try
    	{
    		final org.nhindirect.config.store.CertPolicyGroup retGroup = policyDao.getPolicyGroupByName(groupName);
    		
    		if (retGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();

    		final CertPolicyGroup modelGroup = EntityModelConversion.toModelCertPolicyGroup(retGroup);
    		
    		return Response.ok(modelGroup).cacheControl(noCache).build(); 
    		
    	}
    	catch (Throwable e)
    	{
    		log.error("Error looking up cert policy group", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}     	
    }
    
    @PUT 
    @Consumes(MediaType.APPLICATION_JSON)  
    @Path("groups")
    public Response addPolicyGroup(@Context UriInfo uriInfo, CertPolicyGroup group)
    {
    	// make sure it doesn't exist
    	try
    	{
    		if (policyDao.getPolicyGroupByName(group.getPolicyGroupName()) != null)
    			return Response.status(Status.CONFLICT).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up cert policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{    		
    		final org.nhindirect.config.store.CertPolicyGroup entityGroup = EntityModelConversion.toEntityCertPolicyGroup(group);
    		
    		policyDao.addPolicyGroup(entityGroup);
    		
    		final UriBuilder newLocBuilder = uriInfo.getBaseUriBuilder();
    		final URI newLoc = newLocBuilder.path("certpolicy/group+/" + group.getPolicyGroupName()).build();
    		    		
    		return Response.created(newLoc).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding trust cert policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }  
    
    @Path("groups/{groupName}")
    @DELETE
    public Response removePolicyGroupByName(@PathParam("groupName") String groupName)
    {
    	// make sure it exists
    	org.nhindirect.config.store.CertPolicyGroup enitityGroup = null;
    	try
    	{
    		enitityGroup = policyDao.getPolicyGroupByName(groupName); 
    		if (enitityGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up existing cert policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	try
    	{
    		policyDao.deletePolicyGroups(new long[] {enitityGroup.getId()});
    		
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error deleting cert policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}  
    }       
    
    @POST 
    @Path("groups/{groupName}/groupAttributes")  
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response updateGroupAttributes(@PathParam("groupName") String groupName, String newGroupName)
    { 
       	// make sure the policy exists
    	org.nhindirect.config.store.CertPolicyGroup entityGroup;
    	try
    	{
    		entityGroup = policyDao.getPolicyGroupByName(groupName);
    		if (entityGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up cert policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}

    	// update the group
    	try
    	{
    		policyDao.updateGroupAttributes(entityGroup.getId(), newGroupName);
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error updating cert policy group attributes.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }  
    
    @POST 
    @Path("groups/uses/{group}")  
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response addPolicyUseToGroup(@PathParam("group") String groupName, CertPolicyGroupUse use)
    {
    	// make sure the group exists
    	org.nhindirect.config.store.CertPolicyGroup entityGroup;
    	try
    	{
    		entityGroup = policyDao.getPolicyGroupByName(groupName);
    		if (entityGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	// make sure the policy exists
    	org.nhindirect.config.store.CertPolicy entityPolicy;
    	try
    	{
    		entityPolicy = policyDao.getPolicyByName(use.getPolicy().getPolicyName());
    		if (entityPolicy == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up policy.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
       	// associate the group and policy
    	try
    	{
    		policyDao.addPolicyUseToGroup(entityGroup.getId(), entityPolicy.getId(), org.nhindirect.config.store.CertPolicyUse.valueOf(use.getPolicyUse().toString()), 
    				use.isIncoming(), use.isOutgoing());
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error adding cert policy to group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    
    @POST 
    @Path("groups/uses/{group}/removePolicy")
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response removedPolicyUseFromGroup(@PathParam("group") String groupName, CertPolicyGroupUse use)
    {
    	// make sure the group exists
    	org.nhindirect.config.store.CertPolicyGroup entityGroup;
    	try
    	{
    		entityGroup = policyDao.getPolicyGroupByName(groupName);
    		if (entityGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}    
    	
    	final org.nhindirect.config.store.CertPolicyUse entityUse = org.nhindirect.config.store.CertPolicyUse.valueOf(use.getPolicyUse().toString());
    	
    	boolean reltnFound = false;
		if (entityGroup.getCertPolicyGroupReltn() != null)
		{
			
			for (org.nhindirect.config.store.CertPolicyGroupReltn groupReltn : entityGroup.getCertPolicyGroupReltn())
			{
				if (groupReltn.getCertPolicy().getPolicyName().equals(use.getPolicy().getPolicyName()) &&
						groupReltn.isIncoming() == use.isIncoming() && groupReltn.isOutgoing() == use.isOutgoing() &&
						groupReltn.getPolicyUse() == entityUse)
				{
					try
					{
						policyDao.removePolicyUseFromGroup(groupReltn.getId());
						reltnFound = true;
					}
			    	catch (Exception e)
			    	{
			    		log.error("Error removing cert policy from group.", e);
			    		return Response.serverError().cacheControl(noCache).build();
			    	}
				}
			}
		}
		
		if (reltnFound == false)
			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
		
		return Response.ok().cacheControl(noCache).build();
    }
    
    @GET
    @Path("groups/domain")  
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPolicyGroupDomainReltns()
    {
    	Collection<org.nhindirect.config.store.CertPolicyGroupDomainReltn> retReltn;
    	
    	try
    	{
    		retReltn = policyDao.getPolicyGroupDomainReltns();
    		if (retReltn.isEmpty())
    			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up policy group/domain relations.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<CertPolicyGroupDomainReltn> modelReltns = new ArrayList<CertPolicyGroupDomainReltn>();
    	
    	for (org.nhindirect.config.store.CertPolicyGroupDomainReltn reltn : retReltn)	
    		modelReltns.add(EntityModelConversion.toModelCertPolicyGroupDomainReltn(reltn));
    	
		final GenericEntity<Collection<CertPolicyGroupDomainReltn>> entity = new GenericEntity<Collection<CertPolicyGroupDomainReltn>>(modelReltns){};
		
		return Response.ok(entity).cacheControl(noCache).build();     
    }
    
    @GET
    @Path("groups/domain/{domain}")  
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPolicyGroupsByDomain(@PathParam("domain") String domainName)
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
    	
    	Collection<org.nhindirect.config.store.CertPolicyGroupDomainReltn> retPolicyGroups;
    	
    	try
    	{
    		retPolicyGroups = policyDao.getPolicyGroupsByDomain(entityDomain.getId());
    		if (retPolicyGroups.isEmpty())
    			return Response.status(Status.NO_CONTENT).cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up cert policy groups.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    	
    	final Collection<CertPolicyGroup> modelGroups = new ArrayList<CertPolicyGroup>();
    	
    	for (org.nhindirect.config.store.CertPolicyGroupDomainReltn reltn : retPolicyGroups)	
    		modelGroups.add(EntityModelConversion.toModelCertPolicyGroup(reltn.getCertPolicyGroup()));
    	
		final GenericEntity<Collection<CertPolicyGroup>> entity = new GenericEntity<Collection<CertPolicyGroup>>(modelGroups) {};
		
		return Response.ok(entity).cacheControl(noCache).build();      	
    }
    
    @POST 
    @Path("groups/domain/{group}/{domain}")  
    public Response associatePolicyGroupToDomain(@PathParam("group") String groupName, @PathParam("domain") String domainName)
    {
    	// make sure the group exists
    	org.nhindirect.config.store.CertPolicyGroup entityGroup;
    	try
    	{
    		entityGroup = policyDao.getPolicyGroupByName(groupName);
    		if (entityGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up policy group.", e);
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
    	
       	// associate the domain and group
    	try
    	{
    		policyDao.associatePolicyGroupToDomain(entityDomain.getId(), entityGroup.getId());
    		return Response.noContent().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error associating policy group to domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}
    }
    
    @DELETE 
    @Path("groups/domain/{group}/{domain}")  
    public Response disassociatePolicyGroupFromDomain(@PathParam("group") String groupName, @PathParam("domain") String domainName)
    {
    	// make sure the group exists
    	org.nhindirect.config.store.CertPolicyGroup entityGroup;
    	try
    	{
    		entityGroup = policyDao.getPolicyGroupByName(groupName);
    		if (entityGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up policy group.", e);
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
    		policyDao.disassociatePolicyGroupFromDomain(entityDomain.getId(), entityGroup.getId());
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error disassociating policy group from domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	} 
    }
    
    @DELETE 
    @Path("groups/domain/{domain}/deleteFromDomain")  
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response disassociatePolicyGroupsFromDomain(@PathParam("domain") String domainName)
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
    		policyDao.disassociatePolicyGroupsFromDomain(entityDomain.getId());
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error disassociating policy groups from domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	} 
    }
    
    @DELETE 
    @Path("groups/domain/{group}/deleteFromGroup")  
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response disassociatePolicyGroupFromDomains(@PathParam("group") String groupName)
    {
    	// make sure the group exists
    	org.nhindirect.config.store.CertPolicyGroup entityGroup;
    	try
    	{
    		entityGroup = policyDao.getPolicyGroupByName(groupName);
    		if (entityGroup == null)
    			return Response.status(Status.NOT_FOUND).cacheControl(noCache).build();	
    	}
    	catch (Exception e)
    	{
    		log.error("Error looking up policy group.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	}  
    	
    	// now make the disassociation
    	try
    	{
    		policyDao.disassociatePolicyGroupFromDomains(entityGroup.getId());
    		return Response.ok().cacheControl(noCache).build();
    	}
    	catch (Exception e)
    	{
    		log.error("Error disassociating policy groups from domain.", e);
    		return Response.serverError().cacheControl(noCache).build();
    	} 
    }    
}
