package org.nhindirect.stagent;

import java.security.Security;

import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsManagerUtils;
import org.nhindirect.stagent.options.OptionsParameter;

import junit.framework.TestCase;

public class CryptoExtensions_registerJCEProvidersTest extends TestCase
{
	@Override
	public void setUp()
	{
		OptionsManagerUtils.clearOptionsManagerInstance();
	}
	
	@Override
	public void tearDown()
	{
		OptionsManagerUtils.clearOptionsManagerOptions();
		OptionsManagerUtils.clearOptionsManagerInstance();
	}
	
	public void testRegisterJCEProviders_noOptions_providerNotAlreadyRegistered()
	{
		Security.removeProvider("BC");
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertNull(Security.getProvider("BC"));
		
		
		CryptoExtensions.registerJCEProviders();
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertNotNull(Security.getProvider("BC"));
	}
	
	public void testRegisterJCEProviders_emptyOptionValue_providerNotAlreadyRegistered()
	{
		Security.removeProvider("BC");
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER_CLASSES, ""));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNull(Security.getProvider("BC"));
		
		CryptoExtensions.registerJCEProviders();
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNotNull(Security.getProvider("BC"));
	}
	
	public void testRegisterJCEProviders_nullOptionValue_providerNotAlreadyRegistered()
	{
		Security.removeProvider("BC");
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER_CLASSES, null));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNull(Security.getProvider("BC"));
		
		CryptoExtensions.registerJCEProviders();
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNotNull(Security.getProvider("BC"));
	}
	
	public void testRegisterJCEProviders_noOptions_providerAlreadyRegistered()
	{
		if (Security.getProvider("BC") == null)
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertNotNull(Security.getProvider("BC"));
		int registeredProviderCount = Security.getProviders().length;
		
		CryptoExtensions.registerJCEProviders();
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertNotNull(Security.getProvider("BC"));
		assertEquals(registeredProviderCount, Security.getProviders().length);
	}	
	
	public void testRegisterJCEProviders_singleValidConfiguredProvider_providerNotAlreadyRegistered()
	{
		Security.removeProvider("BC");
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER_CLASSES, "org.bouncycastle.jce.provider.BouncyCastleProvider"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("org.bouncycastle.jce.provider.BouncyCastleProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNull(Security.getProvider("BC"));
		
		CryptoExtensions.registerJCEProviders();
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("org.bouncycastle.jce.provider.BouncyCastleProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNotNull(Security.getProvider("BC"));
	}	
	
	public void testRegisterJCEProviders_singleValidJVMOptionsConfiguredProvider_providerNotAlreadyRegistered()
	{
		Security.removeProvider("BC");
		
		System.clearProperty("org.nhindirect.stagent.cryptography.JCEProviderClassNames");
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderClassNames", "org.bouncycastle.jce.provider.BouncyCastleProvider");
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("org.bouncycastle.jce.provider.BouncyCastleProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNull(Security.getProvider("BC"));
		
		CryptoExtensions.registerJCEProviders();
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("org.bouncycastle.jce.provider.BouncyCastleProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNotNull(Security.getProvider("BC"));
		
		System.clearProperty("org.nhindirect.stagent.cryptography.JCEProviderClassNames");
	}	
	
	public void testRegisterJCEProviders_multipleProviders_providerNotAlreadyRegistered()
	{
		Security.removeProvider("BC");
		Security.removeProvider("JunitMockProvider");
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER_CLASSES, 
				"org.bouncycastle.jce.provider.BouncyCastleProvider,org.nhindirect.stagent.MockJCEProvider"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("org.bouncycastle.jce.provider.BouncyCastleProvider,org.nhindirect.stagent.MockJCEProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNull(Security.getProvider("BC"));
		assertNull(Security.getProvider("JunitMockProvider"));
		
		
		CryptoExtensions.registerJCEProviders();
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("org.bouncycastle.jce.provider.BouncyCastleProvider,org.nhindirect.stagent.MockJCEProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNotNull(Security.getProvider("BC"));

		assertNotNull(Security.getProvider("JunitMockProvider"));
		
		Security.removeProvider("JunitMockProvider");
	}	
	
	public void testRegisterJCEProviders_invalidProvider_assertException()
	{
		Security.removeProvider("BC");
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER_CLASSES, "bogusProvider"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("bogusProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNull(Security.getProvider("BC"));
		
		boolean exceptionOccured = false;
		try
		{
			CryptoExtensions.registerJCEProviders();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES));
		assertEquals("bogusProvider", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES).getParamValue());
		assertNull(Security.getProvider("BC"));
	}	
}
