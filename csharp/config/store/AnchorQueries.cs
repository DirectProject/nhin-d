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
    public static class AnchorQueries
    {
        const string Sql_DeleteAnchorByOwner = "DELETE from Anchors where Owner = {0}";
        const string Sql_DeleteAnchorByThumbprint = "DELETE from Anchors where Owner = {0} and Thumbprint = {1}";
        const string Sql_DeleteAnchorByID = "DELETE from Anchors where CertificateID = {0}";
        const string Sql_UpdateStatus = "UPDATE Anchors Set Status = {0} where CertificateID = {1}";
        const string Sql_UpdateStatusByOwner = "UPDATE Anchors Set Status = {0} where Owner = {1}";
        const string Sql_TruncateAnchors = "truncate table Anchors";

        static readonly Func<ConfigDatabase, long, IQueryable<Anchor>> AnchorByID = CompiledQuery.Compile(
            (ConfigDatabase db, long id) =>
            from anchor in db.Anchors
            where anchor.ID == id
            select anchor
            );

        static readonly Func<ConfigDatabase, string, IQueryable<Anchor>> AnchorsByOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
            from anchor in db.Anchors
            where anchor.Owner == owner
            select anchor
            );
        
        static readonly Func<ConfigDatabase, long, int, IQueryable<Anchor>> AllAnchors = CompiledQuery.Compile(
            (ConfigDatabase db, long lastCertID, int maxResults) =>
            (from anchor in db.Anchors
             where anchor.ID > lastCertID
             orderby anchor.ID
             select anchor).Take(maxResults)
            );

        static readonly Func<ConfigDatabase, string, string, IQueryable<Anchor>> AnchorsByThumbprint = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, string thumbprint) =>
            from anchor in db.Anchors
            where anchor.Owner == owner && anchor.Thumbprint == thumbprint
            select anchor
            );

        static readonly Func<ConfigDatabase, string, IQueryable<Anchor>> AllIncomingAnchorsForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
            from anchor in db.Anchors
            where anchor.Owner == owner && anchor.ForIncoming == true
            select anchor
            );
        static readonly Func<ConfigDatabase, string, IQueryable<Anchor>> AllOutgoingAnchorsForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
            from anchor in db.Anchors
            where anchor.Owner == owner && anchor.ForOutgoing == true
            select anchor
            );

        static readonly Func<ConfigDatabase, string, EntityStatus, IQueryable<Anchor>> IncomingAnchorsForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, EntityStatus status) =>
            from anchor in db.Anchors
            where anchor.Owner == owner && anchor.ForIncoming == true && anchor.Status == status
            select anchor
            );
        static readonly Func<ConfigDatabase, string, EntityStatus, IQueryable<Anchor>> OutgoingAnchorsForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, EntityStatus status) =>
            from anchor in db.Anchors
            where anchor.Owner == owner && anchor.ForOutgoing == true && anchor.Status == status
            select anchor
            );
        
        static readonly Func<ConfigDatabase, IQueryable<Anchor>> AllIncomingAnchors = CompiledQuery.Compile(
            (ConfigDatabase db) =>
            from anchor in db.Anchors
            where anchor.ForIncoming == true
            select anchor
            );
        static readonly Func<ConfigDatabase, IQueryable<Anchor>> AllOutgoingAnchors = CompiledQuery.Compile(
            (ConfigDatabase db) =>
            from anchor in db.Anchors
            where anchor.ForOutgoing == true
            select anchor
            );

        public static ConfigDatabase GetDB(this Table<Anchor> table)
        {
            return (ConfigDatabase)table.Context;
        }
    
        public static Anchor Get(this Table<Anchor> table, long certID)
        {
            return AnchorByID(table.GetDB(), certID).SingleOrDefault();
        }

        public static IEnumerable<Anchor> Get(this Table<Anchor> table, long[] certIDs)
        {
            return from anchor in table.GetDB().Anchors
                   where certIDs.Contains(anchor.ID)
                   select anchor;
        }
        
        public static IQueryable<Anchor> Get(this Table<Anchor> table, long lastCertID, int maxResults)
        {
            return AllAnchors(table.GetDB(), lastCertID, maxResults);
        }

        public static IQueryable<Anchor> Get(this Table<Anchor> table, string owner)
        {
            return AnchorsByOwner(table.GetDB(), owner);
        }

        public static Anchor Get(this Table<Anchor> table, string owner, string thumbprint)
        {
            return AnchorsByThumbprint(table.GetDB(), owner, thumbprint).SingleOrDefault();
        }

        public static IEnumerable<Anchor> GetIncoming(this Table<Anchor> table, string owner)
        {
            return AllIncomingAnchorsForOwner(table.GetDB(), owner);
        }

        public static IEnumerable<Anchor> GetIncoming(this Table<Anchor> table, string owner, EntityStatus status)
        {
            return IncomingAnchorsForOwner(table.GetDB(), owner, status);
        }

        public static IEnumerable<Anchor> GetAllIncoming(this Table<Anchor> table)
        {
            return AllIncomingAnchors(table.GetDB());
        }

        public static IEnumerable<Anchor> GetOutgoing(this Table<Anchor> table, string owner)
        {
            return AllOutgoingAnchorsForOwner(table.GetDB(), owner);
        }

        public static IEnumerable<Anchor> GetOutgoing(this Table<Anchor> table, string owner, EntityStatus status)
        {
            return OutgoingAnchorsForOwner(table.GetDB(), owner, status);
        }

        public static IEnumerable<Anchor> GetAllOutgoing(this Table<Anchor> table)
        {
            return AllOutgoingAnchors(table.GetDB());
        }
                
        public static void ExecDelete(this Table<Anchor> table, string owner)
        {
            table.Context.ExecuteCommand(Sql_DeleteAnchorByOwner, owner);
        }

        public static void ExecDelete(this Table<Anchor> table, string owner, string thumbprint)
        {
            table.Context.ExecuteCommand(Sql_DeleteAnchorByThumbprint, owner, thumbprint);
        }

        public static void ExecDelete(this Table<Anchor> table, long certificateID)
        {
            table.Context.ExecuteCommand(Sql_DeleteAnchorByID, certificateID);
        }

        public static void ExecTruncate(this Table<Anchor> table)
        {
            table.Context.ExecuteCommand(Sql_TruncateAnchors);
        }

        public static void ExecUpdateStatus(this Table<Anchor> table, string owner, EntityStatus status)
        {
            table.Context.ExecuteCommand(Sql_UpdateStatusByOwner, status, owner);
        }

        public static void ExecUpdateStatus(this Table<Anchor> table, long anchorID, EntityStatus status)
        {
            table.Context.ExecuteCommand(Sql_UpdateStatus, status, anchorID);
        }
    }
}