package org.nhindirect.policy;

import static org.mockito.Mockito.mock;

import org.nhindirect.policy.impl.DefaultPolicyFilter;

import junit.framework.TestCase;

public class PolicyFilterFactory_getInstanceTest extends TestCase
{
	public void testGetInstance_emptyContructor() throws Exception
	{
		PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertNotNull(filter);
		assertTrue(filter instanceof DefaultPolicyFilter);
	}
	
	public void testGetInstance_providedCompilerContructor() throws Exception
	{
		PolicyFilter filter = PolicyFilterFactory.getInstance(mock(Compiler.class));
		
		assertNotNull(filter);
		assertTrue(filter instanceof DefaultPolicyFilter);
	}	
	
	public void testGetInstance_providedEngintContructor() throws Exception
	{
		PolicyFilter filter = PolicyFilterFactory.getInstance(mock(ExecutionEngine.class));
		
		assertNotNull(filter);
		assertTrue(filter instanceof DefaultPolicyFilter);
	}
	
	public void testGetInstance_providedEngintAndCompilerContructor() throws Exception
	{
		PolicyFilter filter = PolicyFilterFactory.getInstance(mock(Compiler.class), mock(ExecutionEngine.class));
		
		assertNotNull(filter);
		assertTrue(filter instanceof DefaultPolicyFilter);
	}		
}
