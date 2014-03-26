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
using System.Text;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail.DSN
{
    /// <summary>
    /// Represents DSN (Delivery Status Notification)
    /// </summary>
    /// <remarks>
    /// The <see cref="DSNMessage"/> represents the actually sendable DSN</remarks>
    public class DSN : MultipartEntity
    {
        MimeEntity m_explanation;
        MimeEntity m_notification;
        DSNPerMessage m_perMessage;
        IList<DSNPerRecipient> m_perRecipients;



        /// <summary>
        /// Initializes a new instance of the supplied DSN parts.
        /// </summary>
        public DSN(DSNPerMessage perMessage, IEnumerable<DSNPerRecipient> perRecipient)
            : base(DSNStandard.MediaType.DSNReport)
        {
            m_explanation = new MimeEntity();
            m_explanation.ContentType = MimeStandard.MediaType.TextPlain;

            m_notification = new MimeEntity();
            m_notification.ContentType = DSNStandard.MediaType.DSNDeliveryStatus;
            
            PerRecipient = perRecipient;
            PerMessage = perMessage;
        }

        internal DSN(MimeEntity explanation, HeaderCollection fields, IEnumerable<HeaderCollection> perRecipientCollection)
        {
            m_explanation = explanation;

            m_perMessage = new DSNPerMessage(fields);

            m_perRecipients = new List<DSNPerRecipient>();
            foreach (HeaderCollection perRecipientFields in perRecipientCollection)
            {
                m_perRecipients.Add(new DSNPerRecipient(perRecipientFields));
            }
        }

        /// <summary>
        /// Gets and sets the <see cref="DSNPerRecipient"/> for this instance.
        /// </summary>
        public IEnumerable<DSNPerRecipient> PerRecipient
        {
            get
            {
                return m_perRecipients;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                m_perRecipients = value.ToList();
            }
        }

        /// <summary>
        /// Gets and sets the <see cref="DSNPerMessage"/> for this instance.
        /// </summary>
        public DSNPerMessage PerMessage
        {
            get
            {
                return m_perMessage;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                
                m_perMessage = value;

            }
        }


        /// <summary>
        /// Gets and sets the body part corresponding to the notification explaination.
        /// </summary>
        /// <remarks>
        /// RFC 3436, section 2, item b:
        /// <para>
        /// The first component of the multipart/report contains a human-
        /// readable explanation of the DSN, as described in [RFC-REPORT].
        /// </para>
        /// </remarks>
        public string Explanation
        {
            get
            {
                if (m_explanation.HasBody)
                {
                    return m_explanation.Body.Text;
                }

                return null;
            }
            set
            {
                m_explanation.Body = new Body(value);
            }
        }

               
        
        /// <summary>
        /// The default explanation for notification.
        /// </summary>
        const string DefaultExplanation = "Delivery Status Notification";

        /// <summary>
        /// Returns an enumeration of body parts of the multipart report for this DSN.
        /// </summary>
        /// <returns>An enumeration of <see cref="MimeEntity"/> body parts.</returns>
        public override IEnumerator<MimeEntity> GetEnumerator()
        {
            if (string.IsNullOrEmpty(Explanation))
            {
                Explanation = DefaultExplanation;
            }
            var dsnContent = new StringBuilder();
            
            dsnContent.AppendLine(PerMessage.ToString());
            foreach (var dsnPerRecipient in PerRecipient)
            {
                dsnContent.AppendLine();
                dsnContent.AppendLine(dsnPerRecipient.ToString());
            }
            
            m_notification.Body = new Body(dsnContent.ToString());

            yield return m_explanation;
            yield return m_notification;
        }

    }
}
