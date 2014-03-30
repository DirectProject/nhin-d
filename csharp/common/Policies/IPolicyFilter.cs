/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.IO;
using System.Security.Cryptography.X509Certificates;


namespace Health.Direct.Common.Policies
{
    /// <summary>
    /// <para>
    /// Policy filters are the core constructs of the policy engine as they determine if an X509 certificate is compliant with a given policy.  Internally
    /// they encapsulate the functional components of the engine (parser, compiler, and execution engine), and orchestrate the flow of a certificate and 
    /// policy through the engine's components.
    /// See <see cref="Health.Direct.Policy.PolicyFilter"/> for the default implementation.
    /// </para>
    /// <para>
    /// Filters are aggregate objects.  Each functional component of the policy engine can be used independently to perform specific tasks, but filters combine
    /// the components together to achieve the primary values proposition of the engine: evaluating X509 certificate compliance to a policy.
    /// </para>
    /// </summary>
    public interface IPolicyFilter
    {
        /// <summary>
        /// Checks if an X509 certificate is compliant with a given policy.  The policy is expressed in a given lexicon which must be parsed.
        /// </summary>
        /// <param name="cert">The certificate that will be checked for compliance.</param>
        /// <param name="policyStream">The policy stream</param>
        /// <returns>True if the certificate is compliant with the given policy.  False otherwise.</returns>
        /// <exception cref="PolicyProcessException">Thrown if the policy engine process cannot be successfully executed.</exception>
        bool IsCompliant(X509Certificate2 cert, Stream policyStream);
        /// <summary>
        /// Checks if an X509 certificate is compliant with a given policy.  This is a slight variation from the other version of this method in that
        /// it takes a previously parsed <see cref="Health.Direct.Common.Policies.IPolicyExpression"/>.  This method version exists for performance reasons when it is not necessary to 
        /// parse the policy expression from a lexicon input stream; it allows the reuse of a parsed <see cref="Health.Direct.Common.Policies.IPolicyExpression"/>.
        /// </summary>
        /// <param name="cert"></param>
        /// <param name="expression"></param>
        /// <returns>True if the certificate is compliant with the given policy.  False otherwise.</returns>
        /// <exception cref="PolicyProcessException">Thrown if the policy engine process cannot be successfully executed.</exception>
        bool IsCompliant(X509Certificate2 cert, IPolicyExpression expression);
    }
}
