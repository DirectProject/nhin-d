package org.nhindirect.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.nhindirect.config.store.Address;
import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.Setting;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.dao.AddressDao;
import org.nhindirect.config.store.dao.AnchorDao;
import org.nhindirect.config.store.dao.CertificateDao;
import org.nhindirect.config.store.dao.DNSDao;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.SettingDao;
import org.nhindirect.config.store.dao.TrustBundleDao;
import org.springframework.context.ApplicationContext;
import org.xbill.DNS.Type;

import junit.framework.TestCase;

public abstract class SpringBaseTest extends TestCase
{
	protected String filePrefix;
	
	@Before
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
		
		final AddressDao addressDao = (AddressDao)ctx.getBean("addressDao");
		final TrustBundleDao trustDao = (TrustBundleDao)ctx.getBean("trustBundleDao");
		final DomainDao domainDao = (DomainDao)ctx.getBean("domainDao");
		final AnchorDao anchorDao = (AnchorDao)ctx.getBean("anchorDao");
		final CertificateDao certDao = (CertificateDao)ctx.getBean("certificateDao");
		final DNSDao dnsDao = (DNSDao)ctx.getBean("dnsDao");
		final SettingDao settingDao = (SettingDao)ctx.getBean("settingDao");
		
		// clean anchors
		final List<Anchor> anchors = anchorDao.listAll();
		
		if (!anchors.isEmpty())
		{
			final List<Long> anchorIds = new ArrayList<Long>();
			for (Anchor anchor : anchors)
				anchorIds.add(anchor.getId());
				
			anchorDao.delete(anchorIds);
		}
		// clean domains and the trust bundle domain relationships
		final List<Domain> domains = domainDao.listDomains(null, domainDao.count());
		if (domains != null)
		{
			for (Domain domain : domains)
			{
				Collection<Address> addresses = addressDao.getByDomain(domain, null);
				if (addresses != null)
				{
					for (Address address : addresses)
					{
						addressDao.delete(address.getEmailAddress());
					}
				}
				
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
		
		// clean certificates
		final List<Certificate> certs = certDao.list((String)null);
		if (!certs.isEmpty())
		{
			for (Certificate cert : certs)
			{
				certDao.delete(cert.getOwner());
			}
		}
		
		// clean DNS records
		final Collection<DNSRecord> records = dnsDao.get(Type.ANY);
		if (!records.isEmpty())
		{
			for (DNSRecord record : records)
				dnsDao.remove(record.getId());
		}
		
		// clean settings
		final Collection<Setting> settings = settingDao.getAll();
		if (!settings.isEmpty())
		{
			for (Setting setting : settings)
				settingDao.delete(Arrays.asList(setting.getName()));
		}
	}
}
