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

package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.nhindirect.policy.ReferencePolicyExpression;

/**
 * Interface definition for an X509 certificate referenced policy expression.  Each object takes an X509Certificate and
 * evaluates to a specific field or extension of the certificate.
 * <p>
 * An attribute may be flagged as required meaning that the field or extension must be present in the certificate
 * to comply with the policy.
 * @author Greg Meyer
 * @since 1.0
 * @param <P> The object type of the evaluated field of the X509Certiciate.
 */
public interface X509Field<P> extends ReferencePolicyExpression<X509Certificate, P>
{
	/**
	 * Gets The field type of the certificate.
	 * @return The field type of the certificate.
	 */
	public X509FieldType getX509FieldType();
	
	/**
	 * Indicates if the field or extension must exist in the certificate to be compliant with the policy.
	 * @return true if the field or extension is required. false otherwise
	 */
	public boolean isRequired();
	
	/**
	 * Sets the required indicator.
	 * @param required The required indicate.  Set to true if the field or extension is required. false otherwise
	 */
	public void setRequired(boolean required);
}
