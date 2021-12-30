﻿/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook       Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;

namespace Health.Direct.Config.Store.Entity
{
    public class Certificate
    {
        public const int MaxOwnerLength = 400;
        public const X509KeyStorageFlags KeyStorageFlags = (X509KeyStorageFlags.Exportable);
        
        string m_owner;
        byte[] m_data;
                
        public Certificate()
        {
        }
        
        public Certificate(string owner, byte[] sourceFileBytes, string password)
            : this(owner, Import(sourceFileBytes, password))
        {
        }
        
        public Certificate(string owner, X509Certificate2 certificate)
            : this(owner, certificate, true)
        {
        }

        public Certificate(string owner, X509Certificate2 certificate, bool includePrivateKey)
        {
            if (certificate == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidX509Certificate);
            }

            this.Owner = owner;
            this.SetX509Certificate(certificate, includePrivateKey);
        }

        public string Owner
        {
            get
            {
                return m_owner;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
                }

                if (value.Length > MaxOwnerLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.OwnerLength);
                }
                
                m_owner = value;
            }
        }
        
        public string Thumbprint
        {
            get;
            set;
        }

        
        public long ID
        {
            get;
            set;
        }

        public DateTime CreateDate
        {
            get;
            set;
        }

        public byte[] Data
        {
            get
            {
                return m_data;
            }
            set
            {                
                m_data = value;
            }
        }

        public DateTime ValidStartDate
        {
            get;
            set;
        }

        public DateTime ValidEndDate
        {
            get;
            set;
        }

        public EntityStatus Status
        {
            get;
            set;
        }
        
        public bool HasData
        {
            get
            {
                return (this.Data != null && this.Data.Length > 0);
            }
        }                              
        
        public bool IsValid(DateTime testDate)
        {
            return (this.ValidStartDate <= testDate && testDate <= this.ValidEndDate);
        }
        
        public void ClearData()
        {
            m_data = null;
        }
        
        public void ValidateHasData()
        {
            if (!m_data.Any())
            {
                throw new ConfigStoreException(ConfigStoreError.MissingCertificateData);
            }        
        }
        
        public void ExcludePrivateKey()
        {
            if (this.HasData)
            {
                using DisposableX509Certificate2 cert = this.ToX509CertificateNoKeys();
                this.SetX509Certificate(cert, false);
            }
        }

        public DisposableX509Certificate2 ToX509CertificateNoKeys()
        {
            return new DisposableX509Certificate2(this.Data, string.Empty, X509KeyStorageFlags.MachineKeySet);
        }

        public DisposableX509Certificate2 ToX509Certificate()
        {
            return new DisposableX509Certificate2(this.Data, string.Empty, X509KeyStorageFlags.Exportable | X509KeyStorageFlags.MachineKeySet);
        }

        public static X509Certificate2Collection ToX509Collection(Certificate[] source)
        {
            if (!source.Any())
            {
                return null;
            }
            
            X509Certificate2Collection x509Coll = new X509Certificate2Collection();
            if (source != null)
            {
                for (int i = 0; i < source.Length; ++i)
                {
                    x509Coll.Add(source[i].ToX509Certificate());
                }
            }
            return x509Coll;
        }
                
        void SetX509Certificate(X509Certificate2 certificate, bool includePrivateKey)
        {
            this.Thumbprint = certificate.Thumbprint;
            this.Data = includePrivateKey ? certificate.Export(X509ContentType.Pfx) : certificate.Export(X509ContentType.Cert);
            this.CreateDate = DateTimeHelper.Now;
            this.ValidStartDate = certificate.NotBefore.ToUniversalTime();
            this.ValidEndDate = certificate.NotAfter.ToUniversalTime();
        }
        
        internal static X509Certificate2 Import(byte[] sourceFileBytes, string password)
        {
            if (!sourceFileBytes.Any())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidX509Certificate);
            }

            return new X509Certificate2(sourceFileBytes, password, X509KeyStorageFlags.Exportable);
        }
    }
}