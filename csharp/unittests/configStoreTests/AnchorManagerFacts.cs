/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
    Joe Shook     Joseph.Shook@Surescripts.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Xunit;
using Xunit.Abstractions;

namespace Health.Direct.Config.Store.Tests;


[Collection("ManagerFacts")]
public class AnchorManagerFacts : ConfigStoreTestBase
{
    private readonly ITestOutputHelper _testOutputHelper;
    private readonly DirectDbContext _dbContext;
    private readonly AnchorManager _anchorManager;

    public AnchorManagerFacts(ITestOutputHelper testOutputHelper)
    {
        _testOutputHelper = testOutputHelper;
        _dbContext = CreateConfigDatabase();
        _anchorManager = new AnchorManager(_dbContext);
    }
    
    /// <summary>
    /// property to expose enumerable testing Anchor certificate instances
    /// </summary>
    /// <remarks>
    /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
    /// </remarks>
    public new static IEnumerable<object[]> TestAnchors => ConfigStoreTestBase.TestAnchors;

    /// <summary>
    /// property to expose enumerable test certs extracted from pfx files in metadata\certs folder
    /// </summary>
    /// <remarks>
    /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
    /// </remarks>
    public new static IEnumerable<object[]> TestCerts => ConfigStoreTestBase.TestCerts;

    /// <summary>
    /// property to expose enumerable test cert bytpes extracted from pfx files in metadata\certs folder
    /// </summary>
    /// <remarks>
    /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
    /// </remarks>
    public new static IEnumerable<object[]> TestCertsBytes => ConfigStoreTestBase.TestCertsBytes;


    /// <summary>
    ///A test for SetStatus
    ///</summary>
    [Fact]
    public async Task SetStatusTest()
    {
        await InitAnchorRecords(_dbContext);
        foreach (string domain in TestDomainNames)
        {
            string subject = "CN=" + domain;
            
            var actual = await _anchorManager.Get(subject);
            Dump(_testOutputHelper, $"SetStatusTest1 Subject[{subject}] which has [{actual?.Count ?? -1}] related certs.");
            Assert.NotNull(actual);
            Assert.Equal(MaxCertPerOwner, actual.Count);

            foreach (Anchor cert in actual)
            {
                Assert.Equal(EntityStatus.New, cert.Status);
            }

            await _anchorManager.SetStatus(subject, EntityStatus.Enabled);
            actual = await _anchorManager.Get(subject);
            Assert.NotNull(actual);
            Assert.Equal(MaxCertPerOwner, actual.Count);

            foreach (var cert in actual)
            {
                Assert.Equal(EntityStatus.Enabled, cert.Status);
            }
        }
    }
    
    /// <summary>
    ///A test for Remove
    ///</summary>
    [Fact]
    public async Task RemoveTest5()
    {
        await InitAnchorRecords(_dbContext);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
        long[] certificateIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
        await _anchorManager.Remove(certificateIDs);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner - certificateIDs.Length, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
    }

    /// <summary>
    ///A test for Remove
    ///</summary>
    [Fact]
    public async Task RemoveTest4()
    {
        await InitAnchorRecords(_dbContext);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
        long[] certificateIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
        await _anchorManager.Remove(certificateIDs);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner - certificateIDs.Length, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
    }

    /// <summary>
    ///A test for Remove
    ///</summary>
    [Fact]
    public async Task RemoveTest3()
    {
        await InitAnchorRecords(_dbContext);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
        string ownerName = $"CN={BuildDomainName(GetRndDomainId())}";
        await _anchorManager.Remove(ownerName);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner - MaxCertPerOwner, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
    }

