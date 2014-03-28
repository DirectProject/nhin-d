/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
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
using Health.Direct.Common.DnsResolver;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    [Table(Name = "DnsRecords")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class DnsRecord 
    {
        public const int MaxNotesLength = 255;
        public const int MaxDomainNameLength = 255;

        string m_domainName = String.Empty;
        string m_notes = String.Empty;

        public DnsRecord()
        {
            ID = -1;
            this.CreateDate = DateTimeHelper.Now;
            this.UpdateDate = this.CreateDate;
        }

        public DnsRecord(string domainName
            , int typeID
            , byte[] recordData
            , string notes)
            : this()
        {
            this.DomainName = domainName;
            this.TypeID = typeID;
            this.RecordData = recordData;
            this.Notes = notes;
        }

        public DnsRecord(string domainName, DnsStandard.RecordType recordType, byte[] recordData, string notes)
            : this(domainName, (int) recordType, recordData, notes)
        {
        }

        [Column(Name = "RecordID", IsPrimaryKey = true, IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID { get; set; }

        [Column(Name = "DomainName", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string DomainName
        {
            get
            {
                return m_domainName;
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

                m_domainName = value;
            }
        }

        [Column(Name = "Notes", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = false)]
        public string Notes
        {
            get
            {
                return m_notes;
            }
            set
            {
                if (value.Length > MaxNotesLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.NotesLength);
                }

                m_notes = value;
            }
        }

        [Column(Name = "TypeID", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public int TypeID { get; set; }

        [Column(Name = "RecordData", DbType = "varbinary(MAX)", CanBeNull = false, UpdateCheck = UpdateCheck.Never), DataMember(IsRequired = true)]
        public byte[] RecordData { get; set; }

        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate { get; set; }

        [Column(Name = "UpdateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Always)]
        [DataMember(IsRequired = true)]
        public DateTime UpdateDate { get; set; }
        
        public DnsStandard.RecordType RecordType
        {
            get
            {
                return (DnsStandard.RecordType) this.TypeID;
            }
        }
        
        public void ValidateHasData()
        {
            if (RecordData.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.MissingCertificateData);
            }
        }

        internal void CopyFixed(DnsRecord source)
        {
            this.ID = source.ID;
            this.CreateDate = source.CreateDate;
            this.TypeID = source.TypeID;
            this.UpdateDate = source.UpdateDate;
            this.DomainName = source.DomainName;
        }

        /// <summary>
        /// Only copy those fields that are allowed to change in updates
        /// </summary>
        internal void ApplyChanges(DnsRecord source)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            if (!string.IsNullOrEmpty(source.DomainName))
            {
                this.DomainName = source.DomainName;
            }
            this.RecordData = source.RecordData;
            this.Notes = source.Notes;
            this.UpdateDate = DateTimeHelper.Now;
        }

        /// <summary>
        /// Deserialize the raw ResourceRecord embedded in this DnsRecord
        /// </summary>
        public DnsResourceRecord Deserialize()
        {
            if (this.RecordData.IsNullOrEmpty())
            {
                throw new InvalidOperationException("Empty record data found.");
            }

            DnsBufferReader bufferReader = new DnsBufferReader(this.RecordData, 0, this.RecordData.Length);
            return DnsResourceRecord.Deserialize(ref bufferReader);
        }
                
        /// <summary>
        /// Deserialize the raw ResourceRecord embedded in this DnsRecord
        /// </summary>
        /// <typeparam name="T">Of type DnsResourceRecord</typeparam>
        /// <returns>DnsResourceRecord</returns>
        public T Deserialize<T>()
            where T : DnsResourceRecord
        {
            T record = this.Deserialize() as T;
            if (record == null)
            {
                throw new ArgumentException(string.Format("Returned record type does not match expected type, found {0}", this.RecordType));
            }
            
            return record;
        } 
    }
}
