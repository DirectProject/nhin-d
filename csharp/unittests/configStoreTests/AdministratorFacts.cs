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
    public class AdministratorFacts : ConfigStoreTestBase, IDisposable
    {
        private readonly ConfigDatabase m_database;
        private readonly AdministratorManager m_manager;

        public AdministratorFacts()
        {
            m_database = CreateConfigDatabase();
            m_manager = new AdministratorManager(CreateConfigStore());
        }

        public void Dispose()
        {
            m_database.Dispose();
        }

        [Fact]
        [AutoRollback]
        public Administrator Add()
        {
            var origAdmin = new Administrator("admin", "admin");
            m_manager.Add(origAdmin);
            
            var admin = (from a in m_database.Administrators
                         where a.Username == "admin"
                         select a).SingleOrDefault();

            Assert.NotNull(admin);
            Assert.True(admin.ID > 0);
            Assert.Equal(origAdmin.Username, admin.Username);
            Assert.Equal(origAdmin.CreateDate, admin.CreateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.UpdateDate, admin.UpdateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.Status, admin.Status);
            Assert.True(admin.CheckPassword("admin"));

            return admin;
        }

        [Fact]
        [AutoRollback]
        public void Update()
        {
            var origAdmin = Add();

            origAdmin.Status = EntityStatus.Enabled;
            origAdmin.SetPassword("qwerty");
            m_manager.Update(origAdmin);

            var admin = (from a in m_database.Administrators
                         where a.Username == "admin"
                         select a).SingleOrDefault();

            Assert.NotNull(admin);
            Assert.True(admin.ID > 0);
            Assert.Equal(origAdmin.Username, admin.Username);
            Assert.Equal(origAdmin.CreateDate, admin.CreateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.UpdateDate, admin.UpdateDate, new DbDateTimeComparer());
            Assert.Equal(origAdmin.Status, admin.Status);
            Assert.True(admin.CheckPassword("qwerty"));
        }

        [Fact]
        [AutoRollback]
        public void GetByUsername()
        {
            Add();

            var admin = m_manager.Get("admin");
            Assert.NotNull(admin);
            Assert.Equal("admin", admin.Username);
            Assert.Equal(EntityStatus.New, admin.Status);
            Assert.True(admin.CheckPassword("admin"));
        }

        [Fact]
        [AutoRollback]
        public void GetByID()
        {
            var added = Add();

            var admin = m_manager.Get(added.ID);
            Assert.NotNull(admin);
            Assert.Equal("admin", admin.Username);
        }

        [Fact]
        [AutoRollback]
        public void Remove()
        {
            Add();
            m_manager.Remove("admin");
            Assert.Null(m_manager.Get("admin"));
        }

        [Fact]
        [AutoRollback]
        public void SetStatus()
        {
            Add();
            m_manager.SetStatus("admin", EntityStatus.Disabled);

            var admin = m_manager.Get("admin");
            Assert.NotNull(admin);
            Assert.Equal(EntityStatus.Disabled, admin.Status);
        }

        public class DbDateTimeComparer : IEqualityComparer<DateTime>
        {
            public bool Equals(DateTime x, DateTime y)
            {
                return x.Year == y.Year
                       && x.Month == y.Month
                       && x.Day == y.Day
                       && x.Hour == y.Hour
                       && x.Minute == y.Minute
                       && x.Second == y.Second
                       && x.Millisecond/100 == y.Millisecond/100;
            }

            public int GetHashCode(DateTime obj)
            {
                return obj.GetHashCode();
            }
        }
    }
}