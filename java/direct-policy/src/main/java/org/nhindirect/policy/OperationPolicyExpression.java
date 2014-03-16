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

import java.util.Vector;

/**
 * Operation expression type object.  Operations are a combination of a {@link PolicyOperator} and one or more parameters.  Operator parameters are themselves expressions
 * allowing parameters to be either literals, references, or the result of another operation.
 * <br>
 * Instances of this interface are created using the {@link OperationPolicyExpressionFactory} class.
 * @author Greg Meyer
 * @since 1.0
 */
public interface OperationPolicyExpression extends PolicyExpression
{	
	/**
	 * Gets the operator that will be executed when the expression is evaluated.
	 * @return The operator that will be executed when the expression is evaluated.
	 */
	public PolicyOperator getPolicyOperator();
	
	/**
	 * Gets the parameters that will be used by the operator when the expression is evaluated.  For binary operations, the order of the parameters
	 * may be important (ex: the {@link PolicyOperator#GREATER} operator).
	 * @return The parameters that will be used by the operator when the expression is evaluated.
	 */
	public Vector<PolicyExpression> getOperands();

}
