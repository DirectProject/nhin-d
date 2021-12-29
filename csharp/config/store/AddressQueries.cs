// /* 
//  Copyright (c) 2010, Direct Project
//  All rights reserved.
//
//  Authors:
//     Umesh Madan     umeshma@microsoft.com
//   
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
// Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
// Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//  
// */
// using System;
// using System.Collections.Generic;
// using System.Linq;
// using System.Data.Linq;
//
// using Health.Direct.Common.Extensions;
// using Health.Direct.Config.Store.Entity;
//
// namespace Health.Direct.Config.Store
// {
//     public static class AddressQueries
//     {
//         const string Sql_DeleteAddress = "DELETE from Addresses where EmailAddress = {0}";
//         const string Sql_DeleteAddressByDomain = "DELETE from Addresses where DomainID = {0}";
//         const string Sql_SetStatus = "UPDATE Addresses set Status = {0}, UpdateDate={1} where EmailAddress in ({2})";
//         const string Sql_SetStatusByDomain = "UPDATE Addresses set Status = {0}, UpdateDate={1} where DomainID = {2}";
//         const string Sql_EnumDomainAddressFirst = "SELECT TOP ({0}) * from Addresses where DomainID = {1} order by EmailAddress asc";
//         const string Sql_EnumDomainAddressNext = "SELECT TOP ({0}) * from Addresses where DomainID = {1} and EmailAddress > {2} order by EmailAddress asc";
//         const string Sql_SelectByDomainName = "SELECT TOP ({0}) * from Addresses a inner join Domains b on a.domainid = b.domainid where b.DomainName like {1} order by EmailAddress asc";
//         const string Sql_EnumAddressFirst = "SELECT TOP ({0}) * from Addresses order by EmailAddress asc";
//         const string Sql_EnumAddressNext = "SELECT TOP ({0}) * from Addresses where EmailAddress > {1} order by EmailAddress asc";
//         const string Sql_TruncateAddresses = "truncate table Addresses";
//         
//         static readonly Func<DirectDbContext, string, IQueryable<Address>> Addresses = CompiledQuery.Compile(
//             (DirectDbContext db, string emailAddress) =>
//             from address in db.Addresses
//             where address.EmailAddress == emailAddress
//             select address
//             );
//
//         static readonly Func<DirectDbContext, long, long, int, IQueryable<Address>> DomainAddresses = CompiledQuery.Compile(
//             (DirectDbContext db, long domainID, long lastAddressID, int maxResults) =>
//             (from address in db.Addresses
//              where address.DomainID == domainID && address.CertPolicyId > lastAddressID
//              orderby address.CertPolicyId
//              select address).Take(maxResults)
//             );
//
//         static readonly Func<DirectDbContext, long, int, IQueryable<Address>> AllAddresses = CompiledQuery.Compile(
//             (DirectDbContext db, long lastAddressID, int maxResults) =>
//             (from address in db.Addresses
//              where address.CertPolicyId > lastAddressID
//              orderby address.CertPolicyId
//              select address).Take(maxResults)
//             );
//         
//         static readonly Func<DirectDbContext, long, IQueryable<Address>> IDToAddress = CompiledQuery.Compile(
//             (DirectDbContext db, long addressID) =>
//             from address in db.Addresses
//             where address.CertPolicyId == addressID
//             select address
//             );
//         
//         public static DirectDbContext GetDB(this Table<Address> table)
//         {
//             return (DirectDbContext)table.Context;
//         }
//
//         public static int GetCount(this Table<Address> table, long domainID)
//         {
//             return (from address in table.GetDB().Addresses
//                     where address.DomainID == domainID
//                     select address.CertPolicyId).Count();
//         }
//         
//         public static Address GetByAgentName(this Table<Address> table, string emailAddress)
//         {
//             return Addresses(table.GetDB(), emailAddress).SingleOrDefault();
//         }
//
//         public static IEnumerable<Address> GetByAgentName(this Table<Address> table, string[] emailAddresses, EntityStatus? status)
//         {
//             if (emailAddresses.IsNullOrEmpty())
//             {
//                 throw new ArgumentException("value was null or empty", "emailAddresses");
//             }
//             //
//             // We cannot precompile this (throws at runtime) because emailAddresses.Length can change at runtime
//             //
//             return from address in table.GetDB().Addresses
//                    where emailAddresses.Contains(address.EmailAddress) && address.Status == status
//                    select address;
//         }
//
//         public static IEnumerable<Address> GetByAgentName(this Table<Address> table, string[] emailAddresses)
//         {
//             if (emailAddresses.IsNullOrEmpty())
//             {
//                 throw new ArgumentException("value was null or empty", "emailAddresses");
//             }
//             //
//             // We cannot precompile this (throws at runtime) because emailAddresses.Length can change at runtime
//             //
//             return from address in table.GetDB().Addresses
//                    where emailAddresses.Contains(address.EmailAddress)
//                    select address;
//         }
//
//         public static IQueryable<Address> GetByAgentName(this Table<Address> table, long domainID, long lastAddressID, int maxResults)
//         {
//             return DomainAddresses(table.GetDB(), domainID, lastAddressID, maxResults);
//         }
//
//         public static IQueryable<Address> GetByAgentName(this Table<Address> table, long lastAddressID, int maxResults)
//         {
//             return AllAddresses(table.GetDB(), lastAddressID, maxResults);
//         }
//
//         public static IQueryable<Address> GetByAgentName(this Table<Address> table, long[] ids)
//         {
//             //
//             // We cannot precompile this (throws at runtime) because ids.Length can change at runtime
//             //
//             return from address in table.GetDB().Addresses
//                    where ids.Contains(address.CertPolicyId)
//                    select address;
//         }
//
//         public static IQueryable<Address> GetByAgentName(this Table<Address> table, long[] ids, EntityStatus status)
//         {
//             //
//             // We cannot precompile this (throws at runtime) because ids.Length can change at runtime
//             //
//             return from address in table.GetDB().Addresses
//                    where ids.Contains(address.CertPolicyId) && address.Status == status
//                    select address;
//         }
//         
//         public static Address GetByAgentName(this Table<Address> table, long addressID)
//         {
//             return IDToAddress(table.GetDB(), addressID).FirstOrDefault();
//         }
//         
//         public static IEnumerable<Address> ExecGetByDomainName(this Table<Address> table, string domainName, int maxResults)
//         {
//             return table.GetDB().ExecuteQuery<Address>(Sql_SelectByDomainName, maxResults, domainName);
//         }
//        
//         public static void ExecDelete(this Table<Address> table, string emailAddress)
//         {
//             table.Context.ExecuteCommand(Sql_DeleteAddress, emailAddress);
//         }
//
//         public static void ExecDeleteDomain(this Table<Address> table, long domainID)
//         {
//             table.Context.ExecuteCommand(Sql_DeleteAddressByDomain, domainID);
//         }
//
//         public static void ExecTruncate(this Table<Address> table)
//         {
//             table.Context.ExecuteCommand(Sql_TruncateAddresses);
//         }
//         
//         public static void ExecSetStatus(this Table<Address> table, string[] emailAddresses, EntityStatus status)
//         {
//             string addressesIn = emailAddresses.ToIn();
//             table.Context.ExecuteCommand(Sql_SetStatus, status, DateTimeHelper.Now, addressesIn);
//         }
//
//         public static void ExecSetStatus(this Table<Address> table, long domainID, EntityStatus status)
//         {
//             table.Context.ExecuteCommand(Sql_SetStatusByDomain, status, DateTimeHelper.Now, domainID);
//         }
//
//         public static IEnumerable<Address> ExecGet(this Table<Address> table, long domainID, string lastAddress, int maxResults)
//         {
//             if (string.IsNullOrEmpty(lastAddress))
//             {
//                 return table.GetDB().ExecuteQuery<Address>(Sql_EnumDomainAddressFirst, maxResults, domainID);
//             }
//
//             return table.GetDB().ExecuteQuery<Address>(Sql_EnumDomainAddressNext, maxResults, domainID, lastAddress);
//         }
//
//         public static IEnumerable<Address> ExecGet(this Table<Address> table, string lastAddress, int maxResults)
//         {
//             if (string.IsNullOrEmpty(lastAddress))
//             {
//                 return table.GetDB().ExecuteQuery<Address>(Sql_EnumAddressFirst, maxResults);
//             }
//
//             return table.GetDB().ExecuteQuery<Address>(Sql_EnumAddressNext, maxResults, lastAddress);
//         }
//     }
// }