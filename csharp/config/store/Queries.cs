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

namespace NHINDirect.ConfigStore
{
    public static class Queries
    {
        //-------------------
        //
        // Domains Table
        //
        //-------------------    
        const string Sql_DeleteDomain = "DELETE from Domain where DomainName = {0}";
        
        static readonly Func<ConfigDatabase, string, IQueryable<Domain>> DomainsByName = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
                from domain in db.Domains
                where domain.Name == owner
                select domain
        );
        
        public static ConfigDatabase GetDB(this Table<Domain> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static IEnumerable<Domain> Enumerate(this Table<Domain> table, string domainName)
        {
            return Queries.DomainsByName(table.GetDB(), domainName);
        }

        public static Domain Find(this Table<Domain> table, string domainName)
        {
            return table.Enumerate(domainName).SingleOrDefault();
        }
        
        public static void Delete(this Table<Domain> table, string domainName)
        {
            table.Context.ExecuteCommand(Sql_DeleteDomain, domainName);
        }

        //-------------------
        //
        // Addresses Table
        //
        //-------------------    
        const string Sql_DeleteAddressByName = "DELETE from Address where DomainID = {0} and Name = {1}";
        const string Sql_DeleteAddressByDomain = "DELETE from Address where DomainID = {0}";
        
        static readonly Func<ConfigDatabase, long, IQueryable<Address>> AddressesByDomain = CompiledQuery.Compile(
            (ConfigDatabase db, long domainID) =>
                from address in db.Addresses
                where address.DomainID == domainID
                select address 
        );

        static readonly Func<ConfigDatabase, long, string, IQueryable<Address>> AddressesByName = CompiledQuery.Compile(
            (ConfigDatabase db, long domainID, string endpointName) =>
                from address in db.Addresses
                where address.DomainID == domainID && address.EndpointName == endpointName
                select address
        );
        
        public static ConfigDatabase GetDB(this Table<Address> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static IEnumerable<Address> Enumerate(this Table<Address> table, long domainID)
        {
            return Queries.AddressesByDomain(table.GetDB(), domainID);
        }

        public static Address Find(this Table<Address> table, long domainID)
        {
            return table.Enumerate(domainID).SingleOrDefault();
        }

        public static Address Find(this Table<Address> table, long domainID, string endpointName)
        {
            return Queries.AddressesByName(table.GetDB(), domainID, endpointName).SingleOrDefault();
        }

        public static void Delete(this Table<Address> table, long domainID)
        {
            table.Context.ExecuteCommand(Sql_DeleteAddressByDomain, domainID);
        }

        public static void Delete(this Table<Address> table, long domainID, string endpointName)
        {
            table.Context.ExecuteCommand(Sql_DeleteAddressByName, domainID, endpointName);
        }
        
        //-------------------
        //
        // Certificates Table
        //
        //-------------------    
        const string Sql_DeleteCertByOwner = "DELETE from Certificates where Owner = {0}";
        const string Sql_DeleteCertByThumbprint = "DELETE from Certificates where Owner = {0} and Thumbprint = {1}";
        
        static readonly Func<ConfigDatabase, string, IQueryable<Certificate>> CertsByOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
                from cert in db.Certificates
                where cert.Owner == owner
                select cert
        );
        static readonly Func<ConfigDatabase, string, string, IQueryable<Certificate>> CertsByThumbprint = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, string thumbprint) =>
                from cert in db.Certificates
                where cert.Owner == owner && cert.Thumbprint == thumbprint
                select cert
        );

        public static ConfigDatabase GetDB(this Table<Certificate> table)
        {
            return (ConfigDatabase) table.Context;
        }
            
        public static IEnumerable<Certificate> Enumerate(this Table<Certificate> table, string owner)
        {
            return Queries.CertsByOwner(table.GetDB(), owner);
        }

        public static IEnumerable<Certificate> Enumerate(this Table<Certificate> table, string owner, string thumbprint)
        {
            return Queries.CertsByThumbprint(table.GetDB(), owner, thumbprint);
        }

        public static Certificate Find(this Table<Certificate> table, string owner, string thumbprint)
        {
            return table.Enumerate(owner, thumbprint).SingleOrDefault();
        }

        public static void ExecDelete(this Table<Certificate> table, string owner)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertByOwner, owner);
        }

        public static void ExecDelete(this Table<Certificate> table, string owner, string thumbprint)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertByThumbprint, owner, thumbprint);
        }
        
        //-------------------
        //
        // Anchors Table
        //
        //-------------------    
        const string Sql_DeleteAnchorByOwner = "DELETE from Anchors where Owner = {0}";
        const string Sql_DeleteAnchorByThumbprint = "DELETE from Anchors where Owner = {0} and Thumbprint = {1}";

        static readonly Func<ConfigDatabase, string, IQueryable<Anchor>> AnchorsByOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
                from anchor in db.Anchors
                where anchor.Owner == owner
                select anchor
        );
        static readonly Func<ConfigDatabase, string, string, IQueryable<Anchor>> AnchorsByThumbprint = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, string thumbprint) =>
                from cert in db.Anchors
                where cert.Owner == owner && cert.Thumbprint == thumbprint
                select cert
        );

        static readonly Func<ConfigDatabase, string, IQueryable<Anchor>> IncomingAnchors = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
                from anchor in db.Anchors
                where anchor.Owner == owner && anchor.ForIncoming == true
                select anchor
        );
        static readonly Func<ConfigDatabase, string, IQueryable<Anchor>> OutgoingAnchors = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
                from anchor in db.Anchors
                where anchor.Owner == owner && anchor.ForOutgoing == true
                select anchor
        );


        public static ConfigDatabase GetDB(this Table<Anchor> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static IEnumerable<Anchor> Enumerate(this Table<Anchor> table, string owner)
        {
            return Queries.AnchorsByOwner(table.GetDB(), owner);
        }

        public static IEnumerable<Anchor> Enumerate(this Table<Anchor> table, string owner, string thumbprint)
        {
            return Queries.AnchorsByThumbprint(table.GetDB(), owner, thumbprint);
        }

        public static Anchor Find(this Table<Anchor> table, string owner, string thumbprint)
        {
            return table.Enumerate(owner, thumbprint).SingleOrDefault();
        }

        public static void ExecDelete(this Table<Anchor> table, string owner)
        {
            table.Context.ExecuteCommand(Sql_DeleteAnchorByOwner, owner);
        }

        public static void ExecDelete(this Table<Anchor> table, string owner, string thumbprint)
        {
            table.Context.ExecuteCommand(Sql_DeleteAnchorByThumbprint, owner, thumbprint);
        }
        
        public static IEnumerable<Anchor> EnumerateIncoming(this Table<Anchor> table, string owner)
        {
            return Queries.IncomingAnchors(table.GetDB(), owner);
        }

        public static IEnumerable<Anchor> EnumerateOutgoing(this Table<Anchor> table, string owner)
        {
            return Queries.OutgoingAnchors(table.GetDB(), owner);
        }
    }
}
