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

using Health.Direct.Agent;

using CDO;
using ADODB;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// Puts an ISmtpMessage wrapper around a a CDO.Message
    /// </summary>
    public class CDOSmtpMessage : ISmtpMessage
    {
        CDO.Message m_message;
        bool? m_hasEnvelope;
        
        public CDOSmtpMessage(CDO.Message message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            m_message = message;
        }
        
        public bool HasEnvelope
        {
            get
            {
                if (m_hasEnvelope == null)
                {
                    Fields envelopeFields = m_message.GetEnvelopeFields();
                    m_hasEnvelope = (envelopeFields != null && envelopeFields.Count > 0);
                }
                
                return m_hasEnvelope.Value;
            }
        }
        
        public CDO.Message InnerMessage
        {
            get
            {
                return m_message;
            }
        }
        
        public string GetMailFrom()
        {
            if (!this.HasEnvelope)
            {
                return string.Empty;
            }
            
            return m_message.GetEnvelopeSender();
        }
        
        public string GetRcptTo()
        {
            if (!this.HasEnvelope)
            {
                return string.Empty;
            }

            return m_message.GetEnvelopeRecipients();
        }
        
        public MessageEnvelope GetEnvelope()
        {
            return this.CreateEnvelope(m_message);
        }
        
        public string GetMessageText()
        {
            return m_message.GetMessageText();
        }
        
        public void SetRcptTo(DirectAddressCollection recipients)
        {
            if (recipients == null)
            {
                throw new ArgumentNullException("recipients");
            }
            
            if (this.HasEnvelope)
            {
                m_message.SetEnvelopeRecipients(recipients.ToMailAddressCollection());
            }
        }
                
        public void Update(string messageText)
        {
            if (string.IsNullOrEmpty(messageText))
            {
                throw new ArgumentException("value was null or empty", "messageText");
            }

            m_message.SetMessageText(messageText, true);
        }
        
        public void Accept()
        {
            m_message.SetMessageStatus(CdoMessageStat.cdoStatSuccess);
        }
        
        public void Reject()
        {
            this.Abort();
        }

        public void Abort()
        {
            m_message.AbortMessage();
        }
                
        public void SaveToFile(string filePath)
        {
            if (string.IsNullOrEmpty(filePath))
            {
                throw new ArgumentException("value was null or empty", "filePath");
            }
            
            m_message.SaveToFile(filePath);
        }
        
        MessageEnvelope CreateEnvelope(CDO.Message message)
        {
            DirectAddressCollection recipientAddresses = null;
            DirectAddress senderAddress = null;
            MessageEnvelope envelope;

            string messageText = message.GetMessageText();

            if (this.ExtractEnvelopeFields(message, ref recipientAddresses, ref senderAddress))
            {
                envelope = new MessageEnvelope(messageText, recipientAddresses, senderAddress);
            }
            else
            {
                envelope = new MessageEnvelope(messageText);
            }

            return envelope;
        }
        
        //
        // A CDO Message could be arriving via the SMTP server, or could have been constructed manually
        // The one created by SMTP has envelope information
        // Returns false if no envelope info is available. We have to look within message headers in that case
        //
        bool ExtractEnvelopeFields(CDO.Message message, ref DirectAddressCollection recipientAddresses, ref DirectAddress senderAddress)
        {
            if (!this.HasEnvelope)
            {
                //
                // No envelope
                //
                return false;
            }

            recipientAddresses = null;
            senderAddress = null;

            string sender = message.GetEnvelopeSender();
            if (string.IsNullOrEmpty(sender))
            {
                throw new SmtpAgentException(SmtpAgentError.NoSenderInEnvelope);
            }
            //
            // In SMTP Server, the MAIL TO (sender) in the envelope can be empty if the message is from the server postmaster 
            // The actual postmaster address is found in the message itself
            //
            if (Health.Direct.SmtpAgent.Extensions.IsSenderLocalPostmaster(sender))
            {
                return false;
            }
            string recipients = message.GetEnvelopeRecipients();
            if (string.IsNullOrEmpty(recipients))
            {
                throw new SmtpAgentException(SmtpAgentError.NoRecipientsInEnvelope);
            }

            recipientAddresses = DirectAddressCollection.ParseSmtpServerEnvelope(recipients);
            senderAddress = new DirectAddress(sender);

            return true;
        }
    }
}