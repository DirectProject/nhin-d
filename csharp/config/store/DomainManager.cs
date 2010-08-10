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

namespace NHINDirect.ConfigStore
{
    public class DomainManager : IEnumerable<Domain>
    {
        ConfigStore m_store;

        internal DomainManager(ConfigStore store)
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
        
        public void Add(string name)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, name);
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db, string name)
        {
            if (db == null || string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }
            
            db.Domains.InsertOnSubmit(new Domain(name));
        }
        
        public void Ensure(string name)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                if (!this.Contains(db, name))
                {
                    this.Add(db, name);
                    db.SubmitChanges();
                }
            }
        }

        public Domain Get(string name)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Get(db, name);
            }
        }

        public Domain Get(ConfigDatabase db, string name)
        {
            if (db == null || string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }

            return db.Domains.Find(name);
        }

        public bool Contains(string name)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Contains(db, name);
            }        
        }
        
        public bool Contains(ConfigDatabase db, string name)
        {
            if (db == null || string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }
            
            return (db.Domains.Find(name) != null);
        }
        
        public void Remove(string name)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, name);
            }
        }

        public void Remove(ConfigDatabase db, string name)
        {
            if (db == null || string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }
            
            db.Domains.Delete(name);
        }
        
        public AddressManager GetAddressManager(long domainID)
        {
            //
            // TODO: Cache these in an LRU memorycache
            //
            return new AddressManager(this.Store, domainID);
        }

        public IEnumerator<Domain> GetEnumerator()
        {
           using(ConfigDatabase db = this.Store.CreateContext())
           {
                foreach(Domain domain in db.Domains)
                {
                    yield return domain;
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
