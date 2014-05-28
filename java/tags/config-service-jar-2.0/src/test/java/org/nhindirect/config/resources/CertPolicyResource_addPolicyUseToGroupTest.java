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
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.nhindirect.config.model.CertPolicyUse;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.policy.PolicyLexicon;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertPolicyResource_addPolicyUseToGroupTest 
{
	   protected CertPolicyDao policyDao;
	    
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
			
			protected Collection<CertPolicy> getPoliciesToAdd()
			{
				try
				{
					policies = new ArrayList<CertPolicy>();
					
					CertPolicy policy = new CertPolicy();
					policy.setPolicyName("Policy1");
					policy.setPolicyData(new byte[] {1,2,3});
					policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
					policies.add(policy);
					
					policy = new CertPolicy();
					policy.setPolicyName("Policy2");
					policy.setPolicyData(new byte[] {1,2,5,6});
					policy.setLexicon(PolicyLexicon.JAVA_SER);
					policies.add(policy);
					
					return policies;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			protected abstract String getGroupNameToAssociate();
			
			protected abstract CertPolicyGroupUse getPolicyUseToAssociate();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Collection<CertPolicy> policiesToAdd = getPoliciesToAdd();
				
				if (policiesToAdd != null)
				{
					for (CertPolicy addPolicy : policiesToAdd)
					{
						try
						{
							resource.path("/api/certpolicy").entity(addPolicy, MediaType.APPLICATION_JSON).put(addPolicy);
						}
						catch (UniformInterfaceException e)
						{
							throw e;
						}
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
				
				resource.path("/api/certpolicy/groups/uses/" + TestUtils.uriEscape(getGroupNameToAssociate())).entity(getPolicyUseToAssociate(), MediaType.APPLICATION_JSON).post();
				
				final CertPolicyGroup getGroup = resource.path("/api/certpolicy/groups/" + TestUtils.uriEscape(getGroupNameToAssociate())).get(CertPolicyGroup.class);

				doAssertions(getGroup);
				
				
			}
				
			protected void doAssertions(CertPolicyGroup group) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testAddPolicyUseToGroup_assertPolicyAdded()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToAssociate()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.TRUST);
					use.setPolicy(policies.iterator().next());
					
					return use;
				}
				
				@Override
				protected void doAssertions(CertPolicyGroup group) throws Exception
				{
					
					assertNotNull(group);
					
					assertEquals(getGroupNameToAssociate(), group.getPolicyGroupName());
					assertEquals(1, group.getPolicies().size());
					
					final CertPolicyGroupUse use = group.getPolicies().iterator().next();
					assertEquals(policies.iterator().next().getPolicyName(), use.getPolicy().getPolicyName());
					assertEquals(CertPolicyUse.TRUST, use.getPolicyUse());
					assertTrue(use.isIncoming());
					assertTrue(use.isOutgoing());
					
				}
			}.perform();
		}		
		
		@Test
		public void testAddPolicyUseToGroup_nonExistantGroup_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupNameToAssociate()
				{
					return "Group4";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToAssociate()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.TRUST);
					use.setPolicy(policies.iterator().next());
					
					return use;
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
		public void testAddPolicyUseToGroup_nonExistantPolicy_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToAssociate()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("bogus");
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.TRUST);
					use.setPolicy(policy);
					
					return use;
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
		public void testAddPolicyUseToGroup_errorInGroupLookup_assertServiceError()  throws Exception
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
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
				}
				
				@Override
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}
				
				@Override
				protected void tearDownMocks()
				{
					super.tearDownMocks();
					
					certService.setCertPolicyDao(policyDao);
				}
				
				@Override
				protected String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToAssociate()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("bogus");
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.TRUST);
					use.setPolicy(policy);
					
					return use;
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
		public void testAddPolicyUseToGroup_errorInPolicyLookup_assertServiceError()  throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).getPolicyByName((String)any());
						
						certService.setCertPolicyDao(mockDAO);
					}
					catch (Throwable t)
					{
						throw new RuntimeException(t);
					}
				}
				
				@Override
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
				}
				
				@Override
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}
				
				@Override
				protected void tearDownMocks()
				{
					super.tearDownMocks();
					
					certService.setCertPolicyDao(policyDao);
				}
				
				@Override
				protected String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToAssociate()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("bogus");
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.TRUST);
					use.setPolicy(policy);
					
					return use;
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
		public void testAddPolicyUseToGroup_errorInAssociate_assertServiceError()  throws Exception
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
						when(mockDAO.getPolicyByName((String)any())).thenReturn(new org.nhindirect.config.store.CertPolicy());
						doThrow(new RuntimeException()).when(mockDAO).addPolicyUseToGroup(eq(0L), eq(0L), (org.nhindirect.config.store.CertPolicyUse)any(),
								eq(true), eq(true));
						
						certService.setCertPolicyDao(mockDAO);
					}
					catch (Throwable t)
					{
						throw new RuntimeException(t);
					}
				}
				
				@Override
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
				}
				
				@Override
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}
				
				@Override
				protected void tearDownMocks()
				{
					super.tearDownMocks();
					
					certService.setCertPolicyDao(policyDao);
				}
				
				@Override
				protected String getGroupNameToAssociate()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToAssociate()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("Policy1");
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.TRUST);
					use.setPolicy(policy);
					
					return use;
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
