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
using Health.Direct.Policy.X509;
using Health.Direct.Policy.X509.Standard;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class XMLLexiconPolicyParser_serializeTest
    {
        [Fact (Skip="Xml parser not implemented")]
        public void TestSerialize_SimpleExpression_ValidateExpression()
        {
            LiteralPolicyExpression<int> expr =
                new LiteralPolicyExpression<int>(new PolicyValue<int>((int) KeyUsageBit.DataEncipherment));
            XMLLexiconPolicyParser parser = new XMLLexiconPolicyParser(expr);

            KeyUsageExtensionField extensionField = new KeyUsageExtensionField(true);
            List<IPolicyExpression> operands = new List<IPolicyExpression>();
            operands.Add(expr);
            operands.Add(extensionField);

            IOperationPolicyExpression oper = new OperationPolicyExpression(
                PolicyOperator<int, int, bool>.EQUALS, operands);

           
            string xml = parser.Serialize(oper);
            Console.WriteLine(xml);
            
        }
    }
}
