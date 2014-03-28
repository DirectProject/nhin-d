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

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Config.Store.Tests
{
    public class AnchorManagerTestFixture : ConfigStoreTestBase, IDisposable
    {
        public AnchorManagerTestFixture()
        {
            InitAnchorRecords();
        }

        public void Dispose()
        {
            // Do "global" teardown here; Only called once.
        }
    }

    public class AnchorManagerFacts : ConfigStoreTestBase, IUseFixture<AnchorManagerTestFixture>
    {
        private static new AnchorManager CreateManager()
        {
            return new AnchorManager(CreateConfigStore());
        }

        public void SetFixture(AnchorManagerTestFixture data)
        {

        }
        
        /// <summary>
        /// property to expose enumerable testing Anchor certificate instances
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static new IEnumerable<object[]> TestAnchors
        {
            get
            {
                return ConfigStoreTestBase.TestAnchors;
            }
        }

        /// <summary>
        /// property to expose enumerable test certs extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static new IEnumerable<object[]> TestCerts
        {
            get
            {
                return ConfigStoreTestBase.TestCerts;

            }
        }

        /// <summary>
        /// property to expose enumerable test cert bytpes extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static new IEnumerable<object[]> TestCertsBytes
        {
            get
            {
                return ConfigStoreTestBase.TestCertsBytes;

            }
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact, AutoRollback]
        public void StoreTest()
        {
            AnchorManager mgr = CreateManager();
            ConfigStore actual = mgr.Store;
            Assert.Equal(mgr.Store, actual);
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact, AutoRollback]
        public void SetStatusTest1()
        {
            
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                foreach (string domain in TestDomainNames)
                {

                    string subject = "CN=" + domain;
                    AnchorManager target = CreateManager();
                    Anchor[] actual = target.Get(subject);
                    Dump(string.Format("SetStatusTest1 Subject[{0}] which has [{1}] related certs."
                                       , subject
                                       , actual == null ? -1 : actual.Length));
                    Assert.NotNull(actual);
                    Assert.Equal(MAXCERTPEROWNER, actual.Length);
                    foreach (Anchor cert in actual)
                    {
                        Assert.Equal(EntityStatus.New, cert.Status);
                    }

                    target.SetStatus(db, subject, EntityStatus.Enabled);
                    db.SubmitChanges();
                    actual = target.Get(subject);
                    Assert.NotNull(actual);
                    Assert.Equal(MAXCERTPEROWNER, actual.Length);
                    foreach (Anchor cert in actual)
                    {
                        Assert.Equal(EntityStatus.Enabled, cert.Status);
                    }

                }
            }
           
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact, AutoRollback]
        public void SetStatusTest()
        {
            
            foreach (string domain in TestDomainNames)
            {
                string subject = "CN=" + domain;
                AnchorManager target = CreateManager();
                Anchor[] actual = target.Get(subject);
                Dump(string.Format("SetStatusTest1 Subject[{0}] which has [{1}] related certs."
                                   , subject
                                   , actual == null ? -1 : actual.Length));
                Assert.NotNull(actual);
                Assert.Equal(MAXCERTPEROWNER, actual.Length);
                foreach (Anchor cert in actual)
                {
                    Assert.Equal(EntityStatus.New, cert.Status);
                }

                target.SetStatus(subject, EntityStatus.Enabled);
                actual = target.Get(subject);
                Assert.NotNull(actual);
                Assert.Equal(MAXCERTPEROWNER, actual.Length);
                foreach (Anchor cert in actual)
                {
                    Assert.Equal(EntityStatus.Enabled, cert.Status);
                }

            }
    
            
        }

        /// <summary>
        ///A test for RemoveAll
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveAllTest1()
        {
            
            AnchorManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                target.RemoveAll(db);
            }
            Assert.Equal(0, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());

        }

        /// <summary>
        ///A test for RemoveAll
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveAllTest()
        {
            
            AnchorManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());

            target.RemoveAll();

            Assert.Equal(0, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());

        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest5()
        {
            
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                AnchorManager target = CreateManager();
                Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
                long[] certificateIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
                target.Remove(db, certificateIDs);
                Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - certificateIDs.Length, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            }
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest4()
        {
            
            AnchorManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            long[] certificateIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
            target.Remove(certificateIDs);
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - certificateIDs.Length, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest3()
        {
            
            AnchorManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            string ownerName = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            target.Remove(ownerName);
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest2()
        {
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                
                AnchorManager target = CreateManager();
                Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
                string ownerName = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
                target.Remove(db,ownerName);
                db.SubmitChanges();
                Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            }
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest1()
        {
            
            AnchorManager target = CreateManager();
            List<Anchor> certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
            string owner = certs[0].Owner;
            string thumbprint = certs[0].Thumbprint;
            Assert.NotNull(target.Get(owner, thumbprint));
            target.Remove(owner, thumbprint);
            Assert.Null(target.Get(owner, thumbprint));
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest()
        {
            
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                AnchorManager target = CreateManager();
                List<Anchor> certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
                string owner = certs[0].Owner;
                string thumbprint = certs[0].Thumbprint;
                Assert.NotNull(target.Get(owner, thumbprint));
                target.Remove(db,owner, thumbprint);
                Assert.Null(target.Get(owner, thumbprint));
            }
        }

        /// <summary>
        ///A test for GetOutgoing
        ///</summary>
        [Fact, AutoRollback]
        public void GetOutgoingTest1()
        {
            
            AnchorManager target = CreateManager();
            string ownerName = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            target.SetStatus(ownerName, EntityStatus.Enabled);
            Anchor[] actual = target.GetOutgoing(ownerName, null);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);
            actual = target.GetOutgoing(ownerName, EntityStatus.Enabled);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);
            actual = target.GetOutgoing(ownerName, EntityStatus.New);
            Assert.Equal(0, actual.Length);
        }

        /// <summary>
        ///A test for GetOutgoing
        ///</summary>
        [Fact, AutoRollback]
        public void GetOutgoingTest()
        {
            
            AnchorManager target = CreateManager();
            string ownerName = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            Anchor[] actual = target.GetOutgoing(ownerName);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);
            target.SetStatus(ownerName, EntityStatus.Enabled);
            actual = target.GetOutgoing(ownerName);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);

            
        }

        /// <summary>
        ///A test for GetIncoming
        ///</summary>
        [Fact, AutoRollback]
        public void GetIncomingTest1()
        {
            
            AnchorManager target = CreateManager();
            string ownerName = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            target.SetStatus(ownerName, EntityStatus.Enabled);
            Anchor[] actual = target.GetIncoming(ownerName, null);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);
            actual = target.GetIncoming(ownerName, EntityStatus.Enabled);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);
            actual = target.GetIncoming(ownerName, EntityStatus.New);
            Assert.Equal(0, actual.Length);
            
        }

        /// <summary>
        ///A test for GetIncoming
        ///</summary>
        [Fact, AutoRollback]
        public void GetIncomingTest()
        {
            
            AnchorManager target = CreateManager();
            string ownerName = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            Anchor[] actual = target.GetIncoming(ownerName);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);
            target.SetStatus(ownerName, EntityStatus.Enabled);
            actual = target.GetIncoming(ownerName);
            Assert.Equal(MAXCERTPEROWNER, actual.Length);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest6()
        {
            
            AnchorManager target = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                const long lastCertID = 0;
                const int maxResults = MAXCERTPEROWNER * MAXDOMAINCOUNT + 1;
                IEnumerable<Anchor> actual = target.Get(db, lastCertID, maxResults);
                Assert.Equal(MAXCERTPEROWNER * MAXDOMAINCOUNT, actual.Count());
            }
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest5()
        {
            
            AnchorManager target = CreateManager();
            const long lastCertID = 0;
            const int maxResults = MAXCERTPEROWNER * MAXDOMAINCOUNT + 1;
            IEnumerable<Anchor> actual = target.Get(lastCertID, maxResults);
            Assert.Equal(MAXCERTPEROWNER * MAXDOMAINCOUNT, actual.Count());
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest4()
        {
            
            AnchorManager target = CreateManager();
            long[] certIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
            Anchor[] actual = target.Get(certIDs);
            Assert.Equal(certIDs.Length, actual.Length);
            foreach (Anchor cert in actual)
            {
                Assert.True(certIDs.Contains(cert.ID));
            }
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest3()
        {
            
            AnchorManager target = CreateManager();
            string owner = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            Anchor[] actual = target.Get(owner).ToArray();
            Assert.Equal(MAXCERTPEROWNER, actual.Count());
            foreach (Anchor cert in actual)
            {
                Assert.Equal(owner, cert.Owner);
            }
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest2()
        {
            
            AnchorManager target = CreateManager();
            List<Anchor> certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
            string owner = certs[GetRndCertID()].Owner;
            string thumbprint = certs[GetRndCertID()].Thumbprint;
            Anchor expected = certs[GetRndCertID()];
            Anchor actual = target.Get(owner, thumbprint);
            Assert.Equal(expected.Owner, actual.Owner);
            Assert.Equal(expected.Thumbprint, actual.Thumbprint);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest1()
        {
            
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                AnchorManager target = CreateManager();
                List<Anchor> certs = this.GetCleanEnumerable<Anchor>(TestAnchors);
                string owner = certs[GetRndCertID()].Owner;
                string thumbprint = certs[GetRndCertID()].Thumbprint;
                Anchor expected = certs[GetRndCertID()];
                Anchor actual = target.Get(db, owner, thumbprint);
                Assert.Equal(expected.Owner, actual.Owner);
                Assert.Equal(expected.Thumbprint, actual.Thumbprint);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest()
        {
            
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                AnchorManager target = CreateManager();
                string owner = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
                Anchor[] actual = target.Get(db,owner).ToArray();
                Assert.Equal(MAXCERTPEROWNER, actual.Count());
                foreach (Anchor cert in actual)
                {
                    Assert.Equal(owner, cert.Owner);
                }
            }
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Theory, AutoRollback]
        [PropertyData("TestAnchors")]
        public void AddTest2(Anchor anc)
        {
            AnchorManager target = CreateManager();
            target.RemoveAll();
            target.Add(anc);
            Anchor certNew = target.Get(anc.Owner, anc.Thumbprint); //---should always be 1 (table was truncated above);
            Assert.NotNull(anc);
            Assert.Equal(anc.Owner, certNew.Owner);
            Assert.Equal(anc.Thumbprint, certNew.Thumbprint);

        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact, AutoRollback]
        public void AddTest1()
        {
            AnchorManager target = CreateManager();
            target.RemoveAll();
            List<Anchor> certs = GetCleanEnumerable<Anchor>(TestAnchors);
            target.Add(certs);
            Anchor[] actual = target.Get(0, MAXCERTPEROWNER * MAXDOMAINCOUNT + 1);
            Assert.Equal(certs.Count(), actual.Length);
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Theory, AutoRollback]
        [PropertyData("TestAnchors")]
        public void AddTest(Anchor anc)
        {
            AnchorManager target = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                target.RemoveAll();
                target.Add(db,anc);
                db.SubmitChanges();
                Anchor certNew = target.Get(anc.Owner, anc.Thumbprint); //---should always be 1 (table was truncated above);
                Assert.NotNull(anc);
                Assert.Equal(anc.Owner, certNew.Owner);
                Assert.Equal(anc.Thumbprint, certNew.Thumbprint);
            }
        }
    }
}