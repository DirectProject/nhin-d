/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Collections.Generic;
using System.IO;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.ResolverPlugins.Tests
{
    // +-------------------------------------------------------------------------------------------------+
    // | NOTE all of these tests require a config service running at http://localhost/ConfigService and  |
    // | that the "Patients-Test" bundle of the Direct Patient Community be accessible.                  |
    // +-------------------------------------------------------------------------------------------------+

    public class BundleResolverTests : System.IDisposable
    {
        public BundleResolverTests()
        {
            // System.Diagnostics.Debugger.Break();

            this.SetupServices();
            this.SetupTestBundles();
        }

        public void Dispose()
        {
            this.CleanupTestBundles();
            this.CleanupServices();
        }

        private void SetupTestBundles()
        {
            this.CleanupTestBundles();
            m_client.AddBundles(TestBundles);
            m_client.SetBundleStatusForOwner(BundleGoodSubject, EntityStatus.Enabled);
            m_client.SetBundleStatusForOwner(BundleBadSubject, EntityStatus.Enabled);
        }

        private void CleanupTestBundles()
        {
            m_client.RemoveBundlesForOwner(BundleGoodSubject);
            m_client.RemoveBundlesForOwner(BundleBadSubject);
        }

        private void SetupServices()
        {
            using (StringReader reader = new StringReader(BundleClientSettingsXml))
            {
                XmlSerializer serializer = new XmlSerializer(typeof(ClientSettings));
                m_clientSettings = (ClientSettings)serializer.Deserialize(reader);
                m_client = new BundleStoreClient(m_clientSettings.Binding, m_clientSettings.Endpoint);
            }

            using (StringReader reader2 = new StringReader(BundleResolverSettingsXml))
            {
                XmlSerializer serializer = new XmlSerializer(typeof(BundleResolverSettings));
                m_bundleResolverSettings = (BundleResolverSettings) serializer.Deserialize(reader2);
                m_bundleResolver = m_bundleResolverSettings.CreateResolver();
            }
        }

        private void CleanupServices()
        {
            m_client.Close();
        }

        [Fact] 
        public void EnsureTestBundlesExist()
        {
            List<Bundle> bundles = new List<Bundle>();
            bundles.AddRange(m_client.GetBundlesForOwner(BundleGoodSubject));
            bundles.AddRange(m_client.GetBundlesForOwner(BundleBadSubject));

            Assert.Equal(bundles.Count, TestBundles.Length);

            foreach (Bundle bundleFound in bundles)
            {
                bool found = false;
                foreach (Bundle bundleTest in TestBundles)
                {
                    if (this.BundlesMatch(bundleFound, bundleTest))
                    {
                        found = true;
                        break;
                    }
                }
                Assert.True(found);
            }
        }

        [Fact]
        public void LoadCertsFromGoodIncomingBundle()
        {
            // System.Diagnostics.Debugger.Break();

            X509Certificate2Collection certs =
                m_bundleResolver.IncomingAnchors.GetCertificatesForDomain(BundleGoodSubject);

            Assert.True(certs.Count > 0);
        }

        [Fact]
        public void LoadCertsFromBadOutgoingBundle()
        {
            // System.Diagnostics.Debugger.Break();

            X509Certificate2Collection certs = null;

            Assert.DoesNotThrow(delegate
            {
                certs = m_bundleResolver.OutgoingAnchors.GetCertificatesForDomain(BundleBadSubject);
            });

            Assert.True(certs == null || certs.Count == 0);
        }

        private bool BundlesMatch(Bundle bundle1, Bundle bundle2)
        {
            // note we don't match everything... e.g., create date and such
            if ((bundle1.ForIncoming == bundle2.ForIncoming) &&
                (bundle1.ForOutgoing == bundle2.ForOutgoing) &&
                (bundle1.Owner == bundle2.Owner) &&
                (bundle1.Url == bundle2.Url))
            {
                return (true);
            }

            return (false);
        }

        private ClientSettings m_clientSettings;
        private BundleStoreClient m_client;

        private BundleResolverSettings m_bundleResolverSettings;
        private ITrustAnchorResolver m_bundleResolver;
        
        #region data

        public const string BundleGoodSubject = "direct.example.org";
        public const string BundleBadSubject = "direct.example-bogus.org";

        public const string BundleGoodUrl = "https://secure.bluebuttontrust.org/p7b.ashx?id=4d9daaf9-384a-e211-8bc3-78e3b5114607";
        public const string BundleBadUrl = "https://secure.bluebuttontrust-bogus.org/banana";

        public static Bundle GoodBundleIncoming = new Bundle(BundleGoodSubject, BundleGoodUrl, true, false);
        public static Bundle GoodBundleOutgoing = new Bundle(BundleGoodSubject, BundleGoodUrl, false, true);
        public static Bundle GoodBundleBidi = new Bundle(BundleGoodSubject, BundleGoodUrl, true, true);
        public static Bundle BadBundleBidirectional = new Bundle(BundleBadSubject, BundleBadUrl, true, true);

        public static Bundle[] TestBundles = { GoodBundleIncoming, GoodBundleOutgoing, GoodBundleBidi, BadBundleBidirectional };
    
        public const string BundleClientSettingsXml = @"
            <ClientSettings>
              <Url>http://localhost/ConfigService/CertificateService.svc/Bundles</Url>
            <!--
            <Url>http://localhost:6692/CertificateService.svc/Bundles</Url>
            -->
            </ClientSettings>
            ";

        public const string BundleResolverSettingsXml = @"
            <BundleResolver>
              <ClientSettings>
              <Url>http://localhost/ConfigService/CertificateService.svc/Bundles</Url>
                <!--
                <Url>http://localhost:6692/CertificateService.svc/Bundles</Url>
                -->
              </ClientSettings>
              <CacheSettings>
                <Cache>true</Cache>
                <NegativeCache>true</NegativeCache>
                <CacheTTLSeconds>60</CacheTTLSeconds>
              </CacheSettings>
              <MaxRetries>1</MaxRetries>
              <Timeout>30000</Timeout> <!-- In milliseconds -->
              <VerifySSL>true</VerifySSL>
            </BundleResolver>
            ";

        #endregion

    }
}
