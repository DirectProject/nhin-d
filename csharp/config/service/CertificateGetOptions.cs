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
using System.Runtime.Serialization;

using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    [DataContract(Namespace = Service.Namespace)]
    public class CertificateGetOptions
    {
        internal static CertificateGetOptions Default = new CertificateGetOptions();

        bool m_includeData;
        bool m_includePrivateKey;

        [DataMember]
        public bool IncludeData
        {
            get
            {
                return m_includeData;
            }
            set
            {
                m_includeData = value;
            }
        }

        [DataMember]
        public bool IncludePrivateKey
        {
            get
            {
                return m_includePrivateKey;
            }
            set
            {
                m_includePrivateKey = value;
            }
        }
        
        [DataMember]
        public EntityStatus? Status
        {
            get;
            set;
        }
        
        internal Certificate ApplyTo(Certificate cert)
        {
            if (cert == null)
            {
                return cert;
            }
            
            if (this.Status != null && Status.Value != cert.Status)
            {
                return null;
            }
            
            if (!this.IncludeData)
            {
                cert.ClearData();
            }
            else if (!this.IncludePrivateKey)
            {
                cert.ExcludePrivateKey();
            }

            return cert;
        }

        internal Anchor ApplyTo(Anchor anchor)
        {
            if (anchor == null)
            {
                return anchor;
            }

            if (this.Status != null && Status.Value != anchor.Status)
            {
                return null;
            }
            
            if (!this.IncludeData)
            {
                anchor.ClearData();
            }

            return anchor;
        }
    }
}