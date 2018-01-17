package org.nhindirect.common.rest.auth.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.NHINDPrincipal;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;
import org.nhindirect.common.rest.auth.exceptions.NoSuchUserException;

public class HashableBasicAuthValidator_authenticateRawTest 
{
	protected BasicAuthValidator buildValidator()
	{
		final BasicAuthCredential cred = new DefaultBasicAuthCredential("gm2552", "password", "admin");
		
		final List<BasicAuthCredential> credentials = Arrays.asList(cred);
		
		final BasicAuthCredentialStore store = new BootstrapBasicAuthCredentialStore(credentials);
		
		return new HashableBasicAuthValidator(store);
	}
	
	protected String buildRawCredential(String user, String password)
	{
		final String basicAuthCredFormat = user + ":" + password;
		
		return "Basic " + Base64.encodeBase64String(basicAuthCredFormat.getBytes());
	}
	
	@Test
	public void testAuthenticate_validCredentials_assertPrinciple() throws Exception
	{		
		BasicAuthValidator validator = buildValidator();
		
		final String rawAuth = buildRawCredential("gm2552", "password");
	
		NHINDPrincipal prin = validator.authenticate(rawAuth);
	
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
			final String rawAuth = buildRawCredential("test", "password");
			validator.authenticate(rawAuth);
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
			final String rawAuth = buildRawCredential("gm2552", "Password");
			validator.authenticate(rawAuth);
		}
		catch (BasicAuthException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
}
