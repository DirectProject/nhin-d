package org.nhindirect.stagent.cert.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.security.cert.CRL;
import java.security.cert.X509CRL;

import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class CRLRevocationManager_writeCRLCacheFileTest extends TestCase
{
	@Override
	public void setUp()
	{
    	CryptoExtensions.registerJCEProviders();
		
		CRLRevocationManager.initCRLCacheLocation();
		CRLRevocationManager.getInstance().flush();		
		CRLRevocationManager.crlCacheLocation = null;
	}
	
	@Override
	public void tearDown()
	{
		CRLRevocationManager.getInstance().flush();
		CRLRevocationManager.initCRLCacheLocation();
	}
	
	public void testWriteCRLCacheFile_writeToFile() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();

		CRL crlToWrite = TestUtils.loadCRL("certs.crl");
		String distURI = "http://localhost:8080/config";
		
		CRLRevocationManager.getInstance().writeCRLCacheFile(distURI, (X509CRL)crlToWrite);
		
		// make sure the file exists
		File crlFile = new File(CRLRevocationManager.getCacheFileName(distURI));
		assertTrue(crlFile.exists());
	}
	
	public void testWriteCRLCacheFile_writeToFile_deleteExisting() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();

		CRL crlToWrite = TestUtils.loadCRL("certs.crl");
		String distURI = "http://localhost:8080/config";
		
		// make sure it doesn't exist
		File crlFile = new File(CRLRevocationManager.getCacheFileName(distURI));
		assertFalse(crlFile.exists());
		
		CRLRevocationManager.getInstance().writeCRLCacheFile(distURI, (X509CRL)crlToWrite);
		
		// make sure the file exists
		assertTrue(crlFile.exists());
		
		// mark the date
		long originalFileDate = crlFile.lastModified();
		
		// sleep 2000 ms to make sure we get a new date
		Thread.sleep(2000);
		// write it again
		CRLRevocationManager.getInstance().writeCRLCacheFile(distURI, (X509CRL)crlToWrite);
		
		// make sure the file exists
		crlFile = new File(CRLRevocationManager.getCacheFileName(distURI));
		assertTrue(crlFile.exists());
		
		// mark the date
		long newFileDate = crlFile.lastModified();
		
		// make sure the dates aren't the same
		assertTrue(originalFileDate != newFileDate);
		
	}
	
	public void testWriteCRLCacheFile_errorInWrite() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();

		X509CRL crlToWrite = mock(X509CRL.class);
		doThrow(new RuntimeException("Just Passing Through")).when(crlToWrite).getEncoded();
		String distURI = "http://localhost:8080/config";
		
		CRLRevocationManager.getInstance().writeCRLCacheFile(distURI, crlToWrite);
		
		// make sure the file does not exists
		File crlFile = new File(CRLRevocationManager.getCacheFileName(distURI));
		assertFalse(crlFile.exists());
	}
	
	
	public void testWriteCRLCacheFile_noCRLLocation_assertFileNotCreated() throws Exception
	{

		CRL crlToWrite = TestUtils.loadCRL("certs.crl");
		String distURI = "http://localhost:8080/config";
		
		CRLRevocationManager.getInstance().writeCRLCacheFile(distURI, (X509CRL)crlToWrite);
		
		// make sure the file does not exists
		File crlFile = new File(CRLRevocationManager.getCacheFileName(distURI));
		assertFalse(crlFile.exists());
	}
}
