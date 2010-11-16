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
using System.Data.Linq.Mapping;
using System.Security.Cryptography.X509Certificates;
using System.Runtime.Serialization;

using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    [Table(Name = "Anchors")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class Anchor
    {
        public const int MaxOwnerLength = 400;
        
        string m_owner;
        byte[] m_data;
        
        public Anchor()
        {
        }

        public Anchor(string owner, byte[] sourceFileBytes, string password)
            : this(owner, Certificate.Import(sourceFileBytes, password))
        {
        }
        
        
        public Anchor(string owner, X509Certificate2 certificate)
            : this(owner, certificate, true, true)
        {
        }
        
        public Anchor(string owner, X509Certificate2 certificate, bool forIncoming, bool forOutgoing)
        {
            if (certificate == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertificate);
            }
            
            this.Owner = owner;
            this.Thumbprint = certificate.Thumbprint;
            this.Data = certificate.RawData;
            this.CreateDate = DateTimeHelper.Now;
            this.ValidStartDate = certificate.NotBefore;
            this.ValidEndDate = certificate.NotAfter;
            this.ForIncoming = forIncoming;
            this.ForOutgoing = forOutgoing;
        }

        [Column(Name = "Owner", CanBeNull = false, IsPrimaryKey = true)]
        [DataMember(IsRequired = true)]
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

        [Column(Name = "Thumbprint", CanBeNull = false, IsPrimaryKey = true)]
        [DataMember(IsRequired = true)]
        public string Thumbprint
        {
            get;
            set;
        }

        [Column(Name = "CertificateID", CanBeNull = false, IsDbGenerated=true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID
        {
            get;
            set;
        }
        
        [Column(Name = "CertificateData", DbType = "varbinary(MAX)", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
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

        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
        {
            get;
            set;
        }

        [Column(Name = "ValidStartDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime ValidStartDate
        {
            get;
            set;
        }

        [Column(Name = "ValidEndDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime ValidEndDate
        {
            get;
            set;
        }

        [Column(Name = "ForIncoming", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public bool ForIncoming
        {
            get;
            set;
        }

        [Column(Name = "ForOutgoing", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public bool ForOutgoing
        {
            get;
            set;
        }

        [Column(Name = "Status", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
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
            if (m_data.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.MissingCertificateData);
            }
        }
        
        public X509Certificate2 ToX509Certificate()
        {
            return new X509Certificate2(this.Data);
        }
        
        public static X509Certificate2Collection ToX509Collection(Anchor[] anchors)
        {
            if (anchors.IsNullOrEmpty())
            {
                return null;
            }
            
            X509Certificate2Collection x509Collection = new X509Certificate2Collection();
            if (anchors != null)
            {
                for (int i = 0; i < anchors.Length; ++i)
                {
                    x509Collection.Add(anchors[i].ToX509Certificate());
                }
            }            
            return x509Collection;
        }
    }
}