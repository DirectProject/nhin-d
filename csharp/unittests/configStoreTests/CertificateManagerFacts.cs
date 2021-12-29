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
using System.Security.Cryptography.X509Certificates;
using System.Threading.Tasks;
using Health.Direct.Config.Store.Entity;
using Xunit;
using Xunit.Abstractions;

namespace Health.Direct.Config.Store.Tests
{
    [Collection("ManagerFacts")]
    public class CertificateManagerFacts : ConfigStoreTestBase
    {
        private readonly ITestOutputHelper _testOutputHelper;
        private readonly DirectDbContext _dbContext;
        private readonly CertificateManager _certificateManager;

        public CertificateManagerFacts(ITestOutputHelper testOutputHelper)
        {
            _testOutputHelper = testOutputHelper;
            _dbContext = CreateConfigDatabase();
            _certificateManager = new CertificateManager(_dbContext);
        }
        
        /// <summary>
        /// property to expose enumerable testing Certificate instances
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public new static IEnumerable<object[]> TestCertificates => ConfigStoreTestBase.TestCertificates;

        /// <summary>
        /// property to expose enumerable test certs extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public new static IEnumerable<object[]> TestCerts => ConfigStoreTestBase.TestCerts;

        /// <summary>
        /// property to expose enumerable test cert bytes extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public new static IEnumerable<object[]> TestCertsBytes => ConfigStoreTestBase.TestCertsBytes;

