/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using System.Data.Linq;
using System.Data.Linq.Mapping;
using NHINDirect.Config.Store;

namespace NHINDirect.Config.Service
{    
    // NOTE: If you change the class name "CertificateStore" here, you must also update the reference to "CertificateStore" in Web.config.
    public class CertificateService : ICertificateStore, IAnchorStore
    {        
        #region ICertificateStore
        
        public void AddCertificates(Certificate[] certificates)
        {
            if (certificates == null)
            {
                return;
            }
            
            try
            {
                Service.Current.Store.Certificates.Add(certificates);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Certificate GetCertificate(string owner, string thumbprint, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Service.Current.Store.Certificates.Get(owner, thumbprint), options);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Certificate[] GetCertificates(long[] certificateIDs, CertificateGetOptions options)
        {
            try
            {
                Certificate[] certs = this.ApplyGetOptions(Service.Current.Store.Certificates.Get(certificateIDs), options);
                return certs;
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Certificate[] GetCertificatesForOwner(string owner, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Service.Current.Store.Certificates.Get(owner), options);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Certificate[] EnumerateCertificates(long lastCertificateID, int maxResults, CertificateGetOptions options)
        {
            try
            {
                IEnumerable<Certificate> certs = Service.Current.Store.Certificates.Get(lastCertificateID, maxResults);
                return this.ApplyGetOptions(certs.ToArray(), options);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void SetCertificateStatus(long[] certificateIDs, EntityStatus status)
        {
            try
            {
                Service.Current.Store.Certificates.SetStatus(certificateIDs, status);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void SetCertificateStatusForOwner(string owner, EntityStatus status)
        {
            try
            {
                Service.Current.Store.Certificates.SetStatus(owner, status);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void RemoveCertificates(long[] certificateIDs)
        {
            try
            {
                Service.Current.Store.Certificates.Remove(certificateIDs);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void RemoveCertificatesForOwner(string owner)
        {
            try
            {
                Service.Current.Store.Certificates.Remove(owner);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        #endregion
        
        #region IAnchorStore
        
        public void AddAnchors(Anchor[] anchors)
        {
            try
            {
                Service.Current.Store.Anchors.Add(anchors);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        public Anchor[] GetAnchors(long[] anchorIDs, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Service.Current.Store.Anchors.Get(anchorIDs), options);
            }
            catch(Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Anchor[] GetAnchorsForOwner(string owner, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Service.Current.Store.Anchors.Get(owner), options);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Anchor[] GetIncomingAnchors(string owner, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Service.Current.Store.Anchors.GetIncoming(owner), options);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Anchor[] GetOutgoingAnchors(string owner, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Service.Current.Store.Anchors.GetOutgoing(owner), options);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Anchor[] EnumerateAnchors(long lastAnchorID, int maxResults, CertificateGetOptions options)
        {
            try
            {
                return this.ApplyGetOptions(Service.Current.Store.Anchors.Get(lastAnchorID, maxResults), options);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void RemoveAnchors(long[] anchorIDs)
        {
            try
            {
                Service.Current.Store.Anchors.Remove(anchorIDs);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void RemoveAnchorsForOwner(string owner)
        {
            try
            {
                Service.Current.Store.Anchors.Remove(owner);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
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
