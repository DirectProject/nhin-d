package org.nhindirect.stagent.cert.impl.provider;

import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;

import junit.framework.TestCase;

public class PublicLdapCertifictaeStoreProvider_getResolver_Test extends TestCase {

	public void testGetResolverFromProvider() throws Exception
	{
		PublicLdapCertificateStoreProvider provider = new PublicLdapCertificateStoreProvider(null, null);
		CertificateResolver resolver = provider.get();
		assertNotNull(resolver);
		assertTrue(resolver instanceof LDAPCertificateStore);		
	}
}
