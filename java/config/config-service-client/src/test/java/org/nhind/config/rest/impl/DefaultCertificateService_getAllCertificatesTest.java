package org.nhind.config.rest.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;

import org.nhind.config.rest.CertificateService;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhind.config.testbase.TestUtils;
import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.resources.CertificateResource;

import org.nhindirect.config.store.dao.CertificateDao;
import org.nhindirect.stagent.cert.Thumbprint;

public class DefaultCertificateService_getAllCertificatesTest 
{
    protected CertificateDao certDao;
    
	static CertificateService resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				certDao = (CertificateDao)ConfigServiceRunner.getSpringApplicationContext().getBean("certificateDao");
				
				resource = 	(CertificateService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), new OpenServiceSecurityManager(), CERT_SERVICE);	

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
		
		protected abstract Collection<Certificate> getCertsToAdd();
		
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
						resource.addCertificate(addCert);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			
			try
			{
				

				final Collection<Certificate> getCertificates = resource.getAllCertificates();

				doAssertions(getCertificates);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<Certificate>());
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(Collection<Certificate> certs) throws Exception
		{
			
		}
	}	

	
	@Test
	public void testGetAllCertificates_assertCertsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Certificate> certs;
			
			@Override
			protected Collection<Certificate> getCertsToAdd()
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
			protected void doAssertions(Collection<Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(2, certs.size());
				
				final Iterator<Certificate> addedCertsIter = this.certs.iterator();
				
				for (Certificate retrievedCert : certs)
				{	
					final Certificate addedCert = addedCertsIter.next(); 
					
					final X509Certificate retrievedX509Cert = CertUtils.toX509Certificate(retrievedCert.getData());
					final X509Certificate addedX509Cert = CertUtils.toX509Certificate(addedCert.getData());
					
					assertEquals(CertUtils.getOwner(addedX509Cert), retrievedCert.getOwner());
					assertEquals(Thumbprint.toThumbprint(addedX509Cert).toString(), retrievedCert.getThumbprint());
					assertEquals(retrievedX509Cert, addedX509Cert);
					assertEquals(EntityStatus.NEW, retrievedCert.getStatus());
					assertEquals(addedX509Cert.getNotAfter(), retrievedCert.getValidEndDate().getTime());
					assertEquals(addedX509Cert.getNotBefore(), retrievedCert.getValidStartDate().getTime());
				}
				
			}
		}.perform();
	}			
	
	@Test
	public void testGetAllCertificates_noCertsInStore_assertNoCertsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected Collection<Certificate> getCertsToAdd()
			{
				return null;
			}

			
			@Override
			protected void doAssertions(Collection<Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(0, certs.size());
			}
		}.perform();
	}	
	
	@Test
	public void testGetAllCertificates_errorInLookup_assertServerError() throws Exception
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
					doThrow(new RuntimeException()).when(mockDAO).list(eq((String)null));
					
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}		
}
