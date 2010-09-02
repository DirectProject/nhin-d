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
using System.Net.Mail;
using NHINDirect.Mime;
using NHINDirect.Mail;

namespace NHINDirect.Mail.Notifications
{
    /// <summary>
    /// Can be used to send Message Disposition Notifications, as per RFC 3798
    /// </summary>
    public class NotificationMessage : Message
    {        
        public NotificationMessage(string to, Notification notification)
            : this(to, null, notification)
        {
        }
                        
        public NotificationMessage(string to, string from, Notification notification)
            : base(to, from)
        {
            this.SetParts(notification);
        }                
        
        public static NotificationMessage CreateNotificationFor(Message message, Notification notification)
        {
            if (message == null || notification == null)
            {
                throw new ArgumentNullException();
            }
            //
            // Verify that the message is not itself an MDN!
            //
            if (message.IsMDN())
            {
                throw new ArgumentException("Message is an MDN");
            }
            
            string notifyTo = message.GetNotificationDestination();
            if (string.IsNullOrEmpty(notifyTo))
            {
                throw new ArgumentException("Invalid Disposition-Notification-To Header");
            }
            
            string originalMessageID = message.IDValue;
            if (!string.IsNullOrEmpty(originalMessageID))
            {
                notification.OriginalMessageID = originalMessageID;
            }
            
            NotificationMessage notificationMessage = new NotificationMessage(notifyTo, notification);
            notificationMessage.IDValue = Guid.NewGuid().ToString("D");
            
            return notificationMessage;
        }
    }
}
