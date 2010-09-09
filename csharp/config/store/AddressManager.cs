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
using System.Net.Mail;
using NHINDirect;

namespace NHINDirect.Config.Store
{
    public class AddressManager : IEnumerable<Address>
    {
        ConfigStore m_store;
        
        internal AddressManager(ConfigStore store)
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
        
        public void Add(long domainID, string emailAddress)
        {
            this.Add(new Address(domainID, emailAddress));
        }
        
        public void Add(Address address)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, address);
                db.SubmitChanges();
            }
        }
        
        public void Add(IEnumerable<Address> addresses)
        {
            if (addresses == null)
            {
                throw new ArgumentNullException("addresses");
            }
            
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(Address address in addresses)
                {
                    this.Add(db, address);
                }
                
                db.SubmitChanges();
            }            
        }
        
        public void Add(ConfigDatabase db, Address address)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            if (address == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
            }
            
            if (!address.IsValidMailAddress())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
            }
            
            db.Addresses.InsertOnSubmit(address);
        }
        
        public void Update(Address address)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Update(db, address);
                db.SubmitChanges();
            }
        }

        public void Update(IEnumerable<Address> addresses)
        {
            if (addresses == null)
            {
                throw new ArgumentNullException("addresses");
            }
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(Address address in addresses)
                {
                    this.Update(db, address);
                }
                db.SubmitChanges();
            }
        }
        
        public void Update(ConfigDatabase db, Address address)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (address == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
            }
            if (!address.IsValidMailAddress())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
            }
            
            Address update = new Address();
            update.CopyFixed(address);
            
            db.Addresses.Attach(update);
            update.ApplyChanges(address);             
        }
        
        public int Count(long domainID)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.Addresses.GetCount(domainID);
            }            
        }
               
        public Address Get(string emailAddress)
        {
            using(ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, emailAddress);
            }
        }
        
        public Address Get(ConfigDatabase db, string emailAddress)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            return db.Addresses.Get(emailAddress);
        }

        public Address[] Get(string[] emailAddresses)
        {
            return this.Get(emailAddresses, null);
        }

        public IEnumerable<Address> Get(ConfigDatabase db, string[] emailAddresses)
        {
            return this.Get(db, emailAddresses, null);
        }

        public Address[] Get(string[] emailAddresses, EntityStatus? status)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, emailAddresses, status).ToArray();
            }
        }

        public IEnumerable<Address> Get(ConfigDatabase db, string[] emailAddresses, EntityStatus? status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            this.VerifyEmailAddresses(emailAddresses);
            if (status == null)
            {
                return db.Addresses.Get(emailAddresses);
            }
            
            return db.Addresses.Get(emailAddresses, status.Value);
        }

        public Address[] Get(string lastAddressID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastAddressID, maxResults).ToArray();
            }
        }

        public IEnumerable<Address> Get(ConfigDatabase db, string lastAddressID, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Addresses.ExecGet(lastAddressID, maxResults);
        }

        public Address[] Get(long domainID, string lastAddressID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, domainID, lastAddressID, maxResults).ToArray();
            }
        }

        public IEnumerable<Address> Get(ConfigDatabase db, long domainID, string lastAddressID, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Addresses.ExecGet(domainID, lastAddressID, maxResults);
        }
                
        public Address[] Get(long[] addressIDs)
        {
            return this.Get(addressIDs, null);
        }

        public IEnumerable<Address> Get(ConfigDatabase db, long[] addressIDs)
        {
            return this.Get(db, addressIDs, null);
        }

        public Address[] Get(long[] addressIDs, EntityStatus? status)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, addressIDs, status).ToArray();
            }
        }

        public IEnumerable<Address> Get(ConfigDatabase db, long[] addressIDs, EntityStatus? status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            if (status == null)
            {
                return db.Addresses.Get(addressIDs);
            }
            
            return db.Addresses.Get(addressIDs, status.Value);
        }
                
        public void Remove(string emailAddress)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, emailAddress);
            }
        }
        
        public void Remove(ConfigDatabase db, string emailAddress)
        {
            if (string.IsNullOrEmpty(emailAddress))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidEmailAddress);
            }
            
            db.Addresses.ExecDelete(emailAddress);
        }

        public void Remove(IEnumerable<string> emailAddresses)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(string emailAddress in emailAddresses)
                {
                    this.Remove(db, emailAddress);
                }
            }
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
            db.Addresses.ExecDeleteDomain(domainID);
        }
        
        public void SetStatus(string[] emailAddresses, EntityStatus status)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.SetStatus(db, emailAddresses, status);
            }
        }
        
        public void SetStatus(ConfigDatabase db, string[] emailAddresses, EntityStatus status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            this.VerifyEmailAddresses(emailAddresses);
            db.Addresses.ExecSetStatus(emailAddresses, status);
        }

        public void SetStatus(long domainID, EntityStatus status)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                db.Addresses.ExecSetStatus(domainID, status);
            }
        }
        
        void VerifyEmailAddresses(string[] emailAddresses)
        {
            if (emailAddresses.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidEmailAddress);
            }
            
            for (int i = 0; i < emailAddresses.Length; ++i)
            {
                if (string.IsNullOrEmpty(emailAddresses[i]))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidEmailAddress);
                }
            }
        }

        public IEnumerator<Address> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (Address address in db.Addresses)
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
