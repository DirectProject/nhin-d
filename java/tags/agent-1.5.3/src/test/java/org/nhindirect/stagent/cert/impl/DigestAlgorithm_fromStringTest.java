package org.nhindirect.stagent.cert.impl;

import org.nhindirect.stagent.cryptography.DigestAlgorithm;

import junit.framework.TestCase;

public class DigestAlgorithm_fromStringTest extends TestCase
{
	public void testFromString_SHA1()
	{
		DigestAlgorithm alg = DigestAlgorithm.fromString("SHA1", DigestAlgorithm.SHA256);
		assertEquals(DigestAlgorithm.SHA1, alg);
	}
	
	public void testFromString_SHA256()
	{
		DigestAlgorithm alg = DigestAlgorithm.fromString("SHA256", DigestAlgorithm.SHA1);
		assertEquals(DigestAlgorithm.SHA256, alg);
	}
	
	public void testFromString_SHA384()
	{
		DigestAlgorithm alg = DigestAlgorithm.fromString("SHA384", DigestAlgorithm.SHA1);
		assertEquals(DigestAlgorithm.SHA384, alg);
	}
	
	public void testFromString_SHA512()
	{
		DigestAlgorithm alg = DigestAlgorithm.fromString("SHA512", DigestAlgorithm.SHA1);
		assertEquals(DigestAlgorithm.SHA512, alg);
	}
	
	public void testFromString_nullName_assertDefault()
	{
		DigestAlgorithm alg = DigestAlgorithm.fromString(null, DigestAlgorithm.SHA256);
		assertEquals(DigestAlgorithm.SHA256, alg);
	}
	
	public void testFromString_emptyName_assertDefault()
	{
		DigestAlgorithm alg = DigestAlgorithm.fromString("", DigestAlgorithm.SHA256);
		assertEquals(DigestAlgorithm.SHA256, alg);
	}
	
	public void testFromString_unknownName_assertDefault()
	{
		DigestAlgorithm alg = DigestAlgorithm.fromString("asdfwqerasd", DigestAlgorithm.SHA256);
		assertEquals(DigestAlgorithm.SHA256, alg);
	}
}
