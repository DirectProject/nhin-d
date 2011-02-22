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
using System.Linq;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Config.Store.Tests
{
    public class AdministratorManagerFacts : ConfigStoreTestBase, IDisposable
    {
        private readonly ConfigDatabase m_database;
        private readonly AdministratorManager m_manager;
        private readonly string m_username;
        private readonly string m_password;

        public AdministratorManagerFacts()
        {
            m_database = CreateConfigDatabase();
            m_manager = new AdministratorManager(CreateConfigStore());
            m_username = Guid.NewGuid().ToString("N");
            m_password = "asdf1234";
        }

        public void Dispose()
        {
            m_database.Dispose();
        }

        [Fact, AutoRollback]
        public Administrator Add()
        {
            var origAdmin = new Administrator(m_username, m_password);
            m_manager.Add(origAdmin);
            
            var admin = (from a in m_database.Administrators
                         where a.Username == m_username
                         select a).SingleOrDefault();

            Assert.NotNull(admin);
            Assert.True(admin.ID > 0);
            Assert.Equal(origAdmin.Username, admin.Username);
            Assert.Equal(origAdmin.CreateDate, admin.CreateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.UpdateDate, admin.UpdateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.Status, admin.Status);
            Assert.True(admin.CheckPassword(m_password));

            return admin;
        }

        [Fact, AutoRollback]
        public void Update()
        {
            var origAdmin = Add();

            origAdmin.Status = EntityStatus.Enabled;
            origAdmin.SetPassword("qwerty");
            m_manager.Update(origAdmin);

            var admin = (from a in m_database.Administrators
                         where a.Username == m_username
                         select a).SingleOrDefault();

            Assert.NotNull(admin);
            Assert.True(admin.ID > 0);
            Assert.Equal(origAdmin.Username, admin.Username);
            Assert.Equal(origAdmin.CreateDate, admin.CreateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.UpdateDate, admin.UpdateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.Status, admin.Status);
            Assert.True(admin.CheckPassword("qwerty"));
        }

        [Fact, AutoRollback]
        public void GetByUsername()
        {
            Add();

            var admin = m_manager.Get(m_username);
            Assert.NotNull(admin);
            Assert.Equal(m_username, admin.Username);
            Assert.Equal(EntityStatus.New, admin.Status);
            Assert.True(admin.CheckPassword(m_password));
        }

        [Fact, AutoRollback]
        public void GetByID()
        {
            var added = Add();

            var admin = m_manager.Get(added.ID);
            Assert.NotNull(admin);
            Assert.Equal(m_username, admin.Username);
        }

        [Fact, AutoRollback]
        public void Remove()
        {
            Add();
            m_manager.Remove(m_username);
            Assert.Null(m_manager.Get(m_username));
        }

        [Fact, AutoRollback]
        public void SetStatus()
        {
            Add();
            m_manager.SetStatus(m_username, EntityStatus.Disabled);

            var admin = m_manager.Get(m_username);
            Assert.NotNull(admin);
            Assert.Equal(EntityStatus.Disabled, admin.Status);
        }

        public class DbDateTimeComparer : IEqualityComparer<DateTime>
        {
            public bool Equals(DateTime x, DateTime y)
            {
                return x.ToString() == y.ToString();
            }

            public int GetHashCode(DateTime obj)
            {
                return obj.GetHashCode();
            }
        }
    }
}