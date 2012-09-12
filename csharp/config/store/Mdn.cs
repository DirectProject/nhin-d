/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.ComponentModel;
using System.Data.Linq.Mapping;
using System.Runtime.Serialization;
using System.Security.Cryptography;
using System.Text;

namespace Health.Direct.Config.Store
{
    [Table(Name = "Mdns")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class Mdn
    {
        string m_sender;
        string m_messageId;

        public Mdn(string messageId, string recipient, string sender)
            : this()
        {
            MessageId = messageId;
            Recipient = recipient;
            Sender = sender;
        }
        public Mdn()
        {
            CreateDate = DateTimeHelper.Now;
            UpdateDate = CreateDate;
        }

        /// <summary>
        /// Hash key as primary key to over come the SQL server key length limit of 900 bytes.
        /// MessageId and Reciever make up the composit key.
        /// </summary>
        [Column(Name = "MdnIdentifier", AutoSync = AutoSync.Always, CanBeNull = false, IsPrimaryKey = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string MdnIdentifier
        {
            get
            {
                var fieldsSb = new StringBuilder();
                fieldsSb.Append(MessageId.ToLower()).Append(Recipient.ToLower());

                var md5 = MD5.Create();
                var inputBytes = Encoding.ASCII.GetBytes(fieldsSb.ToString());
                var hashBytes = md5.ComputeHash(inputBytes);

                var sb = new StringBuilder();
                for (int i = 0; i < hashBytes.Length; i++)
                {
                    sb.Append(hashBytes[i].ToString("X2"));
                }
                return sb.ToString();
            }
            set
            {
                //noop
            }
        }

        [Column(Name = "MessageId", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string MessageId
        {
            get
            {
                return m_messageId;
            }
            set
            {
                m_messageId = value;
            }
        }

        [Column(Name = "RecipientAddress", CanBeNull = false, UpdateCheck = UpdateCheck.Never), DataMember(IsRequired = true)]
        public string Recipient { get; set; }


        [Column(Name = "MdnId", IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long Id
        {
            get;
            set;
        }

        [Column(Name = "SenderAddress", CanBeNull = false, IsPrimaryKey = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Sender
        {
            get
            {
                return m_sender;
            }
            set
            {
                m_sender = value;
            }
        }

        [Column(Name = "Status", DbType = "varchar(9)", CanBeNull = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Status
        {
            get;
            set;
        }

        [Column(Name = "Timedout", DbType = "bit", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public bool Timedout
        {
            get;
            set;
        }


        [Column(Name = "MdnProcessedDate", CanBeNull = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime? MdnProcessedDate
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

        [Column(Name = "UpdateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime UpdateDate
        {
            get;
            set;
        }

        internal void CopyFixed(Mdn source)
        {
            Id = source.Id;
            MessageId = source.MessageId;
            Recipient = source.Recipient;
            Sender = source.Sender;
            CreateDate = source.CreateDate;
            UpdateDate = source.UpdateDate;
        }
        /// <summary>
        /// Only copy those fields that are allowed to change in updates
        /// </summary>
        internal void ApplyChanges(Mdn source, Mdn original)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            Status = source.Status;
            Timedout = source.Timedout;
            MdnProcessedDate = source.MdnProcessedDate;
            UpdateDate = DateTimeHelper.Now;

            //Workflow from monitor started to dispostition-type is processed
            if(string.Compare(Status, MdnStatus.Processed, StringComparison.OrdinalIgnoreCase) == 0 
                && string.Compare(Status, original.Status, StringComparison.OrdinalIgnoreCase) != 0)
            {
                MdnProcessedDate = DateTimeHelper.Now;
            }

        }

        
    }
}