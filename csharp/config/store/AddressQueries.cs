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
    public static class AddressQueries
    {
        const string Sql_DeleteAddress = "DELETE from Addresses where EmailAddress = {0}";
        const string Sql_DeleteAddressByDomain = "DELETE from Addresses where DomainID = {0}";
        const string Sql_SetStatus = "UPDATE Addresses set Status = {0}, UpdateDate={1} where EmailAddress in ({2})";

        static readonly Func<ConfigDatabase, string, IQueryable<Address>> Addresses = CompiledQuery.Compile(
            (ConfigDatabase db, string emailAddress) =>
                from address in db.Addresses
                where address.EmailAddress == emailAddress
                select address
        );

        static readonly Func<ConfigDatabase, long, long, int, IQueryable<Address>> DomainAddresses = CompiledQuery.Compile(
            (ConfigDatabase db, long domainID, long lastAddressID, int maxResults) =>
                (from address in db.Addresses
                 where address.DomainID == domainID && address.ID > lastAddressID
                 orderby address.ID
                 select address).Take(maxResults)
        );

        static readonly Func<ConfigDatabase, long, int, IQueryable<Address>> AllAddresses = CompiledQuery.Compile(
            (ConfigDatabase db, long lastAddressID, int maxResults) =>
                (from address in db.Addresses
                 where address.ID > lastAddressID
                 orderby address.ID
                 select address).Take(maxResults)
        );

        public static ConfigDatabase GetDB(this Table<Address> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static Address Get(this Table<Address> table, string emailAddress)
        {
            return Addresses(table.GetDB(), emailAddress).SingleOrDefault();
        }

        public static IEnumerable<Address> Get(this Table<Address> table, string[] emailAddresses)
        {
            //
            // We cannot precompile this (throws at runtime) because emailAddresses.Length can change at runtime
            //
            return from address in table.GetDB().Addresses
                where emailAddresses.Contains(address.EmailAddress)
                select address;
        }

        public static IQueryable<Address> Get(this Table<Address> table, long domainID, long lastAddressID, int maxResults)
        {
            return DomainAddresses(table.GetDB(), domainID, lastAddressID, maxResults);
        }

        public static IQueryable<Address> Get(this Table<Address> table, long lastAddressID, int maxResults)
        {
            return AllAddresses(table.GetDB(), lastAddressID, maxResults);
        }

        public static IQueryable<Address> Get(this Table<Address> table, long[] ids)
        {
            //
            // We cannot precompile this (throws at runtime) because ids.Length can change at runtime
            //
            return from address in table.GetDB().Addresses
                 where ids.Contains(address.ID)
                 select address;
        }

        public static void ExecDelete(this Table<Address> table, string emailAddress)
        {
            table.Context.ExecuteCommand(Sql_DeleteAddress, emailAddress);
        }

        public static void ExecDeleteDomain(this Table<Address> table, long domainID)
        {
            table.Context.ExecuteCommand(Sql_DeleteAddressByDomain, domainID);
        }
        
        public static void ExecSetStatus(this Table<Address> table, string[] emailAddresses, EntityStatus status)
        {
            table.Context.ExecuteCommand(Sql_SetStatus, status, DateTime.Now, emailAddresses.ToIn());
        }
    }
}
