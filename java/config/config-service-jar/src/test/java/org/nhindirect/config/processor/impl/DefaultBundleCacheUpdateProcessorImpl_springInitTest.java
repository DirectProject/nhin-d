package org.nhindirect.config.processor.impl;

import org.nhindirect.config.ConfigServiceRunner;
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
	
	public void testLoadConfigService_validSpringConfig_assertComponentsLoaded() throws Exception
	{
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		assertNotNull(ctx);
		
		assertNotNull(ctx.getBean("trustBundleSvc"));
		assertNotNull(ctx.getBean("trustBundleDao"));
		assertNotNull(ctx.getBean("bundleRefresh"));	
		assertNotNull(ctx.getBean("bundleRefreshProcessor"));	
		assertNotNull(ctx.getBean("bundleCacheUpdateProcessor"));	
	}
}
