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
using System.IO;
using System.Linq;
using System.Net.Mail;
using Health.Direct.Agent;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Extensions;

namespace Health.Direct.SmtpAgent
{
    public class NotificationProducer
    {
        NotificationSettings m_settings;
        
        public NotificationProducer(NotificationSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException("settings");
            }
            
            m_settings = settings;
        }
        
        /// <summary>
        /// To simplify outbound mail sending, SMTP Server allows you to drop new messages into a pickup folder
        /// You don't need to use SmtpClient or some other SMTP client
        /// </summary>
        public void Send(IncomingMessage envelope, string pickupFolder, DirectAddressCollection senders, MDNStandard.NotificationType notificationType)
        {
            if (string.IsNullOrEmpty(pickupFolder))
            {
                throw new ArgumentException("value null or empty", "pickupFolder");
            }
            
            if (senders.IsNullOrEmpty())
            {
                return;
            }
            
            foreach (NotificationMessage notification in this.Produce(envelope, senders.AsMailAddresses(), notificationType))
            {
                string filePath = Path.Combine(pickupFolder, Extensions.CreateUniqueFileName());
                notification.Save(filePath);
            }
        }


        /// <summary>
        /// To simplify inbound mail sending, SMTP Server allows you to drop new messages into a pickup folder
        /// You don't need to use SmtpClient or some other SMTP client
        /// </summary>
        public void SendFailure(OutgoingMessage envelope, string pickupFolder, DirectAddressCollection recipients) 
        {
            if (string.IsNullOrEmpty(pickupFolder))
            {
                throw new ArgumentException("value null or empty", "pickupFolder");
            }

            if (recipients.IsNullOrEmpty())
            {
                return;
            }

            if (recipients != null && envelope.UsingDeliveryStatus)
            {
                DSNMessage notification = this.ProduceFailure(envelope, recipients);
                           

                string filePath = Path.Combine(pickupFolder, Extensions.CreateUniqueFileName());
                notification.Save(filePath);
            }
            

            //Or maybe
            //
            // m_router.Route(message, envelope, routedRecipients);  
            // 
            // This would avoid loopback encrypt/decrypt...
            //
            // ISmtpMessage message
            // MessageEnvelope envelope 
            // DirectAddressCollection routedRecipients, but would use DSN in-reply-to:
            //
            
            
        }

        /// <summary>
        /// To simplify outbound mail sending, SMTP Server allows you to drop new messages into a pickup folder
        /// You don't need to use SmtpClient or some other SMTP client
        /// </summary>
        public void SendFailure(IncomingMessage envelope, string pickupFolder, DirectAddressCollection recipients)
        {
            if (string.IsNullOrEmpty(pickupFolder))
            {
                throw new ArgumentException("value null or empty", "pickupFolder");
            }

            if (recipients.IsNullOrEmpty())
            {
                return;
            }

            if (recipients != null)
            {
                DSNMessage notification = this.ProduceFailure(envelope, recipients);

                string filePath = Path.Combine(pickupFolder, Extensions.CreateUniqueFileName());
                notification.Save(filePath);
            }


            //Or maybe
            //
            // m_router.Route(message, envelope, routedRecipients);  
            // 
            // This would avoid loopback encrypt/decrypt...
            //
            // ISmtpMessage message
            // MessageEnvelope envelope 
            // DirectAddressCollection routedRecipients, but would use DSN in-reply-to:
            //


        }



        /// <summary>
        /// Generate notification messages (if any) for this source message
        /// </summary>
        /// <param name="envelope"></param>
        /// <returns>An enumeration of notification messages</returns>
        public IEnumerable<NotificationMessage> Produce(IncomingMessage envelope)
        {
            return this.Produce(envelope, envelope.HasDomainRecipients ? envelope.DomainRecipients.AsMailAddresses() : null, MDNStandard.NotificationType.Processed);
        }

