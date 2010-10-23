/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
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
    /// <summary>
    /// Queries that hit the Certificates table
    /// </summary>
    public static class CertificateQueries
    {
        const string Sql_DeleteCert = "DELETE from Certificates where CertificateID = {0}";
        const string Sql_DeleteCertByOwner = "DELETE from Certificates where Owner = {0}";
        
        const string Sql_UpdateStatus = "UPDATE Certificates Set Status = {0} where CertificateID = {1}";
        const string Sql_UpdateStatusByOwner = "UPDATE Certificates Set Status = {0} where Owner = {1}";
        
        const string Sql_TruncateCerts =  "truncate table Certificates";
        //const string Sql_AllCertsByID = "SELECT * from Certificates where CertificateID in ({0})";
        
        static readonly Func<ConfigDatabase, long, IQueryable<Certificate>> CertByID = CompiledQuery.Compile(
            (ConfigDatabase db, long id) =>
            from cert in db.Certificates
            where cert.ID == id
            select cert
            );
        
        static readonly Func<ConfigDatabase, string, IQueryable<Certificate>> AllCertsByOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
            from cert in db.Certificates
            where cert.Owner == owner
            select cert
            );

        static readonly Func<ConfigDatabase, string, EntityStatus, IQueryable<Certificate>> CertsByOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, EntityStatus status) =>
            from cert in db.Certificates
            where cert.Owner == owner && cert.Status == status
            select cert
            );

        static readonly Func<ConfigDatabase, long, int, IQueryable<Certificate>> AllCerts = CompiledQuery.Compile(
            (ConfigDatabase db, long lastCertID, int maxResults) =>
            (from cert in db.Certificates
             where cert.ID > lastCertID
             orderby cert.ID
             select cert).Take(maxResults)
            );

        static readonly Func<ConfigDatabase, string, string, IQueryable<Certificate>> CertsByThumbprint = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, string thumbprint) =>
            from cert in db.Certificates
            where cert.Owner == owner && cert.Thumbprint == thumbprint
            select cert
            );

        public static ConfigDatabase GetDB(this Table<Certificate> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static Certificate Get(this Table<Certificate> table, long certID)
        {
            return CertByID(table.GetDB(), certID).SingleOrDefault();
        }

        public static IEnumerable<Certificate> Get(this Table<Certificate> table, long[] certIDs)
        {
            //return table.GetDB().ExecuteQuery<Certificate>(Sql_AllCertsByID, certIDs.ToIn());
            return from cert in table.GetDB().Certificates
                   where certIDs.Contains(cert.ID)
                   select cert;
        }

        public static IQueryable<Certificate> Get(this Table<Certificate> table, long lastCertID, int maxResults)
        {
            return AllCerts(table.GetDB(), lastCertID, maxResults);
        }

        public static IQueryable<Certificate> Get(this Table<Certificate> table, string owner)
        {
            return AllCertsByOwner(table.GetDB(), owner);
        }

        public static IQueryable<Certificate> Get(this Table<Certificate> table, string owner, EntityStatus status)
        {
            return CertsByOwner(table.GetDB(), owner, status);
        }

        public static Certificate Get(this Table<Certificate> table, string owner, string thumbprint)
        {
            return CertsByThumbprint(table.GetDB(), owner, thumbprint).SingleOrDefault();
        }

        public static void ExecDelete(this Table<Certificate> table, string owner)
        {
            table.Context.ExecuteCommand(Sql_DeleteCertByOwner, owner);
        }

        public static void ExecDelete(this Table<Certificate> table, long certificateID)
        {
            table.Context.ExecuteCommand(Sql_DeleteCert, certificateID);
        }

        public static void ExecTruncate(this Table<Certificate> table)
        {
            table.Context.ExecuteCommand(Sql_TruncateCerts);
        }

        public static void ExecUpdateStatus(this Table<Certificate> table, long certificateID, EntityStatus status)
        {
            table.Context.ExecuteCommand(Sql_UpdateStatus, status, certificateID);
        }

        public static void ExecUpdateStatus(this Table<Certificate> table, string owner, EntityStatus status)
        {
            table.Context.ExecuteCommand(Sql_UpdateStatusByOwner, status, owner);
        }
    }
}