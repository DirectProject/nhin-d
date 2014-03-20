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
using System.Data.Linq;
using System.Linq;
using System.Windows.Forms.VisualStyles;

namespace Health.Direct.Config.Store
{
    public static class CertPolicyQueries
    {
        const string Sql_DeleteCertPolicies =
            @" Begin tran
                DELETE from CertPolicyGroupMap where CertPolicyId = {0}                
                DELETE from CertPolicies where CertPolicyId = {0}
               Commit tran 
            ";

        const string Sql_DeleteAllCertPolicies =
                     @" Begin tran                         
                        delete from CertPolicyGroupMap                        
                        delete from CertPolicies
                        DBCC CHECKIDENT(CertPolicies,RESEED,0)                                             
                    Commit tran 
                ";

        static readonly Func<ConfigDatabase, string, IQueryable<CertPolicy>> CertPolicyByName = CompiledQuery.Compile(
            (ConfigDatabase db, string certPolicyName) =>
            from certPolicy in db.CertPolicies
            where certPolicy.Name == certPolicyName
            select certPolicy
            );

        static readonly Func<ConfigDatabase, long, IQueryable<CertPolicy>> CertPolicyByID = CompiledQuery.Compile(
           (ConfigDatabase db, long ID) =>
           from certPolicy in db.CertPolicies
           where certPolicy.ID == ID
           select certPolicy
           );

        static readonly Func<ConfigDatabase, long, int, IQueryable<CertPolicy>> EnumPoliciesByID = CompiledQuery.Compile(
            (ConfigDatabase db, long lastPolicyID, int maxResults) =>
            (from certPolicy in db.CertPolicies
             where certPolicy.ID > lastPolicyID
             orderby certPolicy.ID ascending
             select certPolicy).Take(maxResults)
            );

        public static ConfigDatabase GetDB(this Table<CertPolicy> table)
        {
            ConfigDatabase db = (ConfigDatabase)table.Context;
            //db.LoadOptions = CertPolicyManager.DataLoadOptions;
            return db;
        }

        public static int GetCount(this Table<CertPolicy> table)
        {
            return (from certPolicy in table.GetDB().CertPolicies
                    select certPolicy.ID).Count();
        }

        public static CertPolicy Get(this Table<CertPolicy> table, string name)
        {
            return CertPolicyByName(table.GetDB(), name).SingleOrDefault();
        }

        public static CertPolicy Get(this Table<CertPolicy> table, long id)
        {
            return CertPolicyByID(table.GetDB(), id).SingleOrDefault();
        }

        public static IEnumerable<CertPolicy> GetIncoming(this Table<CertPolicy> table, string @owner)
        {
            var q =
                from c in table.GetDB().CertPolicies
                join groupMap in table.GetDB().CertPolicyGroupMaps
                    on c equals groupMap.CertPolicy
                join g in table.GetDB().CertPolicyGroups
                    on groupMap.CertPolicyGroup equals g
                join domainMap in table.GetDB().CertPolicyGroupDomainMaps
                    on g equals domainMap.CertPolicyGroup
                where domainMap.Owner == @owner
                      && groupMap.ForIncoming
                select c;
            return q;
        }

        public static IEnumerable<CertPolicy> GetOutgoing(this Table<CertPolicy> table, string owner)
        {
            var q =
                from c in table.GetDB().CertPolicies
                join groupMap in table.GetDB().CertPolicyGroupMaps
                    on c equals groupMap.CertPolicy
                join g in table.GetDB().CertPolicyGroups
                    on groupMap.CertPolicyGroup equals g
                join domainMap in table.GetDB().CertPolicyGroupDomainMaps
                    on g equals domainMap.CertPolicyGroup
                where domainMap.Owner == @owner
                      && groupMap.ForOutgoing
                select c;
            return q;
        }

        

        public static IEnumerable<CertPolicy> Get(this Table<CertPolicy> table, long[] policyIDs)
        {
            return from cp in table.GetDB().CertPolicies
                   where policyIDs.Contains(cp.ID)
                   select cp;
        }

        public static IQueryable<CertPolicy> Get(this Table<CertPolicy> table, long lastPolicyID, int maxResults)
        {
            return EnumPoliciesByID(table.GetDB(), lastPolicyID, maxResults);
        }

        public static void ExecDelete(this Table<CertPolicy> table, long certPolicyId)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertPolicies, certPolicyId);
        }

        public static void ExecDeleteAll(this Table<CertPolicy> table)
        {
            table.Context.ExecuteCommand(Sql_DeleteAllCertPolicies);
        }
    }

}
