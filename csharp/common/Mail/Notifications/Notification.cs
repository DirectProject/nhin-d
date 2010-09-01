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

namespace NHINDirect.Mail.Notifications
{
    public class Notification : MultipartEntity
    {
        MimeEntity m_explanation;
        MimeEntity m_notification;
        MdnGateway m_reportingAgent;
        MdnGateway m_gateway;
        Disposition m_disposition;
                
        public Notification(MDNStandard.NotificationType notification)
            : this(new Disposition(notification))
        {
        }
        
        public Notification(Disposition disposition)
            : base(MDNStandard.MediaType.DispositionReport)
        {
            m_explanation = new MimeEntity();
            m_explanation.ContentType = MimeStandard.MediaType.TextPlain;
            
            m_notification = new MimeEntity();
            m_notification.ContentType = MDNStandard.MediaType.DispositionNotification;
            
            this.Disposition = disposition;
        }
        
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
        
        // TODO: should be MUA.
        /// <summary>
        /// The reporting agent that triggered this notification (optional)
        /// </summary>
        public MdnGateway ReportingAgent
        {
            get
            {
                return m_reportingAgent;
            }
            set
            {
                m_notification.Headers.SetValue(MDNStandard.Headers.ReportingAgent, (value != null) ? value.ToString() : null);                
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
                m_notification.Headers.SetValue(MDNStandard.Headers.Gateway, (value != null) ? value.ToString() : null);
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
                return m_notification.Headers.GetValue(MDNStandard.Headers.OriginalMessageID);
            }
            set
            {
                m_notification.Headers.SetValue(MDNStandard.Headers.OriginalMessageID, value);
            }
        }
        
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
                    throw new ArgumentNullException();
                }
                m_notification.Headers.SetValue(MDNStandard.Headers.Disposition, value.ToString());
                m_disposition = value;
            }
        }
        
        public string Error
        {
            get
            {
                return m_notification.Headers.GetValue(MDNStandard.Headers.Error);
            }
            set
            {
                m_notification.Headers.SetValue(MDNStandard.Headers.Error, value);
            }
        }
        
        const string DefaultExplanation = "Your message was successfully {0}";

        public override IEnumerator<MimeEntity> GetEnumerator()
        {            
            if (string.IsNullOrEmpty(this.Explanation))
            {
                this.Explanation = string.Format(DefaultExplanation, m_disposition.Notification);
            }
            yield return m_explanation;
            yield return m_notification;
        }
    }
}
