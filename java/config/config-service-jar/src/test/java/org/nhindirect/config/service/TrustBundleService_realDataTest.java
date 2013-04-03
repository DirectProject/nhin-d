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
	
	public void testUpdateAttributes_noBundleRefresh() throws Exception
	{
		File bundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		// add a bundle
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + bundleLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		// make sure the bundle is there
		final TrustBundle addedBundle = trustService.getTrustBundleByName("Test Bundle");
		assertNotNull(addedBundle);
		
		// update the bundle
		trustService.updateTrustBundleAttributes(addedBundle.getId(), "Test Bundle 2", addedBundle.getBundleURL(), 
				null, addedBundle.getRefreshInterval());
		
		// now get the updated bundle
		final TrustBundle updatedBundle = trustService.getTrustBundleById(addedBundle.getId());
		
		assertEquals("Test Bundle 2", updatedBundle.getBundleName());
		assertEquals(addedBundle.getBundleURL(), updatedBundle.getBundleURL());
		assertEquals(addedBundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		assertEquals(addedBundle.getTrustBundleAnchors().size(), updatedBundle.getTrustBundleAnchors().size());
	}	
	
	public void testUpdateAttributes_bundleRefresh() throws Exception
	{
		final File bundleLocation = new File("./src/test/resources/bundles/signedbundle.p7b");
		
		final ApplicationContext ctx = ConfigServiceRunner.getSpringApplicationContext();
		
		// add a bundle
		TrustBundleService trustService = (TrustBundleService)ctx.getBean("trustBundleSvc");
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL(filePrefix + bundleLocation.getAbsolutePath());
		
		trustService.addTrustBundle(bundle);
		
		// make sure the bundle is there
		final TrustBundle addedBundle = trustService.getTrustBundleByName("Test Bundle");
		assertNotNull(addedBundle);
		
		// update the bundle
		final File newBundleLocation = new File("./src/test/resources/bundles/providerTestBundle.p7b");
		trustService.updateTrustBundleAttributes(addedBundle.getId(), "Test Bundle 2", filePrefix + newBundleLocation.getAbsolutePath(), 
				null, addedBundle.getRefreshInterval());
		
		// now get the updated bundle
		final TrustBundle updatedBundle = trustService.getTrustBundleById(addedBundle.getId());
		
		assertEquals("Test Bundle 2", updatedBundle.getBundleName());
		assertFalse(newBundleLocation.getAbsolutePath().equals(updatedBundle.getBundleURL()));
		assertEquals(addedBundle.getRefreshInterval(), updatedBundle.getRefreshInterval());
		assertNull(updatedBundle.getSigningCertificateData());
		
		
		assertFalse(addedBundle.getTrustBundleAnchors().size() == updatedBundle.getTrustBundleAnchors().size());
	}		
}
