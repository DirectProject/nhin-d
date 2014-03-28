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


namespace Health.Direct.Config.Store
{
    public static class MdnQueries
    {
        private const string Sql_DeleteMdn = "DELETE From Mdns where MessageId = {0} and Receiver = {1}";

        private const string Sql_DeleteAllMdn =
            @"BEGIN TRAN 
                DELETE From Mdns DBCC CHECKIDENT([Mdns],RESEED,0) 
              COMMIT TRAN ";

        private const string Sql_DeleteTimedOutMdns =
            @"  --CTE Common table expression
                ;With Candidates as (
	                Select top ({1})
		                  MessageId
		                , RecipientAddress
	                From
		                Mdns
	                Where
	                ( 
		                Status = 'timedout' 
	                )
	                AND
		                CreateDate < {0}
	                Order by CreateDate desc
                )

                Delete Mdns
                Where
	                MessageId in (select MessageId from Candidates)
	                and
	                RecipientAddress in (select RecipientAddress from Candidates)
            ";

        private const string Sql_DeleteCompletedMdns =
            @"  --CTE Common table expression
                ;With Candidates as (
	                Select top ({1})
		                  MessageId
		                , RecipientAddress
                        , SenderAddress
                        , Status
	                From
		                Mdns
	                Where
	                ( 
		                Status = 'processed' AND NotifyDispatched = 0
	                OR
		                Status = 'dispatched'
                    OR
                        Status = 'Failed'
	                )
	                AND
		                CreateDate < {0}
	                Order by CreateDate desc
                )

                Delete Mdns
                Where
	                   MessageId in (select MessageId from Candidates)
	                   and
	                   (
                            RecipientAddress in (select RecipientAddress from Candidates)
                            or
                            RecipientAddress in (select SenderAddress from Candidates where Status = 'Failed')                 
                       )
                    
               
            ";

        private const string Sql_ExpiredProcessedMdns =

            @"  ;With timeOuts as (
	                Select MessageId
	                From Mdns	
	                Where status = 'timedout'
                    Or status = 'processed'
                    Or status = 'dispatched'
                    Or status = 'Failed'
                )
                Select 
	                top({1}) *
                From Mdns
                Where
	                MessageId not in (select MessageId from timeOuts)
                And
	                CreateDate <  {0}
                Order by CreateDate desc
            ";

        private const string Sql_ExpiredDispatchedMdns =

           @"  ;With timeOuts as (
	                Select MessageId
	                From Mdns	
	                Where status = 'timedout'
                    Or status = 'dispatched'
                    Or status = 'Failed'
                ),
                dispatchRequests as (
	                Select max(MdnId) MdnId
	                From Mdns
	                Group by MessageId		
                    Having max(Cast(NotifyDispatched as tinyint)) = 1                       
                )  

                Select 
	                top({1}) *
                From Mdns
                Where
	                MessageId not in (select MessageId from timeOuts)
                And
                    Status = 'processed'
                And
					MdnId in (select MdnId from dispatchRequests)
                And
	                CreateDate <  {0}
                Order by CreateDate desc
            ";

        private const string Sql_GetMdn =
            @"
                Declare @notifyRequest tinyint;
                set @notifyRequest =
                (   select max(Cast(m2.NotifyDispatched as tinyint))
                    FROM Mdns m1
                    Join Mdns m2 
                    on m1.MessageId = m2.MessageId
                    where m1.MdnIdentifier= {0}
                    group by m2.MessageId
                );

                select 
	                 [MdnIdentifier]
	                ,[MdnId]
	                ,[MessageId]
	                ,[RecipientAddress]
	                ,[SenderAddress]
	                ,[Subject]
	                ,[Status]
	                ,Cast(@notifyRequest as bit) as NotifyDispatched    
	                ,[CreateDate]
                From Mdns
                Where MdnIdentifier= {0}
                ";

        const string Sql_EnumMdnFirst = "SELECT TOP ({0}) * from Mdns order by CreateDate desc";
        const string Sql_EnumMdnNext = "SELECT TOP ({0}) * from Mdns where CreateDate > {1} order by CreateDate asc";

        
        static readonly Func<ConfigDatabase, IQueryable<Mdn>> TimedOutMdns = CompiledQuery.Compile(
            (ConfigDatabase db) =>
            (from mdn in db.Mdns
             where mdn.Status == MdnStatus.TimedOut
             select mdn)
            );

        
        public static ConfigDatabase GetDB(this Table<Mdn> table)
        {
            return (ConfigDatabase)table.Context;
        }


        public static Mdn Get(this Table<Mdn> table, string mdnIdentifier)
        {
            return table.GetDB().ExecuteQuery<Mdn>(Sql_GetMdn, mdnIdentifier).FirstOrDefault();
        }

        public static IEnumerable<Mdn> ExecGet(this Table<Mdn> table, string lastMdn, int maxResults)
        {
            if (string.IsNullOrEmpty(lastMdn))
            {
                return table.GetDB().ExecuteQuery<Mdn>(Sql_EnumMdnFirst, maxResults);
            }

            return table.GetDB().ExecuteQuery<Mdn>(Sql_EnumMdnNext, maxResults, lastMdn);
        }

        public static IEnumerable<Mdn> GetTimedOut(this Table<Mdn> table)
        {
            return TimedOutMdns(table.GetDB());
        }

        public static IEnumerable<Mdn> GetExpiredProcessed(this Table<Mdn> table, TimeSpan expiredLimit, int maxResults)
        {
            DateTime lookBackTime = DateTimeHelper.Now.Subtract(expiredLimit);
            return table.GetDB().ExecuteQuery<Mdn>(Sql_ExpiredProcessedMdns, lookBackTime, maxResults);
        }

        public static IEnumerable<Mdn> GetExpiredDispatched(this Table<Mdn> table, TimeSpan expiredLimit, int maxResults)
        {
            DateTime lookBackTime = DateTimeHelper.Now.Subtract(expiredLimit);
            return table.GetDB().ExecuteQuery<Mdn>(Sql_ExpiredDispatchedMdns, lookBackTime, maxResults);
        }

        public static int GetCount(this Table<Mdn> table)
        {
            return (from mdn in table.GetDB().Mdns
                    select "0").Count();
        }

        public static void ExecDelete(this Table<Mdn> table, Mdn mdn)
        {
            table.Context.ExecuteCommand(Sql_DeleteMdn, mdn);
        }

        public static void ExecDeleteTimedOut(this Table<Mdn> table, TimeSpan limitTime, int bulkCount)
        {
            DateTime lookBackTime = DateTimeHelper.Now.Subtract(limitTime);
            table.Context.ExecuteCommand(Sql_DeleteTimedOutMdns, lookBackTime.ToString(), bulkCount);
        }

        public static void ExecDeleteDispositions(this Table<Mdn> table, TimeSpan limitTime, int bulkCount)
        {
            DateTime lookBackTime = DateTimeHelper.Now.Subtract(limitTime);
            table.Context.ExecuteCommand(Sql_DeleteCompletedMdns, lookBackTime.ToString(), bulkCount);
        }

        public static void ExecDeleteAll(this Table<Mdn> table)
        {

            table.Context.ExecuteCommand(Sql_DeleteAllMdn);
        }

    }
}