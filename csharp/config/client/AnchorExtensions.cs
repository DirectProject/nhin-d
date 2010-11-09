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
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Client
{
    public static class AnchorExtensions
    {        
        public static void AddAnchor(this AnchorStoreClient client, Anchor anchor)
        {
            client.AddAnchors(new Anchor[] {anchor});
        }
        
        public static bool Contains(this AnchorStoreClient client, string owner, X509Certificate2 certificate)
        {
            if (certificate == null)
            {
                throw new ArgumentNullException("certificate");
            }
            
            return client.Contains(owner, certificate.Thumbprint);
        }

        public static bool Contains(this AnchorStoreClient client, string owner, string thumbprint)
        {
            if (string.IsNullOrEmpty(owner))
            {
                throw new ArgumentException("value was null or empty", "owner");
            }
            if (string.IsNullOrEmpty(thumbprint))
            {
                throw new ArgumentException("value was null or empty", "thumbprint");
            }

            Anchor anchor = client.GetAnchor(owner, thumbprint, CertificateExtensions.CertInfo);
            return (anchor != null);
        }
        
        public static Anchor[] GetIncomingAnchors(this AnchorStoreClient client, string owner)
        {
            return client.GetIncomingAnchors(owner, CertificateExtensions.FullCertData);
        }

        public static Anchor[] GetIncomingAnchors(this AnchorStoreClient client, string owner, EntityStatus status)
        {
            CertificateGetOptions options = CertificateExtensions.FullCertData.Clone();
            options.Status = status;
            return client.GetIncomingAnchors(owner, options);
        }

        public static Anchor[] GetOutgoingAnchors(this AnchorStoreClient client, string owner)
        {
            return client.GetOutgoingAnchors(owner, CertificateExtensions.FullCertData);
        }

        public static Anchor[] GetOutgoingAnchors(this AnchorStoreClient client, string owner, EntityStatus status)
        {
            CertificateGetOptions options = CertificateExtensions.FullCertData.Clone();
            options.Status = status;
            return client.GetOutgoingAnchors(owner, options);
        }

        public static X509Certificate2Collection GetIncomingAnchorX509Certificates(this AnchorStoreClient client, string owner, EntityStatus status)
        {
            return Anchor.ToX509Collection(client.GetIncomingAnchors(owner, status));
        }

        public static X509Certificate2Collection GetOutgoingAnchorX509Certificates(this AnchorStoreClient client, string owner, EntityStatus status)
        {
            return Anchor.ToX509Collection(client.GetOutgoingAnchors(owner, status));
        }
        
        public static void RemoveAnchor(this AnchorStoreClient client, long anchorID)
        {
            client.RemoveAnchors(new long[] {anchorID});
        }
        
        public static IEnumerable<Anchor> EnumerateAnchors(this AnchorStoreClient client, int chunkSize)
        {
            return client.EnumerateAnchors(chunkSize, CertificateExtensions.FullCertData);
        }

        public static IEnumerable<Anchor> EnumerateAnchors(this AnchorStoreClient client, int chunkSize, CertificateGetOptions options)
        {
            if (options == null)
            {
                throw new ArgumentNullException("options");
            }

            if (chunkSize < 1)
            {
                throw new ArgumentException("value was less than 1", "chunkSize");
            }

            long lastID = -1;

            Anchor[] anchors;
            while (true)
            {
                anchors = client.EnumerateAnchors(lastID, chunkSize, options);
                if (anchors == null || anchors.Length == 0)
                {
                    yield break;
                }
                for (int i = 0; i < anchors.Length; ++i)
                {
                    yield return anchors[i];
                }
                lastID = anchors[anchors.Length - 1].ID;
            }
        }

        public static IEnumerable<X509Certificate2> EnumerateX509Certificates(this AnchorStoreClient client, int chunkSize)
        {
            foreach (Anchor cert in client.EnumerateAnchors(chunkSize, CertificateExtensions.FullCertData))
            {
                yield return cert.ToX509Certificate();
            }
        }
    }
}