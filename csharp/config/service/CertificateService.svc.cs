/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Activation;

using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    //[AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    public class CertificateService : ConfigServiceBase, ICertificateStore, IAnchorStore, IBundleStore
    {        
        #region ICertificateStore
        
        public Certificate AddCertificate(Certificate certificate)
        {
            try
            {
                return Store.Certificates.Add(certificate);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddCertificates", ex);
            }
        }

        public void AddCertificates(Certificate[] certificates)
        {
            if (certificates == null)
            {
                return;
            }
            
            try
            {
                Store.Certificates.Add(certificates);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddCertificates", ex);
            }
        }

        public Certificate GetCertificate(string owner, string thumbprint, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Store.Certificates.Get(owner, thumbprint), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetCertificate", ex);
            }
        }

        public Certificate[] GetCertificates(long[] certificateIDs, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Store.Certificates.Get(certificateIDs), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetCertificates", ex);
            }
        }

        public Certificate[] GetCertificatesForOwner(string owner, CertificateGetOptions options)
        {
            try
            {
                options = options ?? CertificateGetOptions.Default;
                return this.ApplyGetOptions(Store.Certificates.Get(owner, options.Status), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetCertificatesForOwner", ex);
            }
        }

        public Certificate[] EnumerateCertificates(long lastCertificateID, int maxResults, CertificateGetOptions options)
        {
            try
            {
                IEnumerable<Certificate> certs = Store.Certificates.Get(lastCertificateID, maxResults);
                return this.ApplyGetOptions(certs.ToArray(), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateCertificates", ex);
            }
        }

        public void SetCertificateStatus(long[] certificateIDs, EntityStatus status)
        {
            try
            {
                Store.Certificates.SetStatus(certificateIDs, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetCertificateStatus", ex);
            }
        }

        public void SetCertificateStatusForOwner(string owner, EntityStatus status)
        {
            try
            {
                Store.Certificates.SetStatus(owner, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetCertificateStatusForOwner", ex);
            }
        }

        public void RemoveCertificates(long[] certificateIDs)
        {
            try
            {
                Store.Certificates.Remove(certificateIDs);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveCertificates", ex);
            }
        }

        public void RemoveCertificatesForOwner(string owner)
        {
            try
            {
                Store.Certificates.Remove(owner);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveCertificatesForOwner", ex);
            }
        }
        
        #endregion
        
        #region IAnchorStore
        
        public Anchor AddAnchor(Anchor anchor)
        {
            try
            {
                return Store.Anchors.Add(anchor);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddAnchors", ex);
            }
        }

        public void AddAnchors(Anchor[] anchors)
        {
            try
            {
                Store.Anchors.Add(anchors);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddAnchors", ex);
            }
        }
        
        public Anchor GetAnchor(string owner, string thumbprint, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Store.Anchors.Get(owner, thumbprint), options);
            }
            catch(Exception ex)
            {
                throw CreateFault("GetAnchor", ex);
            }
        }
                      
        public Anchor[] GetAnchors(long[] anchorIDs, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Store.Anchors.Get(anchorIDs), options);
            }
            catch(Exception ex)
            {
                throw CreateFault("GetAnchors", ex);
            }
        }

        public Anchor[] GetAnchorsForOwner(string owner, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Store.Anchors.Get(owner), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAnchorsForOwner", ex);
            }
        }

        public Anchor[] GetIncomingAnchors(string owner, CertificateGetOptions options)
        {
            try
            {
                options = options ?? CertificateGetOptions.Default;
                return this.ApplyGetOptions(Store.Anchors.GetIncoming(owner, options.Status), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetIncomingAnchors", ex);
            }
        }

        public Anchor[] GetOutgoingAnchors(string owner, CertificateGetOptions options)
        {
            try
            {
                options = options ?? CertificateGetOptions.Default;
                return this.ApplyGetOptions(Store.Anchors.GetOutgoing(owner, options.Status), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetOutgoingAnchors", ex);
            }
        }

        public void SetAnchorStatus(long[] anchorIDs, EntityStatus status)
        {
            try
            {
                Store.Anchors.SetStatus(anchorIDs, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetAnchorStatusForOwner", ex);
            }
        }

        public void SetAnchorStatusForOwner(string owner, EntityStatus status)
        {
            try
            {
                Store.Anchors.SetStatus(owner, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetAnchorStatusForOwner", ex);
            }
        }

        public Anchor[] EnumerateAnchors(long lastAnchorID, int maxResults, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Store.Anchors.Get(lastAnchorID, maxResults), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateAnchors", ex);
            }
        }

        public void RemoveAnchors(long[] anchorIDs)
        {
            try
            {
                Store.Anchors.Remove(anchorIDs);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveAnchors", ex);
            }
        }

        public void RemoveAnchorsForOwner(string owner)
        {
            try
            {
                Store.Anchors.Remove(owner);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveAnchorsForOwner", ex);
            }
        }
        
        #endregion

        #region IBundleStore

        public Bundle AddBundle(Bundle bundle)
        {
            try
            {
                return Store.Bundles.Add(bundle);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddBundles", ex);
            }
        }

        public void AddBundles(Bundle[] bundles)
        {
            try
            {
                Store.Bundles.Add(bundles);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddBundles", ex);
            }
        }

        public Bundle GetBundle(long bundleID)
        {
            try
            {
                Bundle[] bundles = Store.Bundles.Get(new long[] { bundleID });
                return (bundles[0]);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetBundle", ex);
            }
        }

        public Bundle[] GetBundles(long[] bundleIDs)
        {
            try
            {
                return (Store.Bundles.Get(bundleIDs));
            }
            catch (Exception ex)
            {
                throw CreateFault("GetBundles", ex);
            }
        }

        public Bundle[] GetBundlesForOwner(string owner)
        {
            try
            {
                return (Store.Bundles.Get(owner));
            }
            catch (Exception ex)
            {
                throw CreateFault("GetBundlesForOwner", ex);
            }
        }

        public Bundle[] GetIncomingBundles(string owner, EntityStatus status)
        {
            try
            {
                return (Store.Bundles.GetIncoming(owner, status));
            }
            catch (Exception ex)
            {
                throw CreateFault("GetIncomingBundles", ex);
            }
        }

        public Bundle[] GetOutgoingBundles(string owner, EntityStatus status)
        {
            try
            {
                return (Store.Bundles.GetOutgoing(owner, status));
            }
            catch (Exception ex)
            {
                throw CreateFault("GetOutgoingBundles", ex);
            }
        }

        public void SetBundleStatus(long[] bundleIDs, EntityStatus status)
        {
            try
            {
                Store.Bundles.SetStatus(bundleIDs, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetBundleStatus", ex);
            }
        }

        public void SetBundleStatusForOwner(string owner, EntityStatus status)
        {
            try
            {
                Store.Bundles.SetStatus(owner, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetBundleStatusForOwner", ex);
            }
        }

        public Bundle[] EnumerateBundles(long lastBundleID, int maxResults)
        {
            try
            {
                return (Store.Bundles.Get(lastBundleID, maxResults));
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateBundles", ex);
            }
        }

        public void RemoveBundles(long[] bundleIDs)
        {
            try
            {
                Store.Bundles.Remove(bundleIDs);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveBundles", ex);
            }
        }

        public void RemoveBundlesForOwner(string owner)
        {
            try
            {
                Store.Bundles.Remove(owner);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveBundlesForOwner", ex);
            }
        }

        #endregion

        Certificate[] ApplyGetOptions(Certificate[] certs, CertificateGetOptions options)
        {
            if (certs == null)
            {
                return null;
            }
            
            return (from cert in (from cert in certs
                                  where cert != null
                                  select ApplyGetOptions(cert, options)
                                 )
                    where cert != null
                    select cert).ToArray();
        }
        
        Certificate ApplyGetOptions(Certificate cert, CertificateGetOptions options)
        {
            if (options == null)
            {
                options = CertificateGetOptions.Default;
            }
            
            return options.ApplyTo(cert);
        }

        Anchor[] ApplyGetOptions(Anchor[] anchors, CertificateGetOptions options)
        {
            if (anchors == null)
            {
                return null;
            }

            return (from anchor in
                        (from anchor in anchors
                         select ApplyGetOptions(anchor, options)
                        )
                    where anchor != null
                    select anchor).ToArray();
        }

        Anchor ApplyGetOptions(Anchor anchor, CertificateGetOptions options)
        {
            if (options == null)
            {
                options = CertificateGetOptions.Default;
            }

            return options.ApplyTo(anchor);
        }
    }
}