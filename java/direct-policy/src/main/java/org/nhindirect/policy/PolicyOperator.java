package org.nhindirect.policy;

import org.nhindirect.policy.impl.BinaryBooleanPolicyOperatorExecutor;
import org.nhindirect.policy.impl.BinaryCollectionPolicyOperatorExecutor;
import org.nhindirect.policy.impl.BinaryIntegerPolicyOperatorExecutor;
import org.nhindirect.policy.impl.UnaryBooleanPolicyOperatorExecutor;
import org.nhindirect.policy.impl.UnaryIntegerPolicyOperatorExecutor;

public enum PolicyOperator 
{	
	EQUALS("=", "equals", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	NOT_EQUALS("!=", "not equals", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	GREATER(">", "greater than", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),

	LESS("<", "less than", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	REG_EX("$", "matches", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	CONTAINS("{?}", "contains", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),

	NOT_CONTAINS("!{?}", "not contains", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	CONTAINS_REG_EX("{$}", "matches", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	SIZE("^", "size", UnaryIntegerPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),

	EMPTY("{}", "empty", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),
	
	NOT_EMPTY("!{}", "not empty", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),
	
	INTERSECTION("&{}", "intersection", BinaryCollectionPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	LOGICAL_OR("||",  "or", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	LOGICAL_AND("&&", "and", BinaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),

	LOGICAL_NOT("!", "not", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY),
	
	BITWISE_AND("&", "bitand", BinaryIntegerPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	BITWISE_OR("|", "bitor", BinaryIntegerPolicyOperatorExecutor.class, PolicyOperatorParamsType.BINARY),
	
	URI_VALIDATE("@@", "uri validate", UnaryBooleanPolicyOperatorExecutor.class, PolicyOperatorParamsType.UNARY);

	
	protected final String operatorToken;
	protected final String operatorText;
	protected final Class<?> executorClass;
	protected final PolicyOperatorParamsType paramsType;
	
	private PolicyOperator(String operatorToken, String operatorText, Class<?> executorClass, PolicyOperatorParamsType paramsType)
	{
		this.operatorToken = operatorToken;
		this.operatorText = operatorText;
		this.executorClass = executorClass;
		this.paramsType = paramsType;
	}
	
	public String getOperatorToken()
	{
		return operatorToken;
	}
	
	public String getOperatorText()
	{
		return operatorText;
	}
	
	public Class<?> getExecutorClass()
	{
		return executorClass;
	}
	
	public PolicyOperatorParamsType getParamsType()
	{
		return paramsType;
	}
}
