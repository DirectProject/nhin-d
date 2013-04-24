package org.nhindirect.policy.impl.machine;

import java.security.cert.X509Certificate;
import java.util.Vector;

import org.nhindirect.policy.LiteralPolicyExpression;
import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.OperationPolicyExpression;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.ReferencePolicyExpression;
import org.nhindirect.policy.x509.X509Field;

public class StackMachineCompiler implements org.nhindirect.policy.Compiler
{
	public StackMachineCompiler()
	{
		
	}
	
	@Override
	public Vector<Opcode> compile(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException
	{	
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
		    default:
		    	return null;
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
			default:
				return null;
		}
	}
	
	protected PolicyValue<?> evaluateX509Field(X509Certificate cert, X509Field<?> expression) throws PolicyProcessException
	{
		expression.injectReferenceValue(cert);
		
		return expression.getPolicyValue();
	}
}
