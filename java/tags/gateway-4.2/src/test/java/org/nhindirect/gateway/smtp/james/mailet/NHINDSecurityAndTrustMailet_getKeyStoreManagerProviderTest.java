package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.mailet.MailetConfig;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.gateway.smtp.provider.StaticPKCS11TokenKeyStoreProtectionManagerProvider;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.options.OptionsManagerUtils;

import com.google.inject.Provider;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_getKeyStoreManagerProviderTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			params.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PROVIDER, getKeyStoreProvider());
			params.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PIN, getKeyStorePin());
			params.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_PROVIDER, getPKCS11Provider());
			params.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_CONFIG_FILE, getPKCS11ConfigFile());

			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected String getPKCS11Provider()
		{
			return "sun.security.pkcs11.SunPKCS11";
		}
		
		protected String getKeyStorePin()
		{
			return "1Kingpuff";
		}
		
		protected String getKeyStoreProvider()
		{
			return "org.nhindirect.gateway.smtp.provider.StaticPKCS11TokenKeyStoreProtectionManagerProvider";
		}
		
		protected String getPKCS11ConfigFile()
		{
			return "./src/test/resources/pkcs11Config/pkcs11.cfg";
		}

		
		@Override
		protected void setupMocks() 
		{
			OptionsManagerUtils.clearOptionsManagerInstance();
		}
		
		@Override
		protected void tearDownMocks()
		{
			OptionsManagerUtils.clearOptionsManagerOptions();
			OptionsManagerUtils.clearOptionsManagerInstance();
		}
		
		@Override
		protected void performInner() throws Exception
		{
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet()
			{

			};

			MailetConfig config = getMailetConfig();
			
			try
			{
				theMailet.init(config);
			}
			catch (Exception e)
			{
				/* don't care if an exception occured during init */
			}

			doAssertions(theMailet.getKeyStoreManagerProvider());

		}
		
		protected void doAssertions(Provider<KeyStoreProtectionManager> provider) throws Exception
		{
			
		}	
	}
	
	public void test_getKeyStoreManagerProvider_assertProvider() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(Provider<KeyStoreProtectionManager> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof StaticPKCS11TokenKeyStoreProtectionManagerProvider);
			}				
		}.perform();
	}	
	
	public void test_getKeyStoreManagerProvider_assertProviderAndCreateInstance() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(Provider<KeyStoreProtectionManager> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof StaticPKCS11TokenKeyStoreProtectionManagerProvider);
				
				final String tokenProvider = TestUtils.setupSafeNetToken();
				if (!StringUtils.isEmpty(tokenProvider))
				{
					final StaticPKCS11TokenKeyStoreProtectionManagerProvider prov = 
						(StaticPKCS11TokenKeyStoreProtectionManagerProvider)provider;
					
					final KeyStoreProtectionManager mgr = prov.get();
					assertNotNull(mgr);
				}
				
			}				
		}.perform();
	}
	
	public void test_getKeyStoreManagerProvider_unknownProviderClass_assertNull() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getKeyStoreProvider()
			{
				return "org.nhindirect.gateway.smtp.provider.BogusProvider";
			}
			
			@Override
			protected void doAssertions(Provider<KeyStoreProtectionManager> provider) throws Exception
			{
				assertNull(provider);
			}				
		}.perform();
	}	
	
	public void test_getKeyStoreManagerProvider_emptyProviderClass_assertNull() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getKeyStoreProvider()
			{
				return "";
			}
			
			@Override
			protected void doAssertions(Provider<KeyStoreProtectionManager> provider) throws Exception
			{
				assertNull(provider);
			}				
		}.perform();
	}	
	
	public void test_getKeyStoreManagerProvider_nullProviderClass_assertNull() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getKeyStoreProvider()
			{
				return null;
			}
			
			@Override
			protected void doAssertions(Provider<KeyStoreProtectionManager> provider) throws Exception
			{
				assertNull(provider);
			}				
		}.perform();
	}	
}
