package org.nhindirect.config.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.store.dao.CertPolicyDao;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertPolicyResource_getPolicyGroupsTest 
{
	   protected CertPolicyDao policyDao;
	    
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					policyDao = (CertPolicyDao)ConfigServiceRunner.getSpringApplicationContext().getBean("certPolicyDao");
					
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
			
			
			@Override
			protected void performInner() throws Exception
			{				
				
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
				
				try
				{
					
					final GenericType<ArrayList<CertPolicyGroup>> genType = new GenericType<ArrayList<CertPolicyGroup>>(){};
					final Collection<CertPolicyGroup> getGroups = resource.path("/api/certpolicy/groups").get(genType);

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
		public void testGetAllPolicies_assertPoliciesRetrieved()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<CertPolicyGroup> groups;
				
				@Override
				protected Collection<CertPolicyGroup> getGroupsToAdd()
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
				protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
				{
					assertNotNull(groups);
					assertEquals(2, groups.size());
					
					final Iterator<CertPolicyGroup> addedGroupsIter = this.groups.iterator();
					
					for (CertPolicyGroup retrievedGroup : groups)
					{	
						final CertPolicyGroup addedGroup = addedGroupsIter.next(); 
						
						assertEquals(addedGroup.getPolicyGroupName(), retrievedGroup.getPolicyGroupName());
						assertTrue(retrievedGroup.getPolicies().isEmpty());

					}
					
				}
			}.perform();
		}		
		
		@Test
		public void testGetAllPolicyGroups_noGroupsInStore_assertNoPoliciesRetrieved()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
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
		public void testGetAllPolicyGroups_errorInLookup_assertServiceError()  throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).getPolicyGroups();
						
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
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
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
