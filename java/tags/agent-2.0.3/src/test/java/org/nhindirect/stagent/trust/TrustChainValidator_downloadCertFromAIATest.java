package org.nhindirect.stagent.trust;

import java.io.File;
import java.security.cert.X509Certificate;

import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class TrustChainValidator_downloadCertFromAIATest extends TestCase
{
	protected String filePrefix;
	
	@Override
	public void setUp()
	{	
		// check for Windows... it doens't like file://<drive>... turns it into FTP
		File file = new File("./src/test/resources/certs/bob.der");
		if (file.getAbsolutePath().contains(":/"))
			filePrefix = "file:///";
		else
			filePrefix = "file:///";
	}
	
	public void testDownloadCertFromAIA_validURL_assertDownloaded() throws Exception
	{
		final TrustChainValidator validator = new TrustChainValidator();
		
		final File fl = new File("src/test/resources/certs/bob.der");
		
		final X509Certificate downloadedCert = validator.downloadCertFromAIA(filePrefix + fl.getAbsolutePath());
		
		assertNotNull(downloadedCert);
		
		assertEquals(TestUtils.loadCertificate("bob.der"), downloadedCert);
	}
	
	public void testDownloadCertFromAIA_certNotAtURL_assertException() throws Exception
	{
		final TrustChainValidator validator = new TrustChainValidator();
		
		final File fl = new File("src/test/resources/certs/bob.derdd");
		
		boolean exceptionOccurred = false;
		
		try
		{
			validator.downloadCertFromAIA(filePrefix + fl.getAbsolutePath());
		}
		catch (NHINDException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);
	}
}
