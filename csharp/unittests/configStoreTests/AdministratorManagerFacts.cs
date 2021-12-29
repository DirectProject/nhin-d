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
using System.Threading.Tasks;
using Health.Direct.Config.Store.Entity;
using Xunit;

namespace Health.Direct.Config.Store.Tests;

[Collection("ManagerFacts")]
public class AdministratorManagerFacts : ConfigStoreTestBase, IDisposable
{
    private readonly DirectDbContext _dbContext;
    private readonly AdministratorManager _adminManager;
    private readonly string _username;
    private readonly string _password;

    public AdministratorManagerFacts()
    {
        _dbContext = CreateConfigDatabase();
        _adminManager = new AdministratorManager(_dbContext);
        _username = Guid.NewGuid().ToString("N");
        _password = "asdf1234";
    }

    public void Dispose()
    {
        _dbContext.Dispose();
    }

    [Fact]
    public async Task<Administrator> Add()
    {
        var origAdmin = new Administrator(_username, _password);
        await _adminManager.Add(origAdmin);

        var admin = (_dbContext.Administrators.Where(a => a.Username == _username)).SingleOrDefault();

        Assert.NotNull(admin);
        Assert.True(admin.ID > 0);
        Assert.Equal(origAdmin.Username, admin.Username);
        Assert.Equal(origAdmin.CreateDate, admin.CreateDate, new DbDateTimeComparer());
        Assert.Equal(origAdmin.UpdateDate, admin.UpdateDate, new DbDateTimeComparer());
        Assert.Equal(origAdmin.Status, admin.Status);
        Assert.True(admin.CheckPassword(_password));

        return admin;
    }

    [Fact]
    public async Task Update()
    {
        var origAdmin = await Add();

        origAdmin.Status = EntityStatus.Enabled;
        origAdmin.SetPassword("qwerty");
        await _adminManager.Update(origAdmin);

        var admin = (_dbContext.Administrators.Where(a => a.Username == _username)).SingleOrDefault();

        Assert.NotNull(admin);
        Assert.True(admin.ID > 0);
        Assert.Equal(origAdmin.Username, admin.Username);
        Assert.Equal(origAdmin.CreateDate, admin.CreateDate, new DbDateTimeComparer());
        Assert.Equal(origAdmin.UpdateDate, admin.UpdateDate, new DbDateTimeComparer());
        Assert.Equal(origAdmin.Status, admin.Status);
        Assert.True(admin.CheckPassword("qwerty"));
    }

    [Fact]
    public async Task GetByUsername()
    {
        await Add();

        var admin = await _adminManager.Get(_username);
        Assert.NotNull(admin);
        Assert.Equal(_username, admin.Username);
        Assert.Equal(EntityStatus.New, admin.Status);
        Assert.True(admin.CheckPassword(_password));
    }

    [Fact]
    public async Task GetById()
    {
        var added = await Add();

        var admin = await _adminManager.Get(added.ID);
        Assert.NotNull(admin);
        Assert.Equal(_username, admin.Username);
    }

    [Fact]
    public async Task Remove()
    {
        await Add();
        await _adminManager.Remove(_username);
        Assert.Null(await _adminManager.Get(_username));
    }

    [Fact]
    public async Task SetStatus()
    {
        await Add();
        await _adminManager.SetStatus(_username, EntityStatus.Disabled);

        var admin = await _adminManager.Get(_username);
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
