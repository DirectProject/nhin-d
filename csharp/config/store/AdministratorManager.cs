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
        private readonly DirectDbContext _dbContext;

        internal AdministratorManager(DirectDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        /// <summary>
        /// Add an administrator to the database using the given database context
        /// The administrator will be added within the context's currently active transaction 
        /// </summary>
        /// <param name="administrator">administrator object</param>
        public async Task<Administrator> Add(Administrator administrator)
        {
            if (administrator == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAdministrator);
            }

            _dbContext.Administrators.Add(administrator);

            await _dbContext.SaveChangesAsync();

            return administrator;
        }

        public async Task Update(Administrator administrator)
        {
            if (administrator == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAdministrator);
            }

            _dbContext.Administrators.Attach(administrator);
            
            await _dbContext.SaveChangesAsync();
        }

        public async Task<Administrator?> Get(string username)
        {
            return await _dbContext.Administrators
                .Where(a => a.Username == username)
                .SingleOrDefaultAsync();
        }

        public async Task<Administrator?> Get(long administratorId)
        {
            return await _dbContext.Administrators
                .Where(a => a.ID == administratorId)
                .SingleOrDefaultAsync();
        }

        public async Task Remove(string username)
        {
            if (string.IsNullOrEmpty(username))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidUsername);
            }

            var entity = await _dbContext.Administrators
                .Where(a => a.Username == username)
                .SingleOrDefaultAsync();

            if (entity != null)
            {
                _dbContext.Administrators.Remove(entity);
            }

            await _dbContext.SaveChangesAsync();
        }

        public async Task SetStatus(string username, EntityStatus status)
        {

            var entity = await _dbContext.Administrators
                .Where(a => a.Username == username)
                .SingleOrDefaultAsync();

            if (entity != null)
            {
                entity.Status = status;
                entity.UpdateDate = DateTimeHelper.Now;
            }

            await _dbContext.SaveChangesAsync();
        }

        public async Task<List<Administrator>> Get(string lastUsername, int maxResults)
        {
            return await _dbContext.Administrators
                .Where(a => String.Compare(a.Username, lastUsername ?? "") > 0)
                .OrderBy(a => a.Username)
                .Take(maxResults)
                .ToListAsync();
        }

        //public bool CheckPasswordHash(string username, string passwordHash)
        //{
        //    using (DirectDbContext db = this.Store.CreateContext())
        //    {
        //        return CheckPasswordHash(db, username, passwordHash);
        //    }
        //}

      

        //private static bool CheckPasswordHash(DirectDbContext db, string username, string passwordHash)
        //{
        //    if (string.IsNullOrEmpty(username) || string.IsNullOrEmpty(passwordHash))
        //    {
        //        return false;
        //    }

        //    return _dbContext.Administrators.CheckPasswordHash(username, passwordHash);
        //}
    }
}