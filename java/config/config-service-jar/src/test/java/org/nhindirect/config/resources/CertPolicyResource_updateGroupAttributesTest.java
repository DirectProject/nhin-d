package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

public class CertPolicyResource_updateGroupAttributesTest 
{
	   protected CertPolicyDao policyDao;
	    
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			
			protected Collection<CertPolicyGroup> groups;
			
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

			protected Collection<CertPolicyGroup> getGroupsToAdd()
			{
				try
				{
					groups = new ArrayList<CertPolicyGroup>();
					
					CertPolicyGroup group = new CertPolicyGroup();
					group.setPolicyGroupName("Group1");
					groups.add(group);
					
					return groups;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			protected String getGroupToUpdate()
			{
				return "Group1";
			}

			
			protected abstract String getUpdateGroupAttributes();
			
			protected abstract String getGroupUpdatedName();
			
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
				
				resource.path("/api/certpolicy/groups/" + TestUtils.uriEscape(getGroupToUpdate()) + "/groupAttributes").entity(getUpdateGroupAttributes(), MediaType.APPLICATION_JSON).post();
				
				final CertPolicyGroup getGroup = resource.path("/api/certpolicy/groups/" + TestUtils.uriEscape(getGroupUpdatedName())).get(CertPolicyGroup.class);

				doAssertions(getGroup);
				
			}
				
			protected void doAssertions(CertPolicyGroup group) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testUpdateGroupAttributes_updateGroupName_assertNameUpdated()  throws Exception
		{
			new TestPlan()
			{
				@Override
				protected String getUpdateGroupAttributes()
				{
					return "Group2";
				}
				
				@Override
				protected String getGroupUpdatedName()
				{
					return "Group2";
				}
				
				@Override
				protected void doAssertions(CertPolicyGroup group) throws Exception
				{
					assertEquals(getUpdateGroupAttributes(), group.getPolicyGroupName());
				}
			}.perform();
		}
		
		@Test
		public void testUpdateGroupAttributes_nonExistantGroup_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				@Override
				protected String getGroupToUpdate()
				{
					return "Group2";
				}
				
				@Override
				protected String getUpdateGroupAttributes()
				{
					return "Group2";
				}
				
				@Override
				protected String getGroupUpdatedName()
				{
					return "Group2";
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
		public void testUpdateGroupAttributes_errorInLookup_assertServiceError()  throws Exception
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
				
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
				}
				
				@Override
				protected String getUpdateGroupAttributes()
				{
					return "Group2";
				}
				
				@Override
				protected String getGroupUpdatedName()
				{
					return "Group2";
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
		public void testUpdateGroupAttributes_errorInUpdate_assertServiceError()  throws Exception
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
						when(mockDAO.getPolicyGroupByName((String)any())).thenReturn(new org.nhindirect.config.store.CertPolicyGroup());
						doThrow(new RuntimeException()).when(mockDAO).updateGroupAttributes(eq(0L), (String)any());
						
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
				
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
				}
				
				@Override
				protected String getUpdateGroupAttributes()
				{
					return "Group2";
				}
				
				@Override
				protected String getGroupUpdatedName()
				{
					return "Group2";
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
