/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescipts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Net.Mail;
using Health.Direct.Common.Caching;
using Health.Direct.Common.Certificates;
using Health.Direct.Policy;
using Health.Direct.SmtpAgent.Config;

namespace Health.Direct.SmtpAgent.Policy
{

    /// <inheritdoc />
    public class PrivatePolicyResolver : IPolicyResolver
    {
        IPolicyResolver m_outgoingPolicies;
        IPolicyResolver m_incomingPolicies;
        PrivatePolicyServiceResolverSettings m_settings;

        public PrivatePolicyResolver(PrivatePolicyServiceResolverSettings settings)
        {

            m_settings = settings;

            CacheSettings incomingCacheSettings =
                new CacheSettings(m_settings.CacheSettings) { Name = "BundleCache.incoming" };

            CacheSettings outgoingCacheSettings =
                new CacheSettings(m_settings.CacheSettings) { Name = "BundleCache.outgoing" };

            //m_incomingResolver =
            //    new PolicyResolver(new BundleAnchorIndex(m_settings, true), incomingCacheSettings);

            //m_outgoingResolver =
            //    new CertificateResolver(new BundleAnchorIndex(m_settings, false), outgoingCacheSettings);

        }
        public IList<IPolicyExpression>
            GetOutgoingPolicy(MailAddress address)
        {
            throw new NotImplementedException();
        }

        public IList<IPolicyExpression> GetIncomingPolicy(MailAddress address)
        {
            throw new NotImplementedException();
        }
    }
}