        /// <summary>
        /// Generate notification messages (if any) for this source message
        /// </summary>
        /// <param name="envelope"></param>
        /// <param name="senders">sending acks on behalf of these message recipients</param>
        /// <param name="notificationType">processed or dispatched</param>
        /// <returns>An enumeration of messages</returns>
        public IEnumerable<NotificationMessage> Produce(IncomingMessage envelope, IEnumerable<MailAddress> senders, MDNStandard.NotificationType notificationType)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }              
            if (senders != null && m_settings.AutoResponse)
            {
                IEnumerable<NotificationMessage> notifications = envelope.CreateAcks(senders, m_settings.ProductName, m_settings.Text, m_settings.AlwaysAck, notificationType);
                if (notifications != null)
                {
                    foreach (NotificationMessage notification in notifications)
                    {
                        yield return notification;
                    }
                }
            }
        }

        /// <summary>
        /// Generate internal notification messages (if any) for this outgoing message
        /// </summary>
        /// <param name="envelope"></param>
        /// <param name="recipients">sending failure status for these message recipients</param>
        /// <returns>An DSNmessage</returns>
        public DSNMessage ProduceFailure(OutgoingMessage envelope, DirectAddressCollection recipients)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }

            if (string.IsNullOrEmpty(m_settings.ProductName))
            {
                throw new ArgumentException("reportingAgentName:AgentSettings:ProductName");
            }

            DSNPerMessage perMessage = new DSNPerMessage(envelope.Sender.Host, envelope.Message.IDValue);

            //
            // Un-Secured recipients
            //
            List<DSNPerRecipient> dsnPerRecipients = envelope.CreatePerRecipientStatus(recipients.UnResolvedCertificates().AsMailAddresses()
                , m_settings.Text, m_settings.AlwaysAck, DSNStandard.DSNAction.Failed, DSNStandard.DSNStatus.Permanent
                , DSNStandard.DSNStatus.UNSECURED_STATUS).ToList();
            //
            // Un-Trusted recipients
            //
            dsnPerRecipients.AddRange(envelope.CreatePerRecipientStatus(recipients.ResolvedCertificates().AsMailAddresses()
                , m_settings.Text, m_settings.AlwaysAck, DSNStandard.DSNAction.Failed, DSNStandard.DSNStatus.Permanent
                , DSNStandard.DSNStatus.UNTRUSTED_STATUS).ToList());

            DSN dsn = new DSN(perMessage, dsnPerRecipients);

            //Configure and/or dynamic plus refactor
            return envelope.Message.CreateStatusMessage(new MailAddress("Postmaster@" + envelope.Sender.Host), dsn);

        }



        /// <summary>
        /// Generate external notification messages for failed final destination delivery
        /// </summary>
        /// <param name="envelope"></param>
        /// <param name="recipients">sending failure status for these message recipients</param>
        /// <returns>An DSNmessage</returns>
        public DSNMessage ProduceFailure(IncomingMessage envelope, DirectAddressCollection recipients)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }

            if (string.IsNullOrEmpty(m_settings.ProductName))
            {
                throw new ArgumentException("reportingAgentName:AgentSettings:ProductName");  
            }

            DSNPerMessage perMessage = new DSNPerMessage(envelope.Sender.Host, envelope.Message.IDValue);

            //
            // Un-Deliverable recipients
            //
            List<DSNPerRecipient> dsnPerRecipients = envelope.CreatePerRecipientStatus(recipients.AsMailAddresses()
                , m_settings.Text, m_settings.AlwaysAck, DSNStandard.DSNAction.Failed, DSNStandard.DSNStatus.Permanent
                , DSNStandard.DSNStatus.DELIVERY_OTHER).ToList();
            

            DSN dsn = new DSN(perMessage, dsnPerRecipients);

            //Configure and/or dynamic plus refactor
            //TODO: Split messages by domain.  See envelope.Recipients[0] below.
            return envelope.Message.CreateStatusMessage(new MailAddress("Postmaster@" + envelope.Recipients[0].Host), dsn);

        }
    }
}