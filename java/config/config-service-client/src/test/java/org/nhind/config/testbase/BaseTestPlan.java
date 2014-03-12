package org.nhind.config.testbase;


import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.impl.DefaultAddressService;
import org.nhind.config.rest.impl.DefaultAnchorService;
import org.nhind.config.rest.impl.DefaultCertPolicyService;
import org.nhind.config.rest.impl.DefaultCertificateService;
import org.nhind.config.rest.impl.DefaultDNSService;
import org.nhind.config.rest.impl.DefaultDomainService;
import org.nhind.config.rest.impl.DefaultSettingService;
import org.nhind.config.rest.impl.DefaultTrustBundleService;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.BootstrapBasicAuthServiceSecurityManager;
import org.nhindirect.common.rest.HttpClientFactory;
import org.nhindirect.common.rest.ServiceSecurityManager;


public abstract class BaseTestPlan extends SpringBaseTest
{
	protected static final String DOMAIN_SERVICE = "DomainService";
	protected static final String ADDRESS_SERVICE = "AddressService";
	protected static final String ANCHOR_SERVICE = "AnchorService";
	protected static final String DNS_SERVICE = "DNSService";
	protected static final String SETTING_SERVICE = "SettingService";
	protected static final String CERT_SERVICE = "CertService";
	protected static final String CERT_POLICY_SERVICE = "CertPolicyService";
	protected static final String TRUST_BUNDLE_SERVICE = "TrustBundleService";
	
	private static final Map<String, Class<?>> serviceClassMap;
	
	static protected String filePrefix;
	
    static
    {

		// check for Windows... it doens't like file://<drive>... turns it into FTP
		File file = new File("./src/test/resources/bundles/signedbundle.p7b");
		if (file.getAbsolutePath().contains(":/"))
			filePrefix = "file:///";
		else
			filePrefix = "file:///";
		
		// services that will be dynamically instanciated
		serviceClassMap = new HashMap<String, Class<?>>();
		serviceClassMap.put(DOMAIN_SERVICE, DefaultDomainService.class);
		serviceClassMap.put(ADDRESS_SERVICE, DefaultAddressService.class);
		serviceClassMap.put(ANCHOR_SERVICE, DefaultAnchorService.class);
		serviceClassMap.put(DNS_SERVICE, DefaultDNSService.class);	
		serviceClassMap.put(SETTING_SERVICE, DefaultSettingService.class);		
		serviceClassMap.put(CERT_SERVICE, DefaultCertificateService.class);	
		serviceClassMap.put(CERT_POLICY_SERVICE, DefaultCertPolicyService.class);	
		serviceClassMap.put(TRUST_BUNDLE_SERVICE, DefaultTrustBundleService.class);	
		
    }
    
	
	public void perform() throws Exception 
	{
		try 
		{
			setUp();
			setupMocks();
			Exception exception = null;
			try 
			{
				performInner();
			} 
			catch (Exception e) 
			{
				exception = e;
			}
			assertException(exception);
		} 
		finally 
		{
			tearDownMocks();
		}
	}

	protected abstract void performInner() throws Exception;

	protected void setupMocks() {
	}

	protected void tearDownMocks() {
	}

	protected void assertException(Exception exception) throws Exception {
		// default case should not throw an exception
		if (exception != null) {
			throw exception;
		}
	}
	
	public static AbstractSecuredService getService(String serviceURL, String serviceName)
	{
		final Class<?> clazz = serviceClassMap.get(serviceName);
		
		if (clazz == null)
			throw new IllegalArgumentException("Service name " + serviceName + " is an unknown service");
		
		try
		{
			final Constructor<?> ctr = clazz.getDeclaredConstructor(String.class, HttpClient.class, ServiceSecurityManager.class);
		
			final AbstractSecuredService service = (AbstractSecuredService)ctr.newInstance(serviceURL, HttpClientFactory.createHttpClient(), getTestServiceSecurityManager());
			
			return service;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static ServiceSecurityManager getTestServiceSecurityManager() 
	{
		//return new OpenServiceSecurityManager();
		return new BootstrapBasicAuthServiceSecurityManager("gm2552", "password");
	}
}
