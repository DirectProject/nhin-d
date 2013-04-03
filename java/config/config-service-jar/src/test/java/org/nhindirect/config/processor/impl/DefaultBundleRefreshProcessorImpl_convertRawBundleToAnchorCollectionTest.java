package org.nhindirect.config.processor.impl;

import static org.mockito.Mockito.mock;

import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import junit.framework.TestCase;

import org.nhindirect.config.TestUtils;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.dao.TrustBundleDao;

public class DefaultBundleRefreshProcessorImpl_convertRawBundleToAnchorCollectionTest extends TestCase
{
	public void testConvertRawBundleToAnchorCollection_getFromP7B_assertAnchors() throws Exception
	{
		final byte[] rawBundle = TestUtils.loadBundle("signedbundle.p7b");
		
		final DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		
		final TrustBundle existingBundle = new TrustBundle();
		
		final Calendar processAttempStart = Calendar.getInstance(Locale.getDefault());
		
		Collection<X509Certificate> anchors = processor.convertRawBundleToAnchorCollection(rawBundle, existingBundle, processAttempStart);
		
		assertNotNull(anchors);
		
		assertEquals(1, anchors.size());
	}	
	
	public void testConvertRawBundleToAnchorCollection_getFromSignedBundle_noVerification_assertAnchors() throws Exception
	{
		final byte[] rawBundle = TestUtils.loadBundle("signedbundle.p7m");
		
		final DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		
		final TrustBundle existingBundle = new TrustBundle();
		
		final Calendar processAttempStart = Calendar.getInstance(Locale.getDefault());
		
		Collection<X509Certificate> anchors = processor.convertRawBundleToAnchorCollection(rawBundle, existingBundle, processAttempStart);
		
		assertNotNull(anchors);
		
		assertEquals(1, anchors.size());
	}
	
	public void testConvertRawBundleToAnchorCollection_getFromSignedBundle_verifySigner_assertAnchors() throws Exception
	{
		final X509Certificate signer = TestUtils.loadSigner("bundleSigner.der");
		
		final byte[] rawBundle = TestUtils.loadBundle("signedbundle.p7m");
		
		final DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		
		final TrustBundle existingBundle = new TrustBundle();
		existingBundle.setSigningCertificateData(signer.getEncoded());
		
		final Calendar processAttempStart = Calendar.getInstance(Locale.getDefault());
		
		Collection<X509Certificate> anchors = processor.convertRawBundleToAnchorCollection(rawBundle, existingBundle, processAttempStart);
		
		assertNotNull(anchors);
		
		assertEquals(1, anchors.size());
	}	
	
	public void testConvertRawBundleToAnchorCollection_getFromSignedBundle_invalidSigner_assertNoAnchors() throws Exception
	{
		TrustBundleDao dao = mock(TrustBundleDao.class);
		
		final X509Certificate signer = TestUtils.loadSigner("sm1.direct.com Root CA.der");
		
		final byte[] rawBundle = TestUtils.loadBundle("signedbundle.p7m");
		
		final DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle existingBundle = new TrustBundle();
		existingBundle.setSigningCertificateData(signer.getEncoded());
		
		final Calendar processAttempStart = Calendar.getInstance(Locale.getDefault());
		
		Collection<X509Certificate> anchors = processor.convertRawBundleToAnchorCollection(rawBundle, existingBundle, processAttempStart);
		
		assertNull(anchors);

	}	
	
	public void testConvertRawBundleToAnchorCollection_invalidBundle_assertNoAnchors() throws Exception
	{
		TrustBundleDao dao = mock(TrustBundleDao.class);
		
		final byte[] rawBundle = TestUtils.loadBundle("invalidBundle.der");
		
		final DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle existingBundle = new TrustBundle();
		
		final Calendar processAttempStart = Calendar.getInstance(Locale.getDefault());
		
		Collection<X509Certificate> anchors = processor.convertRawBundleToAnchorCollection(rawBundle, existingBundle, processAttempStart);
		
		assertNull(anchors);

	}		
}
