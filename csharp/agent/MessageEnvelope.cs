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
using System.Net.Mail;

using NHINDirect.Mime;
using NHINDirect.Mail;

namespace NHINDirect.Agent
{
    /// <summary>
    /// Represents a message with addresses and content body.
    /// </summary>
    public class MessageEnvelope
    {
    	readonly NHINDAgent m_agent;
        Message m_message;
        NHINDAddress m_sender;
        NHINDAddressCollection m_to;
        NHINDAddressCollection m_cc;
        NHINDAddressCollection m_bcc;
        NHINDAddressCollection m_recipients;
        NHINDAddressCollection m_rejectedRecipients;
        
        /// <summary>
        /// Creates an instance from  a <see cref="Message"/>
        /// </summary>
        /// <param name="message">The <see cref="Message"/> instance to use as the underlying message for this envelope.</param>
    	public MessageEnvelope(Message message)
        {
            this.Message = message;
            this.Recipients = CollectRecipients();
            
            Header from = message.From;
            if (from == null)
            {
                throw new AgentException(AgentError.MissingFrom);
            }
            this.Sender = new NHINDAddress(from.Value);
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
        /// <param name="recipients">The <see cref="NHINDAddressCollection"/> of reciepients; takes precedence over the <c>To:</c> header</param>
        /// <param name="sender">The <see cref="NHINDAddress"/> of the sender; takes precendence over the <c>From:</c> header.</param>
        public MessageEnvelope(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
        {
            this.Message = message;
            this.Recipients = recipients;
            this.Sender = sender;
        }

        /// <summary>
        /// Creates an instance from an RFC 5322 message string  and explicitly assigned sender and receivers, which take precendence over what may be
        /// in the message headers. 
        /// </summary>
        /// <param name="messageText">The RFC 5322 message string to intialize this envelope from. Stored as <c>RawMessage</c></param>
        /// <param name="recipients">The <see cref="NHINDAddressCollection"/> of reciepients; takes precedence over the <c>To:</c> header</param>
        /// <param name="sender">The <see cref="NHINDAddress"/> of the sender; takes precendence over the <c>From:</c> header.</param>
        public MessageEnvelope(string messageText, NHINDAddressCollection recipients, NHINDAddress sender)
            : this(MimeSerializer.Default.Deserialize<Message>(messageText), recipients, sender)
        {
            this.RawMessage = messageText;
        }
        
        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance, with explicitly assigned raw message, recipients and sender,
        /// which take precendece over what may be in the message object or text.
        /// </summary>
        /// <param name="message">The <see cref="Message"/> this envelopes</param>
        /// <param name="recipients">The <see cref="NHINDAddressCollection"/> of reciepients; takes precedence over the <c>To:</c> header</param>
        /// <param name="sender">The <see cref="NHINDAddress"/> of the sender; takes precendence over the <c>From:</c> header.</param>
        /// <param name="rawMessage">The RFC 5322 message string to use ae the raw message for this instance.</param>
        protected MessageEnvelope(Message message, string rawMessage, NHINDAddressCollection recipients, NHINDAddress sender)
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
                m_recipients = new NHINDAddressCollection {envelope.m_recipients};
            }
            
            m_sender = envelope.m_sender;
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
        /// The sender (<c>From:</c> header) address.
        /// </summary>
        public NHINDAddress Sender
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
        /// The recipients of the message. Will generally reflect the <c>To:</c> header unless there are any <c>RejectedRecipients</c>
        /// </summary>
        public virtual NHINDAddressCollection Recipients
        {
            get
            {
                if (m_recipients == null)
                {
                    this.CollectRecipients();
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
        public NHINDAddressCollection RejectedRecipients
        {
            get
            {
                if (m_rejectedRecipients == null)
                {
                    m_rejectedRecipients = new NHINDAddressCollection();
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
        public NHINDAddressCollection DomainRecipients 
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
        
        internal NHINDAddressCollection To
        {
            get
            {
                if (m_to == null)
                {
                    m_to = NHINDAddressCollection.Parse(m_message.To);
                }
                
                return m_to;
            }
        }

        internal NHINDAddressCollection Cc
        {
            get
            {
                if (m_cc == null)
                {
                    m_cc = NHINDAddressCollection.Parse(m_message.Cc);
                }

                return m_cc;
            }
        }

        internal NHINDAddressCollection Bcc
        {
            get
            {
                if (m_bcc == null)
                {
                    m_bcc = NHINDAddressCollection.Parse(m_message.Bcc);
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
                
        internal NHINDAddressCollection CollectRecipients()
        {
            var addresses = new NHINDAddressCollection();
            if (To != null)
            {
                addresses.Add(To);
            }                
            if (Cc != null)
            {
                addresses.Add(Cc);
            }
            if (Bcc != null)
            {
                addresses.Add(Bcc);
            }
            return addresses;
        }

        internal void UpdateRoutingHeaders(NHINDAddressCollection rejectedRecipients)
        {
            if (rejectedRecipients.IsNullOrEmpty()) 
            {
                return;
            }

			this.UpdateRecipientHeader(To, MailStandard.ToHeader, rejectedRecipients);
            this.UpdateRecipientHeader(Cc, MailStandard.CcHeader, rejectedRecipients);
            this.UpdateRecipientHeader(Bcc, MailStandard.BccHeader, rejectedRecipients);
        }
        
    	void UpdateRecipientHeader(NHINDAddressCollection recipients, string headerName, IEnumerable<NHINDAddress> rejectedRecipients)
    	{
    		if (recipients != null) 
    		{
			    recipients.Remove(rejectedRecipients);
                this.Message.Headers[headerName] = (recipients.Count > 0) ? new Header(headerName, recipients.ToString()) : null;	   
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
        /// Categorize recipients as follows:
        /// - are they in the local domain or are they external
        /// </summary>
        /// <param name="domains"></param>
        internal virtual void CategorizeRecipientsByDomain(AgentDomains domains)
        {
            NHINDAddressCollection recipients = Recipients;
            NHINDAddressCollection domainRecipients = null;
            MailAddressCollection otherRecipients = null;

            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                NHINDAddress address = recipients[i];
                if (domains.IsManaged(address))
                {
                    if (domainRecipients == null)
                    {
                        domainRecipients = new NHINDAddressCollection();
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
            m_rejectedRecipients = NHINDAddressCollection.Create(Recipients.GetUntrusted(minTrustStatus));
            this.Recipients.RemoveUntrusted(minTrustStatus);
        }
    }    
}
