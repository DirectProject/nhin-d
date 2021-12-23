/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
    Joe Shook     Joseph.Shook@Surescripts.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store
{
    /// <summary>
    /// Used to manage configured administrators
    /// </summary>
    public class AdministratorManager
    {
        internal AdministratorManager(ConfigStore store)
        {
            Store = store;
        }

        private ConfigStore Store { get; }

        /// <summary>
        /// Add an administrator to the database using the given database context
        /// The administrator will be added within the context's currently active transaction 
        /// </summary>
        /// <param name="administrator">administrator object</param>
        public async Task<Administrator> Add(Administrator administrator)
        {
            await using var db = this.Store.CreateContext();
            Add(db, administrator);
            await db.SaveChangesAsync();

            return administrator;
        }

        public async Task Update(Administrator administrator)
        {
            await using var db = this.Store.CreateContext();
            Update(db, administrator);
            await db.SaveChangesAsync();
        }

        public async Task<Administrator> Get(string username)
        {
            await using var db = this.Store.CreateReadContext();
            return await Get(db, username);
        }

        public async Task<Administrator> Get(long administratorId)
        {
            await using var db = this.Store.CreateReadContext();
            return await Get(db, administratorId);
        }

        public async Task Remove(string username)
        {
            await using var db = this.Store.CreateContext();
            await Remove(db, username);
            await db.SaveChangesAsync();
        }

        public async Task SetStatus(string username, EntityStatus status)
        {
            await using var db = this.Store.CreateContext();

            var entity = await db.Administrators
                .Where(a => a.Username == username)
                .SingleOrDefaultAsync();

            if (entity != null)
            {
                entity.Status = status;
                entity.UpdateDate = DateTimeHelper.Now;
            }

            await db.SaveChangesAsync();
        }

        public async Task<List<Administrator>> Get(string lastUsername, int maxResults)
        {
            await using var db = this.Store.CreateContext();
            return await Get(db, lastUsername, maxResults);
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
                throw new ArgumentNullException(nameof(db));
            }

            if (administrator == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAdministrator);
            }

            db.Administrators.Add(administrator);
        }

        private static void Update(ConfigDatabase db, Administrator administrator)
        {
            if (db == null)
            {
                throw new ArgumentNullException(nameof(db));
            }

            if (administrator == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAdministrator);
            }

            var update = new Administrator(administrator);

            db.Administrators.Attach(update);
            update.UpdateFrom(administrator);
        }

        private static async Task<Administrator> Get(ConfigDatabase db, string username)
        {
            if (db == null)
            {
                throw new ArgumentNullException(nameof(db));
            }
            
            return await db.Administrators
                .Where(a => a.Username == username)
                .SingleOrDefaultAsync();
        }

        private static async Task<List<Administrator>> Get(ConfigDatabase db, string lastUsername, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException(nameof(db));
            }

            return await db.Administrators
                .Where(a => String.Compare(a.Username, lastUsername ?? "") > 0)
                .OrderBy(a => a.Username)
                .Take(maxResults)
                .ToListAsync();
        }

        private static async Task<Administrator> Get(ConfigDatabase db, long administratorId)
        {
            if (db == null)
            {
                throw new ArgumentNullException(nameof(db));
            }

            return await db.Administrators
                .Where(a => a.ID == administratorId)
                .SingleOrDefaultAsync();
        }
                
        private static async Task Remove(ConfigDatabase db, string username)
        {
            if (string.IsNullOrEmpty(username))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidUsername);
            }

            var entity = await db.Administrators
                .Where(a => a.Username == username)
                .SingleOrDefaultAsync();

            if (entity != null)
            {
                db.Administrators.Remove(entity);
            }
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