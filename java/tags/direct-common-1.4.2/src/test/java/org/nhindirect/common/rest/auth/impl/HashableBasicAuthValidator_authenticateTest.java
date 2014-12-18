package org.nhindirect.common.rest.auth.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.NHINDPrincipal;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;
import org.nhindirect.common.rest.auth.exceptions.NoSuchUserException;

public class HashableBasicAuthValidator_authenticateTest 
{
	protected BasicAuthValidator buildValidator()
	{
		final BasicAuthCredential cred = new DefaultBasicAuthCredential("gm2552", "password", "admin");
		
		final List<BasicAuthCredential> credentials = Arrays.asList(cred);
		
		final BasicAuthCredentialStore store = new BootstrapBasicAuthCredentialStore(credentials);
		
		return new HashableBasicAuthValidator(store);
	}
	
	@Test
	public void testAuthenticate_validCredentials_assertPrinciple() throws Exception
	{		
		BasicAuthValidator validator = buildValidator();
	
		NHINDPrincipal prin = validator.authenticate("gm2552", "password");
	
		assertEquals("gm2552", prin.getName());
		assertEquals("admin", prin.getRole());
	}
	
	@Test
	public void testAuthenticate_unknownUser_assertAssertException() throws Exception
	{		
		BasicAuthValidator validator = buildValidator();

		boolean exceptionOccured = false;
		try
		{
			validator.authenticate("test", "password");
		}
		catch (NoSuchUserException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testAuthenticate_invalidCreds_assertAssertException() throws Exception
	{		
		BasicAuthValidator validator = buildValidator();

		boolean exceptionOccured = false;
		try
		{
			validator.authenticate("gm2552", "Password");
		}
		catch (BasicAuthException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testAuthenticate_invalidAlg_assertAssertException() throws Exception
	{		
		BasicAuthValidator validator = buildValidator();

		HashableBasicAuthValidator.DIGEST_TYPE_MAP.put("Bogus", "Bogus");
		((HashableBasicAuthValidator)validator).hashType = "Bogus";
		
		boolean exceptionOccured = false;
		try
		{
			validator.authenticate("gm2552", "Password");
		}
		catch (BasicAuthException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		HashableBasicAuthValidator.DIGEST_TYPE_MAP.remove("Bogus");
	}		
}
