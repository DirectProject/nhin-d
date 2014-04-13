/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook	    jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Net.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Mail.DSN
{
    /// <summary>
    /// Represents a delivery status notification (DSN) sent to a message sender, as per RFC 3798
    /// </summary>
    public class DSNMessage : Message
    {
        /// <summary>
        /// Initializes a DSN to the specified recipient.
        /// </summary>
        /// <param name="to">The DSN recipient.</param>
        /// <param name="notification">The notification to send.</param>
        public DSNMessage(string to, DSN notification)
            : this(to, null, notification)
        {
        }

        /// <summary>
        /// Initializes a DSN to the specified recipient.
        /// </summary>
        /// <param name="to">The DSN recipient.</param>
        /// <param name="from">Postmaster</param>
        /// <param name="notification">The dsn to send.</param>
        public DSNMessage(string to, string from, DSN notification)
            : base(to, from)
        {
            if (notification == null)
            {
                throw new ArgumentNullException("notification");
            }
            
            this.SetParts(notification);
       }

        /// <summary>
        /// Takes a message and constructs an DSN for it.
        /// </summary>
        /// <param name="message">The message to send notification about.</param>
        /// <param name="from">MailAddress this notification is from</param>
        /// <param name="dsn">The dsn to create.</param>
        /// <returns>The DSN.</returns>
        public static DSNMessage CreateNotificationFor(Message message, MailAddress from, DSN dsn)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            if (from == null)
            {
                throw new ArgumentNullException("from");
            }
            if (dsn == null)
            {
                throw new ArgumentNullException("dsn");
            }
            //
            // Verify that the message is not itself an MDN!
            //
            if (message.IsMDN())
            {
                throw new ArgumentException("Message is an MDN");
            }

            string notifyTo = message.From.Value;

            DSNMessage statusMessage = new DSNMessage(notifyTo, from.ToString(), dsn);
            statusMessage.AssignMessageID();

            statusMessage.SubjectValue = string.Format("{0}:{1}", "Rejected", message.SubjectValue);
            
            statusMessage.Timestamp();

            return statusMessage;
        }
    }
}