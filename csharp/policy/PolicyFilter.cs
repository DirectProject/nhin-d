﻿/* 
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
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.Machine;

namespace Health.Direct.Policy
{
    public class PolicyFilter : IPolicyFilter
    {
        readonly ICompiler m_compiler;
        private readonly IPolicyLexiconParser m_parser;

        public PolicyFilter(ICompiler compiler, IPolicyLexiconParser parser)
        {
            m_compiler = compiler;
            m_parser = parser;
        }

        /// <summary>
        /// The default policy filter.
        /// </summary>
        static IPolicyFilter _defaultPolicyFilter = new PolicyFilter(
            new StackMachineCompiler(), 
            new SimpleTextV1LexiconPolicyParser());

        /// <summary>
        /// Gets and sets the default policy filter.
        /// </summary>
        public static IPolicyFilter Default
        {
            get
            {
                return _defaultPolicyFilter;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }

                System.Threading.Interlocked.Exchange(ref _defaultPolicyFilter, value);
            }
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

            StackMachine executionEngine = new StackMachine();
            IList<IOpCode> opcodes = m_compiler.Compile(cert, expression);
            var compliant = executionEngine.Evaluate(opcodes);

            return compliant;
        }
    }
}
