package org.nhindirect.config.processor.impl;


import java.io.File;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.SpringBaseTest;
import org.nhindirect.config.service.TrustBundleService;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.springframework.context.ApplicationContext;

public class DefaultBundleCacheUpdateProcessorImpl_springInitTest extends SpringBaseTest
{
	
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
		
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + bundleLocation.getAbsolutePath());
		
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
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + bundleLocation.getAbsolutePath());
		
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
	
	public void testLoadConfigService_refreshBundle_newBundleData_assertBundleRefreshed() throws Exception
	{
		final File originalBundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		final File updatedBundleLocation = new File("./src/test/resources/bundles/providerTestBundle.p7b");
		
		final File targetTempFileLocation = new File("./target/tempFiles/bundle.p7b");
		
		// copy the original bundle to the target location
		FileUtils.copyFile(originalBundleLocation, targetTempFileLocation);
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		assertNotNull(ctx);
		
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + targetTempFileLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		final TrustBundle addedBundle = trustService.getTrustBundleByName("Test Bundle");
		assertTrue(addedBundle.getTrustBundleAnchors().size() > 0);
		
		// validate the contents of the bundle
		final TrustBundle firstBundleInsert = trustService.getTrustBundleByName("Test Bundle");
		assertEquals(1, firstBundleInsert.getTrustBundleAnchors().size());
		
		// copy in the new bundle
		FileUtils.copyFile(updatedBundleLocation, targetTempFileLocation);
		
		// now refresh
		trustService.refreshTrustBundle(addedBundle.getId());
		
		final TrustBundle refreshedBundle = trustService.getTrustBundleByName("Test Bundle");
		assertEquals(6, refreshedBundle.getTrustBundleAnchors().size());
	}
}
