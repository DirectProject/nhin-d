package org.nhindirect.policy.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.nhindirect.policy.Compiler;
import org.nhindirect.policy.ExecutionEngine;
import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyLexicon;

import junit.framework.TestCase;

public class DefaultPolicyFilter_isCompliantTest extends TestCase
{
	
	@SuppressWarnings("unchecked")
	public void testIsCompliant_parse_engineReturnsCompliant_assertTrue() throws Exception
	{
		final InputStream inStream = FileUtils.openInputStream(new File("./src/test/resources/policies/dataEnciphermentOnlyRequired.xml"));
		
		final Compiler compiler = mock(Compiler.class);
		final ExecutionEngine engine = mock(ExecutionEngine.class);
		final X509Certificate cert = mock(X509Certificate.class);
		
		when(engine.evaluate((Vector<Opcode>)any())).thenReturn(true);
		
		final DefaultPolicyFilter filter = new DefaultPolicyFilter();
		filter.setCompiler(compiler);
		filter.setExecutionEngine(engine);
		
		assertTrue(filter.isCompliant(cert, inStream, PolicyLexicon.XML));
	}
	
	@SuppressWarnings("unchecked")
	public void testIsCompliant_engineReturnsCompliant_assertTrue() throws Exception
	{
		final Compiler compiler = mock(Compiler.class);
		final ExecutionEngine engine = mock(ExecutionEngine.class);
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		
		when(engine.evaluate((Vector<Opcode>)any())).thenReturn(true);
		
		final DefaultPolicyFilter filter = new DefaultPolicyFilter();
		filter.setCompiler(compiler);
		filter.setExecutionEngine(engine);
		
		assertTrue(filter.isCompliant(cert, expression));
	}
	
	@SuppressWarnings("unchecked")
	public void testIsCompliant_engineReturnsNotCompliant_assertFalse() throws Exception
	{
		final Compiler compiler = mock(Compiler.class);
		final ExecutionEngine engine = mock(ExecutionEngine.class);
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		
		when(engine.evaluate((Vector<Opcode>)any())).thenReturn(false);
		
		final DefaultPolicyFilter filter = new DefaultPolicyFilter();
		filter.setCompiler(compiler);
		filter.setExecutionEngine(engine);
		
		assertFalse(filter.isCompliant(cert, expression));
	}	
	
	public void testIsCompliant_missingComplier_assertException() throws Exception
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		
		final DefaultPolicyFilter filter = new DefaultPolicyFilter();
		filter.setCompiler(null);

		boolean exceptionOccured = false;
		
		try
		{
			assertFalse(filter.isCompliant(cert, expression));
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	public void testIsCompliant_missingEngine_assertException() throws Exception
	{
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		
		final DefaultPolicyFilter filter = new DefaultPolicyFilter();
		filter.setExecutionEngine(null);

		boolean exceptionOccured = false;
		
		try
		{
			assertFalse(filter.isCompliant(cert, expression));
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
}
