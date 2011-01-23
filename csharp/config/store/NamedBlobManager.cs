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
using System.Text;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Collections;

namespace Health.Direct.Config.Store
{
    /// <summary>
    /// Simple Blob Store
    /// </summary>
    public class NamedBlobManager : IEnumerable<NamedBlob>
    {
        readonly ConfigStore m_store;

        internal NamedBlobManager(ConfigStore store)
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
        
        public void Add(NamedBlob blob)        
        {
            if (blob == null)
            {
                throw new ArgumentNullException("blob");
            }
            
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, blob);
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db, NamedBlob blob)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (blob == null)
            {
                throw new ArgumentNullException("blob");
            }
            db.Blobs.InsertOnSubmit(blob);
        }
        
        public NamedBlob Get(string name)
        {
            using(ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, name);
            }
        }
        
        public NamedBlob Get(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("name");
            }
            
            return db.Blobs.Get(name).SingleOrDefault();
        }
                        
        public NamedBlob[] GetNameStartsWith(string name)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return GetNameStartsWith(db, name).ToArray();
            }
        }

        public IEnumerable<NamedBlob> GetNameStartsWith(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("name");
            }

            return db.Blobs.GetNameStartsWith(name);
        }
        
        public void Update(NamedBlob blob)
        {
            if (blob == null)
            {
                throw new ArgumentNullException("blob");
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                Update(db, blob);
                db.SubmitChanges();
            }
        }
        
        protected void Update(ConfigDatabase db, NamedBlob blob)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (blob == null)
            {
                throw new ArgumentNullException("blob");
            }
            
            NamedBlob updated = new NamedBlob();
            updated.CopyFixed(blob);
            db.Blobs.Attach(updated);
            updated.ApplyChanges(blob);
        }

        public void Update(string name, object item)
        {
            if (item == null)
            {
                throw new ArgumentNullException("item");
            }
            
            this.Update(new NamedBlob(name, item));
        }

        protected void Update(ConfigDatabase db, string name, object item)
        {
            if (item == null)
            {
                throw new ArgumentNullException("item");
            }
            
            
            this.Update(db, new NamedBlob(name, item));
        }

        public void Remove(string name)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, name);
            }
        }

        public void Remove(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            db.Blobs.ExecDelete(name);
        }
        
        public string[] ListNamesStartWith(string prefix)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.ListNamesStartWith(db, prefix).ToArray();
            }            
        }
                
        public IEnumerable<string> ListNamesStartWith(ConfigDatabase db, string prefix)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (string.IsNullOrEmpty(prefix))
            {
                throw new ArgumentException("prefix");
            }

            return db.Blobs.EnumerateNamesStartsWith(prefix);
        }

        public bool Contains(string blobName)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Contains(db, blobName);
            }
        }
        
        public bool Contains(ConfigDatabase db, string blobName)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            return db.Blobs.ContainsBlob(blobName);
        }

        public IEnumerator<NamedBlob> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (NamedBlob blob in db.Blobs)
                {
                    yield return blob;
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
