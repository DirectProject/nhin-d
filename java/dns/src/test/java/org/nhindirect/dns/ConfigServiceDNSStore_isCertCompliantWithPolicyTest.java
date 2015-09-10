package org.nhindirect.dns;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.any;

import java.net.URL;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.nhind.config.CertPolicy;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.PolicyLexicon;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.nhindirect.dns.util.DNSRecordUtil;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;

public class ConfigServiceDNSStore_isCertCompliantWithPolicyTest extends TestCase
{
	static final String KEY_ENC_POLICY = "(X509.TBS.EXTENSION.KeyUsage & 32) > 0";
	
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
	
	public void testisCertCompliantWithPolicy_noPolicyConfigured_assertCompliant() throws Exception
	{
		final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
		
		assertNull(store.polExpression);
		assertNull(store.polFilter);
		
		X509Certificate cert = DNSRecordUtil.loadCertificate("bob.der");
		
		assertTrue(store.isCertCompliantWithPolicy(cert));
	}
	
	public void testisCertCompliantWithPolicy_policyConfigured_compliantCert_assertCompliant() throws Exception
	{
		System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "ValidPolicy");
		
		try
		{
			final CertPolicy policy = new CertPolicy();
			policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
			policy.setPolicyName("ValidPolicy");
			policy.setPolicyData(KEY_ENC_POLICY.getBytes());
			
			proxy.addPolicy(policy);
			
			final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
			
			assertNotNull(store.polExpression);
			assertNotNull(store.polFilter);
			
			X509Certificate cert = DNSRecordUtil.loadCertificate("bob.der");
			
			assertTrue(store.isCertCompliantWithPolicy(cert));
		}
		finally
		{
			System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "");
		}
	}
	
	public void testisCertCompliantWithPolicy_policyConfigured_nonCompliantCert_assertNonCompliant() throws Exception
	{
		System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "ValidPolicy");
		
		try
		{
			final CertPolicy policy = new CertPolicy();
			policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
			policy.setPolicyName("ValidPolicy");
			policy.setPolicyData(KEY_ENC_POLICY.getBytes());
			
			proxy.addPolicy(policy);
			
			final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
			
			assertNotNull(store.polExpression);
			assertNotNull(store.polFilter);
			
			X509Certificate cert = DNSRecordUtil.loadCertificate("umesh.der");
			
			assertFalse(store.isCertCompliantWithPolicy(cert));
		}
		finally
		{
			System.setProperty(ConfigServiceDNSStore.DNS_CERT_POLICY_NAME_VAR, "");
		}
	}
	
	public void testisCertCompliantWithPolicy_exceptionInFilter_assertCompliant() throws Exception
	{
		final ConfigServiceDNSStore store = new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL()));
		final PolicyFilter filt = mock(PolicyFilter.class);
		
		doThrow(new RuntimeException("Just Passing Through")).when(filt).isCompliant((X509Certificate)any(), (PolicyExpression)any());
		
		store.polFilter = filt;
		
		X509Certificate cert = DNSRecordUtil.loadCertificate("umesh.der");
		
		assertTrue(store.isCertCompliantWithPolicy(cert));

	}
}
