package org.nhindirect.stagent;


import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsManagerUtils;
import org.nhindirect.stagent.options.OptionsParameter;


import junit.framework.TestCase;

public class CryptoExtensions_getJCEProviderNameForTypeAndAlgorithmTest extends TestCase
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
	
	public void testGetJCEProviderNameForTypeAndAlgorithm_noConfiguredJCENames_assertEmptyBCProvider()
	{
		CryptoExtensions.registerJCEProviders();
		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}
	
	public void testGetJCEProviderNameForTypeAndAlgorithm_nullConfiguredJCENames_assertEmptyBCProvider()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, null));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}
	
	public void testGetJCEProviderNameForTypeAndAlgorithm_emptyConfiguredJCENames_assertEmptyBCProvider()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, ""));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}	
	
	public void testGetJCEProviderNameForTypeAndAlgorithm_configuredJCENames_algAndTypeNotFound_assertEmptyProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "BC"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("BC", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}
	
	public void testGetJCEProviderNameForTypeAndAlgorithm_configuredJCENames_unknownProvider_assertEmptyProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "dummy"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("dummy", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}	
	
	public void testGetJCEProviderNameForTypeAndAlgorithm_configuredJCENames_foundProvider_assertProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "BC"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("BC", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("BC", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("CertPathValidator", "PKIX"));
	}	
	
	public void testGetJCEProviderNameForTypeAndAlgorithm_multipConfiguredJCENames_foundProvider_assertProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "MOCK,BC"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("MOCK,BC", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("BC", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("CertPathValidator", "PKIX"));
	}			
}
