package org.nhindirect.config;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.TrustBundleDao;
import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

public abstract class SpringBaseTest extends TestCase
{
	protected String filePrefix;
	
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
		
		// check for Windows... it doens't like file://<drive>... turns it into FTP
		File file = new File("./src/test/resources/bundles/signedbundle.p7b");
		if (file.getAbsolutePath().contains(":/"))
			filePrefix = "file:///";
		else
			filePrefix = "file:///";
		
		try
		{
			cleanDataStore();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		// clean up the file system
		File dir = new File("./target/tempFiles");
		if (dir.exists())
		try
		{
			FileUtils.cleanDirectory(dir);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected void cleanDataStore() throws Exception
	{
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		final TrustBundleDao trustDao = (TrustBundleDao)ctx.getBean("trustBundleDao");
		final DomainDao domainDao = (DomainDao)ctx.getBean("domainDao");
		
		// clean domains and the trust bundle domain relationships
		final List<Domain> domains = domainDao.listDomains(null, domainDao.count());
		if (domains != null)
		{
			for (Domain domain : domains)
			{
				trustDao.disassociateTrustBundlesFromDomain(domain.getId());
				domainDao.delete(domain.getId());
			}
		}
		assertEquals(0, domainDao.count());
		
		//clean trust bundles
		Collection<TrustBundle> bundles = trustDao.getTrustBundles();
		for (TrustBundle bundle : bundles)
			trustDao.deleteTrustBundles(new long[] {bundle.getId()});
		
		bundles = trustDao.getTrustBundles();
		assertEquals(0, bundles.size());
	}
}
