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
using System.Text;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using NHINDirect;

namespace NHINDirect.Agent
{
    public class NHINDAddress : MailAddress
    {
        AddressSource m_source = AddressSource.Unknown;
        X509Certificate2Collection m_certificates;
        TrustEnforcementStatus m_trustStatus;
        X509Certificate2Collection m_trustAnchors;
        
        internal NHINDAddress(string address, AddressSource source)
            : this(address)
        {
            m_source = source;
        }
                
        public NHINDAddress(string address)
            : this(address, null)
        {
        }

        public NHINDAddress(MailAddress address)
            : this(address.ToString())
        {
        }
        
        public NHINDAddress(string address, X509Certificate2Collection certificates)
            : base(address)
        {            
            this.m_certificates = certificates;
        }
                
        public X509Certificate2Collection Certificates
        {
            get
            {
                return this.m_certificates;
            }
            set
            {
                this.m_certificates = value;
            }
        }
                
        public bool HasCertificates
        {
            get
            {
                return (this.m_certificates != null && this.m_certificates.Count > 0);
            }
        }
        
        public X509Certificate2Collection TrustAnchors
        {
            get
            {
                return this.m_trustAnchors;
            }
            set
            {
                this.m_trustAnchors = value;
            }
        }
        
        public bool HasTrustAnchors
        {
            get
            {
                return (this.m_trustAnchors != null && this.m_trustAnchors.Count > 0);
            }
        }
        
        public TrustEnforcementStatus Status
        {
            get
            {
                return this.m_trustStatus;
            }
            set
            {
                this.m_trustStatus = value;
            }
        }

        internal AddressSource Source
        {
            get
            {
                return m_source;
            }
            set
            {
                m_source = value;
            }
        }
        
        public bool IsTrusted(TrustEnforcementStatus minTrustStatus)
        {
            return (this.m_trustStatus >= minTrustStatus);
        }        
    }
    
    internal enum AddressSource
    {
        Unknown,
        RcptTo,
        MailFrom,
        To,
        CC,
        BCC,
        From
    }
}
