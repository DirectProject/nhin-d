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
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents a message with addresses and content body.
    /// </summary>
    public class MessageEnvelope
    {
        readonly DirectAgent m_agent;
        Message m_message;
        DirectAddress m_sender;
        DirectAddress m_notifyTo;
        DirectAddressCollection m_to;
        DirectAddressCollection m_cc;
        DirectAddressCollection m_bcc;
        DirectAddressCollection m_recipients;
        DirectAddressCollection m_rejectedRecipients;
        
        /// <summary>
        /// Creates an instance from  a <see cref="Message"/>
        /// </summary>
        /// <param name="message">The <see cref="Message"/> instance to use as the underlying message for this envelope.</param>
        public MessageEnvelope(Message message)
        {
            this.Message = message;
            this.Recipients = GetRecipientsInRoutingHeaders();
            
            Header from = message.From;
            if (from == null)
            {
                from = message.Headers[MailStandard.Headers.Sender];
                if (from == null)
                {
                    throw new AgentException(AgentError.MissingFrom);
                }
            }
            this.Sender = new DirectAddress(from.Value);
            this.NotifyTo = GetDispostionNotifyTo(message);
        }

        private static DirectAddress GetDispostionNotifyTo(Message message)
        {
            string notify = message.Headers.GetValue(MDNStandard.Headers.DispositionNotificationTo);
            if (string.IsNullOrEmpty(notify))
            {
                notify = message.FromValue;
            }
            return new DirectAddress(notify);
        }
        
        /// <summary>
        /// Creates an instance from an RFC 5322 message string. 
        /// </summary>
        /// <param name="messageText">The RFC 5322 message string to intialize this envelope from. Stored as <c>RawMessage</c></param>
        public MessageEnvelope(string messageText)
            : this(MimeSerializer.Default.Deserialize<Message>(messageText))
        {
            this.RawMessage = messageText;
        }
        

        /// <summary>
        /// Creates an instance from a <see cref="Message"/> and explicitly assigned sender and receivers, which take precendence over what may be
        /// in the message headers.
        /// </summary>
        /// <param name="message">The <see cref="Message"/> this envelopes</param>
        /// <param name="recipients">The <see cref="DirectAddressCollection"/> of reciepients; takes precedence over the <c>To:</c> header</param>
        /// <param name="sender">The <see cref="DirectAddress"/> of the sender - typically the MAIL FROM in SMTP; takes precendence over the <c>From:</c> header.</param>
        public MessageEnvelope(Message message, DirectAddressCollection recipients, DirectAddress sender)
        {
            this.Message = message;
            this.Recipients = recipients;
            this.Sender = sender;
            this.NotifyTo = GetDispostionNotifyTo(message);
        }

        /// <summary>
        /// Creates an instance from a <see cref="Message"/> and explicitly assigned sender receivers, and rejected recipients, which take precendence over what may be
        /// in the message headers.
        /// </summary>
        /// <param name="message">The <see cref="Message"/> this envelopes</param>
        /// <param name="recipients">The <see cref="DirectAddressCollection"/> of reciepients; takes precedence over the <c>To:</c> header</param>
        /// <param name="rejecteRecipients">The <see cref="DirectAddressCollection"/> of rejected recipients</param>
        /// <param name="sender">The <see cref="DirectAddress"/> of the sender - typically the MAIL FROM in SMTP; takes precendence over the <c>From:</c> header.</param>
        public MessageEnvelope(Message message, DirectAddressCollection recipients, DirectAddressCollection rejecteRecipients, DirectAddress sender)
        {
            this.Message = message;
            this.Recipients = recipients;
            m_rejectedRecipients = rejecteRecipients;
            this.Sender = sender;
            this.NotifyTo = GetDispostionNotifyTo(message);
        }

        /// <summary>
        /// Creates an instance from an RFC 5322 message string  and explicitly assigned sender and receivers, which take precendence over what may be
        /// in the message headers. 
        /// </summary>
        /// <param name="messageText">The RFC 5322 message string to intialize this envelope from. Stored as <c>RawMessage</c></param>
        /// <param name="recipients">The <see cref="DirectAddressCollection"/> of reciepients; takes precedence over the <c>To:</c> header</param>
        /// <param name="sender">The <see cref="DirectAddress"/> of the sender - typically the MAIL FROM in SMTP; takes precendence over the <c>From:</c> header.</param>
        public MessageEnvelope(string messageText, DirectAddressCollection recipients, DirectAddress sender)
            : this(MimeSerializer.Default.Deserialize<Message>(messageText), recipients, sender)
        {
            this.RawMessage = messageText;
        }
        
        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance, with explicitly assigned raw message, recipients and sender,
        /// which take precendece over what may be in the message object or text.
        /// </summary>
        /// <param name="message">The <see cref="Message"/> this envelopes</param>
        /// <param name="recipients">The <see cref="DirectAddressCollection"/> of reciepients; takes precedence over the <c>To:</c> header</param>
        /// <param name="sender">The <see cref="DirectAddress"/> of the sender; takes precendence over the <c>From:</c> header.</param>
        /// <param name="rawMessage">The RFC 5322 message string to use ae the raw message for this instance.</param>
        protected MessageEnvelope(Message message, string rawMessage, DirectAddressCollection recipients, DirectAddress sender)
            : this(message, recipients, sender)
        {
            this.RawMessage = rawMessage;
        }
        
        internal MessageEnvelope(MessageEnvelope envelope)
        {
            m_agent = envelope.m_agent;
            this.RawMessage = envelope.RawMessage;
            m_message = envelope.m_message;
            if (envelope.m_recipients != null)
            {
                m_recipients = new DirectAddressCollection {envelope.m_recipients};
            }
            
            m_sender = envelope.m_sender;
            m_notifyTo = envelope.m_notifyTo;
        }
        
        /// <summary>
        /// The <see cref="Message"/> instance representing the underlying message
        /// </summary>
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
        
        /// <summary>
        /// Either the SMTP envelope MAIL FROM OR the sender (<c>From:</c> header) address.
        /// </summary>
        public DirectAddress Sender
        {
            get
            {
                return m_sender;
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


        /// <summary>
        /// Disposition-Notification-To header value
        /// </summary>
        public DirectAddress NotifyTo
        {
            get
            {
                return m_notifyTo;
            }
            internal set
            {
                m_notifyTo = value;
            }
        }


        /// <summary>
        /// The recipients of the message. Will generally reflect the <c>To:</c> header unless there are any <c>RejectedRecipients</c>
        /// </summary>
        public virtual DirectAddressCollection Recipients
        {
            get
            {
                if (m_recipients == null)
                {
                    this.GetRecipientsInRoutingHeaders();
                }
                return m_recipients;
            }
            internal set
            {
                if (value.IsNullOrEmpty())
                {
                    throw new AgentException(AgentError.NoRecipients);
                }
                m_recipients = value;
            }
        }

        /// <summary>
        /// Gets if this message has recipients
        /// </summary>
        public bool HasRecipients
        {
            get
            {
                return (m_recipients != null && m_recipients.Count > 0);
            }
        }

        /// <summary>
        /// A collection of recipients that have been rejected due to trust issues.
        /// </summary>
        public DirectAddressCollection RejectedRecipients
        {
            get
            {
                if (m_rejectedRecipients == null)
                {
                    m_rejectedRecipients = new DirectAddressCollection();
                }

                return m_rejectedRecipients;
            }
        }

        /// <summary>
        /// Does this message have rejected recipients?
        /// </summary>
        public bool HasRejectedRecipients
        {
            get
            {
                return (m_rejectedRecipients != null && m_rejectedRecipients.Count > 0);
            }
        }

        /// <summary>
        /// Gets the domain managed recipients for this envelope
        /// </summary>
        public DirectAddressCollection DomainRecipients 
        { 
            get;
            internal set;
        }


        /// <summary>
        /// Indicates if this envelope has domain managed recipients.
        /// </summary>
        /// <value><c>true</c> if this envelope has domain managed recipients, <c>false</c> otherwise</value>
        public bool HasDomainRecipients
        {
            get
            {
                return (!this.DomainRecipients.IsNullOrEmpty());
            }
        }

        /// <summary>
        /// Gets and sets the non-domain recipients for this evelope.
        /// </summary>
        public MailAddressCollection OtherRecipients
        {
            get;
            internal set;
        }

        /// <summary>
        /// Indicates if this envelope has non-domain managed recipients.
        /// </summary>
        /// <value><c>true</c> if this envelope has non-domain managed recipients, <c>false</c> otherwise</value>
        public bool HasOtherRecipients
        {
            get
            {
                return (!this.OtherRecipients.IsNullOrEmpty());
            }
        }
        
        internal DirectAddressCollection To
        {
            get
            {
                if (m_to == null)
                {
                    m_to = DirectAddressCollection.Parse(m_message.To);
                }
                
                return m_to;
            }
        }

        internal DirectAddressCollection Cc
        {
            get
            {
                if (m_cc == null)
                {
                    m_cc = DirectAddressCollection.Parse(m_message.Cc);
                }

                return m_cc;
            }
        }

        internal DirectAddressCollection Bcc
        {
            get
            {
                if (m_bcc == null)
                {
                    m_bcc = DirectAddressCollection.Parse(m_message.Bcc);
                }

                return m_bcc;
            }
        }

        internal bool HasRawMessage
        {
            get
            {
                return (!string.IsNullOrEmpty(RawMessage));
            }
        }

        internal string RawMessage { get; set; }


        /// <summary>
        /// Creates an RFC 5322 representation of this evelope's message. Does not serialize the custom properties, only the underlying
        /// message instance associated with this envelope.
        /// </summary>
        /// <returns>An RFC 5322 string representation of this envelope's message.</returns>
        public string SerializeMessage()
        {
            return MimeSerializer.Default.Serialize(m_message);
        }
        
        //
        // Release buffers
        //
        internal void Clear()
        {
            RawMessage = null;
            m_message = null;
            m_sender = null;
            m_to = null;
            m_cc = null;
            m_bcc = null;
            m_recipients = null;
            m_rejectedRecipients = null;
        }
        
        internal virtual void Validate()
        {
        }        
        
        /// <summary>
        /// Return a combined collection of all recipients in routing headers (to/cc/bcc)
        /// </summary>
        /// <returns>A collection of recipients</returns>        
        public DirectAddressCollection GetRecipientsInRoutingHeaders()
        {
            DirectAddressCollection addresses = new DirectAddressCollection();
            if (this.To != null)
            {
                addresses.Add(this.To);
            }                
            if (this.Cc != null)
            {
                addresses.Add(this.Cc);
            }
            if (this.Bcc != null)
            {
                addresses.Add(this.Bcc);
            }
            return addresses;
        }
        
        /// <summary>
        /// Remove addresses from Routing headers
        /// </summary>
        /// <param name="addresses"><see cref="DirectAddressCollection"/> containing addresses to remove</param>
        public void RemoveFromRoutingHeaders(DirectAddressCollection addresses)
        {
            this.UpdateRoutingHeaders(addresses);
        }
        
        internal void UpdateRoutingHeaders(DirectAddressCollection rejectedRecipients)
        {
            if (rejectedRecipients.IsNullOrEmpty()) 
            {
                return;
            }

            this.UpdateRecipientHeader(To, MailStandard.Headers.To, rejectedRecipients);
            this.UpdateRecipientHeader(Cc, MailStandard.Headers.Cc, rejectedRecipients);
            this.UpdateRecipientHeader(Bcc, MailStandard.Headers.Bcc, rejectedRecipients);
        }
        
        void UpdateRecipientHeader(DirectAddressCollection recipients, string headerName, IEnumerable<DirectAddress> rejectedRecipients)
        {
            if (recipients != null) 
            {
                recipients.Remove(rejectedRecipients);
                this.Message.Headers[headerName] = recipients.ToHeader(headerName);
            }
        }

        internal void UpdateRoutingHeaders()
        {
            if (this.HasRejectedRecipients)
            {
                this.UpdateRoutingHeaders(RejectedRecipients);
            }
        }
        
        /// <summary>
        /// If recipients have  not been categorized by domain, categorizes by domain.
        /// </summary>
        /// <param name="domains">Domains to treat as  domain recipients.</param>
        public void EnsureRecipientsCategorizedByDomain(AgentDomains domains)
        {
            // We only want to categorize if we haven't done it already
            // Do NOT change these to IsNullOrEmpty
            if (this.DomainRecipients != null || this.OtherRecipients != null)
            {
                return;
            }
            
            this.CategorizeRecipientsByDomain(domains);
        }

        /// <summary>
        /// Verify that the recipient list in the Message Envelope matches that in the message
        /// </summary>
        public bool AreAddressesInRoutingHeaders(IEnumerable<DirectAddress> addresses)
        {
            if (addresses == null)
            {
                throw new ArgumentException("addresses");
            }
            //
            // Get the list of recipients as specified in the message itself
            //
            DirectAddressCollection recipientsInHeaders = this.GetRecipientsInRoutingHeaders();
            foreach(DirectAddress address in addresses)
            {
                if (!recipientsInHeaders.Contains(address.Address))
                {
                    return false;
                }
            }
            
            return true;
        }
        
        /// <summary>
        /// Categorize recipients as follows:
        /// - are they in the local domain or are they external
        /// </summary>
        /// <param name="domains"></param>
        internal void CategorizeRecipientsByDomain(AgentDomains domains)
        {
            DirectAddressCollection recipients = Recipients;
            DirectAddressCollection domainRecipients = null;
            MailAddressCollection otherRecipients = null;

            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                DirectAddress address = recipients[i];
                if (domains.IsManaged(address))
                {
                    if (domainRecipients == null)
                    {
                        domainRecipients = new DirectAddressCollection();
                    }
                    domainRecipients.Add(address);
                }
                else
                {
                    if (otherRecipients == null)
                    {
                        otherRecipients = new MailAddressCollection();
                    }
                    otherRecipients.Add(address);
                }
            }

            this.DomainRecipients = domainRecipients;
            this.OtherRecipients = otherRecipients;
        }

        internal virtual void CategorizeRecipientsByTrust(TrustEnforcementStatus minTrustStatus)
        {
            m_rejectedRecipients = DirectAddressCollection.Create(Recipients.GetUntrusted(minTrustStatus));
            if (this.HasRecipients)
            {
                this.Recipients.RemoveUntrusted(minTrustStatus);
            }
            if (this.HasDomainRecipients)
            {
                this.DomainRecipients.RemoveUntrusted(minTrustStatus);
            }
        }

        /// <summary>
        /// Create PerRecipient Status part of (DSN) for this message - IF DSNs should be generated. 
        /// If no DSN should be generated, returns NULL. 
        ///   - If there are no recipients
        ///   - If the message is itself an MDN or DSN
        /// </summary>
        /// <param name="recipients">Final-Recipients to report on delivery status</param>
        /// <param name="textMessage">Optional text message to accompany the Ack</param>
        /// <param name="alwaysAck">Generate acks even when none were requested</param>
        /// <param name="action">DSN action</param>
        /// <param name="classSubCode">Status code class</param>
        /// <param name="subjectSubCode">Status code subject</param>
        /// <returns>An DSNMessage</returns>
        public IEnumerable<DSNPerRecipient> CreatePerRecipientStatus(IEnumerable<MailAddress> recipients, string textMessage
                                                                     , bool alwaysAck, DSNStandard.DSNAction action, int classSubCode, string subjectSubCode)
        {
            if (recipients == null)
            {
                throw new ArgumentException("senders");
            }        
            
            if (this.Message.IsMDN())
            {
                return null;
            }

            
            var perRecipients = recipients.Select(
                recipient => 
                new DSNPerRecipient(action, classSubCode, subjectSubCode, recipient)
                ).ToList();

            return perRecipients;


        }
    }
}