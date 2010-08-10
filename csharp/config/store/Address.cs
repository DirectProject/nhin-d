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

namespace NHINDirect.ConfigStore
{
    public enum AddressStatus : byte
    {
        Reserved = 0,
        Enabled = 1,
        Disabled = 2
    }

    [Table(Name = "Addresses")]
    public class Address
    {
        public const int MaxEndpointNameLength = 64;
        public const int MaxDisplayNameLength = 64;
        
        string m_endpointName;
        string m_displayName;
        
        public Address()
        {
        }
        
        public Address(long domainID, string endpoint, string displayName)
        {
            this.DomainID = domainID;
            this.EndpointName = endpoint;
            this.DisplayName = displayName;
            this.CreateDate = DateTime.Now;
            this.UpdateDate = this.CreateDate;
            this.Status = AddressStatus.Reserved;
        }
        
        [Column(Name="AddressID", IsDbGenerated=true, IsPrimaryKey=true)]
        public long ID
        {
            get;
            set;
        }
        
        [Column(Name="DomainID", CanBeNull=false)]
        public long DomainID
        {
            get;
            set;
        }
        
        [Column(Name="EndpointName", CanBeNull=false)]
        public string EndpointName
        {
            get
            {
                return m_endpointName;
            }
            set
            {
                if (string.IsNullOrEmpty(value) || value.Length > MaxEndpointNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.EndpointNameLength);
                }
                
                m_endpointName = value;
            }
        }
        
        [Column(Name="DisplayName", CanBeNull=false)]
        public string DisplayName
        {
            get
            {
                return m_displayName;
            }
            set
            {
                if (value != null && value.Length > MaxDisplayNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.DisplayNameLength);
                }
                
                m_displayName = value ?? string.Empty;
            }
        }

        [Column(Name = "CreateDate", CanBeNull = false)]
        public DateTime CreateDate
        {
            get;
            set;
        }

        [Column(Name = "UpdateDate", CanBeNull = false)]
        public DateTime UpdateDate
        {
            get;
            set;
        }

        [Column(Name = "Status", DbType = "tinyint", CanBeNull = false)]
        public AddressStatus Status
        {
            get;
            set;
        }
    }
}
