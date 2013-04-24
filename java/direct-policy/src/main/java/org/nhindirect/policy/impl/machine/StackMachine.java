package org.nhindirect.policy.impl.machine;

import java.lang.reflect.Constructor;
import java.util.Stack;
import java.util.Vector;

import org.nhindirect.policy.ExecutionEngine;
import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyOperatorExecutor;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValue;

public class StackMachine implements ExecutionEngine
{
	protected final Stack<PolicyValue<?>> machineStack = new Stack<PolicyValue<?>>();
	
	public StackMachine()
	{

	}

	@Override
	public Boolean evaluate(Vector<Opcode> opcodes) throws PolicyProcessException
	{

		Boolean retVal;
		
		for (Opcode opcode : opcodes)
		{
			// the vector for this machine type should only use StackMachineEntry codes
			final StackMachineEntry entry = StackMachineEntry.class.cast(opcode);
			
			switch(entry.getEntryType())
			{
			    case VALUE:
				{
					machineStack.push(entry.getValue());
					break;
				}
			    case OPERATOR:
				{
					PolicyOperatorExecutor<?,?> executor = null;
					switch(entry.getOperator().getParamsType())
					{
						case BINARY:	
						{	
							if (machineStack.size() < 2)
								throw new IllegalStateException("Stack machine must have at least two pushed operands for " + entry.getOperator().getOperatorText()
										+ " operator");
							
							executor = 
									createOperatorExecutor(entry.getOperator(), machineStack.pop(), machineStack.pop());
								
							break;
						}					
						case UNARY:
						{
							if (machineStack.size() < 1)
								throw new IllegalStateException("Stack machine must have at least one pushed operand for " + entry.getOperator().getOperatorText()
										+ " operator");
							
							executor = 
									createOperatorExecutor(entry.getOperator(), machineStack.pop());
							
							break;
						}
					}
					
					machineStack.push(executor.execute());
					
					break;
				}
				
			}
		}
		
		if (machineStack.isEmpty() || machineStack.size() > 1)
			throw new IllegalStateException("Stack machine is either empty or has remaining parameters to be processed." +
					"\r\n\tFinal stack size: " + machineStack.size());
		
		final PolicyValue<?> finalValue = machineStack.pop();
		try
		{
			retVal = Boolean.class.cast(finalValue.getPolicyValue());
		}
		catch (ClassCastException e)
		{
			throw new IllegalStateException("Final machine value must be a boolean litteral" +
					"\r\n\tFinal value type: " + finalValue.getPolicyValue().getClass() 
					+ "\r\n\tFinal value value:" + finalValue.getPolicyValue().toString(), e);			
		}
		
		return retVal;
	}
	
	protected PolicyOperatorExecutor<?,?> createOperatorExecutor(PolicyOperator operator, PolicyValue<?>... values) throws PolicyProcessException
	{
		PolicyOperatorExecutor<?,?> executor = null;
		Constructor<?> constructor = null;
		
		switch(operator.getParamsType())
		{
			case BINARY:
			{
				try
				{
					constructor =
							operator.getExecutorClass().getConstructor(PolicyValue.class, PolicyValue.class, PolicyOperator.class);
					
				}
				///CLOVER:OFF
				catch (Exception e)
				{
					throw new PolicyProcessException("Failed to get constructor for operator executor.", e);
				}
				///CLOVER:ON
				break;
			}					
			case UNARY:
			{
				try
				{
					constructor =
							operator.getExecutorClass().getConstructor(PolicyValue.class, PolicyOperator.class);
				}
				///CLOVER:OFF
				catch (Exception e)
				{
					throw new PolicyProcessException("Failed to get constructor for operator executor.", e);
				}
				///CLOVER:ON
				break;
			}	
		}
		
		try
		{
			if (values.length == 1)
				executor =  PolicyOperatorExecutor.class.cast(constructor.newInstance(values[0], operator));
			else
				executor =  PolicyOperatorExecutor.class.cast(constructor.newInstance(values[0], values[1], operator));
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			throw new PolicyProcessException("Failed to create operator executor.", e);
		}
		///CLOVER:ON
		
		return executor;
	}
	
}
