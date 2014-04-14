/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Data.Linq.Mapping;
using System.Runtime.Serialization;
using System.Net.Mail;

namespace Health.Direct.Config.Store
{
    [Table(Name = "MXs")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class MX
    {
        public const int MaxMXNameLength = 255;

        string m_SMTPDomainName = String.Empty;

        public MX()
        {
        }

        public MX(long domainID
                  , string SMTPDomainName)
            : this(domainID, SMTPDomainName, 0)
        {
        }

        public MX(long domainID
                  , string SMTPDomainName
                  , int preference)
        {
            this.DomainID = domainID;
            this.SMTPDomainName = SMTPDomainName;
            this.Preference = preference;
            this.CreateDate = DateTime.Now;
            this.UpdateDate = this.CreateDate;
            

        }

        [Column(Name = "MXID", IsPrimaryKey = true, IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID
        {
            get;
            set;
        }

        [Column(Name = "SMTPDomainName", CanBeNull = false, IsPrimaryKey = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string SMTPDomainName
        {
            get
            {
                return m_SMTPDomainName;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
                }

                if (value.Length > Domain.MaxDomainNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.DomainNameLength);
                }

                m_SMTPDomainName = value;
            }
        }


        [Column(Name = "DomainID", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long DomainID
        {
            get;
            set;
        }

        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
        {
            get;
            set;
        }

        [Column(Name = "UpdateDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime UpdateDate
        {
            get;
            set;
        }

        [Column(Name = "Preference", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public int Preference
        {
            get;
            set;
        }

        internal void CopyFixed(MX source)
        {
            this.ID = source.ID;
            this.CreateDate = source.CreateDate;
            this.DomainID = source.DomainID;
            this.SMTPDomainName = source.SMTPDomainName;
            this.Preference = source.Preference;
            this.UpdateDate = source.UpdateDate;
        }

        internal void ApplyChanges(MX source)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            this.SMTPDomainName = source.SMTPDomainName;
            this.Preference = source.Preference;
            this.UpdateDate = DateTime.Now; //---TODO: Use UTC Here?

        }


        public bool IsValidEmailDomain()
        {
            return IsValidEmailDomain(this.SMTPDomainName);
        }

        /// <summary>
        /// The robust way to validate that a domainName meets the criteria of RFC5322...is to parse it as as an Address Field.
        /// We use MailAdress to do that.
        /// </summary>
        public static bool IsValidEmailDomain(string smtpDomainName)
        {
            if (string.IsNullOrEmpty(smtpDomainName))
            {
                throw new ArgumentException("value was null or empty", "smtpDomainName");
            }
            try
            {
                MailAddress address = new MailAddress("unknown.user@" + smtpDomainName);
                return address.Host.Equals(smtpDomainName, StringComparison.OrdinalIgnoreCase);
            }
            catch
            {
            }

            return false;

        }

    }
}