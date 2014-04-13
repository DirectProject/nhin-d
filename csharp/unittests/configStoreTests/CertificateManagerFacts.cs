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
using System.Security.Cryptography.X509Certificates;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Config.Store.Tests
{
    public class CertificateManagerTestFixture : ConfigStoreTestBase, IDisposable
    {
        public CertificateManagerTestFixture()
        {
            InitCertRecords();
        }

        public void Dispose()
        {
            // Do "global" teardown here; Only called once.
        }
    }

    public class CertificateManagerFacts : ConfigStoreTestBase, IUseFixture<CertificateManagerTestFixture>
    {
        private static new CertificateManager CreateManager()
        {
            return new CertificateManager(CreateConfigStore());
        }

        public void SetFixture(CertificateManagerTestFixture data)
        {

        }

        /// <summary>
        /// property to expose enumerable testing Certificate instances
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static new IEnumerable<object[]> TestCertificates
        {
            get
            {
                return ConfigStoreTestBase.TestCertificates;
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
            CertificateManager mgr = CreateManager();
            ConfigStore actual = mgr.Store;
            Assert.Equal(mgr.Store, actual);
        }

        /// <summary>
        ///A test for Item
        ///</summary>
        [Fact, AutoRollback]
        public void ItemTest()
        {
            foreach (string domain in TestDomainNames)
            {

                string subject = "CN=" + domain;
                CertificateManager target = CreateManager();
                X509Certificate2Collection actual = target[subject];
                Dump(string.Format("ItemTest Subject[{0}] which has [{1}] related certs."
                                   , subject
                                   , actual==null?-1:actual.Count));
                Assert.NotNull(actual);
            }

        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact, AutoRollback]
        public void SetStatusTest4()
        {
            foreach (string domain in TestDomainNames)
            {

                string subject = "CN=" + domain;
                CertificateManager target = CreateManager();
                Certificate[] actual = target.Get(subject);
                Dump(string.Format("SetStatusTest4 Subject[{0}] which has [{1}] related certs."
                                   , subject
                                   , actual == null ? -1 : actual.Length));
                Assert.NotNull(actual);
                Assert.Equal(MAXCERTPEROWNER, actual.Length);
                foreach (Certificate cert in actual)
                {
                    Assert.Equal(EntityStatus.New, cert.Status);
                }

                target.SetStatus(subject, EntityStatus.Enabled);

                actual = target.Get(subject);
                Assert.NotNull(actual);
                Assert.Equal(MAXCERTPEROWNER, actual.Length);
                foreach (Certificate cert in actual)
                {
                    Assert.Equal(EntityStatus.Enabled, cert.Status);
                }
                
            }


            foreach (Certificate cert in GetCleanEnumerable<Certificate>(TestCertificates))
            {
                CertificateManager target = CreateManager();
                X509Certificate xcert = cert.ToX509Certificate();
                Certificate[] expected = target.Get(xcert.Subject);
                target.SetStatus(xcert.Subject, EntityStatus.Enabled);

            }
            

        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact, AutoRollback]
        public void SetStatusTest3()
        {
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                foreach (string domain in TestDomainNames)
                {

                    string subject = "CN=" + domain;
                    CertificateManager target = CreateManager();
                    Certificate[] actual = target.Get(subject);
                    Dump(string.Format("SetStatusTest3 Subject[{0}] which has [{1}] related certs."
                                       , subject
                                       , actual == null ? -1 : actual.Length));
                    Assert.NotNull(actual);
                    Assert.Equal(MAXCERTPEROWNER, actual.Length);
                    foreach (Certificate cert in actual)
                    {
                        Assert.Equal(EntityStatus.New, cert.Status);
                    }

                    target.SetStatus(db,subject, EntityStatus.Enabled);
                    db.SubmitChanges();
                    actual = target.Get(subject);
                    Assert.NotNull(actual);
                    Assert.Equal(MAXCERTPEROWNER, actual.Length);
                    foreach (Certificate cert in actual)
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
        public void SetStatusTest2()
        {
            for (long i = 1; i <= MAXCERTPEROWNER * MAXDOMAINCOUNT; i++)
            {
                CertificateManager target = CreateManager();
                Certificate cert = target.Get(i);
                Dump(string.Format("SetStatusTest1 Subject[{0}] Status:[{1}]"
                                   , cert == null ? "null cert" : cert.Owner
                                   , cert == null ? "null cert" : cert.Status.ToString()));
                Assert.Equal(EntityStatus.New, cert.Status);
                target.SetStatus(i, EntityStatus.Enabled);
                cert = target.Get(i);
                Assert.Equal(EntityStatus.Enabled, cert.Status);
            }
            
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact, AutoRollback]
        public void SetStatusTest1()
        {
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                for (long i = 1; i <= MAXCERTPEROWNER * MAXDOMAINCOUNT; i++)
                {
                    CertificateManager target = CreateManager();
                    Certificate cert = target.Get(i);
                    Dump(string.Format("SetStatusTest1 Subject[{0}] Status:[{1}]"
                                       , cert == null ? "null cert" : cert.Owner
                                       , cert == null ? "null cert" : cert.Status.ToString()));
                    Assert.Equal(EntityStatus.New, cert.Status);
                    target.SetStatus(db, i, EntityStatus.Enabled);
                    cert = target.Get(i);
                    Assert.Equal(EntityStatus.Enabled, cert.Status);
                }
            }

        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact, AutoRollback]
        public void SetStatusTest()
        {
            long t = 1;
            for (long i = 1; i <= MAXDOMAINCOUNT; i++)
            {
                List<long> ids = new List<long>(MAXCERTPEROWNER);
                while((t%MAXCERTPEROWNER) > 0)
                {
                    ids.Add(t);
                    t++;
                }
                ids.Add(t);
                t++;
                Dump(String.Format("SetStatusTest checking certs for {0}, found {1}", BuildDomainName(i), ids.Count));
                
                CertificateManager target = CreateManager();
                Certificate[] certs = target.Get(ids.ToArray());
                foreach (Certificate cert in certs)
                {
                    Dump(string.Format("\t - Subject[{0}] Status:[{1}] ID:[{2}]"
                                       , cert == null ? "null cert" : cert.Owner
                                       , cert == null ? "null cert" : cert.Status.ToString()
                                       , cert == null ? -1:cert.ID));
                    Assert.Equal(EntityStatus.New,cert.Status);
                }
                target.SetStatus(ids.ToArray(), EntityStatus.Enabled);
                certs = target.Get(ids.ToArray());
                foreach (Certificate cert in certs)
                {
                    Assert.Equal(EntityStatus.Enabled,cert.Status);
                }
        
            }

        }

        /// <summary>
        ///A test for RemoveAll
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveAllTest1()
        {
            CertificateManager target = CreateManager();
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
            CertificateManager target = CreateManager();
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
            CertificateManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());

            using (ConfigDatabase db = CreateConfigDatabase())
            {
                string ownerName = string.Format("CN={0}",BuildDomainName(1));
                target.Remove(db, ownerName);
            }
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());

        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest4()
        {
            CertificateManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            string ownerName = string.Format("CN={0}", BuildDomainName(1));
            target.Remove(ownerName);
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());

        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest3()
        {
            CertificateManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            long[] certificateIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
            target.Remove(certificateIDs);
            Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - certificateIDs.Length, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest2()
        {
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                CertificateManager target = CreateManager();
                Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
                long[] certificateIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
                target.Remove(db,certificateIDs);
                Assert.Equal(MAXDOMAINCOUNT * MAXCERTPEROWNER - certificateIDs.Length, target.Get(-1, MAXDOMAINCOUNT * MAXCERTPEROWNER + 1).Count());
            }
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest1()
        {
            CertificateManager target = CreateManager();
            const long certID = 1;
            Assert.NotNull(target.Get(certID));
            target.Remove(certID);
            Assert.Null(target.Get(certID));

        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact, AutoRollback]
        public void RemoveTest()
        {
            CertificateManager target = CreateManager();
            const long certID = 1;
            Assert.NotNull(target.Get(certID));
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                target.Remove(db,certID);
            }
            Assert.Null(target.Get(certID));

        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest10()
        {
            CertificateManager target = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                const long lastCertID = 0;
                const int maxResults = MAXCERTPEROWNER * MAXDOMAINCOUNT + 1;
                IEnumerable<Certificate> actual = target.Get(db, lastCertID, maxResults);
                Assert.Equal(MAXCERTPEROWNER * MAXDOMAINCOUNT, actual.Count());
            }

        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest9()
        {
            CertificateManager target = CreateManager();
            List<Certificate> certs = this.GetCleanEnumerable<Certificate>(TestCertificates);
            string owner = certs[GetRndCertID()].Owner;
            string thumbprint = certs[GetRndCertID()].Thumbprint;
            Certificate expected = certs[GetRndCertID()]; 
            Certificate actual = target.Get(owner, thumbprint);
            Assert.Equal(expected.Owner, actual.Owner);
            Assert.Equal(expected.Thumbprint, actual.Thumbprint);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest8()
        {
            CertificateManager target = CreateManager();
            const long lastCertID = 0;
            const int maxResults = MAXCERTPEROWNER * MAXDOMAINCOUNT + 1;
            IEnumerable<Certificate> actual = target.Get(lastCertID, maxResults);
            Assert.Equal(MAXCERTPEROWNER * MAXDOMAINCOUNT, actual.Count());
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest7()
        {
            CertificateManager target = CreateManager();
            long[] certIDs = new long[] { 1, 2, 3, 4, 5, 6, 7 };
            Certificate[] actual = target.Get(certIDs);
            Assert.Equal(certIDs.Length, actual.Length);
            foreach (Certificate cert in actual)
            {
                Assert.True(certIDs.Contains(cert.ID));
            }

        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest6()
        {
            CertificateManager target = CreateManager();
            long certID = GetRndCertID();

            using (ConfigDatabase db = CreateConfigDatabase())
            {
                Certificate cert = target.Get(db, certID);
                Assert.NotNull(cert);
                Assert.Equal(certID, cert.ID);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest5()
        {
            
            CertificateManager target = CreateManager();
            List<Certificate> certs = this.GetCleanEnumerable<Certificate>(TestCertificates);
            string owner = certs[GetRndCertID()].Owner;
            string thumbprint = certs[GetRndCertID()].Thumbprint;
            Certificate expected = certs[GetRndCertID()];
            Certificate actual = target.Get(owner, thumbprint);
            Assert.Equal(expected.Owner, actual.Owner);
            Assert.Equal(expected.Thumbprint, actual.Thumbprint);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest4(){
            
            CertificateManager target = CreateManager();
            long certID = GetRndCertID();
            Certificate cert = target.Get( certID);
            Assert.NotNull(cert);
            Assert.Equal(certID, cert.ID);

        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest3()
        {
            CertificateManager target = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                string owner = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
                EntityStatus? status = EntityStatus.New;
                Certificate[] actual  = target.Get(db, owner, status).ToArray();
                Assert.Equal(MAXCERTPEROWNER, actual.Count());
                foreach (Certificate cert in actual)
                {
                    Assert.Equal(EntityStatus.New, cert.Status);
                }
                target.SetStatus(actual.First().ID, EntityStatus.Enabled);
                actual = target.Get(db, owner, status).ToArray();
                Assert.Equal(MAXCERTPEROWNER - 1, actual.Count());
            }

        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest2()
        {
            CertificateManager target = CreateManager();
            string owner = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            EntityStatus? status = EntityStatus.New;
            Certificate[] actual = target.Get( owner, status).ToArray();
            Assert.Equal(MAXCERTPEROWNER, actual.Count());
            foreach (Certificate cert in actual)
            {
                Assert.Equal(EntityStatus.New, cert.Status);
                Assert.Equal(owner, cert.Owner);
            }
            target.SetStatus(actual.First().ID, EntityStatus.Enabled);
            actual = target.Get( owner, status).ToArray();
            Assert.Equal(MAXCERTPEROWNER - 1, actual.Count());
 
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact, AutoRollback]
        public void GetTest1()
        {
            CertificateManager target = CreateManager();
            string owner = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
            Certificate[] actual = target.Get(owner).ToArray();
            Assert.Equal(MAXCERTPEROWNER, actual.Count());
            foreach (Certificate cert in actual)
            {
                Assert.Equal(owner, cert.Owner);
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
                CertificateManager target = CreateManager();
                string owner = string.Format("CN={0}", BuildDomainName(GetRndDomainID()));
                Certificate[] actual = target.Get(db,owner).ToArray();
                Assert.Equal(MAXCERTPEROWNER, actual.Count());
                foreach (Certificate cert in actual)
                {
                    Assert.Equal(owner, cert.Owner);
                }
            }
        }
        
        /// <summary>
        ///A test for Add
        ///</summary>
        [Theory, AutoRollback]
        [PropertyData("TestCertificates")]
        public void AddTest2(Certificate cert)
        {
            CertificateManager target = CreateManager();
            target.RemoveAll();
            target.Add(cert);
            Certificate certNew = target.Get(1); //---should always be 1 (table was truncated above);
            Assert.NotNull(cert);
            Assert.Equal(cert.Owner, certNew.Owner);
            Assert.Equal(cert.Thumbprint, certNew.Thumbprint);

        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact, AutoRollback]
        public void AddTest1()
        {
            CertificateManager target = CreateManager();
            target.RemoveAll();
            List<Certificate> certs = GetCleanEnumerable<Certificate>(TestCertificates);
            target.Add(certs);
            Certificate[] actual = target.Get(0, MAXCERTPEROWNER * MAXDOMAINCOUNT + 1);
            Assert.Equal(certs.Count(), actual.Length);


        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Theory, AutoRollback]
        [PropertyData("TestCertificates")]
        public void AddTest(Certificate cert)
        {
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                CertificateManager target = CreateManager();
                target.RemoveAll();
                target.Add(db,cert);
                db.SubmitChanges();
                Certificate certNew = target.Get(1); //---should always be 1 (table was truncated above);
                Assert.NotNull(cert);
                Assert.Equal(cert.Owner, certNew.Owner);
                Assert.Equal(cert.Thumbprint, certNew.Thumbprint);
            }
        }


    }
}