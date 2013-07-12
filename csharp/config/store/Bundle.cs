/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
  
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
    [Table(Name = "Bundles")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class Bundle
    {
        public const int MaxOwnerLength = 400;
        public const int MaxUrlLength = 1024;
        
        string m_owner;
        string m_url;
        
        public Bundle()
        {
            this.CreateDate = DateTimeHelper.Now;
        }

        public Bundle(string owner, string url, bool forIncoming, bool forOutgoing)
        {
            this.Owner = owner;
            this.Url = url;
            this.CreateDate = DateTimeHelper.Now;
            this.ForIncoming = forIncoming;
            this.ForOutgoing = forOutgoing;
        }

        [Column(Name = "BundleID", IsPrimaryKey = true, IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID
        {
            get;
            set;
        }

        [Column(Name = "Owner", CanBeNull = false, IsPrimaryKey = false)]
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

        [Column(Name = "Url", CanBeNull = false, IsPrimaryKey = false)]
        [DataMember(IsRequired = true)]
        public string Url
        {
            get
            {
                return m_url;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidUrl);
                }

                if (value.Length > MaxUrlLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.UrlLength);
                }

                m_url = value;
            }
        }

        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
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
        
        public Uri Uri
        {
            get
            {
                if (string.IsNullOrEmpty(this.Url))
                {
                    return null;
                }
                
                return new Uri(this.Url);
            }
        }
    }
}