package org.nhindirect.policy.impl.machine;

import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;

public class StackMachineEntry implements Opcode
{
	public static enum EntryType
	{
		OPERATOR,
		
		VALUE;
	}
	
	protected final EntryType entryType;
	protected final PolicyOperator operator;
	protected final PolicyValue<?> value;
	
	public StackMachineEntry(PolicyOperator operator)
	{
		this.operator = operator;
		this.entryType = EntryType.OPERATOR;
		this.value = null;
	}
	
	public StackMachineEntry(PolicyValue<?> value)
	{
		this.operator = null;
		this.entryType = EntryType.VALUE;
		this.value = value;
	}
	
	public PolicyValue<?> getValue()
	{
		return value;
	}
	
	public PolicyOperator getOperator()
	{
		return operator;
	}
	
	public EntryType getEntryType()
	{
		return entryType;
	}
	
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder("Entry Type: ")
				.append(getEntryType()).append("\r\n");
		switch(getEntryType())
		{
			case OPERATOR:
				builder.append("Operator: " + getOperator().getOperatorText());
				break;
			case VALUE:
				builder.append("Value: " + getValue().getPolicyValue());
				break;	
		}
		
		return builder.toString();
	}
}
