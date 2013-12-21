package org.nhindirect.stagent.policy.impl;

import static org.mockito.Mockito.mock;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.policy.PolicyExpression;

import junit.framework.TestCase;

public class UniversalPolicyResolver_getPolicyTest extends TestCase
{
	public void testGetOutgoingPolicy_assertPolicyRetrieved() throws Exception
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		
		final UniversalPolicyResolver resolver = new UniversalPolicyResolver(expression);
		
		final Collection<PolicyExpression> policies = resolver.getOutgoingPolicy(new InternetAddress("me@you.com"));
		
		assertEquals(1, policies.size());
		assertEquals(expression, policies.iterator().next());
	}
	
	public void testGetOutgoingPolicy_nullAddress_assertException() throws Exception
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		
		final UniversalPolicyResolver resolver = new UniversalPolicyResolver(expression);
		
		boolean execptionOccured = false;
		
		try
		{
			resolver.getOutgoingPolicy(null);
		}
		catch (IllegalArgumentException e)
		{
			execptionOccured = true;
		}

		assertTrue(execptionOccured);
	}	
	
	public void testGetIncomingPolicy_assertPolicyRetrieved() throws Exception
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		
		final UniversalPolicyResolver resolver = new UniversalPolicyResolver(expression);
		
		final Collection<PolicyExpression> policies = resolver.getIncomingPolicy(new InternetAddress("me@you.com"));
		
		assertEquals(1, policies.size());
		assertEquals(expression, policies.iterator().next());
	}
	
	public void testGetIncomingPolicy_nullAddress_assertException() throws Exception
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		
		final UniversalPolicyResolver resolver = new UniversalPolicyResolver(expression);
		
		boolean execptionOccured = false;
		
		try
		{
			resolver.getIncomingPolicy(null);
		}
		catch (IllegalArgumentException e)
		{
			execptionOccured = true;
		}

		assertTrue(execptionOccured);
	}
}
