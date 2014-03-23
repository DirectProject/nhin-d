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
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Client
{
    public static class CertPolicyExtensions
    {
        public static bool Contains(this CertPolicyStoreClient client, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("value was null or empty", "name");
            }
            CertPolicy policy = client.GetPolicyByName(name);
            return (policy != null);
        }

        public static bool Contains(this CertPolicyStoreClient client, string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
        {
            return client.PolicyToGroupExists(policyName, groupName, policyUse, incoming, outgoing);
        }

        public static bool Contains(this CertPolicyStoreClient client, string policyGroup, string owner)
        {
            return client.PolicyGroupToOwnerExists(policyGroup, owner);
        }

        public static IEnumerable<CertPolicy> EnumerateCertPolicies(this CertPolicyStoreClient client, int chunkSize)
        {
            if (chunkSize < 1)
            {
                throw new ArgumentException("value was less than 1", "chunkSize");
            }

            long lastPolicy = -1;

            CertPolicy[] policies;
            while (true)
            {
                policies = client.EnumerateCertPolicies(lastPolicy, chunkSize);
                if (policies.IsNullOrEmpty())
                {
                    yield break;
                }
                for (int i = 0; i < policies.Length; ++i)
                {
                    yield return policies[i];
                }
                lastPolicy = policies[policies.Length - 1].ID;
            }
        }

        public static IEnumerable<CertPolicyGroup> EnumerateCertPolicyGroups(this CertPolicyStoreClient client, int chunkSize)
        {
            if (chunkSize < 1)
            {
                throw new ArgumentException("value was less than 1", "chunkSize");
            }

            long lastPolicy = -1;

            CertPolicyGroup[] groups;
            while (true)
            {
                groups = client.EnumerateCertPolicyGroups(lastPolicy, chunkSize);
                if (groups.IsNullOrEmpty())
                {
                    yield break;
                }
                for (int i = 0; i < groups.Length; ++i)
                {
                    yield return groups[i];
                }
                lastPolicy = groups[groups.Length - 1].ID;
            }
        }


    }
}
