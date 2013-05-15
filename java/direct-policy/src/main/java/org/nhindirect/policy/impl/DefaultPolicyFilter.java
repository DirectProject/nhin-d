/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.policy.impl;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Vector;

import org.nhindirect.policy.ExecutionEngine;
import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.policy.PolicyLexicon;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.impl.machine.StackMachine;
import org.nhindirect.policy.impl.machine.StackMachineCompiler;

/**
 * Default implementation of the {@link PolicyFilter} interface.
 * @author Greg Meyer
 * @since 1.0
 */
public class DefaultPolicyFilter implements PolicyFilter
{
	protected org.nhindirect.policy.Compiler compiler;
	protected ExecutionEngine executionEngine;
	
	/**
	 * Default constructor.  Creates default instances of the compiler and execution engine.
	 */
	public DefaultPolicyFilter()
	{
		this.compiler = new StackMachineCompiler();
		this.executionEngine = new StackMachine();
	}

	/**
	 * Sets the compiler for the filters.
	 * @param compiler The compiler for the filters.
	 */
	public void setCompiler(org.nhindirect.policy.Compiler compiler)
	{
		this.compiler = compiler;
	}

	/**
	 * Sets the execution engine for the filters.
	 * @param executionEngine The execution engine for the filters.
	 */
	public void setExecutionEngine(ExecutionEngine executionEngine)
	{
		this.executionEngine = executionEngine;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompliant(X509Certificate cert, InputStream policyStream, PolicyLexicon lexicon) throws PolicyProcessException
	{

		final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(lexicon);
		final PolicyExpression expression = parser.parse(policyStream);
	
		return isCompliant(cert, expression);

	}
	
	/**
	 * {@inheritDoc}
	 */
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
