package org.nhindirect.common.rest.auth.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HashableBasicAuthValidator_setHashTypeTest 
{
	@Test
	public void testSetHashType_validHashType() throws Exception
	{	
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		validator.setHashType(HashableBasicAuthValidator.HASH_MD5);
		
		assertEquals(HashableBasicAuthValidator.HASH_MD5, validator.hashType);
	}
	
	@Test
	public void testSetHashType_validHashType_setViaConstructor() throws Exception
	{	
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator(null, HashableBasicAuthValidator.HASH_MD5);
		
		assertEquals(HashableBasicAuthValidator.HASH_MD5, validator.hashType);
	}	
	
	@Test
	public void testSetHashType_invalidHashType_assertException() throws Exception
	{	
		HashableBasicAuthValidator validator = new HashableBasicAuthValidator();
		
		boolean exceptionOccured = false;
		
		try
		{
			validator.setHashType("Bogus");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
}
