package org.nhindirect.config;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestUtils 
{
	private static final String certsBasePath = "src/test/resources/certs/"; 
	private static final String signerBasePath = "src/test/resources/signers/"; 
	private static final String bundleBasePath = "src/test/resources/bundles/"; 
	
	public static byte[] loadBundle(String bundleFileName) throws Exception
	{
		File fl = new File(bundleBasePath + bundleFileName);
		
		return FileUtils.readFileToByteArray(fl);

	}
	
	public static X509Certificate loadCert(String certFileName) throws Exception
	{
		return fromFile(certsBasePath, certFileName);
	}	
	
	public static X509Certificate loadSigner(String authorityFileName) throws Exception
	{
		return fromFile(signerBasePath, authorityFileName);
	}		
	
	protected static final X509Certificate fromFile(String base, String file) throws Exception
	{
		File fl = new File(base + file);
		
		InputStream data = null;

		try
		{
			data = FileUtils.openInputStream(fl);
			X509Certificate retVal = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(data);
			return retVal;
		}
		finally
		{
			IOUtils.closeQuietly(data);
		}
	}
	
    public static final String uriEscape(String val) 
    {
        try 
        {
            final String escapedVal = URLEncoder.encode(val, "UTF-8");
            // Spaces are treated differently in actual URLs. There don't appear to be any other
            // differences...
            return escapedVal.replace("+", "%20");
        } 
        catch (UnsupportedEncodingException e) 
        {
            throw new RuntimeException("Failed to encode value: " + val, e);
        }
    }
    
	@Test
	public void testDummy()
	{
		
	}
}
