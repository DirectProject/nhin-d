/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
  
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
    public static class BundleQueries
    {
        const string Sql_DeleteBundleByOwner = "DELETE from Bundles where Owner = {0}";
        const string Sql_DeleteBundleByID = "DELETE from Bundles where BundleID = {0}";
        const string Sql_UpdateStatus = "UPDATE Bundles Set Status = {0} where BundleID = {1}";
        const string Sql_UpdateStatusByOwner = "UPDATE Bundles Set Status = {0} where Owner = {1}";
        const string Sql_TruncateBundles = "truncate table Bundles";

        static readonly Func<ConfigDatabase, long, IQueryable<Bundle>> BundleByID = CompiledQuery.Compile(
            (ConfigDatabase db, long id) =>
            from b in db.Bundles
            where b.ID == id
            select b
            );

        static readonly Func<ConfigDatabase, string, IQueryable<Bundle>> BundlesByOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
            from b in db.Bundles
            where b.Owner == owner
            select b
            );
        
        static readonly Func<ConfigDatabase, long, int, IQueryable<Bundle>> AllBundles = CompiledQuery.Compile(
            (ConfigDatabase db, long lastBundleID, int maxResults) =>
            (from b in db.Bundles
             where b.ID > lastBundleID
             orderby b.ID
             select b).Take(maxResults)
            );

        static readonly Func<ConfigDatabase, string, IQueryable<Bundle>> AllIncomingBundlesForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
            from b in db.Bundles
            where b.Owner == owner && b.ForIncoming == true
            select b
            );

        static readonly Func<ConfigDatabase, string, IQueryable<Bundle>> AllOutgoingBundlesForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner) =>
            from b in db.Bundles
            where b.Owner == owner && b.ForOutgoing == true
            select b
            );

        static readonly Func<ConfigDatabase, string, EntityStatus, IQueryable<Bundle>> IncomingBundlesForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, EntityStatus status) =>
            from b in db.Bundles
            where b.Owner == owner && b.ForIncoming == true && b.Status == status
            select b
            );

        static readonly Func<ConfigDatabase, string, EntityStatus, IQueryable<Bundle>> OutgoingBundlesForOwner = CompiledQuery.Compile(
            (ConfigDatabase db, string owner, EntityStatus status) =>
            from b in db.Bundles
            where b.Owner == owner && b.ForOutgoing == true && b.Status == status
            select b
            );
        
        static readonly Func<ConfigDatabase, IQueryable<Bundle>> AllIncomingBundles = CompiledQuery.Compile(
            (ConfigDatabase db) =>
            from b in db.Bundles
            where b.ForIncoming == true
            select b
            );
        static readonly Func<ConfigDatabase, IQueryable<Bundle>> AllOutgoingBundles = CompiledQuery.Compile(
            (ConfigDatabase db) =>
            from b in db.Bundles
            where b.ForOutgoing == true
            select b
            );

        public static ConfigDatabase GetDB(this Table<Bundle> table)
        {
            return (ConfigDatabase)table.Context;
        }
    
        public static Bundle Get(this Table<Bundle> table, long bundleID)
        {
            return BundleByID(table.GetDB(), bundleID).SingleOrDefault();
        }

        public static IEnumerable<Bundle> Get(this Table<Bundle> table, long[] bundleIDs)
        {
            return from b in table.GetDB().Bundles
                   where bundleIDs.Contains(b.ID)
                   select b;
        }
        
        public static IQueryable<Bundle> Get(this Table<Bundle> table, long lastBundleID, int maxResults)
        {
            return AllBundles(table.GetDB(), lastBundleID, maxResults);
        }

        public static IQueryable<Bundle> Get(this Table<Bundle> table, string owner)
        {
            return BundlesByOwner(table.GetDB(), owner);
        }

        public static IEnumerable<Bundle> GetIncoming(this Table<Bundle> table, string owner)
        {
            return AllIncomingBundlesForOwner(table.GetDB(), owner);
        }

        public static IEnumerable<Bundle> GetIncoming(this Table<Bundle> table, string owner, EntityStatus status)
        {
            return IncomingBundlesForOwner(table.GetDB(), owner, status);
        }

        public static IEnumerable<Bundle> GetAllIncoming(this Table<Bundle> table)
        {
            return AllIncomingBundles(table.GetDB());
        }

        public static IEnumerable<Bundle> GetOutgoing(this Table<Bundle> table, string owner)
        {
            return AllOutgoingBundlesForOwner(table.GetDB(), owner);
        }

        public static IEnumerable<Bundle> GetOutgoing(this Table<Bundle> table, string owner, EntityStatus status)
        {
            return OutgoingBundlesForOwner(table.GetDB(), owner, status);
        }

        public static IEnumerable<Bundle> GetAllOutgoing(this Table<Bundle> table)
        {
            return AllOutgoingBundles(table.GetDB());
        }
                
        public static void ExecDelete(this Table<Bundle> table, string owner)
        {
            table.Context.ExecuteCommand(Sql_DeleteBundleByOwner, owner);
        }

        public static void ExecDelete(this Table<Bundle> table, long bundleID)
        {
            table.Context.ExecuteCommand(Sql_DeleteBundleByID, bundleID);
        }

        public static void ExecTruncate(this Table<Bundle> table)
        {
            table.Context.ExecuteCommand(Sql_TruncateBundles);
        }

        public static void ExecUpdateStatus(this Table<Bundle> table, string owner, EntityStatus status)
        {
            table.Context.ExecuteCommand(Sql_UpdateStatusByOwner, status, owner);
        }

        public static void ExecUpdateStatus(this Table<Bundle> table, long bundleID, EntityStatus status)
        {
            table.Context.ExecuteCommand(Sql_UpdateStatus, status, bundleID);
        }
    }
}