package org.nhindirect.config.model.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.nhindirect.config.model.utils.CertUtils.CertContainer;

public class CertUtils_certAndWrappedKeyToRawByteFormatTest 
{
	
	@Test
	public void testWrapToRawBytes_assertConverted() throws Exception
	{
		final byte[] key = FileUtils.readFileToByteArray(new File("./src/test/resources/certs/gm2552Key.der"));
		final X509Certificate cert = CertUtils.toX509Certificate(FileUtils.readFileToByteArray(new File("./src/test/resources/certs/gm2552.der")));
		
		byte[] rawBytes = CertUtils.certAndWrappedKeyToRawByteFormat(key, cert);
		
		assertNotNull(rawBytes);
		
		// convert back;
		final CertContainer container = CertUtils.toCertContainer(rawBytes);
		
		assertEquals(cert, container.getCert());
		assertTrue(Arrays.equals(key, container.getWrappedKeyData()));
		assertNull(container.getKey());
	}
	
	@Test
	public void testWrapToRawBytes_signedBytesInSize_assertConverted() throws Exception
	{
		final byte[] rawP12 = FileUtils.readFileToByteArray(new File("./src/test/resources/certs/certCheckA.p12"));
		final CertUtils.CertContainer cont = CertUtils.toCertContainer(rawP12);
		
		
		byte[] rawBytes = CertUtils.certAndWrappedKeyToRawByteFormat(cont.getKey().getEncoded(), cont.getCert());
		
		assertNotNull(rawBytes);
		
		// convert back;
		final CertContainer container = CertUtils.toCertContainer(rawBytes);
		
		assertEquals(cont.getCert(), container.getCert());
		assertTrue(Arrays.equals(cont.getKey().getEncoded(), container.getWrappedKeyData()));
		assertNull(container.getKey());
	}	
}
