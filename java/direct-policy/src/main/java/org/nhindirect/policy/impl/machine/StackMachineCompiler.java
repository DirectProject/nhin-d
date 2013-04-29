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

public class StackMachineCompiler implements org.nhindirect.policy.Compiler
{
	protected boolean reportModeEnabled;
	
	protected ThreadLocal<Collection<String>> compilerReport;
	
	public StackMachineCompiler()
	{
		reportModeEnabled = false;
		compilerReport = new ThreadLocal<Collection<String>>();
	}
	
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
	
	@Override
	public void setReportModeEnabled(boolean reportMode) 
	{
		this.reportModeEnabled = reportMode;
		
	}

	@Override
	public boolean isReportModeEnabled() 
	{
		return this.reportModeEnabled;
	}	
	
	public Collection<String> getCompilationReport()
	{
		final Collection<String> report = compilerReport.get();
		if (report != null)
			return Collections.unmodifiableCollection(report);
		else
			return Collections.emptyList();
	}
}
