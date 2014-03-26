package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.store.dao.CertPolicyDao;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertPolicyResource_getPolicyGroupByNameTest 
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
			
			protected abstract String getGroupToRetrieve();
			
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
					
					final CertPolicyGroup getGroup = resource.path("/api/certpolicy/groups/" + TestUtils.uriEscape(getGroupToRetrieve())).get(CertPolicyGroup.class);

					doAssertions(getGroup);
				}
				catch (UniformInterfaceException e)
				{
					if (e.getResponse().getStatus() == 404)
						doAssertions(null);
					else
						throw e;
				}
				
			}
				
			protected void doAssertions(CertPolicyGroup group) throws Exception
			{
				
			}
		}
		
		@Test
		public void testGetGroupByName_existingGroup_assertGroupRetrieved()  throws Exception
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
				protected String getGroupToRetrieve()
				{
					return "Group1";
				}
				
				@Override
				protected void doAssertions(CertPolicyGroup group) throws Exception
				{
					assertNotNull(group);
					
					final CertPolicyGroup addedGroup = this.groups.iterator().next();

					assertEquals(addedGroup.getPolicyGroupName(), group.getPolicyGroupName());
					assertTrue(group.getPolicies().isEmpty());	
					
				}
			}.perform();
		}		
		
		@Test
		public void testGetGroupByName_nonExistantGroup_assertGroupNotRetrieved()  throws Exception
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
				protected String getGroupToRetrieve()
				{
					return "Group144";
				}
				
				@Override
				protected void doAssertions(CertPolicyGroup group) throws Exception
				{
					assertNull(group);
					
				}
			}.perform();
		}		
		
		@Test
		public void testGetGroupByName_errorInLookup_assertServiceError()  throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).getPolicyGroupByName((String)any());
						
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
				protected String getGroupToRetrieve()
				{
					return "Group1";
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
