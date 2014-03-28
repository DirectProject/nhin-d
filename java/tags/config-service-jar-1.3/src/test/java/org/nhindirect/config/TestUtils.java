package org.nhindirect.config;

import java.io.File;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.io.FileUtils;

public class TestUtils 
{
	private static final String signerBasePath = "src/test/resources/signers/"; 
	private static final String bundleBasePath = "src/test/resources/bundles/"; 
	
	public static byte[] loadBundle(String bundleFileName) throws Exception
	{
		File fl = new File(bundleBasePath + bundleFileName);
		
		return FileUtils.readFileToByteArray(fl);

	}
	
	public static X509Certificate loadSigner(String authorityFileName) throws Exception
	{
		File fl = new File(signerBasePath + authorityFileName);
		
		InputStream data =  FileUtils.openInputStream(fl);
		
		X509Certificate retVal = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(data);
		
		data.close();
		
		return retVal;
	}		
}
