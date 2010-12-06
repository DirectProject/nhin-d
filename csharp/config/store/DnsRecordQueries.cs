/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico     chris.lomonico@surescripts.com
  
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

namespace Health.Direct.Config.Store
{
    public static class DnsRecordQueries
    {
        const string Sql_DeleteDnsRecord = "DELETE from DnsRecords where RecordID = {0}";
        const string Sql_UpdateDnsRecord = "UPDATE DnsRecords set RecordData={0}, UpdateDate={1} where RecordID = {2}";
        const string Sql_TruncateDnsRecords = "truncate table DnsRecords";

        static readonly Func<ConfigDatabase, string, IQueryable<DnsRecord>> DnsRecord = CompiledQuery.Compile(
            (ConfigDatabase db, string domainName) =>
            from dnsrecords in db.DnsRecords
            where dnsrecords.DomainName == domainName
            select dnsrecords
            );

        static readonly Func<ConfigDatabase, long, IQueryable<DnsRecord>> DnsRecordByID = CompiledQuery.Compile(
            (ConfigDatabase db, long recordID) =>
            from dnsrecords in db.DnsRecords
            where dnsrecords.ID == recordID
            select dnsrecords
            );

        static readonly Func<ConfigDatabase, long, int, IQueryable<DnsRecord>> AllDnsRecords = CompiledQuery.Compile(
            (ConfigDatabase db, long lastRecordID, int maxResults) =>
            (from dnsrecords in db.DnsRecords
             where dnsrecords.ID > lastRecordID
             orderby dnsrecords.ID ascending
             select dnsrecords).Take(maxResults)
            );

        static readonly Func<ConfigDatabase, long, int, int, IQueryable<DnsRecord>> AllDnsRecordsForType = CompiledQuery.Compile(
            (ConfigDatabase db, long lastRecordID, int maxResults, int typeID) =>
            (from dnsrecords in db.DnsRecords
             where dnsrecords.ID > lastRecordID
             && dnsrecords.TypeID == typeID
             orderby dnsrecords.ID ascending
             select dnsrecords).Take(maxResults)
            );


        public static ConfigDatabase GetDB(this Table<DnsRecord> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static IEnumerable<DnsRecord> Get(this Table<DnsRecord> table
            , string domainName
            , int? typeID)
        {
            if (typeID.HasValue)
            {
                return (from dnsrecords in table.GetDB().DnsRecords
                        where dnsrecords.TypeID == typeID.Value
                              && domainName.ToLower().Equals(dnsrecords.DomainName.ToLower())
                        select dnsrecords);
            }
            return (from dnsrecords in table.GetDB().DnsRecords
                    where domainName.ToLower().Equals(dnsrecords.DomainName.ToLower())
                    select dnsrecords);
        }


        public static DnsRecord Get(this Table<DnsRecord> table, string domainName)
        {
            return DnsRecord(table.GetDB(), domainName).SingleOrDefault();
        }
        
        public static int GetCount(this Table<DnsRecord> table, int? typeID)
        {
            if (typeID.HasValue)
            {
                return (from dnsrecords in table.GetDB().DnsRecords
                        where dnsrecords.TypeID == typeID.Value
                        select dnsrecords.ID).Count();

            }
            return (from dnsrecords in table.GetDB().DnsRecords
                    select dnsrecords.ID).Count();

        }

        public static IQueryable<DnsRecord> Get(this Table<DnsRecord> table, long lastID, int maxResults)
        {
            return AllDnsRecords(table.GetDB(), lastID, maxResults);
        }

        public static IQueryable<DnsRecord> Get(this Table<DnsRecord> table
            , long lastID
            , int maxResults
            , int typeID)
        {
            return AllDnsRecordsForType(table.GetDB()
                , lastID
                , maxResults
                ,typeID);
        }

        public static DnsRecord Get(this Table<DnsRecord> table, long recordID)
        {
            return DnsRecordByID(table.GetDB(), recordID).SingleOrDefault();
        }

        public static IEnumerable<DnsRecord> Get(this Table<DnsRecord> table, long[] recordIDs)
        {
            return from dnsrecords in table.GetDB().DnsRecords
                   where recordIDs.Contains(dnsrecords.ID)
                   select dnsrecords;
        }

        public static void DeleteAll(this Table<DnsRecord> table)
        {
            table.Context.ExecuteCommand(Sql_TruncateDnsRecords);
        }

        public static void ExecDelete(this Table<DnsRecord> table, long recordID)
        {
            table.Context.ExecuteCommand(Sql_DeleteDnsRecord, recordID);
        }

        public static void SetRecordData(this Table<DnsRecord> table
            , byte[] recordData
            , long recordID){
                table.Context.ExecuteCommand(Sql_UpdateDnsRecord
                    , recordData
                    , DateTimeHelper.Now
                    , recordID);
        }
    }
}
