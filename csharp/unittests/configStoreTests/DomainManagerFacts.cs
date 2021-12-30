/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
  
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
using Xunit;

namespace Health.Direct.Config.Store.Tests;

[Collection("ManagerFacts")]
public class DomainManagerFacts : ConfigStoreTestBase, IDisposable
{
    private readonly DirectDbContext _dbContext;
    private readonly DomainManager _domainManager;

    public DomainManagerFacts()
    {
        _dbContext = CreateConfigDatabase();
        _domainManager = new DomainManager(_dbContext);
    }

    /// <summary>Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources asynchronously.</summary>
    /// <returns>A task that represents the asynchronous dispose operation.</returns>
    public void Dispose()
    {
        _dbContext.Dispose();
    }

    /// <summary>
    ///A test for GetEnumerator
    ///</summary>
    [Fact]
    public async Task GetEnumeratorTest()
    {
        InitDomainRecords(_dbContext);
        Assert.Equal(MaxDomainCount, await _domainManager.Count());
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest7()
    {
        InitDomainRecords(_dbContext);
        var names = TestDomainNames.ToArray();
        var actual = await _domainManager.Get(names);
        Assert.Equal(names.Length, actual.Count);
        foreach (var dom in actual)
        {
            Assert.Contains(dom.Name, names);
        }
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest6()
    {
        InitDomainRecords(_dbContext);
        var names = TestDomainNames.ToArray();
        var actual = await _domainManager.Get(names);
        Assert.Equal(names.Length, actual.Count);
        foreach (Domain dom in actual)
        {
            Assert.Contains(dom.Name, names);
        }
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest5()
    {
        InitDomainRecords(_dbContext);
        string name = BuildDomainName(GetRndDomainId());
        var actual = await _domainManager.Get(name);
        Assert.Equal(name, actual.Name);
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest4()
    {
        InitDomainRecords(_dbContext);
        string name = BuildDomainName(GetRndDomainId());
        var actual = await _domainManager.Get(name);
        Assert.Equal(name, actual.Name);
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest3Last()
    {

        InitDomainRecords(_dbContext);

        //----------------------------------------------------------------------------------------------------
        //---get the full dictionary using the domain name as the key and pick one to start at
        Dictionary<string, Domain> mxsAll = _domainManager.ToDictionary(p => p.Name);

        Assert.Equal(MaxDomainCount, mxsAll.Count);

        int pos = GetRndDomainId();
        //----------------------------------------------------------------------------------------------------
        //---grab the key at position pos-1 in the array, and use that as the "last" name to be passed in
        string[] allKeys = mxsAll.Keys.OrderBy(k => k).ToArray();
        string val = allKeys[pos - 1];

        var mxs = await _domainManager.Get(val, MaxDomainCount + 1);

        //----------------------------------------------------------------------------------------------------
        //---expected that the count of mxs will be  max count - pos
        Assert.Equal(MaxDomainCount - pos, mxs.Count);

        //----------------------------------------------------------------------------------------------------
        //---try one with a limited number less than pos
        mxs = await _domainManager.Get(allKeys[0], pos - 1);
        Assert.Equal(pos - 1, mxs.Count);

        //----------------------------------------------------------------------------------------------------
        //---get the last item and see to ensure that no records are returned
        val = allKeys.Last();
        mxs = await _domainManager.Get(val, MaxDomainCount + 1);
        Assert.Equal(0, mxs.Count);

        //----------------------------------------------------------------------------------------------------
        //---get the first item and see to ensure that MAX - 1 records are returned
        val = mxsAll.Keys.OrderBy(k => k).ToArray().First();
        mxs = await _domainManager.Get(val, MaxDomainCount + 1);
        Assert.Equal(MaxDomainCount - 1, mxs.Count);
    }

    [Fact]
    public async Task GetTest3First()
    {
        InitDomainRecords(_dbContext);

        var mxs = await _domainManager.Get(String.Empty, MaxDomainCount + 1);

        //----------------------------------------------------------------------------------------------------
        //---expected that all of the records will be returned
        Assert.Equal(MaxDomainCount, mxs.Count);

        //----------------------------------------------------------------------------------------------------
        //---try one with a limited number less than max count
        int pos = GetRndDomainId();
        mxs = await _domainManager.Get(String.Empty, pos - 1);
        Assert.Equal(pos - 1, mxs.Count);
    }
    
    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest()
    {
        InitDomainRecords(_dbContext);

        string[] names = new[] { BuildDomainName(1), BuildDomainName(2), BuildDomainName(3) };

        //----------------------------------------------------------------------------------------------------
        //---new status should still yield 3 results
        var actual = await _domainManager.Get(names, EntityStatus.New);
        Assert.Equal(names.Length, actual.Count);

        //----------------------------------------------------------------------------------------------------
        //---pass in null and expect matching results
        actual = await _domainManager.Get(names, null);
        Assert.Equal(names.Length, actual.Count);

        //----------------------------------------------------------------------------------------------------
        //---disabled status should still yield no results
        actual = await _domainManager.Get(names, EntityStatus.Disabled);
        Assert.Empty(actual);

        //----------------------------------------------------------------------------------------------------
        //---null pref should still yield same results
        actual = await _domainManager.Get(names, EntityStatus.Enabled);
        Assert.Empty(actual);
    }

    

    /// <summary>
    ///A test for Count
    ///</summary>
    [Fact]
    public async Task CountTest()
    {
        InitDomainRecords(_dbContext);
        Assert.Equal(MaxDomainCount, await _domainManager.Count());
    }

    /// <summary>
    ///A test for Add
    ///</summary>
    [Fact]
    public async Task AddTest()
    {
        Assert.Equal(0, await _domainManager.Count());
        string name = BuildDomainName(GetRndDomainId());
        var domain = new Domain(name);
        await _domainManager.Add(domain);
        Assert.NotNull(await _domainManager.Get(name));
    }
}
