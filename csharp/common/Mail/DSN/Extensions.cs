/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Net.Mail;
using Health.Direct.Common.Mail.Notifications;

namespace Health.Direct.Common.Mail.DSN
{
    ///<summary>
    /// Extension methods related to DSN
    ///</summary>
    public static class Extensions
    {

        /// <summary>
        /// Provides the appropriate Action header value for the <paramref name="type"/>
        /// </summary>
        /// <param name="type">The DSN Action to translate</param>
        /// <returns>A string representation suitable for the Action header value</returns>
        public static string AsString(this DSNStandard.DSNAction type)
        {
            return DSNStandard.ToString(type);
        }

        /// <summary>
        /// Provides the appropriate MTA-name-type value for the <paramref name="type"/>
        /// </summary>
        /// <param name="type">The MTA-name-type to translate</param>
        /// <returns>A string representation suitable for MTA-name-type header value</returns>
        public static string AsString(this DSNStandard.MtaNameType type)
        {
            return DSNStandard.ToString(type);
        }

        /// <summary>
        /// Tests if this message IS an DSN
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public static bool IsDSN(this Message message)
        {
            return DSNStandard.IsReport(message);
        }

        /// <summary>
        /// Creates an DSN Notification for the given message
        /// </summary>
        /// <param name="from">PostalAddress this notification is from</param>
        /// <param name="message">source message</param>
        /// <param name="notification"></param>
        /// <returns>Null if no notification should be issued</returns>
        public static DSNMessage CreateStatusMessage(this Message message, MailAddress from, DSN notification)
        {
            if (from == null)
            {
                throw new ArgumentNullException("from");
            }

            if (notification == null)
            {
                throw new ArgumentNullException("notification");
            }

            if (message.IsDSN())
            {
                return null;
            }

            return DSNMessage.CreateNotificationFor(message, from, notification);
        }


        
        /// <summary>
        /// Creates an DSN Notification for the given message
        /// </summary>
        /// <param name="from">PostalAddress this notification is from</param>
        /// <param name="message">source message</param>
        /// <param name="dsn">The dsn to created</param>
        /// <returns>Null if no notification should be issued</returns>
        public static DSNMessage CreateNotificationMessage(this Message message, MailAddress from, DSN dsn)
        {
            if (from == null)
            {
                throw new ArgumentNullException("from");
            }

            if (dsn == null)
            {
                throw new ArgumentNullException("dsn");
            }

            if (!message.ShouldIssueNotification())
            {
                return null;
            }

            return DSNMessage.CreateNotificationFor(message, from, dsn);
        }

        /// <summary>
        /// Creates a deliver status notification (DSN) for the given <paramref name="message"/> to the <paramref name="senders"/>.
        /// </summary>
        /// <param name="message">The message for which to send notification</param>
        /// <param name="senders">The message senders to which to send notification</param>
        /// <param name="notificationCreator">A function creating dsn objects from addresses</param>
        /// <returns>An enumerator over dsn messages</returns>
        public static IEnumerable<DSNMessage> CreateNotificationMessages(this Message message, IEnumerable<MailAddress> senders, Func<MailAddress, DSN> notificationCreator)
        {
            if (senders == null)
            {
                throw new ArgumentNullException("senders");
            }
            if (notificationCreator == null)
            {
                throw new ArgumentNullException("notificationCreator");
            }

            if (message.IsDSN())
            {
                yield break;
            }

            foreach (MailAddress sender in senders)
            {
                DSN dsn = notificationCreator(sender);
                DSNMessage statusMessage = message.CreateStatusMessage(sender, dsn);
                if (statusMessage != null)
                {
                    yield return statusMessage;
                }
            }
        }
    }
}
