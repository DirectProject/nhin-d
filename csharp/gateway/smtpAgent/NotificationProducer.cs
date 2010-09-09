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
using System.IO;
using System.Net.Mail;
using NHINDirect;
using NHINDirect.Mail;
using NHINDirect.Agent;
using NHINDirect.Mail.Notifications;

namespace NHINDirect.SmtpAgent
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
                
        public void Send(IncomingMessage envelope, string pickupFolder)
        {
            if (string.IsNullOrEmpty(pickupFolder))
            {
                throw new ArgumentException("value null or empty", "pickupFolder");
            }
            
            IEnumerable<MailAddress> senders = envelope.DomainRecipients.AsMailAddresses();
            IEnumerable<NotificationMessage> notifications = envelope.Message.CreateNotificationMessages(senders, this.CreateAck);
            foreach(NotificationMessage notification in notifications)
            {
                string filePath = Path.Combine(pickupFolder, notification.IDValue + ".eml");
                notification.Save(filePath);
            }
        }
        
        /// <summary>
        /// Generate notification messages (if any) for this source message
        /// </summary>
        /// <param name="envelope"></param>
        /// <returns></returns>
        public IEnumerable<NotificationMessage> Produce(IncomingMessage envelope)
        {
            if (envelope == null)
            {
                throw new ArgumentException();
            }

            if (!m_settings.AutoResponse || !envelope.HasDomainRecipients || !envelope.Message.ShouldIssueNotification())
            {
                yield break;
            }

            IEnumerable<MailAddress> senders = envelope.DomainRecipients.AsMailAddresses();
            IEnumerable<NotificationMessage> notifications = envelope.Message.CreateNotificationMessages(senders, this.CreateAck);
            //
            // We do our own foreach in case we want to inject additional behavior (such as logging) here...
            //
            foreach (NotificationMessage notification in notifications)
            {
                yield return notification;
            }
        }
        
        Notification CreateAck(MailAddress address)
        {
            Notification notification = new Notification(MDNStandard.NotificationType.Processed);
            if (m_settings.HasText)
            {
                notification.Explanation = m_settings.Text;
            }
            notification.ReportingAgent = new ReportingUserAgent(address.Host, m_settings.ProductName);            
            return notification;
        }
    }
}
