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

import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;

/**
 * Implementation of the {@link Opcode} interface specific to the {@link StackMachine} execution engine.
 * @author Greg Meyer
 * @since 1.0
 */
public class StackMachineEntry implements Opcode
{
	/**
	 * Enumerates the type of machine entry
	 * @author Greg Meyer
	 * @since 1.0
	 *
	 */
	public static enum EntryType
	{
		/**
		 * An operator entry
		 */
		OPERATOR,
		
		/**
		 * A policy value entry
		 */
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
				builder.append("Operator: " + getOperator().getOperatorText() + "\r\n");
				break;
			case VALUE:
				builder.append("Value: " + getValue().getPolicyValue() + "\r\n");
				break;	
		}
		
		return builder.toString();
	}
}
