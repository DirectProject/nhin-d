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
using System.Runtime.Serialization;
using System.Net.Mail;

namespace NHINDirect.Config.Store
{
    [Table(Name="Domains")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class Domain
    {
        public const int MaxDomainNameLength = 255;
        
        string m_name;
        
        public Domain()
        {
        }
        
        public Domain(string name)
        {
            this.Name = name;
            this.CreateDate = DateTime.Now;
            this.UpdateDate = this.CreateDate;
            this.Status = EntityStatus.New;
        }
           
        [Column(Name="DomainID", IsPrimaryKey=true, IsDbGenerated=true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID
        {
            get;
            set;
        }

        [Column(Name = "DomainName", DbType = "varchar(255)", CanBeNull = false, IsPrimaryKey = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Name
        {
            get
            {
                return m_name;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
                }
                
                if (value.Length > MaxDomainNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.DomainNameLength);
                }
                                
                m_name = value;
            }
        }
        
        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
        {
            get;
            set;
        }

        [Column(Name = "UpdateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Always)]
        [DataMember(IsRequired = true)]
        public DateTime UpdateDate
        {
            get;
            set;
        }

        [Column(Name = "Status", DbType = "tinyint", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public EntityStatus Status
        {
            get;
            set;
        }

        [Column(Name = "PostmasterAddressID", CanBeNull = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = false)]
        public long? PostmasterID
        {
            get;
            set;
        }
        
        internal void CopyFixed(Domain source)
        {
            this.ID = source.ID;
            this.CreateDate = source.CreateDate;
            this.Name = source.Name;
            this.UpdateDate = source.UpdateDate;
        }
        
        internal void ApplyChanges(Domain source)
        {
            this.PostmasterID = source.PostmasterID;
            this.Status = source.Status;
            this.UpdateDate = DateTime.Now;
        }
        
        public bool IsValidEmailDomain()
        {
            return IsValidEmailDomain(this.Name);
        }        
        /// <summary>
        /// The robust way to validate that a domainName meets the criteria of RFC5322...is to parse it as as an Address Field.
        /// We use MailAdress to do that.
        /// </summary>
        public static bool IsValidEmailDomain(string domainName)
        {
            if (string.IsNullOrEmpty(domainName))
            {
                throw new ArgumentException();
            }
            try
            {
                MailAddress address = new MailAddress("unknown.user@" + domainName);
                return address.Host.Equals(domainName, StringComparison.OrdinalIgnoreCase);
            }
            catch
            {
            }
            
            return false;
            
        }
    }
}
