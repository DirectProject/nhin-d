package org.nhindirect.policy.impl;

import java.security.cert.X509Certificate;
import java.util.Vector;

import org.nhindirect.policy.ExecutionEngine;
import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.impl.machine.StackMachine;
import org.nhindirect.policy.impl.machine.StackMachineCompiler;

public class DefaultPolicyFilter implements PolicyFilter
{
	protected org.nhindirect.policy.Compiler compiler;
	protected ExecutionEngine executionEngine;
	
	public DefaultPolicyFilter()
	{
		this.compiler = new StackMachineCompiler();
		this.executionEngine = new StackMachine();
	}

	
	public void setCompiler(org.nhindirect.policy.Compiler compiler)
	{
		this.compiler = compiler;
	}

	public void setExecutionEngine(ExecutionEngine executionEngine)
	{
		this.executionEngine = executionEngine;
	}
	
	@Override
	public boolean isCompliant(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException
	{
		if (compiler == null)
			throw new IllegalStateException("Compiler cannot be null");
		
		if (executionEngine == null)
			throw new IllegalStateException("Execution engine cannot be null");
		
		final Vector<Opcode> opcodes = compiler.compile(cert, expression);
		
		return executionEngine.evaluate(opcodes);
	}

}
