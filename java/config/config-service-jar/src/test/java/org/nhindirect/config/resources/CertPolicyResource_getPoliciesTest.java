package org.nhindirect.config.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.policy.PolicyLexicon;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertPolicyResource_getPoliciesTest 
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

			protected abstract Collection<CertPolicy> getPoliciesToAdd();
			
			
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
				
				try
				{
					
					final GenericType<ArrayList<CertPolicy>> genType = new GenericType<ArrayList<CertPolicy>>(){};
					final Collection<CertPolicy> getPolicies = resource.path("/api/certpolicy/").get(genType);

					doAssertions(getPolicies);
				}
				catch (UniformInterfaceException e)
				{
					if (e.getResponse().getStatus() == 204)
						doAssertions(new ArrayList<CertPolicy>());
					else
						throw e;
				}
				
			}
				
			protected void doAssertions(Collection<CertPolicy> policies) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetAllPolicies_assertPoliciesRetrieved()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<CertPolicy> policies;
				
				@Override
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

				@Override
				protected void doAssertions(Collection<CertPolicy> policies) throws Exception
				{
					assertNotNull(policies);
					assertEquals(2, policies.size());
					
					final Iterator<CertPolicy> addedPoliciesIter = this.policies.iterator();
					
					for (CertPolicy retrievedPolicy : policies)
					{	
						final CertPolicy addedPolicy = addedPoliciesIter.next(); 
						
						assertEquals(addedPolicy.getPolicyName(), retrievedPolicy.getPolicyName());
						assertTrue(Arrays.equals(addedPolicy.getPolicyData(), retrievedPolicy.getPolicyData()));
						assertEquals(addedPolicy.getLexicon(), retrievedPolicy.getLexicon());
					}
					
				}
			}.perform();
		}		
		
		@Test
		public void testGetAllPolicies_noPoliciesInStore_assertNoPoliciesRetrieved()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					try
					{
						return null;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}

				
				protected void doAssertions(Collection<CertPolicy> policies) throws Exception
				{
					assertNotNull(policies);
					assertEquals(0, policies.size());
					
				}
			}.perform();
		}	
		
		@Test
		public void testGetAllPolicies_errorInLookup_assertServiceError()  throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).getPolicies();
						
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
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					try
					{
						return null;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
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
