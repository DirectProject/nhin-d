package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.rest.DomainService;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.resources.CertPolicyResource;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.config.store.dao.DomainDao;

public class DefaultCertPolicyService_getPolicyGroupsByDomainTest 
{
    protected CertPolicyDao policyDao;
    
    protected DomainDao domainDao;
    
	static CertPolicyService resource;
	static DomainService domainResource;	
	
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
				
				resource = 	(CertPolicyService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), new OpenServiceSecurityManager(), CERT_POLICY_SERVICE);	
				domainResource = 	(DomainService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), new OpenServiceSecurityManager(), DOMAIN_SERVICE);	
				
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
					domainResource.addDomain(addDomain);
				}
				catch (ServiceException e)
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
						resource.addPolicyGroup(addGroup);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			
			if (addDomain != null & groupsToAdd != null)
				resource.associatePolicyGroupToDomain(getGroupNameToAssociate(), getDomainNameToAssociate());
			
			try
			{
				final Collection<CertPolicyGroup> getGroups = resource.getPolicyGroupsByDomain(getDomainNameToLookup());
				doAssertions(getGroups);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
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
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(404, ex.getResponseCode());
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
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
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
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}					
}
	
