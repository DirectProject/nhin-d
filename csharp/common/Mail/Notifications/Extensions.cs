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
using Health.Direct.Common.Mail.DSN;

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// Extension methods relating to MDN
    /// </summary>
    public static class Extensions
    {
        /// <summary>
        /// Tests if this message has requested MDN notification.
        /// </summary>
        /// <remarks>The received message may be tested to see if it has a message disposition notification
        /// request, based on the <c>Disposition-Notification-To</c> header</remarks>
        /// <param name="message">The message to test.</param>
        /// <returns><c>true</c> if this message has requested disposition notification, <c>false</c> if not</returns>
        public static bool HasNotificationRequest(this Message message)
        {
            return message.HasHeader(MDNStandard.Headers.DispositionNotificationTo);
        }
        
        /// <summary>
        /// Tests if this message has requested MDN notification.
        /// </summary>
        /// <remarks>The received message may be tested to see if it has a message disposition notification
        /// request, based on the <c>Disposition-Notification-To</c> header</remarks>
        /// <param name="message">The message to test.</param>
        /// <returns><c>true</c> if this message has requested disposition notification, <c>false</c> if not</returns>
        public static bool HasDeliveryNotificationRequest(this Message message)
        {
            var optionsHeader = message.Headers.GetValue(MDNStandard.Headers.DispositionNotificationOptions);
            if(string.IsNullOrEmpty(optionsHeader))
            {
                return false;
            }
            if (optionsHeader.Contains(MDNStandard.DispositionOption_TimelyAndReliable))
            {
                return true;
            }
            return false;
        }
        

        /// <summary>
        /// Tests if this message IS an MDN
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public static bool IsMDN(this Message message)
        {
            return MDNStandard.IsReport(message);
        }

        /// <summary>
        /// Returns true if the user agent should issue a notification of positive delivery for this message.
        /// </summary>
        /// <remarks>Tests the message to see if it has a message disposition notification
        /// request, based on the <c>Disposition-Notification-Options</c> header and the
        /// parameter <c>X-DIRECT-FINAL-DESTINATION-DELIVERY</c> with an importance of <c>optional</c>
        /// and a value of <c>true</c> as per Implementation Guid for Delivery Notification in Direct, 1.3
        /// Additionally, verifies that the message is NOT itself an MDN. As per RFC 3798, agents should never
        /// issue an MDN in response to an MDN
        /// </remarks>
        /// <param name="message"></param>
        /// <returns></returns>
        public static bool IsTimelyAndReliable(this Message message)
        {
            return (!message.IsMDN() && message.HasDeliveryNotificationRequest());
        }

        /// <summary>
        /// Returns true if the user agent should issue a notification for this message.
        /// </summary>
        /// <remarks>Tests the message to see if it has a message disposition notification
        /// request, based on the <c>Disposition-Notification-To</c> header
        /// Additionally, verifies that the message is NOT itself an MDN. As per RFC 3798, agents should never
        /// issue an MDN in response to an MDN
        /// </remarks>
        /// <param name="message"></param>
        /// <returns></returns>
        public static bool ShouldIssueNotification(this Message message)
        {
            return (!message.IsMDN() && message.HasNotificationRequest());
        }
                
        /// <summary>
        /// Gets the value of the <c>Disposition-Notification-To</c> header, which indicates where
        /// the original UA requested notification be sent.
        /// </summary>
        /// <param name="message">The message to get the destination from.</param>
        /// <returns>The value of the header (which will be a comma separated list of addresses)</returns>
        public static string GetNotificationDestination(this Message message)
        {
            return message.Headers.GetValue(MDNStandard.Headers.DispositionNotificationTo);
        }

        /// <summary>
        /// Gets the mail addresses contained in the <c>Disposition-Notification-To</c> header, which indicates where
        /// the original UA requested notification be sent.
        /// </summary>
        /// <param name="message">The message to get the destination from.</param>
        /// <returns>a MailAddressCollection, or null if no header was found</returns>
        public static MailAddressCollection GetNotificationDestinationAddresses(this Message message)
        {
            return MailParser.ParseAddressCollection(message.Headers[MDNStandard.Headers.DispositionNotificationTo]);
        }
        
        /// <summary>
        /// Sets the header values for this message to request message disposition notification.
        /// </summary>
        /// <param name="message">The message for which to set the disposition request headers</param>
        public static void RequestNotification(this Message message)
        {
            if (message.IsMDN())
            {
                throw new MDNException(MDNError.CannotSendMDNForMDN);
            }
            if (message.IsDSN())
            {
                throw new DSNException(DSNError.CannotSendDSNForMDN);
            }

            string notificationTo = message.Headers.GetValue(MailStandard.Headers.Sender);
            if (string.IsNullOrEmpty(notificationTo))
            {
                notificationTo = message.Headers.GetValue(MailStandard.Headers.From);
            }
            
            message.Headers.SetValue(MDNStandard.Headers.DispositionNotificationTo, notificationTo);
        }

        /// <summary>
        /// Sets the header values for this message to request message disposition notification.
        /// </summary>
        /// <param name="message">The message for which to set the disposition request headers</param>
        /// <param name="notificationTo">Send notifications to this address</param>
        public static void RequestNotification(this Message message, MailAddress notificationTo)
        {
            if (message.IsMDN())
            {
                throw new MDNException(MDNError.CannotSendMDNForMDN);
            }
            if (notificationTo == null)
            {
                throw new ArgumentNullException("notificationTo");
            }
            
            message.Headers.SetValue(MDNStandard.Headers.DispositionNotificationTo, notificationTo.Address);
        }
                
        /// <summary>
        /// Creates an MDN Notification for the given message
        /// </summary>
        /// <param name="from">PostalAddress this notification is from</param>
        /// <param name="message">source message</param>
        /// <param name="notification"></param>
        /// <returns>Null if no notification should be issued</returns>
        public static NotificationMessage CreateNotificationMessage(this Message message, MailAddress from, Notification notification)
        {
            if (from == null)
            {
                throw new ArgumentNullException("from");
            }

            if (notification == null)
            {
                throw new ArgumentNullException("notification");
            }

            if (!message.ShouldIssueNotification())
            {
                return null;
            }
            
            return NotificationMessage.CreateNotificationFor(message, from, notification);
        }
        
        /// <summary>
        /// Creates a notification message (MDN) for the given <paramref name="message"/> to the <paramref name="senders"/>.
        /// </summary>
        /// <param name="message">The message for which to send notification</param>
        /// <param name="senders">The message senders to which to send notification</param>
        /// <param name="notificationCreator">A function creating notification objects from addresses</param>
        /// <returns>An enumerator over notification messages</returns>
        public static IEnumerable<NotificationMessage> CreateNotificationMessages(this Message message, IEnumerable<MailAddress> senders, Func<MailAddress, Notification> notificationCreator)
        {
            if (senders == null)
            {
                throw new ArgumentNullException("senders");
            }
            if (notificationCreator == null)
            {
                throw new ArgumentNullException("notificationCreator");
            }
            
            if (!message.ShouldIssueNotification())
            {
                yield break;
            }
            
            foreach (MailAddress sender in senders)
            {
                Notification notification = notificationCreator(sender);
                NotificationMessage notificationMessage = message.CreateNotificationMessage(sender, notification);
                if (notificationMessage  != null)
                {
                    yield return notificationMessage;
                }
            }
        }



        /// <summary>
        /// Provides the appropriate <c>Disposition</c> header value for the <paramref name="type"/>
        /// </summary>
        /// <param name="type">The mode to translate</param>
        /// <returns>A string representation suitable for inclusion in the action mode section of the <c>Disposition</c> header value</returns>
        public static string AsString(this MDNStandard.TriggerType type)
        {
            return MDNStandard.ToString(type);
        }

        /// <summary>
        /// Provides the appropriate <c>Disposition</c> header value for the <paramref name="type"/>
        /// </summary>
        /// <param name="type">The mode to translate</param>
        /// <returns>A string representation suitable for inclusion in the sending mode section of the <c>Disposition</c> header value</returns>
        public static string AsString(this MDNStandard.SendType type)
        {
            return MDNStandard.ToString(type);
        }

        /// <summary>
        /// Provides the appropriate <c>Disposition</c> header value for the <paramref name="type"/>
        /// </summary>
        /// <param name="type">The type to translate</param>
        /// <returns>A string representation suitable for inclusion in the disposition type section of the <c>Disposition</c> header value</returns>
        public static string AsString(this MDNStandard.NotificationType type)
        {
            return MDNStandard.ToString(type);
        }
    }
}