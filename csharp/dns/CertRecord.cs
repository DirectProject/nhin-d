/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
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

namespace DnsResolver
{
    //                 1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
    // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // |             type              |             key tag           |
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // |   algorithm   |                                               /
    // +---------------+            certificate or CRL                 /
    // /                                                               /
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-|
    public class CertRecord : DnsResourceRecord
    {
        //
        // For Cert Records
        //
        public enum CertificateType
        {
            Reserved = 0,
            X509 = 1,
            SPKI,
            PGP,        // Open PGP
            IPKIX,      // Url to an X509 data object
            ISPKI,      // Url of n SPKI certificate
            IPGP,       // fingerprint + URL of an OpenPGP packet
            ACPKIX,     // Attribute cert 
            IACPKIK
        }       
        
        CertificateType m_certType;
        ushort m_keyTag;
        byte m_algorithm;
        byte[] m_certData;
        DNSCert m_cert;
        
        internal CertRecord()
        {
        }
        
        public CertificateType CertType
        {
            get
            {
                return m_certType;
            }
            set
            {
                m_certType = value;
            }
        }
        
        public ushort KeyTag
        {
            get
            {
                return m_keyTag;
            }
            set
            {
                m_keyTag = value;
            }
        }
        
        public byte Algorithm
        {
            get
            {
                return m_algorithm;
            }
            set
            {
                m_algorithm = value;
            }
        }
        
        public byte[] Data
        {
            get
            {
                return m_certData;
            }
            set
            {
                m_certData = value;
                m_cert = null;
            }
        }
        
        public DNSCert Cert
        {
            get
            {
                this.EnsureDnsCert();                
                return m_cert;
            }          
            set
            {
                m_cert = value;
            }  
        }
        
        void EnsureDnsCert()
        {
            if (m_cert == null && m_certType == CertificateType.X509)
            {
                m_cert = new DNSCert(m_keyTag, m_certData);
            }
        }
        
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            ushort certType = reader.ReadUShort();
            if (certType > (ushort) CertificateType.IACPKIK)
            {
                throw new DnsProtocolException(DnsProtocolError.InvalidCertRecord);
            } 
            m_certType = (CertificateType) certType;
            m_keyTag = reader.ReadUShort();
            m_algorithm = reader.ReadByte();
            m_certData = reader.ReadBytes(this.RecordDataLength - 5);
            
            this.EnsureDnsCert();            
        }
    }
}
