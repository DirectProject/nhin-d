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
using System.Net.Mail;

using Health.Direct.Common.Mail;

namespace Health.Direct.SmtpAgent
{
    internal class DomainPostmasters : IEnumerable<MailAddress>
    {
        const string DefaultPostmasterUserName = "postmaster";
        
        Dictionary<string, MailAddress> m_postmasters;
           
        internal DomainPostmasters()
        {
            m_postmasters = new Dictionary<string,MailAddress>(StringComparer.OrdinalIgnoreCase);
        }
        
        /// <summary>
        /// Return the postmaster for the given domain. If none, return null
        /// </summary>
        public MailAddress this[string domain]
        {
            get
            {
                MailAddress address = null;
                if (m_postmasters.TryGetValue(domain, out address))
                {
                    return address;
                }
                
                return null;
            }
            internal set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                if (string.IsNullOrEmpty(domain))
                {
                    throw new ArgumentException("domain was null or empty", "domain");
                }
                
                m_postmasters[domain] = value;
            }
        }
        
        public MailAddress this[MailAddress address]
        {
            get
            {
                if (address == null)
                {
                    throw new ArgumentNullException("address");
                }
                
                return this[address.Host];
            }
        }
                       
        internal void Init(IEnumerable<string> domains, string[] postmasters)
        {
            if (postmasters != null && postmasters.Length > 0)
            {
                for (int i = 0; i < postmasters.Length; ++i)
                {
                    MailAddress address = new MailAddress(postmasters[i]);
                    m_postmasters[address.Host] = address;
                }                        
            }
            
            foreach(string domain in domains)
            {
                if (!m_postmasters.ContainsKey(domain))
                {
                    m_postmasters[domain] = new MailAddress(string.Format("{0}@{1}", DefaultPostmasterUserName, domain));
                }
            }
        }
        
        internal bool IsPostmaster(MailAddress address)
        {
            MailAddress postmaster = null;
            if (!m_postmasters.TryGetValue(address.Host, out postmaster))
            {
                return false;
            }
            
            return (MailStandard.Equals(address.User, postmaster.User));
        }
        
        internal bool IsPostmaster(string address)
        {
            return this.IsPostmaster(new MailAddress(address));
        }

        public IEnumerator<MailAddress> GetEnumerator()
        {
            return m_postmasters.Values.GetEnumerator();
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}