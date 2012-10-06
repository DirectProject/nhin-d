/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
  
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
using System.Text;

namespace Health.Direct.Config.Store
{
    public static class MdnQueries
    {
        private const string Sql_DeleteMdn = "DELETE From Mdns where MessageId = {0} and Receiver = {1}";

        private const string Sql_DeleteAllMdn =
            "BEGIN TRAN DELETE From Mdns DBCC CHECKIDENT([Mdns],RESEED,0) COMMIT TRAN ";

        private const string Sql_DeleteTimedOutMdns =
            "BEGIN TRAN DELETE From Mdns Where Timedout = 1 COMMIT TRAN ";

        private const string Sql_DeleteCompletedMdns =
            @"  BEGIN TRAN 
                    DELETE From Mdns 
                    Where 
                        Status = 'processed' AND NotifyDispatched = 0
                    OR
                        Status = 'dispatched'
                COMMIT TRAN 
            ";
            
        private static readonly Func<ConfigDatabase, string, IQueryable<Mdn>> Mdn = CompiledQuery.Compile(
            (ConfigDatabase db, string mdnIdentifier) =>
            from mdn in db.Mdns
            where mdn.MdnIdentifier == mdnIdentifier
            select mdn
            );


        private static readonly Func<ConfigDatabase, DateTime, int, IQueryable<Mdn>> ExpiredProcessedMdns = CompiledQuery.Compile(
            (ConfigDatabase db, DateTime expiredLimit, int maxResults) =>
            (from mdn in db.Mdns
             where mdn.CreateDate < expiredLimit
             && mdn.Status == null 
             && mdn.Timedout == false
             orderby mdn.CreateDate descending 
             select mdn).Take(maxResults)
            );

        private static readonly Func<ConfigDatabase, DateTime, int, IQueryable<Mdn>> ExpiredDispatchededMdns = CompiledQuery.Compile(
            (ConfigDatabase db, DateTime expiredLimit, int maxResults) =>
            (from mdn in db.Mdns
             where mdn.MdnProcessedDate < expiredLimit
             && mdn.Status == MdnStatus.Processed
             && mdn.NotifyDispatched       //timely and reliable must have been requestd (X-DIRECT-FINAL-DESTINATION-DELIVERY)
             && mdn.Timedout == false
             orderby mdn.CreateDate descending 
             select mdn).Take(maxResults)
            );

        private static readonly Func<ConfigDatabase, IQueryable<Mdn>> TimedOutMdns = CompiledQuery.Compile(
            (ConfigDatabase db) =>
            (from mdn in db.Mdns
             where mdn.Timedout
             select mdn)
            );

        public static ConfigDatabase GetDb(this Table<Mdn> table)
        {
            var dbContext = (ConfigDatabase) table.Context;
            return dbContext;
        }

        public static Mdn Get(this Table<Mdn> table, string mdnIdentifier)
        {
            return Mdn(table.GetDb(), mdnIdentifier).SingleOrDefault();
        }

        public static IEnumerable<Mdn> GetTimedOut(this Table<Mdn> table)
        {
            return TimedOutMdns(table.GetDb());
        }

        public static IEnumerable<Mdn> GetExpiredProcessed(this Table<Mdn> table, TimeSpan expiredLimit, int maxResults)
        {
            DateTime lookBackTime = DateTimeHelper.Now.Subtract(expiredLimit);
            return ExpiredProcessedMdns(table.GetDb(), lookBackTime, maxResults);
        }

        public static IEnumerable<Mdn> GetExpiredDispatched(this Table<Mdn> table, TimeSpan expiredLimit, int maxResults)
        {
            DateTime lookBackTime = DateTimeHelper.Now.Subtract(expiredLimit);
            return ExpiredDispatchededMdns(table.GetDb(), lookBackTime, maxResults);
        }

        public static int GetCount(this Table<Mdn> table)
        {
            return (from mdn in table.GetDb().Mdns
                    select "0").Count();
        }

        public static void ExecDelete(this Table<Mdn> table, Mdn mdn)
        {
            table.Context.ExecuteCommand(Sql_DeleteMdn, mdn);
        }

        public static void ExecDeleteTimedOut(this Table<Mdn> table)
        {
            table.Context.ExecuteCommand(Sql_DeleteTimedOutMdns);
        }

        public static void ExecDeleteDispositions(this Table<Mdn> table)
        {
            table.Context.ExecuteCommand(Sql_DeleteCompletedMdns);
        }

        public static void ExecDeleteAll(this Table<Mdn> table)
        {
            table.Context.ExecuteCommand(Sql_DeleteAllMdn);
        }
    }
       
    
}