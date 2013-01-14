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

using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    public class BundleManager
    {
        ConfigStore m_store;
        
        internal BundleManager(ConfigStore store)
        {
            m_store = store;
        }

        internal ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }
        
        
        public Bundle Add(Bundle bundle)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, bundle);
                db.SubmitChanges();
                return bundle;
            }
        }

        public void Add(IEnumerable<Bundle> bundles)
        {
            if (bundles == null)
            {
                throw new ArgumentNullException("bundles");
            }
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(Bundle bundle in bundles)
                {
                    this.Add(db, bundle);
                }
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db, Bundle bundle)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (bundle == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidBundle);
            }
            
            db.Bundles.InsertOnSubmit(bundle);
        }

        public Bundle[] Get(long[] bundleIDs)
        {
            if (bundleIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.Bundles.Get(bundleIDs).ToArray();
            }
        }

        public Bundle[] Get(long lastBundleID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastBundleID, maxResults).ToArray();
            }
        }

        public IEnumerable<Bundle> Get(ConfigDatabase db, long lastBundleID, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Bundles.Get(lastBundleID, maxResults);
        }

        public Bundle[] Get(string owner)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Get(db, owner).ToArray();
            }
        }

        public IEnumerable<Bundle> Get(ConfigDatabase db, string owner)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            
            return db.Bundles.Get(owner);
        }

        public Bundle[] GetIncoming(string ownerName)
        {
            return this.GetIncoming(ownerName, null);
        }

        public Bundle[] GetIncoming(string ownerName, EntityStatus? status)
        {
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                IEnumerable<Bundle> matches;
                if (status == null)
                {
                    matches = db.Bundles.GetIncoming(ownerName);
                }
                else
                {
                    matches = db.Bundles.GetIncoming(ownerName, status.Value);
                }
                
                return matches.ToArray();
            }
        }

        public Bundle[] GetOutgoing(string ownerName)
        {
            return this.GetOutgoing(ownerName, null);
        }

        public Bundle[] GetOutgoing(string ownerName, EntityStatus? status)
        {
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                IEnumerable<Bundle> matches;
                
                if (status == null)
                {
                    matches = db.Bundles.GetOutgoing(ownerName);
                }
                else
                {
                    matches = db.Bundles.GetOutgoing(ownerName, status.Value);
                }
                
                return matches.ToArray();                    
            }
        }

        public void SetStatus(ConfigDatabase db, long BundleID, EntityStatus status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            db.Bundles.ExecUpdateStatus(BundleID, status);
        }

        public void SetStatus(string owner, EntityStatus status)
        {
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.SetStatus(db, owner, status);
            }
        }

        public void SetStatus(long[] bundleIDs, EntityStatus status)
        {
            if (bundleIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                //
                // Todo: optimize this by using an 'in' query.. 
                //
                for (int i = 0; i < bundleIDs.Length; ++i)
                {
                    this.SetStatus(db, bundleIDs[i], status);
                }
                //db.SubmitChanges(); // Not needed, since we do a direct update
            }
        }

        public void SetStatus(ConfigDatabase db, string owner, EntityStatus status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.Bundles.ExecUpdateStatus(owner, status);
        }

        public void Remove(long[] bundleIDs)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, bundleIDs);

                // We don't commit, because we execute deletes directly
            }
        }

        public void Remove(ConfigDatabase db, long[] bundleIDs)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (bundleIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }
            //
            // Todo: this in a single query
            //
            for (int i = 0; i < bundleIDs.Length; ++i)
            {
                db.Bundles.ExecDelete(bundleIDs[i]);
            }
        }

        public void Remove(string ownerName)
        {
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, ownerName);
            }
        }

        public void Remove(ConfigDatabase db, string ownerName)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            db.Bundles.ExecDelete(ownerName);
        }


        public void RemoveAll(ConfigDatabase db)
        {
            db.Bundles.ExecTruncate();
        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                RemoveAll(db);
            }
        }
    }
}