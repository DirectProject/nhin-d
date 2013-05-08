package org.nhindirect.stagent.cert.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.CRL;
import java.security.cert.X509CRL;
import java.util.Calendar;
import java.util.UUID;

import javax.security.auth.x500.X500Principal;


import org.apache.commons.io.FileUtils;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class CRLRevocationManager_getCrlFromUriTest extends TestCase
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
	
	public void testGetCrlFromUri_emptyURI_assertNull()
	{
		CRL crl = CRLRevocationManager.getInstance().getCrlFromUri("");
		assertNull(crl);
	}
	
	public void testGetCrlFromUri_nullURI_assertNull()
	{
		CRL crl = CRLRevocationManager.getInstance().getCrlFromUri(null);
		assertNull(crl);
	}
	
	public void testGetCrlFromUri_existsInCache_assertCRLFound()
	{
		String uri = "http://localhost:8080/master.crl";
		Calendar nextUpdateDate = Calendar.getInstance();
		nextUpdateDate.set(Calendar.YEAR, nextUpdateDate.get(Calendar.YEAR) + 10);
		
		X509CRL crl = mock(X509CRL.class);
		when(crl.getNextUpdate()).thenReturn(nextUpdateDate.getTime());
		
		CRLRevocationManager.cache.put(uri, new SoftReference<X509CRL>(crl));
		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNotNull(retCrl);
		assertEquals(crl, retCrl);
		
	}
	
	public void testGetCrlFromUri_existsInCache_crlExpire_assertCRLNotFound()
	{
		String uri = "http://localhost:8080/master.crl";
		Calendar nextUpdateDate = Calendar.getInstance();
		nextUpdateDate.set(Calendar.YEAR, nextUpdateDate.get(Calendar.YEAR) - 10);
		
		X509CRL crl = mock(X509CRL.class);
		when(crl.getNextUpdate()).thenReturn(nextUpdateDate.getTime());
		
		CRLRevocationManager.cache.put(uri, new SoftReference<X509CRL>(crl));
		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNull(retCrl);
		
		//make sure it got removed from the cache
		assertEquals(0, CRLRevocationManager.cache.size());
		
	}
	
	@SuppressWarnings("unchecked")
	public void testGetCrlFromUri_existsInCache_softRefRemoved_assertCRLNotFound()
	{
		String uri = "http://localhost:8080/master.crl";
		
		
		SoftReference<X509CRL> softRef = mock(SoftReference.class);
		when(softRef.get()).thenReturn(null);
		
		CRLRevocationManager.cache.put(uri, softRef);
		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNull(retCrl);
		
		//make sure it got removed from the cache
		assertEquals(0, CRLRevocationManager.cache.size());
		
	}
	
	public void testGetCrlFromUri_notInCache_assertCRLNotFound()
	{
		String uri = "http://localhost:8080/master.crl";

		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNull(retCrl);
		
	}

	public void testGetCrlFromUri_notInCache_noCacheFile_assertCRLNotFound()
	{
		CRLRevocationManager.initCRLCacheLocation();
		String uri = "http://localhost:8080/master.crl";

		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNull(retCrl);
		
	}
	
	public void testGetCrlFromUri_notInCache_loadFromCacheFile_assertCRLFound() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();
		String uri = "http://localhost:8080/certs.crl";

		X509CRL crl = (X509CRL)TestUtils.loadCRL("certs.crl");
		
		KeyPairGenerator     kpGen = KeyPairGenerator.getInstance("RSA", "BC");
		KeyPair  pair = kpGen.generateKeyPair();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 10);
		X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
    	crlGen.setIssuerDN(new X500Principal("CN=Test CRL"));
    	crlGen.setNextUpdate(cal.getTime());
    	crlGen.setSignatureAlgorithm("SHA256withRSAEncryption");
    	crlGen.setThisUpdate(Calendar.getInstance().getTime());
		crlGen.addCRL(crl);
		crl = crlGen.generate(pair.getPrivate(), "BC");
    	
		CRLRevocationManager.INSTANCE.writeCRLCacheFile(uri, crl);
		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNotNull(retCrl);
		assertEquals(crl, retCrl);
	}
	
	public void testGetCrlFromUri_notInCache_loadFromCacheFile_expiredCRL_assertCRLFound() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();
		String uri = "http://localhost:8080/certs.crl";

		X509CRL crl = (X509CRL)TestUtils.loadCRL("certs.crl");
    	
		CRLRevocationManager.INSTANCE.writeCRLCacheFile(uri, crl);
		
		String fileName = CRLRevocationManager.getCacheFileName(uri);
		File writeFile = new File(fileName);
		assertTrue(writeFile.exists());
		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNull(retCrl);
		
		writeFile = new File(fileName);
		assertFalse(writeFile.exists());
	}
	
	public void testGetCrlFromUri_notInCache_loadFromCacheFile_corruptFile_assertCRLNotFound() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();
		String uri = "http://localhost:8080/certs.crl";

		String fileName = CRLRevocationManager.getCacheFileName(uri);
		File writeFile = new File(fileName);

		FileUtils.writeByteArrayToFile(writeFile, new byte[]{9,6,4});
		
		X509CRL retCrl = CRLRevocationManager.getInstance().getCrlFromUri(uri);
		assertNull(retCrl);

		writeFile = new File(fileName);
		assertFalse(writeFile.exists());
		
	}
	
	public void testGetCrlFromUri_fromURL_assertCRLFound() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();
		
		X509CRL crl = (X509CRL)TestUtils.loadCRL("certs.crl");
		
		KeyPairGenerator     kpGen = KeyPairGenerator.getInstance("RSA", "BC");
		KeyPair  pair = kpGen.generateKeyPair();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 10);
		X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
    	crlGen.setIssuerDN(new X500Principal("CN=Test CRL"));
    	crlGen.setNextUpdate(cal.getTime());
    	crlGen.setSignatureAlgorithm("SHA256withRSAEncryption");
    	crlGen.setThisUpdate(Calendar.getInstance().getTime());
		crlGen.addCRL(crl);
		crl = crlGen.generate(pair.getPrivate(), "BC");
    	
		String fileName = UUID.randomUUID().toString();
		final File crlFile = new File("target/" + fileName + ".crl");
		FileUtils.writeByteArrayToFile(crlFile, crl.getEncoded());
		
		CRLRevocationManager mgr = new CRLRevocationManager()
		{
            @Override
            protected String getNameString(String generalNameString) 
            {
                return "file:///" + crlFile.getAbsolutePath();
            }
		};
		
		String uri = crlFile.getAbsolutePath();
		X509CRL retCRL = mgr.getCrlFromUri("file:///" + uri);
		assertEquals(crl, retCRL);
		
		String cacheFileName = CRLRevocationManager.getCacheFileName("file:///" + uri);
		File cacheFile = new File(cacheFileName);
		assertTrue(cacheFile.exists());
	}
	
	public void testGetCrlFromUri_fromURL_corruptEncoding_assertCRLNotFound() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();
		
    	
		String fileName = UUID.randomUUID().toString();
		final File crlFile = new File("target/" + fileName + ".crl");
		FileUtils.writeByteArrayToFile(crlFile, new byte[] {93,39,0,1});
		
		CRLRevocationManager mgr = new CRLRevocationManager()
		{
            @Override
            protected String getNameString(String generalNameString) 
            {
                return "file:///" + crlFile.getAbsolutePath();
            }
		};
		
		String uri = crlFile.getAbsolutePath();
		X509CRL retCRL = mgr.getCrlFromUri("file:///" + uri);
		assertNull(retCRL);
		
		String cacheFileName = CRLRevocationManager.getCacheFileName("file:///" + uri);
		File cacheFile = new File(cacheFileName);
		assertFalse(cacheFile.exists());
	}
	
	public void testGetCrlFromUri_fromURL_uriNotAvailable_assertCRLNotFound() throws Exception
	{
		CRLRevocationManager.initCRLCacheLocation();

		X509CRL retCRL = CRLRevocationManager.getInstance().getCrlFromUri("file://target/bogusURI");
		assertNull(retCRL);

	}
}
