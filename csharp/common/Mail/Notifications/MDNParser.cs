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
using System.Linq;
using System.Text;
using System.Net.Mime;
using System.Net.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// A set of lightweight MDN Parsing methods
    /// </summary>
    public static class MDNParser
    {
        /// <summary>
        /// Extract MDN Notifications from a message
        /// </summary>
        /// <param name="message"><see cref="Health.Direct.Common.Mail.Message"/></param>
        /// <returns><see cref="Notification"/>object</returns>
        public static Notification Parse(Message message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            
            if (!message.IsMDN())
            {
                throw new MDNException(MDNError.NotMDN);
            }            
            
            if (!message.IsMultiPart)
            {
                throw new MDNException(MDNError.InvalidMDNBody);
            }
            
            return Parse(message.GetParts());
        }
        
        /// <summary>
        /// An MDN Notification message has a multipart body. 
        /// Parse the message into a Notification object.
        /// </summary>
        /// <param name="parts">Parts of a message body</param>
        /// <returns><see cref="Notification"/>object</returns>
        public static Notification Parse(IEnumerable<MimeEntity> parts)
        {
            if (parts == null)
            {
                throw new ArgumentNullException("parts");
            }

            MimeEntity explanation = null;
            MimeEntity mdn = null;
            //
            // Retrieve the entities embedded in the Notification message.
            // 
            GetMDNEntities(parts, out explanation, out mdn);
            //
            // Extract notification fields from the MDN Body
            //
            HeaderCollection mdnFields = ParseMDNFields(mdn);
            //
            // Transform the raw fields into a structured object
            //
            return new Notification(explanation, mdnFields);
        }
        
        /// <summary>
        /// Parses the given ReportingUserAgent value string following the conventions for RFC 3798
        /// </summary>
        /// <param name="value">input string</param>
        /// <returns><see cref="ReportingUserAgent"/> object</returns>
        public static ReportingUserAgent ParseReportingUserAgent(string value)
        {
            // SplitField checks value for null/empty
            string[] parts = SplitField(value, MDNError.InvalidReportingUserAgent);
            return new ReportingUserAgent(parts[0], parts[1]);
        }

        /// <summary>
        /// Parses the given MdnGateway value string following the conventions for RFC 3798
        /// </summary>
        /// <param name="value">input string</param>
        /// <returns><see cref="MdnGateway"/>object</returns>
        public static MdnGateway ParseMdnGateway(string value)
        {
            // SplitField checks value for null/empty
            string[] parts = SplitField(value, MDNError.InvalidMdnGateway);
            return new MdnGateway(parts[1],         // Domain
                                  parts[0]          // Type
                                  );    
        }

        /// <summary>
        /// Parse a disposition header value
        /// </summary>
        /// <param name="value">header value</param>
        /// <returns><see cref="Disposition"/> object</returns>
        public static Disposition ParseDisposition(string value)
        {
            // SplitField checks value for null/empty
            string[] parts = SplitField(value, MDNError.InvalidDisposition);

            string[] dispositionMode = SplitSubField(parts[0], MDNError.InvalidDisposition);
            MDNStandard.TriggerType trigger = ParseTriggerType(dispositionMode[0]);
            MDNStandard.SendType sendType= ParseSendType(dispositionMode[1]);
            
            string[] dispositionType = SplitSubField(parts[1], 1, MDNError.InvalidDisposition);
            MDNStandard.NotificationType notification = ParseNotificationType(dispositionType[0].Trim());

            Disposition disposition = new Disposition(trigger, sendType, notification);
            if (dispositionType.Length > 1)
            {
                //
                // Modifiers that also need parsing
                //
                string[] modifiers = SplitModifier(dispositionType[1].Trim(), MDNError.InvalidDisposition);
                for (int i = 0; i < modifiers.Length; ++i)
                {
                    if (MailStandard.Equals(modifiers[i], MDNStandard.Modifier_Error))
                    {
                        disposition.IsError = true;
                    }
                }
            }            
            
            return disposition;
        }

        /// <summary>
        /// Parse RFC 3798, Disposition modes, 3.2.6.1, action-mode
        /// </summary>
        /// <param name="value">input text</param>
        /// <returns><see cref="MDNStandard.TriggerType"/> value</returns>
        public static MDNStandard.TriggerType ParseTriggerType(string value)
        {
            if (string.IsNullOrEmpty(value))
            {
                throw new MDNException(MDNError.InvalidTriggerType);
            }

            if (MDNStandard.Equals(value, MDNStandard.Action_Automatic))
            {
                return MDNStandard.TriggerType.Automatic;
            }

            if (MDNStandard.Equals(value, MDNStandard.Action_Manual))
            {
                return MDNStandard.TriggerType.UserInitiated;
            }

            throw new MDNException(MDNError.InvalidTriggerType);
        }

        /// <summary>
        /// Parse RFC 3798, Disposition modes, 3.2.6.1, sending-mode
        /// </summary>
        /// <param name="value">input text</param>
        /// <returns><see cref="MDNStandard.SendType"/> value</returns>
        public static MDNStandard.SendType ParseSendType(string value)
        {
            if (string.IsNullOrEmpty(value))
            {
                throw new MDNException(MDNError.InvalidSendType);
            }
            
            if (MDNStandard.Equals(value, MDNStandard.Send_Automatic))
            {
                return MDNStandard.SendType.Automatic;
            }

            if (MDNStandard.Equals(value, MDNStandard.Send_Manual))
            {
                return MDNStandard.SendType.UserMediated;
            }

            throw new MDNException(MDNError.InvalidSendType);
        }
        
        /// <summary>
        /// Parse RFC 3798, Disposition types, 3.2.6.2, includes type (processed) mentioned in document but
        /// (failed) added per Implementation Guide for Delivery Notification in Direct
        /// </summary>
        /// <param name="value">input text</param>
        /// <returns><see cref="MDNStandard.NotificationType"/> value</returns>
        public static MDNStandard.NotificationType ParseNotificationType(string value)
        {
            if (string.IsNullOrEmpty(value))
            {
                throw new MDNException(MDNError.InvalidNotificationType);
            }

            if (MDNStandard.Equals(value, MDNStandard.Disposition_Processed))
            {
                return MDNStandard.NotificationType.Processed;
            }

            if (MDNStandard.Equals(value, MDNStandard.Disposition_Dispatched))
            {
                return MDNStandard.NotificationType.Dispatched;
            }

            if (MDNStandard.Equals(value, MDNStandard.Disposition_Displayed))
            {
                return MDNStandard.NotificationType.Displayed;
            }

            if (MDNStandard.Equals(value, MDNStandard.Disposition_Deleted))
            {
                return MDNStandard.NotificationType.Deleted;
            }

            if (MDNStandard.Equals(value, MDNStandard.Disposition_Denied))
            {
                return MDNStandard.NotificationType.Denied;
            }

            if (MDNStandard.Equals(value, MDNStandard.Disposition_Error))
            {
                return MDNStandard.NotificationType.Error;
            }

            if (MDNStandard.Equals(value, MDNStandard.Disposition_Failed))
            {
                return MDNStandard.NotificationType.Failed;
            }

            throw new MDNException(MDNError.InvalidNotificationType);
        }
        
        /// <summary>
        /// Tries to parse the final recipient - IF it is a mail address
        /// Else returns null
        /// </summary>
        /// <param name="value">input text</param>
        /// <returns><see cref="System.Net.Mail.MailAddress"/> object</returns>
        public static MailAddress ParseFinalRecipient(string value)
        {
            if (string.IsNullOrEmpty(value))
            {
                return null;
            }
            
            string[] parts = SplitField(value, MDNError.InvalidFinalRecipient);
            if (!MDNStandard.Equals(parts[0], MDNStandard.RecipientType_Mail))
            {
                return null;
            }
            
            try
            {
                return new MailAddress(parts[1]);
            }
            catch(Exception ex)
            {
                throw new MDNException(MDNError.InvalidFinalRecipient, ex);
            }
        }
        
        /// <summary>
        /// Retrieve MDN entites from the multiple parts of a message body
        /// <param name="parts">message body parts</param>
        /// <param name="explanation">(out) the explanation entity, if any</param>
        /// <param name="mdn">(out) the entity containing the MDN notification fields</param>
        /// </summary>
        public static void GetMDNEntities(IEnumerable<MimeEntity> parts, out MimeEntity explanation, out MimeEntity mdn)
        {
            if (parts == null)
            {
                throw new ArgumentNullException("parts");
            }
            
            explanation = null;
            mdn = null;
            foreach (MimeEntity entity in parts)
            {
                ContentType contentType = entity.ParsedContentType;
                if (contentType.IsMediaType(MDNStandard.MediaType.DispositionNotification))
                {
                    if (mdn != null)
                    {
                        throw new MDNException(MDNError.InvalidMDNBody);
                    }
                    mdn = entity;
                }
                else if (contentType.IsMediaType(MimeStandard.MediaType.TextPlain))
                {
                    if (explanation != null)
                    {
                        throw new MDNException(MDNError.InvalidMDNBody);
                    }
                    explanation = entity;
                }
            }

            if (explanation == null || mdn == null)
            {
                throw new MDNException(MDNError.InvalidMDNBody);
            }
        }
        
        /// <summary>
        /// Extract MDN fields (RFC 3798 Section 3.1.*). 
        /// Fields are formatted just like MIME headers, but embedded within the Body of MimeEntity instead
        /// </summary>
        /// <param name="fieldEntity">Source entity</param>
        /// <returns>Collection of fields</returns>
        public static HeaderCollection ParseMDNFields(MimeEntity fieldEntity)
        {
            if (fieldEntity == null)
            {
                throw new ArgumentNullException("fieldEntity");
            }
            Body mdnBody = fieldEntity.Body;
            if (mdnBody == null)
            {
                throw new MDNException(MDNError.InvalidMDNBody);
            }
            HeaderCollection mdnFields = null;
            try
            {
                mdnFields = new HeaderCollection(MimeSerializer.Default.DeserializeHeaders(mdnBody.Text));
            }
            catch(Exception ex)
            {
                throw new MDNException(MDNError.InvalidMDNFields, ex);
            }

            if (mdnFields.IsNullOrEmpty())
            {
                throw new MDNException(MDNError.InvalidMDNFields);
            }

            return mdnFields;
        }
         
        static char[] s_fieldSeparator = new char[] { ';' };
        static string[] SplitField(string value, MDNError error)
        {
            return Split(value, s_fieldSeparator, 2, 2, error);
        }

        static char[] s_subfieldSeparator = new char[] { '/' };
        static string[] SplitSubField(string value, MDNError error)
        {
            return Split(value, s_subfieldSeparator, 2, 2, error);
        }
        static string[] SplitSubField(string value, int minCount, MDNError error)
        {
            return Split(value, s_subfieldSeparator, minCount, 2, error);
        }
        static char[] s_modifierSeparator = new char[] { ',' };
        static string[] SplitModifier(string value, MDNError error)
        {
            return Split(value, s_modifierSeparator, 1, byte.MaxValue, error);
        }

        internal static string[] Split(string value, char[] separators, int minCount, int maxCount, MDNError error)
        {
            if (string.IsNullOrEmpty(value))
            {
                throw new MDNException(error);
            }

            string[] parts = value.Split(separators);
            if (parts.IsNullOrEmpty() || parts.Length < minCount || parts.Length > maxCount)
            {
                throw new MDNException(error);
            }

            return parts;
        }
    }
}
