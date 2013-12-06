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
using System.Configuration;
using System.Linq;
using System.Net.Mail;

using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    /// <summary>
    /// Used to manage configured addresses
    /// </summary>
    public class AddressManager : IEnumerable<Address>
    {
        readonly ConfigStore m_store;

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

        /// <summary>
        /// Add a new email address
        /// </summary>
        /// <remarks>
        ///  - Gets the domain of the address and ensures that it exists
        ///  - Then tries to create an entry in the Address table
        ///  - The address is created with EntityStatus.New
        ///  - To use the address, you must enable it
        /// </remarks>
        /// <param name="mailAddress">Mail address object</param>
        public void Add(MailAddress mailAddress)
        {
            this.Add(mailAddress, EntityStatus.New, "SMTP");
        }
        
        /// <summary>
        /// Add a new email address
        /// </summary>
        /// <remarks>
        ///  - Gets the domain of the address and ensures that it exists
        ///  - Then tries to create an entry in the Address table
        ///  - The address is created in the given state
        /// </remarks>
        /// <param name="mailAddress">Mail address object</param>
        /// <param name="status">entity status</param>
        /// <param name="addressType"></param>
        public void Add(MailAddress mailAddress, EntityStatus status, string addressType)
        {
            if (mailAddress == null)
            {
                throw new ArgumentNullException("mailAddress");
            }
            
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, mailAddress, status, addressType);
                db.SubmitChanges();
            }
        }

        /// <summary>
        /// Add a new email address within the given database context
        /// The operation is performed within any transactions in the context
        /// </summary>
        /// <remarks>
        ///  - Gets the domain of the address and ensures that it exists
        ///  - Then tries to create an entry in the Address table
        ///  - The address is created in the given state
        /// </remarks>
        /// <param name="db">db context</param>
        /// <param name="mailAddress">Mail address object</param>
        /// <param name="status">entity status</param>
        /// <param name="addressType"></param>
        public void Add(ConfigDatabase db, MailAddress mailAddress, EntityStatus status, string addressType)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (mailAddress == null)
            {
                throw new ArgumentNullException("mailAddress");
            }

            Domain domain = this.Store.Domains.Get(db, mailAddress.Host);
            if (domain == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
            }

            Address address = new Address(domain.ID, mailAddress) {Type = addressType, Status = status};

            this.Add(db, address);
        }
        
        /// <summary>
        /// Add an address to the store
        /// </summary>
        /// <param name="address">address object</param>        
        public Address Add(Address address)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, address);
                db.SubmitChanges();
                return address;
            }
        }
        
        /// <summary>
        /// Add a set of addresses to the store in a single transaction
        /// </summary>
        /// <param name="addresses">address set</param>
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
        
        /// <summary>
        /// Add an address to the database using the given database context
        /// The address will be added within the context's currently active transaction 
        /// </summary>
        /// <param name="db">database context to use</param>
        /// <param name="address">address object</param>
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
        
        void Update(ConfigDatabase db, Address address)
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

        public Address[] GetAllForDomain(string domainName
                        , int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.GetAllForDomain(db
                                , domainName
                                , maxResults).ToArray();
            }
        }

        public IEnumerable<Address> GetAllForDomain(ConfigDatabase db
                                   , string domainName
                                   , int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Addresses.ExecGetByDomainName(domainName, maxResults);
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
            return this.Get(emailAddresses, false, null);
        }

        public Address[] Get(string[] emailAddresses, bool domainSearchEnabled)
        {
            return this.Get(emailAddresses, domainSearchEnabled, null);
        }

        public IEnumerable<Address> Get(ConfigDatabase db, string[] emailAddresses)
        {
            return this.Get(db, emailAddresses, null);
        }

        public Address[] Get(string[] emailAddresses, EntityStatus? status)
        {
            return Get(emailAddresses, false, status);
        }

        public Address[] Get(string[] emailAddresses, bool domainSearchEnabled, EntityStatus? status)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                Address[] addresses = this.Get(db, emailAddresses, status).ToArray();
                if (domainSearchEnabled)
                {
                    List<Address> addressList = new List<Address>();
                    foreach (var emailAddress in emailAddresses)
                    {
                        string enclosureEmailAddress = emailAddress;
                        Address existingAddress = addresses.SingleOrDefault(a => a.EmailAddress.Equals(enclosureEmailAddress, StringComparison.OrdinalIgnoreCase));
                        if (existingAddress != null)
                        {
                            addressList.Add(existingAddress);
                            continue;
                        }
                        AutoMapDomains(enclosureEmailAddress, addressList, status);
                    }
                    return addressList.ToArray();
                }
                return addresses;
            }
        }


        private void AutoMapDomains(string enclosureEmailAddress, List<Address> addressList, EntityStatus? status)
        {
            MailAddress mailAddress = new MailAddress(enclosureEmailAddress);
            Domain domain = Store.Domains.Get(mailAddress.Host);
            if (domain == null || 
                (status.HasValue && domain.Status != status)
                ) return;
            var address = new Address(domain.ID, mailAddress);
            address.Type = "SMTP";
            address.Status = domain.Status;
            addressList.Add(address);
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
        
        public Address Get(long addressID)
        {
            using(ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, addressID);
            }
        }
        
        public Address Get(ConfigDatabase db, long addressID)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            return db.Addresses.Get(addressID);
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