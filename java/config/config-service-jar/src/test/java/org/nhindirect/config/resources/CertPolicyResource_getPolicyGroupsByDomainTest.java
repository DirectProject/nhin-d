package org.nhindirect.config.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

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
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.config.store.dao.DomainDao;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertPolicyResource_getPolicyGroupsByDomainTest 
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
			
			protected abstract String getGroupNameToAssociate();
			
			protected abstract String getDomainNameToAssociate();
			
			protected abstract String getDomainNameToLookup();
			
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
					
					final GenericType<ArrayList<CertPolicyGroup>> genType = new GenericType<ArrayList<CertPolicyGroup>>(){};
					final Collection<CertPolicyGroup> getGroups = resource.path("/api/certpolicy/groups/domain/" + TestUtils.uriEscape(getDomainNameToLookup())).get(genType);

					doAssertions(getGroups);
				}
				catch (UniformInterfaceException e)
				{
					if (e.getResponse().getStatus() == 204)
						doAssertions(new ArrayList<CertPolicyGroup>());
					else
						throw e;
				}
				
				
			}
				
			protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetPolicyGroupsByDomain_assertGroupsRetrieved()  throws Exception
		{
			new TestPlan()
			{

				
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
				protected String getDomainNameToLookup()
				{
					return "test.com";
							
				}
				
				@Override
				protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
				{
					
					assertNotNull(groups);
					
					assertEquals(1, groups.size());
					final CertPolicyGroup group = groups.iterator().next();
					
					assertEquals("Group1", group.getPolicyGroupName());
					
				}
			}.perform();
		}		
		
		@Test
		public void testGetPolicyGroupsByDomain_noGroupsInDomain_assertNoGroupsRetrieved()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected  Collection<CertPolicyGroup> getGroupsToAdd()
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
				protected String getDomainNameToLookup()
				{
					return "test.com";
							
				}
				
				@Override
				protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
				{
					
					assertNotNull(groups);
					
					assertEquals(0, groups.size());
					
				}
			}.perform();
		}	
		
		@Test
		public void testGetPolicyGroupsByDomain_domainNotFound_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected  Collection<CertPolicyGroup> getGroupsToAdd()
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
				protected String getDomainNameToLookup()
				{
					return "test.com1";
							
				}
				
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(404, ex.getResponse().getStatus());
				}
			}.perform();
		}	
		
		@Test
		public void testGetPolicyGroupsByDomain_errorInDomainLookup_assertServiceError()  throws Exception
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

						CertPolicyDao mockPolicyDAO = mock(CertPolicyDao.class);
						DomainDao mockDomainDAO = mock(DomainDao.class);
						
						doThrow(new RuntimeException()).when(mockDomainDAO).getDomainByName((String)any());
						
						certService.setCertPolicyDao(mockPolicyDAO);
						certService.setDomainDao(mockDomainDAO);
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
					certService.setDomainDao(domainDao);
				}
				
				@Override
				protected  Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected  Collection<CertPolicyGroup> getGroupsToAdd()
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
				protected String getDomainNameToLookup()
				{
					return "test.com1";
							
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
		
		@Test
		public void testGetPolicyGroupsByDomain_errorInPolicyGroupLookup_assertServiceError()  throws Exception
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

						CertPolicyDao mockPolicyDAO = mock(CertPolicyDao.class);
						DomainDao mockDomainDAO = mock(DomainDao.class);
						
						when(mockDomainDAO.getDomainByName((String)any())).thenReturn(new org.nhindirect.config.store.Domain());
						doThrow(new RuntimeException()).when(mockPolicyDAO).getPolicyGroupsByDomain(eq(0L));
						
						certService.setCertPolicyDao(mockPolicyDAO);
						certService.setDomainDao(mockDomainDAO);
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
					certService.setDomainDao(domainDao);
				}
				
				@Override
				protected  Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected  Collection<CertPolicyGroup> getGroupsToAdd()
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
				protected String getDomainNameToLookup()
				{
					return "test.com1";
							
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
