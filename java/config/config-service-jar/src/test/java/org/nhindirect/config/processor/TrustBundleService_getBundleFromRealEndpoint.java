package org.nhindirect.config.processor;

import static org.mockito.Mockito.mock;

import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.dao.TrustBundleDao;

import junit.framework.TestCase;

public class TrustBundleService_getBundleFromRealEndpoint extends TestCase
{
	public void testgetBundleFromRealEndpoint() throws Exception
	{
		TrustBundleDao dao = mock(TrustBundleDao.class);
		
		BundleRefreshProcessor processor = new BundleRefreshProcessor();
		processor.setDao(dao);
		
		TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test ABBI Bundle");
		bundle.setBundleURL("https://secure.bluebuttontrust.org/p7b.ashx?id=d7a59811-ad48-e211-8bc3-78e3b5114607");
		
		processor.refreshBundle(bundle);
		
		processor.refreshBundle(bundle);
		
		bundle.setBundleURL("https://secure.bluebuttontrust.org/p7b.ashx?id=4d9daaf9-384a-e211-8bc3-78e3b5114607");
		
		processor.refreshBundle(bundle);
	}
}
