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
    /// Message Disposition Notifications
    /// RFC 3798
    /// http://tools.ietf.org/html/rfc3798
    /// </summary>
    public class MDNStandard : MailStandard
    {
        /// <summary>
        /// How was the notification triggered? Automatically or initiated by the user manually?
        /// </summary>
        public enum TriggerType
        {
            Automatic,
            UserInitiated
        }
        /// <summary>
        /// Was the user asked if the notification could be sent?
        /// </summary>
        public enum SendType
        {
            Automatic,
            UserMediated
        }
        
        public enum NotificationType
        {
            Processed,
            Displayed,
            Deleted
        }
        
        public enum ErrorType
        {
            Error,
            Failure,
            Warning
        }
        //
        // MIME Types
        //
        public new class MediaType : MailStandard.MediaType
        {
            public const string ReportMessage = "multipart/report";
            public const string DispositionReport = ReportMessage + "; report-type=disposition-notification";
            public const string DispositionNotification = "message/disposition-notification";
        }
        //
        // Fields
        //
        public new class Headers : MailStandard.Headers
        {
            public const string Disposition = "Disposition";
            public const string DispositionNotificationTo = "Disposition-Notification-To";
            public const string DispositionNotificationOptions = "Disposition-Notification-Options";
            public const string ReportingAgent = "Reporting-UA";
            public const string Gateway = "MDN-Gateway";
            public const string OriginalMessageID = "Original-Message-ID";
            public const string Failure = "Failure";
            public const string Error = "Error";
            public const string Warning = "Warning";
        }
                
        internal const string Action_Manual = "manual-action";
        internal const string Action_Automatic = "automatic-action";
        internal const string Send_Manual = "MDN-sent-manually";
        internal const string Send_Automatic = "MDN-sent-automatically";
        internal const string Disposition_Displayed = "displayed";
        internal const string Disposition_Processed = "processed";
        internal const string Disposition_Deleted = "deleted";
        internal const string Modifier_Error = "error";
        
        public static bool HasMDNRequest(MimeEntity entity)
        {
            if (entity == null)
            {
                return false;
            }
            
            return entity.HasHeader(Headers.DispositionNotificationTo);
        }
                
        public static bool IsReport(MimeEntity entity)
        {
            if (entity == null)
            {
                return false;
            }
            
            return entity.HasMediaType(MDNStandard.MediaType.ReportMessage);
        }
        
        public static bool IsNotification(MimeEntity entity)
        {
            if (entity == null)
            {
                return false;
            }

            return entity.HasMediaType(MDNStandard.MediaType.DispositionNotification);
        }
        
        public static string ToString(TriggerType mode)
        {
            switch(mode)
            {
                default:
                    throw new NotSupportedException();
                
                case TriggerType.Automatic:
                    return Action_Automatic;
                
                case TriggerType.UserInitiated:
                    return Action_Manual;
            }
        }
        
        public static string ToString(SendType mode)
        {
            switch(mode)
            {
                default:
                    throw new NotSupportedException();

                case SendType.Automatic:
                    return Send_Automatic;

                case SendType.UserMediated:
                    return Send_Manual;
            }
        }

        public static string ToString(NotificationType type)
        {
            switch (type)
            {
                default:
                    throw new NotSupportedException();

                case NotificationType.Processed:
                    return Disposition_Processed;

                case NotificationType.Displayed:
                    return Disposition_Displayed;
                
                case NotificationType.Deleted:
                    return Disposition_Deleted;
            }
        }
    }
}
