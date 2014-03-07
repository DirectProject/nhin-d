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
using System.IO;
using System.Xml.Serialization;

namespace Health.Direct.Policy
{

    public class XMLLexiconPolicyParser : IPolicyLexiconParser, IPolicyExpressionSerializer
    {
        IPolicyExpression m_policyExpression;

        public XMLLexiconPolicyParser()
        {
        }

        /// <summary>
        /// Create a new XMLLexiconPolicyParser
        /// </summary>
        public XMLLexiconPolicyParser(IPolicyExpression policyExpression)
        {
            m_policyExpression = policyExpression;

        }

        public virtual IPolicyExpression Parse(Stream stream)
        {
            m_policyExpression = Deserialize<IPolicyExpression>(stream);
            return m_policyExpression;
        }

        public T Deserialize<T>(Stream stream)
        {
            if (stream == null)
            {
                throw new ArgumentNullException("stream");
            }
            XmlSerializer xmlSerializer = new XmlSerializer(typeof(T));

            try
            {
                T retVal = (T)xmlSerializer.Deserialize(stream);
                return retVal;
            }
            catch (Exception e)
            {
                throw new PolicyParseException("Could not deseriale policy expression from XML.", e);
            }
        }

        public string Serialize(IPolicyExpression expression)
        {
            if (expression == null)
            {
                throw new ArgumentNullException("expression");
            }

            using (StringWriter writer = new StringWriter())
            {
                Serialize(expression, writer);
                return writer.ToString();
            }
        }

        public void Serialize(IPolicyExpression expression, TextWriter writer)
        {
            if (expression == null)
            {
                throw new ArgumentNullException("expression");
            }
            XmlSerializer xmlSerializer = new XmlSerializer(expression.GetType());
            xmlSerializer.Serialize(writer, expression);
        }

        public void Serialize(IPolicyExpression expression, Stream stream)
        {
            if (expression == null)
                throw new ArgumentNullException("expression");

            if (stream == null)
                throw new ArgumentNullException("stream");

            XmlSerializer xmlSerializer = new XmlSerializer(expression.GetType());

            xmlSerializer.Serialize(stream, expression);
        }
    }
}
