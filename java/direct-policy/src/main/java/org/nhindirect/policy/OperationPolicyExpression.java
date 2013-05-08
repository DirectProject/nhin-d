package org.nhindirect.policy;

import java.util.Vector;

public interface OperationPolicyExpression extends PolicyExpression
{	
	public PolicyOperator getPolicyOperator();
	
	public Vector<PolicyExpression> getOperands();

}
