
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
using System.Data.Linq;
using System.Linq;

namespace Health.Direct.Config.Store
{
    public static class CertPolicyGroupMapQueries
    {
        const string Sql_DeleteCertPolicyGroupMap =
            @"
                    Delete from CertPolicyGroupMap
                    Where MapID = {0}
            ";

        public static ConfigDatabase GetDB(this Table<CertPolicyGroupMap> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static void ExecDeleteGroupMap(this Table<CertPolicyGroupMap> table, long mapId)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertPolicyGroupMap, mapId);
        }


        static readonly Func<ConfigDatabase, string, string, CertPolicyUse, bool, bool, IQueryable<CertPolicyGroupMap>> map = CompiledQuery.Compile(
            (ConfigDatabase db, string policyOwner, string groupName, CertPolicyUse policyUse, bool incoming,
                bool outgoing) =>
            
                from c in db.CertPolicies
                join groupMap in db.CertPolicyGroupMaps
                    on c equals groupMap.CertPolicy
                join g in db.CertPolicyGroups
                    on groupMap.CertPolicyGroup equals g
                where c.Name == policyOwner
                      && g.Name == groupName
                      && groupMap.Use == policyUse
                      && groupMap.ForIncoming == incoming
                      && groupMap.ForOutgoing == outgoing
                select groupMap
            
            );


        public static bool Exists(this Table<CertPolicyGroupMap> table, string policyName, string groupName, CertPolicyUse policyUse, bool incoming,
            bool outgoing)
        {
            return map(table.GetDB(), policyName, groupName, policyUse, incoming, outgoing).Any();
        }
    }
}
