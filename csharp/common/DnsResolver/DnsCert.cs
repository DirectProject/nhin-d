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
using System.Text;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Net;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Representation of an X509 cert in a DNS CERT RR.
    /// </summary>
    public class DnsX509Cert
    {
        X509Certificate2 m_cert;
        ushort m_keyTag;
        string m_name;
        int m_ttl;

        /// <summary>
        /// Creates a DnsX509Cert instance for an X509 certificate.
        /// </summary>
        /// <param name="certificate">A Base64 encoded DER representation of the certificate.</param>
        public DnsX509Cert(string certificate)
        {
            if (string.IsNullOrEmpty(certificate))
            {
                throw new ArgumentException("value was null or empty", "certificate");
            }

            // This will also create a key tag
            this.Certificate = new X509Certificate2(Convert.FromBase64String(this.NormalizeInputCertString(certificate)));
        }
        
        /// <summary>
        /// Creates a DnsX509Cert instance for an X509 certificate.
        /// </summary>
        /// <param name="certificate">A Base64 encoded DER representation of the certificate.</param>
        /// <param name="keyTag">The key tag for this certificate. See RFC 2535 for details.</param>
        public DnsX509Cert(string certificate, ushort keyTag)
            : this(certificate)
        {
            m_keyTag = keyTag;
        }

        /// <summary>
        /// Creates a DnsX509Cert instance for an X509 certificate.
        /// </summary>
        /// <param name="certificate">A byte array providing a DER representation of an X509 certificate.</param>
        public DnsX509Cert(byte[] certificate)
        {
            if (certificate == null || certificate.Length == 0)
            {
                throw new ArgumentException("value was null or empty", "certificate");
            }

            // This will also create a key tag
            this.Certificate = new X509Certificate2(certificate);
        }

        /// <summary>
        /// Creates a DnsX509Cert instance for an X509 certificate.
        /// </summary>
        /// <param name="certificate">A byte array providing a DER representation of an X509 certificate.</param>
        /// <param name="keyTag">The key tag for this certificate. See RFC 2535 for details.</param>
        public DnsX509Cert(byte[] certificate, ushort keyTag)
            : this(certificate)
        {
            m_keyTag = keyTag;
        }

        /// <summary>
        /// Creates a DnsX509Cert instance for an X509 certificate.
        /// </summary>
        /// <param name="cert">The <see cref="X509Certificate2"/> certificate instance.</param>
        public DnsX509Cert(X509Certificate2 cert)
        {
            this.Certificate = cert;
        }

        /// <summary>
        /// Gets and sets the <see cref="X509Certificate2"/> instance for this DNS RR.
        /// When setting, also updates the KeyTag to match the new certificate.
        /// </summary>
        /// <value>
        /// The <see cref="X509Certificate2"/> stored in this DnsCert RR.
        /// </value>
        public X509Certificate2 Certificate
        {
            get
            {
                return m_cert;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }

                m_cert = value;
                m_name = m_cert.ExtractName();
                if (string.IsNullOrEmpty(m_name))
                {
                    throw new NotSupportedException();
                }
                m_name = m_name.Replace('@', '.');
                this.ExtractTag();
            }
        }

        /// <summary>
        /// Gets the domain name for this RR.
        /// </summary>
        public string Name
        {
            get
            {
                return m_name;
            }
        }

        /// <summary>
        /// Gets the key tag (see RFC 2535) for this RR.
        /// </summary>
        public ushort KeyTag
        {
            get
            {
                return m_keyTag;
            }
        }

        /// <summary>
        /// Gets the TTL for this RR.
        /// </summary>
        public int TTL
        {
            get
            {
                return m_ttl;
            }
            set
            {
                m_ttl = value;
            }
        }
        
        /// <summary>
        /// Return a byte array containing the certificate exported as .DER (.CER) 
        /// </summary>
        /// <returns></returns>
        public byte[] GetData()
        {
            return m_cert.Export(X509ContentType.Cert);
        }
        
        /// <summary>
        /// Exports this record as a DNS CERT RR.
        /// </summary>
        /// <param name="dnsDomain">The domain name to use for the address.</param>
        /// <returns>A string representation of the DNS CERT RR.</returns>
        public string Export(string dnsDomain)
        {
            StringWriter writer = new StringWriter();
            this.Export(writer, dnsDomain);
            return writer.ToString();
        }

        /// <summary>
        /// Exports this record as a DNS CERT RR
        /// </summary>
        /// <param name="writer">The writer to which to export the RR.</param>
        /// <param name="dnsDomain">The domain name to use for the address.</param>
        public void Export(TextWriter writer, string dnsDomain)
        {
            if (writer == null)
            {
                throw new ArgumentNullException("writer");
            }
            if (string.IsNullOrEmpty(dnsDomain))
            {
                throw new ArgumentException("value was null or empty", "dnsDomain");
            }

            string exported = this.NormalizeOutputCertString(Convert.ToBase64String(m_cert.Export(X509ContentType.Cert)));
            string certName = m_name;
            if (!certName.EndsWith(dnsDomain))
            {
                certName = dnsDomain.ConstructEmailDnsDomainName(m_name);
            }
            
            if (m_ttl > 0)
            {
                writer.Write("{0}. {1} IN CERT 1 {2} 5 {3}", certName, m_ttl, (ushort)IPAddress.HostToNetworkOrder((short)m_keyTag), exported);
            }
            else
            {
                writer.Write("{0}. IN CERT 1 {1} 5 {2}", certName, (ushort)IPAddress.HostToNetworkOrder((short)m_keyTag), exported);
            }
        }

        void ExtractTag()
        {
            RSACryptoServiceProvider rsaProvider = m_cert.PublicKey.Key as RSACryptoServiceProvider;
            if (rsaProvider == null)
            {
                throw new NotSupportedException();
            }

            RSAParameters rsaParams = rsaProvider.ExportParameters(false);
            byte[] modulus = rsaParams.Modulus;

            m_keyTag = (ushort)((modulus[modulus.Length - 2] << 8) | (modulus[modulus.Length - 1]));
        }


        string NormalizeInputCertString(string cert)
        {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < cert.Length; ++i)
            {
                char ch = cert[i];
                if (ch != '(' && ch != ')' && !char.IsWhiteSpace(ch))
                {
                    builder.Append(ch);
                }
            }

            return builder.ToString();
        }

        string NormalizeOutputCertString(string cert)
        {
            StringBuilder builder = new StringBuilder();
            builder.AppendLine("(");
            for (int i = 0; i < cert.Length; ++i)
            {
                char ch = cert[i];
                if (i > 0 && (i % 28) == 0)
                {
                    builder.AppendLine();
                }
                builder.Append(ch);
            }
            builder.AppendLine();
            builder.AppendLine(")");
            return builder.ToString();
        }

    }
}