    /// <summary>
    ///A test for Remove
    ///</summary>
    [Fact]
    public async Task RemoveTest2Async()
    {
        await InitAnchorRecords(_dbContext);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
        string ownerName = $"CN={BuildDomainName(GetRndDomainId())}";
        await _anchorManager.Remove(ownerName);
        Assert.Equal(MaxDomainCount * MaxCertPerOwner - MaxCertPerOwner, (await _anchorManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
    }

    /// <summary>
    ///A test for Remove
    ///</summary>
    [Fact]
    public async Task RemoveTest1()
    {
        await InitAnchorRecords(_dbContext);
        var certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
        string owner = certs[0].Owner;
        string thumbprint = certs[0].Thumbprint;
        Assert.NotNull(_anchorManager.Get(owner, thumbprint));
        await _anchorManager.Remove(owner, thumbprint);
        Assert.Null(await _anchorManager.Get(owner, thumbprint));
    }

    /// <summary>
    ///A test for Remove
    ///</summary>
    [Fact]
    public async Task RemoveTest()
    {
        await InitAnchorRecords(_dbContext);
        var certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
        string owner = certs[0].Owner;
        string thumbprint = certs[0].Thumbprint;
        Assert.NotNull(await _anchorManager.Get(owner, thumbprint));
        await _anchorManager.Remove(owner, thumbprint);
        Assert.Null(await _anchorManager.Get(owner, thumbprint));
    }

    /// <summary>
    ///A test for GetOutgoing
    ///</summary>
    [Fact]
    public async Task GetOutgoingTest1()
    {
        await InitAnchorRecords(_dbContext);
        string ownerName = $"CN={BuildDomainName(GetRndDomainId())}";
        await _anchorManager.SetStatus(ownerName, EntityStatus.Enabled);
        var actual = await _anchorManager.GetOutgoing(ownerName, null);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        actual = await _anchorManager.GetOutgoing(ownerName, EntityStatus.Enabled);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        actual = await _anchorManager.GetOutgoing(ownerName, EntityStatus.New);
        Assert.Empty(actual);
    }

    /// <summary>
    ///A test for GetOutgoing
    ///</summary>
    [Fact]
    public async Task GetOutgoingTest()
    {
        await InitAnchorRecords(_dbContext);
        string ownerName = $"CN={BuildDomainName(GetRndDomainId())}";
        var actual = await _anchorManager.GetOutgoing(ownerName);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        await _anchorManager.SetStatus(ownerName, EntityStatus.Enabled);
        actual = await _anchorManager.GetOutgoing(ownerName);
        Assert.Equal(MaxCertPerOwner, actual.Count);
    }

    /// <summary>
    ///A test for GetIncoming
    ///</summary>
    [Fact]
    public async Task GetIncomingTest1()
    {
        await InitAnchorRecords(_dbContext);
        string ownerName = $"CN={BuildDomainName(GetRndDomainId())}";
        await _anchorManager.SetStatus(ownerName, EntityStatus.Enabled);
        var actual = await _anchorManager.GetIncoming(ownerName, null);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        actual = await _anchorManager.GetIncoming(ownerName, EntityStatus.Enabled);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        actual = await _anchorManager.GetIncoming(ownerName, EntityStatus.New);
        Assert.Empty(actual);
    }

    /// <summary>
    ///A test for GetIncoming
    ///</summary>
    [Fact]
    public async Task GetIncomingTest()
    {
        await InitAnchorRecords(_dbContext);
        string ownerName = $"CN={BuildDomainName(GetRndDomainId())}";
        var actual = await _anchorManager.GetIncoming(ownerName);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        await _anchorManager.SetStatus(ownerName, EntityStatus.Enabled);
        actual = await _anchorManager.GetIncoming(ownerName);
        Assert.Equal(MaxCertPerOwner, actual.Count);
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest6()
    {
        await InitAnchorRecords(_dbContext);
        const long lastCertId = 0;
        const int maxResults = MaxCertPerOwner * MaxDomainCount + 1;
        IEnumerable<Anchor> actual = await _anchorManager.Get(lastCertId, maxResults);
        Assert.Equal(MaxCertPerOwner * MaxDomainCount, actual.Count());
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest5()
    {
        await InitAnchorRecords(_dbContext);
        const long lastCertId = 0;
        const int maxResults = MaxCertPerOwner * MaxDomainCount + 1;
        IEnumerable<Anchor> actual = await _anchorManager.Get(lastCertId, maxResults);
        Assert.Equal(MaxCertPerOwner * MaxDomainCount, actual.Count());
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest4()
    {
        await InitAnchorRecords(_dbContext);
        long[] certIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
        var actual = await _anchorManager.Get(certIDs);
        Assert.Equal(certIDs.Length, actual.Count);

        foreach (Anchor cert in actual)
        {
            Assert.Contains(cert.ID, certIDs);
        }
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest3()
    {
        await InitAnchorRecords(_dbContext);
        string owner = $"CN={BuildDomainName(GetRndDomainId())}";
        var actual = await _anchorManager.Get(owner);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        foreach (Anchor cert in actual)
        {
            Assert.Equal(owner, cert.Owner);
        }
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest2()
    {
        await InitAnchorRecords(_dbContext);
        var certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
        int i = GetRndCertId();
        string owner = certs[i].Owner;
        string thumbprint = certs[i].Thumbprint;
        Anchor expected = certs[i];
        Anchor actual = await _anchorManager.Get(owner, thumbprint);
        Assert.Equal(expected.Owner, actual.Owner);
        Assert.Equal(expected.Thumbprint, actual.Thumbprint);
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest1()
    {
        await InitAnchorRecords(_dbContext);
        var certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
        int i = GetRndCertId();
        string owner = certs[i].Owner;
        string thumbprint = certs[i].Thumbprint;
        Anchor expected = certs[i];
        Anchor actual = await _anchorManager.Get(owner, thumbprint);
        Assert.Equal(expected.Owner, actual.Owner);
        Assert.Equal(expected.Thumbprint, actual.Thumbprint);
    }

    /// <summary>
    ///A test for GetByAgentName
    ///</summary>
    [Fact]
    public async Task GetTest()
    {
        await InitAnchorRecords(_dbContext);
        string owner = $"CN={BuildDomainName(GetRndDomainId())}";
        List<Anchor> actual = await _anchorManager.Get(owner);
        Assert.Equal(MaxCertPerOwner, actual.Count);
        foreach (Anchor cert in actual)
        {
            Assert.Equal(owner, cert.Owner);
        }
    }

    /// <summary>
    ///A test for Add
    ///</summary>
    [Theory]
    [MemberData("TestAnchors")]
    public async Task AddTest2(Anchor anc)
    {
        await _anchorManager.Add(anc);
        Anchor certNew = await _anchorManager.Get(anc.Owner, anc.Thumbprint); //---should always be 1 (table was truncated above);
        Assert.NotNull(anc);
        Assert.Equal(anc.Owner, certNew.Owner);
        Assert.Equal(anc.Thumbprint, certNew.Thumbprint);
    }

    /// <summary>
    ///A test for Add
    ///</summary>
    [Fact]
    public async Task AddTest1()
    {
        List<Anchor> certs = GetCleanEnumerable<Anchor>(TestAnchors);
        await _anchorManager.Add(certs);
        var actual = await _anchorManager.Get(0, MaxCertPerOwner * MaxDomainCount);
        Assert.Equal(certs.Count, actual.Count);
    }

    /// <summary>
    ///A test for Add
    ///</summary>
    [Theory]
    [MemberData(nameof(TestAnchors))]
    public async Task AddTest(Anchor anc)
    {
        await _anchorManager.Add(anc);
        
        var certNew = await _anchorManager.Get(anc.Owner, anc.Thumbprint); //---should always be 1 (table was truncated above);
        Assert.NotNull(anc);
        Assert.Equal(anc.Owner, certNew.Owner);
        Assert.Equal(anc.Thumbprint, certNew.Thumbprint);
    }
}
