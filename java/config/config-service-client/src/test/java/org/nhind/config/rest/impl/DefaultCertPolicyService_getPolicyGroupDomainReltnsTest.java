package org.nhind.config.rest.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

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
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.resources.CertPolicyResource;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.config.store.dao.DomainDao;

public class DefaultCertPolicyService_getPolicyGroupDomainReltnsTest 
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
				final Collection<CertPolicyGroupDomainReltn> getReltns = resource.getPolicyGroupDomainReltns();
				doAssertions(getReltns);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
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
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}			
}
