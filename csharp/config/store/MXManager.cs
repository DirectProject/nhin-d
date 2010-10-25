/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
  
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
    public class MXManager : IEnumerable<MX>
    {
        ConfigStore m_store;

        internal MXManager(ConfigStore store)
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

        public void Add(long domainID
                        , string SMTPName)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db
                         , domainID
                         , SMTPName
                         , 0);
                db.SubmitChanges();
            }
        }


        public void Add(long domainID
                        , string SMTPName
                        , int preference)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db
                         , domainID
                         , SMTPName
                         , preference);
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db
                        , long domainID
                        , string SMTPName
                        , int preferece)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.MXs.InsertOnSubmit(new MX(domainID
                                         , SMTPName
                                         , preferece));
        }

        public void Add(MX mx)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, mx);
                db.SubmitChanges();
            }
        }

        public void Add(ConfigDatabase db, MX mx)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (mx == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidMX);
            }
            
           
            db.MXs.InsertOnSubmit(mx);
        }
        
        public int Count(long domainID)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.MXs.GetCount(domainID);
            }            
        }
                
        public MX Get(string smtpName)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, smtpName);
            }
        }

        public MX Get(ConfigDatabase db
                      , string smtpName)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(smtpName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
            }

            return db.MXs.Get(smtpName);
        }

        public MX[] Get(string[] smtpNames)
        {
            return this.Get(smtpNames, null);
        }
                
        public IEnumerable<MX> Get(ConfigDatabase db
                                   , string[] smtpNames)
        {
            return this.Get(db, smtpNames, null);
        }

        public MX[] Get(string[] smtpNames, int? preference)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, smtpNames, preference).ToArray();
            }
        }

        public IEnumerable<MX> Get(ConfigDatabase db, string[] smtpNames, int? preference)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (smtpNames.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
            }

            if (preference == null)
            {
                return db.MXs.Get(smtpNames);
            }

            return db.MXs.Get(smtpNames, preference.Value);
        }

        public MX[] Get(string lastDomain
                        , int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db
                                , lastDomain
                                , maxResults).ToArray();
            }
        }

        public IEnumerable<MX> Get(ConfigDatabase db
                                   , string lastDomain
                                   , int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.MXs.ExecGet(lastDomain, maxResults);
        }

        public void Update(MX mx)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Update(db, mx);
                db.SubmitChanges();
            }
        }

        public void Update(IEnumerable<MX> mxs)
        {
            if (mxs == null)
            {
                throw new ArgumentNullException("mxs");
            }
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (MX mx in mxs)
                {
                    this.Update(db, mx);
                }
                db.SubmitChanges();
            }
        }

        void Update(ConfigDatabase db, MX mx)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (mx == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
            }
            if (!mx.IsValidEmailDomain())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
            }

            MX update = new MX();
            update.CopyFixed(mx);

            db.MXs.Attach(update);
            update.ApplyChanges(mx);
        }

        public void Remove(string smtpName)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, smtpName);
            }
        }

        public void Remove(ConfigDatabase db, string smtpName)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (string.IsNullOrEmpty(smtpName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
            }

            db.MXs.ExecDelete(smtpName);
        }


        public void RemoveDomain(long domainID)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.RemoveDomain(db, domainID);
            }
        }

        public void RemoveDomain(ConfigDatabase db, long domainID)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            db.MXs.ExecDeleteDomain(domainID);
        }

        public void RemoveAll(ConfigDatabase db)
        {
            db.MXs.ExecTruncate();

        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.RemoveAll(db);
            }
        }

        public IEnumerator<MX> GetEnumerator()
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(MX mx in db.MXs)
                {
                    yield return mx;
                }       
            }
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    
    }
}