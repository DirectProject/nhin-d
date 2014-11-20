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

using System.Security.Cryptography.X509Certificates;
using Health.Direct.Policy;

namespace Health.Direct.Common.Policies
{
    //TODO: revisit the explanation. Specifically the Operations: part.

    /// <summary>
    /// <para>
    /// Expressions are the building blocks of building policy statements.  Expressions can be simple literals or a complex series of operations that take other expressions as parameters.
    /// </para>
    /// <para>
    /// PolicyExpression objects are generally the resulting intermediate state of a parsed input by an implemented <see cref="Health.Direct.Policy.IPolicyLexiconParser"/> before being further processed by the policy engine <see cref="ICompiler"/>\
    /// </para>
    /// Expressions are categorized into three types.
    /// <ul>
    ///     <li>Literals: Literals are simply primitive types or objects that have a static value.  In the policy engine, literals are represented by <see cref="Health.Direct.Policy.IPolicyValue{T}"/> objects. </li>
    ///     <li>References: References are objects whose values are evaluated at runtime similar to variables.  Reference may be simple structures or specific structure types such as X509 certificates. At this time all known implementations are of type <see cref="X509Certificate2"/> </li>
    ///     <li>Operations: Operations are a combination of a <see cref="PolicyOperator{T}"/> and one or more parameters.  Operator parameters are themselves expressions allowing parameters to be either literals, references, or the result of another operation. </li>
    ///  </ul>
    /// <para>
    /// Because operator parameters are expressions, complex expressions can be built, nesting other operations as parameters resulting in a tree construct.
    /// Expressions are built using the either the <see cref="ILiteralPolicyExpression{T}"/>, the <see cref="IOperationPolicyExpression"/>, or by instantiating 
    /// one of the defined reference structures in the <see cref="Health.Direct.Policy.X509"/> name space.
    /// </para>
    /// </summary>
    public interface IPolicyExpression
    {
        /// <summary>
        /// Get the expression type.
        /// </summary>
        /// <returns><see cref="PolicyExpressionType"/></returns>
        PolicyExpressionType GetExpressionType();
    }
}