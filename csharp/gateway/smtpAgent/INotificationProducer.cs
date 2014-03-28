using System.Collections.Generic;
using System.Net.Mail;
using Health.Direct.Agent;
using Health.Direct.Common.Mail.Notifications;

namespace Health.Direct.SmtpAgent
{
    public interface INotificationProducer
    {
        void Send(IncomingMessage envelope, string pickupFolder, DirectAddressCollection senders,
                  MDNStandard.NotificationType notificationType);

        void SendFailure(OutgoingMessage envelope, string pickupFolder);

        void SendFailure(IncomingMessage envelope, string pickupFolder, DirectAddressCollection recipients);

        IEnumerable<NotificationMessage> Produce(IncomingMessage envelope);

        IEnumerable<NotificationMessage> Produce(IncomingMessage envelope, IEnumerable<MailAddress> senders,
                                                 MDNStandard.NotificationType notificationType);

        /// <summary>
        /// Notify resolution of temporary failure.
        /// </summary>
        /// <remarks>
        /// An implementer my have a retry queue.  If the retry succeeds this is the time to notify of it's success.
        /// </remarks>
        /// <param name="messageId"></param>
        void Resolved(string messageId);
    }
}