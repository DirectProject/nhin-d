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

using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Client
{
    public static class DomainManagerExtensions
    {
        public static bool DomainExists(this DomainManagerClient client, string domainName)
        {
            Domain domain = client.GetDomain(domainName);
            return (domain != null);
        }
        
        public static Domain GetDomain(this DomainManagerClient client, string domainName, EntityStatus? status)
        {
            if (string.IsNullOrEmpty(domainName))
            {
                throw new ArgumentException("value was null or empty", "domainName");
            }

            Domain[] domains = client.GetDomains(new string[] { domainName }, status);
            if (domains.IsNullOrEmpty())
            {
                return null;
            }

            return domains[0];
        }

        public static Domain GetDomain(this DomainManagerClient client, string domainName)
        {
            return client.GetDomain(domainName, null);
        }

        public static Domain GetDomain(this DomainManagerClient client, MailAddress address)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }
            
            return client.GetDomain(address.Host);
        }

        public static void RemoveDomain(this DomainManagerClient client, MailAddress address)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }

            client.RemoveDomain(address.Host);
        }

        public static IEnumerable<Domain> EnumerateDomains(this DomainManagerClient client, int chunkSize)
        {
            if (chunkSize < 1)
            {
                throw new ArgumentException("value was less than 1", "chunkSize");
            }

            string lastDomain = null;

            Domain[] domains;
            while (true)
            {
                domains = client.EnumerateDomains(lastDomain, chunkSize);
                if (domains.IsNullOrEmpty())
                {
                    yield break;
                }
                for (int i = 0; i < domains.Length; ++i)
                {
                    yield return domains[i];
                }
                lastDomain = domains[domains.Length - 1].Name;
            }
        }
    }
}