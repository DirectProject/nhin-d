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
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace Health.Direct.Config.Store
{
    public class MdnManager : IEnumerable<Mdn>
    {
        const int DEFAULT_PROCESSED_TIMEOUT_MINUTES = 10;
        const int DEFAULT_DISPATCHED_TIMEOUT_MINUTES = 10;
        private const int DEFAULT_TIMEOUT_RECORDS = 10;

        readonly ConfigStore m_store;

        

        public MdnManager(ConfigStore store)
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

        public void Start(Mdn mdn)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                Start(db, new []{mdn});
                db.SubmitChanges();
            }
        }

        public void Start(Mdn[] mdns)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                Start(db, mdns);
                db.SubmitChanges();
            }
        }
        
        public void Start(ConfigDatabase db, Mdn[] mdns)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (mdns == null || mdns.Length.Equals(0))
            {
                return;
            }

            foreach (var mdn in mdns)
            {
                db.Mdns.InsertOnSubmit(mdn);
            }
        }

        public void TimeOut(Mdn mdn)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                TimeOut(db, mdn);
                db.SubmitChanges();
            }
        }

        public void TimeOut(Mdn[] mdns)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                foreach (var mdn in mdns)
                {
                    TimeOut(db, mdn);
                }
                db.SubmitChanges();
            }
        }

        public void TimeOut(ConfigDatabase db, Mdn mdn)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (mdn == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidMdn);
            }
            mdn.Status = MdnStatus.TimedOut;
            db.Mdns.InsertOnSubmit(mdn);
        }

        public void Update(Mdn mdn)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                try
                {
                    Update(db, mdn);
                    db.SubmitChanges();
                }
                catch
                {
                    ThrowMdnFaultException(mdn);
                    throw;
                }
            }
        }
               
        public void Update(Mdn[] mdns)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                foreach (var mdn in mdns)
                {
                    Update(db, mdn);
                }
                db.SubmitChanges();
            }
        }
        
        public void Update(ConfigDatabase db, Mdn mdn)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (mdn == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidMdn);
            }

            db.Mdns.InsertOnSubmit(mdn);
           
        }

        private static void ThrowMdnFaultException(Mdn mdn)
        {
           
            if (mdn.Status == null)
            {
                throw new ConfigStoreException(ConfigStoreError.DuplicateMdnStart);
            }

            if (mdn.Status.Equals(MdnStatus.Processed, StringComparison.OrdinalIgnoreCase))
            {
                //
                // Duplicate processed MDN
                //
                throw new ConfigStoreException(ConfigStoreError.DuplicateProcessedMdn);
            }

            if (mdn.Status.Equals(MdnStatus.Failed, StringComparison.OrdinalIgnoreCase))
            {
                //
                // Duplicate failed MDN
                //
                throw new ConfigStoreException(ConfigStoreError.DuplicateFailedMdn);
            }

            if (mdn.Status.Equals(MdnStatus.Dispatched, StringComparison.OrdinalIgnoreCase))
            {
                //
                // Duplicate Dispatched MDN
                //
                throw new ConfigStoreException(ConfigStoreError.DuplicateDispatchedMdn);
            }

            if (mdn.Status.Equals(MdnStatus.Processed, StringComparison.OrdinalIgnoreCase))
            {
                //
                // Dispatched MDN already sent
                //
                throw new ConfigStoreException(ConfigStoreError.MdnPreviouslyProcessed);
            }

        }


        public int Count()
        {
            using (ConfigDatabase db = Store.CreateReadContext())
            {
                return db.Mdns.GetCount();
            }
        }

        public Mdn Get(string mdnIdentifier)
        {
            using (var db = Store.CreateReadContext())
            {
                return Get(db, mdnIdentifier);
            }
        }

        public Mdn Get(ConfigDatabase db, string mdnIdentifier)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(mdnIdentifier))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidMdnIdentifier);
            }

            return db.Mdns.Get(mdnIdentifier);
        }

        public Mdn[] Get(string lastMdn, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastMdn, maxResults).ToArray();
            }
        }

        public IEnumerable<Mdn> Get(ConfigDatabase db, string lastMdn, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Mdns.ExecGet(lastMdn, maxResults);
        }


        public Mdn[] GetTimedOut()
        {
            using (var db = Store.CreateReadContext())
            {
                return db.Mdns.GetTimedOut().ToArray();
            }
        }
        
        public Mdn[] GetExpiredProcessed(TimeSpan expiredLimit, int maxResults)
        {
            using (ConfigDatabase db = Store.CreateReadContext())
            {
                return GetExpiredProcessed(db, expiredLimit, maxResults).ToArray();
            }
        }
        public Mdn[] GetExpiredProcessed(TimeSpan expiredLimit)
        {
            using (ConfigDatabase db = Store.CreateReadContext())
            {
                return GetExpiredProcessed(db, expiredLimit, DEFAULT_TIMEOUT_RECORDS).ToArray();
            }
        }
        public Mdn[] GetExpiredProcessed()
        {
            using (ConfigDatabase db = Store.CreateReadContext())
            {
                return GetExpiredProcessed(db, TimeSpan.FromMinutes(DEFAULT_PROCESSED_TIMEOUT_MINUTES), DEFAULT_TIMEOUT_RECORDS).ToArray();
            }
        }   
        public IEnumerable<Mdn> GetExpiredProcessed(ConfigDatabase db, TimeSpan expiredLimit, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Mdns.GetExpiredProcessed(expiredLimit, maxResults);
        }


        public Mdn[] GetExpiredDispatched(TimeSpan expiredLimit, int maxResults)
        {
            using (ConfigDatabase db = Store.CreateReadContext())
            {
                return GetExpiredDispatched(db, expiredLimit, maxResults).ToArray();
            }
        }
        public Mdn[] GetExpiredDispatched(TimeSpan expiredLimit)
        {
            using (ConfigDatabase db = Store.CreateReadContext())
            {
                return GetExpiredDispatched(db, expiredLimit, DEFAULT_TIMEOUT_RECORDS).ToArray();
            }
        }
        public Mdn[] GetExpiredDispatched()
        {
            using (ConfigDatabase db = Store.CreateReadContext())
            {
                return GetExpiredDispatched(db, TimeSpan.FromMinutes(DEFAULT_DISPATCHED_TIMEOUT_MINUTES), DEFAULT_TIMEOUT_RECORDS).ToArray();
            }
        }
        public IEnumerable<Mdn> GetExpiredDispatched(ConfigDatabase db, TimeSpan expiredLimit, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Mdns.GetExpiredDispatched(expiredLimit, maxResults);
        }

        public void Remove(Mdn mdn)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                Remove(db, mdn);
            }

        }

        public void Remove(ConfigDatabase db, Mdn mdn)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.Mdns.ExecDelete(mdn);
        }

        public void RemoveTimedOut(TimeSpan limitTime, int bulkCount)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                RemoveTimedOut(db, limitTime, bulkCount);
            }

        }

        public void RemoveTimedOut(ConfigDatabase db, TimeSpan limitTime, int bulkCount)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.Mdns.ExecDeleteTimedOut(limitTime, bulkCount);
        }

        public void RemoveDispositions(TimeSpan limitTime, int bulkCount)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                RemoveDispositions(db, limitTime, bulkCount);
            }
        }

        public void RemoveDispositions(ConfigDatabase db, TimeSpan limitTime, int bulkCount)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.Mdns.ExecDeleteDispositions(limitTime, bulkCount);
        }

        public void RemoveAll(ConfigDatabase db)
        {
            db.Mdns.ExecDeleteAll();

        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                RemoveAll(db);
            }
        }


        public IEnumerator<Mdn> GetEnumerator()
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                foreach (Mdn mdn in db.Mdns)
                {
                    yield return mdn;
                }
            }
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }


        
    }
}
