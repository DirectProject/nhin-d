package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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

public class CertPolicyResource_removedPolicyUseFromGroupTest 
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
			
			protected String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			protected CertPolicyGroupUse getPolicyUseToAssociate()
			{
				final CertPolicyGroupUse use = new CertPolicyGroupUse();
				
				use.setIncoming(true);
				use.setOutgoing(true);
				use.setPolicyUse(CertPolicyUse.TRUST);
				use.setPolicy(policies.iterator().next());
				
				return use;
			}
			
			protected abstract String getGroupToRemoveFrom();
			
			protected abstract CertPolicyGroupUse getPolicyUseToRemove();
			
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
				
				// add policy to group
				if (groupsToAdd != null && policiesToAdd != null)
					resource.path("/api/certpolicy/groups/uses/" + TestUtils.uriEscape(getGroupNameToAssociate())).entity(getPolicyUseToAssociate(), MediaType.APPLICATION_JSON).post();
				
				// remove policy from group
				resource.path("/api/certpolicy/groups/uses/" + TestUtils.uriEscape(getGroupToRemoveFrom()) + "/removePolicy").entity(getPolicyUseToRemove(), MediaType.APPLICATION_JSON).post();
				
				// get the group
				final CertPolicyGroup getGroup = resource.path("/api/certpolicy/groups/" + TestUtils.uriEscape(getGroupNameToAssociate())).get(CertPolicyGroup.class);

				doAssertions(getGroup);
			}
				
			protected void doAssertions(CertPolicyGroup group) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testRemovePolicyUseFromGroup_assertPolicyRemoved()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupToRemoveFrom()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
				{
					return getPolicyUseToAssociate();
				}
				
				@Override
				protected void doAssertions(CertPolicyGroup group) throws Exception
				{
					
					assertNotNull(group);
					
					assertEquals(getGroupNameToAssociate(), group.getPolicyGroupName());
					assertEquals(0, group.getPolicies().size());
					
				}
			}.perform();
		}	
		
		@Test
		public void testRemovePolicyUseFromGroup_nonExistantGroup_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupToRemoveFrom()
				{
					return "Group3";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
				{
					return getPolicyUseToAssociate();
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
		public void testRemovePolicyUseFromGroup_nonExistantPolicyUseName_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupToRemoveFrom()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
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
		public void testRemovePolicyUseFromGroup_nonMathingIncomingDirection_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupToRemoveFrom()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					use.setIncoming(false);
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
		public void testRemovePolicyUseFromGroup_nonMathingOutgoingDirection_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupToRemoveFrom()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					use.setIncoming(true);
					use.setOutgoing(false);
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
		public void testRemovePolicyUseFromGroup_nonMathingUse_assertNotFound()  throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getGroupToRemoveFrom()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.PUBLIC_RESOLVER);
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
		public void testRemovePolicyUseFromGroup_errorInGroupLookup_assertServiceError()  throws Exception
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
				protected String getGroupToRemoveFrom()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("Policy1");
					policy.setPolicyData(new byte[] {1,2,3});
					policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.PUBLIC_RESOLVER);
					use.setPolicy(policy);
					
					return use;
				}
				
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					exception.printStackTrace();
					
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}	
		
		@Test
		public void testRemovePolicyUseFromGroup_errorInRemove_assertServiceError()  throws Exception
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
						
						final org.nhindirect.config.store.CertPolicy policy = new org.nhindirect.config.store.CertPolicy();
						policy.setPolicyName("Policy1");
						
						final org.nhindirect.config.store.CertPolicyGroupReltn reltn = new org.nhindirect.config.store.CertPolicyGroupReltn();
						reltn.setIncoming(true);
						reltn.setOutgoing(true);
						reltn.setPolicyUse(org.nhindirect.config.store.CertPolicyUse.TRUST);
						reltn.setCertPolicy(policy);
						
						final org.nhindirect.config.store.CertPolicyGroup group = new org.nhindirect.config.store.CertPolicyGroup();
						reltn.setCertPolicyGroup(group);
						group.setPolicyGroupName("Group1");
						group.setCertPolicyGroupReltn(Arrays.asList(reltn));
						
						when(mockDAO.getPolicyGroupByName((String)any())).thenReturn(group);
						doThrow(new RuntimeException()).when(mockDAO).removePolicyUseFromGroup(eq(0L));
						
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
				protected String getGroupToRemoveFrom()
				{
					return "Group1";
				}
				
				@Override
				protected CertPolicyGroupUse getPolicyUseToRemove()
				{
					final CertPolicyGroupUse use = new CertPolicyGroupUse();
					
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("Policy1");
					policy.setPolicyData(new byte[] {1,2,3});
					policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
					
					use.setIncoming(true);
					use.setOutgoing(true);
					use.setPolicyUse(CertPolicyUse.TRUST);
					use.setPolicy(policy);
					
					return use;
				}
				
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					exception.printStackTrace();
					
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}		
}
