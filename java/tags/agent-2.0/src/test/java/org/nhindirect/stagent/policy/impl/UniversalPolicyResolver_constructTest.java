package org.nhindirect.stagent.policy.impl;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.stagent.policy.impl.UniversalPolicyResolver;

import junit.framework.TestCase;

public class UniversalPolicyResolver_constructTest extends TestCase
{
	public void testConstruct_singleExpression_assertAttributes()
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		
		final UniversalPolicyResolver resolver = new UniversalPolicyResolver(expression);
		
		assertNotNull(resolver);
		assertEquals(1, resolver.expressions.size());
		assertEquals(expression, resolver.expressions.iterator().next());
	}
	
	public void testConstruct_multipleExpressions_assertAttributes()
	{
		final PolicyExpression expression1 = mock(PolicyExpression.class);
		final PolicyExpression expression2 = mock(PolicyExpression.class);
		
		final UniversalPolicyResolver resolver = new UniversalPolicyResolver(Arrays.asList(expression1, expression2));
		
		assertNotNull(resolver);
		assertEquals(2, resolver.expressions.size());
		
		final Iterator<PolicyExpression> iter = resolver.expressions.iterator();
		assertEquals(expression1, iter.next());
		assertEquals(expression2, iter.next());
	}	
	
	public void testConstruct_emptyExpression_assertException()
	{	
		boolean exceptionOccured = false;
		try
		{
			new UniversalPolicyResolver(new ArrayList<PolicyExpression>());

		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testConstruct_nullExpression_assertException()
	{	
		// single expression constructor
		boolean exceptionOccured = false;
		try
		{
			new UniversalPolicyResolver((PolicyExpression)null);

		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		// multiple expression constructor
		exceptionOccured = false;
		try
		{
			new UniversalPolicyResolver((Collection<PolicyExpression>)null);

		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);	
	}	
}
