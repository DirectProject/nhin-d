package org.nhindirect.config.service;

import java.io.File;
import java.util.Collection;

import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.SpringBaseTest;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleDomainReltn;
import org.springframework.context.ApplicationContext;

public class TrustBundleService_realDataTest extends SpringBaseTest
{
	public void testAssociateDomainToBundle_incomingAndOutgoingTrue_assertIncomingAndOutgoingFlags() throws Exception
	{
		File bundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		// add a bundle
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + bundleLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		// add a domain
		DomainService domainService = (DomainService)ctx.getBean("domainSvc");
		
		Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		domain.setStatus(EntityStatus.ENABLED);
		
		domainService.addDomain(domain);
		
		//associate domain to bundle
		trustService.associateTrustBundleToDomain(domain.getId(),  bundle.getId(), true, true);
		
		// assert the association
		Collection<TrustBundleDomainReltn> associatedBundel = trustService.getTrustBundlesByDomain(domain.getId(),true);
		assertEquals(1, associatedBundel.size());
		
		TrustBundleDomainReltn reltn = associatedBundel.iterator().next();
		assertTrue(reltn.isIncoming());
		assertTrue(reltn.isOutgoing());
	}
	
	public void testAssociateDomainToBundle_incomingTrueOutgoingFalse_assertIncomingAndOutgoingFlags() throws Exception
	{
		File bundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		// add a bundle
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + bundleLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		// add a domain
		DomainService domainService = (DomainService)ctx.getBean("domainSvc");
		
		Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		domain.setStatus(EntityStatus.ENABLED);
		
		domainService.addDomain(domain);
		
		//associate domain to bundle
		trustService.associateTrustBundleToDomain(domain.getId(),  bundle.getId(), true, false);
		
		// assert the association
		Collection<TrustBundleDomainReltn> associatedBundel = trustService.getTrustBundlesByDomain(domain.getId(),true);
		assertEquals(1, associatedBundel.size());
		
		TrustBundleDomainReltn reltn = associatedBundel.iterator().next();
		assertTrue(reltn.isIncoming());
		assertFalse(reltn.isOutgoing());
	}
	
	public void testAssociateDomainToBundle_incomingFalseOutgoingTrue_assertIncomingAndOutgoingFlags() throws Exception
	{
		File bundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		// add a bundle
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + bundleLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		// add a domain
		DomainService domainService = (DomainService)ctx.getBean("domainSvc");
		
		Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		domain.setStatus(EntityStatus.ENABLED);
		
		domainService.addDomain(domain);
		
		//associate domain to bundle
		trustService.associateTrustBundleToDomain(domain.getId(),  bundle.getId(), false, true);
		
		// assert the association
		Collection<TrustBundleDomainReltn> associatedBundel = trustService.getTrustBundlesByDomain(domain.getId(),true);
		assertEquals(1, associatedBundel.size());
		
		TrustBundleDomainReltn reltn = associatedBundel.iterator().next();
		assertTrue(reltn.isOutgoing());
		assertFalse(reltn.isIncoming());
	}
}
