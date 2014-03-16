package org.nhindirect.gateway.smtp.config.cert.impl;

import java.io.File;

import junit.framework.TestCase;

import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.gateway.smtp.config.ConfigServiceRunner;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;

public class ConfigServiceCertificateStore_CertBootstrap_Test extends TestCase 
{
	private static final String keyStoreFile = "./target/TempKeyStore";
	
	abstract class TestPlan extends BaseTestPlan 
	{
		
		
		protected ConfigurationServiceProxy proxy;
		
		@Override
		protected void setupMocks() 
		{
			// create the web service and proxy.... not really mocks
			try
			{
				ConfigServiceRunner.startConfigService();
				proxy = new ConfigurationServiceProxy();
				proxy.setEndpoint(ConfigServiceRunner.getConfigServiceURL());
				
				cleanConfig();
				
				removeTestFiles();
				
				CertCacheFactory.getInstance().flushAll();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
        protected void cleanConfig() throws Exception
        {
     	        	             	
        	// clean certificates
        	Certificate[] certs = proxy.listCertificates(0, 0x8FFFF, null);
        	if (certs != null && certs.length > 0)
        	{
        		long[] ids = new long[certs.length];
        		for (int i = 0; i < certs.length; ++i)
        			ids[i] = certs[i].getId();
        		
        		proxy.removeCertificates(ids) ;
        	}
        	
        	removeTestFiles();
			// flush the caches
			CertCacheFactory.getInstance().flushAll();
        } 	
		
        protected void removeTestFiles()
        {
            removeFile("LDAPPrivateCertStore");
            removeFile("LDAPTrustAnchorStore");
            removeFile("LdapCacheStore");
            removeFile("DNSCacheStore");
            removeFile("WSPrivCacheStore");
            removeFile("PublicStoreKeyFile");
            removeFile("WSPublicCacheStore");
            removeFile(keyStoreFile);
            
        }           
        
        protected void removeFile(String filename)
        {
            File delete = new File(filename);
            if (delete.exists())
            	assertTrue(delete.delete());
        }   
        
        
		@Override
		protected abstract void performInner() throws Exception;   
		
		protected class TestConfigServiceCertificateStore extends ConfigServiceCertificateStore
		{
			public TestConfigServiceCertificateStore(ConfigurationServiceProxy proxy)
			{
				super(proxy);
			}	
		}
	}
	
	
	public void testBootstrapStore_DefaultSystemBootStrap_AssertNoRecords() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected void performInner() throws Exception
			{
								
				TestConfigServiceCertificateStore store = new TestConfigServiceCertificateStore(proxy);
				
				assertNull(store.localStoreDelegate);
				assertEquals(0, store.getAllCertificates().size());
			}
		}.perform();
	}		
	
	public void testBootstrapStore_SetNewBootstrap_AssertNoRecords() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected void performInner() throws Exception
			{
				KeyStoreCertificateStore keyStore = new KeyStoreCertificateStore(new File(keyStoreFile), "nH!NdK3yStor3", "31visl!v3s");
				
								
				TestConfigServiceCertificateStore store = new TestConfigServiceCertificateStore(proxy);
				store.setBootStrap(keyStore);
				
				assertNotNull(store.localStoreDelegate);
				assertEquals(0, store.getAllCertificates().size());
			}
		}.perform();
	}		
	
	public void testBootstrapStore_SetNullBootstrap_AssertException() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected void performInner() throws Exception
			{
												
				TestConfigServiceCertificateStore store = new TestConfigServiceCertificateStore(proxy);
				
				boolean exceptionOccured = false;
				
				try
				{
					store.setBootStrap(null);
				}
				catch (IllegalArgumentException e)
				{
					exceptionOccured = true;
				}
				assertTrue(exceptionOccured);
			}
		}.perform();
	}		
		
}
