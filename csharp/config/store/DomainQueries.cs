/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Linq;
using System.Data.Linq.Mapping;
using System.Net.Mail;

namespace NHINDirect.Config.Store
{
    public static class DomainQueries
    {
        const string Sql_DeleteDomain = "DELETE from Domains where DomainName = {0}";

        static readonly Func<ConfigDatabase, string, IQueryable<Domain>> Domain = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
                from domain in db.Domains
                where domain.Name == owner
                select domain
        );
        static readonly Func<ConfigDatabase, long, int, IQueryable<Domain>> Domains = CompiledQuery.Compile(
            (ConfigDatabase db, long lastDomainID, int maxResults) =>
                (from domain in db.Domains
                 where domain.ID > lastDomainID
                 orderby domain.ID
                 select domain).Take(maxResults)
        );

        public static ConfigDatabase GetDB(this Table<Domain> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static Domain Get(this Table<Domain> table, string domainName)
        {
            return Domain(table.GetDB(), domainName).SingleOrDefault();
        }

        public static IEnumerable<Domain> Get(this Table<Domain> table, string[] domains, EntityStatus status)
        {
            if (domains.IsNullOrEmpty())
            {
                throw new ArgumentException();
            }
            //
            // We cannot precompile this (throws at runtime) because domains.Length can change at runtime
            //
            return from domain in table.GetDB().Domains
                   where domains.Contains(domain.Name) && domain.Status == status
                   select domain;
        }

        public static IEnumerable<Domain> Get(this Table<Domain> table, string[] domains)
        {
            if (domains.IsNullOrEmpty())
            {
                throw new ArgumentException();
            }
            //
            // We cannot precompile this (throws at runtime) because domains.Length can change at runtime
            //
            return from domain in table.GetDB().Domains
                   where domains.Contains(domain.Name)
                   select domain;
        }

        public static IQueryable<Domain> Get(this Table<Domain> table, long lastDomainID, int maxResults)
        {
            return Domains(table.GetDB(), lastDomainID, maxResults);
        }

        public static void ExecDelete(this Table<Domain> table, string domainName)
        {
            table.Context.ExecuteCommand(Sql_DeleteDomain, domainName);
        }
    }
}
