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

import java.io.InputStream;
import java.security.cert.X509Certificate;

/**
 * Policy filters are the core constructs of the policy engine as they determine if an X509 certificate is compliant with a given policy.  Internally
 * they encapsulate the functional components of the engine (parser, compiler, and execution engine), and orchestrate the flow of a certificate and 
 * policy through the engine's components.
 * <p>
 * Filters are aggregate objects.  Each functional component of the policy engine can be used independently to perform specific tasks, but filters combine
 * the components together to achieve the primary values proposition of the engine: evaluating X509 certificate compliance to a policy.
 * <p>
 * Filter instances should be created using the {@link PolicyFilterFactory} class.
 * @author Greg Meyer
 * @since 1.0
 */
public interface PolicyFilter 
{
	/**
	 * Checks if an X509 certificate is compliant with a given policy.  The policy is expressed in a given lexicon which must be parsed.
	 * @param cert The certificate that will be checked for compliance.
	 * @param policyStream The policy stream in an input stream.
	 * @param lexicon The lexicon of the policy contained within the input stream.
	 * @return True if the certificate is compliant with the given policy.  False otherwise.
	 * @throws PolicyProcessException Thrown if the policy engine process cannot be successfully executed.
	 */
	boolean isCompliant(X509Certificate cert, InputStream policyStream, PolicyLexicon lexicon) throws PolicyProcessException;
	
	/**
	 * Checks if an X509 certificate is compliant with a given policy.  This is a slight variation from the other version of this method in that
	 * it takes a previously parsed {@link PolicyExpression}.  This method version exists for performance reasons when it is not necessary to 
	 * parse the policy expression from a lexicon input stream; it allows the reuse of a parsed {@link PolicyExpression}.
	 * @param cert The certificate that will be checked for compliance.
	 * @param expression A previously parsed or programmatically generated {@link PolicyExpression}.
	 * @return True if the certificate is compliant with the given policy.  False otherwise.
	 * @throws PolicyProcessException  Thrown if the policy engine process cannot be successfully executed.
	 */
	boolean isCompliant(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException;
}
