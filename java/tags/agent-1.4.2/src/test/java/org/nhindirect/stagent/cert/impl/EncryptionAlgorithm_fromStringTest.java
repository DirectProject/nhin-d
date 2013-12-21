package org.nhindirect.stagent.cert.impl;

import junit.framework.TestCase;

import org.nhindirect.stagent.cryptography.EncryptionAlgorithm;

public class EncryptionAlgorithm_fromStringTest extends TestCase
{
	public void testFromString_RSA_3DES()
	{
		EncryptionAlgorithm alg = EncryptionAlgorithm.fromString("RSA_3DES", EncryptionAlgorithm.AES256);
		assertEquals(EncryptionAlgorithm.RSA_3DES, alg);
	}
	
	public void testFromString_AES128()
	{
		EncryptionAlgorithm alg = EncryptionAlgorithm.fromString("AES128", EncryptionAlgorithm.AES256);
		assertEquals(EncryptionAlgorithm.AES128, alg);
	}
	
	public void testFromString_AES192()
	{
		EncryptionAlgorithm alg = EncryptionAlgorithm.fromString("AES192", EncryptionAlgorithm.AES128);
		assertEquals(EncryptionAlgorithm.AES192, alg);
	}
	
	public void testFromString_AES256()
	{
		EncryptionAlgorithm alg = EncryptionAlgorithm.fromString("AES256", EncryptionAlgorithm.AES128);
		assertEquals(EncryptionAlgorithm.AES256, alg);
	}
	
	public void testFromString_nullName_assertDefault()
	{
		EncryptionAlgorithm alg = EncryptionAlgorithm.fromString(null, EncryptionAlgorithm.AES192);
		assertEquals(EncryptionAlgorithm.AES192, alg);
	}
	
	public void testFromString_emptyName_assertDefault()
	{
		EncryptionAlgorithm alg = EncryptionAlgorithm.fromString("", EncryptionAlgorithm.AES192);
		assertEquals(EncryptionAlgorithm.AES192, alg);
	}
	
	public void testFromString_unknownName_assertDefault()
	{
		EncryptionAlgorithm alg = EncryptionAlgorithm.fromString("asdfwqerasd", EncryptionAlgorithm.AES192);
		assertEquals(EncryptionAlgorithm.AES192, alg);
	}
}
