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
using System.Net.Mime;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// Provides constants and utility functions for working with MDN
    /// </summary>
    /// <remarks>
    /// Message Disposition Notifications
    /// RFC 3798
    /// http://tools.ietf.org/html/rfc3798
    /// </remarks>
    public class MDNStandard : MailStandard
    {
        /// <summary>
        /// Specifies how notification was triggered.
        /// </summary>
        /// <remarks>
        /// RFC 3798, Disposition modes, 3.2.6.1, action-mode
        /// </remarks>
        public enum TriggerType
        {
            /// <summary>
            /// Notification was triggered automatically.
            /// </summary>
            Automatic,
            /// <summary>
            /// Notification was triggered based on user action.
            /// </summary>
            UserInitiated
        }
        /// <summary>
        /// Specifies how user was involved in sending notfication.
        /// </summary>
        /// <remarks>
        /// RFC 3798, Disposition modes, 3.2.6.1, sending-mode
        /// </remarks>
        public enum SendType
        {
            /// <summary>
            /// Notification was sent automatically.
            /// </summary>
            Automatic,
            /// <summary>
            /// Notification was sent based on user action.
            /// </summary>
            UserMediated
        }
        
        /// <summary>
        /// Indicates what this disposition notification means
        /// </summary>
        /// <remarks>
        /// RFC 3798, Disposition types, 3.2.6.2, includes type (processed) mentioned in document but
        /// not listed in grammar.
        /// </remarks>
        public enum NotificationType
        {
            /// <summary>
            /// Indicates message has been received but not displayed to user.
            /// Adde per Applicability Statement for Secure Health Transport v1.1
            /// </summary>
            Processed,
            /// <summary>
            /// Indicates message has been displayed to user (does not imply the message was read, understood, etc.)
            /// </summary>
            Displayed,
            /// <summary>
            /// Indicates message was deleted.
            /// </summary>
            Deleted,
            /// <summary>
            /// Indicates message was dispatched.
            /// Added per Implementation Guide for Delivery Notification in Direct
            /// </summary>
            Dispatched,
            /// <summary>
            /// Indicates message was denied.
            /// </summary>
            Denied,
            /// <summary>
            /// Indicates message was errored.
            /// </summary>
            Error,
            /// <summary>
            /// Indicates message was failed.
            /// </summary>
            Failed
            
        }
        
        /// <summary>
        /// Indicates disposition modifier of error, failure or warning
        /// </summary>
        /// <remarks>
        /// RFC 3798, Disposition field, 3.2.6, disposition-modifier, includes modifiers
        /// that are referred to in the text but not in the grammar.
        /// </remarks>
        public enum ErrorType
        {
            /// <summary>
            /// Indicates message had an error.
            /// </summary>
            Error,
            /// <summary>
            /// Indicates failure to deliver.
            /// </summary>
            Failure,
            /// <summary>
            /// Indicates an informative warning
            /// </summary>
            Warning
        }
        //
        // MIME Types
        //
        /// <summary>
        /// MIME types for MDN
        /// </summary>
        public new class MediaType : MailStandard.MediaType
        {
            /// <summary>
            /// Base MIME type for an MDN
            /// </summary>
            public const string ReportMessage = "multipart/report";
            /// <summary>
            /// MIME type with qualifier for a disposition report.
            /// </summary>
            public const string DispositionReport = ReportMessage + "; report-type=disposition-notification";
            /// <summary>
            /// MIME type for the disposition notification body part of the <c>multipart/report</c> report.
            /// </summary>
            public const string DispositionNotification = "message/disposition-notification";
        }

        /// <summary>
        /// Standard header names for mail messages and notifications
        /// </summary>
        public new class Headers : MailStandard.Headers
        {
            /// <summary>
            /// Disposition-Notification-To header name
            /// </summary>
            /// <remarks>
            /// RFC 3798, The Disposition-Notification-To Header, 2.1
            /// </remarks>
            public const string DispositionNotificationTo = "Disposition-Notification-To";
            /// <summary>
            /// Disposition-Notification-Options header name
            /// </summary>
            /// <remarks>
            /// RFC 3798, The Disposition-Notification-Options Header, 2.2
            /// </remarks>
            public const string DispositionNotificationOptions = "Disposition-Notification-Options";
        }
        
        /// <summary>
        /// Fields contained in Notification bodies. 
        /// RFC 3798 Section 3.2
        /// </summary>
        public class Fields
        {
            /// <summary>
            /// Reporting-UA field name (value is the Health Internet Addresa and software that triggered notification) 
            /// </summary>
            /// <remarks>
            /// RFC 3798, The Reporting-UA field, 3.2.1
            /// </remarks>
            public const string ReportingAgent = "Reporting-UA";
            /// <summary>
            /// MDN-Gateway field name (for SMTP to non-SMTP gateways -- e.g., XDD to SMTP)
            /// </summary>
            /// <remarks>
            /// RFC 3798, The MDN-Gateway field, 3.2.2
            /// </remarks>
            public const string Gateway = "MDN-Gateway";
            /// <summary>
            /// The Recipient for which this MDN is being sent
            /// </summary>
            /// <remarks>
            /// RFC 3798, FinalRecipient field, 3.2.4
            /// </remarks>
            public const string FinalRecipient = "Final-Recipient";
            /// <summary>
            /// Original-Message-ID field name (value is message for which notification is being sent)
            /// </summary>
            /// <remarks>
            /// RFC 3798, Original-Message-ID field, 3.2.5
            /// </remarks>
            public const string OriginalMessageID = "Original-Message-ID";
            /// <summary>
            /// Disposition header field name
            /// </summary>
            /// <remarks>
            /// RFC 3798, Disposition field, 3.2.6
            /// </remarks>
            public const string Disposition = "Disposition";
            /// <summary>
            /// Failure field name, value is original failure text (e.g., exception)
            /// </summary>
            /// <remarks>
            /// RFC 3798, Failure, Error and Warning fields, 3.2.7
            /// </remarks>
            public const string Failure = "Failure";
            /// <summary>
            /// Error field name, value is original error text (e.g., HL7 error report)
            /// </summary>
            /// <remarks>
            /// RFC 3798, Failure, Error and Warning fields, 3.2.7
            /// </remarks>
            public const string Error = "Error";
            /// <summary>
            /// Warnig field name, value is original warning text
            /// </summary>
            /// <remarks>
            /// RFC 3798, Failure, Error and Warning fields, 3.2.7
            /// </remarks>
            public const string Warning = "Warning";
        }
        
        internal const string Action_Manual = "manual-action";
        internal const string Action_Automatic = "automatic-action";
        internal const string Send_Manual = "MDN-sent-manually";
        internal const string Send_Automatic = "MDN-sent-automatically";
        internal const string Disposition_Displayed = "displayed";
        internal const string Disposition_Dispatched = "dispatched";
        internal const string Disposition_Denied = "denied";
        internal const string Disposition_Error = "error";
        internal const string Disposition_Failed = "failed";
        internal const string Disposition_Processed = "processed";
        internal const string Disposition_Deleted = "deleted";
        internal const string Modifier_Error = "error";

        internal const string RecipientType_Mail = "rfc822";
        
        internal const string ReportType = "report-type";
        internal const string ReportTypeValueNotification = "disposition-notification";

        /// <summary>
        /// Direct specific dispostion options for requesting timely and reliable messaging.
        /// Also used as the MDN report extension (special) field for indicating an MDN in response to a timely and reliable request.
        /// </summary>
        public const string DispositionOption_TimelyAndReliable = "X-DIRECT-FINAL-DESTINATION-DELIVERY";
        
        /// <summary>
        /// Tests the <paramref name="entity"/> to see if it contains an MDN request.
        /// </summary>
        /// <param name="entity">The entity to test</param>
        /// <returns><c>true</c> if the entity contains an MDN request, <c>false</c> otherwise</returns>
        public static bool HasMDNRequest(MimeEntity entity)
        {
            if (entity == null)
            {
                return false;
            }
            
            return entity.HasHeader(Headers.DispositionNotificationTo);
        }

        /// <summary>
        /// Tests the <paramref name="entity"/> to see if it is an MDN
        /// </summary>
        /// <remarks>
        /// MDN status is indicated by the appropriate main body <c>Content-Type</c>. The multipart body
        /// will contain the actual disposition notification (see <see cref="MDNStandard.IsNotification"/>
        /// </remarks>
        /// <param name="entity">The entity to test</param>
        /// <returns><c>true</c> if the entity is an MDN, <c>false</c> otherwise</returns>
        public static bool IsReport(MimeEntity entity)
        {
            if (entity == null)
            {
                return false;
            }
            
            ContentType contentType = entity.ParsedContentType;
            return (contentType.IsMediaType(MDNStandard.MediaType.ReportMessage) && contentType.HasParameter(MDNStandard.ReportType, MDNStandard.ReportTypeValueNotification));
        }
        
        /// <summary>
        /// Tests the entity to determine if it is a disposition notification body part
        /// </summary>
        /// <remarks>
        /// Notification status is indicated by the appropriate <c>Content-Type</c>. The notification
        /// section will be a body part of the appropriate MDN report multipart body.
        /// </remarks>
        /// <param name="entity">The entity to test</param>
        /// <returns><c>true</c> if this body part is an MDN notification, <c>false</c> otherwise</returns>
        public static bool IsNotification(MimeEntity entity)
        {
            if (entity == null)
            {
                return false;
            }

            return entity.HasMediaType(MDNStandard.MediaType.DispositionNotification);
        }

        /// <summary>
        /// Provides the appropriate <c>Disposition</c> header value for the <paramref name="mode"/>
        /// </summary>
        /// <param name="mode">The mode to translate</param>
        /// <returns>A string representation suitable for inclusion in the action mode section of the <c>Disposition</c> header value</returns>
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
        
        /// <summary>
        /// Provides the appropriate <c>Disposition</c> header value for the <paramref name="mode"/>
        /// </summary>
        /// <param name="mode">The mode to translate</param>
        /// <returns>A string representation suitable for inclusion in the sending mode section of the <c>Disposition</c> header value</returns>
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

        /// <summary>
        /// Provides the appropriate <c>Disposition</c> header value for the <paramref name="type"/>
        /// </summary>
        /// <param name="type">The type to translate</param>
        /// <returns>A string representation suitable for inclusion in the disposition type section of the <c>Disposition</c> header value</returns>
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

                case NotificationType.Dispatched:
                    return Disposition_Dispatched;

                case NotificationType.Failed:
                    return Disposition_Failed;

                
            }
        }
    }
}