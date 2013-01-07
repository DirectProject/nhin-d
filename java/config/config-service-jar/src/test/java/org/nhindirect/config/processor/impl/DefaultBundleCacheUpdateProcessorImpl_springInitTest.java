package org.nhindirect.config.processor.impl;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;

import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.service.TrustBundleService;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

public class DefaultBundleCacheUpdateProcessorImpl_springInitTest extends TestCase
{
	@Override
	public void setUp()
	{
		try
		{
			ConfigServiceRunner.startConfigService();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void cleanBundles(TrustBundleService service) throws Exception
	{
		Collection<TrustBundle> bundles = service.getTrustBundles(true);
		
		for (TrustBundle bundle : bundles)
			service.deleteTrustBundles(new long[] {bundle.getId()});
		
		bundles = service.getTrustBundles(true);
		assertEquals(0, bundles.size());
	}
	
	public void testLoadConfigService_validSpringConfig_assertComponentsLoaded() throws Exception
	{
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		assertNotNull(ctx);
		
		assertNotNull(ctx.getBean("trustBundleSvc"));
		assertNotNull(ctx.getBean("trustBundleDao"));
		assertNotNull(ctx.getBean("bundleRefresh"));	
		assertNotNull(ctx.getBean("bundleRefreshProcessor"));	
		assertNotNull(ctx.getBean("bundleCacheUpdateProcessor"));	
		assertNotNull(ctx.getBean("trustBundleDao"));	
		assertNotNull(ctx.getBean("domainDao"));			
	}
	
	
	public void testLoadConfigService_addTrustBundle_bundleAnchorsAdded() throws Exception
	{
		File bundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		assertNotNull(ctx);
		
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		cleanBundles(trustService);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("file:///" + bundleLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		final TrustBundle addedBundle = trustService.getTrustBundleByName("Test Bundle");
		assertTrue(addedBundle.getTrustBundleAnchors().size() > 0);		
		
		for (TrustBundleAnchor anchor : addedBundle.getTrustBundleAnchors())
			assertNotNull(anchor.getData());
	}
	
	public void testLoadConfigService_refreshBundle_assertBundleRefreshed() throws Exception
	{
		File bundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		assertNotNull(ctx);
		
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		cleanBundles(trustService);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("file:///" + bundleLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		final TrustBundle addedBundle = trustService.getTrustBundleByName("Test Bundle");
		assertTrue(addedBundle.getTrustBundleAnchors().size() > 0);
		final Calendar lastRefreshAttemp = addedBundle.getLastRefreshAttempt();
		final Calendar lastSuccessfulRefresh = addedBundle.getLastSuccessfulRefresh();
		
		// now refresh
		trustService.refreshTrustBundle(addedBundle.getId());
		
		final TrustBundle refreshedBundle = trustService.getTrustBundleByName("Test Bundle");
		assertEquals(lastSuccessfulRefresh.getTimeInMillis(), refreshedBundle.getLastSuccessfulRefresh().getTimeInMillis());
		assertTrue(refreshedBundle.getLastRefreshAttempt().getTimeInMillis() > lastRefreshAttemp.getTimeInMillis());
	}
	
}
