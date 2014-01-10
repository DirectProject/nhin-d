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
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.dao.CertificateDao;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class CertificateResource_removeCertificatesByOwnerTest 
{
	   protected CertificateDao certDao;
	    
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					certDao = (CertificateDao)ConfigServiceRunner.getSpringApplicationContext().getBean("certificateDao");
					
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

			protected abstract Collection<Certificate> getCertsToAdd() throws Exception;
			
			protected abstract String getOwnerToRemove();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Collection<Certificate> certsToAdd = getCertsToAdd();

				if (certsToAdd != null)
				{
					for (Certificate addCert : certsToAdd)
					{
						try
						{
							resource.path("/api/certificate").entity(addCert, MediaType.APPLICATION_JSON).put(addCert);
						}
						catch (UniformInterfaceException e)
						{
							throw e;
						}
					}			
				}
				
				try
				{
					resource.path("/api/certificate/" + TestUtils.uriEscape(getOwnerToRemove())).delete();

				}
				catch (UniformInterfaceException e)
				{
					throw e;
				}
				
				
				doAssertions();
			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testRemoveCertificatesByIds_removeExistingCerts_assertCertRemoved() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Certificate> certs;
				
				@Override
				protected Collection<Certificate> getCertsToAdd() throws Exception
				{
					try
					{
						certs = new ArrayList<Certificate>();
						
						Certificate cert = new Certificate();					
						cert.setData(TestUtils.loadCert("gm2552.der").getEncoded());
						
						certs.add(cert);
			
						cert = new Certificate();					
						cert.setData(TestUtils.loadCert("umesh.der").getEncoded());
						
						certs.add(cert);
						
						return certs;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}
				
				@Override
				protected String getOwnerToRemove()
				{
					return "gm2552@securehealthemail.com";
				}
				
				@Override
				protected void doAssertions() throws Exception
				{
					final Collection<org.nhindirect.config.store.Certificate> certs = certDao.list((String)null);
					assertEquals(1, certs.size());
				}
			}.perform();
		}
		
		@Test
		public void testRemoveCertificatesByIds_errorInDelete_assertServierError() throws Exception
		{
			new TestPlan()
			{
				
				protected CertificateResource certService;

				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						certService = (CertificateResource)ConfigServiceRunner.getSpringApplicationContext().getBean("certificateResource");

						CertificateDao mockDAO = mock(CertificateDao.class);
						doThrow(new RuntimeException()).when(mockDAO).delete((String)any());
						
						certService.setCertificateDao(mockDAO);
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
					
					certService.setCertificateDao(certDao);
				}			
				
				@Override
				protected Collection<Certificate> getCertsToAdd()
				{
					return null;
				}

				
				@Override
				protected String getOwnerToRemove()
				{
					return "gm2552@securehealthemail.com";
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
