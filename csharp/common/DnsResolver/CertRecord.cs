/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>A representation of CERT DNS RDATA</summary>
    /// <remarks>
    /// RFC 4398.
    ///
    /// Record format:
    /// <code>
    ///                     1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
    /// 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    /// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    /// |             type              |             key tag           |
    /// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    /// |   algorithm   |                                               /
    /// +---------------+            certificate or CRL                 /
    /// /                                                               /
    /// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-|
    /// </code>
    /// </remarks>
    public class CertRecord : DnsResourceRecord
    {

        /// <summary>
        /// Enumeration of the CERT RR supported certificate types
        /// </summary>
        /// <remarks>
        /// RFC 4398, Section 2.1
        /// </remarks>
        public enum CertificateType
        {
            /// <summary>
            /// Reserved certificate type.
            /// </summary>
            Reserved = 0,
            /// <summary>
            /// X509 certificate
            /// </summary>
            X509 = 1,
            /// <summary>
            /// SPKI certificate
            /// </summary>
            SPKI,
            /// <summary>
            /// OpenPGP certificate
            /// </summary>
            PGP,        // Open PGP
            /// <summary>
            /// URL to an X.509 data object 
            /// </summary>
            IPKIX,      
            /// <summary>
            ///  Url of an SPKI certificate
            /// </summary>
            ISPKI,
            /// <summary>
            /// fingerprint + URL of an OpenPGP packet
            /// </summary>
            IPGP,
            /// <summary>
            /// Attribute Certificate
            /// </summary>
            ACPKIX,
            /// <summary>
            /// The URL of an Attribute Certificate
            /// </summary>
            IACPKIK
        }       
        
        CertificateType m_certType;
        ushort m_keyTag;
        byte m_algorithm;
        byte[] m_certData;
        DnsX509Cert m_cert;
        
        internal CertRecord()
        {
        }
        
        /// <summary>
        /// Initializes an instance with the supplied certificate.
        /// </summary>
        /// <param name="cert">The certificate for this record.</param>
        public CertRecord(DnsX509Cert cert)
            : base(cert.Name, DnsStandard.RecordType.CERT)
        {
            this.Cert = cert;
            this.CertType = CertificateType.X509;
            this.KeyTag = cert.KeyTag;
            this.Algorithm = 5;  // RFC 4034
        }
        
        /// <summary>
        /// Gets and sets the certificate type
        /// </summary>
        /// <value>The CERT RR type of this certificate</value>
        public CertificateType CertType
        {
            get
            {
                return m_certType;
            }
            internal set
            {
                m_certType = value;
            }
        }
        
        /// <summary>
        /// Gets/sets the keyTag (see RFC 2535)
        /// </summary>
        public ushort KeyTag
        {
            get
            {
                return m_keyTag;
            }
            internal set
            {
                m_keyTag = value;
            }
        }
        
        /// <summary>
        /// Gets/sets the certificate algorithm (see RFC 4034, Appendix 1)
        /// </summary>
        public byte Algorithm
        {
            get
            {
                return m_algorithm;
            }
            internal set
            {
                m_algorithm = value;
            }
        }
        
        /// <summary>
        /// Gets/sets the raw certificate RDATA.
        /// </summary>
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
        
        /// <summary>
        /// Gets/sets the X509 DNS Cert instance associated with this record.
        /// </summary>
        /// <value>A <see cref="DnsX509Cert"/> instance, will be null if this is not an X509 RR</value>
        public DnsX509Cert Cert
        {
            get
            {
                this.EnsureDnsCert();                
                return m_cert;
            }          
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                
                m_cert = value;
                m_certData = value.GetData();
            }  
        }
        
        void EnsureDnsCert()
        {
            if (m_cert == null && m_certType == CertificateType.X509)
            {
                m_cert = new DnsX509Cert(m_certData, m_keyTag);
            }
        }

        /// <summary>
        /// Tests equality between this CERT record and the other <paramref name="record"/>.
        /// </summary>
        /// <param name="record">The other record.</param>
        /// <returns><c>true</c> if the RRs are equal, <c>false</c> otherwise.</returns>
        public override bool Equals(DnsResourceRecord record)
        {
            if (!base.Equals(record))
            {
                return false;
            }
            
            CertRecord certRecord = record as CertRecord;
            if (certRecord == null)
            {
                return false;
            }
            
            return (
                       this.m_algorithm == certRecord.m_algorithm
                       &&  this.m_certType == certRecord.m_certType
                       &&  this.m_keyTag == certRecord.m_keyTag
                       &&  DnsStandard.Equals(this.Cert.Name, certRecord.Cert.Name)
                   );
        }

        /// <summary>
        /// Writes this RR in DNS wire format to the <paramref name="buffer"/>
        /// </summary>
        /// <param name="buffer">The buffer to which DNS wire data are written</param>
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddUshort((ushort) m_certType);
            buffer.AddUshort(m_keyTag);
            buffer.AddByte(m_algorithm);
            buffer.AddBytes(m_certData);
        }

        /// <summary>
        /// Reads data into this RR from the DNS wire format data in <paramref name="reader"/>
        /// </summary>
        /// <param name="reader">Reader in which wire format data for this RR is already buffered.</param>
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
            m_certData = reader.ReadBytes(this.RecordDataLength - 5); // 5 == # of bytes we've already read (certType, keytag etc)
            
            this.EnsureDnsCert();            
        }
    }
}