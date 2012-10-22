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
        public void Send(OutgoingMessage envelope, string pickupFolder, DirectAddressCollection recipients, 
            DSNStandard.DSNAction dsnAction, int classSubCode, string subjectSubCode)
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
                DSNMessage notification = this.Produce(envelope, recipients.AsMailAddresses(), dsnAction, classSubCode,
                                                   subjectSubCode);

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
        /// <param name="recipients">sending acks to these message recipients</param>
        /// <param name="action">DSN action</param>
        /// <param name="classSubCode">Status code class</param>
        /// <param name="subjectSubCode">Status code subject</param>
        /// <returns>An DSNmessages</returns>
        public DSNMessage Produce(OutgoingMessage envelope, IEnumerable<MailAddress> recipients, DSNStandard.DSNAction action, int classSubCode, string subjectSubCode)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }

            DSNMessage dsnMessage = envelope.CreateDeliveryStatus(recipients, m_settings.ProductName, m_settings.Text, m_settings.AlwaysAck, action, classSubCode, subjectSubCode);
            return dsnMessage;
            
        }
    }
}