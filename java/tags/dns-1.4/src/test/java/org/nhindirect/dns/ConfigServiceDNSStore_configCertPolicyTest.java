package org.nhindirect.dns;

import java.net.URL;

import org.nhind.config.CertPolicy;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.PolicyLexicon;
import org.nhindirect.dns.util.ConfigServiceRunner;

import junit.framework.TestCase;

public class ConfigServiceDNSStore_configCertPolicyTest extends TestCase
{
	static final String VALID_POLICY = "(X509.TBS.EXTENSION.KeyUsage & 32) > 0";
	static final String INVALID_VALID_POLICY = "(X509.TBS.EXTENSION.KeyUsage4fds & | 32) > 0";
	
	protected ConfigurationServiceProxy proxy;
	
	public void setUp()
	{

		try
		{
			if (!ConfigServiceRunner.isServiceRunning())
				ConfigServiceRunner.startConfigService();
			proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());

			cleanRecords();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void cleanRecords() throws Exception
	{
		CertPolicy[] pols = proxy.getPolicies();

		if (pols != null && pols.length > 0)
		{
			final Long[] ids = new Long[pols.length];
			for (int i = 0; i < pols.length; ++i)
				ids[i] = pols[i].getId();
			
			proxy.deletePolicies(ids);
		}
		pols = proxy.getPolicies();

		assertNull(pols);
	}
	
	public void testConfigCertPolicy_noJVMParam_assertNoPolicyConfiged() throws Exception
	{
		final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
		
		assertNull(store.polExpression);
		assertNull(store.polFilter);
	}
	
	public void testConfigCertPolicy_policyDoesNotExists_assertNoPolicyConfiged() throws Exception
	{
		System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "NoPolicy");
		
		try
		{
			final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
			
			assertNull(store.polExpression);
			assertNull(store.polFilter);
		}
		finally
		{
			System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "");
		}	
	}
	
	public void testConfigCertPolicy_invalidPolicy_assertNoPolicyConfiged() throws Exception
	{
		System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "InvalidPolicy");
		
		try
		{
			final CertPolicy policy = new CertPolicy();
			policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
			policy.setPolicyName("InvalidPolicy");
			policy.setPolicyData(INVALID_VALID_POLICY.getBytes());
			
			proxy.addPolicy(policy);
			
			final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
			
			assertNull(store.polExpression);
			assertNull(store.polFilter);
		}
		finally
		{
			System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "");
		}
	}
	
	public void testConfigCertPolicy_validPolicy_assertPolicyConfiged() throws Exception
	{
		System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "ValidPolicy");
		
		try
		{
			final CertPolicy policy = new CertPolicy();
			policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
			policy.setPolicyName("ValidPolicy");
			policy.setPolicyData(VALID_POLICY.getBytes());
			
			proxy.addPolicy(policy);
			
			final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
			
			assertNotNull(store.polExpression);
			assertNotNull(store.polFilter);
		}
		finally
		{
			System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "");
		}
	}	
	
	
}
