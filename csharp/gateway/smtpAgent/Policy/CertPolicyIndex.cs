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

using System;
using System.Collections.Generic;
using System.Linq;
using Health.Direct.Common.Policies;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Policy;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.Impl;

namespace Health.Direct.SmtpAgent.Policy
{
    public class CertPolicyIndex : IPolicyIndex
    {
        ClientSettings m_clientSettings;
        bool m_incoming;

        internal CertPolicyIndex(ClientSettings clientSettings, bool incoming)
        {
            m_clientSettings = clientSettings;
            m_incoming = incoming;
        }

        /// <summary>
        /// Indexer for <see cref="IPolicyExpression"/>s.
        /// </summary>
        /// <param name="domain"></param>
        /// <returns></returns>
        public IList<IPolicyExpression> this[string domain]
        {
            get
            {
                IList<IPolicyExpression> matches;
                using (CertPolicyStoreClient client = this.CreateClient())
                {
                    if (m_incoming) //incoming
                    {
                        matches = client.GetIncomingPoliciesByDomain(domain)
                            .Select(p => GetPolicyExpression(p.Data))
                            .Select(p => p)
                            .Where(p => p != null)
                            .ToList();
                    }
                    else // outgoing
                    {
                        matches = client.GetOutgoingPoliciesByDomain(domain)
                            .Select(p => GetPolicyExpression(p.Data))
                            .Select(p => p)
                            .Where(p => p != null)
                            .ToList();
                    }
                }

                return matches;
            }
        }

        CertPolicyStoreClient CreateClient()
        {
            return new CertPolicyStoreClient(m_clientSettings.Binding, m_clientSettings.Endpoint);
        }

        public IPolicyExpression GetPolicyExpression(byte[] policy)
        {
            try
            {
                //might get parser from policy.Lexicon in the future
                var parser = new SimpleTextV1LexiconPolicyParser();
                IPolicyExpression expression = parser.Parse(policy.ToMemoryStream());
                return expression;
            }
            catch (Exception)
            {
                return null;
            }

        }
    }
}