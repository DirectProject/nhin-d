/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// Encapsulates message disposition status.
    /// </summary>
    public class Disposition
    {
        /// <summary>
        /// Initializes an instance with the specified disposition notification type and automatic modes
        /// </summary> 
        /// <param name="notification">The disposition notification type</param>
        public Disposition(MDNStandard.NotificationType notification)
            : this(MDNStandard.TriggerType.Automatic, MDNStandard.SendType.Automatic, notification)
        {
        }

        /// <summary>
        /// Initializes an instance with the specified disposition notification type and automatic modes
        /// </summary> 
        /// <param name="notification">The disposition notification type</param>
        /// <param name="isError">Notification is for an error</param>
        public Disposition(MDNStandard.NotificationType notification, bool isError)
            : this(MDNStandard.TriggerType.Automatic, MDNStandard.SendType.Automatic, notification, isError)
        {
        }

        /// <summary>
        /// Initializes an instance with the specified disposition notification type and action and sending modes
        /// </summary> 
        /// <param name="notification">The disposition notification type</param>
        /// <param name="sendType">The sending mode type</param>
        /// <param name="triggerType">The action (trigger) mode type</param>
        public Disposition(MDNStandard.TriggerType triggerType, MDNStandard.SendType sendType, MDNStandard.NotificationType notification)
            : this(triggerType, sendType, notification, false)
        {
        }

        /// <summary>
        /// Initializes an instance with the specified disposition notification type and action and sending modes
        /// </summary> 
        /// <param name="notification">The disposition notification type</param>
        /// <param name="sendType">The sending mode type</param>
        /// <param name="triggerType">The action (trigger) mode type</param>
        /// <param name="isError">Notification for an error</param>
        public Disposition(MDNStandard.TriggerType triggerType, MDNStandard.SendType sendType, MDNStandard.NotificationType notification, bool isError)
        {
            this.TriggerType = triggerType;
            this.SendType = sendType;
            this.Notification = notification;
            this.IsError = isError;
        }
        
        /// <summary>
        /// Trigger action that generated this disposition (action-mode)
        /// </summary>
        public MDNStandard.TriggerType TriggerType
        {
            get;
            internal set;
        }

        /// <summary>
        /// Sending type (system or user) that sent this dispositon (sending-mode)
        /// </summary>
        public MDNStandard.SendType SendType
        {
            get;
            internal set;
        }

        /// <summary>
        /// Type of disposition indicated
        /// </summary>
        public MDNStandard.NotificationType Notification
        {
            get;
            internal set;
        }
        
        /// <summary>
        /// Is this disposition an error report?
        /// </summary>
        public bool IsError
        {
            get;
            internal set;
        }
        
        /// <summary>
        /// Constructs the appropriate headers suitable for inclusion in an MDN report
        /// </summary>
        /// <returns>A string representation of the disposition suitable for inclusion in the MDN headers</returns>
        public override string ToString()
        {
            StringBuilder notification = new StringBuilder();
            //
            // Disposition Mode
            //
            notification.Append(this.TriggerType.AsString());
            notification.Append('/');
            notification.Append(this.SendType.AsString());
            notification.Append(';');
            //
            // Disposition Type & Modifier
            //
            notification.Append(this.Notification.AsString());
            if (this.IsError)
            {
                notification.Append('/');
                notification.Append(MDNStandard.Modifier_Error);
            }

            return notification.ToString();
        }        
    }
}