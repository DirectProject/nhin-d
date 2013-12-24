package org.nhindirect.stagent.policy.impl;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.nhindirect.policy.PolicyExpression;

public class DomainPolicyResolver_contstuctTest extends TestCase
{
	public void testConstruct_sameForIncomingAndOutgoing_assertAttributes()
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		final List<PolicyExpression> expressions = Arrays.asList(expression);
		
		final Map<String, Collection<PolicyExpression>> policies = new HashMap<String, Collection<PolicyExpression>>();
		policies.put("testdomain.com", expressions);
		
		final DomainPolicyResolver resolver = new DomainPolicyResolver(policies);
		
		assertNotNull(resolver);
		
		assertEquals(1, resolver.incomingPolicies.size());		
		Collection<PolicyExpression> retrievedExpressions =  resolver.incomingPolicies.get("TESTDOMAIN.COM");
		assertNotNull(retrievedExpressions);
		
		
		assertEquals(1, resolver.outgoingPolicies.size());		
		retrievedExpressions =  resolver.outgoingPolicies.get("TESTDOMAIN.COM");
		assertNotNull(retrievedExpressions);
	}
	
	public void testConstruct_differentForIncomingAndOutgoing_assertAttributes()
	{
		final PolicyExpression expression1 = mock(PolicyExpression.class);
		final List<PolicyExpression> expressions1 = Arrays.asList(expression1);
		
		final Map<String, Collection<PolicyExpression>> outgoingPolicies = new HashMap<String, Collection<PolicyExpression>>();
		outgoingPolicies.put("testdomain.com", expressions1);
		
		final PolicyExpression expression2 = mock(PolicyExpression.class);
		final PolicyExpression expression3 = mock(PolicyExpression.class);
		final List<PolicyExpression> expressions2 = Arrays.asList(expression2, expression3);
		
		final Map<String, Collection<PolicyExpression>> incomingPolicies = new HashMap<String, Collection<PolicyExpression>>();
		incomingPolicies.put("testdomain.com", expressions2);
		
		final DomainPolicyResolver resolver = new DomainPolicyResolver(incomingPolicies, outgoingPolicies);
		
		assertNotNull(resolver);
		
		assertEquals(1, resolver.outgoingPolicies.size());		
		Collection<PolicyExpression> retrievedExpressions =  resolver.outgoingPolicies.get("TESTDOMAIN.COM");
		assertEquals(1, retrievedExpressions.size());
		assertNotNull(retrievedExpressions);
		
		assertEquals(1, resolver.incomingPolicies.size());		
		retrievedExpressions =  resolver.incomingPolicies.get("TESTDOMAIN.COM");
		assertEquals(2, retrievedExpressions.size());
		assertNotNull(retrievedExpressions);
	}	
	
	public void testConstruct_nullPolicies_assertException()
	{
		// single parameter
		boolean exceptionOccured = false;
		
		try
		{
			new DomainPolicyResolver(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		// multiple parameters
		exceptionOccured = false;
		
		try
		{
			new DomainPolicyResolver(null, null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	
}
