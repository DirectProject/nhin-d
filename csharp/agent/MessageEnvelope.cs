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

    	public MessageEnvelope(Message message)
        {
            Message = message;
            Recipients = CollectRecipients();
            
            Header from = message.From;
            if (from == null)
            {
                throw new AgentException(AgentError.MissingFrom);
            }            
            Sender = new NHINDAddress(from.Value);
        }
        
        public MessageEnvelope(string messageText)
            : this(MimeSerializer.Default.Deserialize<Message>(messageText))
        {
            RawMessage = messageText;
        }
                
        public MessageEnvelope(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
        {
            Message = message;
            Recipients = recipients;
            Sender = sender;
        }
        
        public MessageEnvelope(string messageText, NHINDAddressCollection recipients, NHINDAddress sender)
            : this(MimeSerializer.Default.Deserialize<Message>(messageText), recipients, sender)
        {
            RawMessage = messageText;
        }
                
        protected MessageEnvelope(Message message, string rawMessage, NHINDAddressCollection recipients, NHINDAddress sender)
            : this(message, recipients, sender)
        {
            RawMessage = rawMessage;
        }
        
        internal MessageEnvelope(MessageEnvelope envelope)
        {
            m_agent = envelope.m_agent;
            RawMessage = envelope.RawMessage;
            m_message = envelope.m_message;
            if (envelope.m_recipients != null)
            {
                m_recipients = new NHINDAddressCollection {envelope.m_recipients};
            }
            
            m_sender = envelope.m_sender;
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
                RawMessage = null;
            }
        }
        
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
        
        public virtual NHINDAddressCollection Recipients
        {
            get
            {
                if (m_recipients == null)
                {
                    CollectRecipients();
                }
                return m_recipients;
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
                if (m_rejectedRecipients == null)
                {
                    m_rejectedRecipients = new NHINDAddressCollection();
                }

                return m_rejectedRecipients;
            }
        }

        public bool HasRejectedRecipients
        {
            get
            {
                return (m_rejectedRecipients != null && m_rejectedRecipients.Count > 0);
            }
        }

    	public NHINDAddressCollection DomainRecipients { get; internal set; }

    	public bool HasDomainRecipients
        {
            get
            {
                return (DomainRecipients != null && DomainRecipients.Count > 0);
            }
        }

    	public MailAddressCollection OtherRecipients { get; set; }

    	public bool HasOtherRecipients
        {
            get
            {
                return (OtherRecipients != null && OtherRecipients.Count > 0);
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
            DomainRecipients = null;
            OtherRecipients = null;
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
            if (rejectedRecipients == null || rejectedRecipients.Count == 0) return;

			UpdateRecipientHeader(To, MailStandard.ToHeader, rejectedRecipients, header => Message.To = header);
			UpdateRecipientHeader(Cc, MailStandard.CcHeader, rejectedRecipients, header =>  Message.Cc = header);
			UpdateRecipientHeader(Bcc, MailStandard.BccHeader, rejectedRecipients, header => Message.Bcc = header);
        }

		// TODO: revist this, I'm not sure this is any cleaner than before...
    	private static void UpdateRecipientHeader(NHINDAddressCollection recipients, string headerName, 
			IEnumerable<NHINDAddress> rejectedRecipients, Action<Header> headerSetter)
    	{
    		if (recipients == null) return;
   
			recipients.Remove(rejectedRecipients);
    		if (recipients.Count > 0)
    		{
    			headerSetter(new Header(headerName, recipients.ToString()));
    		}
    	}

    	internal void UpdateRoutingHeaders()
        {
            if (HasRejectedRecipients)
            {
                UpdateRoutingHeaders(RejectedRecipients);
            }
        }
        
        public void EnsureRecipientsCategorizedByDomain(AgentDomains domains)
        {
            if (HasDomainRecipients || HasOtherRecipients)
            {
                return;
            }
            
            CategorizeRecipientsByDomain(domains);
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

            DomainRecipients = domainRecipients;
            OtherRecipients = otherRecipients;
        }

        internal virtual void CategorizeRecipientsByTrust(TrustEnforcementStatus minTrustStatus)
        {
            m_rejectedRecipients = NHINDAddressCollection.Create(Recipients.GetUntrusted(minTrustStatus));
            Recipients.RemoveUntrusted(minTrustStatus);
        }
    }    
}
