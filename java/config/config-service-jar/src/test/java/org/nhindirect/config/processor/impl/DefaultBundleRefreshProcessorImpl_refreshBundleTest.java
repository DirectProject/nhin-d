package org.nhindirect.config.processor.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.nhindirect.config.store.BundleThumbprint;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.config.store.dao.TrustBundleDao;

import junit.framework.TestCase;

public class DefaultBundleRefreshProcessorImpl_refreshBundleTest extends TestCase
{
	protected TrustBundleDao dao;
	protected String filePrefix;
	
	@Override
	public void setUp()
	{
		dao = mock(TrustBundleDao.class);
		
		// check for Windows... it doens't like file://<drive>... turns it into FTP
		File file = new File("./src/test/resources/bundles/signedbundle.p7b");
		if (file.getAbsolutePath().contains(":/"))
			filePrefix = "file:///";
		else
			filePrefix = "file:///";
	}
	
	@SuppressWarnings("unchecked")
	public void testRefreshBundle_validBundle_noCheckSum_needsRefreshed_assertUpdateCalled() throws Exception
	{
		DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Junit Bundle");
		File fl = new File("src/test/resources/bundles/signedbundle.p7b");
		bundle.setBundleURL(filePrefix + fl.getAbsolutePath());
	
		processor.refreshBundle(bundle);
	
		verify(dao, times(1)).updateTrustBundleAnchors(eq(bundle.getId()), (Calendar)any(), (Collection<TrustBundleAnchor>)any(), (String)any());
	}	
	
	@SuppressWarnings("unchecked")
	public void testRefreshBundle_validBundle_unmatchedChecksum_needsRefreshed_assertUpdateCalled() throws Exception
	{
		DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Junit Bundle");
		File fl = new File("src/test/resources/bundles/signedbundle.p7b");
		bundle.setBundleURL(filePrefix + fl.getAbsolutePath());
		bundle.setCheckSum("12345");
	
		processor.refreshBundle(bundle);
	
		verify(dao, times(1)).updateTrustBundleAnchors(eq(bundle.getId()), (Calendar)any(), (Collection<TrustBundleAnchor>)any(), (String)any());
	}		
	
	
	@SuppressWarnings("unchecked")
	public void testRefreshBundle_checkSumsMatch_assertUpdateNotCalled() throws Exception
	{
		DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle bundle = new TrustBundle();
		
		File fl = new File("src/test/resources/bundles/signedbundle.p7b");
		
		byte[] rawBundleByte = FileUtils.readFileToByteArray(fl);
		
		bundle.setBundleName("Junit Bundle");
		bundle.setBundleURL(filePrefix + fl.getAbsolutePath());
		bundle.setCheckSum(BundleThumbprint.toThumbprint(rawBundleByte).toString());
		
		processor.refreshBundle(bundle);
	
		verify(dao, times(0)).updateTrustBundleAnchors(eq(bundle.getId()), (Calendar)any(), (Collection<TrustBundleAnchor>)any(), (String)any());
	}	
	
	@SuppressWarnings("unchecked")
	public void testRefreshBundle_bundleNotFound_assertUpdateNotCalled() throws Exception
	{
		DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Junit Bundle");
		File fl = new File("src/test/resources/bundles/signedbundle.p7b2122");
		bundle.setBundleURL(filePrefix + fl.getAbsolutePath());
	
		processor.refreshBundle(bundle);
	
		verify(dao, times(0)).updateTrustBundleAnchors(eq(bundle.getId()), (Calendar)any(), (Collection<TrustBundleAnchor>)any(), (String)any());
	}		
	
	@SuppressWarnings("unchecked")
	public void testRefreshBundle_invalidBundle_assertUpdateNotCalled() throws Exception
	{
		DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Junit Bundle");
		File fl = new File("src/test/resources/bundles/invalidBundle.der");
		bundle.setBundleURL(filePrefix + fl.getAbsolutePath());
	
		processor.refreshBundle(bundle);
	
		verify(dao, times(0)).updateTrustBundleAnchors(eq(bundle.getId()), (Calendar)any(), (Collection<TrustBundleAnchor>)any(), (String)any());
	}	
	
	@SuppressWarnings("unchecked")
	public void testRefreshBundle_errorOnUpdate() throws Exception
	{
		
		DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Junit Bundle");
		File fl = new File("src/test/resources/bundles/signedbundle.p7b");
		bundle.setBundleURL(filePrefix + fl.getAbsolutePath());
	
		doThrow(new ConfigurationStoreException("Just Passing Through")).when(dao).updateTrustBundleAnchors(eq(bundle.getId()), 
				(Calendar)any(), (Collection<TrustBundleAnchor>)any(), (String)any());
		
		
		processor.refreshBundle(bundle);
	
		verify(dao, times(1)).updateTrustBundleAnchors(eq(bundle.getId()), (Calendar)any(), (Collection<TrustBundleAnchor>)any(), (String)any());
	}
	
	/*
	public void testGetBundleFromRealEndpoint() throws Exception
	{
		TrustBundleDao dao = mock(TrustBundleDao.class);
		
		DefaultBundleRefreshProcessorImpl processor = new DefaultBundleRefreshProcessorImpl();
		processor.setDao(dao);
		
		TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test ABBI Bundle");
		bundle.setBundleURL("https://secure.bluebuttontrust.org/p7b.ashx?id=d7a59811-ad48-e211-8bc3-78e3b5114607");
		
		processor.refreshBundle(bundle);
		
		processor.refreshBundle(bundle);
		
		bundle.setBundleURL("https://secure.bluebuttontrust.org/p7b.ashx?id=4d9daaf9-384a-e211-8bc3-78e3b5114607");
		
		processor.refreshBundle(bundle);
	}
	*/
}
