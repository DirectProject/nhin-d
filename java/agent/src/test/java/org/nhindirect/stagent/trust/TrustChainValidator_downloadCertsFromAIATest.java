package org.nhindirect.stagent.trust;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Collection;

import junit.framework.TestCase;

import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.utils.TestUtils;

public class TrustChainValidator_downloadCertsFromAIATest extends TestCase
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
	
	public void testDownloadCertsFromAIA_validURL_singleCert_assertDownloaded() throws Exception
	{
		final TrustChainValidator validator = new TrustChainValidator();
		
		final File fl = new File("src/test/resources/certs/bob.der");
		
		final X509Certificate downloadedCert = validator.downloadCertsFromAIA(filePrefix + fl.getAbsolutePath()).iterator().next();
		
		assertNotNull(downloadedCert);
		
		assertEquals(TestUtils.loadCertificate("bob.der"), downloadedCert);
	}
	
	public void testDownloadCertsFromAIA_validURL_collectionCert_assertDownloaded() throws Exception
	{
		final TrustChainValidator validator = new TrustChainValidator();
		
		final File fl = new File("src/test/resources/certs/cmsRandomizer.p7b");
		
		final Collection<X509Certificate> downloadedCerts = validator.downloadCertsFromAIA(filePrefix + fl.getAbsolutePath());
		
		assertNotNull(downloadedCerts);
		
		
		assertEquals(6, downloadedCerts.size());
	}
	
	public void testDownloadCertsFromAIA_certNotAtURL_assertException() throws Exception
	{
		final TrustChainValidator validator = new TrustChainValidator();
		
		final File fl = new File("src/test/resources/certs/bob.derdd");
		
		boolean exceptionOccurred = false;
		
		try
		{
			validator.downloadCertsFromAIA(filePrefix + fl.getAbsolutePath());
		}
		catch (NHINDException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);
	}
}
