/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Net.Mime;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail.DSN
{
    /// <summary>
    /// A set of lightweight DSN Parsing methods
    /// </summary>
    public class DSNParser
    {
        ///<summary>
        /// In memory padding header for collecting per-recipient headers into collections
        ///</summary>
        public const string SeperatorHeader = "Per-Recipient-Pad";

        /// <summary>
        /// Extract DSN from a message
        /// </summary>
        /// <param name="message"><see cref="Health.Direct.Common.Mail.Message"/></param>
        /// <returns><see cref="Notification"/>object</returns>
        public static DSN Parse(Message message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }

            if (!message.IsDSN())
            {
                throw new DSNException(DSNError.NotDSN);
            }

            if (!message.IsMultiPart)
            {
                throw new MDNException(MDNError.InvalidMDNBody);
            }

            return Parse(message.GetParts());
        }

        /// <summary>
        /// An Status message has a multipart body. 
        /// Parse the message into a Status object.
        /// </summary>
        /// <param name="parts">Parts of a message body</param>
        /// <returns><see cref="Notification"/>object</returns>
        public static DSN Parse(IEnumerable<MimeEntity> parts)
        {
            if (parts == null)
            {
                throw new ArgumentNullException("parts");
            }

            MimeEntity explanation = null;
            MimeEntity deliveryStatus = null;
            //
            // Retrieve the entities embedded in the Notification message.
            // 
            GetDSNEntities(parts, out explanation, out deliveryStatus);
            //
            // Extract notification fields from the MDN Body
            //
            HeaderCollection dsnFields = ParseDSNFields(deliveryStatus);
            //
            // Aggregate the per-recipient headers for easier access
            //
            List<HeaderCollection> perRecipientCollection = ParsePerRecientFields(deliveryStatus);
            //
            // Transform the raw fields into a structured object
            //

            return new DSN(explanation, dsnFields, perRecipientCollection);
        }

        /// <summary>
        /// Retrieve DSN entites from the multiple parts of a message body
        /// <param name="parts">message body parts</param>
        /// <param name="explanation">(out) the explanation entity, if any</param>
        /// <param name="deliveryStatus">(out) the entity containing the DSN fields</param>
        /// </summary>
        public static void GetDSNEntities(IEnumerable<MimeEntity> parts, out MimeEntity explanation, out MimeEntity deliveryStatus)
        {
            if (parts == null)
            {
                throw new ArgumentNullException("parts");
            }

            explanation = null;
            deliveryStatus = null;
            foreach (MimeEntity entity in parts)
            {
                ContentType contentType = entity.ParsedContentType;
                if (contentType.IsMediaType(DSNStandard.MediaType.DSNDeliveryStatus))
                {
                    if (deliveryStatus != null)
                    {
                        throw new DSNException(DSNError.InvalidDSNBody);
                    }
                    deliveryStatus = entity;
                }
                else if (contentType.IsMediaType(MimeStandard.MediaType.TextPlain))
                {
                    if (explanation != null)
                    {
                        throw new DSNException(DSNError.InvalidDSNBody);
                    }
                    explanation = entity;
                }
            }

            if (explanation == null || deliveryStatus == null)
            {
                throw new DSNException(DSNError.InvalidDSNBody);
            }
        }

        /// <summary>
        /// Parses the given Final-Recipient value string following the conventions for RFC 3464
        /// </summary>
        /// <param name="value">input string</param>
        /// <returns><see cref="MailAddress"/>object</returns>
        public static MailAddress ParseFinalRecipient(string value)
        {
            string[] parts = SplitField(value, MDNError.InvalidDisposition);
            return new MailAddress(parts[1]);
        }

        /// <summary>
        /// Parses the given Reporting-MTA value string following the conventions for RFC 3464
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static string ParseReportingMTA(string value)
        {
            string[] parts = SplitField(value, MDNError.InvalidDisposition);
            return parts[1];
        }

        


        /// <summary>
        /// Extract DSN fields 
        /// Fields are formatted just like MIME headers, but embedded within the Body of MimeEntity instead
        /// </summary>
        /// <param name="fieldEntity">Source entity</param>
        /// <returns>Collection of fields</returns>
        public static HeaderCollection ParseDSNFields(MimeEntity fieldEntity)
        {
            if (fieldEntity == null)
            {
                throw new ArgumentNullException("fieldEntity");
            }
            Body DSNBody = fieldEntity.Body;
            if (DSNBody == null)
            {
                throw new DSNException(DSNError.InvalidDSNBody);
            }
            HeaderCollection DSNFields = null;
            try
            {
                DSNFields = new HeaderCollection(MimeSerializer.Default.DeserializeHeaders(DSNBody.TrimEmptyLines()));
            }
            catch (Exception ex)
            {
                throw new DSNException(DSNError.InvalidDSNFields, ex);
            }

            if (DSNFields.IsNullOrEmpty())
            {
                throw new DSNException(DSNError.InvalidDSNFields);
            }

            return DSNFields;
        }


        /// <summary>
        /// Extract per-recipient dsn headers as a collection
        /// Fields are formatted just like MIME headers, but embedded within the Body of MimeEntity instead
        /// </summary>
        /// <param name="fieldEntity">Source entity</param>
        /// <returns>Collection of <see cref="HeaderCollection"/></returns>
        public static List<HeaderCollection> ParsePerRecientFields(MimeEntity fieldEntity)
        {
            if (fieldEntity == null)
            {
                throw new ArgumentNullException("fieldEntity");
            }
            Body dsnBody = fieldEntity.Body;
            if (dsnBody == null)
            {
                throw new DSNException(DSNError.InvalidDSNBody);
            }
            HeaderCollection dsnFields = new HeaderCollection();
            try
            {
                dsnFields.Add(new HeaderCollection(MimeSerializer.Default.DeserializeHeaders(dsnBody.PerRecipientSeperator()))
                    , PerRecipientFieldList());
            }
            catch (Exception ex)
            {
                throw new DSNException(DSNError.InvalidDSNFields, ex);
            }

            if (dsnFields.IsNullOrEmpty())
            {
                throw new DSNException(DSNError.InvalidDSNFields);
            }

            return PerRecipientList(dsnFields);
        }

        private static string[] PerRecipientFieldList()
        {
            List<string> selectFields = DSNStandard.PerRecipientFields.ToList();
            selectFields.Add(SeperatorHeader);
            return selectFields.ToArray();
        }

        private static List<HeaderCollection> PerRecipientList(IEnumerable<Header> fields)
        {
            List<HeaderCollection> perRecipientList = new List<HeaderCollection>();
            HeaderCollection headers = new HeaderCollection();
            foreach (var dsnField in fields)
            {
                if(dsnField.IsNamed(SeperatorHeader) && ! headers.IsNullOrEmpty()) 
                {
                    perRecipientList.Add(headers);
                    headers = new HeaderCollection();
                    continue;
                }              
                headers.Add(dsnField);
            }
            if (! headers.IsNullOrEmpty())
            {
                perRecipientList.Add(headers);
            }
            return perRecipientList;
        }



        static char[] s_fieldSeparator = new char[] { ';' };

        static string[] SplitField(string value, MDNError error)
        {
            return Split(value, s_fieldSeparator, 2, 2, error);
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
