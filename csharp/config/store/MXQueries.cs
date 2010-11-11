/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Data.Linq;

using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    public static class MXQueries
    {
        const string Sql_Deletemx = "DELETE from MXs where SMTPDomainName = {0}";
        const string Sql_DeletemxByDomain = "DELETE from MXs where DomainID = {0}";
        const string Sql_SetPreference = "UPDATE MXs set Preference = {0}, UpdateDate={1} where SMTPDomainName in ({2})";
        const string Sql_SetPreferenceByDomain = "UPDATE MXs set Preference = {0}, UpdateDate={1} where DomainID = {2}";
        const string Sql_EnumDomainMXFirst = "SELECT TOP ({0}) * from MXs where DomainID = {1} order by SMTPDomainName asc";
        const string Sql_EnumDomainMXNext = "SELECT TOP ({0}) * from MXs where DomainID = {1} and SMTPDomainName > {2} order by SMTPDomainName asc";
        const string Sql_EnumMXFirst = "SELECT TOP ({0}) * from MXs order by SMTPDomainName asc";
        const string Sql_EnumMXNext = "SELECT TOP ({0}) * from MXs where SMTPDomainName > {1} order by SMTPDomainName asc";
        const string Sql_Truncate = "truncate table MXs";

        static readonly Func<ConfigDatabase, string, IQueryable<MX>> MXs = CompiledQuery.Compile(
            (ConfigDatabase db, string SMTPDomainName) =>
            from mx in db.MXs
            where mx.SMTPDomainName == SMTPDomainName
            select mx
            );

        static readonly Func<ConfigDatabase, long, long, int, IQueryable<MX>> DomainMXs = CompiledQuery.Compile(
            (ConfigDatabase db, long domainID, long lastmxID, int maxResults) =>
            (from mx in db.MXs
             where mx.DomainID == domainID && mx.ID > lastmxID
             orderby mx.ID
             select mx).Take(maxResults)
            );

        static readonly Func<ConfigDatabase, long, int, IQueryable<MX>> AllMXs = CompiledQuery.Compile(
            (ConfigDatabase db, long lastmxID, int maxResults) =>
            (from mx in db.MXs
             where mx.ID > lastmxID
             orderby mx.ID
             select mx).Take(maxResults)
            );

        public static ConfigDatabase GetDB(this Table<MX> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static int GetCount(this Table<MX> table, long domainID)
        {
            return (from mx in table.GetDB().MXs
                    where mx.DomainID == domainID
                    select mx.ID).Count();
        }

        public static MX Get(this Table<MX> table, string SMTPDomainName)
        {
            return MXs(table.GetDB(), SMTPDomainName).SingleOrDefault();
        }

        public static IEnumerable<MX> Get(this Table<MX> table, string[] emailMXs, int? Preference)
        {
            if (emailMXs.IsNullOrEmpty())
            {
                throw new ArgumentException("value was null or empty", "emailMXs");
            }
            //
            // We cannot precompile this (throws at runtime) because emailMXs.Length can change at runtime
            //
            return from mx in table.GetDB().MXs
                   where emailMXs.Contains(mx.SMTPDomainName) && mx.Preference == Preference
                   select mx;
        }

        public static IEnumerable<MX> Get(this Table<MX> table, string[] emailMXs)
        {
            if (emailMXs.IsNullOrEmpty())
            {
                throw new ArgumentException("value was null or empty", "emailMXs");
            }
            //
            // We cannot precompile this (throws at runtime) because emailMXs.Length can change at runtime
            //
            return from mx in table.GetDB().MXs
                   where emailMXs.Contains(mx.SMTPDomainName)
                   select mx;
        }

        public static IQueryable<MX> Get(this Table<MX> table, long domainID, long lastmxID, int maxResults)
        {
            return DomainMXs(table.GetDB(), domainID, lastmxID, maxResults);
        }

        public static IQueryable<MX> Get(this Table<MX> table, long lastmxID, int maxResults)
        {
            return AllMXs(table.GetDB(), lastmxID, maxResults);
        }

        public static IQueryable<MX> Get(this Table<MX> table, long[] ids)
        {
            //
            // We cannot precompile this (throws at runtime) because ids.Length can change at runtime
            //
            return from mx in table.GetDB().MXs
                   where ids.Contains(mx.ID)
                   select mx;
        }

        public static IQueryable<MX> Get(this Table<MX> table, long[] ids, int Preference)
        {
            //
            // We cannot precompile this (throws at runtime) because ids.Length can change at runtime
            //
            return from mx in table.GetDB().MXs
                   where ids.Contains(mx.ID) && mx.Preference == Preference
                   select mx;
        }

        public static void ExecDelete(this Table<MX> table, string SMTPDomainName)
        {
            table.Context.ExecuteCommand(Sql_Deletemx, SMTPDomainName);
        }

        public static void ExecDeleteDomain(this Table<MX> table, long domainID)
        {
            table.Context.ExecuteCommand(Sql_DeletemxByDomain, domainID);
        }

        public static void ExecTruncate(this Table<MX> table)
        {
            table.Context.ExecuteCommand(Sql_Truncate);
        }
        

        public static IEnumerable<MX> ExecGet(this Table<MX> table, long domainID, string lastmx, int maxResults)
        {
            if (string.IsNullOrEmpty(lastmx))
            {
                return table.GetDB().ExecuteQuery<MX>(Sql_EnumDomainMXFirst, maxResults, domainID);
            }

            return table.GetDB().ExecuteQuery<MX>(Sql_EnumDomainMXNext, maxResults, domainID, lastmx);
        }

        public static IEnumerable<MX> ExecGet(this Table<MX> table, string lastmx, int maxResults)
        {
            if (string.IsNullOrEmpty(lastmx))
            {
                return table.GetDB().ExecuteQuery<MX>(Sql_EnumMXFirst, maxResults);
            }

            return table.GetDB().ExecuteQuery<MX>(Sql_EnumMXNext, maxResults, lastmx);
        }
    }
}