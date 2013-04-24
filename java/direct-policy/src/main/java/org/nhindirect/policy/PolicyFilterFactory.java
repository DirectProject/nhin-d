package org.nhindirect.policy;

import org.nhindirect.policy.impl.DefaultPolicyFilter;

public class PolicyFilterFactory 
{
	public static PolicyFilter getInstance() throws PolicyParseException
	{
		return new DefaultPolicyFilter();
	}
	
	public static PolicyFilter getInstance(Compiler compiler) throws PolicyParseException
	{
		final DefaultPolicyFilter retVal = new DefaultPolicyFilter();
		retVal.setCompiler(compiler);
		
		return retVal;	
	}
	
	
	public static PolicyFilter getInstance(ExecutionEngine engine) throws PolicyParseException
	{
		final DefaultPolicyFilter retVal = new DefaultPolicyFilter();
		retVal.setExecutionEngine(engine);
		
		return retVal;	
	}	
	
	public static PolicyFilter getInstance(Compiler compiler, ExecutionEngine engine) throws PolicyParseException
	{
		final DefaultPolicyFilter retVal = new DefaultPolicyFilter();
		retVal.setExecutionEngine(engine);
		retVal.setCompiler(compiler);
		
		return retVal;	
	}		
}
