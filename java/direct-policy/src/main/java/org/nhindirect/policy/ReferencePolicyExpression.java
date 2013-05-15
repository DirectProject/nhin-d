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

/**
 * Reference expression type object.  Reference expression values are set at runtime by injecting the referenced value into the expressing using the 
 * {@link #injectReferenceValue(Object)} method.  Although reference expressions may reference complex objects, they functionally only allow the retrieval of
 * an object attribute returned as a {@link PolicyValue}.
 * <br>
 * The policy engine supports either a generic structure or an X509 specific attributes.  X509 reference expressions are defined in the 
 * org.nhindirect.policy.x509 package.
 * @author Greg Meyer
 * @Since 1.0
 * 
 * @param <R> The type of the reference object.
 * @param <P> The type of value of the accessible {@link PolicyValue}.
 */
public interface ReferencePolicyExpression<R, P> extends LiteralPolicyExpression<P>
{
	/**
	 * Gets the type of referenceable  expressions.
	 * @return The type of referenceable  expressions.
	 */
	public PolicyExpressionReferenceType getPolicyExpressionReferenceType();
	
	/**
	 * Injects the referenced value into the expressions.  The relevant accessible attribute of the reference object can be 
	 * retrieved by calling the {@link #getPolicyValue()} method.
	 * @param value The reference value.
	 * @throws PolicyProcessException Thrown if the reference value cannot be successfully processed or the relevant accessible attribute cannot be 
	 * retrieved from the value.  For example, the X509 reference expressions may require an X509 attribute to be present in a certificate.  If the required
	 * certificate attribute is not present, the this exception would be thrown.
	 */
	public void injectReferenceValue(R value) throws PolicyProcessException;
}
