package org.nhindirect.config.resources;
 
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.config.store.dao.DomainDao;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertPolicyResource_getPolicyGroupDomainReltnsTest 
{
	   protected CertPolicyDao policyDao;
	   protected DomainDao domainDao;  
	   
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			protected Collection<CertPolicyGroup> groups;
			
			protected Collection<CertPolicy> policies;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					policyDao = (CertPolicyDao)ConfigServiceRunner.getSpringApplicationContext().getBean("certPolicyDao");
					domainDao =  (DomainDao)ConfigServiceRunner.getSpringApplicationContext().getBean("domainDao");
					
					resource = 	getResource(ConfigServiceRunner.getConfigServiceURL());		
				}
				catch (Throwable t)
				{
					throw new RuntimeException(t);
				}
			}
			
			@Override
			protected void tearDownMocks()
			{

			}

			protected abstract Collection<CertPolicyGroup> getGroupsToAdd();
			
			protected abstract Domain getDomainToAdd();
			
			protected abstract String getGroupNameToAssociate();
			
			protected abstract String getDomainNameToAssociate();
			
			@Override
			protected void performInner() throws Exception
			{				
				final Domain addDomain = getDomainToAdd();
				
				if (addDomain != null)
				{
					try
					{
						resource.path("/api/domain").entity(addDomain, MediaType.APPLICATION_JSON).put(addDomain);
					}
					catch (UniformInterfaceException e)
					{
						throw e;
					}
				}
				
				final Collection<CertPolicyGroup> groupsToAdd = getGroupsToAdd();
				
				if (groupsToAdd != null)
				{
					for (CertPolicyGroup addGroup : groupsToAdd)
					{
						try
						{
							resource.path("/api/certpolicy/groups").entity(addGroup, MediaType.APPLICATION_JSON).put(addGroup);
						}
						catch (UniformInterfaceException e)
						{
							throw e;
						}
					}
				}
				
				if (addDomain != null & groupsToAdd != null)
					resource.path("/api/certpolicy/groups/domain/" + TestUtils.uriEscape(getGroupNameToAssociate()) + 
						"/" + TestUtils.uriEscape(getDomainNameToAssociate())).post();
				
				
				
				try
				{
					
					final GenericType<ArrayList<CertPolicyGroupDomainReltn>> genType = new GenericType<ArrayList<CertPolicyGroupDomainReltn>>(){};
					final Collection<CertPolicyGroupDomainReltn> getReltns = resource.path("/api/certpolicy/groups/domain").get(genType);

					doAssertions(getReltns);
				}
				catch (UniformInterfaceException e)
				{
					if (e.getResponse().getStatus() == 204)
						doAssertions(new ArrayList<CertPolicyGroupDomainReltn>());
					else
						throw e;
				}
				
				
			}
				
			protected void doAssertions(Collection<CertPolicyGroupDomainReltn> reltns) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetPolicyGroupDomainReltns_assertReltnsRetrieved()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected  Collection<CertPolicyGroup> getGroupsToAdd()
				{
					try
					{
						groups = new ArrayList<CertPolicyGroup>();
						
						CertPolicyGroup group = new CertPolicyGroup();
						group.setPolicyGroupName("Group1");
						groups.add(group);
						
						group = new CertPolicyGroup();
						group.setPolicyGroupName("Group2");
						groups.add(group);
						
						return groups;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}
				
				@Override
				protected  Domain getDomainToAdd()
				{
					final Address postmasterAddress = new Address();
					postmasterAddress.setEmailAddress("me@test.com");
					
					Domain domain = new Domain();
					
					domain.setDomainName("test.com");
					domain.setStatus(EntityStatus.ENABLED);
					domain.setPostmasterAddress(postmasterAddress);			
					
					return domain;
				}
				
				
				@Override
				protected  String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected  String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected void doAssertions(Collection<CertPolicyGroupDomainReltn> reltns) throws Exception
				{
					
					assertNotNull(reltns);
					
					assertEquals(1, reltns.size());
					
					final CertPolicyGroupDomainReltn reltn = reltns.iterator().next();
					
					assertEquals("test.com", reltn.getDomain().getDomainName());
					assertEquals("Group1", reltn.getPolicyGroup().getPolicyGroupName());
					
				}
			}.perform();
		}	
		
		@Test
		public void testGetPolicyGroupDomainReltns_noReltnsInStore_assertNoReltnsRetrieved()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected  Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
				}
				
				@Override
				protected  Domain getDomainToAdd()
				{
					return null;
				}
				
				
				@Override
				protected  String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected  String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected void doAssertions(Collection<CertPolicyGroupDomainReltn> reltns) throws Exception
				{
					
					assertNotNull(reltns);
					
					assertEquals(0, reltns.size());
					
				}
			}.perform();
		}	
		
		@Test
		public void testGetPolicyGroupDomainReltns_errorInLookup_assertServiceError()  throws Exception
		{
			new TestPlan()
			{

				protected CertPolicyResource certService;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						certService = (CertPolicyResource)ConfigServiceRunner.getSpringApplicationContext().getBean("certPolicyResource");

						CertPolicyDao mockDAO = mock(CertPolicyDao.class);
						doThrow(new RuntimeException()).when(mockDAO).getPolicyGroupDomainReltns();
						
						certService.setCertPolicyDao(mockDAO);
					}
					catch (Throwable t)
					{
						throw new RuntimeException(t);
					}
				}
				
				@Override
				protected void tearDownMocks()
				{
					super.tearDownMocks();
					
					certService.setCertPolicyDao(policyDao);
				}
				
				@Override
				protected  Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
				}
				
				@Override
				protected  Domain getDomainToAdd()
				{
					return null;
				}
				
				
				@Override
				protected  String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected  String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}			
}
