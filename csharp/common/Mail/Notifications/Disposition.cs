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
    /// Message Disposition Field
    /// </summary>
    public class Disposition
    {
        public Disposition(MDNStandard.NotificationType notification)
            : this(MDNStandard.TriggerType.Automatic, MDNStandard.SendType.Automatic, notification)
        {
        }

        public Disposition(MDNStandard.TriggerType triggerType, MDNStandard.SendType sendType, MDNStandard.NotificationType notification)
        {
            this.TriggerType = triggerType;
            this.SendType = sendType;
            this.Notification = notification;
        }
        
        /// <summary>
        /// Was the notification triggered automatically or manually (by the user?)
        /// </summary>
        public MDNStandard.TriggerType TriggerType
        {
            get;
            set;
        }

        public MDNStandard.SendType SendType
        {
            get;
            set;
        }

        public MDNStandard.NotificationType Notification
        {
            get;
            set;
        }
        
        public bool IsError
        {
            get;
            set;
        }
        
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
