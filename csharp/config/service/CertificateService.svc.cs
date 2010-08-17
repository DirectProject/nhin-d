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
        public void AddCertificate(Certificate certificate)
        {
            try
            {
                Service.Current.Store.Certificates.Add(certificate);
            }
            catch(ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
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
                Service.Current.Store.Certificates.Add(certificates);
            }
            catch(ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }

        public Certificate GetCertificate(string owner, string thumbprint)
        {
            try
            {
                return Service.Current.Store.Certificates.Get(owner, thumbprint);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }

        public Certificate[] GetCertificates(string owner)
        {
            try
            {
                return Service.Current.Store.Certificates.Get(owner);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }            
        }
        
        public Certificate[] EnumerateCertificates(long lastCertificateID, int maxResults)
        {
            try
            {
                IEnumerable<Certificate> certs = Service.Current.Store.Certificates.Get(lastCertificateID, maxResults);
                return certs.ToArray();
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }            
        }


        public void RemoveCertificate(string owner, string thumbprint)
        {
            try
            {
                Service.Current.Store.Certificates.Remove(owner, thumbprint);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }

        public void RemoveCertificates(string owner)
        {
            try
            {
                Service.Current.Store.Certificates.Remove(owner);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }
        
        public void AddAnchor(Anchor anchor)
        {
            try
            {
                Service.Current.Store.Anchors.Add(anchor);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }

        public void AddAnchors(Anchor[] anchors)
        {
            try
            {
                Service.Current.Store.Anchors.Add(anchors);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }            
        }

        public Anchor[] GetAnchors(string owner)
        {
            try
            {
                return Service.Current.Store.Anchors.Get(owner).ToArray();
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }            
        }

        public Anchor[] GetIncomingAnchors(string owner)
        {
            try
            {
                return Service.Current.Store.Anchors.GetIncoming(owner);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }            
        }

        public Anchor[] GetOutgoingAnchors(string owner)
        {
            try
            {
                return Service.Current.Store.Anchors.GetOutgoing(owner);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }

        public Anchor[] EnumerateAnchors(long lastAnchorID, int maxResults)
        {
            try
            {
                return Service.Current.Store.Anchors.Get(lastAnchorID, maxResults);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }

        public void RemoveAnchor(string owner, string thumbprint)
        {
            try
            {
                Service.Current.Store.Anchors.Remove(owner, thumbprint);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }

        public void RemoveAnchors(string owner)
        {
            try
            {
                Service.Current.Store.Anchors.Remove(owner);
            }
            catch (ConfigStoreException ex)
            {
                throw new FaultException<ConfigStoreFault>(ex.ToFault());
            }
        }
    }
}
