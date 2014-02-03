package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.resources.CertPolicyResource;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.config.store.dao.DomainDao;

public class DefaultCertPolicyService_addPolicyGroupTest 
{
    protected CertPolicyDao policyDao;
    
    protected DomainDao domainDao;
    
	static CertPolicyService resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				policyDao = (CertPolicyDao)ConfigServiceRunner.getSpringApplicationContext().getBean("certPolicyDao");
				domainDao =  (DomainDao)ConfigServiceRunner.getSpringApplicationContext().getBean("domainDao");
				
				resource = 	(CertPolicyService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), new OpenServiceSecurityManager(), CERT_POLICY_SERVICE);	

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
						resource.addPolicyGroup(addGroup);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}

			doAssertions();

			
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}
	
	@Test
	public void testGetAddPolicyGroup_assertPolicyGroupsAdded()  throws Exception
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
			protected void doAssertions() throws Exception
			{
				final Collection<org.nhindirect.config.store.CertPolicyGroup> groups = policyDao.getPolicyGroups();
				
				assertNotNull(groups);
				assertEquals(2, groups.size());
				
				final Iterator<CertPolicyGroup> addedGroupsIter = this.groups.iterator();
				
				for (org.nhindirect.config.store.CertPolicyGroup retrievedGroup : groups)
				{	
					final CertPolicyGroup addedGroup = addedGroupsIter.next(); 
					
					assertEquals(addedGroup.getPolicyGroupName(), retrievedGroup.getPolicyGroupName());
					assertTrue(retrievedGroup.getCertPolicyGroupReltn().isEmpty());
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAddPolicyGroup_addDuplicate_assertConflict()  throws Exception
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
					group.setPolicyGroupName("Group1");
					groups.add(group);
					
					return groups;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(409, ex.getResponseCode());
			}
		}.perform();
	}		
	
	@Test
	public void testGetAddPolicyGroup_errorInAdd_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			protected CertPolicyResource certService;
			
			protected Collection<CertPolicyGroup> groups;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					certService = (CertPolicyResource)ConfigServiceRunner.getSpringApplicationContext().getBean("certPolicyResource");

					CertPolicyDao mockDAO = mock(CertPolicyDao.class);
					doThrow(new RuntimeException()).when(mockDAO).addPolicyGroup((org.nhindirect.config.store.CertPolicyGroup)any());
					
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}		
	
	@Test
	public void testGetAddPolicyGroup_errorInLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			protected CertPolicyResource certService;
			
			protected Collection<CertPolicyGroup> groups;
			
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}			
}
