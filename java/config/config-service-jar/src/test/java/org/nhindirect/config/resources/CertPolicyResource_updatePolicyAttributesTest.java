package org.nhindirect.config.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

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

public class CertPolicyResource_updatePolicyAttributesTest 
{
	   protected CertPolicyDao policyDao;
	    
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
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
					
					return policies;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			protected String getPolicyToUpdate()
			{
				return "Policy1";
			}

			
			protected abstract CertPolicy getUpdatePolicyAttributes();
			
			protected abstract String getPolicyUpdatedName();
			
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
				
				resource.path("/api/certpolicy/" + TestUtils.uriEscape(getPolicyToUpdate()) + "/policyAttributes").entity(getUpdatePolicyAttributes(), MediaType.APPLICATION_JSON).post();
				
				final CertPolicy getPolicy = resource.path("/api/certpolicy/" + TestUtils.uriEscape(getPolicyUpdatedName())).get(CertPolicy.class);

				doAssertions(getPolicy);
			}
				
			protected void doAssertions(CertPolicy policy) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testUpdatePolicyAttributes_assertAttributesChanged()  throws Exception
		{
			new TestPlan()
			{
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("Policy 2");
					policy.setLexicon(PolicyLexicon.XML);
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
				{
					return "Policy 2";
				}

				@Override
				protected void doAssertions(CertPolicy policy) throws Exception
				{
					assertNotNull(policies);

						
					assertEquals("Policy 2", policy.getPolicyName());
					assertTrue(Arrays.equals(new byte[] {1,3,9,8}, policy.getPolicyData()));
					assertEquals(PolicyLexicon.XML, policy.getLexicon());
				}
			}.perform();
		}	
		
		@Test
		public void testUpdatePolicyAttributes_nullNameAndLexiconChange_assertAttributesUpdated()  throws Exception
		{
			new TestPlan()
			{
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
				{
					return "Policy1";
				}

				@Override
				protected void doAssertions(CertPolicy policy) throws Exception
				{
					assertNotNull(policies);

						
					assertEquals("Policy1", policy.getPolicyName());
					assertTrue(Arrays.equals(new byte[] {1,3,9,8}, policy.getPolicyData()));
					assertEquals(PolicyLexicon.SIMPLE_TEXT_V1, policy.getLexicon());
				}
			}.perform();
		}	
		
		@Test
		public void testUpdatePolicyAttributes_nonExistantPolicy_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}
				
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
				{
					return "Policy4";
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
		public void testUpdatePolicyAttributes_errorInLookup_assertServiceError()  throws Exception
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
					return null;
				}
				
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
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

		@Test
		public void testUpdatePolicyAttributes_errorInUpdate_assertServiceError()  throws Exception
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
						when(mockDAO.getPolicyByName((String)any())).thenReturn(new org.nhindirect.config.store.CertPolicy());
						doThrow(new RuntimeException()).when(mockDAO).updatePolicyAttributes(eq(0L), (String)any(), 
								(PolicyLexicon)any(), (byte[])any());
						
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
					return null;
				}
				
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
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
