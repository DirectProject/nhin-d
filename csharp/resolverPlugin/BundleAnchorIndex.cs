/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
    Umesh Madan     umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Pkcs;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Common.Caching;
using Health.Direct.Common.Certificates;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// Retrieves configured bundles for a given subject
    /// </summary>
    public class BundleAnchorIndex : IX509CertificateIndex
    {
        /// <summary>
        /// Construct a new BundleAnchorIndex
        /// </summary>
        /// <param name="settings">Construct index based on these settings</param>
        /// <param name="incoming">Retrieve incoming or outgoing anchors</param>
        public BundleAnchorIndex(BundleResolverSettings settings, bool incoming)
        {
            m_clientSettings = settings.ClientSettings;
            m_incoming = incoming;
            m_downloader = new AnchorBundleDownloader();
            m_downloader.VerifySSL = settings.VerifySSL;
            m_downloader.TimeoutMS = settings.TimeoutMilliseconds;
            m_downloader.MaxRetries = settings.MaxRetries;
        }

        /// <summary>
        /// Returns the certificates in a bundle for the given subject name
        /// </summary>
        /// <param name="subjectName">Return certificates associated with this name</param>
        /// <returns>Collection of certificates. Null or empty if none found</returns>
        public X509Certificate2Collection this[string subjectName]
        {
            get
            {
                X509Certificate2Collection certs = new X509Certificate2Collection();

                Bundle[] bundles = this.GetBundlesForSubject(subjectName);
                foreach (Bundle bundle in bundles)
                    this.AddAnchorsForBundle(bundle, certs);

                return (certs);
            }
        }

        /// <summary>
        /// Subscribe to get error notifications
        /// </summary>
        public event Action<Exception> Error;

        private void AddAnchorsForBundle(Bundle bundle, X509Certificate2Collection certs)
        {
            try
            {
                X509Certificate2Collection bundleCerts = m_downloader.DownloadCertificates(bundle.Uri);
                if (!bundleCerts.IsNullOrEmpty())
                {
                    certs.Add(bundleCerts);
                }
            }
            catch (Exception e)
            {
                this.NotifyError(bundle, e);
            }
        }

        private Bundle[] GetBundlesForSubject(string subjectName)
        {
            Bundle[] bundles = null;

            using (BundleStoreClient client = this.CreateClient())
            {
                if (m_incoming)
                    bundles = client.GetIncomingBundles(subjectName, EntityStatus.Enabled);
                else
                    bundles = client.GetOutgoingBundles(subjectName, EntityStatus.Enabled);
            }

            return (bundles);
        }

        private BundleStoreClient CreateClient()
        {
            return (new BundleStoreClient(m_clientSettings.Binding, m_clientSettings.Endpoint));
        }

        private void NotifyError(Bundle bundle, Exception e)
        {
            if (this.Error != null  )
            {
                this.Error(e);
            }
            EventLogHelper.WriteWarning(null,
                                       string.Format("BundleResolver: Failed pulling certs from bundle URL: {0}, [{1}]", bundle.Url, e)
                                       );
        }

        private ClientSettings m_clientSettings;
        private bool m_incoming;
        private AnchorBundleDownloader m_downloader;
    }
}
