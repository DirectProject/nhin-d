package org.nhindirect.stagent.testmodules;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.UniformCertificateStore;
import org.nhindirect.stagent.cert.impl.annotation.UniformCertStoreCerts;
import org.nhindirect.stagent.trust.annotation.IncomingTrustAnchors;
import org.nhindirect.stagent.trust.annotation.OutgoingTrustAnchors;
import org.nhindirect.stagent.utils.TestUtils;

import com.google.inject.AbstractModule;

public class TrustAnchorResolverTestModule extends AbstractModule
{
	
	final Collection<X509Certificate> anchors;
	
	public TrustAnchorResolverTestModule(Collection<X509Certificate> anchors)
	{
		this.anchors = anchors;
	}
		
	protected void configure()
	{
		this.bind(TestUtils.collectionOf(X509Certificate.class)).annotatedWith(UniformCertStoreCerts.class).toInstance(anchors);
		this.bind(CertificateResolver.class).annotatedWith(OutgoingTrustAnchors.class).to(UniformCertificateStore.class);
		this.bind(CertificateResolver.class).annotatedWith(IncomingTrustAnchors.class).to(UniformCertificateStore.class);
	}
	
}
