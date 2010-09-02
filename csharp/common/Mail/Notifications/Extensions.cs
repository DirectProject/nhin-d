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

namespace NHINDirect.Mail.Notifications
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
        
        //TODO: would be nicer to return IEnumeration<MailAddress>
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
        /// Sets the header values for this message to request message disposition notification.
        /// </summary>
        /// <param name="message">The message for which to set the disposition request headers</param>
        public static void RequestNotification(this Message message)
        {
            message.Headers.SetValue(MDNStandard.Headers.DispositionNotificationTo, message.FromValue);
        }
        
        public static string AsString(this MDNStandard.TriggerType type)
        {
            return MDNStandard.ToString(type);
        }

        public static string AsString(this MDNStandard.SendType type)
        {
            return MDNStandard.ToString(type);
        }
        
        public static string AsString(this MDNStandard.NotificationType type)
        {
            return MDNStandard.ToString(type);
        }
    }
}
