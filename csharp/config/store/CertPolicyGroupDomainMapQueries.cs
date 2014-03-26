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
    public static class CertPolicyGroupDomainMapQueries
    {
        const string Sql_DeleteCertPolicyGroupMapsByGroup = "Delete from CertPolicyGroupDomainMap where CertPolicyGroupId = {0}";
        const string Sql_DeleteCertPolicyGroupMapsByOwner = "Delete from CertPolicyGroupDomainMap where Owner = {0}";

        public static ConfigDatabase GetDB(this Table<CertPolicyGroupDomainMap> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static void ExecDelete(this Table<CertPolicyGroupDomainMap> table, long certPolicyGroupId)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertPolicyGroupMapsByGroup, certPolicyGroupId);
        }

        public static void ExecDelete(this Table<CertPolicyGroupDomainMap> table, string owner)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertPolicyGroupMapsByOwner, owner);
        }

        static readonly Func<ConfigDatabase, string, string, IQueryable<CertPolicyGroupDomainMap>> map = CompiledQuery.Compile(
            (ConfigDatabase db, string groupName, string owner) =>

                from groups in db.CertPolicyGroups
                join ownerMap in db.CertPolicyGroupDomainMaps
                    on groups equals ownerMap.CertPolicyGroup
                where groups.Name == groupName
                    && ownerMap.Owner == owner
                select ownerMap
            );


        public static bool Exists(this Table<CertPolicyGroupDomainMap> table, string groupName, string owner)
        {
            return map(table.GetDB(), groupName, owner).Any();
        }
    }
}