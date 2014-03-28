/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico      chris.lomonico@surescripts.com
  
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
    public class DnsRecordManager 
    {
        readonly ConfigStore m_store;

        internal DnsRecordManager(ConfigStore store)
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

        public void Add(DnsRecord record)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, record);
                db.SubmitChanges();
            }    
        }

        public void Add(ConfigDatabase db, DnsRecord record)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (record == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDnsRecord);
            }
            db.DnsRecords.InsertOnSubmit(record);
        }
        
        public void Add(DnsRecord[] dnsRecords)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, dnsRecords);
                db.SubmitChanges();
            }
        }

        public void Add(ConfigDatabase db
            , DnsRecord[] dnsRecords)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (dnsRecords == null || dnsRecords.Length.Equals(0))
            {
                return;
            }
            foreach (DnsRecord dnsRecord in dnsRecords)
            {
                db.DnsRecords.InsertOnSubmit(dnsRecord);
            }
        }

        public DnsRecord Get(long recordID)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, recordID);
            }
        }

        public DnsRecord Get(ConfigDatabase db, long recordID)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.DnsRecords.Get(recordID);
        }

        public DnsRecord[] Get(long[] recordIDs)
        {
            if (recordIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
               return db.DnsRecords.Get(recordIDs).ToArray();
               
            }
        }


        public DnsRecord[] Get(string domainName)
        {
            if (string.IsNullOrEmpty(domainName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db
                    , domainName).ToArray();

            }
        }


        public DnsRecord[] Get(ConfigDatabase db
            , string domainName)
        {
            return db.DnsRecords.Get(domainName
                , null).ToArray();
        }

        public DnsRecord[] Get(string domainName
            , Common.DnsResolver.DnsStandard.RecordType typeID)
        {
            if (string.IsNullOrEmpty(domainName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
            }
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db
                    , domainName
                    , typeID).ToArray();

            }
        }


        public DnsRecord[] Get(ConfigDatabase db
            , string domainName
            , Common.DnsResolver.DnsStandard.RecordType typeID)
        {
            return db.DnsRecords.Get(domainName
                , (int)typeID).ToArray();
        }

        public DnsRecord[] Get(long lastRecordID
            , int maxResults
            , Common.DnsResolver.DnsStandard.RecordType typeID)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db
                    , lastRecordID
                    , maxResults
                    , typeID).ToArray();
            }
        }
        
        public IEnumerable<DnsRecord> Get(ConfigDatabase db
            , long lastRecordID
            , int maxResults
            , Common.DnsResolver.DnsStandard.RecordType typeID)
        {
            return db.DnsRecords.Get(lastRecordID
                , maxResults
                , (int)typeID);
        }

        public DnsRecord[] Get(long lastRecordID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastRecordID, maxResults).ToArray();
            }
        }
        
        public IEnumerable<DnsRecord> Get(ConfigDatabase db, long lastRecordID, int maxResults)
        {
            return db.DnsRecords.Get(lastRecordID, maxResults);
        }

        /// <summary>
        /// simple method to remove an dns record by ID 
        /// </summary>
        /// <param name="dnsRecord">DnsRecord instance to be removed</param>
        public void Remove(DnsRecord dnsRecord)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                db.DnsRecords.Attach(dnsRecord);
                this.Remove(db
                    , dnsRecord);
            }
        }

        /// <summary>
        /// simple method to remove an dns record by ID 
        /// </summary>
        /// <param name="db">database context to use</param>
        /// <param name="dnsRecord">DnsRecord instance to be removed</param>
        public void Remove(ConfigDatabase db
            , DnsRecord dnsRecord)
        {
            db.DnsRecords.DeleteOnSubmit(dnsRecord);
        }

        /// <summary>
        /// simple method to remove an dns record by ID 
        /// </summary>
        /// <param name="recordID">long holding the id of the record to be deleted</param>
        public void Remove(long recordID)
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                this.Remove(db
                    , recordID);
            }
        }

        /// <summary>
        /// simple method to remove an dns record by ID 
        /// </summary>
        /// <param name="db">database context to use</param>
        /// <param name="recordID">long holding the id of the record to be deleted</param>
        public void Remove(ConfigDatabase db
            , long recordID)
        {
            db.DnsRecords.ExecDelete(recordID);
        }

        /// <summary>
        /// removes all dnsrecords from the store
        /// </summary>
        public void RemoveAll()
        {
            using (ConfigDatabase db = Store.CreateContext())
            {
                this.RemoveAll(db);
            }
        }

        /// <summary>
        /// removes all dnsrecords from the store
        /// </summary>
        /// <param name="db">ConfigDatabase instance context</param>
        public void RemoveAll(ConfigDatabase db)
        {
            db.DnsRecords.DeleteAll();
        }

        public void Update(DnsRecord dnsRecord)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                Update(db, dnsRecord);
                db.SubmitChanges();
            }
        }

        public void Update(IEnumerable<DnsRecord> dnsRecords)
        {
            if (dnsRecords == null)
            {
                throw new ArgumentNullException("DnsRecords");
            }
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (DnsRecord dnsRecord in dnsRecords)
                {
                    Update(db, dnsRecord);
                }
                db.SubmitChanges();
            }
        }

        public void Update(ConfigDatabase db, DnsRecord dnsRecord)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (dnsRecord == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDnsRecord);
            }

            DnsRecord update = Get(db, dnsRecord.ID);
            update.ApplyChanges(dnsRecord);
        }

        public int Count(Common.DnsResolver.DnsStandard.RecordType? recordType)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.DnsRecords.GetCount((int?)recordType.Value);
            }
        }
    }
}
