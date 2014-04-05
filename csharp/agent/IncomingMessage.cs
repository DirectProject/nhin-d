/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook	    jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Net.Mail;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Cryptography;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents an incoming message, with sender, receivers, and message to be decrypted and verified.
    /// </summary>
    public class IncomingMessage : MessageEnvelope
    {
        SignedCms m_signatures;                             // All signatures + info about the signed blob etc
        MessageSignatureCollection m_senderSignatures;      // The sender's signatures, which are a subset of m_signatures
        //
        // Temporary state associated with decryption of the incoming message
        //
        byte[] m_encryptedBytes;
        
        /// <summary>
        /// Creates an instance from an RFC 5322 format message string.
        /// </summary>
        /// <param name="messageText">RFC 5322 message string, signed and encrypted.</param>
        public IncomingMessage(string messageText)
            : base(messageText)
        {
        }

        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance.
        /// </summary>
        /// <param name="message"><see cref="Message"/> instance, signed and encrypted.</param>
        public IncomingMessage(Message message)
            : base(message)
        {
        }

        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance, specifying recipients and sender.
        /// </summary>
        /// <param name="message"><see cref="Message"/> instance, signed and encrypted.</param>
        /// <param name="recipients">An <see cref="DirectAddress"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddressCollection"/>, takes precendence over the <c>To</c> field in the message.</param>
        public IncomingMessage(Message message, DirectAddressCollection recipients, DirectAddress sender)
            : base(message, recipients, sender)
        {
        }

        /// <summary>
        /// Creates an instance from an RFC 5322 format message string., specifying recipients and sender.
        /// </summary>
        /// <param name="messageText">RFC 5322 message string, signed and encrypted.</param>
        /// <param name="recipients">An <see cref="DirectAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        public IncomingMessage(string messageText, DirectAddressCollection recipients, DirectAddress sender)
            : base(messageText, recipients, sender)
        {
        }        
         
        internal IncomingMessage(MessageEnvelope envelope)
            : base(envelope)
        {
        }
        
        /// <summary>
        /// Gets the signatures attached to this message in <see cref="SignedCms"/> format for manipulation with
        /// the raw .Net API.
        /// </summary>
        /// <remarks>The <c>SenderSignatures</c> property is generally easier to work with.</remarks>
        /// <value>A <see cref="SignedCms"/> instance representing signatures for this message.</value>
        public SignedCms Signatures
        {
            get
            {
                return this.m_signatures;
            }
            internal set
            {
                this.m_signatures = value;
            }
        }
        
        /// <summary>
        /// Gets if this message has signatures
        /// </summary>
        /// <value><c>true</c> if this message has signatures, <c>false</c> otherwise.</value>
        public bool HasSignatures
        {
            get
            {
                return (m_signatures != null);
            }
        }
        
        /// <summary>
        /// Gets the <see cref="MessageSignatureCollection"/> for this message
        /// </summary>
        /// <value>A <see cref="MessageSignatureCollection"/> of <see cref="MessageSignature"/> instances for each sender signature or <c>null</c> if there are no signatures.</value>
        public MessageSignatureCollection SenderSignatures
        {
            get
            {
                return m_senderSignatures;
            }
            internal set
            {
                m_senderSignatures = value;
            }
        }
        
        /// <summary>
        /// Gets if this message has sender signatures.
        /// </summary>
        /// <value><c>true</c> if this message has sender signatures, <c>false</c> otherwise.</value>
        public bool HasSenderSignatures
        {
            get
            {
                return (m_senderSignatures != null && m_senderSignatures.Count > 0);
            }
        }

        /// <summary>
        /// Create ACK MDN notifications for this message - IF MDNs should be generated. 
        /// Since the message could have multiple recipients, an independant MDN is generated FROM each recipient. 
        /// If no MDNs should be generated, returns NULL. 
        ///   - If there are no trusted domain recipients for the message, meaning there is nothing to ACK
        ///   - If the message is itself an MDN
        /// </summary>
        /// <param name="reportingAgentName">The name of the MTA or MUA that is generating this ack</param>
        /// <param name="textMessage">Optional text message to accompany the Ack</param>
        /// <param name="notificationType">processed, dispatched or failed</param>
        /// <returns>An enumeration of NotificationMessage</returns>
        public IEnumerable<NotificationMessage> CreateAcks(string reportingAgentName, string textMessage, MDNStandard.NotificationType notificationType)
        {
            return CreateAcks(reportingAgentName, textMessage, true, notificationType);
        }

        /// <summary>
        /// Create ACK MDN notifications for this message - IF MDNs should be generated. 
        /// Since the message could have multiple recipients, an independant MDN is generated FROM each recipient. 
        /// If no MDNs should be generated, returns NULL. 
        ///   - If there are no trusted domain recipients for the message, meaning there is nothing to ACK
        ///   - If the message is itself an MDN
        /// </summary>
        /// <param name="reportingAgentName">The name of the MTA or MUA that is generating this ack</param>
        /// <param name="textMessage">Optional text message to accompany the Ack</param>
        /// <param name="alwaysAck">Generate acks even when none were requested</param>
        /// <param name="notificationType">processed, dispatched or failed</param>
        /// <returns>An enumeration of NotificationMessage</returns>
        public IEnumerable<NotificationMessage> CreateAcks(string reportingAgentName, string textMessage, bool alwaysAck, MDNStandard.NotificationType notificationType)
        {
            if (!this.HasDomainRecipients)
            {
                return null;
            }
            
            return CreateAcks(this.DomainRecipients.AsMailAddresses(), reportingAgentName, textMessage, alwaysAck, notificationType);
        }

        /// <summary>
        /// Create ACK MDN notifications for this message - IF MDNs should be generated. 
        /// Since the message could have multiple recipients, an independant MDN is generated FROM each recipient. 
        /// If no MDNs should be generated, returns NULL. 
        ///   - If there are no trusted domain recipients for the message, meaning there is nothing to ACK
        ///   - If the message is itself an MDN
        /// </summary>
        /// <param name="senders">Sending Acks on behalf of these recipients</param>
        /// <param name="reportingAgentName">The name of the MTA or MUA that is generating this ack</param>
        /// <param name="textMessage">Optional text message to accompany the Ack</param>
        /// <param name="alwaysAck">Generate acks even when none were requested</param>
        /// <param name="notificationType">processed, dispatched or failed</param>
        /// <returns>An enumeration of NotificationMessage</returns>
        public IEnumerable<NotificationMessage> CreateAcks(IEnumerable<MailAddress> senders, string reportingAgentName, string textMessage, bool alwaysAck, MDNStandard.NotificationType notificationType)
        {
            if (senders == null)
            {
                throw new ArgumentException("senders");
            }
            
            if (string.IsNullOrEmpty(reportingAgentName))
            {
                throw new ArgumentException("reportingAgentName");
            }

            if (this.Message.IsMDN())
            {
                return null;
            }

            if (!this.Message.HasNotificationRequest())
            {
                if (!alwaysAck)
                {
                    return null;
                }

                //
                // Although an MDN was not explicitly requested, we are going to send one (as per the Direct spec). 
                // To allow the MDN code to remain MDN RFC compliant & work naturally, we inject an MDN requested header 
                // on behalf of the sender
                //
                this.Message.RequestNotification(this.Sender);
            }

            return this.Message.CreateNotificationMessages(senders,
                                                           sender => Notification.CreateAck(new ReportingUserAgent(sender.Host, reportingAgentName), textMessage, notificationType)
                                                           );

        }



        internal byte[] GetEncryptedBytes(SMIMECryptographer cryptographer)
        {
            if (m_encryptedBytes == null)
            {
                m_encryptedBytes = cryptographer.GetEncryptedBytes(this.Message);
            }
            
            return m_encryptedBytes;
        }
   }
}