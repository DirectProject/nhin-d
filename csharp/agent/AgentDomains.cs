/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using Health.Direct.Common.Domains;
using Health.Direct.Common.Mail;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents domains managed by an agent.
    /// </summary>
    public class AgentDomains
    {
        IDomainResolver m_tenancy;

        /// <summary>
        /// Create a new AgentDomains object
        /// </summary>
        /// <param name="tenancy">set of domains to manage</param>        
        public AgentDomains(IDomainResolver tenancy)
        {
            m_tenancy = tenancy;

            if (tenancy is StaticDomainResolver && !tenancy.Validate(tenancy.Domains.ToArray()))
            {
                throw new ArgumentException("Missing domains.");
            }
        }
        
        /// <summary>
        /// Gets the domains managed.
        /// </summary>
        public IEnumerable<string> Domains
        {
            get
            {
                return m_tenancy.Domains;
            }
        }
        
        /// <summary>
        /// Tests if an address is managed.
        /// </summary>
        /// <param name="address">The <see cref="MailAddress"/> to test</param>
        /// <returns><c>true</c> if the address's domain is managed by the agent,
        /// <c>false</c> otherwise.</returns>
        public bool IsManaged(MailAddress address)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }
            return m_tenancy.IsManaged(address.Host);
        }

        
        internal static bool Validate(string[] domains)
        {
            if (domains == null || domains.Length == 0)
            {
                return false;
            }

            for (int i = 0; i < domains.Length; ++i)
            {
                string domain = domains[i];
                if (string.IsNullOrEmpty(domain))
                {
                    return false;
                }
            }
            
            return true;
        }
    }
}