        /// <summary>
        ///A test for Item
        ///</summary>
        [Fact]
        public async Task ItemTest()
        {
            await InitCertRecords(_dbContext);

            foreach (string domain in TestDomainNames)
            {
                string subject = domain;
                var actual = _certificateManager[subject];
                Dump(_testOutputHelper, $"ItemTest Subject[{subject}] which has [{actual?.Count ?? -1}] related certs.");
                Assert.NotNull(actual);
            }
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact]
        public async Task SetStatusTest4()
        {
            await InitCertRecords(_dbContext);

            foreach (string domain in TestDomainNames)
            {
                string subject =domain;
                
                List<Certificate> actual = await _certificateManager.Get(subject);
                Dump(_testOutputHelper,
                    $"SetStatusTest4 Subject[{subject}] which has [{actual?.Count ?? -1}] related certs.");
                Assert.NotNull(actual);
                Assert.Equal(MaxCertPerOwner, actual.Count);

                foreach (Certificate cert in actual)
                {
                    Assert.Equal(EntityStatus.New, cert.Status);
                }

                await _certificateManager.SetStatus(subject, EntityStatus.Enabled);

                actual = await _certificateManager.Get(subject);
                Assert.NotNull(actual);
                Assert.Equal(MaxCertPerOwner, actual.Count);
                foreach (Certificate cert in actual)
                {
                    Assert.Equal(EntityStatus.Enabled, cert.Status);
                }
            }

            foreach (Certificate cert in GetCleanEnumerable<Certificate>(TestCertificates))
            {
                
                X509Certificate xcert = cert.ToX509Certificate();
                var expected = await _certificateManager.Get(xcert.Subject);
                await _certificateManager.SetStatus(xcert.Subject, EntityStatus.Enabled);
            }
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact]
        public async Task SetStatusTest3()
        {
            await InitCertRecords(_dbContext);
            
            foreach (string domain in TestDomainNames)
            {
                string subject = domain;
                
                var actual = await _certificateManager.Get(subject);
                Dump(_testOutputHelper, $"SetStatusTest3 Subject[{subject}] which has [{actual?.Count ?? -1}] related certs.");
                Assert.NotNull(actual);
                Assert.Equal(MaxCertPerOwner, actual.Count);

                foreach (Certificate cert in actual)
                {
                    Assert.Equal(EntityStatus.New, cert.Status);
                }

                await _certificateManager.SetStatus(subject, EntityStatus.Enabled);
                await _dbContext.SaveChangesAsync();
                actual = await _certificateManager.Get(subject);
                Assert.NotNull(actual);
                Assert.Equal(MaxCertPerOwner, actual.Count);
                foreach (var cert in actual)
                {
                    Assert.Equal(EntityStatus.Enabled, cert.Status);
                }
            }
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact]
        public async Task SetStatusTest2()
        {
            await InitCertRecords(_dbContext);

            for (long i = 1; i <= MaxCertPerOwner * MaxDomainCount; i++)
            {
                
                Certificate cert = await _certificateManager.Get(i);
                Dump(_testOutputHelper,
                    $"SetStatusTest1 Subject[{(cert == null ? "null cert" : cert.Owner)}] Status:[{cert?.Status.ToString() ?? "null cert"}]");
                Assert.Equal(EntityStatus.New, cert.Status);
                await _certificateManager.SetStatus(i, EntityStatus.Enabled);
                cert = await _certificateManager.Get(i);
                Assert.Equal(EntityStatus.Enabled, cert.Status);
            }
        }

       
        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact]
        public async Task SetStatusTest()
        {
            long t = 1;
            for (long i = 1; i <= MaxDomainCount; i++)
            {
                var ids = new List<long>(MaxCertPerOwner);
                while ((t % MaxCertPerOwner) > 0)
                {
                    ids.Add(t);
                    t++;
                }
                ids.Add(t);
                t++;
                Dump(_testOutputHelper, $"SetStatusTest checking certs for {BuildDomainName(i)}, found {ids.Count}");

                
                var certs = await _certificateManager.Get(ids.ToArray());
                foreach (Certificate cert in certs)
                {
                    Dump(_testOutputHelper, 
                        $"\t - Subject[{(cert == null ? "null cert" : cert.Owner)}] Status:[{cert?.Status.ToString() ?? "null cert"}] CertPolicyId:[{cert?.ID ?? -1}]");
                    Assert.Equal(EntityStatus.New, cert?.Status);
                }

                await _certificateManager.SetStatus(ids.ToArray(), EntityStatus.Enabled);
                certs = await _certificateManager.Get(ids.ToArray());
                foreach (Certificate cert in certs)
                {
                    Assert.Equal(EntityStatus.Enabled, cert.Status);
                }
            }
        }
        
        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest4()
        {
            await InitCertRecords(_dbContext);
            Assert.Equal(MaxDomainCount * MaxCertPerOwner, (await _certificateManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
            string ownerName = $"{BuildDomainName(1)}";
            await _certificateManager.Remove(ownerName);
            Assert.Equal(MaxDomainCount * MaxCertPerOwner - MaxCertPerOwner, (await _certificateManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest3()
        {
            await InitCertRecords(_dbContext);
            Assert.Equal(MaxDomainCount * MaxCertPerOwner, (await _certificateManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
            long[] certificateIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
            await _certificateManager.Remove(certificateIDs);
            Assert.Equal(MaxDomainCount * MaxCertPerOwner - certificateIDs.Length, (await _certificateManager.Get(-1, MaxDomainCount * MaxCertPerOwner + 1)).Count);
        }

        
        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest1()
        {
            await InitCertRecords(_dbContext);
            const long certId = 1;
            Assert.NotNull(await _certificateManager.Get(certId));
            await _certificateManager.Remove(certId);
            Assert.Null(await _certificateManager.Get(certId));
        }

        
        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest9()
        {
            await InitCertRecords(_dbContext);
            var certs = this.GetCleanEnumerable<Certificate>(TestCertificates);
            var i = GetRndCertId();
            string owner = certs[i].Owner;
            string thumbprint = certs[i].Thumbprint;
            var expected = certs[i];
            var actual = await _certificateManager.Get(owner, thumbprint);
            Assert.Equal(expected.Owner, actual.Owner);
            Assert.Equal(expected.Thumbprint, actual.Thumbprint);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest8()
        {
            await InitCertRecords(_dbContext);
            const long lastCertId = 0;
            const int maxResults = MaxCertPerOwner * MaxDomainCount + 1;
            IEnumerable<Certificate> actual = await _certificateManager.Get(lastCertId, maxResults);
            Assert.Equal(MaxCertPerOwner * MaxDomainCount, actual.Count());
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest7()
        {
            await InitCertRecords(_dbContext);
            long[] certIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
            List<Certificate> actual = await _certificateManager.Get(certIDs);
            Assert.Equal(certIDs.Length, actual.Count);
            foreach (Certificate cert in actual)
            {
                Assert.Contains(cert.ID, certIDs);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest5()
        {
            await InitCertRecords(_dbContext);
            var certs = this.GetCleanEnumerable<Certificate>(TestCertificates);
            var i = GetRndCertId();
            string owner = certs[i].Owner;
            string thumbprint = certs[i].Thumbprint;
            var expected = certs[i];
            var actual = await _certificateManager.Get(owner, thumbprint);
            Assert.Equal(expected.Owner, actual.Owner);
            Assert.Equal(expected.Thumbprint, actual.Thumbprint);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest4()
        {
            await InitCertRecords(_dbContext);
            long certId = GetRndCertId();
            var cert = await _certificateManager.Get(certId);
            Assert.NotNull(cert);
            Assert.Equal(certId, cert.ID);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest2()
        {
            await InitCertRecords(_dbContext);
            string owner = $"{BuildDomainName(GetRndDomainId())}";
            EntityStatus? status = EntityStatus.New;
            List<Certificate> actual = await _certificateManager.Get(owner, status);
            Assert.Equal(MaxCertPerOwner, actual.Count);
            foreach (Certificate cert in actual)
            {
                Assert.Equal(EntityStatus.New, cert.Status);
                Assert.Equal(owner, cert.Owner);
            }
            await _certificateManager.SetStatus(actual.First().ID, EntityStatus.Enabled);
            actual = await _certificateManager.Get(owner, status);
            Assert.Equal(MaxCertPerOwner - 1, actual.Count);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest1()
        {
            await InitCertRecords(_dbContext);
            string owner = $"{BuildDomainName(GetRndDomainId())}";
            List<Certificate> actual = await _certificateManager.Get(owner);
            Assert.Equal(MaxCertPerOwner, actual.Count);
            foreach (Certificate cert in actual)
            {
                Assert.Equal(owner, cert.Owner);
            }
        }
        
        /// <summary>
        ///A test for Add
        ///</summary>
        [Theory]
        [MemberData(nameof(TestCertificates))]
        public async Task AddTest2(Certificate cert)
        {
            await _certificateManager.Add(cert);
            Certificate certNew = await _certificateManager.Get(1); //---should always be 1 (table was truncated above);
            Assert.NotNull(cert);
            Assert.Equal(cert.Owner, certNew.Owner);
            Assert.Equal(cert.Thumbprint, certNew.Thumbprint);
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest1()
        {
            List<Certificate> certs = GetCleanEnumerable<Certificate>(TestCertificates);
            await _certificateManager.Add(certs);
            List<Certificate> actual = await _certificateManager.Get(0, MaxCertPerOwner * MaxDomainCount + 1);
            Assert.Equal(certs.Count, actual.Count);
        }

    }
}