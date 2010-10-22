/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Net.Mail;

using NHINDirect.Mail;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents domains managed by an agent.
    /// </summary>
    public class AgentDomains
    {
        //
        // Currently, we use this dictionary as a fast lookup table
        // In the future, we may maintain additional state for each domain
        //
        Dictionary<string, string> m_managedDomains;
        
        /// <summary>
        /// Create a new AgentDomains object
        /// </summary>
        /// <param name="domains">set of domains to manage</param>        
        public AgentDomains(string[] domains)
        {
            this.SetDomains(domains);
        }
        
        /// <summary>
        /// Gets the domains managed.
        /// </summary>
        public IEnumerable<string> Domains
        {
            get
            {
                return m_managedDomains.Keys;
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
            
            return this.IsManaged(address.Host);
        }

        /// <summary>
        /// Tests if an address is managed.
        /// </summary>
        /// <param name="domain">The domain in <c>string</c> form to test</param>
        /// <returns><c>true</c> if the address's domain is managed by the agent,
        /// <c>false</c> otherwise.</returns>
        public bool IsManaged(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("value was null or empty", "domain");
            }
            
            return m_managedDomains.ContainsKey(domain);
        }

        void SetDomains(string[] domains)
        {
            if (!AgentDomains.Validate(domains))
            {
                throw new ArgumentException("domains");                
            }

            m_managedDomains = new Dictionary<string, string>(MailStandard.Comparer); // Case-IN-sensitive
            for (int i = 0; i < domains.Length; ++i)
            {
                string domain = domains[i];
                m_managedDomains[domain] = domain;
            }
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