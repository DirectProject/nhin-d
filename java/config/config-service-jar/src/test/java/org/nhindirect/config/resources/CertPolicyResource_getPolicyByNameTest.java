package org.nhindirect.config.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.policy.PolicyLexicon;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertPolicyResource_getPolicyByNameTest 
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
			
			protected abstract String getPolicyToRetrieve();
			
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
					
					final CertPolicy getPolicy = resource.path("/api/certpolicy/" + TestUtils.uriEscape(getPolicyToRetrieve())).get(CertPolicy.class);

					doAssertions(getPolicy);
				}
				catch (UniformInterfaceException e)
				{
					if (e.getResponse().getStatus() == 404)
						doAssertions(null);
					else
						throw e;
				}
				
			}
				
			protected void doAssertions(CertPolicy policy) throws Exception
			{
				
			}
		}
		
		@Test
		public void testGetPolicyByName_getExistingPolicy_assertPolicyRetrieved()  throws Exception
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
				protected String getPolicyToRetrieve()
				{
					return "Policy1";
				}
				
				@Override
				protected void doAssertions(CertPolicy policy) throws Exception
				{
					assertNotNull(policy);
					
					final CertPolicy addedPolicy = this.policies.iterator().next();

					assertEquals(addedPolicy.getPolicyName(), policy.getPolicyName());
					assertTrue(Arrays.equals(addedPolicy.getPolicyData(), policy.getPolicyData()));
					assertEquals(addedPolicy.getLexicon(), policy.getLexicon());					
				}
			}.perform();
		}		

		
		@Test
		public void testGetPolicyByName_nonExistantPolicy_assertPolicyNotRetrieved()  throws Exception
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
				protected String getPolicyToRetrieve()
				{
					return "Policy45";
				}
				
				@Override
				protected void doAssertions(CertPolicy policy) throws Exception
				{
					assertNull(policy);
				
				}
			}.perform();
		}	
		
		@Test
		public void testGetPolicyByName_errorInLookup_assertServiceError()  throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).getPolicyByName((String)any());
						
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
				protected String getPolicyToRetrieve()
				{
					return "Policy1";
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

