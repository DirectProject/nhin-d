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
                throw new ArgumentNullException();
            }
            
            m_settings = settings;
        }
                
        public void Send(IncomingMessage envelope, string pickupFolder)
        {
            if (string.IsNullOrEmpty(pickupFolder))
            {
                throw new ArgumentException();
            }
            
            foreach(NotificationMessage notificationMessage in this.Produce(envelope))
            {
                string filePath = Path.Combine(pickupFolder, notificationMessage.IDValue + ".eml");
                notificationMessage.Save(filePath);
            }
        }

        public IEnumerable<NotificationMessage> Produce(IncomingMessage envelope)
        {
            if (envelope == null)
            {
                throw new ArgumentException();
            }

            if (!m_settings.AutoResponse || !envelope.HasDomainRecipients || !envelope.Message.HasNotificationRequest())
            {
                yield break;
            }
            
            Notification notification = new Notification(MDNStandard.NotificationType.Processed);
            if (m_settings.HasText)
            {
                notification.Explanation = m_settings.Text;
            }

            NotificationMessage notificationMessage = NotificationMessage.CreateNotificationFor(envelope.Message, notification);
            foreach (NHINDAddress sender in envelope.DomainRecipients)
            {
                notificationMessage.FromValue = sender.ToString();
                notificationMessage.IDValue = Guid.NewGuid().ToString("D");
                yield return notificationMessage;
            }
        }
    }
}
