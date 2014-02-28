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
using System.Data.Linq;
using System.Linq;

namespace Health.Direct.Config.Store
{
    public static class CertPolcyQueries
    {
        const string Sql_DeleteAllCertPolicies =
                     @" Begin tran                         
                        delete from CertPolicyGroupMap                        
                        delete from CertPolicy
                        DBCC CHECKIDENT(CertPolicy,RESEED,0)                                             
                    commit tran 
                ";

        static readonly Func<ConfigDatabase, string, IQueryable<CertPolicy>> CertPolicy = CompiledQuery.Compile(
            (ConfigDatabase db, string certPolicyName) =>
            from certPolicy in db.CertPolicies
            where certPolicy.Name == certPolicyName
            select certPolicy
            );

        
        public static ConfigDatabase GetDB(this Table<CertPolicy> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static int GetCount(this Table<CertPolicy> table)
        {
            return (from certPolicy in table.GetDB().CertPolicies
                    select certPolicy.ID).Count();
        }

        public static CertPolicy Get(this Table<CertPolicy> table, string name)
        {
            return CertPolicy(table.GetDB(), name).SingleOrDefault();
        }
        
        public static void ExecDeleteAll(this Table<CertPolicy> table)
        {
            table.Context.ExecuteCommand(Sql_DeleteAllCertPolicies);
        }
    }

}
