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

package org.nhindirect.policy;

import java.util.HashMap;
import java.util.Map;

import org.nhindirect.policy.impl.BinaryBooleanPolicyOperatorExecutor;
import org.nhindirect.policy.impl.BinaryCollectionPolicyOperatorExecutor;
import org.nhindirect.policy.impl.BinaryIntegerPolicyOperatorExecutor;
import org.nhindirect.policy.impl.UnaryBooleanPolicyOperatorExecutor;
import org.nhindirect.policy.impl.UnaryIntegerPolicyOperatorExecutor;

/**
 * Enumeration of operators supported by the policy engine.  Each operator has equal precedence and are evaluated in the 
 * order they are encountered.
 * @author Greg Meyer
 * @since 1.0
 */
public enum PolicyOperator 
{	
	
	/**
	 * Performs an equality operation against two operands.  Equality semantics are specific the operands types.
	 */
	EQUALS("=", "equals", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a non-equality operation against two operands.  Equality semantics are specific the operands types.
	 */
	NOT_EQUALS("!=", "not equals", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a greater than operation against two operands.  This operation can only be performed on numeric operands.
	 */
	GREATER(">", "greater than", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),

	/**
	 * Performs a less than operation against two operands.  This operation can only be performed on numeric operands.
	 */
	LESS("<", "less than", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a regular expression match on a string.  This operation returns true if the regular expression is found in 
	 * the given string and can only be performed on two strings.
	 */
	REG_EX("$", "matches", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a search for the existence of an object within a collection.  This operation returns true if the object exists within
	 * the collection.  One of the operands MUST be a collection of arbitrary types that will be searched.
	 */
	CONTAINS("{?}", "contains", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),

	/**
	 * Performs a search for the non-existence of an object within a collection.  This operation returns true if the object 
	 * does not exist within the collection.  One of the operands MUST be a collection of arbitrary types that will be searched.
	 */
	NOT_CONTAINS("{?}!", "not contains", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a regular expressions match on a collection of strings.  This operations returns true if one of the elements
	 * within collection matches the regular expressions.  The operation can only be performed on a string a collection arbitrary types.
	 * Non string types are converted to strings using the object's toString method. 
	 */
	CONTAINS_REG_EX("{}$", "contains match", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs an evaluation of the number of elements within a collection.  This operation is only performed on a single
	 * collection of arbitrary objects.
	 */
	SIZE("^", "size", UnaryIntegerPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),

	/**
	 * Performs an evaluation of the number of elements within a collection and determines if the collection is empty.  
	 * This operation is only performed on a single collection of arbitrary objects.
	 */
	EMPTY("{}", "empty", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),
	

	/**
	 * Performs an evaluation of the number of elements within a collection and determines if the collection is non-empty.  
	 * This operation is only performed on a single collection of arbitrary objects.
	 */
	NOT_EMPTY("{}!", "not empty", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),
	
	/**
	 * Performs an intersection of two sets and returns the resulting set.  This operation can only be performed on collection operands.
	 */
	INTERSECTION("{}&", "intersection", BinaryCollectionPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a logical or operation against two operands.  This operation can only be performed on two boolean values.  Boolean
	 * values may be the result of another boolean expression.
	 */
	LOGICAL_OR("||",  "or", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a logical and operation against two operands.  This operation can only be performed on two boolean values.  Boolean
	 * values may be the result of another boolean expression.
	 */
	LOGICAL_AND("&&", "and", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),

	/**
	 * Performs a logical not operation against a single boolean value.  The boolean
	 * values may be the result of another boolean expression.
	 */	
	LOGICAL_NOT("!", "not", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),
	
	/**
	 * Performs a bitwise and operation against two operands.  This operation can only be performed on two numeric values.
	 */
	BITWISE_AND("&", "bitand", BinaryIntegerPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a bitwise or operation against two operands.  This operation can only be performed on two numeric values.
	 */
	BITWISE_OR("|", "bitor", BinaryIntegerPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	/**
	 * Performs a URL validation operation against a URL.  The URL is provided as a string, and valid indicates that the URL is syntactically correct and that
	 * it is accessible from the application performing the validation.
	 */
	URI_VALIDATE("@@", "uri validate", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY);

	
	protected final String operatorToken;
	protected final String operatorText;
	protected final Class<?> executorClass;
	protected final PolicyOperatorParamsType paramsType;
	protected static final Map<String, PolicyOperator> tokenOperatorMap; 
	
	static
	{
		tokenOperatorMap = new HashMap<String, PolicyOperator>();
		
		final PolicyOperator[] operators = (PolicyOperator[].class.cast(PolicyOperator.class.getEnumConstants()));
		for (PolicyOperator operator : operators)
			tokenOperatorMap.put(operator.getOperatorToken(), operator);
	}	
	
	/*
	 * Private constructor
	 */
	private PolicyOperator(String operatorToken, String operatorText, Class<?> executorClass, PolicyOperatorParamsType paramsType)
	{
		this.operatorToken = operatorToken;
		this.operatorText = operatorText;
		this.executorClass = executorClass;
		this.paramsType = paramsType;
	}
	
	/**
	 * Gets the token of the operator used in a lexicon parser.
	 * @return  The token of the operator used in a lexicon parser.
	 */
	public String getOperatorToken()
	{
		return operatorToken;
	}
	
	/**
	 * Gets the plain English representation of the operator.
	 * @return The plain English representation of the operator.
	 */
	public String getOperatorText()
	{
		return operatorText;
	}
	
	/**
	 * Gets the {@link PolicyOperator} class that performs the actual execution logic of the operator.
	 * @return The {@link PolicyOperator} class that performs the actual execution logic of the operator.
	 */
	public Class<?> getExecutorClass()
	{
		return executorClass;
	}
	
	/**
	 * Gets the {@link PolicyOperatorParamsType} of the operator.
	 * @return  The {@link PolicyOperatorParamsType} of the operator.
	 */
	public PolicyOperatorParamsType getParamsType()
	{
		return paramsType;
	}
	
	/**
	 * Gets the policy operator associated with a specific token string.
	 * @param token The token used to look up the PolicyOperator.
	 * @return The PolicyOperator associated with the token.  If the token does not represent a known operator, then null is returned,.
	 */
	public static PolicyOperator fromToken(String token)
	{
		return tokenOperatorMap.get(token);
	}
}
