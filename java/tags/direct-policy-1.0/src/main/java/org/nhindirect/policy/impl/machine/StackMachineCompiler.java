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

package org.nhindirect.policy.impl.machine;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import org.nhindirect.policy.LiteralPolicyExpression;
import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.OperationPolicyExpression;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.ReferencePolicyExpression;
import org.nhindirect.policy.x509.X509Field;

/**
 * Implementation of the {@link org.nhindirect.policy.Compiler} interface that generates opcodes used by the 
 * {@link StackMachine} execution engine.
 * <p>
 * This implementation traverses that expression tree and generates a vector of opcodes using a reverse policy notation strategy .
 * @author Greg Meyer
 * @since 1.0
 */
public class StackMachineCompiler implements org.nhindirect.policy.Compiler
{
	protected boolean reportModeEnabled;
	
	protected ThreadLocal<Collection<String>> compilerReport;
	
	/**
	 * Default constructor
	 */
	public StackMachineCompiler()
	{
		reportModeEnabled = false;
		compilerReport = new ThreadLocal<Collection<String>>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vector<Opcode> compile(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException
	{	
		final Collection<String> report = compilerReport.get();
		if (report != null)
			report.clear();
		
		final Vector<Opcode> entries = new Vector<Opcode>();
		
		entries.add(compile(entries, cert, expression));
		
		return entries;
	}
	
	/**
	 * Creates a {@link StackMachineEntry} opcode for the given expression.  The entry is added to the vector of machine entries.
	 * @param entries The current list of opcode entries.  The generated entry is appended to this list of entries.
	 * @param cert The X509 certificate that is being evaluated.  This is used for value substitution in 
	 * {@link org.nhindirect.policy.PolicyExpressionReferenceType#CERTIFICATE} reference types.
	 * @param expression The expression to be compiled into an opcode.
	 * @return Returns a compiled machine entry that will be added to the vector of entries.
	 * @throws PolicyProcessException
	 */
	protected StackMachineEntry compile(Vector<Opcode> entries, X509Certificate cert, PolicyExpression expression) throws PolicyProcessException
	{
		switch(expression.getExpressionType())
		{
		    case LITERAL:
		    	return new StackMachineEntry(((LiteralPolicyExpression<?>)expression).getPolicyValue());
		    	
		    case REFERENCE:
		    {
		    	final ReferencePolicyExpression<?,?> refExpression = (ReferencePolicyExpression<?,?>)expression;
		    	
		    	evaluateReferenceExpression(cert, refExpression);
		    	
		    	return new StackMachineEntry(refExpression.getPolicyValue());
		    }
		    	
		    case OPERATION:
		    {
		    	final OperationPolicyExpression opExpression = (OperationPolicyExpression)expression;
		    	for (PolicyExpression polExpression : opExpression.getOperands())
		    		entries.add(compile(entries, cert, polExpression));
		    		
		    	return new StackMachineEntry(opExpression.getPolicyOperator());
		    }
			///CLOVER:OFF
		    default:
		    	return null;
			///CLOVER:ON	
		}
		
	}
	
	/**
	 * Makes appropriate substitutions is reference expressions.
	 * @param cert The X509 certificate that is being evaluated.  This is used for value substitution in 
	 * {@link org.nhindirect.policy.PolicyExpressionReferenceType#CERTIFICATE} reference types.
	 * @param expression The reference expression utilized for value substitution.
	 * @return A policy value returned by the reference expression.
	 * @throws PolicyProcessException
	 */
	protected PolicyValue<?> evaluateReferenceExpression(X509Certificate cert, ReferencePolicyExpression<?,?> expression) throws PolicyProcessException
	{
		switch(expression.getPolicyExpressionReferenceType())
		{
			case STRUCT:
			case CERTIFICATE:
			{
				return evaluateX509Field(cert, (X509Field<?>)expression);
			}
			///CLOVER:OFF
			default:
				return null;
			///CLOVER:ON
		}
	}
	
    /**
     * Performs value substitution for X509 certificate reference expressions.
     * @param cert The certificate used for value substitution
     * @param expression The reference expression utilized for value substitution.
     * @return The value of the X509 attribute reference by the expression.
     * @throws PolicyProcessException
     */
	protected PolicyValue<?> evaluateX509Field(X509Certificate cert, X509Field<?> expression) throws PolicyProcessException
	{
		try
		{
			expression.injectReferenceValue(cert);
		
			return expression.getPolicyValue();
		}
		catch (PolicyRequiredException e) 
		{
			// add this to the report and re-evaluate without the required flag
			if (this.reportModeEnabled)
			{
				addErrorToReport(e);
				expression.setRequired(false);
				expression.injectReferenceValue(cert);
				return expression.getPolicyValue();
			}
			// re-throw
			else throw e;
		}
	}

	/**
	 * Adds a compiler error to the compiler report.
	 * @param e
	 */
	protected void addErrorToReport(PolicyProcessException e)
	{
		Collection<String> report = compilerReport.get();
		if (report == null)
		{
			report = new ArrayList<String>();
			compilerReport.set(report);
		}
		
		report.add(e.getMessage());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReportModeEnabled(boolean reportMode) 
	{
		this.reportModeEnabled = reportMode;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReportModeEnabled() 
	{
		return this.reportModeEnabled;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCompilationReport()
	{
		final Collection<String> report = compilerReport.get();
		if (report != null)
			return Collections.unmodifiableCollection(report);
		else
			return Collections.emptyList();
	}
}
