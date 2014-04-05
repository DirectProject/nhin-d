package org.nhindirect.common.rest.auth.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nhindirect.common.crypto.exceptions.CryptoException;

public class HashableBasicAuthValidator_convertPassToHashTest 
{
	@Test
	public void testConvertPassToHashTest_ClearText() throws Exception
	{
		final String password = "password";
		
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		
		final String hashedPass = validator.convertPassToHash(password);
		
		assertEquals("password", hashedPass);
	}
	
	@Test
	public void testConvertPassToHashTest_MD5Hash() throws Exception
	{
		final String password = "password";
		
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		validator.setHashType(HashableBasicAuthValidator.HASH_MD5);
		
		final String hashedPass = validator.convertPassToHash(password);
		
		assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", hashedPass);
	}
	
	@Test
	public void testConvertPassToHashTest_SHA1Hash() throws Exception
	{
		final String password = "password";
		
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		validator.setHashType(HashableBasicAuthValidator.HASH_SHA1);
		
		final String hashedPass = validator.convertPassToHash(password);
		
		assertEquals("5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8", hashedPass);
	}	
	
	@Test
	public void testConvertPassToHashTest_SHA256Hash() throws Exception
	{
		final String password = "password";
		
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		validator.setHashType(HashableBasicAuthValidator.HASH_SHA256);
		
		final String hashedPass = validator.convertPassToHash(password);
		
		assertEquals("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", hashedPass);
	}	
	
	@Test
	public void testConvertPassToHashTest_SHA512Hash() throws Exception
	{
		final String password = "password";
		
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		validator.setHashType(HashableBasicAuthValidator.HASH_SHA512);
		
		final String hashedPass = validator.convertPassToHash(password);
		
		assertEquals("b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86", hashedPass);
	}
	
	@Test
	public void testConvertPassToHashTest_unknownAlg_assertException() throws Exception
	{
		final String password = "password";
		
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		HashableBasicAuthValidator.DIGEST_TYPE_MAP.put("Bogus", "Bogus");
		validator.hashType = "Bogus";

		boolean exceptionOccured = false;
		try
		{
			validator.convertPassToHash(password);
		}	
		catch (CryptoException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		HashableBasicAuthValidator.DIGEST_TYPE_MAP.remove("Bogus");
	}
}
