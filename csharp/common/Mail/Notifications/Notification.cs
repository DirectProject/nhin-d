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
using System.Net.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// Represents notification (MDN) content
    /// </summary>
    /// <remarks>
    /// The <see cref="NotificationMessage"/> represents the actually sendable MDN</remarks>
    public class Notification : MultipartEntity
    {
        MimeEntity m_explanation;
        MimeEntity m_notification;
        ReportingUserAgent m_reportingAgent;
        MdnGateway m_gateway;
        Disposition m_disposition;
        MailAddress m_finalRecipient;
        HeaderCollection m_fields;
                
        /// <summary>
        /// Initializes a new instance of the supplied notification type.
        /// </summary>
        /// <param name="notification">The notification disposition for this instance.</param>
        public Notification(MDNStandard.NotificationType notification)
            : this(new Disposition(notification))
        {
        }
        
        /// <summary>
        /// Initializes a new instance with the supplied <see cref="Disposition"/>
        /// </summary>
        /// <param name="disposition">The notification disposition for this notification.</param>
        public Notification(Disposition disposition)
            : base(MDNStandard.MediaType.DispositionReport)
        {
            m_explanation = new MimeEntity();
            m_explanation.ContentType = MimeStandard.MediaType.TextPlain;
            
            m_notification = new MimeEntity();
            m_notification.ContentType = MDNStandard.MediaType.DispositionNotification;
            
            m_fields = new HeaderCollection();
            
            this.Disposition = disposition;
        }
        
        /// <summary>
        /// Gets and sets the body part corresponding to the notification explaination.
        /// </summary>
        /// <remarks>
        /// RFC 3798, section 3, item b:
        /// <para>
        /// The first component of the multipart/report contains a human-
        /// readable explanation of the MDN, as described in [RFC-REPORT].
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
        /// The reporting agent that triggered this notification (optional)
        /// </summary>
        public ReportingUserAgent ReportingAgent
        {
            get
            {
                return m_reportingAgent;
            }
            set
            {
                m_fields.SetValue(MDNStandard.Fields.ReportingAgent, (value != null) ? value.ToString() : null);                
                m_reportingAgent = value;
            }
        }
        
        /// <summary>
        /// The gateway that triggered this notification (optional)
        /// </summary>
        public MdnGateway Gateway
        {
            get
            {
                return m_gateway;
            }
            set
            {
                m_fields.SetValue(MDNStandard.Fields.Gateway, (value != null) ? value.ToString() : null);
                m_gateway = value;                
            }
        }
        
        /// <summary>
        /// The ID of the message that triggered this notification (optional)
        /// </summary>
        public string OriginalMessageID
        {
            get
            {
                return m_fields.GetValue(MDNStandard.Fields.OriginalMessageID);
            }
            set
            {
                m_fields.SetValue(MDNStandard.Fields.OriginalMessageID, value);
            }
        }
        
        /// <summary>
        /// The message's final recipient
        /// </summary>
        public MailAddress FinalRecipient
        {
            get
            {
                return m_finalRecipient;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                
                m_finalRecipient = value;
                m_fields.SetValue(MDNStandard.Fields.FinalRecipient, string.Format("rfc822 ; {0}", m_finalRecipient.Address));
            }
        }
        
        /// <summary>
        /// Gets and sets the <see cref="Disposition"/> for this instance.
        /// </summary>
        public Disposition Disposition
        {
            get
            {
                return m_disposition;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                m_fields.SetValue(MDNStandard.Fields.Disposition, value.ToString());
                m_disposition = value;
            }
        }
        
        /// <summary>
        /// Gets and sets the value of the error header.
        /// </summary>
        public string Error
        {
            get
            {
                return m_fields.GetValue(MDNStandard.Fields.Error);
            }
            set
            {
                m_fields.SetValue(MDNStandard.Fields.Error, value);
            }
        }
        
        internal bool HasFinalRecipient
        {
            get
            {
                return (m_finalRecipient != null);
            }
        }
        /// <summary>
        /// The default explanation for notification.
        /// </summary>
        const string DefaultExplanation = "Your message was successfully {0}";

        /// <summary>
        /// Returns an enumeration of body parts of the multipart report for this notification.
        /// </summary>
        /// <returns>An enumeration of <see cref="MimeEntity"/> body parts.</returns>
        public override IEnumerator<MimeEntity> GetEnumerator()
        {            
            if (string.IsNullOrEmpty(this.Explanation))
            {
                this.Explanation = string.Format(DefaultExplanation, m_disposition.Notification);
            }
            
            m_notification.Body = new Body(m_fields.ToString());
            
            yield return m_explanation;
            yield return m_notification;
        }
    }
}