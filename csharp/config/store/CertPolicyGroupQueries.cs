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
using System.Drawing.Drawing2D;
using System.Linq;
using Health.Direct.Common.Mail;

namespace Health.Direct.Config.Store
{
    public static class CertPolicyGroupQueries
    {
        const string Sql_DeleteCertPolicyGroups = "Delete from CertPolicyGroups where CertPolicyId = {0}";

        const string Sql_DeleteCertPolicyGroupMap =
            @"
                    Delete from CertPolicyGroupMap
                    Where CertPolicyId = {0}
                    And   CertPolicyGroupId = {1}
            ";

        private const string Sql_DeleteCertPolicyGroupDomainMap =
            @"
                    Delete from CertPolicyGroupDomainMap
                    Where CertPolicyGroupId = {0}
                    And   Owner = {1}
            ";
        const string Sql_DeleteAllCertPolicies =
                     @" Begin tran 
                        Delete from CertPolicyGroupDomainMap 
                        Delete from CertPolicyGroupMap 
                        Delete from CertPolicyGroups
                        DBCC CHECKIDENT(CertPolicyGroups,RESEED,0)                         
                    commit tran 
                ";

        static readonly Func<ConfigDatabase, string, IQueryable<CertPolicyGroup>> CertPolicyGroupByName = CompiledQuery.Compile(
           (ConfigDatabase db, string certPolicyName) =>
           from policyGroup in db.CertPolicyGroups
           where policyGroup.Name == certPolicyName
           select policyGroup
           );

        static readonly Func<ConfigDatabase, long, IQueryable<CertPolicyGroup>> CertPolicyGroupByID = CompiledQuery.Compile(
           (ConfigDatabase db, long ID) =>
           from policyGroup in db.CertPolicyGroups
           where policyGroup.ID == ID
           select policyGroup
           );

        static readonly Func<ConfigDatabase, long, int, IQueryable<CertPolicyGroup>> EnumPoliciyGroupsByID = CompiledQuery.Compile(
            (ConfigDatabase db, long lastPolicyID, int maxResults) =>
            (from certPolicyGroup in db.CertPolicyGroups
             where certPolicyGroup.ID > lastPolicyID
             orderby certPolicyGroup.ID ascending
             select certPolicyGroup).Take(maxResults)
            );

        public static ConfigDatabase GetDB(this Table<CertPolicyGroup> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static int GetCount(this Table<CertPolicyGroup> table)
        {
            return (from policyGroup in table.GetDB().CertPolicyGroups
                    select policyGroup.ID).Count();
        }

        public static CertPolicyGroup Get(this Table<CertPolicyGroup> table, string name)
        {
            return CertPolicyGroupByName(table.GetDB(), name).SingleOrDefault();
        }

        public static CertPolicyGroup Get(this Table<CertPolicyGroup> table, long id)
        {
            return CertPolicyGroupByID(table.GetDB(), id).SingleOrDefault();
        }

        public static IQueryable<CertPolicyGroup> GetByOwner(this Table<CertPolicyGroup> table, string owner)
        {
            var q = from certPolicyGroup in table.GetDB().CertPolicyGroups
                    join domainMap in table.GetDB().CertPolicyGroupDomainMaps
                        on certPolicyGroup equals domainMap.CertPolicyGroup
                    where domainMap.Owner == owner
                    select certPolicyGroup;
            return q;
        }

        public static IEnumerable<CertPolicyGroup> GetByOwners(this Table<CertPolicyGroup> table, string[] owners)
        {
            var q =
                (from certPolicyGroup in table.GetDB().CertPolicyGroups
                 select new
                        {
                            CertPolicyGroup = certPolicyGroup,
                            certPolicyGroup.CertPolicyGroupDomainMaps
                        }).Where(arg => arg.CertPolicyGroupDomainMaps.Any(map => owners.Contains(map.Owner)))
                    .AsEnumerable() //key to this working.
                    .Select(cpg => new CertPolicyGroup
                                   {
                                       ID = cpg.CertPolicyGroup.ID,
                                       Name = cpg.CertPolicyGroup.Name,
                                       Description = cpg.CertPolicyGroup.Description,
                                       CreateDate = cpg.CertPolicyGroup.CreateDate,
                                       CertPolicyGroupDomainMaps =
                                           (from map in table.GetDB().CertPolicyGroupDomainMaps
                                            where owners.Contains(map.Owner)
                                            && cpg.CertPolicyGroup.ID == map.CertPolicyGroup.ID
                                            select map).ToList(),
                                       CertPolicyGroupMaps =
                                          (from map in table.GetDB().CertPolicyGroupMaps
                                           select new
                                                  {
                                                      CertPolicyGroupMap = map,
                                                      map.CertPolicy
                                                  })
                                           .Where(arg => cpg.CertPolicyGroup == arg.CertPolicyGroupMap.CertPolicyGroup)
                                           .AsEnumerable()
                                           .Select(gm => new CertPolicyGroupMap()
                                                        {
                                                            Use = gm.CertPolicyGroupMap.Use,
                                                            ForIncoming = gm.CertPolicyGroupMap.ForIncoming,
                                                            ForOutgoing = gm.CertPolicyGroupMap.ForOutgoing,
                                                            CreateDate = gm.CertPolicyGroupMap.CreateDate,
                                                            CertPolicyGroup = gm.CertPolicyGroupMap.CertPolicyGroup,
                                                            CertPolicy =
                                                                (from cp in table.GetDB().CertPolicies
                                                                 where cp == gm.CertPolicy
                                                                 select cp
                                                                   ).FirstOrDefault()

                                                        }).ToList()
                                   });
            return q;
        }


        public static IQueryable<CertPolicyGroup> Get(this Table<CertPolicyGroup> table, long lastPolicyID, int maxResults)
        {
            return EnumPoliciyGroupsByID(table.GetDB(), lastPolicyID, maxResults);
        }

        public static void ExecDelete(this Table<CertPolicyGroup> table, long certPolicyGroupId)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertPolicyGroups, certPolicyGroupId);
        }

        public static void ExecDeleteGroupMap(this Table<CertPolicyGroup> table, CertPolicyGroupMap map)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertPolicyGroupMap, map.CertPolicy.ID, map.CertPolicyGroup.ID);
        }

        public static void ExecDeleteDomainMap(this Table<CertPolicyGroup> table, CertPolicyGroupDomainMap map)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertPolicyGroupDomainMap, map.CertPolicyGroup.ID, map.Owner);
        }

        public static void ExecDeleteAll(this Table<CertPolicyGroup> table)
        {
            table.Context.ExecuteCommand(Sql_DeleteAllCertPolicies);
        }
    }
}
