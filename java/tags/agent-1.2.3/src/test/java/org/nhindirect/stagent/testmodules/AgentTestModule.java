package org.nhindirect.stagent.testmodules;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.stagent.annotation.AgentDomains;
import org.nhindirect.stagent.utils.TestUtils;

import com.google.inject.AbstractModule;

public class AgentTestModule extends AbstractModule 
{
	private final Collection<String> domains;
	private final CertResolverTestModule certModule;
	private final TrustAnchorResolverTestModule trustAnchorModule;
	
	public AgentTestModule(Collection<String> domains, Collection<X509Certificate> anchors, 
			String keyStoreFileName, String keyStorePassword, String  keyStorePrivPassword)
	{
		this.domains = domains;
		this.certModule = new CertResolverTestModule(keyStoreFileName, keyStorePassword, keyStorePrivPassword);
		this.trustAnchorModule = new TrustAnchorResolverTestModule(anchors);				
	}
	
	protected void configure()
	{
		this.install(certModule);
		this.install(trustAnchorModule);
		this.bind(TestUtils.collectionOf(String.class)).annotatedWith(AgentDomains.class).toInstance(domains);
	}
}
