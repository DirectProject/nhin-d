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
using System.Net.Mail;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Mime;
using NHINDirect.Mail;
using NHINDirect.Collections;

namespace NHINDirect.Agent
{
    public class MessageEnvelope
    {
        NHINDAgent m_agent;
        Message m_message;
        NHINDAddress m_sender;
        NHINDAddressCollection m_to;
        NHINDAddressCollection m_cc;
        NHINDAddressCollection m_bcc;
        NHINDAddressCollection m_recipients;
        NHINDAddressCollection m_rejectedRecipients;
        NHINDAddressCollection m_domainRecipients;
        MailAddressCollection m_otherRecipients;

        string m_rawMessage;
        
        public MessageEnvelope(Message message)
        {
            this.Message = message;
            this.Recipients = this.CollectRecipients();
            
            Header from = message.From;
            if (from == null)
            {
                throw new AgentException(AgentError.MissingFrom);
            }            
            this.Sender = new NHINDAddress(from.Value, AddressSource.From);
        }
        
        public MessageEnvelope(string messageText)
            : this(MimeSerializer.Default.Deserialize<Message>(messageText))
        {
            this.RawMessage = messageText;
        }
                
        public MessageEnvelope(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
        {
            this.Message = message;
            this.Recipients = recipients;
            this.Sender = sender;
        }
        
        public MessageEnvelope(string messageText, NHINDAddressCollection recipients, NHINDAddress sender)
            : this(MimeSerializer.Default.Deserialize<Message>(messageText), recipients, sender)
        {
            this.RawMessage = messageText;
        }
                
        protected MessageEnvelope(Message message, string rawMessage, NHINDAddressCollection recipients, NHINDAddress sender)
            : this(message, recipients, sender)
        {
            this.RawMessage = rawMessage;
        }
        
        internal MessageEnvelope(MessageEnvelope envelope)
        {
            m_agent = envelope.m_agent;
            m_rawMessage = envelope.m_rawMessage;
            m_message = envelope.m_message;
            m_recipients = envelope.m_recipients;
            m_sender = envelope.m_sender;
        }
        
        internal NHINDAgent Agent
        {
            get
            {
                return this.m_agent;
            }
            set
            {
                this.m_agent = value;
            }
        }
        
        public Message Message
        {
            get
            {
                return m_message;
            }
            set
            {
                if (value == null)
                {
                    throw new AgentException(AgentError.MissingMessage);
                }
                
                m_message = value;
                this.RawMessage = null;
            }
        }
        
        public NHINDAddress Sender
        {
            get
            {
                return this.m_sender;
            }
            internal set
            {
                if (value == null)
                {
                    throw new AgentException(AgentError.NoSender);
                }
                m_sender = value;
            }
        }
        
        public virtual NHINDAddressCollection Recipients
        {
            get
            {
                if (m_recipients == null)
                {
                    this.CollectRecipients();
                }
                return this.m_recipients;
            }
            internal set
            {
                if (value == null || value.Count == 0)
                {
                    throw new AgentException(AgentError.NoRecipients);
                }
                m_recipients = value;
            }
        }

        public bool HasRecipients
        {
            get
            {
                return (m_recipients != null && m_recipients.Count > 0);
            }
        }

        public NHINDAddressCollection RejectedRecipients
        {
            get
            {
                if (this.m_rejectedRecipients == null)
                {
                    this.m_rejectedRecipients = new NHINDAddressCollection();
                }

                return this.m_rejectedRecipients;
            }
        }

        public bool HasRejectedRecipients
        {
            get
            {
                return (this.m_rejectedRecipients != null && this.m_rejectedRecipients.Count > 0);
            }
        }

        public NHINDAddressCollection DomainRecipients
        {
            get
            {
                if (this.m_domainRecipients == null)
                {
                    this.CategorizeRecipients(this.Agent.Domain);
                }

                return this.m_domainRecipients;
            }
        }

        public bool HasDomainRecipients
        {
            get
            {
                NHINDAddressCollection recipients = this.DomainRecipients;
                return (recipients != null && recipients.Count > 0);
            }
        }

        public MailAddressCollection OtherRecipients
        {
            get
            {
                if (this.m_otherRecipients == null)
                {
                    this.CategorizeRecipients(this.Agent.Domain);
                }

                return this.m_otherRecipients;
            }
        }

        public bool HasOtherRecipients
        {
            get
            {
                MailAddressCollection recipients = this.OtherRecipients;
                return (recipients != null && recipients.Count > 0);
            }
        }
        
        internal NHINDAddressCollection To
        {
            get
            {
                if (m_to == null)
                {
                    m_to = NHINDAddressCollection.Parse(m_message.To, AddressSource.To);
                }
                
                return m_to;
            }
        }

        internal NHINDAddressCollection CC
        {
            get
            {
                if (m_cc == null)
                {
                    m_cc = NHINDAddressCollection.Parse(m_message.CC, AddressSource.CC);
                }

                return m_cc;
            }
        }

        internal NHINDAddressCollection BCC
        {
            get
            {
                if (m_bcc == null)
                {
                    m_bcc = NHINDAddressCollection.Parse(m_message.BCC, AddressSource.BCC);
                }

                return m_bcc;
            }
        }

        internal bool HasRawMessage
        {
            get
            {
                return (!string.IsNullOrEmpty(m_rawMessage));
            }
        }

        internal string RawMessage
        {
            get
            {
                return m_rawMessage;
            }
            set
            {
                m_rawMessage = value;
            }
        }
        
        public string SerializeMessage()
        {
            return MimeSerializer.Default.Serialize(m_message);
        }
        
        //
        // Release buffers
        //
        internal void Clear()
        {
            m_rawMessage = null;
            m_message = null;
            m_sender = null;
            m_to = null;
            m_cc = null;
            m_bcc = null;
            m_recipients = null;
            m_rejectedRecipients = null;
            m_domainRecipients = null;
            m_otherRecipients = null;
        }
        
        internal virtual void Validate()
        {
        }        
                
        internal NHINDAddressCollection CollectRecipients()
        {
            NHINDAddressCollection addresses = new NHINDAddressCollection();
            if (this.To != null)
            {
                addresses.Add(this.To);
            }                
            if (this.CC != null)
            {
                addresses.Add(this.CC);
            }
            if (this.BCC != null)
            {
                addresses.Add(this.BCC);
            }
            return addresses;
        }

        internal void UpdateRoutingHeaders(NHINDAddressCollection rejectedRecipients)
        {
            if (rejectedRecipients == null || rejectedRecipients.Count == 0)
            {
                return;
            }
            //
            // TODO: Optimize this
            //
            if (this.To != null)
            {
                this.To.Remove(rejectedRecipients);
                this.Message.To = (this.To.Count > 0) ? new Header(MailStandard.ToHeader, this.To.ToString()) : null;
            }
            if (this.CC != null)
            {
                this.CC.Remove(rejectedRecipients);
                this.Message.CC = (this.CC.Count > 0) ? new Header(MailStandard.CCHeader, this.CC.ToString()) : null;
            }
            if (this.BCC != null)
            {
                this.BCC.Remove(rejectedRecipients);
                this.Message.BCC = (this.BCC.Count > 0) ? new Header(MailStandard.BCCHeader, this.BCC.ToString()) : null;
            }
        }
                
        internal void UpdateRoutingHeaders()
        {
            if (this.HasRejectedRecipients)
            {
                this.UpdateRoutingHeaders(this.RejectedRecipients);
            }
        }

        /// <summary>
        /// Split recipients - are they in the local domain or are they external
        /// </summary>
        /// <param name="domain"></param>
        internal void CategorizeRecipients(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException();
            }

            NHINDAddressCollection recipients = this.Recipients;
            this.m_domainRecipients = new NHINDAddressCollection();
            this.m_otherRecipients = new MailAddressCollection();

            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                NHINDAddress address = recipients[i];
                if (address.DomainEquals(domain))
                {
                    this.m_domainRecipients.Add(address);
                }
                else
                {
                    this.m_otherRecipients.Add(address);
                }
            }
        }

        internal virtual void CategorizeRecipients(TrustEnforcementStatus minTrustStatus)
        {
            this.m_rejectedRecipients = NHINDAddressCollection.Create(this.Recipients.GetUntrusted(minTrustStatus));
            this.Recipients.RemoveUntrusted(minTrustStatus);
        }
    }    
}
