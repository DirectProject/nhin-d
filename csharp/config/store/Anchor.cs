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
using System.Data.SqlTypes;
using System.Data.Linq;
using System.Data.Linq.Mapping;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Certificates;

namespace NHINDirect.ConfigStore
{
    [Table(Name = "Anchors")]
    public class Anchor
    {
        public const int MaxOwnerLength = 400;
        
        string m_owner;
        
        public Anchor()
        {
        }
        
        public Anchor(string owner, X509Certificate2 certificate)
            : this(owner, certificate, true, true)
        {
        }
        
        public Anchor(string owner, X509Certificate2 certificate, bool forIncoming, bool forOutgoing)
        {
            if (string.IsNullOrEmpty(owner) || certificate == null)
            {
                throw new ArgumentException();
            }
            
            this.Owner = owner;
            this.Thumbprint = certificate.Thumbprint;
            this.Data = certificate.RawData;
            this.CreateDate = DateTime.Now;
            this.ValidStartDate = certificate.NotBefore;
            this.ValidEndDate = certificate.NotAfter;
            this.ForIncoming = forIncoming;
            this.ForOutgoing = forOutgoing;
        }

        [Column(Name = "Owner", CanBeNull = false, IsPrimaryKey = true)]
        public string Owner
        {
            get
            {
                return m_owner;
            }
            set
            {
                if (string.IsNullOrEmpty(value) || value.Length > MaxOwnerLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.OwnerLength);
                }
                
                m_owner = value;
            }
        }

        [Column(Name = "Thumbprint", CanBeNull = false, IsPrimaryKey = true)]
        public string Thumbprint
        {
            get;
            set;
        }

        [Column(Name = "CertificateID", CanBeNull = false, IsDbGenerated=true)]
        public long CertificateID
        {
            get;
            set;
        }
        
        [Column(Name = "CertificateData", DbType = "varbinary(MAX)", CanBeNull = false)]
        public byte[] Data
        {
            get;
            set;
        }

        [Column(Name = "CreateDate", CanBeNull = false)]
        public DateTime CreateDate
        {
            get;
            set;
        }
        
        [Column(Name = "ValidStartDate", CanBeNull = false)]
        public DateTime ValidStartDate
        {
            get;
            set;
        }

        [Column(Name = "ValidEndDate", CanBeNull = false)]
        public DateTime ValidEndDate
        {
            get;
            set;
        }

        [Column(Name = "ForIncoming", CanBeNull = false)]
        public bool ForIncoming
        {
            get;
            set;
        }

        [Column(Name = "ForOutgoing", CanBeNull = false)]
        public bool ForOutgoing
        {
            get;
            set;
        }
        
        public X509Certificate2 ToCertificate()
        {
            return new X509Certificate2(this.Data);
        }
    }
}
