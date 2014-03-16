/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Policies;

namespace Health.Direct.Policy
{
    public class DefaultPolicyFilter : IPolicyFilter
    {
        readonly ICompiler m_compiler;
        readonly IExecutionEngine m_executionEngine;
        private readonly IPolicyLexiconParser m_parser;

        public DefaultPolicyFilter(ICompiler compiler, IExecutionEngine engine, IPolicyLexiconParser parser)
        {
            m_compiler = compiler;
            m_executionEngine = engine;
            m_parser = parser;
        }

        public DefaultPolicyFilter(ICompiler compiler, IExecutionEngine engine)
        {
            m_compiler = compiler;
            m_executionEngine = engine;
        }

        public bool IsCompliant(X509Certificate2 cert, Stream policyStream)
        {
            IPolicyExpression expression = m_parser.Parse(policyStream);

            return IsCompliant(cert, expression);
        }

        public bool IsCompliant(X509Certificate2 cert, IPolicyExpression expression)
        {
            if (m_compiler == null)
                throw new InvalidOperationException("Compiler cannot be null");

            if (m_executionEngine == null)
                throw new InvalidOperationException("Execution engine cannot be null");

            IList<IOpCode> opcodes = m_compiler.Compile(cert, expression);
            return m_executionEngine.Evaluate(opcodes);
        }
    }
}
