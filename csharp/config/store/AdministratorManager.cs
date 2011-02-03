/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;

namespace Health.Direct.Config.Store
{
    /// <summary>
    /// Used to manage configured administrators
    /// </summary>
    public class AdministratorManager
    {
        readonly ConfigStore m_store;
        
        internal AdministratorManager(ConfigStore store)
        {
            m_store = store;
        }

        private ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }

        /// <summary>
        /// Add an administrator to the database using the given database context
        /// The administrator will be added within the context's currently active transaction 
        /// </summary>
        /// <param name="administrator">administrator object</param>
        public Administrator Add(Administrator administrator)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                Add(db, administrator);
                db.SubmitChanges();
                return administrator;
            }
        }

        public void Update(Administrator administrator)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                Update(db, administrator);
                db.SubmitChanges();
            }
        }

        public Administrator Get(string username)
        {
            using(ConfigDatabase db = this.Store.CreateReadContext())
            {
                return Get(db, username);
            }
        }

        public Administrator Get(long administratorID)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return Get(db, administratorID);
            }
        }

        public void Remove(string username)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                Remove(db, username);
            }
        }

        public void SetStatus(string username, EntityStatus status)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                db.Addresses.ExecSetStatus(username, status);
            }
        }

        public IEnumerable<Administrator> Get(string lastUsername, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (Administrator admin in Get(db, lastUsername, maxResults))
                {
                    yield return admin;
                }
            }
        }

        //public bool CheckPasswordHash(string username, string passwordHash)
        //{
        //    using (ConfigDatabase db = this.Store.CreateContext())
        //    {
        //        return CheckPasswordHash(db, username, passwordHash);
        //    }
        //}

        private static void Add(ConfigDatabase db, Administrator administrator)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (administrator == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAdministrator);
            }

            db.Administrators.InsertOnSubmit(administrator);
        }

        private static void Update(ConfigDatabase db, Administrator administrator)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (administrator == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAdministrator);
            }

            Administrator update = new Administrator(administrator);

            db.Administrators.Attach(update);
            update.UpdateFrom(administrator);
        }

        private static Administrator Get(ConfigDatabase db, string username)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            return db.Administrators.Get(username);
        }

        private static IEnumerable<Administrator> Get(ConfigDatabase db, string lastUsername, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Administrators.Get(lastUsername ?? "", maxResults);
        }

        private static Administrator Get(ConfigDatabase db, long administratorID)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            return db.Administrators.Get(administratorID);
        }
                
        private static void Remove(ConfigDatabase db, string username)
        {
            if (string.IsNullOrEmpty(username))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidUsername);
            }
            
            db.Administrators.ExecDelete(username);
        }

        //private static bool CheckPasswordHash(ConfigDatabase db, string username, string passwordHash)
        //{
        //    if (string.IsNullOrEmpty(username) || string.IsNullOrEmpty(passwordHash))
        //    {
        //        return false;
        //    }

        //    return db.Administrators.CheckPasswordHash(username, passwordHash);
        //}
    }
}