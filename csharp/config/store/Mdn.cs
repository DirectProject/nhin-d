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

        public Mdn(string messageId, string recipient, string sender, bool notifyDispatched)
            : this()
        {
            MessageId = messageId;
            Recipient = recipient;
            Sender = sender;
            NotifyDispatched = notifyDispatched;
            Status = MdnStatus.Started;
        }

        public Mdn(string messageId, string recipient, string sender)
            : this()
        {
            MessageId = messageId;
            Recipient = recipient;
            Sender = sender;
            NotifyDispatched = false;
            Status = MdnStatus.Started;
        }

        public Mdn(string messageId, string recipient, string sender, string status)
            : this()
        {
            MessageId = messageId;
            Recipient = recipient;
            Sender = sender;
            NotifyDispatched = false;
            Status = status;
        }

        public Mdn()
        {
            CreateDate = DateTimeHelper.Now;
        }

        /// <summary>
        /// Hash key as primary key to over come the SQL server key length limit of 900 bytes.
        /// MessageId, Reciever, and Status make up the composit key.
        /// </summary>
        [Column(Name = "MdnIdentifier", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string MdnIdentifier
        {
            get
            {
                if (MessageId == null || Recipient == null)
                {
                    return null;
                }
                var fieldsSb = new StringBuilder();
                fieldsSb.Append(MessageId.ToLower()).Append(Recipient.ToLower()).Append(Status.ToLower());

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


        [Column(Name = "MdnId", IsDbGenerated = true, IsPrimaryKey = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long Id
        {
            get;
            set;
        }

        [Column(Name = "SenderAddress", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
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

        [Column(Name = "Subject", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = false)]
        public string SubjectValue { get; set; }

        [Column(Name = "Status", DbType = "varchar(15)", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Status
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

       

        [Column(Name = "NotifyDispatched", DbType = "bit", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public bool NotifyDispatched
        {
            get;
            set;
        }

    }
}