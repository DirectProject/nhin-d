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
    public class AddressManager : IEnumerable<Address>
    {
        ConfigStore m_store;
        long m_domainID;
        
        internal AddressManager(ConfigStore store, long domainID)
        {
            m_store = store;
            m_domainID = domainID;
        }
        
        public long DomainID
        {
            get
            {
                return m_domainID;
            }
        }
        
        internal ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }
        
        public void Add(string endpointName)
        {
            this.Add(endpointName, string.Empty);
        }
        
        public void Add(string endpointName, string displayName)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, endpointName, displayName);
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db, string endpointName, string displayName)
        {
            if (db == null || string.IsNullOrEmpty(endpointName))
            {
                throw new ArgumentException();
            }
            
            db.Addresses.InsertOnSubmit(new Address(m_domainID, endpointName, displayName));
        }
        
        public void Ensure(string endpointName, string displayName)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                if (!this.Contains(db, endpointName))
                {
                    this.Add(db, endpointName, displayName);
                    db.SubmitChanges();
                }
            }
        }        
        
        public Address Get(string endpointName)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Get(db, endpointName);
            }
        }
        
        public Address Get(ConfigDatabase db, string endpointName)
        {
            if (db == null || string.IsNullOrEmpty(endpointName))
            {
                throw new ArgumentException();
            }
            
            return db.Addresses.Find(m_domainID, endpointName);
        }
        
        public bool Contains(string endpointName)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Contains(db, endpointName);
            }
        }

        public bool Contains(ConfigDatabase db, string endpointName)
        {
            if (db == null || string.IsNullOrEmpty(endpointName))
            {
                throw new ArgumentException();
            }
            
            return (db.Addresses.Find(m_domainID, endpointName) != null);
        }
        
        public void Remove(string endpointName)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, endpointName);
            }
        }
        
        public void Remove(ConfigDatabase db, string endpointName)
        {
            if (string.IsNullOrEmpty(endpointName))
            {
                throw new ArgumentException();
            }
            
            db.Addresses.Delete(m_domainID, endpointName);
        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.RemoveAll(db);
            }
        }

        public void RemoveAll(ConfigDatabase db)
        {
            db.Addresses.Delete(m_domainID);
        }

        public IEnumerator<Address> GetEnumerator()
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(Address address in db.Addresses.Enumerate(m_domainID))
                {
                    yield return address;
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
